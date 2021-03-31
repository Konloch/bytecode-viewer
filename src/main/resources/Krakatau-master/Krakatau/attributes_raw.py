def get_attribute_raw(bytestream, ic_indices):
    name_ind, length = bytestream.get('>HL')

    # Hotspot does not actually check the attribute length of InnerClasses prior to 49.0
    # so this case requires special handling. We will keep the purported length of the
    # attribute so that it can be displayed in the disassembly. For InnerClass attributes
    # data is actually a (length, bytes) tuple, rather than storing the bytes directly
    if name_ind in ic_indices:
        count = bytestream.get('>H', peek=True)
        data = length, bytestream.getRaw(2+8*count)
    else:
        data = bytestream.getRaw(length)

    return name_ind,data

def get_attributes_raw(bytestream, ic_indices=()):
    attribute_count = bytestream.get('>H')
    return [get_attribute_raw(bytestream, ic_indices) for _ in range(attribute_count)]

def fixAttributeNames(attributes_raw, cpool):
    return [(cpool.getArgsCheck('Utf8', name_ind), data) for name_ind, data in attributes_raw]
