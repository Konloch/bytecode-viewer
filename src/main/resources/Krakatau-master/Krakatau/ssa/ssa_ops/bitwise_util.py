import itertools
import operator

from ..constraints import IntConstraint

def split_pow2ranges(x,y):
    '''split given range into power of two ranges of form [x, x+2^k)'''
    out = []
    while x<=y:
        # The largest power of two range of the form x,k
        # has k min of number of zeros at end of x
        # and the largest power of two that fits in y-x
        bx = bin(x)
        numzeroes = float('inf') if x==0 else (len(bx)-bx.rindex('1')-1)
        k = min(numzeroes, (y-x+1).bit_length()-1)
        out.append((x,k))
        x += 1<<k
    assert x == y+1
    return out

def propagateBitwise(arg1, arg2, op, usemin, usemax):
    ranges1 = split_pow2ranges(arg1.min, arg1.max)
    ranges2 = split_pow2ranges(arg2.min, arg2.max)

    vals = []
    for (s1,k1),(s2,k2) in itertools.product(ranges1, ranges2):
        # there are three parts. The high bits fixed in both arguments,
        # the middle bits fixed in one but not the other, and the
        # lowest bits which can be chosen freely for both arguments
        # high = op(h1,h2) and low goes from 0 to 1... but the range of
        # the middle depends on the particular operation
        # 0-x, x-1 and 0-1 for and, or, and xor respectively
        if k1 > k2:
            (s1,k1),(s2,k2) = (s2,k2),(s1,k1)

        mask1 = (1<<k1) - 1
        mask2 = (1<<k2) - 1 - mask1

        high = op(s1, s2) & ~(mask1 | mask2)
        midmin = (s1 & mask2) if usemin else 0
        midmax = (s1 & mask2) if usemax else mask2

        vals.append(high | midmin)
        vals.append(high | midmax | mask1)
    return IntConstraint.range(arg1.width, min(vals), max(vals))

def propagateAnd(x, y):
    return propagateBitwise(x, y, operator.__and__, False, True)

def propagateOr(x, y):
    return propagateBitwise(x, y, operator.__or__, True, False)

def propagateXor( x, y):
    return propagateBitwise(x, y, operator.__xor__, False, False)
