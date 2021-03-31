import collections
import itertools

from . import objtypes
from .mixin import ValueType

class CatchSetManager(object):
    def __init__(self, env, sets, mask):
        self.env, self.sets, self.mask = env, sets, mask
        assert not self._conscheck()

    @staticmethod # factory
    def new(env, chpairs):
        sets = collections.OrderedDict() # make this ordered since OnException relies on it
        sofar = empty = ExceptionSet.EMPTY
        for catchtype, handler in chpairs:
            old = sets.get(handler, empty)
            new = ExceptionSet.fromTops(env, catchtype)
            sets[handler] = old | (new - sofar)
            sofar = sofar | new
        return CatchSetManager(env, sets, sofar)

    def newMask(self, mask):
        for k in self.sets:
            self.sets[k] &= mask
        self.mask &= mask
        assert not self._conscheck()

    def pruneKeys(self):
        for handler, catchset in list(self.sets.items()):
            if not catchset:
                del self.sets[handler]

    def copy(self):
        return CatchSetManager(self.env, self.sets.copy(), self.mask)

    def replaceKeys(self, replace):
        self.sets = collections.OrderedDict((replace.get(key,key), val) for key, val in self.sets.items())

    def _conscheck(self):
        temp = ExceptionSet.EMPTY
        for v in self.sets.values():
            assert not v & temp
            temp |= v
        assert temp == self.mask
        assert isinstance(self.sets, collections.OrderedDict)

class ExceptionSet(ValueType):
    __slots__ = "env pairs".split()
    def __init__(self, env, pairs): # assumes arguments are in reduced form
        self.env = env
        self.pairs = frozenset([(x,frozenset(y)) for x,y in pairs])

        # We allow env to be None for the empty set so we can construct empty sets easily
        # Any operation resulting in a nonempty set will get its env from the nonempty argument
        assert self.empty() or self.env is not None

        # make sure set is fully reduced
        parts = []
        for t, holes in pairs:
            parts.append(t)
            parts.extend(holes)
        assert len(set(parts)) == len(parts)

    @staticmethod # factory
    def fromTops(env, *tops):
        return ExceptionSet(env, [(x, frozenset()) for x in tops])

    def _key(self): return self.pairs
    def empty(self): return not self.pairs
    def __nonzero__(self): return bool(self.pairs)

    def getTopTTs(self): return sorted([objtypes.TypeTT(top,0) for (top,holes) in self.pairs])

    def __sub__(self, other):
        assert type(self) == type(other)
        if self.empty() or other.empty():
            return self
        if self == other:
            return ExceptionSet.EMPTY

        subtest = self.env.isSubclass
        pairs = self.pairs

        for pair2 in other.pairs:
            # Warning, due to a bug in Python, TypeErrors raised inside the gen expr will give an incorect error message
            # TypeError: type object argument after * must be a sequence, not generator
            # This can be worked around by using a list comprehension instead of a genexpr after the *
            pairs = itertools.chain(*[ExceptionSet.diffPair(subtest, pair1, pair2) for pair1 in pairs])
        return ExceptionSet.reduce(self.env, pairs)

    def __or__(self, other):
        assert type(self) == type(other)
        if other.empty() or self == other:
            return self
        if self.empty():
            return other
        return ExceptionSet.reduce(self.env, self.pairs | other.pairs)

    def __and__(self, other):
        assert type(self) == type(other)
        new = self - (self - other)
        return new

    def isdisjoint(self, other):
        return (self-other) == self

    def __str__(self):   # pragma: no cover
        parts = [('{} - [{}]'.format(top, ', '.join(sorted(holes))) if holes else top) for top, holes in self.pairs]
        return 'ES[{}]'.format(', '.join(parts))
    __repr__ = __str__

    @staticmethod
    def diffPair(subtest, pair1, pair2): # subtract pair2 from pair1. Returns a list of new pairs
        # todo - find way to make this less ugly
        t1, holes1 = pair1
        t2, holes2 = pair2
        if subtest(t1,t2): # t2 >= t1
            if any(subtest(t1, h) for h in holes2):
                return pair1,
            else:
                newpairs = []
                holes2 = [h for h in holes2 if subtest(h, t1) and not any(subtest(h,h2) for h2 in holes1)]

                for h in holes2:
                    newholes = [h2 for h2 in holes1 if subtest(h2, h)]
                    newpairs.append((h, newholes))
                return newpairs
        elif subtest(t2,t1): # t2 < t1
            if any(subtest(t2, h) for h in holes1):
                return pair1,
            else:
                newpairs = [(t1,ExceptionSet.reduceHoles(subtest, list(holes1)+[t2]))]
                holes2 = [h for h in holes2 if not any(subtest(h,h2) for h2 in holes1)]

                for h in holes2:
                    newholes = [h2 for h2 in holes1 if subtest(h2, h)]
                    newpairs.append((h, newholes))
                return newpairs
        else:
            return pair1,

    @staticmethod
    def mergePair(subtest, pair1, pair2): # merge pair2 into pair1 and return the union
        t1, holes1 = pair1
        t2, holes2 = pair2
        assert subtest(t2,t1)

        if t2 in holes1:
            holes1 = list(holes1)
            holes1.remove(t2)
            return t1, holes1 + list(holes2)

        # TODO - this can probably be made more efficient
        holes1a = set(h for h in holes1 if not subtest(h, t2))
        holes1b = [h for h in holes1 if h not in holes1a]

        merged_holes = set()
        for h1, h2 in itertools.product(holes1b, holes2):
            if subtest(h2, h1):
                merged_holes.add(h1)
            elif subtest(h1, h2):
                merged_holes.add(h2)
        merged_holes = ExceptionSet.reduceHoles(subtest, merged_holes)
        assert len(merged_holes) <= len(holes1b) + len(holes2)
        return t1, (list(holes1a) + merged_holes)

    @staticmethod
    def reduceHoles(subtest, holes):
        newholes = []
        for hole in holes:
            for ehole in newholes:
                if subtest(hole, ehole):
                    break
            else:
                newholes = [hole] + [h for h in newholes if not subtest(h, hole)]
        return newholes

    @staticmethod
    def reduce(env, pairs):
        subtest = env.isSubclass
        pairs = [pair for pair in pairs if pair[0] not in pair[1]] # remove all degenerate pairs

        newpairs = []
        while pairs:
            top, holes = pair = pairs.pop()

            # look for an existing top to merge into
            for epair in newpairs[:]:
                etop, eholes = epair
                # new pair can be merged into existing pair
                if subtest(top, etop) and (top in eholes or not any(subtest(top, ehole) for ehole in eholes)):
                    new = ExceptionSet.mergePair(subtest, epair, pair)
                    newpairs, pairs = [new], [p for p in newpairs if p is not epair] + pairs
                    break
                # existing pair can be merged into new pair
                elif subtest(etop, top) and (etop in holes or not any(subtest(etop, hole) for hole in holes)):
                    new = ExceptionSet.mergePair(subtest, pair, epair)
                    newpairs, pairs = [new], [p for p in newpairs if p is not epair] + pairs
                    break
            # pair is incomparable to all existing pairs
            else:
                holes = ExceptionSet.reduceHoles(subtest, holes)
                newpairs.append((top,holes))
        return ExceptionSet(env, newpairs)

ExceptionSet.EMPTY = ExceptionSet(None, [])
