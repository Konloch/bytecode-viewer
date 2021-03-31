from ... import floatutil as fu

from ..mixin import ValueType

SPECIALS = frozenset((fu.NAN, fu.INF, fu.NINF, fu.ZERO, fu.NZERO))

def botRange(size):
    mbits, emin, emax = size
    mag = (1<<(mbits+1))-1, emax-mbits
    return (-1,mag), (1,mag)

class FloatConstraint(ValueType):
    def __init__(self, size, finite, special):
        self.size = size
        self.finite = finite
        self.spec = special

        self.isBot = (special == SPECIALS) and (finite == botRange(size))

    @staticmethod
    def const(size, val):
        if val in SPECIALS:
            return FloatConstraint(size, (None, None), frozenset([val]))
        return FloatConstraint(size, (val, val), frozenset())

    @staticmethod
    def bot(size):
        finite = botRange(size)
        return FloatConstraint(size, finite, SPECIALS)

    def _key(self): return self.finite, self.spec

    def join(*cons): # more precise (intersection)
        spec = frozenset.intersection(*[c.spec for c in cons])
        ranges = [c.finite for c in cons]

        if (None, None) in ranges:
            xmin = xmax = None
        else:
            mins, maxs = zip(*ranges)
            xmin = max(mins, key=fu.sortkey)
            xmax = min(maxs, key=fu.sortkey)
            if fu.sortkey(xmax) < fu.sortkey(xmin):
                xmin = xmax = None
        if not xmin and not spec:
            return None
        return FloatConstraint(cons[0].size, (xmin, xmax), spec)

    def meet(*cons):
        spec = frozenset.union(*[c.spec for c in cons])
        ranges = [c.finite for c in cons if c.finite != (None,None)]

        if ranges:
            mins, maxs = zip(*ranges)
            xmin = min(mins, key=fu.sortkey)
            xmax = max(maxs, key=fu.sortkey)
        else:
            xmin = xmax = None
        return FloatConstraint(cons[0].size, (xmin, xmax), spec)
