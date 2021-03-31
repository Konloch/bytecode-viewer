from ...verifier.descriptors import parseMethodDescriptor

from .. import constraints, excepttypes, objtypes
from ..constraints import ObjectConstraint, returnOrThrow, throw
from ..ssa_types import SSA_OBJECT, verifierToSSAType

from .base import BaseOp

class Invoke(BaseOp):
    has_side_effects = True

    def __init__(self, parent, instr, info, args, isThisCtor, target_tt):
        super(Invoke, self).__init__(parent, args, makeException=True)

        self.instruction = instr
        self.target, self.name, self.desc = info
        self.isThisCtor = isThisCtor # whether this is a ctor call for the current class
        self.target_tt = target_tt
        vtypes = parseMethodDescriptor(self.desc)[1]

        dtype = None
        if vtypes:
            stype = verifierToSSAType(vtypes[0])
            dtype = objtypes.verifierToSynthetic(vtypes[0])
            cat = len(vtypes)
            # clone() on an array type is known to always return that type, rather than any Object
            if self.name == "clone" and target_tt[1] > 0:
                dtype = target_tt

            self.rval = parent.makeVariable(stype, origin=self)
            self.returned = [self.rval] + [None]*(cat-1)
        else:
            self.rval, self.returned = None, []

        # just use a fixed constraint until we can do interprocedural analysis
        # output order is rval, exception, defined by BaseOp.getOutputs
        env = parent.env
        self.eout = ObjectConstraint.fromTops(env, [objtypes.ThrowableTT], [], nonnull=True)
        self.eout_npe = ObjectConstraint.fromTops(env, [excepttypes.NullPtr], [], nonnull=True)
        if self.rval is not None:
            if self.rval.type == SSA_OBJECT:
                supers, exact = objtypes.declTypeToActual(env, dtype)
                self.rout = ObjectConstraint.fromTops(env, supers, exact)
            else:
                self.rout = constraints.fromVariable(env, self.rval)
        else:
            self.rout = None

    def propagateConstraints(self, *incons):
        if self.instruction[0] != 'invokestatic' and incons[0].isConstNull():
            return throw(self.eout_npe)
        return returnOrThrow(self.rout, self.eout)

# TODO - cleanup
class InvokeDynamic(BaseOp):
    has_side_effects = True

    def __init__(self, parent, desc, args):
        super(InvokeDynamic, self).__init__(parent, args, makeException=True)
        self.desc = desc
        vtypes = parseMethodDescriptor(self.desc)[1]

        dtype = None
        if vtypes:
            stype = verifierToSSAType(vtypes[0])
            dtype = objtypes.verifierToSynthetic(vtypes[0])
            cat = len(vtypes)
            self.rval = parent.makeVariable(stype, origin=self)
            self.returned = [self.rval] + [None]*(cat-1)
        else:
            self.rval, self.returned = None, []

        # just use a fixed constraint until we can do interprocedural analysis
        # output order is rval, exception, defined by BaseOp.getOutputs
        env = parent.env
        self.eout = ObjectConstraint.fromTops(env, [objtypes.ThrowableTT], [], nonnull=True)
        if self.rval is not None:
            if self.rval.type == SSA_OBJECT:
                supers, exact = objtypes.declTypeToActual(env, dtype)
                self.rout = ObjectConstraint.fromTops(env, supers, exact)
            else:
                self.rout = constraints.fromVariable(env, self.rval)
        else:
            self.rout = None

    def propagateConstraints(self, *incons):
        return returnOrThrow(self.rout, self.eout)
