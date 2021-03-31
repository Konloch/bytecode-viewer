import collections

from . import bytecode
from .attributes_raw import fixAttributeNames, get_attributes_raw
from .classfileformat.reader import Reader

exceptionHandlerRaw = collections.namedtuple("exceptionHandlerRaw",
                                             ["start","end","handler","type_ind"])

class Code(object):
    def __init__(self, method, bytestream, keepRaw):
        self.method = method
        self.class_ = method.class_

        # Old versions use shorter fields for stack, locals, and code length
        field_fmt = ">HHL" if self.class_.version > (45,2) else ">BBH"
        self.stack, self.locals, codelen = bytestream.get(field_fmt)
        # assert codelen > 0 and codelen < 65536
        self.bytecode_raw = bytestream.getRaw(codelen)
        self.codelen = codelen

        except_cnt = bytestream.get('>H')
        self.except_raw = [bytestream.get('>HHHH') for _ in range(except_cnt)]
        self.except_raw = [exceptionHandlerRaw(*t) for t in self.except_raw]
        attributes_raw = get_attributes_raw(bytestream)
        assert bytestream.size() == 0

        if self.except_raw:
            assert self.stack >= 1

        # print 'Parsing code for', method.name, method.descriptor, method.flags
        codestream = Reader(data=self.bytecode_raw)
        self.bytecode = bytecode.parseInstructions(codestream, self.isIdConstructor)
        self.attributes = fixAttributeNames(attributes_raw, self.class_.cpool)

        for e in self.except_raw:
            assert e.start in self.bytecode
            assert e.end == codelen or e.end in self.bytecode
            assert e.handler in self.bytecode
        if keepRaw:
            self.attributes_raw = attributes_raw

    # This is a callback passed to the bytecode parser to determine if a given method id represents a constructor
    def isIdConstructor(self, methId):
        args = self.class_.cpool.getArgsCheck('Method', methId)
        return args[1] == '<init>'


    def __str__(self):   # pragma: no cover
        lines = ['Stack: {}, Locals {}'.format(self.stack, self.locals)]

        instructions = self.bytecode
        lines += ['{}: {}'.format(i, bytecode.printInstruction(instructions[i])) for i in sorted(instructions)]
        if self.except_raw:
            lines += ['Exception Handlers:']
            lines += map(str, self.except_raw)
        return '\n'.join(lines)

class Method(object):
    flagVals = {'PUBLIC':0x0001,
                'PRIVATE':0x0002,
                'PROTECTED':0x0004,
                'STATIC':0x0008,
                'FINAL':0x0010,
                'SYNCHRONIZED':0x0020,
                'BRIDGE':0x0040,
                'VARARGS':0x0080,
                'NATIVE':0x0100,
                'ABSTRACT':0x0400,
                'STRICTFP':0x0800,
                'SYNTHETIC':0x1000,
                }

    def __init__(self, data, classFile, keepRaw):
        self.class_ = classFile
        cpool = self.class_.cpool

        flags, name_id, desc_id, attributes_raw = data

        self.name = cpool.getArgsCheck('Utf8', name_id)
        self.descriptor = cpool.getArgsCheck('Utf8', desc_id)
        # print 'Loading method ', self.name, self.descriptor
        self.attributes = fixAttributeNames(attributes_raw, cpool)

        self.flags = set(name for name, mask in Method.flagVals.items() if (mask & flags))
        # Flags are ignored for <clinit>?
        if self.name == '<clinit>':
            self.flags = set(['STATIC'])


        self._checkFlags()
        self.static = 'STATIC' in self.flags
        self.native = 'NATIVE' in self.flags
        self.abstract = 'ABSTRACT' in self.flags
        self.isConstructor = (self.name == '<init>')

        self.code = self._loadCode(keepRaw)
        if keepRaw:
            self.attributes_raw = attributes_raw
            self.name_id, self.desc_id = name_id, desc_id

    def _checkFlags(self):
        assert len(self.flags & set(('PRIVATE','PROTECTED','PUBLIC'))) <= 1
        if 'ABSTRACT' in self.flags:
            assert not self.flags & set(['SYNCHRONIZED', 'PRIVATE', 'FINAL', 'STRICT', 'STATIC', 'NATIVE'])

    def _loadCode(self, keepRaw):
        code_attrs = [a for a in self.attributes if a[0] == 'Code']
        if not (self.native or self.abstract):
            assert len(code_attrs) == 1
            code_raw = code_attrs[0][1]
            bytestream = Reader(code_raw)
            return Code(self, bytestream, keepRaw)
        assert not code_attrs
        return None
