from __future__ import print_function

import collections
import math
import re

from ..classfileformat import classdata, mutf8
from ..classfileformat.reader import Reader, TruncatedStreamError
from ..util.thunk import thunk

from . import codes, token_regexes
from . import flags
from .instructions import OPNAMES, OP_CLS, OP_FMIM, OP_LBL, OP_NONE, OP_SHORT

MAX_INLINE_SIZE = 300
MAX_INDENT = 20
WORD_REGEX = re.compile(token_regexes.WORD + r'\Z')

PREFIXES = {'Utf8': 'u', 'Class': 'c', 'String': 's', 'Field': 'f', 'Method': 'm', 'InterfaceMethod': 'im', 'NameAndType': 'nat', 'MethodHandle': 'mh', 'MethodType': 'mt', 'InvokeDynamic': 'id'}

class DisassemblyError(Exception):
    pass

def reprbytes(b):
    # repr will have b in python3 but not python2
    return 'b' + repr(b).lstrip('b')

def isword(s):
    try:
        s = s.decode('ascii')
    except UnicodeDecodeError:
        return False
    return WORD_REGEX.match(s) and s not in flags.FLAGS

def format_string(s):
    try:
        u = mutf8.decode(s)
    except UnicodeDecodeError:
        print('Warning, invalid utf8 data!')
    else:
        if mutf8.encode(u) == s:
            return repr(u).lstrip('u')
    return reprbytes(s)

def make_signed(x, bits):
    if x >= (1 << (bits - 1)):
        x -= 1 << bits
    return x

class StackMapReader(object):
    def __init__(self):
        self.stream = None
        self.tag = -1
        self.pos = -1
        self.count = 0
        self.valid = True

    def setdata(self, r):
        if self.stream is None:
            self.stream = r
            self.count = self.u16() + 1
            self.parseNextPos()
        else:
            # Multiple StackMapTable attributes in same Code attribute
            self.valid = False

    def parseNextPos(self):
        self.count -= 1
        if self.count > 0:
            self.tag = tag = self.u8()

            if tag <= 127: # same and stack_1
                delta = tag % 64
            else: # everything else has 16bit delta field
                delta = self.u16()
            self.pos += delta + 1

    def u8(self):
        try:
            return self.stream.u8()
        except TruncatedStreamError:
            self.valid = False
            return 0

    def u16(self):
        try:
            return self.stream.u16()
        except TruncatedStreamError:
            self.valid = False
            return 0


class ReferencePrinter(object):
    def __init__(self, clsdata, roundtrip):
        self.roundtrip = roundtrip

        self.cpslots = clsdata.pool.slots
        for attr in clsdata.getattrs(b'BootstrapMethods'):
            self.bsslots = classdata.BootstrapMethodsData(attr.stream()).slots
            break
        else:
            self.bsslots = []

        # CP index 0 should always be a raw reference. Additionally, there is one case where exact
        # references are significant due to a bug in the JVM. In the InnerClasses attribute,
        # specifying the same index for inner and outer class will fail verification, but specifying
        # different indexes which point to identical class entries will pass. In this case, we force
        # references to those indexes to be raw, so they don't get merged and break the class.
        self.forcedraw = set()
        for attr in clsdata.getattrs(b'InnerClasses'):
            r = attr.stream()
            for _ in range(r.u16()):
                inner, outer, _, _ = r.u16(), r.u16(), r.u16(), r.u16()
                if inner != outer and clsdata.pool.getclsutf(inner) == clsdata.pool.getclsutf(outer):
                    self.forcedraw.add(inner)
                    self.forcedraw.add(outer)
        self.explicit_forcedraw = self.forcedraw.copy()

        # For invalid cp indices, just output raw ref instead of throwing (including 0)
        for i, slot in enumerate(self.cpslots):
            if slot.tag is None:
                self.forcedraw.add(i)
        self.forcedraw.update(range(len(self.cpslots), 65536))

        self.used = set()
        self.encoded = {}
        self.utfcounts = {}

    def _float_or_double(self, x, nmbits, nebits, suffix, nanfmt):
        nbits = nmbits + nebits + 1
        assert nbits % 32 == 0

        sbit, ebits, mbits = x >> (nbits - 1), (x >> nmbits) % (1 << nebits), x % (1 << nmbits)
        if ebits == (1 << nebits) - 1:
            result = 'NaN' if mbits else 'Infinity'
            if self.roundtrip and mbits:
                result += nanfmt.format(x)
        elif ebits == 0 and mbits == 0:
            result = '0.0'
        else:
            ebias = (1 << (nebits - 1)) - 1
            exponent = ebits - ebias - nmbits
            mantissa = mbits
            if ebits > 0:
                mantissa += 1 << nmbits
            else:
                exponent += 1

            if self.roundtrip:
                result = '0x{:X}p{}'.format(mantissa, exponent)
            else:
                result = repr(math.ldexp(mantissa, exponent))
        return '+-'[sbit] + result + suffix

    def _int(self, x): return str(make_signed(x, 32))
    def _long(self, x): return str(make_signed(x, 64)) + 'L'
    def _float(self, x): return self._float_or_double(x, 23, 8, 'f', '<0x{:08X}>')
    def _double(self, x): return self._float_or_double(x, 52, 11, '', '<0x{:016X}>')

    def _encode_utf(self, ind, wordok=True):
        try:
            return self.encoded[ind][wordok]
        except KeyError:
            s = self.cpslots[ind].data
            string = format_string(s)
            word = s.decode() if isword(s) else string
            self.encoded[ind] = [string, word]
            return word if wordok else string

    def rawref(self, ind, isbs=False):
        return '[{}{}]'.format('bs:' if isbs else '', ind)

    def symref(self, ind, isbs=False):
        self.used.add((ind, isbs))
        if isbs:
            return '[bs:_{}]'.format(ind)

        prefix = PREFIXES.get(self.cpslots[ind].tag, '_')
        return '[{}{}]'.format(prefix, ind)

    def ref(self, ind, isbs=False):
        if self.roundtrip or not isbs and ind in self.forcedraw:
            return self.rawref(ind, isbs)
        return self.symref(ind, isbs)

    def _ident(self, ind, wordok=True):
        if self.cpslots[ind].tag == 'Utf8':
            val = self._encode_utf(ind, wordok=wordok)
            if len(val) < MAX_INLINE_SIZE:
                if len(val) < 50 or self.utfcounts.get(ind, 0) < 10:
                    self.utfcounts[ind] = 1 + self.utfcounts.get(ind, 0)
                    return val

    def utfref(self, ind):
        if self.roundtrip or ind in self.forcedraw:
            return self.rawref(ind)
        temp = self._ident(ind)
        if temp is not None:
            return temp
        return self.symref(ind)

    def clsref(self, ind, tag='Class'):
        assert tag in 'Class Module Package'.split()
        if self.roundtrip or ind in self.forcedraw:
            return self.rawref(ind)
        if self.cpslots[ind].tag == tag:
            ind2 = self.cpslots[ind].refs[0]
            temp = self._ident(ind2)
            if temp is not None:
                return temp
        return self.symref(ind)

    def natref(self, ind):
        if self.roundtrip or ind in self.forcedraw:
            return self.rawref(ind)
        if self.cpslots[ind].tag == 'NameAndType':
            ind2, ind3 = self.cpslots[ind].refs
            temp = self._ident(ind2)
            if temp is not None:
                return temp + ' ' + self.utfref(ind3)
        return self.symref(ind)

    def fmimref(self, ind):
        if self.roundtrip or ind in self.forcedraw:
            return self.rawref(ind)
        if self.cpslots[ind].tag in ['Field', 'Method', 'InterfaceMethod']:
            ind2, ind3 = self.cpslots[ind].refs
            return ' '.join([self.cpslots[ind].tag, self.clsref(ind2), self.natref(ind3)])
        return self.symref(ind)

    def mhnotref(self, ind):
        slot = self.cpslots[ind]
        return codes.handle_rcodes[slot.data] + ' ' + self.taggedref(slot.refs[0], allowed=['Field', 'Method', 'InterfaceMethod'])

    def taggedconst(self, ind):
        slot = self.cpslots[ind]
        if slot.tag == 'Utf8':
            parts = [self._encode_utf(ind)]
        elif slot.tag == 'Int':
            parts = [self._int(slot.data)]
        elif slot.tag == 'Float':
            parts = [self._float(slot.data)]
        elif slot.tag == 'Long':
            parts = [self._long(slot.data)]
        elif slot.tag == 'Double':
            parts = [self._double(slot.data)]
        elif slot.tag in ['Class', 'String', 'MethodType', 'Module', 'Package']:
            parts = [self.utfref(slot.refs[0])]
        elif slot.tag in ['Field', 'Method', 'InterfaceMethod']:
            parts = [self.clsref(slot.refs[0]), self.natref(slot.refs[1])]
        elif slot.tag == 'NameAndType':
            parts = [self.utfref(slot.refs[0]), self.utfref(slot.refs[1])]
        elif slot.tag == 'MethodHandle':
            parts = [self.mhnotref(ind)]
        elif slot.tag == 'InvokeDynamic':
            parts = [self.bsref(slot.refs[0]), self.natref(slot.refs[1])]
        parts.insert(0, slot.tag)
        return ' '.join(parts)

    def taggedref(self, ind, allowed=None):
        if self.roundtrip or ind in self.forcedraw:
            return self.rawref(ind)

        if allowed is None or self.cpslots[ind].tag in allowed:
            temp = self.taggedconst(ind)
            if len(temp) < MAX_INLINE_SIZE:
                return temp
        return self.symref(ind)

    def ldcrhs(self, ind):
        if self.roundtrip or ind in self.forcedraw:
            return self.rawref(ind)
        slot = self.cpslots[ind]
        t = slot.tag

        if t == 'Int':
            return self._int(slot.data)
        elif slot.tag == 'Float':
            return self._float(slot.data)
        elif slot.tag == 'Long':
            return self._long(slot.data)
        elif slot.tag == 'Double':
            return self._double(slot.data)
        elif t == 'String':
            ind2 = self.cpslots[ind].refs[0]
            temp = self._ident(ind2, wordok=False)
            if temp is not None:
                return temp
            return self.symref(ind)
        return self.taggedref(ind, allowed=['Class', 'MethodHandle', 'MethodType'])

    def bsnotref(self, ind, tagged=False):
        slot = self.bsslots[ind]
        parts = []
        if tagged:
            parts.append('Bootstrap')

        if tagged and self.roundtrip:
            parts.append(self.rawref(slot.refs[0]))
        else:
            parts.append(self.mhnotref(slot.refs[0]))
        for bsarg in slot.refs[1:]:
            parts.append(self.taggedref(bsarg))
        parts.append(':')
        return ' '.join(parts)

    def bsref(self, ind):
        if self.roundtrip:
            return self.rawref(ind, isbs=True)
        return self.bsnotref(ind)

LabelInfos = collections.namedtuple('LabelInfos', 'defined used')
class Disassembler(object):
    def __init__(self, clsdata, out, roundtrip):
        self.roundtrip = roundtrip

        self.out = out
        self.cls = clsdata
        self.pool = clsdata.pool

        self.indentlevel = 0
        self.labels = None
        self.refprinter = ReferencePrinter(clsdata, roundtrip)

    def _getattr(a, obj, name):
        for attr in obj.attributes:
            if a.pool.getutf(attr.name) == name:
                return attr

    def sol(a, text=''):
        level = min(a.indentlevel, MAX_INDENT) * 4
        text += ' ' * (level - len(text))
        a.out(text)

    def eol(a): a.out('\n')
    def val(a, s): a.out(s + ' ')
    def int(a, x): a.val(str(x))

    def lbl(a, x):
        a.labels.used.add(x)
        a.val('L{}'.format(x))

    def try_lbl(a, x):
        if a.labels is None or x not in a.labels.defined:
            raise DisassemblyError()
        a.lbl(x)
    ###########################################################################
    def extrablankline(a): a.eol()

    def ref(a, ind, isbs=False): a.val(a.refprinter.ref(ind, isbs))
    def utfref(a, ind): a.val(a.refprinter.utfref(ind))
    def clsref(a, ind, tag='Class'): a.val(a.refprinter.clsref(ind, tag))
    def natref(a, ind): a.val(a.refprinter.natref(ind))
    def fmimref(a, ind): a.val(a.refprinter.fmimref(ind))
    def taggedbs(a, ind): a.val(a.refprinter.bsnotref(ind, tagged=True))
    def taggedconst(a, ind): a.val(a.refprinter.taggedconst(ind))
    def taggedref(a, ind): a.val(a.refprinter.taggedref(ind))
    def ldcrhs(a, ind): a.val(a.refprinter.ldcrhs(ind))

    def flags(a, access, names):
        for i in range(16):
            if access & (1 << i):
                a.val(names[1 << i])

    ###########################################################################
    ### Top level stuff (class, const defs, fields, methods) ##################
    def disassemble(a):
        cls = a.cls
        a.val('.version'), a.int(cls.version[0]), a.int(cls.version[1]), a.eol()
        a.val('.class'), a.flags(cls.access, flags.RFLAGS_CLASS), a.clsref(cls.this), a.eol()
        a.val('.super'), a.clsref(cls.super), a.eol()
        for ref in cls.interfaces:
            a.val('.implements'), a.clsref(ref), a.eol()

        for f in cls.fields:
            a.field(f)

        for m in cls.methods:
            a.method(m)

        for attr in cls.attributes:
            a.attribute(attr)

        a.constdefs()
        a.val('.end class'), a.eol()

    def field(a, f):
        a.val('.field'), a.flags(f.access, flags.RFLAGS_FIELD), a.utfref(f.name), a.utfref(f.desc)

        attrs = f.attributes[:]
        cvattr = a._getattr(f, b'ConstantValue')
        if cvattr and not a.roundtrip:
            a.val('='), a.ldcrhs(cvattr.stream().u16())
            attrs.remove(cvattr)

        if attrs:
            a.val('.fieldattributes'), a.eol()
            a.indentlevel += 1
            for attr in attrs:
                a.attribute(attr)
            a.indentlevel -= 1
            a.val('.end fieldattributes')
        a.eol()

    def method(a, m):
        a.extrablankline()
        a.val('.method'), a.flags(m.access, flags.RFLAGS_METHOD), a.utfref(m.name), a.val(':'), a.utfref(m.desc), a.eol()
        a.indentlevel += 1
        for attr in m.attributes:
            a.attribute(attr, in_method=True)
        a.indentlevel -= 1
        a.val('.end method'), a.eol()

    def constdefs(a):
        if a.roundtrip:
            for ind in range(len(a.refprinter.cpslots)):
                a.constdef(ind, False)
            for ind in range(len(a.refprinter.bsslots)):
                a.constdef(ind, True)
        else:
            assert not a.refprinter.used & a.refprinter.forcedraw
            for ind in sorted(a.refprinter.explicit_forcedraw):
                a.constdef(ind, False)

            done = set()
            while len(done) < len(a.refprinter.used):
                for ind, isbs in sorted(a.refprinter.used - done):
                    a.constdef(ind, isbs)
                    done.add((ind, isbs))

    def constdef(a, ind, isbs):
        if not isbs and a.refprinter.cpslots[ind].tag is None:
            return

        a.sol(), a.val('.bootstrap' if isbs else '.const'), a.ref(ind, isbs), a.val('=')
        if isbs:
            a.taggedbs(ind)
        else:
            a.taggedconst(ind)
        a.eol()

    ###########################################################################
    ### Bytecode ##############################################################
    def code(a, r):
        c = classdata.CodeData(r, a.pool, a.cls.version < (45, 3))
        a.val('.code'), a.val('stack'), a.int(c.stack), a.val('locals'), a.int(c.locals), a.eol()
        a.indentlevel += 1
        assert a.labels is None
        a.labels = LabelInfos(set(), set())

        stackreader = StackMapReader()
        for attr in c.attributes:
            if a.pool.getutf(attr.name) == b'StackMapTable':
                stackreader.setdata(attr.stream())

        rexcepts = c.exceptions[::-1]
        bcreader = Reader(c.bytecode)
        while bcreader.size():
            a.insline_start(bcreader.off, rexcepts, stackreader)
            a.instruction(bcreader)
        a.insline_start(bcreader.off, rexcepts, stackreader), a.eol()

        badlbls = a.labels.used - a.labels.defined
        if badlbls:
            stackreader.valid = False
            a.sol('; Labels used by invalid StackMapTable attribute'), a.eol()
            for pos in sorted(badlbls):
                a.sol('L{}'.format(pos) + ':'), a.eol()

        if stackreader.stream and (stackreader.stream.size() or stackreader.count > 0):
            stackreader.valid = False

        if not stackreader.valid:
            a.sol('.noimplicitstackmap'), a.eol()

        for attr in c.attributes:
            a.attribute(attr, use_raw_stackmap=not stackreader.valid)

        a.labels = None
        a.indentlevel -= 1
        a.sol(), a.val('.end code')

    def insline_start(a, pos, rexcepts, stackreader):
        while rexcepts and rexcepts[-1].start <= pos:
            e = rexcepts.pop()
            a.sol(), a.val('.catch'), a.clsref(e.type), a.val('from'), a.lbl(e.start)
            a.val('to'), a.lbl(e.end), a.val('using'), a.lbl(e.handler), a.eol()

        if stackreader.count > 0 and stackreader.pos == pos:
            r = stackreader
            tag = stackreader.tag
            a.extrablankline()
            a.sol(), a.val('.stack')
            if tag <= 63:
                a.val('same')
            elif tag <= 127:
                a.val('stack_1'), a.verification_type(r)
            elif tag == 247:
                a.val('stack_1_extended'), a.verification_type(r)
            elif tag < 251:
                a.val('chop'), a.int(251 - tag)
            elif tag == 251:
                a.val('same_extended')
            elif tag < 255:
                a.val('append')
                for _ in range(tag - 251):
                    a.verification_type(r)
            else:
                a.val('full')
                a.indentlevel += 1

                a.eol(), a.sol(), a.val('locals')
                for _ in range(r.u16()):
                    a.verification_type(r)
                a.eol(), a.sol(), a.val('stack')
                for _ in range(r.u16()):
                    a.verification_type(r)

                a.indentlevel -= 1
                a.eol(), a.sol(), a.val('.end stack')
            a.eol()
            stackreader.parseNextPos()

        a.sol('L{}'.format(pos) + ':')
        a.labels.defined.add(pos)

    def verification_type(a, r):
        try:
            tag = codes.vt_rcodes[r.u8()]
        except IndexError:
            r.valid = False
            a.val('Top')
            return

        a.val(tag)
        if tag == 'Object':
            a.clsref(r.u16())
        elif tag == 'Uninitialized':
            a.lbl(r.u16())

    def instruction(a, r):
        pos = r.off
        op = OPNAMES[r.u8()]
        a.val(op)

        if op in OP_LBL:
            a.lbl(pos + (r.s32() if op.endswith('_w') else r.s16()))
        elif op in OP_SHORT:
            a.int(r.u8())
        elif op in OP_CLS:
            a.clsref(r.u16())
        elif op in OP_FMIM:
            a.fmimref(r.u16())
        elif op == 'invokeinterface':
            a.fmimref(r.u16()), a.int(r.u8()), r.u8()
        elif op == 'invokedynamic':
            a.taggedref(r.u16()), r.u16()
        elif op in ['ldc', 'ldc_w', 'ldc2_w']:
            a.ldcrhs(r.u8() if op == 'ldc' else r.u16())
        elif op == 'multianewarray':
            a.clsref(r.u16()), a.int(r.u8())
        elif op == 'bipush':
            a.int(r.s8())
        elif op == 'sipush':
            a.int(r.s16())
        elif op == 'iinc':
            a.int(r.u8()), a.int(r.s8())
        elif op == 'wide':
            op2 = OPNAMES[r.u8()]
            a.val(op2), a.int(r.u16())
            if op2 == 'iinc':
                a.int(r.s16())
        elif op == 'newarray':
            a.val(codes.newarr_rcodes[r.u8()])
        elif op == 'tableswitch':
            r.getRaw((3-pos) % 4)
            default = pos + r.s32()
            low, high = r.s32(), r.s32()

            a.int(low), a.eol()
            a.indentlevel += 1
            for _ in range(high - low + 1):
                a.sol(), a.lbl(pos + r.s32()), a.eol()
            a.sol(), a.val('default'), a.val(':'), a.lbl(default), a.eol()
            a.indentlevel -= 1
        elif op == 'lookupswitch':
            r.getRaw((3-pos) % 4)
            default = pos + r.s32()

            a.eol()
            a.indentlevel += 1
            for _ in range(r.s32()):
                a.sol(), a.int(r.s32()), a.val(':'), a.lbl(pos + r.s32()), a.eol()
            a.sol(), a.val('default'), a.val(':'), a.lbl(default), a.eol()
            a.indentlevel -= 1
        else:
            assert op in OP_NONE
        a.eol()

    ###########################################################################
    ### Attributes ############################################################
    def attribute(a, attr, in_method=False, use_raw_stackmap=False):
        name = a.pool.getutf(attr.name)
        if not a.roundtrip and name in (b'BootstrapMethods', b'StackMapTable'):
            return

        # a.extrablankline()
        a.sol()
        isnamed = False
        if a.roundtrip or name is None:
            isnamed = True
            a.val('.attribute'), a.utfref(attr.name)
            if attr.wronglength:
                a.val('length'), a.int(attr.length)

        if name == b'Code' and in_method:
            a.code(attr.stream())
        elif name == b'BootstrapMethods' and a.cls.version >= (51, 0):
            a.val('.bootstrapmethods')
        elif name == b'StackMapTable' and not use_raw_stackmap:
            a.val('.stackmaptable')
        elif a.attribute_fallible(name, attr):
            pass
        else:
            print('Nonstandard attribute', name[:70], len(attr.raw))
            if not isnamed:
                a.val('.attribute'), a.utfref(attr.name)
            a.val(reprbytes(attr.raw))

        a.eol()

    def attribute_fallible(a, name, attr):
        # Temporarily buffer output so we don't get partial output if attribute disassembly fails
        # in case of failure, we'll fall back to binary output in the caller
        orig_out = a.out
        buffer_ = []
        a.out = buffer_.append

        try:
            r = attr.stream()
            if name == b'AnnotationDefault':
                a.val('.annotationdefault'), a.element_value(r)
            elif name == b'ConstantValue':
                a.val('.constantvalue'), a.ldcrhs(r.u16())
            elif name == b'Deprecated':
                a.val('.deprecated')
            elif name == b'EnclosingMethod':
                a.val('.enclosing method'), a.clsref(r.u16()), a.natref(r.u16())
            elif name == b'Exceptions':
                a.val('.exceptions')
                for _ in range(r.u16()):
                    a.clsref(r.u16())
            elif name == b'InnerClasses':
                a.indented_line_list(r, a._innerclasses_item, 'innerclasses')
            elif name == b'LineNumberTable':
                a.indented_line_list(r, a._linenumber_item, 'linenumbertable')
            elif name == b'LocalVariableTable':
                a.indented_line_list(r, a._localvariabletable_item, 'localvariabletable')
            elif name == b'LocalVariableTypeTable':
                a.indented_line_list(r, a._localvariabletable_item, 'localvariabletypetable')
            elif name == b'MethodParameters':
                a.indented_line_list(r, a._methodparams_item, 'methodparameters', bytelen=True)
            elif name == b'Module':
                a.module_attr(r)
            elif name == b'ModuleMainClass':
                a.val('.modulemainclass'), a.clsref(r.u16())
            elif name == b'ModulePackages':
                a.val('.modulepackages')
                for _ in range(r.u16()):
                    a.clsref(r.u16(), tag='Package')
            elif name in (b'RuntimeVisibleAnnotations', b'RuntimeVisibleParameterAnnotations',
                b'RuntimeVisibleTypeAnnotations', b'RuntimeInvisibleAnnotations',
                b'RuntimeInvisibleParameterAnnotations', b'RuntimeInvisibleTypeAnnotations'):
                a.val('.runtime')
                a.val('invisible' if b'Inv' in name else 'visible')
                if b'Type' in name:
                    a.val('typeannotations'), a.eol()
                    a.indented_line_list(r, a.type_annotation_line, 'runtime', False)
                elif b'Parameter' in name:
                    a.val('paramannotations'), a.eol()
                    a.indented_line_list(r, a.param_annotation_line, 'runtime', False, bytelen=True)
                else:
                    a.val('annotations'), a.eol()
                    a.indented_line_list(r, a.annotation_line, 'runtime', False)

            elif name == b'Signature':
                a.val('.signature'), a.utfref(r.u16())
            elif name == b'SourceDebugExtension':
                a.val('.sourcedebugextension')
                a.val(reprbytes(attr.raw))
            elif name == b'SourceFile':
                a.val('.sourcefile'), a.utfref(r.u16())
            elif name == b'Synthetic':
                a.val('.synthetic')

            # check for extra data in the attribute
            if r.size():
                raise DisassemblyError()

        except (TruncatedStreamError, DisassemblyError):
            a.out = orig_out
            return False

        a.out = orig_out
        a.out(''.join(buffer_))
        return True

    def module_attr(a, r):
        a.val('.module'), a.clsref(r.u16(), tag='Module'), a.flags(r.u16(), flags.RFLAGS_MOD_OTHER)
        a.val('version'), a.utfref(r.u16()), a.eol()
        a.indentlevel += 1

        for _ in range(r.u16()):
            a.sol(), a.val('.requires'), a.clsref(r.u16(), tag='Module'), a.flags(r.u16(), flags.RFLAGS_MOD_REQUIRES), a.val('version'), a.utfref(r.u16()), a.eol()

        for dir_ in ('.exports', '.opens'):
            for _ in range(r.u16()):
                a.sol(), a.val(dir_), a.clsref(r.u16(), tag='Package'), a.flags(r.u16(), flags.RFLAGS_MOD_OTHER)
                count = r.u16()
                if count:
                    a.val('to')
                    for _ in range(count):
                        a.clsref(r.u16(), tag='Module')
                a.eol()

        for _ in range(r.u16()):
            a.sol(), a.val('.uses'), a.clsref(r.u16()), a.eol()

        for _ in range(r.u16()):
            a.sol(), a.val('.provides'), a.clsref(r.u16()), a.val('with')
            for _ in range(r.u16()):
                a.clsref(r.u16())
            a.eol()
        a.indentlevel -= 1
        a.sol(), a.val('.end module')

    def indented_line_list(a, r, cb, dirname, dostart=True, bytelen=False):
        if dostart:
            a.val('.' + dirname), a.eol()
        a.indentlevel += 1
        for _ in range(r.u8() if bytelen else r.u16()):
            a.sol(), cb(r), a.eol()
        a.indentlevel -= 1
        if dirname is not None:
            a.sol(), a.val('.end ' + dirname)

    def _innerclasses_item(a, r): a.clsref(r.u16()), a.clsref(r.u16()), a.utfref(r.u16()), a.flags(r.u16(), flags.RFLAGS_CLASS)
    def _linenumber_item(a, r): a.try_lbl(r.u16()), a.int(r.u16())
    def _localvariabletable_item(a, r):
        start, length, name, desc, ind = r.u16(), r.u16(), r.u16(), r.u16(), r.u16()
        a.int(ind), a.val('is'), a.utfref(name), a.utfref(desc),
        a.val('from'), a.try_lbl(start), a.val('to'), a.try_lbl(start + length)
    def _methodparams_item(a, r): a.utfref(r.u16()), a.flags(r.u16(), flags.RFLAGS_MOD_OTHER)

    ###########################################################################
    ### Annotations ###########################################################
    def annotation_line(a, r):
        a.val('.annotation'), a.annotation_contents(r), a.sol(), a.val('.end'), a.val('annotation')

    def param_annotation_line(a, r):
        a.indented_line_list(r, a.annotation_line, 'paramannotation')

    def type_annotation_line(a, r):
        a.val('.typeannotation')
        a.indentlevel += 1
        a.ta_target_info(r) # Note: begins on same line as .typeannotation
        a.ta_target_path(r)
        a.sol(), a.annotation_contents(r),
        a.indentlevel -= 1
        a.sol(), a.val('.end'), a.val('typeannotation')

    def ta_target_info(a, r):
        tag = r.u8()
        a.int(tag)
        if tag <= 0x01:
            a.val('typeparam'), a.int(r.u8())
        elif tag <= 0x10:
            a.val('super'), a.int(r.u16())
        elif tag <= 0x12:
            a.val('typeparambound'), a.int(r.u8()), a.int(r.u8())
        elif tag <= 0x15:
            a.val('empty')
        elif tag <= 0x16:
            a.val('methodparam'), a.int(r.u8())
        elif tag <= 0x17:
            a.val('throws'), a.int(r.u16())
        elif tag <= 0x41:
            a.val('localvar'), a.eol()
            a.indented_line_list(r, a._localvarrange, 'localvar', False)
        elif tag <= 0x42:
            a.val('catch'), a.int(r.u16())
        elif tag <= 0x46:
            a.val('offset'), a.try_lbl(r.u16())
        else:
            a.val('typearg'), a.try_lbl(r.u16()), a.int(r.u8())
        a.eol()

    def _localvarrange(a, r):
        start, length, index = r.u16(), r.u16(), r.u16()
        if start == length == 0xFFFF: # WTF, Java?
            a.val('nowhere')
        else:
            a.val('from'), a.try_lbl(start), a.val('to'), a.try_lbl(start + length)
        a.int(index)

    def ta_target_path(a, r):
        a.sol(), a.indented_line_list(r, a._type_path_segment, 'typepath', bytelen=True), a.eol()

    def _type_path_segment(a, r):
        a.int(r.u8()), a.int(r.u8())

    # The following are recursive and can be nested arbitrarily deep,
    # so we use generators and a thunk to avoid the Python stack limit.
    def element_value(a, r): thunk(a._element_value(r))
    def annotation_contents(a, r): thunk(a._annotation_contents(r))

    def _element_value(a, r):
        tag = codes.et_rtags.get(r.u8())
        if tag is None:
            raise DisassemblyError()
        a.val(tag)
        if tag == 'annotation':
            (yield a._annotation_contents(r)), a.sol(), a.val('.end'), a.val('annotation')
        elif tag == 'array':
            a.eol()
            a.indentlevel += 1
            for _ in range(r.u16()):
                a.sol(), (yield a._element_value(r)), a.eol()
            a.indentlevel -= 1
            a.sol(), a.val('.end'), a.val('array')
        elif tag == 'enum':
            a.utfref(r.u16()), a.utfref(r.u16())
        elif tag == 'class' or tag == 'string':
            a.utfref(r.u16())
        else:
            a.ldcrhs(r.u16())

    def _annotation_contents(a, r):
        a.utfref(r.u16()), a.eol()
        a.indentlevel += 1
        for _ in range(r.u16()):
            a.sol(), a.utfref(r.u16()), a.val('='), (yield a._element_value(r)), a.eol()
        a.indentlevel -= 1
