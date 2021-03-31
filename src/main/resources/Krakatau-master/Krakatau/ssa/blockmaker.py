import collections
import operator

from .. import opnames as vops
from ..verifier import verifier_types

from . import ssa_jumps, ssa_ops, subproc
from .blockmakerfuncs import ResultDict, instructionHandlers
from .ssa_types import BasicBlock, SSA_OBJECT, slots_t

def toBits(x): return [i for i in range(x.bit_length()) if x & (1 << i)]

# keys for special blocks created at the cfg entry and exit. Negative keys ensures they don't collide
ENTRY_KEY, RETURN_KEY, RETHROW_KEY = -1, -2, -3

def getUsedLocals(iNodes, iNodeD, exceptions):
    # For every instruction, find which locals at that point may be used in the future
    except_ranges = [(h, [node.key for node in iNodes if s <= node.key < e]) for s, e, h, i in exceptions]

    old = collections.defaultdict(int)
    while 1:
        data = old.copy()
        # Do one iteration
        for node in reversed(iNodes):
            used = reduce(operator.__or__, (data[key] for key in node.successors), 0)

            if node.instruction[0] == vops.LOAD:
                used |= 1 << node.instruction[2]
            elif node.instruction[0] == vops.IINC:
                used |= 1 << node.instruction[1]
            elif node.instruction[0] == vops.STORE:
                bits = 3 if node.instruction[1] in 'JD' else 1
                used &= ~(bits << node.instruction[2])
            elif node.instruction[0] == vops.RET:
                # If local is not in mask, it will use the value from the jsr instead of the ret
                mask = sum(1<<i for i in node.out_state.copy().maskFor(node.jsrTarget))
                used &= mask
            elif node.instruction[0] == vops.JSR and node.returnedFrom is not None:
                retnode = iNodeD[node.returnedFrom]
                assert node.successors == (retnode.jsrTarget,)
                mask = sum(1<<i for i in iNodeD[node.returnedFrom].out_state.copy().maskFor(retnode.jsrTarget))

                assert node.next_instruction is not None
                used |= (data[node.next_instruction] & ~mask)
            data[node.key] |= used

        for hkey, region in except_ranges:
            if data[hkey] != old[hkey]:
                for key in region:
                    data[key] |= data[hkey]

        if data == old:
            break
        old = data
    # for entry point, every program argument is marked used so we can preserve input arguments for later
    old[ENTRY_KEY] = (1 << len(iNodeD[0].state.locals)) - 1
    return old

def slotsRvals(inslots):
    stack = [(None if phi is None else phi.rval) for phi in inslots.stack]
    newlocals = {i: phi.rval for i, phi in inslots.locals.items() if phi is not None}
    return slots_t(stack=stack, locals=newlocals)

_jump_instrs = frozenset([vops.GOTO, vops.IF_A, vops.IF_ACMP, vops.IF_I, vops.IF_ICMP, vops.JSR, vops.SWITCH])
class BlockMaker(object):
    def __init__(self, parent, iNodes, inputTypes, returnTypes, except_raw, opts):
        self.parent = parent
        self.blocks = []
        self.blockd = {}

        self.iNodes = [n for n in iNodes if n.visited]
        self.iNodeD = {n.key: n for n in self.iNodes}
        exceptions = [eh for eh in except_raw if eh.handler in self.iNodeD]

        # Calculate which locals are actually live at any point
        self.used_locals = getUsedLocals(self.iNodes, self.iNodeD, exceptions)

        # create map of uninitialized -> initialized types so we can convert them
        self.initMap = {}
        for node in self.iNodes:
            if node.op == vops.NEW:
                self.initMap[node.stack_push[0]] = node.target_type
        self.initMap[verifier_types.T_UNINIT_THIS] = verifier_types.T_OBJECT(parent.class_.name)
        self.hasmonenter = any(node.instruction[0] == vops.MONENTER for node in self.iNodes)

        self.entryBlock = self.makeBlockWithInslots(ENTRY_KEY, newlocals=inputTypes, stack=[])
        self.returnBlock = self.makeBlockWithInslots(RETURN_KEY, newlocals=[], stack=returnTypes)
        self.returnBlock.jump = ssa_jumps.Return(self, [phi.rval for phi in self.returnBlock.phis])
        self.rethrowBlock = self.makeBlockWithInslots(RETHROW_KEY, newlocals=[], stack=[verifier_types.THROWABLE_INFO])
        self.rethrowBlock.jump = ssa_jumps.Rethrow(self, [phi.rval for phi in self.rethrowBlock.phis])

        # for ssagraph to copy
        self.inputArgs = slotsRvals(self.entryBlock.inslots).localsAsList
        self.entryBlock.phis = []

        # We need to create stub blocks for every jump target so we can add them as successors during creation
        jump_targets = [eh.handler for eh in exceptions]
        for node in self.iNodes:
            if node.instruction[0] in _jump_instrs:
                jump_targets += node.successors
            # add jsr fallthroughs too
            if node.instruction[0] == vops.JSR and node.returnedFrom is not None:
                jump_targets.append(node.next_instruction)

        # for simplicity, keep jsr stuff in individual instruction blocks.
        # Note that subproc.py will need to be modified if this is changed
        for node in self.iNodes:
            if node.instruction[0] in (vops.JSR, vops.RET):
                jump_targets.append(node.key)
        for key in jump_targets:
            if key not in self.blockd: # jump_targets may have duplicates
                self.makeBlock(key)

        self.exceptionhandlers = []
        for (start, end, handler, index) in exceptions:
            catchtype = parent.getConstPoolArgs(index)[0] if index else 'java/lang/Throwable'
            self.exceptionhandlers.append((start, end, self.blockd[handler], catchtype))
        self.exceptionhandlers.append((0, 65536, self.rethrowBlock, 'java/lang/Throwable'))

        # State variables for the append/builder loop
        self.current_block = self.entryBlock
        self.current_slots = slotsRvals(self.current_block.inslots)
        for node in self.iNodes:
            # First do a quick check if we have to start a new block
            if not self._canContinueBlock(node):
                self._startNewBlock(node.key)
            vals, outslot_norm = self._getInstrLine(node)

            # Disable exception pruning
            if opts and not vals.jump:
                dummyvals = ResultDict(line=ssa_ops.MagicThrow(self.parent))
                if not self._canAppendInstrToCurrent(node.key, dummyvals):
                    self._startNewBlock(node.key)
                assert self._canAppendInstrToCurrent(node.key, dummyvals)
                self._appendInstr(node, dummyvals, self.current_slots, check_terminate=False)
                vals, outslot_norm = self._getInstrLine(node)

            if not self._canAppendInstrToCurrent(node.key, vals):
                self._startNewBlock(node.key)
                vals, outslot_norm = self._getInstrLine(node)

            assert self._canAppendInstrToCurrent(node.key, vals)
            self._appendInstr(node, vals, outslot_norm)

        # do sanity checks
        assert len(self.blocks) == len(self.blockd)
        for block in self.blocks:
            assert block.jump is not None and block.phis is not None
            assert len(block.predecessors) == len(set(block.predecessors))
            # cleanup temp vars
            block.inslots = None
            block.throwvars = None
            block.chpairs = None
            block.except_used = None
            block.locals_at_except = None

    def _canContinueBlock(self, node):
        return (node.key not in self.blockd) and self.current_block.jump is None # fallthrough goto left as None

    def _chPairsAt(self, address):
        chpairs = []
        for (start, end, handler, catchtype) in self.exceptionhandlers:
            if start <= address < end:
                chpairs.append((catchtype, handler))
        return chpairs

    def _canAppendInstrToCurrent(self, address, vals):
        # If appending exception line to block with existing exceptions, make sure the handlers are the same
        # Also make sure that locals are compatible with all other exceptions in the block
        # If appending a jump, make sure there is no existing exceptions
        block = self.current_block
        if block.chpairs is not None:
            if vals.jump:
                return False
            if vals.line is not None and vals.line.outException is not None:
                chpairs = self._chPairsAt(address)
                if chpairs != block.chpairs:
                    return False

                newlocals = {i: self.current_slots.locals[i] for i in block.except_used}
                return newlocals == block.locals_at_except
        assert block.jump is None
        return True

    def pruneUnused(self, key, newlocals):
        used = toBits(self.used_locals[key])
        return {i: newlocals[i] for i in used}

    def _startNewBlock(self, key):
        ''' We can't continue appending to the current block, so start a new one (or use existing one at location) '''
        # Make new block
        if key not in self.blockd:
            self.makeBlock(key)

        # Finish current block
        block = self.current_block
        curslots = self.current_slots
        assert block.key != key
        if block.jump is None:
            if block.chpairs is not None:
                assert block.throwvars
                self._addOnException(block, self.blockd[key], curslots)
            else:
                assert not block.throwvars
                block.jump = ssa_jumps.Goto(self.parent, self.blockd[key])

        if curslots is not None:
            self.mergeIn((block, False), key, curslots)

        # Update state
        self.current_block = self.blockd[key]
        self.current_slots = slotsRvals(self.current_block.inslots)

    def _getInstrLine(self, iNode):
        parent, initMap = self.parent, self.initMap
        inslots = self.current_slots
        instr = iNode.instruction

        # internal variables won't have any preset type info associated, so we should add in the info from the verifier
        assert len(inslots.stack) == len(iNode.state.stack)
        for i, ivar in enumerate(inslots.stack):
            if ivar and ivar.type == SSA_OBJECT and ivar.decltype is None:
                parent.setObjVarData(ivar, iNode.state.stack[i], initMap)

        for i, ivar in inslots.locals.items():
            if ivar and ivar.type == SSA_OBJECT and ivar.decltype is None:
                parent.setObjVarData(ivar, iNode.state.locals[i], initMap)

        vals = instructionHandlers[instr[0]](self, inslots, iNode)
        newstack = vals.newstack if vals.newstack is not None else inslots.stack
        newlocals = vals.newlocals if vals.newlocals is not None else inslots.locals
        outslot_norm = slots_t(locals=newlocals, stack=newstack)
        return vals, outslot_norm

    def _addOnException(self, block, fallthrough, outslot_norm):
        parent = self.parent
        assert block.throwvars and block.chpairs is not None
        ephi = ssa_ops.ExceptionPhi(parent, block.throwvars)
        block.lines.append(ephi)

        assert block.jump is None
        block.jump = ssa_jumps.OnException(parent, ephi.outException, block.chpairs, fallthrough)
        outslot_except = slots_t(locals=block.locals_at_except, stack=[ephi.outException])
        for suc in block.jump.getExceptSuccessors():
            self.mergeIn((block, True), suc.key, outslot_except)

    def _appendInstr(self, iNode, vals, outslot_norm, check_terminate=True):
        parent = self.parent
        block = self.current_block
        line, jump = vals.line, vals.jump
        if line is not None:
            block.lines.append(line)
        assert block.jump is None
        block.jump = jump

        if line is not None and line.outException is not None:
            block.throwvars.append(line.outException)
            inslots = self.current_slots

            if block.chpairs is None:
                block.chpairs = self._chPairsAt(iNode.key)
                temp = (self.used_locals[h.key] for t, h in block.chpairs)
                block.except_used = toBits(reduce(operator.__or__, temp, 0))
                block.locals_at_except = {i: inslots.locals[i] for i in block.except_used}

            if check_terminate:
                # Return and Throw must be immediately ended because they don't have normal fallthrough
                # CheckCast must terminate block because cast type hack later on requires casts to be at end of block
                if iNode.instruction[0] in (vops.RETURN, vops.THROW) or isinstance(line, ssa_ops.CheckCast):
                    fallthrough = self.getExceptFallthrough(iNode)
                    self._addOnException(block, fallthrough, outslot_norm)

        if block.jump is None:
            unmerged_slots = outslot_norm
        else:
            assert isinstance(block.jump, ssa_jumps.OnException) or not block.throwvars
            unmerged_slots = None
            # Make sure that branch targets are distinct, since this is assumed everywhere
            # Only necessary for if statements as the other jumps merge targets automatically
            # If statements with both branches jumping to same target are replaced with gotos
            block.jump = block.jump.reduceSuccessors([])

            if isinstance(block.jump, subproc.ProcCallOp):
                self.mergeJSROut(iNode, block, outslot_norm)
            else:
                for suc in block.jump.getNormalSuccessors():
                    self.mergeIn((block, False), suc.key, outslot_norm)
        self.current_slots = unmerged_slots
        assert (block.chpairs is None) == (block.except_used is None) == (block.locals_at_except is None)

    def mergeIn(self, from_key, target_key, outslots):
        inslots = self.blockd[target_key].inslots

        assert len(inslots.stack) == len(outslots.stack)
        for i, phi in enumerate(inslots.stack):
            if phi is not None:
                phi.add(from_key, outslots.stack[i])

        for i, phi in inslots.locals.items():
            if phi is not None:
                phi.add(from_key, outslots.locals[i])

        self.blockd[target_key].predecessors.append(from_key)

    ## Block Creation #########################################
    def _makePhiFromVType(self, block, vt):
        var = self.parent.makeVarFromVtype(vt, self.initMap)
        return None if var is None else ssa_ops.Phi(block, var)

    def makeBlockWithInslots(self, key, newlocals, stack):
        assert key not in self.blockd
        block = BasicBlock(key)
        self.blocks.append(block)
        self.blockd[key] = block

        # create inslot phis
        stack = [self._makePhiFromVType(block, vt) for vt in stack]
        newlocals = dict(enumerate(self._makePhiFromVType(block, vt) for vt in newlocals))
        newlocals = self.pruneUnused(key, newlocals)

        block.inslots = slots_t(locals=newlocals, stack=stack)
        block.phis = [phi for phi in stack + block.inslots.localsAsList if phi is not None]
        return block

    def makeBlock(self, key):
        node = self.iNodeD[key]
        return self.makeBlockWithInslots(key, node.state.locals, node.state.stack)

    ###########################################################
    def getExceptFallthrough(self, iNode):
        vop = iNode.instruction[0]
        if vop == vops.RETURN:
            return self.blockd[RETURN_KEY]
        elif vop == vops.THROW:
            return None
        key = iNode.successors[0]
        if key not in self.blockd:
            self.makeBlock(key)
        return self.blockd[key]

    def mergeJSROut(self, jsrnode, block, outslot_norm):
        retnode = self.iNodeD[jsrnode.returnedFrom]
        jump = block.jump
        target_key, ft_key = jump.target.key, jump.fallthrough.key
        assert ft_key == jsrnode.next_instruction

        # first merge regular jump to target
        self.mergeIn((block, False), target_key, outslot_norm)
        # create merged outslots for fallthrough
        fromcall = jump.output
        mask = [mask for key, mask in retnode.state.masks if key == target_key][0]

        skiplocs = fromcall.locals
        retlocs = outslot_norm.locals
        merged = {i: (skiplocs.get(i) if i in mask else retlocs.get(i)) for i in (mask | frozenset(retlocs))}
        # jump.debug_skipvars = set(merged) - set(locals)

        outslot_merged = slots_t(locals=merged, stack=fromcall.stack)
        # merge merged outputs with fallthrough
        self.mergeIn((block, False), ft_key, outslot_merged)
