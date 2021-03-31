from .pool import Pool, utf
from .writer import Writer, Label


def writeU16Count(data, error, objects, message):
    count = len(objects)
    if count >= 1<<16:
        error('Maximum {} count is {}, found {}.'.format(message, (1<<16)-1, count), objects[-1].tok)
    data.u16(count)

class Code(object):
    def __init__(self, tok, short):
        self.tok = tok
        self.short = short
        self.locals = self.stack = 0

        self.bytecode = Writer()
        self.exceptions = Writer()
        self.exceptcount = 0

        self.stackdata = Writer()
        self.stackcount = 0
        self.stackcountpos = self.stackdata.ph16()
        self.laststackoff = -1 # first frame doesn't subtract 1 from offset

        self.stackmaptable = None
        self.dont_generate_stackmap = False
        self.attributes = []

        self.labels = {}
        self.maxcodelen = (1<<16 if short else 1<<32) - 1

    def labeldef(self, lbl, error):
        if lbl.sym in self.labels:
            error('Duplicate label definition', lbl.tok,
                  'Previous definition here:', self.labels[lbl.sym][0])
        self.labels[lbl.sym] = lbl.tok, self.bytecode.pos

    def catch(self, ref, fromlbl, tolbl, usinglbl):
        self.exceptcount += 1
        self.exceptions.lbl(fromlbl, 0, 'u16')
        self.exceptions.lbl(tolbl, 0, 'u16')
        self.exceptions.lbl(usinglbl, 0, 'u16')
        self.exceptions.ref(ref)

    def assembleNoCP(self, data, error):
        bytecode = self.bytecode

        if self.short:
            data.u8(self.stack), data.u8(self.locals), data.u16(len(bytecode))
        else:
            data.u16(self.stack), data.u16(self.locals), data.u32(len(bytecode))
        data += bytecode

        data.u16(self.exceptcount)
        data += self.exceptions

        if self.stackmaptable is None and self.stackcount > 0 and not self.dont_generate_stackmap:
            # Use arbitrary token in case we need to report errors
            self.stackmaptable = Attribute(self.tok, b'StackMapTable')
            self.attributes.append(self.stackmaptable)

        if self.stackmaptable:
            self.stackdata.setph16(self.stackcountpos, self.stackcount)
            self.stackmaptable.data = self.stackdata

        writeU16Count(data, error, self.attributes, 'attribute')
        for attr in self.attributes:
            attr.assembleNoCP(data, error)
        return data.fillLabels(self.labels, error)

class Attribute(object):
    def __init__(self, tok, name, length=None):
        assert tok
        if isinstance(name, bytes):
            name = utf(tok, name)

        self.tok = tok
        self.name = name
        self.length = length
        self.data = Writer()

    def assembleNoCP(self, data, error):
        length = len(self.data) if self.length is None else self.length
        if length >= 1<<32:
            error('Maximum attribute data length is {} bytes, got {} bytes.'.format((1<<32)-1, length), self.tok)

        data.ref(self.name)
        data.u32(length)
        data += self.data
        return data

class Method(object):
    def __init__(self, tok, access, name, desc):
        self.tok = tok
        self.access = access
        self.name = name
        self.desc = desc
        self.attributes = []

    def assembleNoCP(self, data, error):
        data.u16(self.access)
        data.ref(self.name)
        data.ref(self.desc)

        writeU16Count(data, error, self.attributes, 'attribute')
        for attr in self.attributes:
            attr.assembleNoCP(data, error)
        return data

class Field(object):
    def __init__(self, tok, access, name, desc):
        self.tok = tok
        self.access = access
        self.name = name
        self.desc = desc
        self.attributes = []

    def assembleNoCP(self, data, error):
        data.u16(self.access)
        data.ref(self.name)
        data.ref(self.desc)

        writeU16Count(data, error, self.attributes, 'attribute')
        for attr in self.attributes:
            attr.assembleNoCP(data, error)
        return data

class Class(object):
    def __init__(self):
        self.version = 49, 0
        self.access = self.this = self.super = None

        self.interfaces = []
        self.fields = []
        self.methods = []
        self.attributes = []

        self.useshortcodeattrs = False
        self.bootstrapmethods = None
        self.pool = Pool()

    def _getName(self):
        cpool = self.pool.cp
        clsind = self.this.resolved_index
        if not cpool.slots.get(clsind):
            return None

        if cpool.slots[clsind].type != 'Class':
            return None

        utfind = cpool.slots[clsind].refs[0].resolved_index
        if utfind not in cpool.slots:
            return None

        return cpool.slots[utfind].data

    def _assembleNoCP(self, error):
        beforepool = Writer()
        afterpool = Writer()

        beforepool.u32(0xCAFEBABE)
        beforepool.u16(self.version[1])
        beforepool.u16(self.version[0])

        afterpool.u16(self.access)
        afterpool.ref(self.this)
        afterpool.ref(self.super)
        writeU16Count(afterpool, error, self.interfaces, 'interface')
        for i in self.interfaces:
            afterpool.ref(i)

        writeU16Count(afterpool, error, self.fields, 'field')
        for field in self.fields:
            field.assembleNoCP(afterpool, error)

        writeU16Count(afterpool, error, self.methods, 'method')
        for method in self.methods:
            method.assembleNoCP(afterpool, error)

        attrcountpos = afterpool.ph16()
        afterbs = Writer()

        data = afterpool
        for attr in self.attributes:
            if attr is self.bootstrapmethods:
                # skip writing this attr for now and switch to after bs stream
                data = afterbs
            else:
                attr.assembleNoCP(data, error)

        return beforepool, afterpool, afterbs, attrcountpos

    def assemble(self, error):
        beforepool, afterpool, afterbs, attrcountpos = self._assembleNoCP(error)

        self.pool.cp.freezedefs(self.pool, error)
        self.pool.bs.freezedefs(self.pool, error)

        # afterpool is the only part that can contain ldcs
        assert not beforepool.refu8phs
        assert not afterbs.refu8phs
        for _, ref in afterpool.refu8phs:
            ind = ref.resolve(self.pool, error)
            if ind >= 256:
                error("Ldc references too many distinct constants in this class. If you don't want to see this message again, use ldc_w instead of ldc everywhere.", ref.tok)

        beforepool.fillRefs(self.pool, error)
        afterpool.fillRefs(self.pool, error)
        afterbs.fillRefs(self.pool, error)

        # Figure out if we need to add an implicit BootstrapMethods attribute
        self.pool.resolveIDBSRefs(error)
        if self.bootstrapmethods is None and self.pool.bs.slots:
            assert len(afterbs) == 0
            # Use arbitrary token in case we need to report errors
            self.bootstrapmethods = Attribute(self.this.tok, b'BootstrapMethods')
            self.attributes.append(self.bootstrapmethods)
        if self.bootstrapmethods is not None:
            self.bootstrapmethods.name.resolve(self.pool, error)
            assert len(self.bootstrapmethods.data) == 0

        if len(self.attributes) >= 1<<16:
            error('Maximum class attribute count is 65535, found {}.'.format(count), self.attributes[-1].tok)
        afterpool.setph16(attrcountpos, len(self.attributes))


        cpdata, bsmdata = self.pool.write(error)
        assert len(bsmdata) < (1 << 32)

        data = beforepool
        data += cpdata
        data += afterpool

        if self.bootstrapmethods is not None:
            self.bootstrapmethods.data = bsmdata
            self.bootstrapmethods.assembleNoCP(data, error)
            data.fillRefs(self.pool, error)
            data += afterbs
        else:
            assert len(afterbs) == 0

        name = self._getName()
        if name is None:
            error('Invalid reference for class name.', self.this.tok)
        return name, data.toBytes()
