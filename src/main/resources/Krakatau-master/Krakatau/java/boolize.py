import collections

from .. import graph_util
from ..ssa import objtypes
from ..ssa.objtypes import BExpr, BoolTT, ByteTT, CharTT, IntTT, ShortTT

from . import ast

# Class union-find data structure except that we don't bother with weighting trees and singletons are implicit
# Also, booleans are forced to be seperate roots
FORCED_ROOTS = True, False
class UnionFind(object):
    def __init__(self):
        self.d = {}

    def find(self, x):
        if x not in self.d:
            return x
        path = [x]
        while path[-1] in self.d:
            path.append(self.d[path[-1]])
        root = path.pop()
        for y in path:
            self.d[y] = root
        return root

    def union(self, x, x2):
        if x is None or x2 is None:
            return
        root1, root2 = self.find(x), self.find(x2)
        if root2 in FORCED_ROOTS:
            root1, root2 = root2, root1
        if root1 != root2 and root2 not in FORCED_ROOTS:
            self.d[root2] = root1

##############################################################
def visitStatementTree(scope, callback, catchcb=None):
    for item in scope.statements:
        for sub in item.getScopes():
            visitStatementTree(sub, callback, catchcb)
        if item.expr is not None:
            callback(item, item.expr)
        if catchcb is not None and isinstance(item, ast.TryStatement):
            for pair in item.pairs:
                catchcb(pair[0])

int_tags = frozenset(map(objtypes.baset, [IntTT, ShortTT, CharTT, ByteTT, BoolTT]))
array_tags = frozenset(map(objtypes.baset, [ByteTT, BoolTT]) + [objtypes.BExpr])

# Fix int/bool and byte[]/bool[] vars
def boolizeVars(root, arg_vars):
    varlist = []
    sets = UnionFind()

    def visitExpr(expr, forceExact=False):
        # see if we have to merge
        if isinstance(expr, ast.Assignment) or isinstance(expr, ast.BinaryInfix) and expr.opstr in ('==','!=','&','|','^'):
            subs = [visitExpr(param) for param in expr.params]
            sets.union(*subs) # these operators can work on either type but need the same type on each side
        elif isinstance(expr, ast.ArrayAccess):
            sets.union(False, visitExpr(expr.params[1])) # array index is int only
        elif isinstance(expr, ast.BinaryInfix) and expr.opstr in ('* / % + - << >> >>>'):
            sets.union(False, visitExpr(expr.params[0])) # these operators are int only
            sets.union(False, visitExpr(expr.params[1]))

        if isinstance(expr, ast.Local):
            tag, dim = objtypes.baset(expr.dtype), objtypes.dim(expr.dtype)
            if (dim == 0 and tag in int_tags) or (dim > 0 and tag in array_tags):
                # the only "unknown" vars are bexpr[] and ints. All else have fixed types
                if forceExact or (tag != BExpr and tag != objtypes.baset(IntTT)):
                    sets.union(tag == objtypes.baset(BoolTT), expr)
                varlist.append(expr)
                return sets.find(expr)
        elif isinstance(expr, ast.Literal):
            if expr.dtype == IntTT and expr.val not in (0,1):
                return False
            return None # if val is 0 or 1, or the literal is a null, it is freely convertable
        elif isinstance(expr, ast.Assignment) or (isinstance(expr, ast.BinaryInfix) and expr.opstr in ('&','|','^')):
            return subs[0]
        elif isinstance(expr, (ast.ArrayAccess, ast.Parenthesis, ast.UnaryPrefix)):
            return visitExpr(expr.params[0])
        elif expr.dtype is not None and objtypes.baset(expr.dtype) != BExpr:
            return expr.dtype[0] == objtypes.baset(BoolTT)
        return None

    def visitStatement(item, expr):
        root = visitExpr(expr)
        if isinstance(item, ast.ReturnStatement):
            forced_val = (objtypes.baset(item.tt) == objtypes.baset(BoolTT))
            sets.union(forced_val, root)
        elif isinstance(item, ast.SwitchStatement):
            sets.union(False, root) # Switch must take an int, not a bool

    for expr in arg_vars:
        visitExpr(expr, forceExact=True)
    visitStatementTree(root, callback=visitStatement)

    # Fix the propagated types
    for var in set(varlist):
        tag, dim = objtypes.baset(var.dtype), objtypes.dim(var.dtype)
        assert tag in int_tags or (dim>0 and tag == BExpr)
        # make everything bool which is not forced to int
        if sets.find(var) != False:
            var.dtype = objtypes.withDimInc(BoolTT, dim)
        elif dim > 0:
            var.dtype = objtypes.withDimInc(ByteTT, dim)

    # Fix everything else back up
    def fixExpr(item, expr):
        for param in expr.params:
            fixExpr(None, param)

        if isinstance(expr, ast.Assignment):
            left, right = expr.params
            if objtypes.baset(left.dtype) in int_tags and objtypes.dim(left.dtype) == 0:
                if not ast.isPrimativeAssignable(right.dtype, left.dtype):
                    expr.params = [left, ast.makeCastExpr(left.dtype, right)]
        elif isinstance(expr, ast.BinaryInfix):
            a, b = expr.params
            # shouldn't need to do anything here for arrays
            if expr.opstr in '== != & | ^' and a.dtype == BoolTT or b.dtype == BoolTT:
                expr.params = [ast.makeCastExpr(BoolTT, v) for v in expr.params]
    visitStatementTree(root, callback=fixExpr)

# Fix vars of interface/object type
# TODO: do this properly
def interfaceVars(env, root, arg_vars):
    varlist = []
    consts = {}
    assigns = collections.defaultdict(list)

    def isInterfaceVar(expr):
        if not isinstance(expr, ast.Local) or not objtypes.isBaseTClass(expr.dtype):
            return False
        if objtypes.className(expr.dtype) == objtypes.className(objtypes.ObjectTT):
            return True
        return env.isInterface(objtypes.className(expr.dtype))

    def updateConst(var, tt):
        varlist.append(var)
        if var not in consts:
            consts[var] = tt
        else:
            consts[var] = objtypes.commonSupertype(env, [consts[var], tt])

    def visitStatement(item, expr):
        if isinstance(expr, ast.Assignment) and objtypes.isBaseTClass(expr.dtype):
            left, right = expr.params
            if isInterfaceVar(left):
                if isInterfaceVar(right):
                    assigns[left].append(right)
                    varlist.append(right)
                    varlist.append(left)
                else:
                    updateConst(left, right.dtype)

    def visitCatchDecl(decl):
        updateConst(decl.local, decl.typename.dtype)

    for expr in arg_vars:
        if objtypes.isBaseTClass(expr.dtype):
            updateConst(expr, expr.dtype)
    visitStatementTree(root, callback=visitStatement, catchcb=visitCatchDecl)

    # Now calculate actual types and fix
    newtypes = {}

    # visit variables in topological order. Doesn't handle case of loops, but this is a temporary hack anyway
    order = graph_util.topologicalSort(varlist, lambda v:assigns[v])
    for var in order:
        assert var not in newtypes

        tts = [newtypes.get(right, objtypes.ObjectTT) for right in assigns[var]]
        if var in consts:
            tts.append(consts[var])
        newtypes[var] = newtype = objtypes.commonSupertype(env, tts)
        if newtype != objtypes.ObjectTT and newtype != var.dtype and newtype != objtypes.NullTT:
            # assert objtypes.baset(var.dtype) == objtypes.baset(objtypes.ObjectTT)
            var.dtype = newtype
