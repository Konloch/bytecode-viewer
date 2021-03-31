from .. import ssa_types
from ..constraints import IntConstraint, return_

from .base import BaseOp

class FAdd(BaseOp):
    def __init__(self, parent, args):
        BaseOp.__init__(self, parent, args)
        self.rval = parent.makeVariable(args[0].type, origin=self)
class FDiv(BaseOp):
    def __init__(self, parent, args):
        BaseOp.__init__(self, parent, args)
        self.rval = parent.makeVariable(args[0].type, origin=self)
class FMul(BaseOp):
    def __init__(self, parent, args):
        BaseOp.__init__(self, parent, args)
        self.rval = parent.makeVariable(args[0].type, origin=self)
class FRem(BaseOp):
    def __init__(self, parent, args):
        BaseOp.__init__(self, parent, args)
        self.rval = parent.makeVariable(args[0].type, origin=self)
class FSub(BaseOp):
    def __init__(self, parent, args):
        BaseOp.__init__(self, parent, args)
        self.rval = parent.makeVariable(args[0].type, origin=self)

# Unary, unlike the others
class FNeg(BaseOp):
    def __init__(self, parent, args):
        BaseOp.__init__(self, parent, args)
        self.rval = parent.makeVariable(args[0].type, origin=self)

class FCmp(BaseOp):
    def __init__(self, parent, args, NaN_val):
        BaseOp.__init__(self, parent, args)
        self.rval = parent.makeVariable(ssa_types.SSA_INT, origin=self)
        self.NaN_val = NaN_val

    def propagateConstraints(self, x, y):
        return return_(IntConstraint.range(32, -1, 1))
