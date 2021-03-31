from collections import namedtuple as nt

from .. import floatutil as fu
from ..verifier import verifier_types as vtypes

slots_t = nt('slots_t', ('locals', 'stack'))

def _localsAsList(self): return [t[1] for t in sorted(self.locals.items())]
slots_t.localsAsList = property(_localsAsList)

# types
SSA_INT = 'int', 32
SSA_LONG = 'int', 64
SSA_FLOAT = 'float', fu.FLOAT_SIZE
SSA_DOUBLE = 'float', fu.DOUBLE_SIZE
SSA_OBJECT = 'obj',

def verifierToSSAType(vtype):
    vtype_dict = {vtypes.T_INT:SSA_INT,
                vtypes.T_LONG:SSA_LONG,
                vtypes.T_FLOAT:SSA_FLOAT,
                vtypes.T_DOUBLE:SSA_DOUBLE}
    # These should never be passed in here
    assert vtype.tag not in ('.new','.init')
    vtype = vtypes.withNoConst(vtype)
    if vtypes.objOrArray(vtype):
        return SSA_OBJECT
    elif vtype in vtype_dict:
        return vtype_dict[vtype]
    return None

# Note: This is actually an Extended Basic Block. A normal basic block has to end whenever there is
# an instruction that can throw. This means that there is a seperate basic block for every throwing
# method, which causes horrible performance, especially in a large method with otherwise linear code.
# The solution is to use extended basic blocks, which are like normal basic blocks except that they
# can contain multiple throwing instructions as long as every throwing instruction has the same
# handlers. Due to the use of SSA, we also require that there are no changes to the locals between the
# first and last throwing instruction.
class BasicBlock(object):
    __slots__ = "key phis lines jump unaryConstraints predecessors inslots throwvars chpairs except_used locals_at_except".split()

    def __init__(self, key):
        self.key = key
        self.phis = None # The list of phi statements merging incoming variables
        self.lines = [] # List of operations in the block
        self.jump = None # The exit point (if, goto, etc)

        # Holds constraints (range and type information) for each variable in the block.
        # If the value is None, this variable cannot be reached
        self.unaryConstraints = None
        # List of predecessor pairs in deterministic order
        self.predecessors = []

        # temp vars used during graph creation
        self.inslots = None
        self.throwvars = []
        self.chpairs = None
        self.except_used = None
        self.locals_at_except = None

    def filterVarConstraints(self, keepvars):
        self.unaryConstraints = {k:v for k,v in self.unaryConstraints.items() if k in keepvars}

    def removePredPair(self, pair):
        self.predecessors.remove(pair)
        for phi in self.phis:
            del phi.dict[pair]

    def replacePredPair(self, oldp, newp):
        self.predecessors[self.predecessors.index(oldp)] = newp
        for phi in self.phis:
            phi.dict[newp] = phi.dict[oldp]
            del phi.dict[oldp]

    def __str__(self):   # pragma: no cover
        return 'Block ' + str(self.key)
    __repr__ = __str__
