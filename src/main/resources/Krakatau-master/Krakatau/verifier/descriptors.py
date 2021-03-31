from .verifier_types import T_ARRAY, T_BOOL, T_BYTE, T_CHAR, T_DOUBLE, T_FLOAT, T_INT, T_INVALID, T_LONG, T_OBJECT, T_SHORT, unSynthesizeType

_cat2tops = T_LONG, T_DOUBLE

def parseFieldDescriptors(desc_str, unsynthesize=True):
    baseTypes = {'B':T_BYTE, 'C':T_CHAR, 'D':T_DOUBLE, 'F':T_FLOAT,
                 'I':T_INT, 'J':T_LONG, 'S':T_SHORT, 'Z':T_BOOL}

    fields = []
    while desc_str:
        oldlen = len(desc_str)
        desc_str = desc_str.lstrip('[')
        dim = oldlen - len(desc_str)
        if dim > 255:
            raise ValueError('Dimension {} > 255 in descriptor'.format(dim))
        if not desc_str:
            raise ValueError('Descriptor contains [s at end of string')

        if desc_str[0] == 'L':
            end = desc_str.find(';')
            if end == -1:
                raise ValueError('Unmatched L in descriptor')

            name = desc_str[1:end]
            desc_str = desc_str[end+1:]
            baset = T_OBJECT(name)
        else:
            if desc_str[0] not in baseTypes:
                raise ValueError('Unrecognized code {} in descriptor'.format(desc_str[0]))
            baset = baseTypes[desc_str[0]]
            desc_str = desc_str[1:]

        if dim:
            # Hotspot considers byte[] and bool[] identical for type checking purposes
            if unsynthesize and baset == T_BOOL:
                baset = T_BYTE
            baset = T_ARRAY(baset, dim)
        elif unsynthesize:
            # synthetics are only meaningful as basetype of an array
            # if they are by themselves, convert to int.
            baset = unSynthesizeType(baset)

        fields.append(baset)
        if baset in _cat2tops:
            fields.append(T_INVALID)
    return fields

# get a single descriptor
def parseFieldDescriptor(desc_str, unsynthesize=True):
    rval = parseFieldDescriptors(desc_str, unsynthesize)

    cat = 2 if (rval and rval[0] in _cat2tops) else 1
    if len(rval) != cat:
        raise ValueError('Incorrect number of fields in descriptor, expected {} but found {}'.format(cat, len(rval)))
    return rval

# Parse a string to get a Java Method Descriptor
def parseMethodDescriptor(desc_str, unsynthesize=True):
    if not desc_str.startswith('('):
        raise ValueError('Method descriptor does not start with (')

    # we need to split apart the argument list and return value
    # this is greatly complicated by the fact that ) is a legal
    # character that can appear in class names

    lp_pos = desc_str.rfind(')') # this case will work if return type is not an object
    if desc_str.endswith(';'):
        lbound = max(desc_str.rfind(';', 1, -1), 1)
        lp_pos = desc_str.find(')', lbound, -1)
    if lp_pos < 0 or desc_str[lp_pos] != ')':
        raise ValueError('Unable to split method descriptor into arguments and return type')

    arg_str = desc_str[1:lp_pos]
    rval_str = desc_str[lp_pos+1:]

    args = parseFieldDescriptors(arg_str, unsynthesize)
    rval = [] if rval_str == 'V' else parseFieldDescriptor(rval_str, unsynthesize)
    return args, rval

# Adds self argument for nonstatic. Constructors must be handled seperately
def parseUnboundMethodDescriptor(desc_str, target, isstatic):
    args, rval = parseMethodDescriptor(desc_str)
    if not isstatic:
        args = [T_OBJECT(target)] + args
    return args, rval
