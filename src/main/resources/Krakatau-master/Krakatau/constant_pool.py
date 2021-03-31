import collections
import struct

# ConstantPool stores strings as strings or unicodes. They are automatically
# converted to and from modified Utf16 when reading and writing to binary

# Floats and Doubles are internally stored as integers with the same bit pattern
# Since using raw floats breaks equality testing for signed zeroes and NaNs
# cpool.getArgs/getArgsCheck will automatically convert them into Python floats

def decodeStr(s):
    return s.replace('\xc0\x80','\0').decode('utf8'),
def decodeFloat(i):
    return struct.unpack('>f', struct.pack('>i', i)) # Note: returns tuple
def decodeDouble(i):
    return struct.unpack('>d', struct.pack('>q', i))

cpoolInfo_t = collections.namedtuple('cpoolInfo_t',
                                     ['name','tag','recoverArgs'])

Utf8 = cpoolInfo_t('Utf8',1,
                  (lambda self,s:(s,)))

Class = cpoolInfo_t('Class',7,
                    (lambda self,n_id:self.getArgs(n_id)))

NameAndType = cpoolInfo_t('NameAndType',12,
                (lambda self,n,d:self.getArgs(n) + self.getArgs(d)))

Field = cpoolInfo_t('Field',9,
                (lambda self,c_id,nat_id:self.getArgs(c_id) + self.getArgs(nat_id)))

Method = cpoolInfo_t('Method',10,
                (lambda self,c_id,nat_id:self.getArgs(c_id) + self.getArgs(nat_id)))

InterfaceMethod = cpoolInfo_t('InterfaceMethod',11,
                (lambda self,c_id,nat_id:self.getArgs(c_id) + self.getArgs(nat_id)))

String = cpoolInfo_t('String',8,
                (lambda self,n_id:self.getArgs(n_id)))

Int = cpoolInfo_t('Int',3,
                  (lambda self,s:(s,)))

Long = cpoolInfo_t('Long',5,
                  (lambda self,s:(s,)))

Float = cpoolInfo_t('Float',4,
                  (lambda self,s:decodeFloat(s)))

Double = cpoolInfo_t('Double',6,
                  (lambda self,s:decodeDouble(s)))

MethodHandle = cpoolInfo_t('MethodHandle',15,
                (lambda self,t,n_id:(t,)+self.getArgs(n_id)))

MethodType = cpoolInfo_t('MethodType',16,
                (lambda self,n_id:self.getArgs(n_id)))

InvokeDynamic = cpoolInfo_t('InvokeDynamic',18,
                (lambda self,bs_id,nat_id:(bs_id,) + self.getArgs(nat_id)))

cpoolTypes = [Utf8, Class, NameAndType, Field, Method, InterfaceMethod,
              String, Int, Long, Float, Double,
              MethodHandle, MethodType, InvokeDynamic]
name2Type = {t.name:t for t in cpoolTypes}
tag2Type = {t.tag:t for t in cpoolTypes}

class ConstPool(object):
    def __init__(self, initialData=((None,None),)):
        self.pool = []
        self.reserved = set()
        self.available = set()

        for tag, val in initialData:
            if tag is None:
                self.addEmptySlot()
            else:
                t = tag2Type[tag]
                if t.name == 'Utf8':
                    val = decodeStr(*val)
                self.pool.append((t.name, val))

    def addEmptySlot(self):
        self.pool.append((None, None))

    def getArgs(self, i):
        if not (i >= 0 and i<len(self.pool)):
            raise IndexError('Constant pool index {} out of range'.format(i))
        if self.pool[i][0] is None:
            raise IndexError('Constant pool index {} invalid'.format(i))

        name, val = self.pool[i]
        t = name2Type[name]
        return t.recoverArgs(self, *val)

    def getArgsCheck(self, typen, index):
        # if (self.pool[index][0] != typen):
        #     raise KeyError('Constant pool index {} has incorrect type {}'.format(index, typen))
        val = self.getArgs(index)
        return val if len(val) > 1 else val[0]

    def getType(self, index): return self.pool[index][0]
