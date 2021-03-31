from ..functionbase import SSAFunctionBase
from ..ssa_types import SSA_OBJECT

class BaseOp(SSAFunctionBase):
    has_side_effects = False

    def __init__(self, parent, arguments, makeException=False):
        super(BaseOp, self).__init__(parent, arguments)

        self.rval = None
        self.outException = None

        if makeException:
            self.outException = parent.makeVariable(SSA_OBJECT, origin=self)

    def getOutputs(self):
        return self.rval, self.outException

    def removeOutput(self, var):
        outs = self.rval, self.outException
        assert var is not None and var in outs
        self.rval, self.outException = [(x if x != var else None) for x in outs]

    def replaceOutVars(self, vardict):
        self.rval, self.outException = map(vardict.get, (self.rval, self.outException))

    # Given input constraints, return constraints on outputs. Output is (rval, exception)
    # With None returned for unused or impossible values. This should only be defined if it is
    # actually implemented.
    # def propagateConstraints(self, *cons):
