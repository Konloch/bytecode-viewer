from .base import BaseOp

class Phi(object):
    __slots__ = 'block dict rval'.split()
    has_side_effects = False

    def __init__(self, block, rval):
        self.block = block # used in constraint propagation
        self.dict = {}
        self.rval = rval
        assert rval is not None and rval.origin is None
        rval.origin = self

    def add(self, key, val):
        assert key not in self.dict
        assert val.type == self.rval.type
        assert val is not None
        self.dict[key] = val

    @property
    def params(self): return [self.dict[k] for k in self.block.predecessors]

    def get(self, key): return self.dict[key]
    def delete(self, key): del self.dict[key]

    # Copy these over from BaseOp so we don't need to inherit
    def replaceVars(self, rdict):
        for k in self.dict:
            self.dict[k] = rdict.get(self.dict[k], self.dict[k])

    def getOutputs(self):
        return self.rval, None, None

    def removeOutput(self, var):
        assert var == self.rval
        self.rval = None

# An extended basic block can contain multiple throwing instructions
# but the OnException jump expects a single param. The solution is
# to create a dummy op that behaves like a phi function, selecting
# among the possible thrown exceptions in the block. This is always
# the last op in block.lines when there are exceptions.
# As this is a phi, params can be variable length
class ExceptionPhi(BaseOp):
    def __init__(self, parent, params):
        BaseOp.__init__(self, parent, params, makeException=True)
