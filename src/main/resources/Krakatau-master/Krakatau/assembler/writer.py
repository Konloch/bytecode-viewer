import collections
import struct

Label = collections.namedtuple('Label', ['tok', 'sym'])

class Writer(object):
    def __init__(self):
        self.b = bytearray()
        self.refphs = []
        self.refu8phs = []
        self.lblphs = []

        # includes lbl and manual phs but not ref phs
        self._ph8s = set()
        self._ph16s = set()
        self._ph32s = set()

    @property
    def pos(self): return len(self.b)

    def u8(self, x): self.b.append(x)
    def s8(self, x): self.b.append(x % 256)
    def u16(self, x): self.b.extend(struct.pack('>H', x))
    def s16(self, x): self.b.extend(struct.pack('>h', x))
    def u32(self, x): self.b.extend(struct.pack('>I', x))
    def s32(self, x): self.b.extend(struct.pack('>i', x))
    def u64(self, x): self.b.extend(struct.pack('>Q', x))
    def writeBytes(self, b): self.b.extend(b)

    def ref(self, ref):
        self.refphs.append((self.pos, ref))
        self.u16(0)

    def refu8(self, ref):
        self.refu8phs.append((self.pos, ref))
        self.u8(0)

    def ph8(self):
        pos = self.pos
        self.u8(0)
        self._ph8s.add(pos)
        return pos

    def ph16(self):
        pos = self.pos
        self.u16(0)
        self._ph16s.add(pos)
        return pos

    def ph32(self):
        pos = self.pos
        self.u32(0)
        self._ph32s.add(pos)
        return pos

    def lbl(self, lbl, base, dtype):
        pos = self.ph32() if dtype == 's32' else self.ph16()
        self.lblphs.append((pos, lbl, base, dtype))

    def lblrange(self, start, end):
        self.lbl(start, 0, 'u16')
        self.lbl(end, start, 'u16')

    def setph8(self, pos, x):
        assert self.b[pos] == 0
        self.b[pos] = x
        self._ph8s.remove(pos)

    def setph16(self, pos, x):
        assert self.b[pos:pos+2] == b'\0\0'
        self.b[pos:pos+2] = struct.pack('>H', x)
        self._ph16s.remove(pos)

    def setph32(self, pos, x):
        assert self.b[pos:pos+4] == b'\0\0\0\0'
        self.b[pos:pos+4] = struct.pack('>I', x)
        self._ph32s.remove(pos)

    def _getlbl(self, lbl, labels, error):
        if lbl.sym not in labels:
            error('Undefined label', lbl.tok)
        return labels[lbl.sym][1]

    def fillLabels(self, labels, error):
        for pos, lbl, base, dtype in self.lblphs:
            tok = lbl.tok
            lbl = self._getlbl(lbl, labels, error)

            # base can also be a second label
            if isinstance(base, Label):
                base = self._getlbl(base, labels, error)

            offset = lbl - base
            if dtype == 's16':
                if not -1<<15 <= offset < 1<<15:
                    error('Label offset must fit in signed 16 bit int. (offset is {})'.format(offset), tok)
                self.setph16(pos, offset % (1<<16))
            elif dtype == 'u16':
                if not 0 <= offset < 1<<16:
                    error('Label offset must fit in unsigned 16 bit int. (offset is {})'.format(offset), tok)
                self.setph16(pos, offset)
            elif dtype == 's32':
                if not -1<<31 <= offset < 1<<31:
                    error('Label offset must fit in signed 32 bit int. (offset is {})'.format(offset), tok)
                self.setph32(pos, offset % (1<<32))
            else:
                assert 0    # pragma: no cover
        self.lblphs = []
        return self

    def fillRefs(self, pool, error):
        for pos, ref in self.refu8phs:
            self.b[pos] = ref.resolve(pool, error)
        for pos, ref in self.refphs:
            self.b[pos:pos+2] = struct.pack('>H', ref.resolve(pool, error))
        self.refu8phs = []
        self.refphs = []

    def toBytes(self):
        assert not self.refphs and not self.refu8phs
        assert not self._ph8s and not self._ph16s and not self._ph32s
        return bytes(self.b)

    def __len__(self): return len(self.b)

    def __iadd__(self, other):
        # Make sure there are no manual placeholders in other
        assert len(other.lblphs) == len(other._ph8s) + len(other._ph16s) + len(other._ph32s)

        offset = self.pos
        self.b += other.b
        self.refphs.extend((pos + offset, ref) for pos, ref in other.refphs)
        self.refu8phs.extend((pos + offset, ref) for pos, ref in other.refu8phs)
        self.lblphs.extend((pos + offset, lbl, base, dtype) for pos, lbl, base, dtype in other.lblphs)
        self._ph8s.update(pos + offset for pos in other._ph8s)
        self._ph16s.update(pos + offset for pos in other._ph16s)
        self._ph32s.update(pos + offset for pos in other._ph32s)
        return self
