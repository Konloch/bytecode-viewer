from .. import opnames
from ..namegen import LabelGen
from ..ssa import objtypes, ssa_jumps, ssa_ops, ssa_types
from ..verifier.descriptors import parseFieldDescriptor, parseMethodDescriptor

from . import ast
from .setree import SEBlockItem, SEIf, SEScope, SESwitch, SETry, SEWhile

# prefixes for name generation
_prefix_map = {objtypes.IntTT:'i', objtypes.LongTT:'j',
            objtypes.FloatTT:'f', objtypes.DoubleTT:'d',
            objtypes.BoolTT:'b', objtypes.StringTT:'s'}

_ssaToTT = {ssa_types.SSA_INT:objtypes.IntTT, ssa_types.SSA_LONG:objtypes.LongTT,
            ssa_types.SSA_FLOAT:objtypes.FloatTT, ssa_types.SSA_DOUBLE:objtypes.DoubleTT}
class VarInfo(object):
    def __init__(self, method, blocks, namegen):
        self.env = method.class_.env
        self.labelgen = LabelGen().next

        returnTypes = parseMethodDescriptor(method.descriptor, unsynthesize=False)[-1]
        self.return_tt = objtypes.verifierToSynthetic(returnTypes[0]) if returnTypes else None
        self.clsname = method.class_.name
        self._namegen = namegen

        self._uninit_vars = {}
        self._vars = {}
        self._tts = {}
        for block in blocks:
            for var, uc in block.unaryConstraints.items():
                if var.type == ssa_types.SSA_OBJECT:
                    tt = uc.getSingleTType() # temp hack
                    if uc.types.isBoolOrByteArray():
                        tt = objtypes.TypeTT(objtypes.BExpr, objtypes.dim(tt)+1)
                        # assert (objtypes.BoolTT[0], tt[1]) in uc.types.exact
                else:
                    tt = _ssaToTT[var.type]
                self._tts[var] = tt

    def _nameCallback(self, expr):
        prefix = _prefix_map.get(expr.dtype, 'a')
        return self._namegen.getPrefix(prefix)

    def _newVar(self, var, num, isCast):
        tt = self._tts[var]
        if var.const is not None and not isCast:
            return ast.Literal(tt, var.const)

        if var.name:
            # important to not add num when it is 0, since we currently
            # use var names to force 'this'
            temp = '{}_{}'.format(var.name, num) if num else var.name
            if isCast:
                temp += 'c'
            namefunc = lambda expr:temp
        else:
            namefunc = self._nameCallback

        result = ast.Local(tt, namefunc)
        # merge all variables of uninitialized type to simplify fixObjectCreations in javamethod.py
        if var.uninit_orig_num is not None and not isCast:
            result = self._uninit_vars.setdefault(var.uninit_orig_num, result)
        return result

    def var(self, node, var, isCast=False):
        key = node, var, isCast
        try:
            return self._vars[key]
        except KeyError:
            new = self._newVar(key[1], key[0].num, key[2])
            self._vars[key] = new
            return new

    def customVar(self, tt, prefix): # for use with ignored exceptions
        namefunc = lambda expr: self._namegen.getPrefix(prefix)
        return ast.Local(tt, namefunc)

#########################################################################################
_math_types = (ssa_ops.IAdd, ssa_ops.IDiv, ssa_ops.IMul, ssa_ops.IRem, ssa_ops.ISub)
_math_types += (ssa_ops.IAnd, ssa_ops.IOr, ssa_ops.IShl, ssa_ops.IShr, ssa_ops.IUshr, ssa_ops.IXor)
_math_types += (ssa_ops.FAdd, ssa_ops.FDiv, ssa_ops.FMul, ssa_ops.FRem, ssa_ops.FSub)
_math_symbols = dict(zip(_math_types, '+ / * % - & | << >> >>> ^ + / * % -'.split()))
def _convertJExpr(op, getExpr, clsname):
    params = [getExpr(var) for var in op.params]
    assert None not in params
    expr = None

    # Have to do this one seperately since it isn't an expression statement
    if isinstance(op, ssa_ops.Throw):
        return ast.ThrowStatement(params[0])

    if isinstance(op, _math_types):
        opdict = _math_symbols
        expr = ast.BinaryInfix(opdict[type(op)], params)
    elif isinstance(op, ssa_ops.ArrLength):
        expr = ast.FieldAccess(params[0], 'length', objtypes.IntTT)
    elif isinstance(op, ssa_ops.ArrLoad):
        expr = ast.ArrayAccess(*params)
    elif isinstance(op, ssa_ops.ArrStore):
        expr = ast.ArrayAccess(params[0], params[1])
        expr = ast.Assignment(expr, params[2])
    elif isinstance(op, ssa_ops.CheckCast):
        expr = ast.Cast(ast.TypeName(op.target_tt), params[0])
    elif isinstance(op, ssa_ops.Convert):
        expr = ast.makeCastExpr(_ssaToTT[op.target], params[0])
    elif isinstance(op, (ssa_ops.FCmp, ssa_ops.ICmp)):
        boolt = objtypes.BoolTT
        cn1, c0, c1 = ast.Literal.N_ONE, ast.Literal.ZERO, ast.Literal.ONE

        ascend = isinstance(op, ssa_ops.ICmp) or op.NaN_val == 1
        if ascend:
            expr = ast.Ternary(ast.BinaryInfix('<',params,boolt), cn1, ast.Ternary(ast.BinaryInfix('==',params,boolt), c0, c1))
        else:
            assert op.NaN_val == -1
            expr = ast.Ternary(ast.BinaryInfix('>',params,boolt), c1, ast.Ternary(ast.BinaryInfix('==',params,boolt), c0, cn1))
    elif isinstance(op, ssa_ops.FieldAccess):
        dtype = objtypes.verifierToSynthetic(parseFieldDescriptor(op.desc, unsynthesize=False)[0])

        if op.instruction[0] in (opnames.GETSTATIC, opnames.PUTSTATIC):
            printLeft = (op.target != clsname) # Don't print classname if it is a static field in current class
            tt = objtypes.TypeTT(op.target, 0) # Doesn't handle arrays, but they don't have any fields anyway
            expr = ast.FieldAccess(ast.TypeName(tt), op.name, dtype, op, printLeft=printLeft)
        else:
            expr = ast.FieldAccess(params[0], op.name, dtype, op)

        if op.instruction[0] in (opnames.PUTFIELD, opnames.PUTSTATIC):
            expr = ast.Assignment(expr, params[-1])

    elif isinstance(op, ssa_ops.FNeg):
        expr = ast.UnaryPrefix('-', params[0])
    elif isinstance(op, ssa_ops.InstanceOf):
        args = [params[0], ast.TypeName(op.target_tt)]
        expr = ast.BinaryInfix('instanceof', args, dtype=objtypes.BoolTT)
    elif isinstance(op, ssa_ops.Invoke):
        vtypes, rettypes = parseMethodDescriptor(op.desc, unsynthesize=False)
        tt_types = objtypes.verifierToSynthetic_seq(vtypes)
        ret_type = objtypes.verifierToSynthetic(rettypes[0]) if rettypes else None
        target_tt = op.target_tt

        if objtypes.dim(target_tt) and op.name == "clone": # In Java, T[].clone returns T[] rather than Object
            ret_type = target_tt

        if op.instruction[0] == opnames.INVOKEINIT and op.isThisCtor:
            name = 'this' if (op.target == clsname) else 'super'
            expr = ast.MethodInvocation(None, name, tt_types, params[1:], op, ret_type)
        elif op.instruction[0] == opnames.INVOKESTATIC: # TODO - fix this for special super calls
            expr = ast.MethodInvocation(ast.TypeName(target_tt), op.name, [None]+tt_types, params, op, ret_type)
        else:
            expr = ast.MethodInvocation(params[0], op.name, [target_tt]+tt_types, params[1:], op, ret_type)
    elif isinstance(op, ssa_ops.InvokeDynamic):
        vtypes, rettypes = parseMethodDescriptor(op.desc, unsynthesize=False)
        ret_type = objtypes.verifierToSynthetic(rettypes[0]) if rettypes else None
        fmt = '/*invokedynamic*/'
        if ret_type is not None:
            fmt += '{{{}}}'.format(len(params))
            params.append(ast.dummyLiteral(ret_type))
        expr = ast.Dummy(fmt, params, dtype=ret_type)
    elif isinstance(op, ssa_ops.Monitor):
        fmt = '/*monexit({})*/' if op.exit else '/*monenter({})*/'
        expr = ast.Dummy(fmt, params)
    elif isinstance(op, ssa_ops.MultiNewArray):
        expr = ast.ArrayCreation(op.tt, *params)
    elif isinstance(op, ssa_ops.New):
        expr = ast.Dummy('/*<unmerged new> {}*/', [ast.TypeName(op.tt)], isNew=True)
    elif isinstance(op, ssa_ops.NewArray):
        expr = ast.ArrayCreation(op.tt, params[0])
    elif isinstance(op, ssa_ops.Truncate):
        tt = {(True,16): objtypes.ShortTT, (False,16): objtypes.CharTT, (True,8): objtypes.ByteTT}[op.signed, op.width]
        expr = ast.Cast(ast.TypeName(tt), params[0])
    if op.rval is not None and expr:
        expr = ast.Assignment(getExpr(op.rval), expr)

    if expr is None: # Temporary hack
        if isinstance(op, (ssa_ops.TryReturn, ssa_ops.ExceptionPhi, ssa_ops.MagicThrow)):
            return None # Don't print out anything
    return ast.ExpressionStatement(expr)

#########################################################################################
def _createASTBlock(info, endk, node):
    getExpr = lambda var: info.var(node, var)
    op2expr = lambda op: _convertJExpr(op, getExpr, info.clsname)

    block = node.block
    if block is not None:
        split_ind = 0
        if isinstance(block.jump, ssa_jumps.OnException):
            # find index of first throwing instruction, so we can insert eassigns before it later
            assert isinstance(block.lines[-1], ssa_ops.ExceptionPhi)
            split_ind = block.lines.index(block.lines[-1].params[0].origin)

        lines_before = filter(None, map(op2expr, block.lines[:split_ind]))
        lines_after = filter(None, map(op2expr, block.lines[split_ind:]))
    else:
        lines_before, lines_after = [], []

    # Kind of hackish: If the block ends in a cast and hence it is not known to always
    # succeed, assign the results of the cast rather than passing through the variable
    # unchanged. The cast will actually be second to last in block.lines due to the ephi
    outreplace = {}
    if block and len(block.lines) >= 2:
        temp_op = block.lines[-2]
        if lines_after and isinstance(temp_op, ssa_ops.CheckCast):
            assert isinstance(lines_after[-1].expr, ast.Cast)
            var = temp_op.params[0]
            cexpr = lines_after[-1].expr
            lhs = info.var(node, var, True)
            assert lhs != cexpr.params[1]
            lines_after[-1].expr = ast.Assignment(lhs, cexpr)
            nvar = outreplace[var] = lines_after[-1].expr.params[0]
            nvar.dtype = cexpr.dtype

    eassigns = []
    nassigns = []
    for n2 in node.successors:
        assert (n2 in node.outvars) != (n2 in node.eassigns)
        if n2 in node.eassigns:
            for outv, inv in zip(node.eassigns[n2], n2.invars):
                if outv is None: # this is how we mark the thrown exception, which
                    # obviously doesn't get an explicit assignment statement
                    continue
                expr = ast.Assignment(info.var(n2, inv), info.var(node, outv))
                if expr.params[0] != expr.params[1]:
                    eassigns.append(ast.ExpressionStatement(expr))
        else:
            for outv, inv in zip(node.outvars[n2], n2.invars):
                right = outreplace.get(outv, info.var(node, outv))
                expr = ast.Assignment(info.var(n2, inv), right)
                if expr.params[0] != expr.params[1]:
                    nassigns.append(ast.ExpressionStatement(expr))

    # Need to put exception assignments before first throwing statement
    # While normal assignments must come last as they may depend on it
    statements = lines_before + eassigns + lines_after + nassigns

    norm_successors = node.normalSuccessors()
    jump = None if block is None else block.jump
    if isinstance(jump, (ssa_jumps.Rethrow, ssa_jumps.Return)):
        assert not norm_successors
        assert not node.eassigns and not node.outvars
        if isinstance(jump, ssa_jumps.Rethrow):
            param = info.var(node, jump.params[-1])
            statements.append(ast.ThrowStatement(param))
        else:
            if len(jump.params) > 0:
                param = info.var(node, jump.params[0])
                statements.append(ast.ReturnStatement(param, info.return_tt))
            else:
                statements.append(ast.ReturnStatement())
        breakKey, jumpKey = endk, None
    elif len(norm_successors) == 0:
        assert isinstance(jump, ssa_jumps.OnException)
        breakKey, jumpKey = endk, None
    elif len(norm_successors) == 1: # normal successors
        breakKey, jumpKey = endk, norm_successors[0]._key
    else: # case of if and switch jumps handled in parent scope
        assert len(norm_successors) > 1
        breakKey, jumpKey = endk, endk

    new = ast.StatementBlock(info.labelgen, node._key, breakKey, statements, jumpKey)
    assert None not in statements
    return new

_cmp_strs = dict(zip(('eq','ne','lt','ge','gt','le'), "== != < >= > <=".split()))
def _createASTSub(info, current, ftitem, forceUnlabled=False):
    begink = current.entryBlock._key
    endk = ftitem.entryBlock._key if ftitem is not None else None

    if isinstance(current, SEBlockItem):
        return _createASTBlock(info, endk, current.node)
    elif isinstance(current, SEScope):
        ftitems = current.items[1:] + [ftitem]
        parts = [_createASTSub(info, item, newft) for item, newft in zip(current.items, ftitems)]
        return ast.StatementBlock(info.labelgen, begink, endk, parts, endk, labelable=(not forceUnlabled))
    elif isinstance(current, SEWhile):
        parts = [_createASTSub(info, scope, current, True) for scope in current.getScopes()]
        return ast.WhileStatement(info.labelgen, begink, endk, tuple(parts))
    elif isinstance(current, SETry):
        assert len(current.getScopes()) == 2
        parts = [_createASTSub(info, scope, ftitem, True) for scope in current.getScopes()]
        catchnode = current.getScopes()[-1].entryBlock
        declt = ast.CatchTypeNames(info.env, current.toptts)

        if current.catchvar is None: # exception is ignored and hence not referred to by the graph, so we need to make our own
            catchvar = info.customVar(declt.dtype, 'ignoredException')
        else:
            catchvar = info.var(catchnode, current.catchvar)
        decl = ast.VariableDeclarator(declt, catchvar)
        pairs = [(decl, parts[1])]
        return ast.TryStatement(info.labelgen, begink, endk, parts[0], pairs)

    # Create a fake key to represent the beginning of the conditional statement itself
    # doesn't matter what it is as long as it's unique
    midk = begink + (-1,)
    node = current.head.node
    jump = node.block.jump

    if isinstance(current, SEIf):
        parts = [_createASTSub(info, scope, ftitem, True) for scope in current.getScopes()]
        cmp_str = _cmp_strs[jump.cmp]
        exprs = [info.var(node, var) for var in jump.params]
        ifexpr = ast.BinaryInfix(cmp_str, exprs, objtypes.BoolTT)
        new = ast.IfStatement(info.labelgen, midk, endk, ifexpr, tuple(parts))

    elif isinstance(current, SESwitch):
        ftitems = current.ordered[1:] + [ftitem]
        parts = [_createASTSub(info, item, newft, True) for item, newft in zip(current.ordered, ftitems)]
        for part in parts:
            part.breakKey = endk # createSub will assume break should be ft, which isn't the case with switch statements

        expr = info.var(node, jump.params[0])
        pairs = zip(current.ordered_keysets, parts)
        new = ast.SwitchStatement(info.labelgen, midk, endk, expr, pairs)

    # bundle head and if together so we can return as single statement
    headscope = _createASTBlock(info, midk, node)
    assert headscope.jumpKey is midk
    return ast.StatementBlock(info.labelgen, begink, endk, [headscope, new], endk)

def createAST(method, ssagraph, seroot, namegen):
    info = VarInfo(method, ssagraph.blocks, namegen)
    astroot = _createASTSub(info, seroot, None)
    return astroot, info
