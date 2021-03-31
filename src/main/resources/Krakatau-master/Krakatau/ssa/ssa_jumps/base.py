import copy

from ..functionbase import SSAFunctionBase

class BaseJump(SSAFunctionBase):
    def __init__(self, parent, arguments=()):
        super(BaseJump, self).__init__(parent,arguments)

    def replaceBlocks(self, blockDict):
        assert not self.getSuccessors()

    def getNormalSuccessors(self): return []
    def getExceptSuccessors(self): return []
    def getSuccessors(self): return self.getNormalSuccessors() + self.getExceptSuccessors()
    def getSuccessorPairs(self): return [(x,False) for x in self.getNormalSuccessors()] + [(x,True) for x in self.getExceptSuccessors()]
    def reduceSuccessors(self, pairsToRemove): return self

    def clone(self): return copy.copy(self) # overriden by classes which need to do a deep copy
