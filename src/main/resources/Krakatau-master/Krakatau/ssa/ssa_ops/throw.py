from .. import excepttypes, objtypes
from ..constraints import ObjectConstraint, maybeThrow, throw

from .base import BaseOp

class Throw(BaseOp):
    def __init__(self, parent, args):
        super(Throw, self).__init__(parent, args, makeException=True)
        self.env = parent.env

    def propagateConstraints(self, x):
        if x.null:
            t = x.types
            exact = list(t.exact) + [excepttypes.NullPtr]
            return throw(ObjectConstraint.fromTops(t.env, t.supers, exact, nonnull=True))
        return throw(x)

# Dummy instruction that can throw anything
class MagicThrow(BaseOp):
    def __init__(self, parent):
        super(MagicThrow, self).__init__(parent, [], makeException=True)
        self.eout = ObjectConstraint.fromTops(parent.env, [objtypes.ThrowableTT], [], nonnull=True)

    def propagateConstraints(self):
        return maybeThrow(self.eout)
