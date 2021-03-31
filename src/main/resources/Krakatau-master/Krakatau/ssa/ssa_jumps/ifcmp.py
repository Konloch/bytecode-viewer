from .. import ssa_types
from ..constraints import IntConstraint, ObjectConstraint

from .base import BaseJump
from .goto import Goto

class If(BaseJump):
    opposites = {'eq':'ne', 'ne':'eq', 'lt':'ge', 'ge':'lt', 'gt':'le', 'le':'gt'}

    def __init__(self, parent, cmp, successors, arguments):
        super(If, self).__init__(parent, arguments)
        assert cmp in ('eq','ne','lt','ge','gt','le')
        self.cmp = cmp
        self.successors = successors
        self.isObj = (arguments[0].type == ssa_types.SSA_OBJECT)
        assert None not in successors

    def replaceBlocks(self, blockDict):
        self.successors = [blockDict.get(key,key) for key in self.successors]

    def getNormalSuccessors(self):
        return self.successors

    def reduceSuccessors(self, pairsToRemove):
        temp = set(self.successors)
        for (child, t) in pairsToRemove:
            temp.remove(child)

        if len(temp) == 0:
            return None
        elif len(temp) == 1:
            return Goto(self.parent, temp.pop())
        return self

    ###############################################################################
    def constrainJumps(self, x, y):
        impossible = []
        for child in self.successors:
            func = self.getSuccessorConstraints((child,False))

            results = func(x,y)
            if None in results:
                assert results == (None,None)
                impossible.append((child,False))
        return self.reduceSuccessors(impossible)

    def getSuccessorConstraints(self, (block, t)):
        assert t is False
        cmp_t = If.opposites[self.cmp] if block == self.successors[0] else self.cmp

        if self.isObj:
            def propagateConstraints_obj(x, y):
                if x is None or y is None:
                    return None, None
                if cmp_t == 'eq':
                    z = x.join(y)
                    return z,z
                else:
                    x2, y2 = x, y
                    if x.isConstNull():
                        yt = y.types
                        y2 = ObjectConstraint.fromTops(yt.env, yt.supers, yt.exact, nonnull=True)
                    if y.isConstNull():
                        xt = x.types
                        x2 = ObjectConstraint.fromTops(xt.env, xt.supers, xt.exact, nonnull=True)
                    return x2, y2
            return propagateConstraints_obj
        else:
            def propagateConstraints_int(x, y):
                if x is None or y is None:
                    return None, None
                x1, x2, y1, y2 = x.min, x.max, y.min, y.max
                if cmp_t == 'ge' or cmp_t == 'gt':
                    x1, x2, y1, y2 = y1, y2, x1, x2

                # treat greater like less than swap before and afterwards
                if cmp_t == 'lt' or cmp_t == 'gt':
                    x2 = min(x2, y2-1)
                    y1 = max(x1+1, y1)
                elif cmp_t == 'le' or cmp_t == 'ge':
                    x2 = min(x2, y2)
                    y1 = max(x1, y1)
                elif cmp_t == 'eq':
                    x1 = y1 = max(x1, y1)
                    x2 = y2 = min(x2, y2)
                elif cmp_t == 'ne':
                    if x1 == x2 == y1 == y2:
                        return None, None
                    if x1 == x2:
                        y1 = y1 if y1 != x1 else y1+1
                        y2 = y2 if y2 != x2 else y2-1
                    if y1 == y2:
                        x1 = x1 if x1 != y1 else x1+1
                        x2 = x2 if x2 != y2 else x2-1

                if cmp_t == 'ge' or cmp_t == 'gt':
                    x1, x2, y1, y2 = y1, y2, x1, x2
                con1 = IntConstraint.range(x.width, x1, x2) if x1 <= x2 else None
                con2 = IntConstraint.range(y.width, y1, y2) if y1 <= y2 else None
                return con1, con2
            return propagateConstraints_int
