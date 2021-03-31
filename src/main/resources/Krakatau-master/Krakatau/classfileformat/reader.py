import struct


class TruncatedStreamError(EOFError):
    pass


class Reader(object):
    __slots__ = ['d', 'off']
    def __init__(self, data, off=0):
        self.d = data
        self.off = off

    def done(self): return self.off >= len(self.d)
    def copy(self): return Reader(self.d, self.off)

    def u8(self): return self.get('>B')
    def s8(self): return self.get('>b')
    def u16(self): return self.get('>H')
    def s16(self): return self.get('>h')
    def u32(self): return self.get('>I')
    def s32(self): return self.get('>i')
    def u64(self): return self.get('>Q')

    # binUnpacker functions
    def get(self, fmt, forceTuple=False, peek=False):
        size = struct.calcsize(fmt)
        if self.size() < size:
            raise TruncatedStreamError()

        val = struct.unpack_from(fmt, self.d, self.off)

        if not peek:
            self.off += size
        if not forceTuple and len(val) == 1:
            val = val[0]
        return val

    def getRaw(self, num):
        if self.size() < num:
            raise TruncatedStreamError()
        val = self.d[self.off:self.off+num]
        self.off += num
        return val

    def size(self):
        return len(self.d) - self.off
