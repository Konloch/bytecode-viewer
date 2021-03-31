from __future__ import division

from . import opnames

def parseInstructions(bytestream, isConstructor):
    data = bytestream
    assert data.off == 0

    instructions = {}
    while data.size() > 0:
        address = data.off
        inst = getNextInstruction(data, address)

        # replace constructor invocations with synthetic op invokeinit to simplfy things later
        if inst[0] == opnames.INVOKESPECIAL and isConstructor(inst[1]):
            inst = (opnames.INVOKEINIT,) + inst[1:]

        instructions[address] = inst
    assert data.size() == 0
    return instructions

simpleOps = {0x00:opnames.NOP, 0x01:opnames.CONSTNULL, 0x94:opnames.LCMP,
             0xbe:opnames.ARRLEN, 0xbf:opnames.THROW, 0xc2:opnames.MONENTER,
             0xc3:opnames.MONEXIT, 0x57:opnames.POP, 0x58:opnames.POP2, 0x59:opnames.DUP,
             0x5a:opnames.DUPX1, 0x5b:opnames.DUPX2, 0x5c:opnames.DUP2,
             0x5d:opnames.DUP2X1, 0x5e:opnames.DUP2X2, 0x5f:opnames.SWAP}

singleIndexOps = {0xb2:opnames.GETSTATIC,0xb3:opnames.PUTSTATIC,0xb4:opnames.GETFIELD,
            0xb5:opnames.PUTFIELD,0xb6:opnames.INVOKEVIRTUAL,0xb7:opnames.INVOKESPECIAL,
            0xb8:opnames.INVOKESTATIC, 0xbb:opnames.NEW,0xbd:opnames.ANEWARRAY,
            0xc0:opnames.CHECKCAST,0xc1:opnames.INSTANCEOF}

def getNextInstruction(data, address):
    byte = data.get('>B')

    # typecode - B,C,S, and Bool are only used for array types and sign extension
    A,B,C,D,F,I,L,S = "ABCDFIJS"
    Bool = "Z"

    if byte in simpleOps:
        inst = (simpleOps[byte],)
    elif byte in singleIndexOps:
        inst = (singleIndexOps[byte], data.get('>H'))
    elif byte <= 0x11:
        op = opnames.CONST
        if byte <= 0x08:
            t, val = I, byte - 0x03
        elif byte <= 0x0a:
            t, val = L, byte - 0x09
        elif byte <= 0x0d:
            t, val = F, float(byte - 0x0b)
        elif byte <= 0x0f:
            t, val = D, float(byte - 0x0e)
        elif byte == 0x10:
            t, val = I, data.get('>b')
        else:
            t, val = I, data.get('>h')
        inst = op, t, val
    elif byte == 0x12:
        inst = opnames.LDC, data.get('>B'), 1
    elif byte == 0x13:
        inst = opnames.LDC, data.get('>H'), 1
    elif byte == 0x14:
        inst = opnames.LDC, data.get('>H'), 2
    elif byte <= 0x2d:
        op = opnames.LOAD
        if byte <= 0x19:
            t = [I,L,F,D,A][byte - 0x15]
            val = data.get('>B')
        else:
            temp = byte - 0x1a
            t = [I,L,F,D,A][temp // 4]
            val = temp % 4
        inst = op, t, val
    elif byte <= 0x35:
        op = opnames.ARRLOAD
        t = [I,L,F,D,A,B,C,S][byte - 0x2e]
        inst = (op, t) if t != A else (opnames.ARRLOAD_OBJ,) # split object case into seperate op name to simplify things later
    elif byte <= 0x4e:
        op = opnames.STORE
        if byte <= 0x3a:
            t = [I,L,F,D,A][byte - 0x36]
            val = data.get('>B')
        else:
            temp = byte - 0x3b
            t = [I,L,F,D,A][temp // 4]
            val = temp % 4
        inst = op, t, val
    elif byte <= 0x56:
        op = opnames.ARRSTORE
        t = [I,L,F,D,A,B,C,S][byte - 0x4f]
        inst = (op, t) if t != A else (opnames.ARRSTORE_OBJ,) # split object case into seperate op name to simplify things later
    elif byte <= 0x77:
        temp = byte - 0x60
        opt = (opnames.ADD,opnames.SUB,opnames.MUL,opnames.DIV,opnames.REM,opnames.NEG)[temp//4]
        t = (I,L,F,D)[temp % 4]
        inst = opt, t
    elif byte <= 0x83:
        temp = byte - 0x78
        opt = (opnames.SHL,opnames.SHR,opnames.USHR,opnames.AND,opnames.OR,opnames.XOR)[temp//2]
        t = (I,L)[temp % 2]
        inst = opt, t
    elif byte == 0x84:
        inst = opnames.IINC, data.get('>B'), data.get('>b')
    elif byte <= 0x90:
        op = opnames.CONVERT
        pairs = ((I,L),(I,F),(I,D),(L,I),(L,F),(L,D),(F,I),(F,L),(F,D),
                (D,I),(D,L),(D,F))
        src_t, dest_t = pairs[byte - 0x85]
        inst = op, src_t, dest_t
    elif byte <= 0x93:
        op = opnames.TRUNCATE
        dest_t = [B,C,S][byte - 0x91]
        inst = op, dest_t
    elif byte <= 0x98:
        op = opnames.FCMP
        temp = byte - 0x95
        t = (F,D)[temp//2]
        NaN_val = (-1,1)[temp % 2]
        inst = op, t, NaN_val
    elif byte <= 0x9e:
        op = opnames.IF_I
        cmp_t = ('eq','ne','lt','ge','gt','le')[byte - 0x99]
        jumptarget = data.get('>h') + address
        inst = op, cmp_t, jumptarget
    elif byte <= 0xa4:
        op = opnames.IF_ICMP
        cmp_t = ('eq','ne','lt','ge','gt','le')[byte - 0x9f]
        jumptarget = data.get('>h') + address
        inst = op, cmp_t, jumptarget
    elif byte <= 0xa6:
        op = opnames.IF_ACMP
        cmp_t = ('eq','ne')[byte - 0xa5]
        jumptarget = data.get('>h') + address
        inst = op, cmp_t, jumptarget
    elif byte == 0xa7:
        inst = opnames.GOTO, data.get('>h') + address
    elif byte == 0xa8:
        inst = opnames.JSR, data.get('>h') + address
    elif byte == 0xa9:
        inst = opnames.RET, data.get('>B')
    elif byte == 0xaa: # Table Switch
        padding = data.getRaw((3-address) % 4)
        default = data.get('>i') + address
        low = data.get('>i')
        high = data.get('>i')
        assert high >= low
        numpairs = high - low + 1
        offsets = [data.get('>i') + address for _ in range(numpairs)]
        jumps = zip(range(low, high+1), offsets)
        inst = opnames.SWITCH, default, jumps
    elif byte == 0xab: # Lookup Switch
        padding = data.getRaw((3-address) % 4)
        default = data.get('>i') + address
        numpairs = data.get('>i')
        assert numpairs >= 0
        pairs = [data.get('>ii') for _ in range(numpairs)]
        jumps = [(x,(y + address)) for x,y in pairs]
        inst = opnames.SWITCH, default, jumps
    elif byte <= 0xb1:
        op = opnames.RETURN
        t = (I,L,F,D,A,None)[byte - 0xac]
        inst = op, t
    elif byte == 0xb9:
        op = opnames.INVOKEINTERFACE
        index = data.get('>H')
        count, zero = data.get('>B'), data.get('>B')
        inst = op, index, count, zero
    elif byte == 0xba:
        op = opnames.INVOKEDYNAMIC
        index = data.get('>H')
        zero = data.get('>H')
        inst = op, index, zero
    elif byte == 0xbc:
        typecode = data.get('>b')
        types = {4:Bool, 5:C, 6:F, 7:D, 8:B, 9:S, 10:I, 11:L}
        t = types.get(typecode)
        inst = opnames.NEWARRAY, t
    elif byte == 0xc4: # wide
        realbyte = data.get('>B')
        if realbyte >= 0x15 and realbyte < 0x1a:
            t = [I,L,F,D,A][realbyte - 0x15]
            inst = opnames.LOAD, t, data.get('>H')
        elif realbyte >= 0x36 and realbyte < 0x3b:
            t = [I,L,F,D,A][realbyte - 0x36]
            inst = opnames.STORE, t, data.get('>H')
        elif realbyte == 0xa9:
            inst = opnames.RET, data.get('>H')
        elif realbyte == 0x84:
            inst = opnames.IINC, data.get('>H'), data.get('>h')
        else:
            assert 0
    elif byte == 0xc5:
        op = opnames.MULTINEWARRAY
        index = data.get('>H')
        dim = data.get('>B')
        inst = op, index, dim
    elif byte <= 0xc7:
        op = opnames.IF_A
        cmp_t = ('eq','ne')[byte - 0xc6]
        jumptarget = data.get('>h') + address
        inst = op, cmp_t, jumptarget
    elif byte == 0xc8:
        inst = opnames.GOTO, data.get('>i') + address
    elif byte == 0xc9:
        inst = opnames.JSR, data.get('>i') + address
    else:
        assert 0
    return inst

def printInstruction(instr):
    if len(instr) == 1:
        return instr[0]
    elif len(instr) == 2:
        return '{}({})'.format(*instr)
    else:
        return '{}{}'.format(instr[0], instr[1:])
