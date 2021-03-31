from __future__ import print_function

import ast
import struct
import sys

from ..classfileformat import mutf8
from ..util.thunk import thunk

from . import assembly, codes, pool
from .flags import FLAGS
from .instructions import OPNAME_TO_BYTE, OP_CLS, OP_FMIM_TO_GUESS, OP_LBL, OP_NONE, OP_SHORT
from .tokenize import AsssemblerError, Tokenizer
from .writer import Writer

def unique(vals):
    return len(vals) == len(set(vals))

def formatList(vals):
    if len(vals) > 1:
        vals[-1] = 'or ' + vals[-1]
    sep = ', ' if len(vals) > 2 else ' '
    return sep.join(vals)

def parseFloat(s, isfloat):
    if s.endswith('>'):
        return int(s.partition('<')[-1][:-1], 16)

    if s.lstrip('+-')[:2].lower() == '0x':
        f = float.fromhex(s)
    else:
        f = float(s)

    if isfloat:
        return struct.unpack('>I', struct.pack('>f', f))[0]
    else:
        return struct.unpack('>Q', struct.pack('>d', f))[0]

class Parser(object):
    def __init__(self, tokenizer):
        self.tokenizer = tokenizer
        self.tok = None
        self.consume()

        self.cls = assembly.Class()
        self.field = self.method = self.code = None

    def _next_token(self):
        return self.tokenizer.next()

    def _format_error_args(self, message, tok):
        if tok.type == 'NEWLINES' or tok.type == 'EOF':
            return message, tok.pos, tok.pos+1
        else:
            return message, tok.pos, tok.pos + len(tok.val)

    def error(self, *args):
        messages = args[0::2]
        tokens = args[1::2]
        assert len(messages) == len(tokens)
        self.tokenizer.error(*map(self._format_error_args, messages, tokens))

    def fail(self):
        assert unique(self.triedvals)
        assert unique(self.triedtypes)
        expected = sorted(self.triedtypes) + sorted(map(repr, self.triedvals))
        assert expected
        self.error('Expected {}.'.format(formatList(expected)), self.tok)

    def consume(self):
        tok = self.tok
        self.triedvals = []
        self.triedtypes = []
        self.tok = self._next_token()
        return tok

    def hasv(self, val):
        self.triedvals.append(val)
        return self.tok.val == val

    def hasany(self, vals):
        self.triedvals.extend(vals)
        return self.tok.val in vals

    def hastype(self, t):
        self.triedtypes.append(t)
        return self.tok.type == t

    def asserttype(self, t):
        self.triedtypes.append(t)
        if self.tok.type != t:
            self.fail()

    def tryv(self, val):
        if self.hasv(val):
            self.consume()
            return True
        return False

    def val(self, val):
        if not self.tryv(val):
            self.fail()

    def ateol(self): return self.hastype('NEWLINES')
    def atendtok(self): return self.hasv('.end')

    def listu8(a, w, end, callback):
        count, pos = 0, w.ph8()
        while not end():
            if count >= 255:
                a.error('Maximum 255 items.', a.tok)
            count += 1
            callback(w)
        w.setph8(pos, count)

    def list(a, w, end, callback):
        count, pos = 0, w.ph16()
        while not end():
            if count >= 65535:
                a.error('Maximum 65535 items.', a.tok)
            count += 1
            callback(w)
        w.setph16(pos, count)

    ###########################################################################
    def eol(a): a.asserttype('NEWLINES'), a.consume()

    def _rawint(a):
        a.asserttype('INT_LITERAL')
        return a.tok, ast.literal_eval(a.consume().val.lstrip('+'))

    def boundedint(a, lower, upper):
        tok, x = a._rawint()
        if not lower <= x < upper:
            a.error('Value must be in range {} <= x < {}.'.format(lower, upper), tok)
        return x

    def u8(a): return a.boundedint(0, 1<<8)
    def u16(a): return a.boundedint(0, 1<<16)
    def u32(a): return a.boundedint(0, 1<<32)
    def s8(a): return a.boundedint(-1<<7, 1<<7)
    def s16(a): return a.boundedint(-1<<15, 1<<15)
    def s32(a): return a.boundedint(-1<<31, 1<<31)

    def string(a, maxlen=65535):
        a.asserttype('STRING_LITERAL')
        tok = a.consume()
        tokval = tok.val
        if not tokval[0] in 'bB':
            tokval = 'u' + tokval
        val = ast.literal_eval(tokval)
        if not isinstance(val, bytes):
            val = mutf8.encode(val)
        if len(val) > maxlen:
            a.error('Maximum string length here is {} bytes ({} found).'.format(maxlen, len(val)), tok)
        return val

    def word(a, maxlen=65535):
        a.asserttype('WORD')
        tok = a.consume()
        val = tok.val.encode('ascii')
        if len(val) > maxlen:
            a.error('Maximum identifier length is {} bytes ({} found).'.format(maxlen, len(val)), tok)
        return val

    def identifier(a):
        if a.hastype('WORD'):
            return a.word()
        elif a.hastype('STRING_LITERAL'):
            return a.string()
        a.fail()

    def intl(a):
        a.asserttype('INT_LITERAL')
        tok = a.consume()
        x = ast.literal_eval(tok.val.lstrip('+'))
        if not -1<<31 <= x < 1<<31:
            a.error('Value does not fit into int type.', tok)
        return x % (1 << 32)

    def longl(a):
        a.asserttype('LONG_LITERAL')
        tok = a.consume()
        x = ast.literal_eval(tok.val.lstrip('+').rstrip('lL'))
        if not -1 << 63 <= x < 1 << 63:
            a.error('Value does not fit into long type.', tok)
        return x % (1 << 64)

    def floatl(a):
        a.asserttype('FLOAT_LITERAL')
        return parseFloat(a.consume().val.rstrip('fF'), True)

    def doublel(a):
        a.asserttype('DOUBLE_LITERAL')
        return parseFloat(a.consume().val, False)

    def ref(a, isbs=False):
        a.asserttype('REF')
        tok = a.consume()

        content = tok.val[1:-1]
        bootstrap = content.startswith('bs:')
        if isbs and not bootstrap:
            a.error('Expected bootstrap reference, found constant pool reference.', tok)
        elif not isbs and bootstrap:
            a.error('Expected constant pool reference, found bootstrap reference.', tok)

        val = content.rpartition(':')[2]
        try:
            index = int(val)
            if not 0 <= index < 0xFFFF: # note: strict upper bound
                a.error('Reference must be in range 0 <= x < 65535.', tok)
            return pool.Ref(tok, index=index, isbs=bootstrap)
        except ValueError:
            return pool.Ref(tok, symbol=val, isbs=bootstrap)

    def utfref(a):
        if a.hastype('REF'):
            return a.ref()
        return pool.utf(a.tok, a.identifier())

    def clsref(a, tag='Class'):
        assert tag in 'Class Module Package'.split()
        if a.hastype('REF'):
            return a.ref()
        return pool.single(tag, a.tok, a.identifier())

    def natref(a):
        if a.hastype('REF'):
            return a.ref()
        name = pool.utf(a.tok, a.identifier())
        desc = a.utfref()
        return pool.Ref(name.tok, type='NameAndType', refs=[name, desc])

    def fmimref(a, typeguess):
        # This rule requires extra lookahead
        if a.hastype('REF'):
            first = a.ref()
            # Legacy method syntax
            if a.hastype('WORD'):
                return pool.Ref(first.tok, type=typeguess, refs=[first, a.natref()]) # Krakatau v0
            return first

        elif a.hasany(['Field', 'Method', 'InterfaceMethod']):
            return a.tagged_const()

        # Legacy method syntax - attempt to support Jasmin's awful syntax too
        words = []
        while len(words) < 3 and a.hastype('WORD'):
            words.append((a.tok, a.word()))

        if 1 <= len(words) <= 2 and a.hastype('REF'): # Krakatau v0
            cls = pool.single('Class', *words[0])
            if len(words) == 2:
                name = pool.utf(*words[1])
                return pool.Ref(cls.tok, type=typeguess, refs=[cls, pool.nat(name, a.utfref())])
            return pool.Ref(cls.tok, type=typeguess, refs=[cls, a.natref()])

        if len(words) == 3: # Krakatau v0
            cls = pool.single('Class', *words[0])
            name = pool.utf(*words[1])
            desc = pool.utf(*words[2])
        elif len(words) == 2: # Jasmin field syntax
            tok, cnn = words[0]
            left, _, right = cnn.rpartition(b'/')

            cls = pool.single('Class', tok, left)
            name = pool.utf(tok, right)
            desc = pool.utf(*words[1])
        elif len(words) == 1: # Jasmin method syntax
            tok, cnnd = words[0]
            cnn, _, d = cnnd.partition(b'(')
            left, _, right = cnn.rpartition(b'/')

            cls = pool.single('Class', tok, left)
            name = pool.utf(tok, right)
            desc = pool.utf(tok, b'(' + d)
        else:
            a.fail()
        return pool.Ref(cls.tok, type=typeguess, refs=[cls, pool.nat(name, desc)])

    def bootstrapargs(a):
        while not a.hasv(':'):
            yield a.ref_or_tagged_const(methodhandle=True)
        a.val(':')

    def bsref(a):
        tok = a.tok
        if a.hastype('REF'):
            return a.ref(isbs=True)
        refs = [a.mhnotref(a.tok)]
        refs.extend(a.bootstrapargs())
        return pool.Ref(tok, type='Bootstrap', refs=refs, isbs=True)

    def mhnotref(a, tok):
        if a.hasany(codes.handle_codes):
            code = codes.handle_codes[a.consume().val]
            return pool.Ref(tok, type='MethodHandle', data=code, refs=[a.ref_or_tagged_const()])
        a.fail()

    def tagged_const(a, methodhandle=False, invokedynamic=False):
        tok = a.tok
        if a.tryv('Utf8'):
            return pool.utf(tok, a.identifier())
        elif a.tryv('Int'):
            return pool.primitive(tok.val, tok, a.intl())
        elif a.tryv('Float'):
            return pool.primitive(tok.val, tok, a.floatl())
        elif a.tryv('Long'):
            return pool.primitive(tok.val, tok, a.longl())
        elif a.tryv('Double'):
            return pool.primitive(tok.val, tok, a.doublel())
        elif a.hasany(['Class', 'String', 'MethodType', 'Module', 'Package']):
            a.consume()
            return pool.Ref(tok, type=tok.val, refs=[a.utfref()])
        elif a.hasany(['Field', 'Method', 'InterfaceMethod']):
            a.consume()
            return pool.Ref(tok, type=tok.val, refs=[a.clsref(), a.natref()])
        elif a.tryv('NameAndType'):
            return pool.Ref(tok, type=tok.val, refs=[a.utfref(), a.utfref()])
        elif methodhandle and a.tryv('MethodHandle'):
            return a.mhnotref(tok)
        elif invokedynamic and a.tryv('InvokeDynamic'):
            return pool.Ref(tok, type=tok.val, refs=[a.bsref(), a.natref()])

        elif a.tryv('Bootstrap'):
            if a.hastype('REF'):
                refs = [a.ref()]
            else:
                refs = [a.mhnotref(a.tok)]
            refs.extend(a.bootstrapargs())
            return pool.Ref(tok, type=tok.val, refs=refs, isbs=True)
        a.fail()

    def ref_or_tagged_const(a, isbs=False, methodhandle=False, invokedynamic=False):
        if a.hastype('REF'):
            ref = a.ref(isbs=isbs)
        else:
            ref = a.tagged_const(methodhandle=methodhandle, invokedynamic=invokedynamic)

        if isbs and not ref.isbs:
            a.error('Expected bootstrap reference, found constant pool reference.', ref.tok)
        elif not isbs and ref.isbs:
            a.error('Expected constant pool reference, found bootstrap reference.', ref.tok)
        return ref

    def ldc_rhs(a):
        tok = a.tok
        if a.hastype('INT_LITERAL'):
            return pool.primitive('Int', tok, a.intl())
        elif a.hastype('FLOAT_LITERAL'):
            return pool.primitive('Float', tok, a.floatl())
        elif a.hastype('LONG_LITERAL'):
            return pool.primitive('Long', tok, a.longl())
        elif a.hastype('DOUBLE_LITERAL'):
            return pool.primitive('Double', tok, a.doublel())
        elif a.hastype('STRING_LITERAL'):
            return pool.single('String', a.tok, a.string())
        return a.ref_or_tagged_const(methodhandle=True)

    def flags(a):
        flags = 0
        while a.hasany(FLAGS):
            flags |= FLAGS[a.consume().val]
        return flags

    def lbl(a):
        a.asserttype('WORD')
        if not a.tok.val.startswith('L'):
            a.error('Labels must start with L.', a.tok)
        if a.code is None:
            a.error('Labels may only be used inside of a Code attribute.', a.tok)
        return assembly.Label(a.tok, a.consume().val)

    ###########################################################################
    ### Top level stuff (class, const defs, fields, methods) ##################
    def parseClass(a):
        a.version_opt()
        a.class_start()

        # Workaround for legacy code without .end class
        while not (a.atendtok() or a.hastype('EOF')):
            a.class_item()

        if a.tryv('.end'):
            a.val('class')
            a.asserttype('NEWLINES')
        return a.cls.assemble(a.error)

    def version_opt(a):
        if a.tryv('.version'):
            a.cls.version = a.u16(), a.u16()
            a.cls.useshortcodeattrs = a.cls.version < (45, 3)
            a.eol()

    def class_start(a):
        a.val('.class')
        a.cls.access = a.flags()
        a.cls.this = a.clsref()
        a.eol()

        a.val('.super')
        a.cls.super = a.clsref()
        a.eol()

        while a.tryv('.implements'):
            a.cls.interfaces.append(a.clsref())
            a.eol()

    def class_item(a):
        a.try_const_def() or a.try_field() or a.try_method() or a.try_attribute(a.cls) or a.fail()

    def try_const_def(a):
        if a.hasany(['.const', '.bootstrap']):
            isbs = a.consume().val == '.bootstrap'
            lhs = a.ref(isbs)
            if lhs.isbs != isbs:
                a.error('Const/Bootstrap reference mismatch.', lhs.tok)
            a.val('=')

            rhs = a.ref_or_tagged_const(isbs, methodhandle=True, invokedynamic=True)
            if lhs.israw() and (rhs.israw() or rhs.issym()):
                a.error('Raw references cannot be aliased to another reference.', rhs.tok)
            a.eol()

            a.cls.pool.sub(lhs).adddef(lhs, rhs, a.error)
            return True
        return False

    def try_field(a):
        if a.hasv('.field'):
            f = a.field_start()
            a.initial_value_opt()

            if a.tryv('.fieldattributes'):
                a.eol()
                while not a.atendtok():
                    a.try_attribute(f) or a.fail()
                a.val('.end'), a.val('fieldattributes')

            a.eol()
            a.cls.fields.append(f)
            a.field = None
            return True
        return False

    def field_start(a):
        tok, flags, name, desc = a.consume(), a.flags(), a.utfref(), a.utfref()
        a.field = f = assembly.Field(tok, flags, name, desc)
        return f

    def initial_value_opt(a):
        tok = a.tok
        if a.tryv('='):
            attr = assembly.Attribute(tok, b'ConstantValue')
            attr.data.ref(a.ldc_rhs())
            a.field.attributes.append(attr)

    def try_method(a):
        if a.hasv('.method'):
            m = a.method_start()

            # Legacy syntax
            if a.hasv('.throws'):
                m.attributes.append(assembly.Attribute(a.consume(), b'Exceptions'))
                m.attributes[-1].data.u16(1)
                m.attributes[-1].data.ref(a.clsref())
                a.eol()

            if a.hasv('.limit'):
                a.legacy_method_body()
            else:
                while not a.atendtok():
                    a.try_attribute(m) or a.fail()

            a.val('.end'), a.val('method'), a.eol()
            a.cls.methods.append(m)
            a.method = None
            return True
        return False

    def method_start(a):
        tok, flags, name, _, desc, _ = a.consume(), a.flags(), a.utfref(), a.val(':'), a.utfref(), a.eol()
        a.method = m = assembly.Method(tok, flags, name, desc)
        return m

    def legacy_method_body(a):
        a.code = c = assembly.Code(a.tok, a.cls.useshortcodeattrs)
        limitfunc = a.u8 if c.short else a.u16
        while a.tryv('.limit'):
            if a.tryv('stack'):
                c.stack = limitfunc()
            elif a.tryv('locals'):
                c.locals = limitfunc()
            else:
                a.fail()
            a.eol()
        a.code_body()
        a.code = None

        attr = assembly.Attribute(c.tok, b'Code')
        c.assembleNoCP(attr.data, a.error)
        a.method.attributes.append(attr)

    ###########################################################################
    ### Bytecode ##############################################################
    def code_body(a):
        while a.try_instruction_line() or a.try_code_directive():
            pass
        while not a.atendtok():
            a.try_attribute(a.code) or a.fail()

    def try_instruction_line(a):
        haslbl = a.hastype('LABEL_DEF')
        if haslbl:
            lbl = assembly.Label(a.tok, a.consume().val.rstrip(':'))
            a.code.labeldef(lbl, a.error)

        hasinstr = a.try_instruction()
        if haslbl or hasinstr:
            a.eol()
            return True
        return False

    def try_instruction(a):
        w = a.code.bytecode
        starttok = a.tok
        op = a.tok.val

        if a.hasany(OP_NONE):
            w.u8(OPNAME_TO_BYTE[a.consume().val])
        elif a.hasany(OP_LBL):
            pos = w.pos
            w.u8(OPNAME_TO_BYTE[a.consume().val])

            dtype = 's32' if op.endswith('_w') else 's16'
            w.lbl(a.lbl(), pos, dtype)
        elif a.hasany(OP_SHORT):
            w.u8(OPNAME_TO_BYTE[a.consume().val]), w.u8(a.u8())
        elif a.hasany(OP_CLS):
            w.u8(OPNAME_TO_BYTE[a.consume().val]), w.ref(a.clsref())
        elif a.hasany(OP_FMIM_TO_GUESS):
            w.u8(OPNAME_TO_BYTE[a.consume().val]), w.ref(a.fmimref(OP_FMIM_TO_GUESS[op]))
        elif a.hasv('invokeinterface'):
            w.u8(OPNAME_TO_BYTE[a.consume().val])
            ref = a.fmimref('InterfaceMethod')
            w.ref(ref)

            if a.hastype('INT_LITERAL'):
                w.u8(a.u8()),
            else:
                a.asserttype('NEWLINES') # print more helpful error for malformed refs
                if ref.israw() or ref.issym():
                    a.error('Method descriptor must be specified inline when argument count is omitted.', ref.tok)
                ref = ref.refs[1] # NAT
                if ref.israw() or ref.issym():
                    a.error('Method descriptor must be specified inline when argument count is omitted.', ref.tok)
                ref = ref.refs[1] # utf
                if ref.israw() or ref.issym():
                    a.error('Method descriptor must be specified inline when argument count is omitted.', ref.tok)
                desc = ref.data.lstrip(b'(')
                count = 1
                while desc:
                    if desc.startswith(b'J') or desc.startswith(b'D'):
                        count += 1
                    else:
                        desc = desc.lstrip(b'[')

                    if desc.startswith(b'L'):
                        _, _, desc = desc.partition(b';')
                    elif desc.startswith(b')'):
                        break
                    else:
                        desc = desc[1:]
                    count += 1
                w.u8(count & 255)
            w.u8(0)

        elif a.hasv('invokedynamic'):
            w.u8(OPNAME_TO_BYTE[a.consume().val]), w.ref(a.ref_or_tagged_const(invokedynamic=True)), w.u16(0)
        elif a.hasany(['ldc', 'ldc_w', 'ldc2_w']):
            w.u8(OPNAME_TO_BYTE[a.consume().val])
            rhs = a.ldc_rhs()
            if op == 'ldc':
                if rhs.israw() and rhs.index >= 256:
                    a.error('Ldc index must be <= 255.', rhs.tok)
                w.refu8(rhs)
            else:
                w.ref(rhs)
        elif a.hasv('multianewarray'):
            w.u8(OPNAME_TO_BYTE[a.consume().val]), w.ref(a.clsref()), w.u8(a.u8())
        elif a.hasv('bipush'):
            w.u8(OPNAME_TO_BYTE[a.consume().val]), w.s8(a.s8())
        elif a.hasv('sipush'):
            w.u8(OPNAME_TO_BYTE[a.consume().val]), w.s16(a.s16())
        elif a.hasv('iinc'):
            w.u8(OPNAME_TO_BYTE[a.consume().val]), w.u8(a.u8()), w.s8(a.s8())
        elif a.hasv('wide'):
            w.u8(OPNAME_TO_BYTE[a.consume().val])
            if a.hasv('iinc'):
                w.u8(OPNAME_TO_BYTE[a.consume().val]), w.u16(a.u16()), w.s16(a.s16())
            elif a.hasany(OP_SHORT):
                w.u8(OPNAME_TO_BYTE[a.consume().val]), w.u16(a.u16())
            else:
                a.fail()
        elif a.hasv('newarray'):
            w.u8(OPNAME_TO_BYTE[a.consume().val])
            if a.hasany(codes.newarr_codes):
                w.u8(codes.newarr_codes[a.consume().val])
            else:
                a.fail()
        elif a.hasv('tableswitch'):
            pos = w.pos
            w.u8(OPNAME_TO_BYTE[a.consume().val]), w.writeBytes(b'\0' * ((3-pos) % 4))
            low = a.s32()
            a.eol()

            jumps = []
            while not a.hasv('default'):
                jumps.append(a.lbl()), a.eol()
                if low + len(jumps) - 1 > (1 << 31) - 1:
                    a.error('Table switch index must be at most 2147483647.', jumps[-1].tok)
            if not jumps:
                a.error('Table switch must have at least one non-default jump.', a.tok)

            _, _, default = a.val('default'), a.val(':'), a.lbl()
            w.lbl(default, pos, 's32')
            w.s32(low)
            w.s32(low + len(jumps) - 1)
            for lbl in jumps:
                w.lbl(lbl, pos, 's32')
        elif a.hasv('lookupswitch'):
            pos = w.pos
            w.u8(OPNAME_TO_BYTE[a.consume().val]), w.writeBytes(b'\0' * ((3-pos) % 4))
            a.eol()

            jumps = {}
            prevtoks = {}
            while not a.hasv('default'):
                keytok, key, _, jump, _ = a.tok, a.s32(), a.val(':'), a.lbl(), a.eol()
                if key in jumps:
                    a.error('Duplicate lookupswitch key.', keytok,
                            'Key previously defined here:', prevtoks[key])
                elif len(jumps) > 1<<31 - 1:
                    a.error('Lookup switch can have at most 2147483647 jumps.', keytok)
                jumps[key] = jump
                prevtoks[key] = keytok

            _, _, default = a.val('default'), a.val(':'), a.lbl()
            w.lbl(default, pos, 's32')
            w.s32(len(jumps))
            for key in sorted(jumps):
                w.s32(key)
                w.lbl(jumps[key], pos, 's32')
        else:
            return False

        if len(w) > a.code.maxcodelen:
            self.error('Maximum bytecode length is {} (current {}).'.format(a.code.maxcodelen, len(w)), starttok)
        return True

    def try_code_directive(a):
        tok = a.tok
        if a.tryv('.catch'):
            if a.code.exceptcount + 1 > 0xFFFF:
                a.error('Maximum 65535 exception handlers per method.', tok)
            ref, (froml, tol), _, usingl, _ = a.clsref(), a.code_range(), a.val('using'), a.lbl(), a.eol()
            a.code.catch(ref, froml, tol, usingl)
            return True
        elif a.tryv('.stack'):
            w = a.code.stackdata
            pos = a.code.bytecode.pos
            delta_offset = pos - a.code.laststackoff - 1
            frame_type = a.tok.val
            if delta_offset < 0:
                a.error('Stack frame has same offset as previous frame.', tok)

            if a.tryv('same'):
                a._check_delta(tok, frame_type, delta_offset, 63)
                w.u8(delta_offset)
            elif a.tryv('stack_1'):
                a._check_delta(tok, frame_type, delta_offset, 63)
                w.u8(delta_offset + 64)
                a.verification_type(w)
            elif a.tryv('stack_1_extended'):
                a._check_delta(tok, frame_type, delta_offset, 0xFFFF)
                w.u8(247)
                w.u16(delta_offset)
                a.verification_type(w)
            elif a.tryv('chop'):
                a._check_delta(tok, frame_type, delta_offset, 0xFFFF)
                w.u8(251 - a.boundedint(1, 4))
                w.u16(delta_offset)
            elif a.tryv('same_extended'):
                a._check_delta(tok, frame_type, delta_offset, 0xFFFF)
                w.u8(251)
                w.u16(delta_offset)
            elif a.tryv('append'):
                a._check_delta(tok, frame_type, delta_offset, 0xFFFF)

                tag = 252
                temp = Writer()
                a.verification_type(temp)
                if not a.ateol():
                    tag += 1
                    a.verification_type(temp)
                    if not a.ateol():
                        tag += 1
                        a.verification_type(temp)

                w.u8(tag)
                w.u16(delta_offset)
                w += temp
            elif a.tryv('full'):
                a._check_delta(tok, frame_type, delta_offset, 0xFFFF)
                w.u8(255)
                w.u16(delta_offset)
                a.eol(), a.val('locals'), a.list(w, a.ateol, a.verification_type)
                a.eol(), a.val('stack'), a.list(w, a.ateol, a.verification_type)
                a.eol(), a.val('.end'), a.val('stack')
            else:
                a.fail()

            a.eol()
            a.code.laststackoff = pos
            a.code.stackcount += 1
            return True
        elif a.tryv('.noimplicitstackmap'):
            a.eol()
            a.code.dont_generate_stackmap = True

        return False

    def code_range(a):
        _, start, _, end = a.val('from'), a.lbl(), a.val('to'), a.lbl()
        return start, end

    def _check_delta(a, tok, frame_type, delta_offset, maxv):
        if delta_offset > maxv:
            a.error('Stack frame type "{}" must appear at most {} bytes after the previous frame (actual offset is {}).'.format(frame_type, maxv+1, delta_offset+1), tok)

    def verification_type(a, w):
        val = a.tok.val
        if not a.hasany(codes.vt_codes):
            a.fail()

        w.u8(codes.vt_codes[a.consume().val])
        if val == 'Object':
            w.ref(a.clsref())
        elif val == 'Uninitialized':
            w.lbl(a.lbl(), 0, 'u16')

    ###########################################################################
    ### Attributes ############################################################
    def try_attribute(a, parent):
        if a.hasv('.attribute'):
            startok, name = a.consume(), a.utfref()
            if a.tryv('length'):
                attr = assembly.Attribute(startok, name, length=a.u32())
            else:
                attr = assembly.Attribute(startok, name)

            # Now get data
            if a.hastype('STRING_LITERAL'):
                attr.data.writeBytes(a.string(maxlen=0xFFFFFFFF))
            else:
                namedattr = a.maybe_named_attribute(attr)
                if namedattr is not None:
                    attr.data = namedattr.data
                else:
                    a.fail()
            a.eol()
            parent.attributes.append(attr)
            return True
        else:
            namedattr = a.maybe_named_attribute(None)
            if namedattr is not None:
                a.eol()
                parent.attributes.append(namedattr)
                return True
        return False

    def maybe_named_attribute(a, wrapper_attr):
        starttok = a.tok
        def create(name):
            attr = assembly.Attribute(starttok, name)
            return attr, attr.data

        if a.tryv('.annotationdefault'):
            attr, w = create(b'AnnotationDefault')
            a.element_value(w)
        elif a.tryv('.bootstrapmethods'):
            attr, w = create(b'BootstrapMethods')
            a.cls.bootstrapmethods = wrapper_attr or attr
        elif a.code is None and a.tryv('.code'):
            a.code = c = assembly.Code(starttok, a.cls.useshortcodeattrs)
            limitfunc = a.u8 if c.short else a.u16
            _, c.stack, _, c.locals, _ = a.val('stack'), limitfunc(), a.val('locals'), limitfunc(), a.eol()
            a.code_body()
            a.val('.end'), a.val('code')
            a.code = None
            attr, w = create(b'Code')
            c.assembleNoCP(w, a.error)
        elif a.tryv('.constantvalue'):
            attr, w = create(b'ConstantValue')
            w.ref(a.ldc_rhs())
        elif a.tryv('.deprecated'):
            attr, w = create(b'Deprecated')
        elif a.tryv('.enclosing'):
            attr, w = create(b'EnclosingMethod')
            a.val('method'), w.ref(a.clsref()), w.ref(a.natref())
        elif a.tryv('.exceptions'):
            attr, w = create(b'Exceptions')
            a.list(w, a.ateol, a._class_item)
        elif a.tryv('.innerclasses'):
            attr, w = create(b'InnerClasses')
            a.eol(), a.list(w, a.atendtok, a._innerclasses_item), a.val('.end'), a.val('innerclasses')
        elif a.tryv('.linenumbertable'):
            attr, w = create(b'LineNumberTable')
            a.eol(), a.list(w, a.atendtok, a._linenumber_item), a.val('.end'), a.val('linenumbertable')
        elif a.tryv('.localvariabletable'):
            attr, w = create(b'LocalVariableTable')
            a.eol(), a.list(w, a.atendtok, a._localvariabletable_item), a.val('.end'), a.val('localvariabletable')
        elif a.tryv('.localvariabletypetable'):
            attr, w = create(b'LocalVariableTypeTable') # reuse _localvariabletable_item func
            a.eol(), a.list(w, a.atendtok, a._localvariabletable_item), a.val('.end'), a.val('localvariabletypetable')
        elif a.tryv('.methodparameters'):
            attr, w = create(b'MethodParameters')
            a.eol(), a.listu8(w, a.atendtok, a._methodparams_item), a.val('.end'), a.val('methodparameters')
        elif a.tryv('.module'):
            print('Warning! Assembler syntax for Java 9 modules is experimental and subject to change. Please file an issue on Github if you have any opinions or feedback about the syntax')
            attr, w = create(b'Module')
            w.ref(a.utfref()), w.u16(a.flags()), a.consume(), w.ref(a.utfref()), a.eol(),
            a._mod_list(w, '.requires', a._mod_requires_item)
            a._mod_list(w, '.exports', a._mod_exports_item)
            a._mod_list(w, '.opens', a._mod_exports_item)
            a._mod_list(w, '.uses', a._mod_uses_item)
            a._mod_list(w, '.provides', a._mod_provides_item)
            a.val('.end'), a.val('module')

        elif a.tryv('.modulemainclass'):
            print('Warning! Assembler syntax for Java 9 modules is experimental and subject to change. Please file an issue on Github if you have any opinions or feedback about the syntax')
            attr, w = create(b'ModuleMainClass')
            w.ref(a.clsref())
        elif a.tryv('.modulepackages'):
            print('Warning! Assembler syntax for Java 9 modules is experimental and subject to change. Please file an issue on Github if you have any opinions or feedback about the syntax')
            attr, w = create(b'ModulePackages')
            a.list(w, a.ateol, a._package_item)

        elif a.tryv('.runtime'):
            if not a.hasany(['visible', 'invisible']):
                a.fail()
            prefix = b'Runtime' + a.consume().val.capitalize().encode()

            if a.tryv('annotations'):
                attr, w = create(prefix + b'Annotations')
                a.eol(), a.list(w, a.atendtok, a.annotation_line)
            elif a.tryv('paramannotations'):
                attr, w = create(prefix + b'ParameterAnnotations')
                a.eol(), a.listu8(w, a.atendtok, a.param_annotation_line)
            elif a.tryv('typeannotations'):
                attr, w = create(prefix + b'TypeAnnotations')
                a.eol(), a.list(w, a.atendtok, a.type_annotation_line)
            else:
                a.fail()
            a.val('.end'), a.val('runtime')
        elif a.code is not None and a.tryv('.stackmaptable'):
            attr, w = create(b'StackMapTable')
            a.code.stackmaptable = wrapper_attr or attr
        elif a.tryv('.signature'):
            attr, w = create(b'Signature')
            w.ref(a.utfref())
        elif a.tryv('.sourcedebugextension'):
            attr, w = create(b'SourceDebugExtension')
            data = a.string(maxlen=0xFFFFFFFF)
            w.writeBytes(data)
        elif a.tryv('.sourcefile'):
            attr, w = create(b'SourceFile')
            w.ref(a.utfref())
        elif a.tryv('.synthetic'):
            attr, w = create(b'Synthetic')
        else:
            return None
        return attr

    def _class_item(a, w): w.ref(a.clsref())
    def _package_item(a, w): w.ref(a.clsref(tag='Package'))
    def _module_item(a, w): w.ref(a.clsref(tag='Package'))
    def _innerclasses_item(a, w): w.ref(a.clsref()), w.ref(a.clsref()), w.ref(a.utfref()), w.u16(a.flags()), a.eol()
    def _linenumber_item(a, w): w.lbl(a.lbl(), 0, 'u16'), w.u16(a.u16()), a.eol()
    def _methodparams_item(a, w): w.ref(a.utfref()), w.u16(a.flags()), a.eol()

    def _localvariabletable_item(a, w):
        ind, _, name, desc, _range, _ = a.u16(), a.val('is'), a.utfref(), a.utfref(), a.code_range(), a.eol()
        w.lblrange(*_range), w.ref(name), w.ref(desc), w.u16(ind)

    # module attr callbacks
    def _mod_list(a, w, startdir, cb): a.list(w, lambda: not a.tryv(startdir), cb)
    def _mod_requires_item(a, w):
        w.ref(a.clsref(tag='Module')), w.u16(a.flags()), a.val('version'), w.ref(a.utfref()), a.eol()
    def _mod_exports_item(a, w):
        w.ref(a.clsref(tag='Package')), w.u16(a.flags())
        if a.tryv('to'):
            a.list(w, a.ateol, a._module_item)
        else:
            w.u16(0) # count of 0 targets
        a.eol()
    def _mod_uses_item(a, w): w.ref(a.clsref()), a.eol()
    def _mod_provides_item(a, w):
        w.ref(a.clsref()), a.val('with')
        a.list(w, a.ateol, a._class_item)
        a.eol()

    ###########################################################################
    ### Annotations ###########################################################
    def annotation_line(a, w):
        a.val('.annotation'), a.annotation_contents(w), a.val('.end'), a.val('annotation'), a.eol()

    def param_annotation_line(a, w):
        a.val('.paramannotation'), a.eol()
        a.list(w, a.atendtok, a.annotation_line)
        a.val('.end'), a.val('paramannotation'), a.eol()

    def type_annotation_line(a, w):
        a.val('.typeannotation'), a.ta_target_info(w), a.ta_target_path(w)
        a.annotation_contents(w), a.val('.end'), a.val('typeannotation'), a.eol()

    def ta_target_info(a, w):
        w.u8(a.u8())
        if a.tryv('typeparam'):
            w.u8(a.u8())
        elif a.tryv('super'):
            w.u16(a.u16())
        elif a.tryv('typeparambound'):
            w.u8(a.u8()), w.u8(a.u8())
        elif a.tryv('empty'):
            pass
        elif a.tryv('methodparam'):
            w.u8(a.u8())
        elif a.tryv('throws'):
            w.u16(a.u16())
        elif a.tryv('localvar'):
            a.eol()
            a.list(w, a.atendtok, a._localvarrange)
            a.val('.end'), a.val('localvar')
        elif a.tryv('catch'):
            w.u16(a.u16())
        elif a.tryv('offset'):
            w.lbl(a.lbl(), 0, 'u16')
        elif a.tryv('typearg'):
            w.lbl(a.lbl(), 0, 'u16'), w.u8(a.u8())
        else:
            a.fail()
        a.eol()

    def _localvarrange(a, w):
        if a.tryv('nowhere'): # WTF, Java?
            w.u16(0xFFFF), w.u16(0xFFFF)
        else:
            w.lblrange(*a.code_range())
        w.u16(a.u16()), a.eol()

    def ta_target_path(a, w):
        a.val('.typepath'), a.eol()
        a.listu8(w, a.atendtok, a._type_path_segment)
        a.val('.end'), a.val('typepath'), a.eol()

    def _type_path_segment(a, w):
        w.u8(a.u8()), w.u8(a.u8()), a.eol()

    # The following are recursive and can be nested arbitrarily deep,
    # so we use generators and a thunk to avoid the Python stack limit.
    def element_value(a, w): thunk(a._element_value(w))
    def annotation_contents(a, w): thunk(a._annotation_contents(w))

    def _element_value(a, w):
        if not a.hasany(codes.et_tags):
            a.fail()

        tag = a.consume().val
        w.u8(codes.et_tags[tag])
        if tag == 'annotation':
            (yield a._annotation_contents(w)), a.val('.end'), a.val('annotation')
        elif tag == 'array':
            a.eol()
            count, pos = 0, w.ph16()
            while not a.atendtok():
                if count >= 65535:
                    a.error('Maximum 65535 items in annotation array element.', a.tok)
                count += 1
                (yield a._element_value(w)), a.eol()
            w.setph16(pos, count)
            a.val('.end'), a.val('array')
        elif tag == 'enum':
            w.ref(a.utfref()), w.ref(a.utfref())
        elif tag == 'class' or tag == 'string':
            w.ref(a.utfref())
        else:
            w.ref(a.ldc_rhs())

    def _annotation_contents(a, w):
        w.ref(a.utfref()), a.eol()
        count, pos = 0, w.ph16()
        while not a.atendtok():
            if count >= 65535:
                a.error('Maximum 65535 items in annotation.', a.tok)
            count += 1
            w.ref(a.utfref()), a.val('='), (yield a._element_value(w)), a.eol()
        w.setph16(pos, count)

def assemble(source, filename, fatal=False):
    tokenizer = Tokenizer(source, filename)
    try:
        while not tokenizer.atend():
            name, data = Parser(tokenizer).parseClass()
            yield name, data
    except AsssemblerError:
        if fatal:
            raise
        sys.exit(1)
    except Exception:
        if fatal:
            raise
        import traceback
        traceback.print_exc()
        print('If you see this message, please file an issue at https://github.com/Storyyeller/Krakatau/issues, including the error message and the assembly file that caused the error.\n')
