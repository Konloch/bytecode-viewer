import collections
import itertools

class NameGen(object):
    def __init__(self, reserved=frozenset()):
        self.counters = collections.defaultdict(itertools.count)
        self.names = set(reserved)

    def getPrefix(self, prefix, sep=''):
        newname = prefix
        while newname in self.names:
            newname = prefix + sep + str(next(self.counters[prefix]))        
        self.names.add(newname)
        return newname

def LabelGen(prefix='label'):
    for i in itertools.count():
        yield prefix + str(i)
