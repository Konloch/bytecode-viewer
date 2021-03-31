from ..ssa import objtypes

from . import ast

# A simple throws declaration inferrer that only considers throw statements within the method
# this is mostly just useful to make sure the ExceptionHandlers test compiles

def _visit_statement(env, stmt):
    if isinstance(stmt, ast.ThrowStatement):
        return stmt.expr.dtype

    result = objtypes.NullTT
    if isinstance(stmt, ast.TryStatement):
        caught_types = []
        for catch, b in stmt.pairs:
            caught_types.extend(objtypes.className(tn.tt) for tn in catch.typename.tnames)
        if objtypes.ThrowableTT not in caught_types:
            temp = _visit_statement(env, stmt.tryb)
            if temp != objtypes.NullTT:
                assert objtypes.dim(temp) == 0
                name = objtypes.className(temp)
                if not any(env.isSubclass(name, caught) for caught in caught_types):
                    result = temp

        statements = zip(*stmt.pairs)[1]
    elif isinstance(stmt, ast.StatementBlock):
        statements = stmt.statements
    else:
        statements = stmt.getScopes()

    for sub in statements:
        if result == objtypes.ThrowableTT:
            break
        result = objtypes.commonSupertype(env, [result, _visit_statement(env, sub)])

    if result != objtypes.NullTT:
        if env.isSubclass(objtypes.className(result), 'java/lang/RuntimeException'):
            return objtypes.NullTT
    return result

def addSingle(env, meth_asts):
    for meth in meth_asts:
        if not meth.body:
            continue
        tt = _visit_statement(env, meth.body)
        assert objtypes.commonSupertype(env, [tt, objtypes.ThrowableTT]) == objtypes.ThrowableTT
        if tt != objtypes.NullTT:
            meth.throws = ast.TypeName(tt)
