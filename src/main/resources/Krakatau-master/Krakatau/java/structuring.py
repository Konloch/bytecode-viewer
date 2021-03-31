import collections
from collections import defaultdict as ddict
import itertools

from .. import graph_util
from ..ssa import objtypes, ssa_jumps
from ..ssa.exceptionset import ExceptionSet

from .setree import SEBlockItem, SEIf, SEScope, SESwitch, SETry, SEWhile

# This module is responsible for transforming an arbitrary control flow graph into a tree
# of nested structures corresponding to Java control flow statements. This occurs in
# several main steps
#
# Preprocessing - create graph view and ensure that there are no self loops and every node
#   has only one incoming edge type
# Structure loops - ensure every loop has a single entry point. This may result in
#   exponential code duplication in pathological cases
# Structure exceptions - create dummy nodes for every throw exception type for every node
# Structure conditionals - order switch targets consistent with fallthrough and create
#   dummy nodes where necessary
# Create constraints - sets up the constraints used to represent nested statements
# Merge exceptions - try to merge as any try constraints as possible. This is done by
#   extending one until it covers the cases that another one handles, allowing the second
#   to be removed
# Parallelize exceptions - freeze try constraints and turn them into multicatch blocks
#   where possible (not implemented yet)
# Complete scopes - expand scopes to try to reduce the number of successors
# Add break scopes - add extra scope statements so extra successors can be represented as
#   labeled breaks

#########################################################################################
class DominatorInfo(object):
    def __init__(self, root):
        self._doms = doms = {root:frozenset([root])}
        stack = [root]
        while stack:
            cur = stack.pop()
            assert cur not in stack
            for child in cur.successors:
                new = doms[cur] | frozenset([child])
                old = doms.get(child)
                if new != old:
                    new = new if old is None else (old & new)
                    assert child in new
                if old is not None:
                    assert new == old or len(new) < len(old)
                if new != old:
                    doms[child] = new
                    if child not in stack:
                        stack.append(child)
        self.nodeset = set(self._doms)
        self.root = root

    def dominators(self, node):
        return self._doms[node]

    def ordered(self, node): # for debugging
        return sorted(self._doms[node], key=lambda n:len(self._doms[n]))

    def dominator(self, *nodes):
        '''Get the common dominator of nodes'''
        doms = reduce(frozenset.intersection, map(self._doms.get, nodes))
        return max(doms, key=lambda n:len(self._doms[n]))

    def set_extend(self, dom, nodes):
        nodes = list(nodes) + [dom]
        pred_nl_func = lambda x:x.predecessors_nl if x is not dom else []
        return frozenset(graph_util.topologicalSort(nodes, pred_nl_func))

    def area(self, node): return ClosedSet([k for k,v in self._doms.items() if node in v], node, self)
    def extend(self, dom, nodes): return ClosedSet(self.set_extend(dom, nodes), dom, self)
    def extend2(self, nodes): return self.extend(self.dominator(*nodes), nodes)
    def single(self, head): return ClosedSet([head], head, self)

# Immutable class representing a dominator closed set of nodes
# TODO clean up usage (remove copy() calls, etc.)
class ClosedSet(object):
    __slots__ = "nodes", "head", "info"

    def __init__(self, nodes, head, info):
        self.nodes = frozenset(nodes)
        self.head = head
        self.info = info
        if nodes:
            assert head in nodes
            # assert info.dominator(*nodes) == head

    def touches(self, other): return not self.nodes.isdisjoint(other.nodes)
    def isdisjoint(self, other): return self.nodes.isdisjoint(other.nodes)
    def issuperset(self, other): return self.nodes.issuperset(other.nodes)
    def issubset(self, other): return self.nodes.issubset(other.nodes)

    def __or__(self, other):
        assert type(self) == type(other)

        if not other.nodes or self is other:
            return self
        elif not self.nodes:
            return other
        assert self.head is not None and other.head is not None
        assert self.info is other.info

        if self.head in other.nodes:
            self, other = other, self

        nodes, head, info = self.nodes, self.head, self.info
        nodes |= other.nodes
        if other.head not in self.nodes:
            head = info.dominator(head, other.head)
            nodes = info.set_extend(head, nodes)
        return ClosedSet(nodes, head, info)

    def __and__(self, other):
        assert type(self) == type(other)

        nodes = self.nodes & other.nodes
        if not nodes:
            return ClosedSet.EMPTY

        if self.head in other.nodes:
            self, other = other, self
        if other.head in self.nodes:
            head = other.head
        else:
            head = self.info.dominator(*nodes)
        return ClosedSet(nodes, head, self.info)

    @staticmethod
    def union(*sets):
        return reduce(ClosedSet.__or__, sets, ClosedSet.EMPTY)

    def __str__(self):   # pragma: no cover
        return 'set{} ({} nodes)'.format(self.head, len(self.nodes))
    __repr__ = __str__

    def __lt__(self, other): return self.nodes < other.nodes
    def __le__(self, other): return self.nodes <= other.nodes
    def __gt__(self, other): return self.nodes > other.nodes
    def __ge__(self, other): return self.nodes >= other.nodes
ClosedSet.EMPTY = ClosedSet(frozenset(), None, None)

#########################################################################################
class ScopeConstraint(object):
    def __init__(self, lbound, ubound):
        self.lbound = lbound
        self.ubound = ubound

_count = itertools.count()
_gcon_tags = 'while','try','switch','if','scope'
class CompoundConstraint(object):
    def __init__(self, tag, head, scopes):
        assert tag in _gcon_tags
        self.id = next(_count) # for debugging purposes
        self.tag = tag
        self.scopes = scopes
        self.head = head
        # self.heads = frozenset([head]) if head is not None else frozenset()
        # only used by try constraints, but we leave dummy sets for the rest
        self.forcedup = self.forceddown = frozenset()

        self.lbound = ClosedSet.union(*[scope.lbound for scope in self.scopes])
        self.ubound = ClosedSet.union(*[scope.ubound for scope in self.scopes])
        if head is not None:
            assert head in self.lbound.nodes and head in self.ubound.nodes
        assert self.ubound >= self.lbound

    def __str__(self):    # pragma: no cover
        return self.tag+str(self.id)
    __repr__ = __str__

def WhileCon(dom, head):
    ubound = dom.area(head)
    lbound = dom.extend(head, [n2 for n2 in head.predecessors if head in dom.dominators(n2)])
    return CompoundConstraint('while', None, [ScopeConstraint(lbound, ubound)])

def TryCon(dom, trynode, target, cset, catchvar):
    trybound = dom.single(trynode)
    tryscope = ScopeConstraint(trybound, trybound)

    # Catch scopes are added later, once all the merging is finished
    new = CompoundConstraint('try', None, [tryscope])
    new.forcedup = set()
    new.forceddown = set()
    new.target = target
    new.cset = cset
    new.catchvar = catchvar

    assert len(new.target.successors) == 1
    new.orig_target = new.target.successors[0]
    return new

def FixedScopeCon(lbound):
    return CompoundConstraint('scope', None, [ScopeConstraint(lbound, lbound)])
#########################################################################################

def structureLoops(nodes):
    todo = nodes
    while_heads = []
    while todo:
        newtodo = []
        temp = set(todo)
        sccs = graph_util.tarjanSCC(todo, lambda block:[x for x in block.predecessors if x in temp])

        for scc in sccs:
            if len(scc) <= 1:
                continue

            scc_set = set(scc)
            entries = [n for n in scc if not scc_set.issuperset(n.predecessors)]
            assert len(entries) == 1
            head = entries[0]

            newtodo.extend(scc)
            newtodo.remove(head)
            while_heads.append(head)
        todo = newtodo
    return while_heads

def structureExceptions(nodes):
    thrownodes = [n for n in nodes if n.block and isinstance(n.block.jump, ssa_jumps.OnException)]

    newinfos = []
    for n in thrownodes:
        manager = n.block.jump.cs
        assert len(n.block.jump.params) == 1
        thrownvar = n.block.jump.params[0]

        mycsets = {}
        mytryinfos = []
        newinfos.append((n, manager.mask, mycsets, mytryinfos))

        for handler, cset in manager.sets.items():
            en = n.blockdict[handler.key, True]
            mycsets[en] = cset

            en.predecessors.remove(n)
            n.successors.remove(en)

            caughtvars = [v2 for (v1,v2) in zip(n.outvars[en], en.invars) if v1 == thrownvar]
            assert len(caughtvars) <= 1
            caughtvar = caughtvars.pop() if caughtvars else None
            outvars = n.outvars.pop(en)[:]
            assert outvars.count(thrownvar) <= 1
            if caughtvar is not None:
                outvars[outvars.index(thrownvar)] = None

            for tt in cset.getTopTTs():
                top = ExceptionSet.fromTops(cset.env, objtypes.className(tt))
                new = en.indirectEdges([])
                new.predecessors.append(n)
                n.successors.append(new)
                n.eassigns[new] = outvars # should be safe to avoid copy as we'll never modify it
                nodes.append(new)
                mytryinfos.append((top, new, caughtvar))

    return newinfos

def structureConditionals(entryNode, nodes):
    dom = DominatorInfo(entryNode)
    switchnodes = [n for n in nodes if n.block and isinstance(n.block.jump, ssa_jumps.Switch)]
    ifnodes = [n for n in nodes if n.block and isinstance(n.block.jump, ssa_jumps.If)]

    # For switch statements, we can't just blithely indirect all targets as that interferes with fallthrough behavior
    switchinfos = []
    for n in switchnodes:
        targets = n.successors
        # a proper switch block must be dominated by its entry point
        # and all other nonloop predecessors must be dominated by a single other target
        # keep track of remaining good targets, bad ones will be found later by elimination
        target_set = frozenset(targets)
        good = []
        parents = {}
        for target in targets:
            if n not in dom.dominators(target):
                continue

            preds = [x for x in target.predecessors if x != n and target not in dom.dominators(x)]
            for pred in preds:
                choices = dom.dominators(pred) & target_set
                if len(choices) != 1:
                    break
                choice = min(choices)
                if parents.setdefault(target, choice) != choice:
                    break
            else:
                # passed all the tests for now, target appears valid
                good.append(target)

        while 1:
            size = len(parents), len(good)
            # prune bad parents and children from dict
            for k,v in parents.items():
                if k not in good:
                    del parents[k]
                elif v not in good:
                    del parents[k]
                    good.remove(k)

            # make sure all parents are unique. In case they're not, choose one arbitrarily
            chosen = {}
            for target in good:
                if target in parents and chosen.setdefault(parents[target], target) != target:
                    del parents[target]
                    good.remove(target)

            if size == (len(parents), len(good)): # nothing changed this iteration
                break

        # Now we need an ordering of the good blocks consistent with fallthrough
        # regular topoSort can't be used since we require chains to be immediately contiguous
        # which a topological sort doesn't garuentee
        children = {v:k for k,v in parents.items()}
        leaves = [x for x in good if x not in children]
        ordered = []
        for leaf in leaves:
            cur = leaf
            while cur is not None:
                ordered.append(cur)
                cur = parents.get(cur)
        ordered = ordered[::-1]
        assert len(ordered) == len(good)

        # now handle the bad targets
        for x in targets:
            if x not in good:
                new = x.indirectEdges([n])
                nodes.append(new)
                ordered.append(new)
        assert len(ordered) == len(targets)
        switchinfos.append((n, ordered))

        # if we added new nodes, update dom info
        if len(good) < len(targets):
            dom = DominatorInfo(entryNode)

    # Now handle if statements. This is much simpler since we can just indirect everything
    ifinfos = []
    for n in ifnodes:
        targets = [x.indirectEdges([n]) for x in n.successors[:]]
        nodes.extend(targets)
        ifinfos.append((n, targets))
    return switchinfos, ifinfos

def createConstraints(dom, while_heads, newtryinfos, switchinfos, ifinfos):
    constraints = []
    for head in while_heads:
        constraints.append(WhileCon(dom, head))

    masks = {n:mask for n, mask, _, _ in newtryinfos}
    forbid_dicts = ddict(lambda:masks.copy())
    for n, mask, csets, tryinfos in newtryinfos:
        for ot, cset in csets.items():
            forbid_dicts[ot][n] -= cset
    for forbid in forbid_dicts.values():
        for k in forbid.keys():
            if not forbid[k]:
                del forbid[k]

    for n, mask, csets, tryinfos in newtryinfos:
        cons = [TryCon(dom, n, target, top, caughtvar) for top, target, caughtvar in tryinfos]

        for con, con2 in itertools.product(cons, repeat=2):
            if con is con2:
                continue
            if not (con.cset - con2.cset): # cset1 is subset of cset2
                assert con2.cset - con.cset
                con.forcedup.add(con2)
                con2.forceddown.add(con)

        for con in cons:
            con.forbidden = forbid_dicts[con.orig_target].copy()

            if n in con.forbidden:
                for con2 in con.forceddown:
                    con.forbidden[n] -= con2.cset
                assert con.cset.isdisjoint(con.forbidden[n])
                if not con.forbidden[n]:
                    del con.forbidden[n]
            assert all(con.forbidden.values())
        constraints.extend(cons)

    for n, ordered in switchinfos:
        last = []
        scopes = []
        for target in reversed(ordered):
            # find all nodes which fallthrough to the next switch block
            # these must be included in the current switch block
            fallthroughs = [x for x in last if target in dom.dominators(x)]
            assert n not in fallthroughs
            assert(len(last) - len(fallthroughs) <= 1) # every predecessor should be accounted for except n itself
            last = [x for x in target.predecessors if target not in dom.dominators(x)] # make sure not to include backedges

            lbound = dom.extend(target, fallthroughs)
            ubound = dom.area(target)
            assert lbound <= ubound and n not in ubound.nodes
            scopes.append(ScopeConstraint(lbound, ubound))
        con = CompoundConstraint('switch', n, list(reversed(scopes)))
        constraints.append(con)

    for n, targets in ifinfos:
        scopes = []
        for target in targets:
            lbound = dom.single(target)
            ubound = dom.area(target)
            scopes.append(ScopeConstraint(lbound, ubound))
        con = CompoundConstraint('if', n, scopes)
        constraints.append(con)

    return constraints

def orderConstraints(dom, constraints, nodes):
    DummyParent = None # dummy root
    children = ddict(list)
    frozen = set()

    node_set = ClosedSet(nodes, dom.root, dom)
    assert set(dom._doms) == node_set.nodes
    for item in constraints:
        assert item.lbound <= node_set
        assert item.ubound <= node_set
        for scope in item.scopes:
            assert scope.lbound <= node_set
            assert scope.ubound <= node_set

    todo = constraints[:]
    while todo:
        items = []
        queue = [todo[0]]
        iset = set(queue) # set of items to skip when expanding connected component
        nset = ClosedSet.EMPTY
        parents = set() # items that must be above the entire component

        # Find a connected component of non frozen constraints based on intersecting lbounds
        while queue:
            item = queue.pop()
            if item in frozen:
                parents.add(item)
                continue
            items.append(item)

            # forcedup/down are sets so to maintain deterministic behavior we have to sort them
            # use key of target for sorting, since that should be unique
            temp = (item.forcedup | item.forceddown) - iset
            iset |= temp
            assert all(fcon.tag == 'try' for fcon in temp)
            assert len(set(fcon.target._key for fcon in temp)) == len(temp)
            queue += sorted(temp, key=lambda fcon:fcon.target._key)

            if not item.lbound.issubset(nset):
                nset |= item.lbound
                hits = [i2 for i2 in constraints if nset.touches(i2.lbound)]
                queue += [i2 for i2 in hits if not i2 in iset and not iset.add(i2)]
        assert nset <= node_set and nset.nodes

        # Find candidates for the new root of the connected component.
        # It must have a big enough ubound and also can't have nonfrozen forced parents
        candidates = [i for i in items if i.ubound.issuperset(nset)]
        candidates = [i for i in candidates if i.forcedup.issubset(frozen)]

        # make sure for each candidate that all of the nested items fall within a single scope
        cscope_assigns = []
        for cnode in candidates:
            svals = ddict(lambda:ClosedSet.EMPTY)
            bad = False
            for item in items:
                if item is cnode:
                    continue

                scopes = [s for s in cnode.scopes if item.lbound.touches(s.ubound)]
                if len(scopes) != 1 or not scopes[0].ubound.issuperset(item.lbound):
                    bad = True
                    break
                svals[scopes[0]] |= item.lbound

            if not bad:
                cscope_assigns.append((cnode, svals))

        cnode, svals = cscope_assigns.pop() # choose candidate arbitrarily if more than 1
        assert len(svals) <= len(cnode.scopes)
        for scope, ext in svals.items():
            scope.lbound |= ext
            assert scope.lbound <= scope.ubound

        cnode.lbound |= nset # should be extended too
        assert cnode.lbound <= cnode.ubound
        # assert(cnode.lbound == (cnode.heads.union(*[s.lbound for s in cnode.scopes]))) TODO

        # find lowest parent
        parent = DummyParent
        while not parents.isdisjoint(children[parent]):
            temp = parents.intersection(children[parent])
            assert len(temp) == 1
            parent = temp.pop()

        if parent is not None:
            assert cnode.lbound <= parent.lbound

        children[parent].append(cnode)
        todo.remove(cnode)
        frozen.add(cnode)

    # make sure items are nested
    for k, v in children.items():
        temp = set()
        for child in v:
            assert temp.isdisjoint(child.lbound.nodes)
            temp |= child.lbound.nodes
        assert k is None or temp <= k.lbound.nodes

    # Add a root so it is a tree, not a forest
    croot = FixedScopeCon(node_set)
    children[croot] = children[None]
    del children[None]
    return croot, children

def mergeExceptions(dom, children, constraints, nodes):
    parents = {} # con -> parent, parentscope
    for k, cs in children.items():
        for child in cs:
            scopes = [s for s in k.scopes if s.lbound.touches(child.lbound)]
            assert child not in parents and len(scopes) == 1
            parents[child] = k, scopes[0]
    assert set(parents) == set(constraints)

    def removeFromTree(con):
        parent, pscope = parents[con]
        children[parent] += children[con]
        for x in children[con]:
            scopes = [s for s in parent.scopes if s.lbound.touches(x.lbound)]
            parents[x] = parent, scopes[0]
        children[parent].remove(con)
        del children[con]
        del parents[con]

    def insertInTree(con, parent):
        scopes = [s for s in parent.scopes if s.lbound.touches(con.lbound)]
        parents[con] = parent, scopes[0]
        children[con] = []

        for scope in con.scopes:
            hits = [c for c in children[parent] if c.lbound.touches(scope.lbound)]
            for child in hits:
                assert parents[child][0] == parent
                parents[child] = con, scope
                children[con].append(child)
                children[parent].remove(child)
        children[parent].append(con)

    def unforbid(forbidden, newdown):
        for n in newdown.lbound.nodes:
            if n in forbidden:
                forbidden[n] -= newdown.cset
                if not forbidden[n]:
                    del forbidden[n]

    def tryExtend(con, newblocks, xCSet, xUps, xDowns, removed):
        forcedup = con.forcedup | xUps
        forceddown = con.forceddown | xDowns
        assert con not in forceddown
        forcedup.discard(con)
        if forcedup & forceddown:
            return False

        body = con.lbound | newblocks
        ubound = con.ubound
        for tcon in forcedup:
            ubound &= tcon.lbound

        while 1:
            done = True
            parent, pscope = parents[con]
            # Ugly hack to work around the fact that try bodies are temporarily stored
            # in the main constraint, not its scopes
            while not body <= (parent if parent.tag == 'try' else pscope).lbound:
                # Try to extend parent rather than just failing
                if parent.tag == 'try' and parent in forcedup:
                    # Note this call may mutate the parent
                    done = not tryExtend(parent, body, ExceptionSet.EMPTY, set(), set(), removed)
                    # Since the tree may have been updated, start over and rewalk the tree
                    if not done:
                        break

                body |= parent.lbound
                if parent in forcedup or not body <= ubound:
                    return False
                parent, pscope = parents[parent]
            if done:
                break

        for child in children[parent]:
            if child.lbound.touches(body):
                body |= child.lbound
        if not body <= ubound:
            return False

        cset = con.cset | xCSet
        forbidden = con.forbidden.copy()
        for newdown in (forceddown - con.forceddown):
            unforbid(forbidden, newdown)
        assert all(forbidden.values())

        for node in body.nodes:
            if node in forbidden and (cset & forbidden[node]):
                # The current cset is not compatible with the current partial order
                # Try to find some cons to force down in order to fix this
                bad = cset & forbidden[node]
                candidates = [c for c in trycons if c not in removed]
                candidates = [c for c in candidates if node in c.lbound.nodes and c.lbound.issubset(body)]
                candidates = [c for c in candidates if (c.cset & bad)]
                candidates = [c for c in candidates if c not in forcedup and c is not con]

                for topnd in candidates:
                    if topnd in forceddown:
                        continue

                    temp = topnd.forceddown - forceddown - removed
                    temp.add(topnd)
                    for newdown in temp:
                        unforbid(forbidden, newdown)

                    assert con not in temp
                    forceddown |= temp
                    bad = cset & forbidden.get(node, ExceptionSet.EMPTY)
                    if not bad:
                        break
                if bad:
                    assert node not in con.lbound.nodes or cset - con.cset
                    return False
        assert forceddown.isdisjoint(forcedup)
        assert all(forbidden.values())
        for tcon in forceddown:
            assert tcon.lbound <= body

        # At this point, everything should be all right, so we need to update con and the tree
        con.lbound = body
        con.cset = cset
        con.forbidden = forbidden
        con.forcedup = forcedup
        con.forceddown = forceddown
        con.scopes[0].lbound = body
        con.scopes[0].ubound = ubound

        for new in con.forceddown:
            new.forcedup.add(con)
            new.forcedup |= forcedup

        for new in con.forcedup:
            unforbid(new.forbidden, con)
            for new2 in forceddown - new.forceddown:
                unforbid(new.forbidden, new2)
            new.forceddown.add(con)
            new.forceddown |= forceddown

        # Move con into it's new position in the tree
        removeFromTree(con)
        insertInTree(con, parent)
        return True

    trycons = [con for con in constraints if con.tag == 'try']
    # print 'Merging exceptions ({1}/{0}) trys'.format(len(constraints), len(trycons))
    topoorder = graph_util.topologicalSort(constraints, lambda cn:([parents[cn]] if cn in parents else []))
    trycons = sorted(trycons, key=topoorder.index)
    # note that the tree may be changed while iterating, but constraints should only move up

    removed = set()
    for con in trycons:
        if con in removed:
            continue

        # First find the actual upper bound for the try scope, since it's only the one node on creation
        # However, for now we set ubound to be all nodes not reachable from catch, instead of only those
        # dominated by the try node. That way we can expand and merge it. We'll fix it up once we're done
        assert len(con.lbound.nodes) == 1
        tryhead = con.lbound.head
        backnodes = dom.dominators(tryhead)
        catchreach = graph_util.topologicalSort([con.target], lambda node:[x for x in node.successors if x not in backnodes])
        ubound_s = set(nodes) - set(catchreach)
        con.ubound = ClosedSet(ubound_s, dom.root, dom)

        # Now find which cons we can try to merge with
        candidates = [c for c in trycons if c not in removed and c.orig_target == con.orig_target]
        candidates = [c for c in candidates if c.lbound.issubset(con.ubound)]
        candidates = [c for c in candidates if c not in con.forcedup]
        candidates.remove(con)

        success = {}
        for con2 in candidates:
            success[con2] = tryExtend(con, con2.lbound, con2.cset, con2.forcedup, con2.forceddown, removed)

        # Now find which ones can be removed
        def removeable(con2):
            okdiff = set([con,con2])
            if con2.lbound <= (con.lbound):
                if con2.forceddown <= (con.forceddown | okdiff):
                    if con2.forcedup <= (con.forcedup | okdiff):
                        if not con2.cset - con.cset:
                            return True
            return False

        for con2 in candidates:
            # Note that since our tryExtend is somewhat conservative, in rare cases we
            # may find that we can remove a constraint even if tryExtend failed on it
            # but the reverse should obviously never happen
            if not removeable(con2):
                assert not success[con2]
                continue

            removed.add(con2)
            for tcon in trycons:
                if tcon not in removed and tcon is not con:
                    assert con in tcon.forceddown or con2 not in tcon.forceddown
                    assert con in tcon.forcedup or con2 not in tcon.forcedup
                tcon.forcedup.discard(con2)
                tcon.forceddown.discard(con2)

            assert con not in removed
            removeFromTree(con2)

    # Cleanup
    removed_nodes = frozenset(c.target for c in removed)
    constraints = [c for c in constraints if c not in removed]
    trycons = [c for c in trycons if c not in removed]

    for con in trycons:
        assert not con.forcedup & removed
        assert not con.forceddown & removed

        # For convienence, we were previously storing the try scope bounds in the main constraint bounds
        assert len(con.scopes)==1
        tryscope = con.scopes[0]
        tryscope.lbound = con.lbound
        tryscope.ubound = con.ubound
    # print 'Merging done'
    # print dict(collections.Counter(con.tag for con in constraints))

    # Now fix up the nodes. This is a little tricky.
    # Note, the _nl lists are also invalidated. They're fixed below once we create the new dom info
    nodes = [n for n in nodes if n not in removed_nodes]
    for node in nodes:
        node.predecessors = [x for x in node.predecessors if x not in removed_nodes]

        # start with normal successors and add exceptions back in
        node.successors = [x for x in node.successors if x in node.outvars]
        if node.eassigns:
            temp = {k.successors[0]:v for k,v in node.eassigns.items()}
            node.eassigns = ea = {}

            for con in trycons:
                if node in con.lbound.nodes and con.orig_target in temp:
                    ea[con.target] = temp[con.orig_target]
                    if node not in con.target.predecessors:
                        con.target.predecessors.append(node)
                    node.successors.append(con.target)
            assert len(ea) >= len(temp)
        assert removed_nodes.isdisjoint(node.successors)
    assert dom.root not in removed_nodes

    # Regenerate dominator info to take removed nodes into account
    node_set = set(nodes)
    dom = DominatorInfo(dom.root)
    assert set(dom._doms) == node_set
    calcNoLoopNeighbors(dom, nodes)

    def fixBounds(item):
        # note, we have to recalculate heads here too due to the altered graph
        oldl, oldu = item.lbound, item.ubound
        item.lbound = dom.extend2(item.lbound.nodes - removed_nodes)
        item.ubound = _dominatorUBoundClosure(dom, item.ubound.nodes - removed_nodes, item.ubound.head)
        assert item.lbound.nodes <= oldl.nodes and item.ubound.nodes <= oldu.nodes

    for con in constraints:
        fixBounds(con)
        for scope in con.scopes:
            fixBounds(scope)
    return dom, constraints, nodes

def fixTryConstraints(dom, constraints):
    # Add catchscopes and freeze other relations
    for con in constraints:
        if con.tag != 'try':
            continue

        lbound = dom.single(con.target)
        ubound = dom.area(con.target)
        cscope = ScopeConstraint(lbound, ubound)
        con.scopes.append(cscope)

        # After this point, forced relations and cset are frozen
        # So if a node is forbbiden, we can't expand to it at all
        cset = con.cset
        tscope = con.scopes[0]

        empty = ExceptionSet.EMPTY
        ubound_s = set(x for x in tscope.ubound.nodes if not (cset & con.forbidden.get(x, empty)))
        # Note, we use lbound head, not ubound head! The part dominated by lbound is what we actually care about
        tscope.ubound = _dominatorUBoundClosure(dom, ubound_s, tscope.lbound.head)
        del con.forbidden

        con.lbound = tscope.lbound | cscope.lbound
        con.ubound = tscope.ubound | cscope.ubound
        assert tscope.lbound.issubset(tscope.ubound)
        assert tscope.ubound.isdisjoint(cscope.ubound)

def _dominatorUBoundClosure(dom, ubound_s, head):
    # Make sure ubound is dominator closed by removing nodes
    ubound_s = set(x for x in ubound_s if head in dom.dominators(x))
    assert head in ubound_s
    done = len(ubound_s) <= 1
    while not done:
        done = True
        for x in list(ubound_s):
            xpreds_nl = [y for y in x.predecessors if x not in dom.dominators(y)] # pred nl list may not have been created yet
            if x != head and not ubound_s.issuperset(xpreds_nl):
                done = False
                ubound_s.remove(x)
                break
    assert ubound_s == dom.extend(head, ubound_s).nodes
    return ClosedSet(ubound_s, head, dom)

def _augmentingPath(startnodes, startset, endset, used, backedge, bound):
    # Find augmenting path via BFS
    # To make sure each node is used only once we treat it as if it were two nodes connected
    # by an internal edge of capacity 1. However, to save time we don't explicitly model this
    # instead it is encoded by the used set and rules on when we can go forward and backwards
    queue = collections.deque([(n,True,(n,)) for n in startnodes if n not in used])

    seen = set((n,True) for n in startnodes)
    while queue:
        pos, lastfw, path = queue.popleft()
        canfwd = not lastfw or pos not in used
        canback = pos in used and pos not in startset

        if canfwd:
            if pos in endset: # success!
                return path, None
            successors = [x for x in pos.norm_suc_nl if x in bound]
            for pos2 in successors:
                if (pos2, True) not in seen:
                    seen.add((pos2, True))
                    queue.append((pos2, True, path+(pos2,)))
        if canback:
            pos2 = backedge[pos]
            if (pos2, False) not in seen:
                seen.add((pos2, False))
                queue.append((pos2, False, path+(pos2,)))
    # queue is empty but we didn't find anything
    return None, set(x for x,front in seen if front)

def _mincut(startnodes, endnodes, bound):
    startset = frozenset(startnodes)
    endset = frozenset(endnodes)
    bound = bound | endset
    used = set()
    backedge = {}

    while 1:
        oldlen = len(used)
        path, lastseen = _augmentingPath(startnodes, startset, endset, used, backedge, bound)
        if path is None:
            return lastseen | (startset & used)

        assert path[0] in startset and path[-1] in endset
        assert path[0] not in used

        for pos, last in zip(path, (None,)+path):
            # In the case of a backward edge, there's nothing to do since it was already part of a used path
            used.add(pos)
            if last is not None and pos in last.norm_suc_nl: # normal forward edge
                backedge[pos] = last

        assert len(used) > oldlen
        assert set(backedge) == (used - startset)

def completeScopes(dom, croot, children, isClinit):
    parentscope = {}
    for k, v in children.items():
        for child in v:
            pscopes = [scope for scope in k.scopes if child.lbound.issubset(scope.lbound)]
            assert len(pscopes)==1
            parentscope[child] = pscopes[0]

    nodeorder = graph_util.topologicalSort([dom.root], lambda n:n.successors_nl)
    nodeorder = {n:-i for i,n in enumerate(nodeorder)}

    stack = [croot]
    while stack:
        parent = stack.pop()

        # The problem is that when processing one child, we may want to extend it to include another child
        # We solve this by freezing already processed children and ordering them heuristically
        # TODO - find a better way to handle this
        revorder = sorted(children[parent], key=lambda cnode:(-nodeorder[cnode.lbound.head], len(cnode.ubound.nodes)))
        frozen_nodes = set()

        while revorder:
            cnode = revorder.pop()
            if cnode not in children[parent]: # may have been made a child of a previously processed child
                continue

            scopes = [s for s in parent.scopes if s.lbound.touches(cnode.lbound)]
            assert len(scopes)==1

            ubound = cnode.ubound & scopes[0].lbound
            ubound_s = ubound.nodes - frozen_nodes
            for other in revorder:
                if not ubound_s.issuperset(other.lbound.nodes):
                    ubound_s -= other.lbound.nodes
            if isClinit:
                # Avoid inlining return block so that it's always at the end and can be pruned later
                ubound_s = set(n for n in ubound_s if n.block is None or not isinstance(n.block.jump, ssa_jumps.Return))

            ubound = _dominatorUBoundClosure(dom, ubound_s, cnode.lbound.head)
            assert ubound.issuperset(cnode.lbound)
            body = cnode.lbound

            # Be careful to make sure the order is deterministic
            temp = set(body.nodes)
            parts = [n.norm_suc_nl for n in sorted(body.nodes, key=nodeorder.get)]
            startnodes = [n for n in itertools.chain(*parts) if not n in temp and not temp.add(n)]

            temp = set(ubound.nodes)
            parts = [n.norm_suc_nl for n in sorted(ubound.nodes, key=nodeorder.get)]
            endnodes = [n for n in itertools.chain(*parts) if not n in temp and not temp.add(n)]

            # Now use Edmonds-Karp, modified to find min vertex cut
            lastseen = _mincut(startnodes, endnodes, ubound.nodes)

            # Now we have the max flow, try to find the min cut
            # Just use the set of nodes visited during the final BFS
            interior = [x for x in (lastseen & ubound.nodes) if lastseen.issuperset(x.norm_suc_nl)]

            # TODO - figure out a cleaner way to do this
            if interior:
                body |= dom.extend(dom.dominator(*interior), interior)
            assert body.issubset(ubound)
            # The new cut may get messed up by the inclusion of extra children. But this seems unlikely
            newchildren = []
            for child in revorder:
                if child.lbound.touches(body):
                    body |= child.lbound
                    newchildren.append(child)

            assert body.issubset(ubound)
            cnode.lbound = body
            for scope in cnode.scopes:
                scope.lbound |= (body & scope.ubound)

            children[cnode].extend(newchildren)
            children[parent] = [c for c in children[parent] if c not in newchildren]
            frozen_nodes |= body.nodes

        # Note this is only the immediate children, after some may have been moved down the tree during previous processing
        stack.extend(children[parent])

# Class used for the trees created internally while deciding where to create scopes
class _mnode(object):
    def __init__(self, head):
        self.head = head
        self.nodes = set()
        self.items = []
        # externally set fields: children top selected subtree depth
    # def __str__(self): return 'M'+str(self.head)[3:]
    # __repr__ = __str__

def _addBreak_sub(dom, rno_get, body, childcons):
    # Create dom* tree
    # This is a subset of dominators that dominate all nodes reachable from themselves
    # These "super dominators" are the places where it is possible to create a break scope

    domC = {n:dom.dominators(n) for n in body}
    for n in sorted(body, key=rno_get): # reverse topo order
        for n2 in n.successors_nl:
            if n2 not in body:
                continue
            domC[n] &= domC[n2]
            assert domC[n]

    heads = set(n for n in body if n in domC[n]) # find the super dominators
    depths = {n:len(v) for n,v in domC.items()}
    parentC = {n:max(v & heads, key=depths.get) for n,v in domC.items()} # find the last dom* parent
    assert all((n == parentC[n]) == (n in heads) for n in body)

    # Make sure this is deterministicly ordered
    mdata = collections.OrderedDict((k,_mnode(k)) for k in sorted(heads, key=rno_get))
    for n in body:
        mdata[parentC[n]].nodes.add(n)
    for item in childcons:
        head = parentC[item.lbound.head]
        mdata[head].items.append(item)
        mdata[head].nodes |= item.lbound.nodes
        assert mdata[head].nodes <= body
    assert set(mdata) <= heads

    # Now merge nodes until they no longer cross item boundaries, i.e. they don't intersect
    for h in heads:
        if h not in mdata:
            continue

        hits = mdata[h].nodes.intersection(mdata)
        while len(hits) > 1:
            hits.remove(h)
            for h2 in hits:
                assert h in domC[h2] and h2 not in domC[h]
                mdata[h].nodes |= mdata[h2].nodes
                mdata[h].items += mdata[h2].items
                del mdata[h2]
            hits = mdata[h].nodes.intersection(mdata)
        assert hits == set([h])

    # Now that we have the final set of heads, fill in the tree data
    # for each mnode, we need to find its immediate parent
    ancestors = {h:domC[h].intersection(mdata) for h in mdata}
    mparents = {h:(sorted(v,key=depths.get)[-2] if len(v) > 1 else None) for h,v in ancestors.items()}

    for h, mnode in mdata.items():
        mnode.top = True
        mnode.selected = [mnode]
        mnode.subtree = [mnode]
        # Note, this is max nesting depth, NOT depth in the tree
        mnode.depth = 1 if mnode.items else 0
        if any(item.tag == 'switch' for item in mnode.items):
            mnode.depth = 2
        mnode.tiebreak = rno_get(h)

        assert h in mnode.nodes and len(mnode.nodes) >= len(mnode.items)
        mnode.children = [mnode2 for h2, mnode2 in mdata.items() if mparents[h2] == h]

    revorder = graph_util.topologicalSort(mdata.values(), lambda mn:mn.children)
    assert len(revorder) == len(mdata)
    assert sum(len(mn.children) for mn in revorder) == len(revorder)-1

    # Now partition tree into subtrees, trying to minimize max nesting
    for mnode in revorder:
        if mnode.children:
            successor = max(mnode.children, key=lambda mn:(mn.depth, len(mn.subtree), mn.tiebreak))

            depths = sorted(mn.depth for mn in mnode.children)
            temp = max(d-i for i,d in enumerate(depths))
            mnode.depth = max(mnode.depth, temp+len(mnode.children)-1)

            for other in mnode.children:
                if other is successor:
                    continue
                other.top = False
                mnode.selected += other.subtree
            mnode.subtree = mnode.selected + successor.subtree
            for subnode in mnode.selected[1:]:
                subnode.top = False
        assert mnode.top
        assert len(set(mnode.subtree)) == len(mnode.subtree)

    results = []
    for root in revorder:
        if not root.top:
            continue
        nodes, items = set(), []
        for mnode in root.selected:
            nodes |= mnode.nodes
            items += mnode.items
        results.append((nodes, items))

    temp = list(itertools.chain.from_iterable(zip(*results)[1]))
    assert len(temp) == len(childcons) and set(temp) == set(childcons)
    return results

def addBreakScopes(dom, croot, constraints, children):
    nodeorder = graph_util.topologicalSort([dom.root], lambda n:n.successors_nl)
    nodeorder = {n:i for i,n in enumerate(nodeorder)}
    rno_get = nodeorder.get # key for sorting nodes in rev. topo order

    stack = [croot]
    while stack:
        cnode = stack.pop()
        oldchildren = children[cnode][:]
        newchildren = children[cnode] = []

        for scope in cnode.scopes:
            subcons = [c for c in oldchildren if c.lbound <= scope.lbound]

            results = _addBreak_sub(dom, rno_get, scope.lbound.nodes, subcons)
            results = [t for t in results if len(t[0]) > 1]

            for nodes, items in results:
                if len(items) == 1 and items[0].lbound.nodes == nodes:
                    new = items[0] # no point wrapping it in a scope if it already has identical body
                else:
                    head = dom.dominator(*nodes)
                    body = dom.extend(head, nodes)
                    assert body.nodes == nodes

                    new = FixedScopeCon(body)
                    constraints.append(new)
                    children[new] = items
                newchildren.append(new)
                stack.append(new)
        _checkNested(children)

def constraintsToSETree(dom, croot, children, nodes):
    seitems = {n:SEBlockItem(n) for n in nodes} # maps entryblock -> item

    # iterate over tree in reverse topological order (bottom up)
    revorder = graph_util.topologicalSort([croot], lambda cn:children[cn])
    for cnode in revorder:
        sescopes = []
        for scope in cnode.scopes:
            pos, body = scope.lbound.head, scope.lbound.nodes
            items = []
            while pos is not None:
                item = seitems[pos]
                del seitems[pos]
                items.append(item)
                suc = [n for n in item.successors if n in body]
                assert len(suc) <= 1
                pos = suc[0] if suc else None

            newscope = SEScope(items)
            sescopes.append(newscope)
            assert newscope.nodes == frozenset(body)

        if cnode.tag in ('if','switch'):
            head = seitems[cnode.head]
            assert isinstance(head, SEBlockItem)
            del seitems[cnode.head]

        new = None
        if cnode.tag == 'while':
            new = SEWhile(sescopes[0])
        elif cnode.tag == 'if':
            # ssa_jump stores false branch first, but ast gen assumes true branch first
            sescopes = [sescopes[1], sescopes[0]]
            new = SEIf(head, sescopes)
        elif cnode.tag == 'switch':
            # Switch fallthrough can only be done implicitly, but we may need to jump to it
            # from arbitrary points in the scope, so we add an extra scope so we have a
            # labeled break. If unnecessary, it should be removed later on anyway
            sescopes = [SEScope([sescope]) for sescope in sescopes]
            new = SESwitch(head, sescopes)
        elif cnode.tag == 'try':
            catchtts = cnode.cset.getTopTTs()
            catchvar = cnode.catchvar
            new = SETry(sescopes[0], sescopes[1], catchtts, catchvar)
        elif cnode.tag == 'scope':
            new = sescopes[0]

        assert new.nodes == cnode.lbound.nodes
        assert new.entryBlock not in seitems
        seitems[new.entryBlock] = new

    assert len(seitems) == 1
    assert isinstance(seitems.values()[0], SEScope)
    return seitems.values()[0]

def _checkNested(ctree_children):
    # Check tree for proper nesting
    for k, children in ctree_children.items():
        for child in children:
            assert child.lbound <= k.lbound
            assert child.lbound <= child.ubound
            scopes = [s for s in k.scopes if s.ubound.touches(child.lbound)]
            assert len(scopes) == 1

            for c1, c2 in itertools.combinations(child.scopes, 2):
                assert c1.lbound.isdisjoint(c2.lbound)
                assert c1.ubound.isdisjoint(c2.ubound)

        for c1, c2 in itertools.combinations(children, 2):
            assert c1.lbound.isdisjoint(c2.lbound)

def _debug_draw(nodes, outn=''):
    import pygraphviz as pgv
    G=pgv.AGraph(directed=True)
    G.add_nodes_from(nodes)
    for n in nodes:
        for n2 in n.successors:
            color = 'black'
            if isinstance(n.block.jump, ssa_jumps.OnException):
                if any(b.key == n2.bkey for b in n.block.jump.getExceptSuccessors()):
                    color = 'grey'
            # color = 'black' if n2 in n.outvars else 'gray'
            G.add_edge(n, n2, color=color)
    G.layout(prog='dot')
    G.draw('file{}.png'.format(outn))

def calcNoLoopNeighbors(dom, nodes):
    for n in nodes:
        n.successors_nl = [x for x in n.successors if x not in dom.dominators(n)]
        n.predecessors_nl = [x for x in n.predecessors if n not in dom.dominators(x)]
        n.norm_suc_nl = [x for x in n.successors_nl if x in n.outvars]
    for n in nodes:
        for n2 in n.successors_nl:
            assert n in n2.predecessors_nl
        for n2 in n.predecessors_nl:
            assert n in n2.successors_nl

def structure(entryNode, nodes, isClinit):
    # print 'structuring'
    # eliminate self loops
    for n in nodes[:]:
        if n in n.successors:
            nodes.append(n.indirectEdges([n]))

    # inline returns if possible
    retblocks = [n for n in nodes if n.block and isinstance(n.block.jump, ssa_jumps.Return)]
    if retblocks and not isClinit:
        assert len(retblocks) == 1
        ret = retblocks[0]
        for pred in ret.predecessors[1:]:
            new = ret.newDuplicate()
            new.predecessors = [pred]
            pred.replaceSuccessors({ret:new})
            nodes.append(new)
        ret.predecessors = ret.predecessors[:1]

    for n in nodes:
        for x in n.predecessors:
            assert n in x.successors
        for x in n.successors:
            assert n in x.predecessors
        assert set(n.successors) == (set(n.outvars) | set(n.eassigns))

    # note, these add new nodes (list passed by ref)
    while_heads = structureLoops(nodes)
    newtryinfos = structureExceptions(nodes)
    switchinfos, ifinfos = structureConditionals(entryNode, nodes)

    # At this point graph modification is largely done so we can calculate and store dominator info
    # this will be invalidated and recalculated near the end of mergeExceptions
    dom = DominatorInfo(entryNode)
    calcNoLoopNeighbors(dom, nodes)

    constraints = createConstraints(dom, while_heads, newtryinfos, switchinfos, ifinfos)
    croot, ctree_children = orderConstraints(dom, constraints, nodes)

    # print 'exception merging'
    # May remove nodes (and update dominator info)
    dom, constraints, nodes = mergeExceptions(dom, ctree_children, constraints, nodes)

    # TODO - parallelize exceptions
    fixTryConstraints(dom, constraints)

    # After freezing the try constraints we need to regenerate the tree
    croot, ctree_children = orderConstraints(dom, constraints, nodes)

    # print 'completing scopes'
    _checkNested(ctree_children)
    completeScopes(dom, croot, ctree_children, isClinit)

    # print 'adding breaks'
    _checkNested(ctree_children)
    addBreakScopes(dom, croot, constraints, ctree_children)
    _checkNested(ctree_children)

    return constraintsToSETree(dom, croot, ctree_children, nodes)
