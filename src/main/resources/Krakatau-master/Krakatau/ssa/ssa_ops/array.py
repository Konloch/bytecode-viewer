from .. import excepttypes, objtypes
from ..constraints import FloatConstraint, IntConstraint, ObjectConstraint, maybeThrow, returnOrThrow, throw
from ..ssa_types import SSA_INT

from .base import BaseOp

def getElementTypes(env, tops):
    types = [objtypes.withDimInc(tt, -1) for tt in tops]
    # temporary hack
    types = [objtypes.removeInterface(env, tt) for tt in types]

    supers = [tt for tt in types if objtypes.isBaseTClass(tt)]
    exact = [tt for tt in types if not objtypes.isBaseTClass(tt)]
    return ObjectConstraint.fromTops(env, supers, exact)

class ArrLoad(BaseOp):
    def __init__(self, parent, args, ssatype):
        super(ArrLoad, self).__init__(parent, args, makeException=True)
        self.env = parent.env
        self.rval = parent.makeVariable(ssatype, origin=self)
        self.ssatype = ssatype

    def propagateConstraints(self, a, i):
        etypes = (excepttypes.ArrayOOB,)
        if a.null:
            etypes += (excepttypes.NullPtr,)
            if a.isConstNull():
                return throw(ObjectConstraint.fromTops(self.env, [], [excepttypes.NullPtr], nonnull=True))

        if self.ssatype[0] == 'int':
            rout = IntConstraint.bot(self.ssatype[1])
        elif self.ssatype[0] == 'float':
            rout = FloatConstraint.bot(self.ssatype[1])
        elif self.ssatype[0] == 'obj':
            rout = getElementTypes(self.env, a.types.supers | a.types.exact)

        eout = ObjectConstraint.fromTops(self.env, [], etypes, nonnull=True)
        return returnOrThrow(rout, eout)

class ArrStore(BaseOp):
    has_side_effects = True

    def __init__(self, parent, args):
        super(ArrStore, self).__init__(parent, args, makeException=True)
        self.env = parent.env

    def propagateConstraints(self, a, i, x):
        etypes = (excepttypes.ArrayOOB,)
        if a.null:
            etypes += (excepttypes.NullPtr,)
            if a.isConstNull():
                return throw(ObjectConstraint.fromTops(self.env, [], [excepttypes.NullPtr], nonnull=True))

        if isinstance(x, ObjectConstraint):
            # If the type of a is known exactly to be the single possibility T[]
            # and x is assignable to T, we can assume there is no ArrayStore exception
            # if a's type has multiple possibilities, then there can be an exception
            known_type = a.types.exact if len(a.types.exact) == 1 else frozenset()
            allowed = getElementTypes(self.env, known_type)
            if allowed.meet(x) != allowed:
                etypes += (excepttypes.ArrayStore,)

        return maybeThrow(ObjectConstraint.fromTops(self.env, [], etypes, nonnull=True))

class ArrLength(BaseOp):
    def __init__(self, parent, args):
        super(ArrLength, self).__init__(parent, args, makeException=True)
        self.env = parent.env
        self.rval = parent.makeVariable(SSA_INT, origin=self)

    def propagateConstraints(self, x):
        etypes = ()
        if x.null:
            etypes += (excepttypes.NullPtr,)
            if x.isConstNull():
                return throw(ObjectConstraint.fromTops(self.env, [], [excepttypes.NullPtr], nonnull=True))

        excons = ObjectConstraint.fromTops(self.env, [], etypes, nonnull=True)
        return returnOrThrow(IntConstraint.range(32, 0, (1<<31)-1), excons)
