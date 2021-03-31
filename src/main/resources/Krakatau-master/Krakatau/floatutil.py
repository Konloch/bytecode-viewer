import math

INF_MAG = 1, None
ZERO_MAG = 0, None

# Numbers are represented as (sign, (mantissa, exponent))
# For finite nonzero values, the float value is sign * mantissa * 2 ^ (exponent - mbits - 1)
# Mantissa is normalized to always be within (2 ^ mbits) <= m < (2 ^ mbits + 1) even for subnormal numbers
NAN = None,(None,None)
INF = 1,INF_MAG
NINF = -1,INF_MAG
ZERO = 1,ZERO_MAG
NZERO = -1,ZERO_MAG

# Key suitable for sorting finite (normalized) nonzero values
sortkey = lambda (s,(m,e)):(s,s*e,s*m)

# Size info for type - mantissa bits, min exponent, max exponent
FLOAT_SIZE = 23,-126,127
DOUBLE_SIZE = 52,-1022,1023

def flog(x):
    '''returns f such that 2**f <= x < 2**(f+1)'''
    assert x > 0
    return len(bin(x))-3

def roundMag(size, mag):
    '''Round (unnormalized) magnitude to nearest representable magnitude with ties going to 0 lsb'''
    mbits, emin, emax = size
    m, e = mag
    assert m >= 1
    f = flog(m)

    if e+f < emin: # subnormal
        dnmin = emin - mbits
        if e+f < (dnmin - 1):
            return ZERO_MAG
        if e > dnmin:
            m = m << (e - dnmin)
            f += (e - dnmin)
            e = dnmin
        s = dnmin - e
        i = m >> s
        r = (m - (i << s)) * 2
        h = 1 << s
        if r > h or r == h and (i&1):
            i += 1
        return i, e+s-mbits-1
    else:
        if f < mbits:
            m = m << (mbits - f)
            f = mbits
        s = f - mbits
        if (e+f) > emax:
            return INF_MAG
        i = m >> s
        r = (m - (i << s)) * 2
        h = 1 << s
        if r > h or r == h and (i&1):
            i += 1
            if i == (1<<mbits):
                i = i >> 1
                e += 1
                if e > emax:
                    return INF_MAG
        return i, e+s-mbits-1

def fromRawFloat(size, x):
    if math.isnan(x):
        return NAN
    sign = int(math.copysign(1, x))
    x = math.copysign(x, 1)

    if math.isinf(x):
        return sign, INF_MAG
    elif x == 0.0:
        return sign, ZERO_MAG
    else:
        m, e = math.frexp(x)
        m = int(m * (1<<(size[0]+1)))
        return sign, roundMag(size, (m, e))
