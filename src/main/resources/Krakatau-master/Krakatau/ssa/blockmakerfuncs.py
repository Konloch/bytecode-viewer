from .. import opnames as vops
from ..verifier.descriptors import parseFieldDescriptor, parseMethodDescriptor

from . import objtypes, ssa_jumps, ssa_ops, subproc
from .ssa_types import SSA_DOUBLE, SSA_FLOAT, SSA_INT, SSA_LONG, SSA_OBJECT, slots_t


_charToSSAType = {'D':SSA_DOUBLE, 'F':SSA_FLOAT, 'I':SSA_INT, 'J':SSA_LONG,
                'B':SSA_INT, 'C':SSA_INT, 'S':SSA_INT}
def getCategory(c): return 2 if c in 'JD' else 1

class ResultDict(object):
    def __init__(self, line=None, jump=None, newstack=None, newlocals=None):
        self.line = line
        self.jump = jump
        self.newstack = newstack
        self.newlocals = newlocals

##############################################################################
def makeConstVar(parent, type_, val):
    var = parent.makeVariable(type_)
    var.const = val
    return var

def parseArrOrClassName(desc):
    # Accept either a class or array descriptor or a raw class name.
    if desc.startswith('[') or desc.endswith(';'):
        vtypes = parseFieldDescriptor(desc, unsynthesize=False)
        tt = objtypes.verifierToSynthetic(vtypes[0])
    else:
        tt = objtypes.TypeTT(desc, 0)
    return tt

def _floatOrIntMath(fop, iop):
    def math1(maker, input_, iNode):
        cat = getCategory(iNode.instruction[1])
        isfloat = (iNode.instruction[1] in 'DF')
        op = fop if isfloat else iop

        args = input_.stack[-cat*2::cat]
        line = op(maker.parent, args)

        newstack = input_.stack[:-2*cat] + [line.rval] + [None]*(cat-1)
        return ResultDict(line=line, newstack=newstack)
    return math1

def _intMath(op, isShift):
    def math2(maker, input_, iNode):
        cat = getCategory(iNode.instruction[1])
        # some ops (i.e. shifts) always take int as second argument
        size = cat+1 if isShift else cat+cat
        args = input_.stack[-size::cat]
        line = op(maker.parent, args)
        newstack = input_.stack[:-size] + [line.rval] + [None]*(cat-1)
        return ResultDict(line=line, newstack=newstack)
    return math2
##############################################################################

def _anewarray(maker, input_, iNode):
    name = maker.parent.getConstPoolArgs(iNode.instruction[1])[0]
    tt = parseArrOrClassName(name)
    line = ssa_ops.NewArray(maker.parent, input_.stack[-1], tt)
    newstack = input_.stack[:-1] + [line.rval]
    return ResultDict(line=line, newstack=newstack)

def _arrlen(maker, input_, iNode):
    line = ssa_ops.ArrLength(maker.parent, input_.stack[-1:])
    newstack = input_.stack[:-1] + [line.rval]
    return ResultDict(line=line, newstack=newstack)

def _arrload(maker, input_, iNode):
    type_ = _charToSSAType[iNode.instruction[1]]
    cat = getCategory(iNode.instruction[1])

    line = ssa_ops.ArrLoad(maker.parent, input_.stack[-2:], type_)
    newstack = input_.stack[:-2] + [line.rval] + [None]*(cat-1)
    return ResultDict(line=line, newstack=newstack)

def _arrload_obj(maker, input_, iNode):
    line = ssa_ops.ArrLoad(maker.parent, input_.stack[-2:], SSA_OBJECT)
    newstack = input_.stack[:-2] + [line.rval]
    return ResultDict(line=line, newstack=newstack)

def _arrstore(maker, input_, iNode):
    if getCategory(iNode.instruction[1]) > 1:
        newstack, args = input_.stack[:-4], input_.stack[-4:-1]
        arr_vt, ind_vt = iNode.state.stack[-4:-2]
    else:
        newstack, args = input_.stack[:-3], input_.stack[-3:]
        arr_vt, ind_vt = iNode.state.stack[-3:-1]
    line = ssa_ops.ArrStore(maker.parent, args)

    # Check if we can prune the exception early because the
    # array size and index are known constants
    if arr_vt.const is not None and ind_vt.const is not None:
        if 0 <= ind_vt.const < arr_vt.const:
            line.outException = None
    return ResultDict(line=line, newstack=newstack)

def _arrstore_obj(maker, input_, iNode):
    line = ssa_ops.ArrStore(maker.parent, input_.stack[-3:])
    newstack = input_.stack[:-3]
    return ResultDict(line=line, newstack=newstack)

def _checkcast(maker, input_, iNode):
    index = iNode.instruction[1]
    desc = maker.parent.getConstPoolArgs(index)[0]
    tt = parseArrOrClassName(desc)
    line = ssa_ops.CheckCast(maker.parent, tt, input_.stack[-1:])
    return ResultDict(line=line)

def _const(maker, input_, iNode):
    ctype, val = iNode.instruction[1:]
    cat = getCategory(ctype)
    type_ = _charToSSAType[ctype]
    var = makeConstVar(maker.parent, type_, val)
    newstack = input_.stack + [var] + [None]*(cat-1)
    return ResultDict(newstack=newstack)

def _constnull(maker, input_, iNode):
    var = makeConstVar(maker.parent, SSA_OBJECT, 'null')
    var.decltype = objtypes.NullTT
    newstack = input_.stack + [var]
    return ResultDict(newstack=newstack)

def _convert(maker, input_, iNode):
    src_c, dest_c = iNode.instruction[1:]
    src_cat, dest_cat = getCategory(src_c), getCategory(dest_c)

    stack, arg =  input_.stack[:-src_cat], input_.stack[-src_cat]
    line = ssa_ops.Convert(maker.parent, arg, _charToSSAType[src_c], _charToSSAType[dest_c])

    newstack = stack + [line.rval] + [None]*(dest_cat-1)
    return ResultDict(line=line, newstack=newstack)

def _fcmp(maker, input_, iNode):
    op, c, NaN_val = iNode.instruction
    cat = getCategory(c)

    args = input_.stack[-cat*2::cat]
    line = ssa_ops.FCmp(maker.parent, args, NaN_val)
    newstack = input_.stack[:-cat*2] + [line.rval]
    return ResultDict(line=line, newstack=newstack)

def _field_access(maker, input_, iNode):
    index = iNode.instruction[1]
    target, name, desc = maker.parent.getConstPoolArgs(index)
    cat = len(parseFieldDescriptor(desc))

    argcnt = cat if 'put' in iNode.instruction[0] else 0
    if not 'static' in iNode.instruction[0]:
        argcnt += 1
    splitInd = len(input_.stack) - argcnt

    args = [x for x in input_.stack[splitInd:] if x is not None]
    line = ssa_ops.FieldAccess(maker.parent, iNode.instruction, (target, name, desc), args=args)
    newstack = input_.stack[:splitInd] + line.returned
    return ResultDict(line=line, newstack=newstack)

def _goto(maker, input_, iNode):
    jump = ssa_jumps.Goto(maker.parent, maker.blockd[iNode.successors[0]])
    return ResultDict(jump=jump)

def _if_a(maker, input_, iNode):
    null = makeConstVar(maker.parent, SSA_OBJECT, 'null')
    null.decltype = objtypes.NullTT
    jump = ssa_jumps.If(maker.parent, iNode.instruction[1], map(maker.blockd.get, iNode.successors), (input_.stack[-1], null))
    newstack = input_.stack[:-1]
    return ResultDict(jump=jump, newstack=newstack)

def _if_i(maker, input_, iNode):
    zero = makeConstVar(maker.parent, SSA_INT, 0)
    jump = ssa_jumps.If(maker.parent, iNode.instruction[1], map(maker.blockd.get, iNode.successors), (input_.stack[-1], zero))
    newstack = input_.stack[:-1]
    return ResultDict(jump=jump, newstack=newstack)

def _if_cmp(maker, input_, iNode):
    jump = ssa_jumps.If(maker.parent, iNode.instruction[1], map(maker.blockd.get, iNode.successors), input_.stack[-2:])
    newstack = input_.stack[:-2]
    return ResultDict(jump=jump, newstack=newstack)

def _iinc(maker, input_, iNode):
    _, index, amount = iNode.instruction

    oldval = input_.locals[index]
    constval = makeConstVar(maker.parent, SSA_INT, amount)
    line = ssa_ops.IAdd(maker.parent, (oldval, constval))

    newlocals = input_.locals.copy()
    newlocals[index] = line.rval
    return ResultDict(line=line, newlocals=newlocals)

def _instanceof(maker, input_, iNode):
    index = iNode.instruction[1]
    desc = maker.parent.getConstPoolArgs(index)[0]
    tt = parseArrOrClassName(desc)
    line = ssa_ops.InstanceOf(maker.parent, tt, input_.stack[-1:])
    newstack = input_.stack[:-1] + [line.rval]
    return ResultDict(line=line, newstack=newstack)

def _invoke(maker, input_, iNode):
    index = iNode.instruction[1]
    target, name, desc = maker.parent.getConstPoolArgs(index)
    target_tt = parseArrOrClassName(target)

    argcnt = len(parseMethodDescriptor(desc)[0])
    if not 'static' in iNode.instruction[0]:
        argcnt += 1
    splitInd = len(input_.stack) - argcnt

    # If we are an initializer, store a copy of the uninitialized verifier type so the Java decompiler can patch things up later
    isThisCtor = iNode.isThisCtor if iNode.op == vops.INVOKEINIT else False

    args = [x for x in input_.stack[splitInd:] if x is not None]
    line = ssa_ops.Invoke(maker.parent, iNode.instruction, (target, name, desc),
        args=args, isThisCtor=isThisCtor, target_tt=target_tt)
    newstack = input_.stack[:splitInd] + line.returned
    return ResultDict(line=line, newstack=newstack)

def _invoke_dynamic(maker, input_, iNode):
    index = iNode.instruction[1]
    desc = maker.parent.getConstPoolArgs(index)[2]
    argcnt = len(parseMethodDescriptor(desc)[0])
    splitInd = len(input_.stack) - argcnt

    args = [x for x in input_.stack[splitInd:] if x is not None]
    line = ssa_ops.InvokeDynamic(maker.parent, desc, args)
    newstack = input_.stack[:splitInd] + line.returned
    return ResultDict(line=line, newstack=newstack)

def _jsr(maker, input_, iNode):
    newstack = input_.stack + [None]
    if iNode.returnedFrom is None:
        jump = ssa_jumps.Goto(maker.parent, maker.blockd[iNode.successors[0]])
        return ResultDict(newstack=newstack, jump=jump)

    # create output variables from callop to represent vars received from ret.
    # We can use {} for initMap since there will never be unintialized types here
    retnode = maker.iNodeD[iNode.returnedFrom]
    stack = [maker.parent.makeVarFromVtype(vt, {}) for vt in retnode.out_state.stack]
    newlocals = dict(enumerate(maker.parent.makeVarFromVtype(vt, {}) for vt in retnode.out_state.locals))
    newlocals = maker.pruneUnused(retnode.key, newlocals)
    out_slots = slots_t(locals=newlocals, stack=stack)

    # Simply store the data for now and fix things up once all the blocks are created
    jump = subproc.ProcCallOp(maker.blockd[iNode.successors[0]], maker.blockd[iNode.next_instruction], input_, out_slots)
    return ResultDict(jump=jump, newstack=newstack)

def _lcmp(maker, input_, iNode):
    args = input_.stack[-4::2]
    line = ssa_ops.ICmp(maker.parent, args)
    newstack = input_.stack[:-4] + [line.rval]
    return ResultDict(line=line, newstack=newstack)

def _ldc(maker, input_, iNode):
    index, cat = iNode.instruction[1:]
    entry_type = maker.parent.getConstPoolType(index)
    args = maker.parent.getConstPoolArgs(index)

    var = None
    if entry_type == 'String':
        var = makeConstVar(maker.parent, SSA_OBJECT, args[0])
        var.decltype = objtypes.StringTT
    elif entry_type == 'Int':
        var = makeConstVar(maker.parent, SSA_INT, args[0])
    elif entry_type == 'Long':
        var = makeConstVar(maker.parent, SSA_LONG, args[0])
    elif entry_type == 'Float':
        var = makeConstVar(maker.parent, SSA_FLOAT, args[0])
    elif entry_type == 'Double':
        var = makeConstVar(maker.parent, SSA_DOUBLE, args[0])
    elif entry_type == 'Class':
        var = makeConstVar(maker.parent, SSA_OBJECT, parseArrOrClassName(args[0]))
        var.decltype = objtypes.ClassTT
    # Todo - handle MethodTypes and MethodHandles?

    assert var
    newstack = input_.stack + [var] + [None]*(cat-1)
    return ResultDict(newstack=newstack)

def _load(maker, input_, iNode):
    cat = getCategory(iNode.instruction[1])
    index = iNode.instruction[2]
    newstack = input_.stack + [input_.locals[index]] + [None]*(cat-1)
    return ResultDict(newstack=newstack)

def _monitor(maker, input_, iNode):
    isExit = 'exit' in iNode.instruction[0]
    line = ssa_ops.Monitor(maker.parent, input_.stack[-1:], isExit)
    newstack = input_.stack[:-1]
    return ResultDict(line=line, newstack=newstack)

def _multinewarray(maker, input_, iNode):
    op, index, dim = iNode.instruction
    name = maker.parent.getConstPoolArgs(index)[0]
    tt = parseArrOrClassName(name)
    assert objtypes.dim(tt) >= dim

    line = ssa_ops.MultiNewArray(maker.parent, input_.stack[-dim:], tt)
    newstack = input_.stack[:-dim] + [line.rval]
    return ResultDict(line=line, newstack=newstack)

def _neg(maker, input_, iNode):
    cat = getCategory(iNode.instruction[1])
    arg = input_.stack[-cat:][0]

    if (iNode.instruction[1] in 'DF'):
        line = ssa_ops.FNeg(maker.parent, [arg])
    else: # for integers, we can just write -x as 0 - x
        zero = makeConstVar(maker.parent, arg.type, 0)
        line = ssa_ops.ISub(maker.parent, [zero,arg])

    newstack = input_.stack[:-cat] + [line.rval] + [None]*(cat-1)
    return ResultDict(line=line, newstack=newstack)

def _new(maker, input_, iNode):
    index = iNode.instruction[1]
    classname = maker.parent.getConstPoolArgs(index)[0]
    if classname.endswith(';'):
        classname = classname[1:-1]

    line = ssa_ops.New(maker.parent, classname, iNode.key)
    newstack = input_.stack + [line.rval]
    return ResultDict(line=line, newstack=newstack)

def _newarray(maker, input_, iNode):
    vtypes = parseFieldDescriptor(iNode.instruction[1], unsynthesize=False)
    tt = objtypes.verifierToSynthetic(vtypes[0])

    line = ssa_ops.NewArray(maker.parent, input_.stack[-1], tt)
    newstack = input_.stack[:-1] + [line.rval]
    return ResultDict(line=line, newstack=newstack)

def _nop(maker, input_, iNode):
    return ResultDict()

def _ret(maker, input_, iNode):
    jump = subproc.DummyRet(input_, maker.blockd[iNode.jsrTarget])
    return ResultDict(jump=jump)

def _return(maker, input_, iNode):
    # Our special return block expects only the return values on the stack
    rtype = iNode.instruction[1]
    if rtype is None:
        newstack = []
    else:
        newstack = input_.stack[-getCategory(rtype):]

    # TODO: enable once structuring is smarter
    # if not maker.hasmonenter:
    #     jump = ssa_jumps.Goto(maker.parent, maker.returnBlock)
    #     return ResultDict(jump=jump, newstack=newstack)

    line = ssa_ops.TryReturn(maker.parent)
    return ResultDict(line=line, newstack=newstack)

def _store(maker, input_, iNode):
    cat = getCategory(iNode.instruction[1])
    index = iNode.instruction[2]

    newlocals = input_.locals.copy()
    newlocals[index] = input_.stack[-cat]
    newstack = input_.stack[:-cat]
    return ResultDict(newstack=newstack, newlocals=newlocals)

def _switch(maker, input_, iNode):
    default, raw_table = iNode.instruction[1:3]
    table = [(k, maker.blockd[v]) for k,v in raw_table]
    jump = ssa_jumps.Switch(maker.parent, maker.blockd[default], table, input_.stack[-1:])
    newstack = input_.stack[:-1]
    return ResultDict(jump=jump, newstack=newstack)

def _throw(maker, input_, iNode):
    line = ssa_ops.Throw(maker.parent, input_.stack[-1:])
    return ResultDict(line=line, newstack=[])

def _truncate(maker, input_, iNode):
    dest_c = iNode.instruction[1]
    signed, width = {'B':(True, 8), 'C':(False, 16), 'S':(True, 16)}[dest_c]

    line = ssa_ops.Truncate(maker.parent, input_.stack[-1], signed=signed, width=width)
    newstack = input_.stack[:-1] + [line.rval]
    return ResultDict(line=line, newstack=newstack)

def genericStackUpdate(maker, input_, iNode):
    n = iNode.pop_amount
    stack = input_.stack
    stack, popped = stack[:-n], stack[-n:]

    for i in iNode.stack_code:
        stack.append(popped[i])
    return ResultDict(newstack=stack)

instructionHandlers = {
                        vops.ADD: _floatOrIntMath(ssa_ops.FAdd, ssa_ops.IAdd),
                        vops.AND: _intMath(ssa_ops.IAnd, isShift=False),
                        vops.ANEWARRAY: _anewarray,
                        vops.ARRLEN: _arrlen,
                        vops.ARRLOAD: _arrload,
                        vops.ARRLOAD_OBJ: _arrload_obj,
                        vops.ARRSTORE: _arrstore,
                        vops.ARRSTORE_OBJ: _arrstore_obj,
                        vops.CHECKCAST: _checkcast,
                        vops.CONST: _const,
                        vops.CONSTNULL: _constnull,
                        vops.CONVERT: _convert,
                        vops.DIV: _floatOrIntMath(ssa_ops.FDiv, ssa_ops.IDiv),
                        vops.FCMP: _fcmp,
                        vops.GETSTATIC: _field_access,
                        vops.GETFIELD: _field_access,
                        vops.GOTO: _goto,
                        vops.IF_A: _if_a,
                        vops.IF_ACMP: _if_cmp, # cmp works on ints or objs
                        vops.IF_I: _if_i,
                        vops.IF_ICMP: _if_cmp,
                        vops.IINC: _iinc,
                        vops.INSTANCEOF: _instanceof,
                        vops.INVOKEINIT: _invoke,
                        vops.INVOKEINTERFACE: _invoke,
                        vops.INVOKESPECIAL: _invoke,
                        vops.INVOKESTATIC: _invoke,
                        vops.INVOKEVIRTUAL: _invoke,
                        vops.INVOKEDYNAMIC: _invoke_dynamic,
                        vops.JSR: _jsr,
                        vops.LCMP: _lcmp,
                        vops.LDC: _ldc,
                        vops.LOAD: _load,
                        vops.MONENTER: _monitor,
                        vops.MONEXIT: _monitor,
                        vops.MULTINEWARRAY: _multinewarray,
                        vops.MUL: _floatOrIntMath(ssa_ops.FMul, ssa_ops.IMul),
                        vops.NEG: _neg,
                        vops.NEW: _new,
                        vops.NEWARRAY: _newarray,
                        vops.NOP: _nop,
                        vops.OR: _intMath(ssa_ops.IOr, isShift=False),
                        vops.PUTSTATIC: _field_access,
                        vops.PUTFIELD: _field_access,
                        vops.REM: _floatOrIntMath(ssa_ops.FRem, ssa_ops.IRem),
                        vops.RET: _ret,
                        vops.RETURN: _return,
                        vops.SHL: _intMath(ssa_ops.IShl, isShift=True),
                        vops.SHR: _intMath(ssa_ops.IShr, isShift=True),
                        vops.STORE: _store,
                        vops.SUB: _floatOrIntMath(ssa_ops.FSub, ssa_ops.ISub),
                        vops.SWITCH: _switch,
                        vops.THROW: _throw,
                        vops.TRUNCATE: _truncate,
                        vops.USHR: _intMath(ssa_ops.IUshr, isShift=True),
                        vops.XOR: _intMath(ssa_ops.IXor, isShift=False),

                        vops.SWAP: genericStackUpdate,
                        vops.POP: genericStackUpdate,
                        vops.POP2: genericStackUpdate,
                        vops.DUP: genericStackUpdate,
                        vops.DUPX1: genericStackUpdate,
                        vops.DUPX2: genericStackUpdate,
                        vops.DUP2: genericStackUpdate,
                        vops.DUP2X1: genericStackUpdate,
                        vops.DUP2X2: genericStackUpdate,
                        }
