import copy

from .ssa_types import slots_t

class ProcInfo(object):
    def __init__(self, retblock, target):
        self.retblock = retblock
        self.target = target
        self.jsrblocks = []
        assert target is retblock.jump.target

    def __str__(self):   # pragma: no cover
        return 'Proc{}<{}>'.format(self.target.key, ', '.join(str(b.key) for b in self.jsrblocks))
    __repr__ = __str__

###########################################################################################
class ProcJumpBase(object):
    @property
    def params(self):
        return [v for v in self.input.stack + self.input.localsAsList if v is not None]
        # [v for v in self.input.stack if v] + [v for k, v in sorted(self.input.locals.items()) if v]

    def replaceBlocks(self, blockDict):
        self.target = blockDict.get(self.target, self.target)

    def getExceptSuccessors(self): return ()
    def getSuccessors(self): return self.getNormalSuccessors()
    def getSuccessorPairs(self): return [(x,False) for x in self.getNormalSuccessors()]
    def reduceSuccessors(self, pairsToRemove): return self

class ProcCallOp(ProcJumpBase):
    def __init__(self, target, fallthrough, inslots, outslots):
        self.fallthrough = fallthrough
        self.target = target
        self.input = inslots
        self.output = outslots

        for var in self.output.stack + self.output.locals.values():
            if var is not None:
                assert var.origin is None
                var.origin = self

    # def flatOutput(self): return [v for v in self.output.stack if v] + [v for k, v in sorted(self.output.locals.items()) if v]
    def flatOutput(self): return self.output.stack + self.output.localsAsList

    def getNormalSuccessors(self): return self.fallthrough, self.target

class DummyRet(ProcJumpBase):
    def __init__(self, inslots, target):
        self.target = target
        self.input = inslots

    def replaceVars(self, varDict):
        newstack = [varDict.get(v, v) for v in self.input.stack]
        newlocals = {k: varDict.get(v, v) for k, v in self.input.locals.items()}
        self.input = slots_t(stack=newstack, locals=newlocals)

    def getNormalSuccessors(self): return ()
    def clone(self): return copy.copy(self) # target and input will be replaced later by calls to replaceBlocks/Vars
