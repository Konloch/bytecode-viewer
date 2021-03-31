from .. import excepttypes
from ..constraints import ObjectConstraint, maybeThrow

from .base import BaseOp

class Monitor(BaseOp):
    has_side_effects = True

    def __init__(self, parent, args, isExit):
        BaseOp.__init__(self, parent, args, makeException=True)
        self.exit = isExit
        self.env = parent.env

    def propagateConstraints(self, x):
        etypes = ()
        if x.null:
            etypes += (excepttypes.NullPtr,)
        if self.exit and not x.isConstNull():
            etypes += (excepttypes.MonState,)
        eout = ObjectConstraint.fromTops(self.env, [], etypes, nonnull=True)
        return maybeThrow(eout)
