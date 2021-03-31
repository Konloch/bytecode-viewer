from .. import objtypes
from ..constraints import ObjectConstraint
from ..exceptionset import CatchSetManager, ExceptionSet

from .base import BaseJump
from .goto import Goto

class OnException(BaseJump):
    def __init__(self, parent, throwvar, chpairs, fallthrough=None):
        super(OnException, self).__init__(parent, [throwvar])
        self.default = fallthrough
        self.cs = CatchSetManager.new(parent.env, chpairs)
        self.cs.pruneKeys()

    def replaceExceptTarget(self, old, new):
        self.cs.replaceKeys({old:new})

    def replaceNormalTarget(self, old, new):
        self.default = new if self.default == old else self.default

    def replaceBlocks(self, blockDict):
        self.cs.replaceKeys(blockDict)
        if self.default is not None:
            self.default = blockDict.get(self.default, self.default)

    def reduceSuccessors(self, pairsToRemove):
        for (child, t) in pairsToRemove:
            if t:
                self.cs.mask -= self.cs.sets[child]
                del self.cs.sets[child]
            else:
                self.replaceNormalTarget(child, None)

        self.cs.pruneKeys()
        if not self.cs.sets:
            if not self.default:
                return None
            return Goto(self.parent, self.default)
        return self

    def getNormalSuccessors(self):
        return [self.default] if self.default is not None else []

    def getExceptSuccessors(self):
        return self.cs.sets.keys()

    def clone(self):
        new = super(OnException, self).clone()
        new.cs = self.cs.copy()
        return new

    ###############################################################################
    def constrainJumps(self, x):
        if x is None:
            mask = ExceptionSet.EMPTY
        else:
            mask = ExceptionSet(x.types.env, [(objtypes.className(tt),()) for tt in x.types.supers | x.types.exact])
        self.cs.newMask(mask)
        return self.reduceSuccessors([])
