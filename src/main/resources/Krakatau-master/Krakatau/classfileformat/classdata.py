import collections

from .reader import Reader

TAGS = [None, 'Utf8', None, 'Int', 'Float', 'Long', 'Double', 'Class', 'String', 'Field', 'Method', 'InterfaceMethod', 'NameAndType', None, None, 'MethodHandle', 'MethodType', None, 'InvokeDynamic', 'Module', 'Package']

SlotData = collections.namedtuple('SlotData', ['tag', 'data', 'refs'])
ExceptData = collections.namedtuple('ExceptData', ['start', 'end', 'handler', 'type'])

class ConstantPoolData(object):
    def __init__(self, r):
        self.slots = []
        self._null()

        size = r.u16()
        while len(self.slots) < size:
            self._const(r)

    def _null(self):
        self.slots.append(SlotData(None, None, None))

    def _const(self, r):
        t = TAGS[r.u8()]
        data = None
        refs = []

        if t == 'Utf8':
            data = r.getRaw(r.u16())
        elif t == 'Int' or t == 'Float':
            data = r.u32()
        elif t == 'Long' or t == 'Double':
            data = r.u64()
        elif t == 'MethodHandle':
            data = r.u8()
            refs.append(r.u16())
        elif t in ['Class', 'String', 'MethodType', 'Module', 'Package']:
            refs.append(r.u16())
        else:
            refs.append(r.u16())
            refs.append(r.u16())
        self.slots.append(SlotData(t, data, refs))
        if t in ('Long', 'Double'):
            self._null()

    def getutf(self, ind):
        if ind < len(self.slots) and self.slots[ind].tag == 'Utf8':
            return self.slots[ind].data

    def getclsutf(self, ind):
        if ind < len(self.slots) and self.slots[ind].tag == 'Class':
            return self.getutf(self.slots[ind].refs[0])

class BootstrapMethodsData(object):
    def __init__(self, r):
        self.slots = []
        for _ in range(r.u16()):
            first = r.u16()
            argcount = r.u16()
            refs = [first] + [r.u16() for _ in range(argcount)]
            self.slots.append(SlotData('Bootstrap', None, refs))

class CodeData(object):
    def __init__(self, r, pool, short):
        if short:
            self.stack, self.locals, codelen = r.u8(), r.u8(), r.u16()
        else:
            self.stack, self.locals, codelen = r.u16(), r.u16(), r.u32()

        self.bytecode = r.getRaw(codelen)
        self.exceptions = [ExceptData(r.u16(), r.u16(), r.u16(), r.u16()) for _ in range(r.u16())]
        self.attributes = [AttributeData(r) for _ in range(r.u16())]

class AttributeData(object):
    def __init__(self, r, pool=None):
        self.name, self.length = r.u16(), r.u32()

        # The JVM allows InnerClasses attributes to have a bogus length field,
        # and hence we must calculate the length from the contents
        if pool and pool.getutf(self.name) == b'InnerClasses':
            actual_length = r.copy().u16() * 8 + 2
        else:
            actual_length = self.length

        self.raw = r.getRaw(actual_length)
        self.wronglength = actual_length != self.length

    def stream(self): return Reader(self.raw)

class FieldData(object):
    def __init__(self, r):
        self.access, self.name, self.desc = r.u16(), r.u16(), r.u16()
        self.attributes = [AttributeData(r) for _ in range(r.u16())]

class MethodData(object):
    def __init__(self, r):
        self.access, self.name, self.desc = r.u16(), r.u16(), r.u16()
        self.attributes = [AttributeData(r) for _ in range(r.u16())]

class ClassData(object):
    def __init__(self, r):
        magic, minor, major = r.u32(), r.u16(), r.u16()
        self.version = major, minor

        self.pool = ConstantPoolData(r)

        self.access, self.this, self.super = r.u16(), r.u16(), r.u16()
        self.interfaces = [r.u16() for _ in range(r.u16())]
        self.fields = [FieldData(r) for _ in range(r.u16())]
        self.methods = [MethodData(r) for _ in range(r.u16())]
        self.attributes = [AttributeData(r, pool=self.pool) for _ in range(r.u16())]
        # assert r.done()

    def getattrs(self, name):
        for attr in self.attributes:
            if self.pool.getutf(attr.name) == name:
                yield attr
