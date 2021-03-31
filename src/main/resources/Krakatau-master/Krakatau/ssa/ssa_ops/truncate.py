from ..constraints import IntConstraint, return_

from . import bitwise_util
from .base import BaseOp

class Truncate(BaseOp):
    def __init__(self, parent, arg, signed, width):
        super(Truncate, self).__init__(parent, [arg])

        self.signed, self.width = signed, width
        self.rval = parent.makeVariable(arg.type, origin=self)

    def propagateConstraints(self, x):
        # get range of target type
        w = self.width
        intw = x.width
        assert w < intw
        M = 1<<w

        mask = IntConstraint.const(intw, M-1)
        x = bitwise_util.propagateAnd(x,mask)

        # We have the mods in the range [0,M-1], but we want it in the range
        # [-M/2, M/2-1] so we need to find the new min and max
        if self.signed:
            HM = M>>1

            parts = [(i-M if i>=HM else i) for i in (x.min, x.max)]
            if x.min <= HM-1 <= x.max:
                parts.append(HM-1)
            if x.min <= HM <= x.max:
                parts.append(-HM)

            assert -HM <= min(parts) <= max(parts) <= HM-1
            return return_(IntConstraint.range(intw, min(parts), max(parts)))
        else:
            return return_(x)
