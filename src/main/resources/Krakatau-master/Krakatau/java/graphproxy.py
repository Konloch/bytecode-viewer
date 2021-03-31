from collections import defaultdict as ddict
import itertools

def unique(seq): return len(set(seq)) == len(seq)

# This module provides a view of the ssa graph that can be modified without
# touching the underlying graph. This proxy is tailored towards the need of
# cfg structuring, so it allows easy duplication and indirection of nodes,
# but assumes that the underlying variables and statements are immutable

class BlockProxy(object):
    def __init__(self, key, counter, block=None):
        self.bkey = key
        self.num = next(counter)
        self.counter = counter
        self.block = block

        self.predecessors = []
        self.successors = []
        self.outvars = {}
        self.eassigns = {} # exception edge assignments, used after try constraint creation
        self._key = self.bkey, self.num

        # to be assigned later
        self.invars = self.blockdict = None
        # assigned by structuring.py calcNoLoopNeighbors
        self.successors_nl = self.predecessors_nl = self.norm_suc_nl = None

    def replaceSuccessors(self, rmap):
        update = lambda k:rmap.get(k,k)

        self.successors = map(update, self.successors)
        self.outvars = {update(k):v for k,v in self.outvars.items()}
        if self.block is not None:
            d1 = self.blockdict
            self.blockdict = {(b.key,t):update(d1[b.key,t]) for (b,t) in self.block.jump.getSuccessorPairs()}

    def newIndirect(self): # for use during graph creation
        new = BlockProxy(self.bkey, self.counter)
        new.invars = self.invars
        new.outvars = {self:new.invars}
        new.blockdict = None
        new.successors = [self]
        self.predecessors.append(new)
        return new

    def newDuplicate(self): # for use by structuring.structure return inlining
        new = BlockProxy(self.bkey, self.counter, self.block)
        new.invars = self.invars
        new.outvars = self.outvars.copy()
        new.blockdict = self.blockdict
        new.successors = self.successors[:]
        return new

    def indirectEdges(self, edges):
        # Should only be called once graph is completely set up. newIndirect is used during graph creation
        new = self.newIndirect()
        for parent in edges:
            self.predecessors.remove(parent)
            new.predecessors.append(parent)
            parent.replaceSuccessors({self:new})
        return new

    def normalSuccessors(self): # only works once try constraints have been created
        return [x for x in self.successors if x in self.outvars]

    def __str__(self):   # pragma: no cover
        fmt = 'PB {}x{}' if self.num else 'PB {0}'
        return fmt.format(self.bkey, self.num)
    __repr__ = __str__


def createGraphProxy(ssagraph):
    assert(not ssagraph.procs) # should have already been inlined

    nodes = [BlockProxy(b.key, itertools.count(), block=b) for b in ssagraph.blocks]
    allnodes = nodes[:] # will also contain indirected nodes

    entryNode = None
    intypes = ddict(set)
    for n in nodes:
        invars = [phi.rval for phi in n.block.phis]
        for b, t in n.block.jump.getSuccessorPairs():
            intypes[b.key].add(t)

        if n.bkey == ssagraph.entryKey:
            assert(not entryNode and not invars) # shouldn't have more than one entryBlock and entryBlock shouldn't have phis
            entryNode = n
            invars = ssagraph.inputArgs # store them in the node so we don't have to keep track seperately
            invars = [x for x in invars if x is not None] # will have None placeholders for Long and Double arguments
        n.invars = invars

    lookup = {}
    for n in nodes:
        assert len(intypes[n.bkey]) != 2 # should have been handled by graph.splitDualInedges()

        if False in intypes[n.bkey]:
            lookup[n.bkey, False] = n
        if True in intypes[n.bkey]:
            lookup[n.bkey, True] = n
    assert unique(lookup.values())

    for n in nodes:
        n.blockdict = lookup
        block = n.block
        for (block2, t) in block.jump.getSuccessorPairs():
            out = [phi.get((block, t)) for phi in block2.phis]

            n2 = lookup[block2.key, t]
            n.outvars[n2] = out
            n.successors.append(n2)
            n2.predecessors.append(n)

    # sanity check
    for n in allnodes:
        assert (n.block is not None) == (n.num == 0)
        assert (n is entryNode) == (len(n.predecessors) == 0)
        assert unique(n.predecessors)
        assert unique(n.successors)
        for pn in n.predecessors:
            assert n in pn.successors
        assert set(n.outvars) == set(n.successors)
        for sn in n.successors:
            assert n in sn.predecessors
            assert len(n.outvars[sn]) == len(sn.invars)

    return entryNode, allnodes
