from .attributes_raw import fixAttributeNames

class Field(object):
    flagVals = {'PUBLIC':0x0001,
                'PRIVATE':0x0002,
                'PROTECTED':0x0004,
                'STATIC':0x0008,
                'FINAL':0x0010,
                'VOLATILE':0x0040,
                'TRANSIENT':0x0080,
                'SYNTHETIC':0x1000,
                'ENUM':0x4000,
                }

    def __init__(self, data, classFile, keepRaw):
        self.class_ = classFile
        cpool = self.class_.cpool

        flags, name_id, desc_id, attributes_raw = data

        self.name = cpool.getArgsCheck('Utf8', name_id)
        self.descriptor = cpool.getArgsCheck('Utf8', desc_id)
        self.attributes = fixAttributeNames(attributes_raw, cpool)

        self.flags = set(name for name,mask in Field.flagVals.items() if (mask & flags))
        self.static = 'STATIC' in self.flags
        if keepRaw:
            self.attributes_raw = attributes_raw
            self.name_id, self.desc_id = name_id, desc_id
