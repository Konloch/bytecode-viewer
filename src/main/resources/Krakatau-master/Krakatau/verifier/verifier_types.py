from collections import namedtuple as nt

# Define types for Inference
# Extra stores address for .new and .address and class name for object types
# Const stores value for int constants and length for exact arrays. This isn't needed for normal verification but is
# useful for optimizing the code later.
fullinfo_t = nt('fullinfo_t', ['tag','dim','extra','const'])

valid_tags = ['.'+_x for _x in 'int float double long obj new init address byte short char boolean'.split()]
valid_tags = frozenset([None] + valid_tags)

def _makeinfo(tag, dim=0, extra=None, const=None):
    assert tag in valid_tags
    return fullinfo_t(tag, dim, extra, const)

T_INVALID = _makeinfo(None)
T_INT = _makeinfo('.int')
T_FLOAT = _makeinfo('.float')
T_DOUBLE = _makeinfo('.double')
T_LONG = _makeinfo('.long')

T_NULL = _makeinfo('.obj')
T_UNINIT_THIS = _makeinfo('.init')

T_BYTE = _makeinfo('.byte')
T_SHORT = _makeinfo('.short')
T_CHAR = _makeinfo('.char')
T_BOOL = _makeinfo('.boolean') # Hotspot doesn't have a bool type, but we can use this elsewhere

# types with arguments
def T_ADDRESS(entry):
    return _makeinfo('.address', extra=entry)

def T_OBJECT(name):
    return _makeinfo('.obj', extra=name)

def T_ARRAY(baset, newDimensions=1):
    assert 0 <= baset.dim <= 255-newDimensions
    return _makeinfo(baset.tag, baset.dim+newDimensions, baset.extra)

def T_UNINIT_OBJECT(origin):
    return _makeinfo('.new', extra=origin)

def T_INT_CONST(val):
    assert -0x80000000 <= val < 0x80000000
    return _makeinfo(T_INT.tag, const=val)

OBJECT_INFO = T_OBJECT('java/lang/Object')
CLONE_INFO = T_OBJECT('java/lang/Cloneable')
SERIAL_INFO = T_OBJECT('java/io/Serializable')
THROWABLE_INFO = T_OBJECT('java/lang/Throwable')

def objOrArray(fi): # False on uninitialized
    return fi.tag == '.obj' or fi.dim > 0

def unSynthesizeType(t):
    if t in (T_BOOL, T_BYTE, T_CHAR, T_SHORT):
        return T_INT
    return t

def decrementDim(fi):
    if fi == T_NULL:
        return T_NULL
    assert fi.dim

    tag = unSynthesizeType(fi).tag if fi.dim <= 1 else fi.tag
    return _makeinfo(tag, fi.dim-1, fi.extra)

def exactArrayFrom(fi, size):
    assert fi.dim > 0
    if size >= 0:
        return _makeinfo(fi.tag, fi.dim, fi.extra, size)
    return fi

def withNoDimension(fi):
    return _makeinfo(fi.tag, 0, fi.extra)

def withNoConst(fi):
    if fi.const is None:
        return fi
    return _makeinfo(fi.tag, fi.dim, fi.extra)

def _decToObjArray(fi):
    return fi if fi.tag == '.obj' else T_ARRAY(OBJECT_INFO, fi.dim-1)

def mergeTypes(env, t1, t2):
    if t1 == t2:
        return t1

    t1 = withNoConst(t1)
    t2 = withNoConst(t2)
    if t1 == t2:
        return t1

    # non objects must match exactly
    if not objOrArray(t1) or not objOrArray(t2):
        return T_INVALID

    if t1 == T_NULL:
        return t2
    elif t2 == T_NULL:
        return t1

    if t1 == OBJECT_INFO or t2 == OBJECT_INFO:
        return OBJECT_INFO

    if t1.dim or t2.dim:
        for x in (t1,t2):
            if x in (CLONE_INFO,SERIAL_INFO):
                return x
        t1 = _decToObjArray(t1)
        t2 = _decToObjArray(t2)

        if t1.dim > t2.dim:
            t1, t2 = t2, t1

        if t1.dim == t2.dim:
            res = mergeTypes(env, withNoDimension(t1), withNoDimension(t2))
            return res if res == T_INVALID else _makeinfo('.obj', t1.dim, res.extra)
        else: # t1.dim < t2.dim
            return t1 if withNoDimension(t1) in (CLONE_INFO, SERIAL_INFO) else T_ARRAY(OBJECT_INFO, t1.dim)
    else: # neither is array
        if env.isInterface(t2.extra, forceCheck=True):
            return OBJECT_INFO
        return T_OBJECT(env.commonSuperclass(t1.extra, t2.extra))


# Make verifier types printable for easy debugging
def vt_toStr(self):   # pragma: no cover
    if self == T_INVALID:
        return '.none'
    elif self == T_NULL:
        return '.null'
    if self.tag == '.obj':
        base = self.extra
    elif self.extra is not None:
        base = '{}<{}>'.format(self.tag, self.extra)
    else:
        base = self.tag
    return base + '[]'*self.dim
fullinfo_t.__str__ = fullinfo_t.__repr__ = vt_toStr
