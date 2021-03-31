import itertools
import math

from ..ssa import objtypes

from . import visitor
from .stringescape import escapeString

# Explicitly cast parameters to the desired type in order to avoid potential issues with overloaded methods
ALWAYS_CAST_PARAMS = 1

class VariableDeclarator(object):
    def __init__(self, typename, identifier): self.typename = typename; self.local = identifier

    def print_(self, printer, print_):
        return '{} {}'.format(print_(self.typename), print_(self.local))

    def tree(self, printer, tree): return [tree(self.typename), tree(self.local)]

#############################################################################################################################################

class JavaStatement(object):
    expr = None # provide default for subclasses that don't have an expression
    def getScopes(self): return ()

    def fixLiterals(self):
        if self.expr is not None:
            self.expr = self.expr.fixLiterals()

    def addCastsAndParens(self, env):
        if self.expr is not None:
            self.expr.addCasts(env)
            self.expr.addParens()

class ExpressionStatement(JavaStatement):
    def __init__(self, expr):
        self.expr = expr
        assert expr is not None

    def print_(self, printer, print_): return print_(self.expr) + ';'
    def tree(self, printer, tree): return [self.__class__.__name__, tree(self.expr)]

class LocalDeclarationStatement(JavaStatement):
    def __init__(self, decl, expr=None):
        self.decl = decl
        self.expr = expr

    def print_(self, printer, print_):
        if self.expr is not None:
            return '{} = {};'.format(print_(self.decl), print_(self.expr))
        return print_(self.decl) + ';'

    def tree(self, printer, tree): return [self.__class__.__name__, tree(self.expr), tree(self.decl)]

    def addCastsAndParens(self, env):
        if self.expr is not None:
            self.expr.addCasts(env)

            if not isJavaAssignable(env, self.expr.dtype, self.decl.typename.tt):
                self.expr = makeCastExpr(self.decl.typename.tt, self.expr, fixEnv=env)
            self.expr.addParens()

class ReturnStatement(JavaStatement):
    def __init__(self, expr=None, tt=None):
        self.expr = expr
        self.tt = tt

    def print_(self, printer, print_): return 'return {};'.format(print_(self.expr)) if self.expr is not None else 'return;'
    def tree(self, printer, tree): return [self.__class__.__name__, tree(self.expr)]

    def addCastsAndParens(self, env):
        if self.expr is not None:
            self.expr.addCasts(env)
            if not isJavaAssignable(env, self.expr.dtype, self.tt):
                self.expr = makeCastExpr(self.tt, self.expr, fixEnv=env)
            self.expr.addParens()

class ThrowStatement(JavaStatement):
    def __init__(self, expr):
        self.expr = expr
    def print_(self, printer, print_): return 'throw {};'.format(print_(self.expr))
    def tree(self, printer, tree): return [self.__class__.__name__, tree(self.expr)]

class JumpStatement(JavaStatement):
    def __init__(self, target, isFront):
        self.label = target.getLabel() if target is not None else None
        self.keyword = 'continue' if isFront else 'break'

    def print_(self, printer, print_):
        label = ' ' + self.label if self.label is not None else ''
        return self.keyword + label + ';'

    def tree(self, printer, tree): return [self.__class__.__name__, self.keyword, self.label]

# Compound Statements
sbcount = itertools.count()
class LazyLabelBase(JavaStatement):
    # Jumps are represented by arbitrary 'keys', currently just the key of the
    # original proxy node. Each item has a continueKey and a breakKey representing
    # the beginning and the point just past the end respectively. breakKey may be
    # None if this item appears at the end of the function and there is nothing after it.
    # Statement blocks have a jump key representing where it jumps to if any. This
    # may be None if the jump is unreachable (such as if there is a throw or return)
    def __init__(self, labelfunc, begink, endk):
        self.label, self.func = None, labelfunc
        self.continueKey = begink
        self.breakKey = endk
        self.id = next(sbcount) # For debugging purposes

    def getLabel(self):
        if self.label is None:
            self.label = self.func() # Not a bound function!
        return self.label

    def getLabelPrefix(self): return '' if self.label is None else self.label + ': '
    # def getLabelPrefix(self): return self.getLabel() + ': '

    # For debugging
    def __str__(self):   # pragma: no cover
        if isinstance(self, StatementBlock):
            return 'Sb'+str(self.id)
        return type(self).__name__[:3]+str(self.id)
    __repr__ = __str__

class TryStatement(LazyLabelBase):
    def __init__(self, labelfunc, begink, endk, tryb, pairs):
        super(TryStatement, self).__init__(labelfunc, begink, endk)
        self.tryb, self.pairs = tryb, pairs

    def getScopes(self): return (self.tryb,) + zip(*self.pairs)[1]

    def print_(self, printer, print_):
        tryb = print_(self.tryb)
        parts = ['catch({}) {}'.format(print_(x), print_(y)) for x,y in self.pairs]
        return '{}try {} {}'.format(self.getLabelPrefix(), tryb, '\n'.join(parts))

    def tree(self, printer, tree):
        parts = [map(tree, t) for t in self.pairs]
        return [self.__class__.__name__, self.label, tree(self.tryb), parts]

class IfStatement(LazyLabelBase):
    def __init__(self, labelfunc, begink, endk, expr, scopes):
        super(IfStatement, self).__init__(labelfunc, begink, endk)
        self.expr = expr # don't rename without changing how var replacement works!
        self.scopes = scopes

    def getScopes(self): return self.scopes

    def print_(self, printer, print_):
        lbl = self.getLabelPrefix()
        parts = [self.expr] + list(self.scopes)

        if len(self.scopes) == 1:
            parts = [print_(x) for x in parts]
            return '{}if ({}) {}'.format(lbl, *parts)

        # Special case handling for 'else if'
        fblock = self.scopes[1]
        if len(fblock.statements) == 1:
            stmt = fblock.statements[-1]
            if isinstance(stmt, IfStatement) and stmt.label is None:
                parts[-1] = stmt
        parts = [print_(x) for x in parts]
        return '{}if ({}) {} else {}'.format(lbl, *parts)

    def tree(self, printer, tree): return [self.__class__.__name__, self.label, tree(self.expr), map(tree, self.scopes)]

class SwitchStatement(LazyLabelBase):
    def __init__(self, labelfunc, begink, endk, expr, pairs):
        super(SwitchStatement, self).__init__(labelfunc, begink, endk)
        self.expr = expr # don't rename without changing how var replacement works!
        self.pairs = pairs

    def getScopes(self): return zip(*self.pairs)[1]
    def hasDefault(self): return None in zip(*self.pairs)[0]

    def print_(self, printer, print_):
        expr = print_(self.expr)

        def printCase(keys):
            if keys is None:
                return 'default: '
            assert keys
            return ''.join(map('case {}: '.format, sorted(keys)))

        bodies = [(printCase(keys) + print_(scope)) for keys, scope in self.pairs]
        if self.pairs[-1][0] is None and len(self.pairs[-1][1].statements) == 0:
            bodies.pop()

        contents = '\n'.join(bodies)
        indented = ['    '+line for line in contents.splitlines()]
        lines = ['{'] + indented + ['}']
        return '{}switch({}) {}'.format(self.getLabelPrefix(), expr, '\n'.join(lines))

    def tree(self, printer, tree):
        parts = []
        for keys, scope in self.pairs:
            parts.append([[None] if keys is None else sorted(keys), tree(scope)])
        return [self.__class__.__name__, self.label, tree(self.expr), parts]

class WhileStatement(LazyLabelBase):
    def __init__(self, labelfunc, begink, endk, parts):
        super(WhileStatement, self).__init__(labelfunc, begink, endk)
        self.expr = Literal.TRUE
        self.parts = parts
        assert len(self.parts) == 1

    def getScopes(self): return self.parts

    def print_(self, printer, print_):
        parts = print_(self.expr), print_(self.parts[0])
        return '{}while({}) {}'.format(self.getLabelPrefix(), *parts)

    def tree(self, printer, tree): return [self.__class__.__name__, self.label, tree(self.expr), tree(self.parts[0])]

class StatementBlock(LazyLabelBase):
    def __init__(self, labelfunc, begink, endk, statements, jumpk, labelable=True):
        super(StatementBlock, self).__init__(labelfunc, begink, endk)
        self.parent = None # should be assigned later
        self.statements = statements
        self.jumpKey = jumpk
        self.labelable = labelable

    def doesFallthrough(self): return self.jumpKey is None or self.jumpKey == self.breakKey

    def getScopes(self): return self,

    def print_(self, printer, print_):
        assert self.labelable or self.label is None
        contents = '\n'.join(print_(x) for x in self.statements)
        indented = ['    '+line for line in contents.splitlines()]
        # indented[:0] = ['    //{} {}'.format(self,x) for x in (self.continueKey, self.breakKey, self.jumpKey)]
        lines = [self.getLabelPrefix() + '{'] + indented + ['}']
        return '\n'.join(lines)

    @staticmethod
    def join(*scopes):
        blists = [s.bases for s in scopes if s is not None] # allow None to represent the universe (top element)
        if not blists:
            return None
        common = [x for x in zip(*blists) if len(set(x)) == 1]
        return common[-1][0]

    def tree(self, printer, tree): return ['BlockStatement', self.label, map(tree, self.statements)]

#############################################################################################################################################
# Careful, order is important here!
_assignable_sprims = objtypes.ByteTT, objtypes.ShortTT, objtypes.CharTT
_assignable_lprims = objtypes.IntTT, objtypes.LongTT, objtypes.FloatTT, objtypes.DoubleTT

# Also used in boolize.py
def isPrimativeAssignable(x, y): # x = fromt, y = to
    assert objtypes.dim(x) == objtypes.dim(y) == 0

    if x == y or (x in _assignable_sprims and y in _assignable_lprims):
        return True
    elif (x in _assignable_lprims and y in _assignable_lprims):
        return _assignable_lprims.index(x) <= _assignable_lprims.index(y)
    else:
        return (x, y) == (objtypes.ByteTT, objtypes.ShortTT)

def isReferenceType(tt):
    return tt == objtypes.NullTT or objtypes.dim(tt) or (objtypes.className(tt) is not None)

def isJavaAssignable(env, fromt, to):
    if fromt is None or to is None: # this should never happen, except during debugging
        return True

    if isReferenceType(to):
        assert isReferenceType(fromt)
        # todo - make it check interfaces too
        return objtypes.isSubtype(env, fromt, to)
    else: # allowed if numeric conversion is widening
        return isPrimativeAssignable(fromt, to)

_int_tts = objtypes.LongTT, objtypes.IntTT, objtypes.ShortTT, objtypes.CharTT, objtypes.ByteTT
def makeCastExpr(newtt, expr, fixEnv=None):
    if newtt == expr.dtype:
        return expr

    # if casting a literal with compatible type, just create a literal of the new type
    if isinstance(expr, Literal):
        allowed_conversions = [
            (objtypes.FloatTT, objtypes.DoubleTT),
            (objtypes.IntTT, objtypes.LongTT),
            (objtypes.IntTT, objtypes.BoolTT),
            (objtypes.BoolTT, objtypes.IntTT),
        ]
        if (expr.dtype, newtt) in allowed_conversions:
            return Literal(newtt, expr.val)

    if newtt == objtypes.IntTT and expr.dtype == objtypes.BoolTT:
        return Ternary(expr, Literal.ONE, Literal.ZERO)
    elif newtt == objtypes.BoolTT and expr.dtype == objtypes.IntTT:
        return BinaryInfix('!=', [expr, Literal.ZERO], objtypes.BoolTT)

    ret = Cast(TypeName(newtt), expr)
    if fixEnv is not None:
        ret = ret.fix(fixEnv)
    return ret
#############################################################################################################################################
# Precedence:
#    0 - pseudoprimary
#    5 - pseudounary
#    10-19 binary infix
#    20 - ternary
#    21 - assignment
# Associativity: L = Left, R = Right, A = Full

class JavaExpression(object):
    precedence = 0 # Default precedence
    params = [] # for subclasses that don't have params

    def complexity(self): return 1 + max(e.complexity() for e in self.params) if self.params else 0

    def postFlatIter(self):
        return itertools.chain([self], *[expr.postFlatIter() for expr in self.params])

    def print_(self, printer, print_):
        return self.fmt.format(*[print_(expr) for expr in self.params])

    def tree(self, printer, tree): return [self.__class__.__name__, map(tree, self.params)]

    def replaceSubExprs(self, rdict):
        if self in rdict:
            return rdict[self]
        self.params = [param.replaceSubExprs(rdict) for param in self.params]
        return self

    def fixLiterals(self):
        self.params = [param.fixLiterals() for param in self.params]
        return self

    def addCasts(self, env):
        for param in self.params:
            param.addCasts(env)
        self.addCasts_sub(env)

    def addCasts_sub(self, env): pass

    def addParens(self):
        for param in self.params:
            param.addParens()
        self.params = list(self.params) # Copy before editing, just to be extra safe
        self.addParens_sub()

    def addParens_sub(self): pass

    def isLocalAssign(self): return isinstance(self, Assignment) and isinstance(self.params[0], Local)

    def __repr__(self):   # pragma: no cover
        return type(self).__name__.rpartition('.')[-1] + ' ' + visitor.DefaultVisitor().visit(self)
    __str__ = __repr__

class ArrayAccess(JavaExpression):
    def __init__(self, *params):
        if params[0].dtype == objtypes.NullTT:
            # Unfortunately, Java doesn't really support array access on null constants
            #So we'll just cast it to Object[] as a hack
            param = makeCastExpr(objtypes.withDimInc(objtypes.ObjectTT, 1), params[0])
            params = param, params[1]

        self.params = list(params)
        self.fmt = '{}[{}]'

    @property
    def dtype(self): return objtypes.withDimInc(self.params[0].dtype, -1)

    def addParens_sub(self):
        p0 = self.params[0]
        if p0.precedence > 0 or isinstance(p0, ArrayCreation):
            self.params[0] = Parenthesis(p0)

class ArrayCreation(JavaExpression):
    def __init__(self, tt, *sizeargs):
        self.dim = objtypes.dim(tt)
        self.params = [TypeName(objtypes.withNoDim(tt))] + list(sizeargs)
        self.dtype = tt
        assert self.dim >= len(sizeargs) > 0
        self.fmt = 'new {}' + '[{}]'*len(sizeargs) + '[]'*(self.dim-len(sizeargs))

    def tree(self, printer, tree): return [self.__class__.__name__, map(tree, self.params), self.dim]

class Assignment(JavaExpression):
    precedence = 21
    def __init__(self, *params):
        self.params = list(params)
        self.fmt = '{} = {}'

    @property
    def dtype(self): return self.params[0].dtype

    def addCasts_sub(self, env):
        left, right = self.params
        if not isJavaAssignable(env, right.dtype, left.dtype):
            expr = makeCastExpr(left.dtype, right, fixEnv=env)
            self.params = [left, expr]

    def tree(self, printer, tree): return [self.__class__.__name__, map(tree, self.params), '']

_binary_ptable = ['* / %', '+ -', '<< >> >>>',
    '< > <= >= instanceof', '== !=',
    '&', '^', '|', '&&', '||']

binary_precedences = {}
for _ops, _val in zip(_binary_ptable, range(10,20)):
    for _op in _ops.split():
        binary_precedences[_op] = _val

class BinaryInfix(JavaExpression):
    def __init__(self, opstr, params, dtype=None):
        assert len(params) == 2
        self.params = params
        self.opstr = opstr
        self.fmt = '{{}} {} {{}}'.format(opstr)
        self._dtype = dtype
        self.precedence = binary_precedences[opstr]

    @property
    def dtype(self): return self.params[0].dtype if self._dtype is None else self._dtype

    def addParens_sub(self):
        myprec = self.precedence
        associative = myprec >= 15 # for now we treat +, *, etc as nonassociative due to floats

        for i, p in enumerate(self.params):
            if p.precedence > myprec:
                self.params[i] = Parenthesis(p)
            elif p.precedence == myprec and i > 0 and not associative:
                self.params[i] = Parenthesis(p)

    def tree(self, printer, tree): return [self.__class__.__name__, map(tree, self.params), self.opstr]

class Cast(JavaExpression):
    precedence = 5
    def __init__(self, *params):
        self.dtype = params[0].tt
        self.params = list(params)
        self.fmt = '({}){}'

    def fix(self, env):
        tt, expr = self.dtype, self.params[1]
        # "Impossible" casts are a compile error in Java.
        # This can be fixed with an intermediate cast to Object
        if isReferenceType(tt):
            if not isJavaAssignable(env, tt, expr.dtype):
                if not isJavaAssignable(env, expr.dtype, tt):
                    expr = makeCastExpr(objtypes.ObjectTT, expr)
                    self.params = [self.params[0], expr]
        return self

    def addCasts_sub(self, env): self.fix(env)
    def addParens_sub(self):
        p1 = self.params[1]
        if p1.precedence > 5 or (isinstance(p1, UnaryPrefix) and p1.opstr[0] in '-+'):
            self.params[1] = Parenthesis(p1)

class ClassInstanceCreation(JavaExpression):
    def __init__(self, typename, tts, arguments):
        self.typename, self.tts, self.params = typename, tts, arguments
        self.dtype = typename.tt

    def print_(self, printer, print_):
        return 'new {}({})'.format(print_(self.typename), ', '.join(print_(x) for x in self.params))

    def tree(self, printer, tree):
        return [self.__class__.__name__, map(tree, self.params), tree(self.typename)]

    def addCasts_sub(self, env):
        newparams = []
        for tt, expr in zip(self.tts, self.params):
            if expr.dtype != tt and (ALWAYS_CAST_PARAMS or not isJavaAssignable(env, expr.dtype, tt)):
                expr = makeCastExpr(tt, expr, fixEnv=env)
            newparams.append(expr)
        self.params = newparams

class FieldAccess(JavaExpression):
    def __init__(self, primary, name, dtype, op=None, printLeft=True):
        self.dtype = dtype
        self.params = [primary]
        self.op, self.name = op, name
        self.printLeft = printLeft
        # self.params, self.name = [primary], escapeString(name)
        # self.fmt = ('{}.' if printLeft else '') + self.name

    def print_(self, printer, print_):
        if self.op is None:
            name = self.name
            assert name in ('length','class')
        else:
            cls, name, desc = self.op.target, self.op.name, self.op.desc
            name = escapeString(printer.fieldName(cls, name, desc))
        pre = print_(self.params[0])+'.' if self.printLeft else ''
        return pre+name

    def tree(self, printer, tree):
        if self.op is None:
            trip = None, self.name, None
        else:
            trip = self.op.target, self.op.name, self.op.desc
        return [self.__class__.__name__, map(tree, self.params), trip, self.printLeft]

    def addParens_sub(self):
        p0 = self.params[0]
        if p0.precedence > 0:
            self.params[0] = Parenthesis(p0)

def printFloat(x, isSingle):
    assert x >= 0.0 and not math.isinf(x)
    suffix = 'f' if isSingle else ''
    if isSingle and x > 0.0:
        # Try to find more compract representation for floats, since repr treats everything as doubles
        m, e = math.frexp(x)
        half_ulp2 = math.ldexp(1.0, max(e - 25, -150)) # don't bother doubling when near the upper range of a given e value
        half_ulp1 = (half_ulp2/2) if m == 0.5 and e >= -125 else half_ulp2
        lbound, ubound = x-half_ulp1, x+half_ulp2
        assert lbound < x < ubound
        s = '{:g}'.format(x).replace('+','')
        if lbound < float(s) < ubound: # strict ineq to avoid potential double rounding issues
            return s + suffix
    return repr(x) + suffix

class Literal(JavaExpression):
    def __init__(self, vartype, val):
        self.dtype = vartype
        self.val = val
        if self.dtype == objtypes.ClassTT:
            self.params = [TypeName(val)]

    def getStr(self):
        if self.dtype == objtypes.StringTT:
            return '"' + escapeString(self.val) + '"'
        elif self.dtype == objtypes.IntTT:
            return str(self.val)
        elif self.dtype == objtypes.LongTT:
            return str(self.val) + 'L'
        elif self.dtype == objtypes.FloatTT or self.dtype == objtypes.DoubleTT:
            return printFloat(self.val, self.dtype == objtypes.FloatTT)
        elif self.dtype == objtypes.NullTT:
            return 'null'
        elif self.dtype == objtypes.BoolTT:
            return 'true' if self.val else 'false'

    def fixLiterals(self):
        # From the point of view of the Java Language, there is no such thing as a negative literal.
        # This replaces invalid literal values with unary minus (and division for non-finite floats)
        if self.dtype == objtypes.IntTT or self.dtype == objtypes.LongTT:
            if self.val < 0:
                return UnaryPrefix('-', Literal(self.dtype, -self.val))
        elif self.dtype == objtypes.FloatTT or self.dtype == objtypes.DoubleTT:
            x = self.val
            zero = Literal.DZERO if self.dtype == objtypes.DoubleTT else Literal.FZERO
            if math.isnan(x):
                return BinaryInfix('/', [zero, zero])
            elif math.isinf(x): #+/- inf
                numerator = Literal(self.dtype, math.copysign(1.0, x)).fixLiterals()
                return BinaryInfix('/', [numerator, zero])
            # finite negative numbers
            if math.copysign(1.0, x) == -1.0:
                return UnaryPrefix('-', Literal(self.dtype, math.copysign(x, 1.0)))

        return self

    def print_(self, printer, print_):
        if self.dtype == objtypes.ClassTT:
            # for printing class literals
            return '{}.class'.format(print_(self.params[0]))
        return self.getStr()

    def tree(self, printer, tree):
        result = tree(self.params[0]) if self.dtype == objtypes.ClassTT else self.getStr()
        return [self.__class__.__name__, result, self.dtype]

    def _key(self): return self.dtype, self.val
    def __eq__(self, other): return type(self) == type(other) and self._key() == other._key()
    def __ne__(self, other): return type(self) != type(other) or self._key() != other._key()
    def __hash__(self): return hash(self._key())
Literal.FALSE = Literal(objtypes.BoolTT, 0)
Literal.TRUE = Literal(objtypes.BoolTT, 1)
Literal.N_ONE = Literal(objtypes.IntTT, -1)
Literal.ZERO = Literal(objtypes.IntTT, 0)
Literal.ONE = Literal(objtypes.IntTT, 1)

Literal.LZERO = Literal(objtypes.LongTT, 0)
Literal.FZERO = Literal(objtypes.FloatTT, 0.0)
Literal.DZERO = Literal(objtypes.DoubleTT, 0.0)
Literal.NULL = Literal(objtypes.NullTT, None)

_init_d = {objtypes.BoolTT: Literal.FALSE,
        objtypes.IntTT: Literal.ZERO,
        objtypes.LongTT: Literal.LZERO,
        objtypes.FloatTT: Literal.FZERO,
        objtypes.DoubleTT: Literal.DZERO}
def dummyLiteral(tt):
    return _init_d.get(tt, Literal.NULL)

class Local(JavaExpression):
    def __init__(self, vartype, namefunc):
        self.dtype = vartype
        self.name = None
        self.func = namefunc

    def print_(self, printer, print_):
        if self.name is None:
            self.name = self.func(self)
        return self.name

    def tree(self, printer, tree): return [self.__class__.__name__, self.print_(None, None)]

class MethodInvocation(JavaExpression):
    def __init__(self, left, name, tts, arguments, op, dtype):
        if left is None:
            self.params = arguments
        else:
            self.params = [left] + arguments
        self.hasLeft = (left is not None)
        self.dtype = dtype
        self.name = name
        self.tts = tts
        self.op = op # keep around for future reference and new merging

    def print_(self, printer, print_):
        cls, name, desc = self.op.target, self.op.name, self.op.desc
        if name != self.name:
            assert name == '<init>'
            name = self.name
        else:
            name = escapeString(printer.methodName(cls, name, desc))

        if self.hasLeft:
            left, arguments = self.params[0], self.params[1:]
            return '{}.{}({})'.format(print_(left), name, ', '.join(print_(x) for x in arguments))
        else:
            arguments = self.params
            return '{}({})'.format(name, ', '.join(print_(x) for x in arguments))

    def tree(self, printer, tree):
        trip = self.op.target, self.op.name, self.op.desc
        return [self.__class__.__name__, map(tree, self.params), trip, self.name, self.hasLeft]

    def addCasts_sub(self, env):
        newparams = []
        for tt, expr in zip(self.tts, self.params):
            if expr.dtype != tt and (ALWAYS_CAST_PARAMS or not isJavaAssignable(env, expr.dtype, tt)):
                expr = makeCastExpr(tt, expr, fixEnv=env)
            newparams.append(expr)
        self.params = newparams

    def addParens_sub(self):
        if self.hasLeft:
            p0 = self.params[0]
            if p0.precedence > 0:
                self.params[0] = Parenthesis(p0)

class Parenthesis(JavaExpression):
    def __init__(self, param):
        self.params = [param]
        self.fmt = '({})'

    @property
    def dtype(self): return self.params[0].dtype

class Ternary(JavaExpression):
    precedence = 20
    def __init__(self, *params):
        self.params = list(params)
        self.fmt = '{} ? {} : {}'

    @property
    def dtype(self): return self.params[1].dtype

    def addParens_sub(self):
        # Add unecessary parenthesis to complex conditions for readability
        if self.params[0].precedence >= 20 or self.params[0].complexity() > 0:
            self.params[0] = Parenthesis(self.params[0])
        if self.params[2].precedence > 20:
            self.params[2] = Parenthesis(self.params[2])

class TypeName(JavaExpression):
    def __init__(self, tt):
        self.dtype = None
        self.tt = tt

    def print_(self, printer, print_):
        name = objtypes.className(self.tt)
        if name is not None:
            name = printer.className(name)
            name = escapeString(name.replace('/','.'))
            if name.rpartition('.')[0] == 'java.lang':
                name = name.rpartition('.')[2]
        else:
            name = objtypes.primName(self.tt)
        s = name + '[]'*objtypes.dim(self.tt)
        return s

    def tree(self, printer, tree): return [self.__class__.__name__, self.tt]

    def complexity(self): return -1 # exprs which have this as a param won't be bumped up to 1 uncessarily

class CatchTypeNames(JavaExpression): # Used for caught exceptions, which can have multiple types specified
    def __init__(self, env, tts):
        assert(tts and not any(objtypes.dim(tt) for tt in tts)) # at least one type, no array types
        self.tnames = map(TypeName, tts)
        self.dtype = objtypes.commonSupertype(env, tts)

    def print_(self, printer, print_):
        return ' | '.join(print_(tn) for tn in self.tnames)

    def tree(self, printer, tree): return [self.__class__.__name__, map(tree, self.tnames)]

class UnaryPrefix(JavaExpression):
    precedence = 5
    def __init__(self, opstr, param, dtype=None):
        self.params = [param]
        self.opstr = opstr
        self.fmt = opstr + '{}'
        self._dtype = dtype

    @property
    def dtype(self): return self.params[0].dtype if self._dtype is None else self._dtype

    def addParens_sub(self):
        p0 = self.params[0]
        if p0.precedence > 5 or (isinstance(p0, UnaryPrefix) and p0.opstr[0] == self.opstr[0]):
            self.params[0] = Parenthesis(p0)

    def tree(self, printer, tree): return ['Unary', map(tree, self.params), self.opstr, False]

class Dummy(JavaExpression):
    def __init__(self, fmt, params, isNew=False, dtype=None):
        self.params = params
        self.fmt = fmt
        self.isNew = isNew
        self.dtype = dtype
