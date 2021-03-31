import collections, itertools

from ... import floatutil
from .. import objtypes
from .int_c import IntConstraint
from .float_c import FloatConstraint
from .obj_c import ObjectConstraint

from ..ssa_types import SSA_INT, SSA_LONG, SSA_FLOAT, SSA_DOUBLE, SSA_OBJECT

# joins become more precise (intersection), meets become more general (union)
# Join currently supports joining a max of two constraints
# Meet assumes all inputs are not None
def join(*cons):
    if None in cons:
        return None
    return cons[0].join(*cons[1:])

def meet(*cons):
    if not cons:
        return None
    return cons[0].meet(*cons[1:])

def fromConstant(env, var):
    ssa_type = var.type
    cval = var.const

    if ssa_type[0] == SSA_INT[0]:
        return IntConstraint.const(ssa_type[1], cval)
    elif ssa_type[0] == SSA_FLOAT[0]:
        xt = floatutil.fromRawFloat(ssa_type[1], cval)
        return FloatConstraint.const(ssa_type[1], xt)
    elif ssa_type[0] == SSA_OBJECT[0]:
        if var.decltype == objtypes.NullTT:
            return ObjectConstraint.constNull(env)
        return ObjectConstraint.fromTops(env, *objtypes.declTypeToActual(env, var.decltype))

_bots = {
    SSA_INT: IntConstraint.bot(SSA_INT[1]),
    SSA_LONG: IntConstraint.bot(SSA_LONG[1]),
    SSA_FLOAT: FloatConstraint.bot(SSA_FLOAT[1]),
    SSA_DOUBLE: FloatConstraint.bot(SSA_DOUBLE[1]),
}

def fromVariable(env, var):
    if var.const is not None:
        return fromConstant(env, var)
    ssa_type = var.type

    try:
        return _bots[ssa_type]
    except KeyError:
        assert ssa_type == SSA_OBJECT
        isnew = var.uninit_orig_num is not None
        if var.decltype is not None:
            if var.decltype == objtypes.NullTT:
                return ObjectConstraint.constNull(env)
            return ObjectConstraint.fromTops(env, *objtypes.declTypeToActual(env, var.decltype), nonnull=isnew)
        else:
            return ObjectConstraint.fromTops(env, [objtypes.ObjectTT], [], nonnull=isnew)

OpReturnInfo = collections.namedtuple('OpReturnInfo', ['rval', 'eval', 'must_throw'])
def returnOrThrow(rval, eval): return OpReturnInfo(rval, eval, False)
def maybeThrow(eval): return OpReturnInfo(None, eval, False)
def throw(eval): return OpReturnInfo(None, eval, True)
def return_(rval): return OpReturnInfo(rval, None, False)
