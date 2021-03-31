import collections
import copy
import functools
import itertools

from .. import graph_util
from ..verifier.descriptors import parseUnboundMethodDescriptor

from . import blockmaker, constraints, objtypes, ssa_jumps, ssa_ops, subproc
from .ssa_types import BasicBlock, SSA_OBJECT, verifierToSSAType

class SSA_Variable(object):
    __slots__ = 'type','origin','name','const','decltype','uninit_orig_num'

    def __init__(self, type_, origin=None, name=""):
        self.type = type_       # SSA_INT, SSA_OBJECT, etc.
        self.origin = origin
        self.name = name
        self.const = None
        self.decltype = None # for objects, the inferred type from the verifier if any
        self.uninit_orig_num = None # if uninitialized, the bytecode offset of the new instr

    # for debugging
    def __str__(self):   # pragma: no cover
        return self.name if self.name else super(SSA_Variable, self).__str__()

    def __repr__(self):   # pragma: no cover
        name =  self.name if self.name else "@" + hex(id(self))
        return "Var {}".format(name)

# This class is the main IR for bytecode level methods. It consists of a control
# flow graph (CFG) in static single assignment form (SSA). Each node in the
# graph is a BasicBlock. This consists of a list of phi statements representing
# inputs, a list of operations, and a jump statement. Exceptions are represented
# explicitly in the graph with the OnException jump. Each block also keeps track
# of the unary constraints on the variables in that block.

# Handling of subprocedures is rather annoying. Each complete subproc has an associated
# ProcInfo while jsrs and rets are represented by ProcCallOp and DummyRet respectively.
# The jsrblock has the target and fallthrough as successors, while the fallthrough has
# the jsrblock as predecessor, but not the retblock. Control flow paths where the proc
# never returns are represented by ordinary jumps from blocks in the procedure to outside
# Successful completion of the proc is represented by the fallthrough edge. The fallthrough
# block gets its variables from the jsrblock, including skip vars which don't depend on the
# proc, and variables from jsr.output which represent what would have been returned from ret
# Every proc has a reachable retblock. Jsrs with no associated ret are simply turned
# into gotos during the initial basic block creation.

class SSA_Graph(object):
    entryKey = blockmaker.ENTRY_KEY

    def __init__(self, code):
        self.code = code
        self.class_ = code.class_
        self.env = self.class_.env

        self.inputArgs = None
        self.entryBlock = None
        self.blocks = None
        self.procs = None # used to store information on subprocedues (from the JSR instructions)

        self.block_numberer = itertools.count(-4,-1)

    def condenseBlocks(self):
        assert not self.procs
        old = self.blocks
        # Can't do a consistency check on entry as the graph may be in an inconsistent state at this point
        # Since the purpose of this function is to prune unreachable blocks from self.blocks
        sccs = graph_util.tarjanSCC([self.entryBlock], lambda block:block.jump.getSuccessors())
        self.blocks = list(itertools.chain.from_iterable(map(reversed, sccs[::-1])))

        assert set(self.blocks) <= set(old)
        if len(self.blocks) < len(old):
            kept = set(self.blocks)
            for block in self.blocks:
                for pair in block.predecessors[:]:
                    if pair[0] not in kept:
                        block.removePredPair(pair)
            return [b for b in old if b not in kept]
        return []

    def removeUnusedVariables(self):
        assert not self.procs
        roots = [x for x in self.inputArgs if x is not None]
        for block in self.blocks:
            roots += block.jump.params
            for op in block.lines:
                if op.has_side_effects:
                    roots += op.params

        reachable = graph_util.topologicalSort(roots, lambda var:(var.origin.params if var.origin else []))

        keepset = set(reachable)
        assert None not in keepset
        def filterOps(oldops):
            newops = []
            for op in oldops:
                # if any of the params is being removed due to being unreachable, we can assume the whole function can be removed
                keep = keepset.issuperset(op.params) and (op.has_side_effects or not keepset.isdisjoint(op.getOutputs()))
                if keep:
                    newops.append(op)
                    for v in op.getOutputs():
                        if v and v not in keepset:
                            op.removeOutput(v)
                else:
                    assert keepset.isdisjoint(op.getOutputs())
                    assert not op.has_side_effects
            return newops

        for block in self.blocks:
            block.phis = filterOps(block.phis)
            block.lines = filterOps(block.lines)
            block.filterVarConstraints(keepset)
        assert self._conscheck() is None

    def mergeSingleSuccessorBlocks(self):
        assert(not self.procs) # Make sure that all single jsr procs are inlined first
        assert self._conscheck() is None

        removed = set()
        for block in self.blocks:
            if block in removed:
                continue
            while isinstance(block.jump, ssa_jumps.Goto):
                jump = block.jump
                block2 = jump.getNormalSuccessors()[0]
                fromkey = block, False
                if block2.predecessors != [fromkey]:
                    break

                jump2 = block2.jump
                ucs = block.unaryConstraints
                ucs2 = block2.unaryConstraints
                replace = {phi.rval: phi.get(fromkey) for phi in block2.phis}
                for var2, var in replace.items():
                    ucs[var] = constraints.join(ucs[var], ucs2.pop(var2))
                ucs.update(ucs2)

                for op in block2.lines:
                    op.replaceVars(replace)
                block.lines += block2.lines

                jump2.replaceVars(replace)
                block.jump = jump2

                # remember to update phis of blocks referring to old child!
                for successor, t in block.jump.getSuccessorPairs():
                    successor.replacePredPair((block2, t), (block, t))
                    for phi in successor.phis:
                        phi.replaceVars(replace)
                removed.add(block2)
        self.blocks = [b for b in self.blocks if b not in removed]
        assert self._conscheck() is None

    def disconnectConstantVariables(self):
        for block in self.blocks:
            for var, uc in block.unaryConstraints.items():
                if var.origin is not None:
                    newval = None
                    if var.type[0] == 'int':
                        if uc.min == uc.max:
                            newval = uc.min
                    elif var.type[0] == 'obj':
                        if uc.isConstNull():
                            newval = 'null'

                    if newval is not None:
                        var.origin.removeOutput(var)
                        var.origin = None
                        var.const = newval
            block.phis = [phi for phi in block.phis if phi.rval is not None]
        assert self._conscheck() is None

    def _conscheck(self):
        '''Sanity check'''
        for block in self.blocks:
            assert block.jump is not None
            for phi in block.phis:
                assert phi.rval is None or phi.rval in block.unaryConstraints
                for k,v in phi.dict.items():
                    assert v in k[0].unaryConstraints

        keys = [block.key for block in self.blocks]
        assert len(set(keys)) == len(keys)
        temp = [self.entryBlock]
        for proc in self.procs:
            temp += [proc.retblock]
            temp += proc.jsrblocks
        assert len(set(temp)) == len(temp)

    def copyPropagation(self):
        # Loop aware copy propagation
        assert not self.procs
        assert self._conscheck() is None

        # The goal is to propagate constants that would never be inferred pessimistically
        # due to the prescence of loops. Variables that aren't derived from a constant or phi
        # are treated as opaque and variables are processed by SCC in topological order.
        # For each scc, we can infer that it is the meet of all inputs that come from variables
        # in different sccs that come before it in topological order, thus ignoring variables
        # in the current scc (the loop problem).
        v2b = {}
        assigns = collections.OrderedDict()
        for block in self.blocks:
            for var in block.unaryConstraints:
                v2b[var] = block
            for phi in block.phis:
                assigns[phi.rval] = map(phi.get, block.predecessors)

        UCs = {}
        sccs = graph_util.tarjanSCC(assigns, lambda v:assigns.get(v, []))
        for scc in sccs:
            if all(var in assigns for var in scc):
                invars = sum(map(assigns.get, scc), [])
                inputs = [UCs[invar] for invar in invars if invar in UCs]
                assert inputs
                uc = constraints.meet(*inputs)

                for var in scc:
                    old = v2b[var].unaryConstraints[var]
                    new = constraints.join(uc, old) or old # temporary hack
                    v2b[var].unaryConstraints[var] = UCs[var] = new
            else:
                # There is a root in this scc, so we can't do anything
                for var in scc:
                    UCs[var] = v2b[var].unaryConstraints[var]

        assert self._conscheck() is None

    def abstractInterpert(self):
        # Sparse conditional constant propagation and type inference
        assert not self.procs
        assert self._conscheck() is None

        visit_counts = collections.defaultdict(int)
        dirty_phis = set(itertools.chain.from_iterable(block.phis for block in self.blocks))
        while dirty_phis:
            for block in self.blocks:
                assert block in self.blocks
                UCs = block.unaryConstraints
                assert None not in UCs.values()
                dirty = visit_counts[block] == 0
                for phi in block.phis:
                    if phi in dirty_phis:
                        dirty_phis.remove(phi)
                        inputs = [key[0].unaryConstraints[phi.get(key)] for key in block.predecessors]
                        out = constraints.meet(*inputs)
                        old = UCs[phi.rval]
                        UCs[phi.rval] = out = constraints.join(old, out)
                        dirty = dirty or out != old
                        assert out

                if not dirty or visit_counts[block] >= 5:
                    continue
                visit_counts[block] += 1

                must_throw = False
                dirty_vars = set()
                last_line = block.lines[-1] if block.lines else None # Keep reference handy to exception phi, if any
                for i, op in enumerate(block.lines):
                    if hasattr(op, 'propagateConstraints'):
                        output_vars = op.getOutputs()
                        inputs = [UCs[var] for var in op.params]
                        assert None not in inputs
                        output_info = op.propagateConstraints(*inputs)

                        for var, out in zip(output_vars, [output_info.rval, output_info.eval]):
                            if var is None:
                                continue
                            old = UCs[var]
                            UCs[var] = out = constraints.join(old, out)
                            if out is None:
                                if var is op.outException:
                                    assert isinstance(last_line, ssa_ops.ExceptionPhi)
                                    last_line.params.remove(var)
                                op.removeOutput(var) # Note, this must be done after the op.outException check!
                                del UCs[var]
                            elif out != old:
                                dirty_vars.add(var)

                        if output_info.must_throw:
                            must_throw = True
                            # Remove all code after this in the basic block and adjust exception code
                            # at end as appropriate
                            assert isinstance(last_line, ssa_ops.ExceptionPhi)
                            assert i < len(block.lines) and op.outException
                            removed = block.lines[i+1:-1]
                            block.lines = block.lines[:i+1] + [last_line]
                            for op2 in removed:
                                if op2.outException:
                                    last_line.params.remove(op2.outException)
                                for var in op2.getOutputs():
                                    if var is not None:
                                        del UCs[var]
                            break

                # now handle end of block
                if isinstance(last_line, ssa_ops.ExceptionPhi):
                    inputs = map(UCs.get, last_line.params)
                    out = constraints.meet(*inputs)
                    old = UCs[last_line.outException]
                    assert out is None or not out.null
                    UCs[last_line.outException] = out = constraints.join(old, out)
                    if out is None:
                        del UCs[last_line.outException]
                        block.lines.pop()
                    elif out != old:
                        dirty_vars.add(last_line.outException)

                # prune jumps
                dobreak = False
                if hasattr(block.jump, 'constrainJumps'):
                    assert block.jump.params
                    oldEdges = block.jump.getSuccessorPairs()
                    inputs = map(UCs.get, block.jump.params)
                    block.jump = block.jump.constrainJumps(*inputs)
                    # No exception case ordinarily won't be pruned, so we have to handle it explicitly
                    if must_throw and isinstance(block.jump, ssa_jumps.OnException):
                        if block.jump.getNormalSuccessors(): # make sure it wasn't already pruned
                            fallthrough = block.jump.getNormalSuccessors()[0]
                            block.jump = block.jump.reduceSuccessors([(fallthrough, False)])

                    newEdges = block.jump.getSuccessorPairs()
                    if newEdges != oldEdges:
                        pruned = [x for x in oldEdges if x not in newEdges]
                        for (child,t) in pruned:
                            child.removePredPair((block,t))

                        removed_blocks = self.condenseBlocks()
                        # In case where no blocks were removed, self.blocks will possibly be in a different
                        # order than the version of self.blocks we are iterating over, but it still has the
                        # same contents, so this should be safe. If blocks were removed, we break out of the
                        # list and restart to avoid the possibility of processing an unreachable block.
                        dobreak = len(removed_blocks) > 0
                        for removed in removed_blocks:
                            for phi in removed.phis:
                                dirty_phis.discard(phi)

                # update dirty set
                for child, t in block.jump.getSuccessorPairs():
                    assert child in self.blocks
                    for phi in child.phis:
                        if phi.get((block, t)) in dirty_vars:
                            dirty_phis.add(phi)
                if dobreak:
                    break

        # Try to turn switches into if statements - note that this may
        # introduce a new variable and this modify block.unaryConstraints
        # However, it won't change the control flow graph structure
        for block in self.blocks:
            if isinstance(block.jump, ssa_jumps.Switch):
                block.jump = block.jump.simplifyToIf(block)

    def simplifyThrows(self):
        # Try to turn throws into gotos where possible. This primarily helps with certain patterns of try-with-resources
        # To do this, the exception must be known to be non null and there must be only one target that can catch it
        # As a heuristic, we also restrict it to cases where every predecessor of the target can be converted
        candidates = collections.defaultdict(list)
        for block in self.blocks:
            if not isinstance(block.jump, ssa_jumps.OnException) or len(block.jump.getSuccessorPairs()) != 1:
                continue
            if len(block.lines[-1].params) != 1 or not isinstance(block.lines[-2], ssa_ops.Throw):
                continue
            if block.unaryConstraints[block.lines[-2].params[0]].null:
                continue

            candidates[block.jump.getExceptSuccessors()[0]].append(block)

        for child in self.blocks:
            if not candidates[child] or len(candidates[child]) < len(child.predecessors):
                continue

            for parent in candidates[child]:
                ephi = parent.lines.pop()
                throw_op = parent.lines.pop()

                var1 = throw_op.params[0]
                var2 = throw_op.outException
                assert ephi.params == [var2]
                var3 = ephi.outException
                assert parent.jump.params[0] == var3

                for phi in child.phis:
                    phi.replaceVars({var3: var1})
                child.replacePredPair((parent, True), (parent, False))

                del parent.unaryConstraints[var2]
                del parent.unaryConstraints[var3]
                parent.jump = ssa_jumps.Goto(self, child)

    def simplifyCatchIgnored(self):
        # When there is a single throwing instruction, which is garuenteed to throw, has a single handler, and
        # the caught exception is unused, turn it into a goto. This simplifies a pattern used by some obfuscators
        # that do stuff like try{new int[-1];} catch(Exception e) {...}
        candidates = collections.defaultdict(list)
        for block in self.blocks:
            if not isinstance(block.jump, ssa_jumps.OnException) or len(block.jump.getSuccessorPairs()) != 1:
                continue
            if len(block.lines[-1].params) != 1:
                continue
            candidates[block.jump.getExceptSuccessors()[0]].append(block)

        for child in self.blocks:
            if not candidates[child] or len(candidates[child]) < len(child.predecessors):
                continue

            # Make sure caught exception is unused
            temp = candidates[child][0].lines[-1].outException
            if any(temp in phi.params for phi in child.phis):
                continue

            for parent in candidates[child]:
                ephi = parent.lines.pop()
                throw_op = parent.lines.pop()
                del parent.unaryConstraints[throw_op.outException]
                del parent.unaryConstraints[ephi.outException]
                child.replacePredPair((parent, True), (parent, False))
                parent.jump = ssa_jumps.Goto(self, child)

    # Subprocedure stuff #####################################################
    def _newBlockFrom(self, block):
        b = BasicBlock(next(self.block_numberer))
        self.blocks.append(b)
        return b

    def _copyVar(self, var, vard=None):
        v = copy.copy(var)
        v.name = v.origin = None # TODO - generate new names?
        if vard is not None:
            vard[var] = v
        return v

    def _region(self, proc):
        # Find the set of blocks 'in' a subprocedure, i.e. those reachable from the target that can reach the ret block
        region = graph_util.topologicalSort([proc.retblock], lambda block:[] if block == proc.target else [b for b,t in block.predecessors])
        temp = set(region)
        assert self.entryBlock not in temp and proc.target in temp and temp.isdisjoint(proc.jsrblocks)
        return region

    def _duplicateBlocks(self, region, excludedPreds):
        # Duplicate a region of blocks. All inedges will be redirected to the new blocks
        # except for those from excludedPreds
        excludedPreds = excludedPreds | set(region)
        outsideBlocks = [b for b in self.blocks if b not in excludedPreds]

        blockd, vard = {}, {}
        for oldb in region:
            block = blockd[oldb] = self._newBlockFrom(oldb)
            block.unaryConstraints = {self._copyVar(k, vard):v for k, v in oldb.unaryConstraints.items()}
            block.phis = [ssa_ops.Phi(block, vard[oldphi.rval]) for oldphi in oldb.phis]

            for op in oldb.lines:
                new = copy.copy(op)
                new.replaceVars(vard)
                new.replaceOutVars(vard)
                assert new.getOutputs().count(None) == op.getOutputs().count(None)
                for outv in new.getOutputs():
                    if outv is not None:
                        assert outv.origin is None
                        outv.origin = new
                block.lines.append(new)

            assert set(vard).issuperset(oldb.jump.params)
            block.jump = oldb.jump.clone()
            block.jump.replaceVars(vard)

            # Fix up blocks outside the region that jump into the region.
            for key in oldb.predecessors[:]:
                pred = key[0]
                if pred not in excludedPreds:
                    for phi1, phi2 in zip(oldb.phis, block.phis):
                        phi2.add(key, phi1.get(key))
                        del phi1.dict[key]
                    oldb.predecessors.remove(key)
                    block.predecessors.append(key)

        # fix up jump targets of newly created blocks
        for oldb, block in blockd.items():
            block.jump.replaceBlocks(blockd)
            for suc, t in block.jump.getSuccessorPairs():
                suc.predecessors.append((block, t))

        # update the jump targets of predecessor blocks
        for block in outsideBlocks:
            block.jump.replaceBlocks(blockd)

        for old, new in vard.items():
            assert type(old.origin) == type(new.origin)

        # Fill in phi args in successors of new blocks
        for oldb, block in blockd.items():
            for oldc, t in oldb.jump.getSuccessorPairs():
                child = blockd.get(oldc, oldc)
                assert len(child.phis) == len(oldc.phis)
                for phi1, phi2 in zip(oldc.phis, child.phis):
                    phi2.add((block, t), vard[phi1.get((oldb, t))])

        assert self._conscheck() is None
        return blockd

    def _splitSubProc(self, proc):
        # Splits a proc into two, with one callsite using the new proc instead
        # this involves duplicating the body of the procedure
        # the new proc is appended to the list of procs so it can work properly
        # with the stack processing in inlineSubprocs
        assert len(proc.jsrblocks) > 1
        target, retblock = proc.target, proc.retblock
        region = self._region(proc)

        split_jsrs = [proc.jsrblocks.pop()]
        blockd = self._duplicateBlocks(region, set(proc.jsrblocks))

        newproc = subproc.ProcInfo(blockd[proc.retblock], blockd[proc.target])
        newproc.jsrblocks = split_jsrs
        # Sanity check
        for temp in self.procs + [newproc]:
            for jsr in temp.jsrblocks:
                assert jsr.jump.target == temp.target
        return newproc

    def _inlineSubProc(self, proc):
        # Inline a proc with single callsite inplace
        assert len(proc.jsrblocks) == 1
        target, retblock = proc.target, proc.retblock
        region = self._region(proc)

        jsrblock = proc.jsrblocks[0]
        jsrop = jsrblock.jump
        ftblock = jsrop.fallthrough

        # first we find any vars that bypass the proc since we have to pass them through the new blocks
        skipvars = [phi.get((jsrblock, False)) for phi in ftblock.phis]
        skipvars = [var for var in skipvars if var.origin is not jsrop]

        svarcopy = {(var, block):self._copyVar(var) for var, block in itertools.product(skipvars, region)}
        for var, block in itertools.product(skipvars, region):
            # Create a new phi for the passed through var for this block
            rval = svarcopy[var, block]
            phi = ssa_ops.Phi(block, rval)
            block.phis.append(phi)
            block.unaryConstraints[rval] = jsrblock.unaryConstraints[var]

            for key in block.predecessors:
                if key == (jsrblock, False):
                    phi.add(key, var)
                else:
                    phi.add(key, svarcopy[var, key[0]])

        outreplace = {jv:rv for jv, rv in zip(jsrblock.jump.output.stack, retblock.jump.input.stack) if jv is not None}
        outreplace.update({jv:retblock.jump.input.locals[i] for i, jv in jsrblock.jump.output.locals.items() if jv is not None})
        for var in outreplace: # don't need jsrop's out vars anymore
            del jsrblock.unaryConstraints[var]

        for var in skipvars:
            outreplace[var] = svarcopy[var, retblock]
        jsrblock.jump = ssa_jumps.Goto(self, target)
        retblock.jump = ssa_jumps.Goto(self, ftblock)

        ftblock.replacePredPair((jsrblock, False), (retblock, False))
        for phi in ftblock.phis:
            phi.replaceVars(outreplace)

    def inlineSubprocs(self):
        assert self._conscheck() is None
        assert self.procs

        # establish DAG of subproc callstacks if we're doing nontrivial inlining, since we can only inline leaf procs
        regions = {proc:frozenset(self._region(proc)) for proc in self.procs}
        parents = {proc:[] for proc in self.procs}
        for x,y in itertools.product(self.procs, repeat=2):
            if not regions[y].isdisjoint(x.jsrblocks):
                parents[x].append(y)

        self.procs = graph_util.topologicalSort(self.procs, parents.get)
        if any(parents.values()):
            print 'Warning, nesting subprocedures detected! This method may take a long time to decompile.'
        print 'Subprocedures for', self.code.method.name + ':', self.procs

        # now inline the procs
        while self.procs:
            proc = self.procs.pop()
            while len(proc.jsrblocks) > 1:
                print 'splitting', proc
                # push new subproc onto stack
                self.procs.append(self._splitSubProc(proc))
                assert self._conscheck() is None
            # When a subprocedure has only one call point, it can just be inlined instead of splitted
            print 'inlining', proc
            self._inlineSubProc(proc)
            assert self._conscheck() is None
    ##########################################################################
    def splitDualInedges(self):
        # Split any blocks that have both normal and exceptional in edges
        assert not self.procs
        for block in self.blocks[:]:
            if block is self.entryBlock:
                continue
            types = set(zip(*block.predecessors)[1])
            if len(types) <= 1:
                continue
            assert not isinstance(block.jump, (ssa_jumps.Return, ssa_jumps.Rethrow))

            new = self._newBlockFrom(block)
            print 'Splitting', block, '->', new
            # first fix up CFG edges
            badpreds = [t for t in block.predecessors if t[1]]
            new.predecessors = badpreds
            for t in badpreds:
                block.predecessors.remove(t)

            for pred, _ in badpreds:
                assert isinstance(pred.jump, ssa_jumps.OnException)
                pred.jump.replaceExceptTarget(block, new)

            new.jump = ssa_jumps.Goto(self, block)
            block.predecessors.append((new, False))

            # fix up variables
            new.phis = []
            new.unaryConstraints = {}
            for phi in block.phis:
                newrval = self._copyVar(phi.rval)
                new.unaryConstraints[newrval] = block.unaryConstraints[phi.rval]
                newphi = ssa_ops.Phi(new, newrval)
                new.phis.append(newphi)

                for t in badpreds:
                    arg = phi.get(t)
                    phi.delete(t)
                    newphi.add(t, arg)
                phi.add((new, False), newrval)
        assert self._conscheck() is None

    def fixLoops(self):
        assert not self.procs
        todo = self.blocks[:]
        while todo:
            newtodo = []
            temp = set(todo)
            sccs = graph_util.tarjanSCC(todo, lambda block:[x for x,t in block.predecessors if x in temp])

            for scc in sccs:
                if len(scc) <= 1:
                    continue

                scc_pair_set = {(x, False) for x in scc} | {(x, True) for x in scc}
                entries = [n for n in scc if not scc_pair_set.issuperset(n.predecessors)]

                if len(entries) <= 1:
                    head = entries[0]
                else:
                    # if more than one entry point into the loop, we have to choose one as the head and duplicate the rest
                    print 'Warning, multiple entry point loop detected. Generated code may be extremely large',
                    print '({} entry points, {} blocks)'.format(len(entries), len(scc))
                    def loopSuccessors(head, block):
                        if block == head:
                            return []
                        return [x for x in block.jump.getSuccessors() if (x, False) in scc_pair_set]

                    reaches = [(n, graph_util.topologicalSort(entries, functools.partial(loopSuccessors, n))) for n in scc]
                    for head, reachable in reaches:
                        reachable.remove(head)

                    head, reachable = min(reaches, key=lambda t:(len(t[1]), -len(t[0].predecessors)))
                    assert head not in reachable
                    print 'Duplicating {} nodes'.format(len(reachable))
                    blockd = self._duplicateBlocks(reachable, set(scc) - set(reachable))
                    newtodo += map(blockd.get, reachable)
                newtodo.extend(scc)
                newtodo.remove(head)
            todo = newtodo
        assert self._conscheck() is None

    # Functions called by children ###########################################
    # assign variable names for debugging
    varnum = collections.defaultdict(itertools.count)
    def makeVariable(self, *args, **kwargs):
        # Note: Make sure this doesn't hold on to created variables in any way,
        # since this func may be called for temporary results that are discarded
        var = SSA_Variable(*args, **kwargs)
        # pref = args[0][0][0].replace('o','a')
        # var.name = pref + str(next(self.varnum[pref]))
        return var

    def setObjVarData(self, var, vtype, initMap):
        vtype2 = initMap.get(vtype, vtype)
        tt = objtypes.verifierToSynthetic(vtype2)
        assert var.decltype is None or var.decltype == tt
        var.decltype = tt
        # if uninitialized, record the offset of originating new instruction for later
        if vtype.tag == '.new':
            assert var.uninit_orig_num is None or var.uninit_orig_num == vtype.extra
            var.uninit_orig_num = vtype.extra

    def makeVarFromVtype(self, vtype, initMap):
        vtype2 = initMap.get(vtype, vtype)
        type_ = verifierToSSAType(vtype2)
        if type_ is not None:
            var = self.makeVariable(type_)
            if type_ == SSA_OBJECT:
                self.setObjVarData(var, vtype, initMap)
            return var
        return None

    def getConstPoolArgs(self, index):
        return self.class_.cpool.getArgs(index)

    def getConstPoolType(self, index):
        return self.class_.cpool.getType(index)

def ssaFromVerified(code, iNodes, opts):
    method = code.method
    inputTypes, returnTypes = parseUnboundMethodDescriptor(method.descriptor, method.class_.name, method.static)

    parent = SSA_Graph(code)
    data = blockmaker.BlockMaker(parent, iNodes, inputTypes, returnTypes, code.except_raw, opts=opts)

    parent.blocks = blocks = data.blocks
    parent.entryBlock = data.entryBlock
    parent.inputArgs = data.inputArgs
    assert parent.entryBlock in blocks

    # create subproc info
    procd = {block.jump.target: subproc.ProcInfo(block, block.jump.target) for block in blocks if isinstance(block.jump, subproc.DummyRet)}
    for block in blocks:
        if isinstance(block.jump, subproc.ProcCallOp):
            procd[block.jump.target].jsrblocks.append(block)
    parent.procs = sorted(procd.values(), key=lambda p:p.target.key)

    # Intern constraints to save a bit of memory for long methods
    def makeConstraint(var, _cache={}):
        key = var.type, var.const, var.decltype, var.uninit_orig_num is None
        try:
            return _cache[key]
        except KeyError:
            _cache[key] = temp = constraints.fromVariable(parent.env, var)
            return temp

    # create unary constraints for each variable
    for block in blocks:
        bvars = []
        if isinstance(block.jump, subproc.ProcCallOp):
            bvars += block.jump.flatOutput()
        # entry block has no phis
        if block is parent.entryBlock:
            bvars += parent.inputArgs

        bvars = [v for v in bvars if v is not None]
        bvars += [phi.rval for phi in block.phis]
        for op in block.lines:
            bvars += op.params
            bvars += [x for x in op.getOutputs() if x is not None]
        bvars += block.jump.params

        for suc, t in block.jump.getSuccessorPairs():
            for phi in suc.phis:
                bvars.append(phi.get((block, t)))
        assert None not in bvars
        # Note that makeConstraint can indirectly cause class loading
        block.unaryConstraints = {var:makeConstraint(var) for var in bvars}

    parent._conscheck()
    return parent
