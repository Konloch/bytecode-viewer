import itertools

from .. import bytecode, error as error_types, opnames as ops

from .descriptors import parseFieldDescriptor, parseMethodDescriptor, parseUnboundMethodDescriptor
from .verifier_types import OBJECT_INFO, T_ADDRESS, T_ARRAY, T_DOUBLE, T_FLOAT, T_INT, T_INT_CONST, T_INVALID, T_LONG, T_NULL, T_OBJECT, T_UNINIT_OBJECT, T_UNINIT_THIS, decrementDim, exactArrayFrom, fullinfo_t, mergeTypes

class VerifierTypesState(object):
    def __init__(self, stack, locals, masks):
        self.stack = stack
        self.locals = locals
        self.masks = masks

    def copy(self): return VerifierTypesState(self.stack, self.locals, self.masks)

    def withExcept(self, t): return VerifierTypesState([t], self.locals, self.masks)

    def pop(self, n):
        if n == 0:
            return []
        self.stack, popped = self.stack[:-n], self.stack[-n:]
        return popped

    def push(self, vals):
        self.stack = self.stack + list(vals)

    def setLocal(self, i, v):
        if len(self.locals) < i:
            self.locals = self.locals + [T_INVALID]*(i - len(self.locals))
        self.locals = self.locals[:i] + [v] + self.locals[i+1:]

        new = frozenset([i])
        self.masks = [(addr, old | new) for addr, old in self.masks]

    def local(self, i):
        if len(self.locals) <= i:
            return T_INVALID
        return self.locals[i]

    def jsr(self, target):
        self.masks = self.masks + [(target, frozenset())]

    def replace(self, old, new):
        self.stack = [(new if v == old else v) for v in self.stack]

        mask = frozenset(i for i, v in enumerate(self.locals) if v == old)
        self.locals = [(new if v == old else v) for v in self.locals]
        self.masks = [(addr, oldmask | mask) for addr, oldmask in self.masks]

    def invalidateNews(self):
        # Doesn't need to update mask
        self.stack = [(T_INVALID if v.tag == '.new' else v) for v in self.stack]
        self.locals = [(T_INVALID if v.tag == '.new' else v) for v in self.locals]

    def maskFor(self, called):
        self.masks = self.masks[:]
        target, mask = self.masks.pop()
        while target != called:
            target, mask = self.masks.pop()
        return mask

    def returnTo(self, called, jsrstate):
        mask = self.maskFor(called)
        # merge locals using mask
        zipped = itertools.izip_longest(self.locals, jsrstate.locals, fillvalue=T_INVALID)
        self.locals = [(x if i in mask else y) for i,(x,y) in enumerate(zipped)]

    def merge(self, other, env):
        old_triple = self.stack, self.locals, self.masks
        assert len(self.stack) == len(other.stack)
        self.stack = [mergeTypes(env, new, old) for old, new in zip(self.stack, other.stack)]
        self.locals = [mergeTypes(env, new, old) for old, new in zip(self.locals, other.locals)]
        while self.locals and self.locals[-1] == T_INVALID:
            self.locals.pop()

        # Merge Masks
        last_match = -1
        mergedmasks = []
        for entry1, mask1 in self.masks:
            for j, (entry2, mask2) in enumerate(other.masks):
                if j > last_match and entry1 == entry2:
                    item = entry1, (mask1 | mask2)
                    mergedmasks.append(item)
                    last_match = j
        self.masks = mergedmasks
        return (self.stack, self.locals, self.masks) != old_triple

def stateFromInitialArgs(args): return VerifierTypesState([], args[:], [])



_invoke_ops = (ops.INVOKESPECIAL, ops.INVOKESTATIC, ops.INVOKEVIRTUAL, ops.INVOKEINTERFACE, ops.INVOKEINIT, ops.INVOKEDYNAMIC)

def _loadFieldDesc(cpool, ind):
    target, name, desc = cpool.getArgsCheck('Field', ind)
    return parseFieldDescriptor(desc)

def _loadMethodDesc(cpool, ind):
    target, name, desc = cpool.getArgs(ind)
    return parseMethodDescriptor(desc)

def _indexToCFMInfo(cpool, ind, typen):
    actual = cpool.getType(ind)
    # JVM_GetCPMethodClassNameUTF accepts both
    assert actual == typen or actual == 'InterfaceMethod' and typen == 'Method'

    cname = cpool.getArgs(ind)[0]
    if cname.startswith('[') or cname.endswith(';'):
        try:
            return parseFieldDescriptor(cname)[0]
        except ValueError as e:
            return T_INVALID
    else:
        return T_OBJECT(cname)

# Instructions which pop a fixed amount
_popAmount = {
    ops.ARRLOAD_OBJ: 2,
    ops.ARRSTORE_OBJ: 3,
    ops.ARRLOAD: 2,
    ops.TRUNCATE: 1,
    ops.LCMP: 4,
    ops.IF_A: 1,
    ops.IF_I: 1,
    ops.IF_ACMP: 2,
    ops.IF_ICMP: 2,
    ops.SWITCH: 1,
    ops.NEWARRAY: 1,
    ops.ANEWARRAY: 1,
    ops.ARRLEN: 1,
    ops.THROW: 1,
    ops.CHECKCAST: 1,
    ops.INSTANCEOF: 1,
    ops.MONENTER: 1,
    ops.MONEXIT: 1,
    ops.GETFIELD: 1,

    ops.NOP: 0,
    ops.CONSTNULL: 0,
    ops.CONST: 0,
    ops.LDC: 0,
    ops.LOAD: 0,
    ops.IINC: 0,
    ops.GOTO: 0,
    ops.JSR: 0,
    ops.RET: 0,
    ops.NEW: 0,
    ops.GETSTATIC: 0,
}
# Instructions which pop a variable amount depending on whether type is category 2
_popAmountVar = {
    ops.STORE: (1, 0),
    ops.NEG: (1, 0),
    ops.CONVERT: (1, 0),

    ops.ADD: (2, 0),
    ops.SUB: (2, 0),
    ops.MUL: (2, 0),
    ops.DIV: (2, 0),
    ops.REM: (2, 0),
    ops.XOR: (2, 0),
    ops.OR: (2, 0),
    ops.AND: (2, 0),
    ops.FCMP: (2, 0),

    ops.SHL: (1, 1),
    ops.SHR: (1, 1),
    ops.USHR: (1, 1),

    ops.ARRSTORE: (1, 2),
}
# Generic stack codes
genericStackCodes = {
    ops.POP: (1, []),
    ops.POP2: (2, []),
    ops.DUP: (1, [0, 0]),
    ops.DUPX1: (2, [1, 0, 1]),
    ops.DUPX2: (3, [2, 0, 1, 2]),
    ops.DUP2: (2, [0, 1, 0, 1]),
    ops.DUP2X1: (3, [1, 2, 0, 1, 2]),
    ops.DUP2X2: (4, [2, 3, 0, 1, 2, 3]),
    ops.SWAP: (2, [1, 0]),
}

def _getPopAmount(cpool, instr, method):
    op = instr[0]
    if op in _popAmount:
        return _popAmount[op]
    if op in _popAmountVar:
        a, b = _popAmountVar[op]
        cat = 2 if instr[1] in 'JD' else 1
        return a * cat + b
    if op in genericStackCodes:
        return genericStackCodes[op][0]

    if op == ops.MULTINEWARRAY:
        return instr[2]
    elif op == ops.RETURN:
        return len(parseMethodDescriptor(method.descriptor)[1])
    elif op in (ops.PUTFIELD, ops.PUTSTATIC):
        args = len(_loadFieldDesc(cpool, instr[1]))
        if op == ops.PUTFIELD:
            args += 1
        return args
    elif op in _invoke_ops:
        args = len(_loadMethodDesc(cpool, instr[1])[0])
        if op != ops.INVOKESTATIC and op != ops.INVOKEDYNAMIC:
            args += 1
        return args

codes = dict(zip('IFJD', [T_INT, T_FLOAT, T_LONG, T_DOUBLE]))
def _getStackResult(cpool, instr, key):
    op = instr[0]

    if op in (ops.TRUNCATE, ops.LCMP, ops.FCMP, ops.ARRLEN, ops.INSTANCEOF):
        return T_INT
    elif op in (ops.ADD, ops.SUB, ops.MUL, ops.DIV, ops.REM, ops.XOR, ops.AND, ops.OR, ops.SHL, ops.SHR, ops.USHR, ops.NEG):
        return codes[instr[1]]
    elif op == ops.CONSTNULL:
        return T_NULL
    elif op == ops.CONST:
        if instr[1] == 'I':
            return T_INT_CONST(instr[2])
        return codes[instr[1]]
    elif op == ops.ARRLOAD:
        return codes.get(instr[1], T_INT)
    elif op == ops.CONVERT:
        return codes[instr[2]]

    elif op == ops.LDC:
        return {
            'Int': T_INT,
            'Long': T_LONG,
            'Float': T_FLOAT,
            'Double': T_DOUBLE,
            'String': T_OBJECT('java/lang/String'),
            'Class': T_OBJECT('java/lang/Class'),
            'MethodType': T_OBJECT('java/lang/invoke/MethodType'),
            'MethodHandle': T_OBJECT('java/lang/invoke/MethodHandle'),
        }[cpool.getType(instr[1])]

    elif op == ops.JSR:
        return T_ADDRESS(instr[1])

    elif op in (ops.CHECKCAST, ops.NEW, ops.ANEWARRAY, ops.MULTINEWARRAY):
        target = _indexToCFMInfo(cpool, instr[1], 'Class')
        if op == ops.ANEWARRAY:
            return T_ARRAY(target)
        elif op == ops.NEW:
            return T_UNINIT_OBJECT(key)
        return target
    elif op == ops.NEWARRAY:
        return parseFieldDescriptor('[' + instr[1])[0]

    elif op in (ops.GETFIELD, ops.GETSTATIC):
        return _loadFieldDesc(cpool, instr[1])[0]
    elif op in _invoke_ops:
        out = _loadMethodDesc(cpool, instr[1])[1]
        assert 0 <= len(out) <= 2
        return out[0] if out else None

class InstructionNode(object):
    __slots__ = "key code env class_ cpool instruction op visited changed offsetToIndex indexToOffset state out_state jsrTarget next_instruction returnedFrom successors pop_amount stack_push stack_code target_type isThisCtor".split()

    def __init__(self, code, offsetToIndex, indexToOffset, key):
        self.key = key
        assert(self.key is not None) # if it is this will cause problems with origin tracking

        self.code = code
        self.env = code.class_.env
        self.class_ = code.class_
        self.cpool = self.class_.cpool

        self.instruction = code.bytecode[key]
        self.op = self.instruction[0]

        self.visited, self.changed = False, False
        # store for usage calculating JSRs, finding successor instructions and the like
        self.offsetToIndex = offsetToIndex
        self.indexToOffset = indexToOffset

        self.state = None

        # Fields to be assigned later
        self.jsrTarget = None
        self.next_instruction = None
        self.returnedFrom = None
        self.successors = None

        self.pop_amount = -1
        self.stack_push = []
        self.stack_code = None

        # for blockmaker
        self.target_type = None
        self.isThisCtor = False
        self.out_state = None # store out state for JSR/RET instructions

        self._precomputeValues()

    def _removeInterface(self, vt):
        if vt.tag == '.obj' and vt.extra is not None and self.env.isInterface(vt.extra, forceCheck=True):
            return T_ARRAY(OBJECT_INFO, vt.dim)
        return vt

    def _precomputeValues(self):
        # parsed_desc, successors
        off_i = self.offsetToIndex[self.key]
        self.next_instruction = self.indexToOffset[off_i+1] # None if end of code
        op = self.instruction[0]

        self.pop_amount = _getPopAmount(self.cpool, self.instruction, self.code.method)

        # cache these, since they're not state dependent
        result = _getStackResult(self.cpool, self.instruction, self.key)
        # temporary hack
        if op == ops.CHECKCAST:
            result = self._removeInterface(result)

        if result is not None:
            self.stack_push = [result]
            if result in (T_LONG, T_DOUBLE):
                self.stack_push.append(T_INVALID)

        if op in genericStackCodes:
            self.stack_code = genericStackCodes[op][1]

        if op == ops.NEW:
            self.target_type = _indexToCFMInfo(self.cpool, self.instruction[1], 'Class')

        # Now get successors
        next_ = self.next_instruction
        if op in (ops.IF_A, ops.IF_I, ops.IF_ICMP, ops.IF_ACMP):
            self.successors = next_, self.instruction[2]
        elif op in (ops.JSR, ops.GOTO):
            self.successors = self.instruction[1],
        elif op in (ops.RETURN, ops.THROW):
            self.successors = ()
        elif op == ops.RET:
            self.successors = None # calculate it when the node is reached
        elif op == ops.SWITCH:
            opname, default, jumps = self.instruction
            targets = (default,)
            if jumps:
                targets += zip(*jumps)[1]
            self.successors = targets
        else:
            self.successors = next_,

    def _getNewState(self, iNodes):
        state = self.state.copy()
        popped = state.pop(self.pop_amount)

        # Local updates/reading
        op = self.instruction[0]
        if op == ops.LOAD:
            state.push([state.local(self.instruction[2])])
            if self.instruction[1] in 'JD':
                state.push([T_INVALID])
        elif op == ops.STORE:
            for i, val in enumerate(popped):
                state.setLocal(self.instruction[2] + i, val)
        elif op == ops.IINC:
            state.setLocal(self.instruction[1], T_INT) # Make sure to clobber constants
        elif op == ops.JSR:
            state.jsr(self.instruction[1])
        elif op == ops.NEW:
            # This should never happen, but better safe than sorry.
            state.replace(self.stack_push[0], T_INVALID)
        elif op == ops.INVOKEINIT:
            old = popped[0]
            if old.tag == '.new':
                new = _indexToCFMInfo(self.cpool, self.instruction[1], 'Method')
            else: # .init
                new = T_OBJECT(self.class_.name)
                self.isThisCtor = True
            state.replace(old, new)

        # Make sure that push happens after local replacement in case of new/invokeinit
        if self.stack_code is not None:
            state.push(popped[i] for i in self.stack_code)
        elif op == ops.ARRLOAD_OBJ:
            # temporary hack
            result = self._removeInterface(decrementDim(popped[0]))
            state.push([result])

        elif op == ops.NEWARRAY or op == ops.ANEWARRAY:
            arrt = self.stack_push[0]
            size = popped[0].const
            if size is not None:
                arrt = exactArrayFrom(arrt, size)
            state.push([arrt])
        else:
            state.push(self.stack_push)

        if self.op in (ops.RET, ops.JSR):
            state.invalidateNews()
            self.out_state = state # store for later convienence

        assert all(isinstance(vt, fullinfo_t) for vt in state.stack)
        assert all(isinstance(vt, fullinfo_t) for vt in state.locals)
        return state

    def _mergeSingleSuccessor(self, other, newstate, iNodes, isException):
        if self.op == ops.RET and not isException:
            # Get the instruction before other
            off_i = self.offsetToIndex[other.key]
            jsrnode = iNodes[self.indexToOffset[off_i-1]]
            jsrnode.returnedFrom = self.key

            if jsrnode.visited: # if not, skip for later
                newstate = newstate.copy()
                newstate.returnTo(jsrnode.instruction[1], jsrnode.state)
            else:
                return

        if not other.visited:
            other.state = newstate.copy()
            other.visited = other.changed = True
        else:
            changed = other.state.merge(newstate, self.env)
            other.changed = other.changed or changed

    def update(self, iNodes, exceptions):
        assert self.visited
        self.changed = False
        newstate = self._getNewState(iNodes)

        successors = self.successors
        if self.op == ops.JSR and self.returnedFrom is not None:
            iNodes[self.returnedFrom].changed = True

        if successors is None:
            assert self.op == ops.RET
            called = self.state.local(self.instruction[1]).extra
            temp = [n.next_instruction for n in iNodes.values() if (n.op == ops.JSR and n.instruction[1] == called)]
            successors = self.successors = tuple(temp)
            self.jsrTarget = called # store for later use in ssa creation

        # Merge into exception handlers first
        for (start, end, handler, except_info) in exceptions:
            if start <= self.key < end:
                self._mergeSingleSuccessor(handler, self.state.withExcept(except_info), iNodes, True)
                if self.op == ops.INVOKEINIT: # two cases since the ctor may suceed or fail before throwing
                    self._mergeSingleSuccessor(handler, newstate.withExcept(except_info), iNodes, True)

        # Now regular successors
        for k in self.successors:
            self._mergeSingleSuccessor(iNodes[k], newstate, iNodes, False)

    def __str__(self):   # pragma: no cover
        lines = ['{}: {}'.format(self.key, bytecode.printInstruction(self.instruction))]
        if self.visited:
            lines.append('Stack: ' + ', '.join(map(str, self.state.stack)))
            lines.append('Locals: ' + ', '.join(map(str, self.state.locals)))
            if self.state.masks:
                lines.append('Masks:')
                lines += ['\t{}: {}'.format(entry, sorted(cset)) for entry, cset in self.state.masks]
        else:
            lines.append('\tunvisited')
        return '\n'.join(lines) + '\n'

def verifyBytecode(code):
    method, class_ = code.method, code.class_
    args, rval = parseUnboundMethodDescriptor(method.descriptor, class_.name, method.static)
    env = class_.env

    # Object has no superclass to construct, so it doesn't get an uninit this
    if method.isConstructor and class_.name != 'java/lang/Object':
        assert args[0] == T_OBJECT(class_.name)
        args[0] = T_UNINIT_THIS
    assert len(args) <= 255 and len(args) <= code.locals

    offsets = sorted(code.bytecode.keys())
    offset_rmap = {v:i for i,v in enumerate(offsets)}
    offsets.append(None) # Sentinel for end of code
    iNodes = [InstructionNode(code, offset_rmap, offsets, key) for key in offsets[:-1]]
    iNodeLookup = {n.key:n for n in iNodes}

    keys = frozenset(iNodeLookup)
    for raw in code.except_raw:
        if not ((0 <= raw.start < raw.end) and (raw.start in keys) and
            (raw.handler in keys) and (raw.end in keys or raw.end == code.codelen)):

            keylist = sorted(keys) + [code.codelen]
            msg = "Illegal exception handler: {}\nValid offsets are: {}".format(raw, ', '.join(map(str, keylist)))
            raise error_types.VerificationError(msg)

    def makeException(rawdata):
        if rawdata.type_ind:
            typen = class_.cpool.getArgsCheck('Class', rawdata.type_ind)
        else:
            typen = 'java/lang/Throwable'
        return (rawdata.start, rawdata.end, iNodeLookup[rawdata.handler], T_OBJECT(typen))
    exceptions = map(makeException, code.except_raw)

    start = iNodes[0]
    start.state = stateFromInitialArgs(args)
    start.visited, start.changed = True, True

    done = False
    while not done:
        done = True
        for node in iNodes:
            if node.changed:
                node.update(iNodeLookup, exceptions)
                done = False
    return iNodes
