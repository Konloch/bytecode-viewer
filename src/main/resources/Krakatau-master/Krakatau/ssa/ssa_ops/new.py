from .. import excepttypes, objtypes
from ..constraints import ObjectConstraint, returnOrThrow, throw
from ..ssa_types import SSA_OBJECT

from .base import BaseOp

class New(BaseOp):
    has_side_effects = True

    def __init__(self, parent, name, inode_key):
        super(New, self).__init__(parent, [], makeException=True)
        self.env = parent.env
        self.tt = objtypes.TypeTT(name, 0)
        self.rval = parent.makeVariable(SSA_OBJECT, origin=self)
        self.rval.uninit_orig_num = inode_key

    def propagateConstraints(self):
        eout = ObjectConstraint.fromTops(self.env, [], (excepttypes.OOM,), nonnull=True)
        rout = ObjectConstraint.fromTops(self.env, [], [self.tt], nonnull=True)
        return returnOrThrow(rout, eout)

class NewArray(BaseOp):
    has_side_effects = True

    def __init__(self, parent, param, baset):
        super(NewArray, self).__init__(parent, [param], makeException=True)
        self.baset = baset
        self.rval = parent.makeVariable(SSA_OBJECT, origin=self)
        self.tt = objtypes.withDimInc(baset, 1)
        self.env = parent.env

    def propagateConstraints(self, i):
        if i.max < 0:
            eout = ObjectConstraint.fromTops(self.env, [], (excepttypes.NegArrSize,), nonnull=True)
            return throw(eout)

        etypes = (excepttypes.OOM,)
        if i.min < 0:
            etypes += (excepttypes.NegArrSize,)

        eout = ObjectConstraint.fromTops(self.env, [], etypes, nonnull=True)
        rout = ObjectConstraint.fromTops(self.env, [], [self.tt], nonnull=True)
        return returnOrThrow(rout, eout)

class MultiNewArray(BaseOp):
    has_side_effects = True

    def __init__(self, parent, params, type_):
        super(MultiNewArray, self).__init__(parent, params, makeException=True)
        self.tt = type_
        self.rval = parent.makeVariable(SSA_OBJECT, origin=self)
        self.env = parent.env

    def propagateConstraints(self, *dims):
        for i in dims:
            if i.max < 0: # ignore possibility of OOM here
                eout = ObjectConstraint.fromTops(self.env, [], (excepttypes.NegArrSize,), nonnull=True)
                return throw(eout)

        etypes = (excepttypes.OOM,)
        for i in dims:
            if i.min < 0:
                etypes += (excepttypes.NegArrSize,)
                break

        eout = ObjectConstraint.fromTops(self.env, [], etypes, nonnull=True)
        rout = ObjectConstraint.fromTops(self.env, [], [self.tt], nonnull=True)
        return returnOrThrow(rout, eout)
