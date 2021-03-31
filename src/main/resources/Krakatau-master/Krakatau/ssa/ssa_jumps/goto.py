from .base import BaseJump

class Goto(BaseJump):
    def __init__(self, parent, target):
        super(Goto, self).__init__(parent, [])
        self.successors = [target]

    def replaceBlocks(self, blockDict):
        self.successors = [blockDict.get(key,key) for key in self.successors]

    def getNormalSuccessors(self):
        return self.successors

    def reduceSuccessors(self, pairsToRemove):
        if (self.successors[0], False) in pairsToRemove:
            return None
        return self
