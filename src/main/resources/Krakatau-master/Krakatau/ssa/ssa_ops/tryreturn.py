from .. import excepttypes
from ..constraints import ObjectConstraint, maybeThrow

from .base import BaseOp

class TryReturn(BaseOp):
    def __init__(self, parent, canthrow=True):
        super(TryReturn, self).__init__(parent, [], makeException=True)
        self.outExceptionCons = ObjectConstraint.fromTops(parent.env, [], (excepttypes.MonState,), nonnull=True)

    def propagateConstraints(self):
        return maybeThrow(self.outExceptionCons)
