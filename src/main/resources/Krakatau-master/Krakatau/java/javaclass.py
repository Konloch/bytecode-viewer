import struct

from ..ssa import objtypes
from ..verifier.descriptors import parseFieldDescriptor

from . import ast, ast2, javamethod, throws
from .reserved import reserved_identifiers

def loadConstValue(cpool, index):
    entry_type = cpool.pool[index][0]
    args = cpool.getArgs(index)

    # Note: field constant values cannot be class literals
    tt = {'Int':objtypes.IntTT, 'Long':objtypes.LongTT,
        'Float':objtypes.FloatTT, 'Double':objtypes.DoubleTT,
        'String':objtypes.StringTT}[entry_type]
    return ast.Literal(tt, args[0]).fixLiterals()

def _getField(field):
    flags = [x.lower() for x in sorted(field.flags) if x not in ('SYNTHETIC','ENUM')]
    desc = field.descriptor
    dtype = objtypes.verifierToSynthetic(parseFieldDescriptor(desc, unsynthesize=False)[0])

    initexpr = None
    if field.static:
        cpool = field.class_.cpool
        const_attrs = [data for name,data in field.attributes if name == 'ConstantValue']
        if const_attrs:
            assert len(const_attrs) == 1
            data = const_attrs[0]
            index = struct.unpack('>h', data)[0]
            initexpr = loadConstValue(cpool, index)
    return ast2.FieldDef(' '.join(flags), ast.TypeName(dtype), field.class_, field.name, desc, initexpr)

def _getMethod(method, cb, forbidden_identifiers, skip_errors):
    try:
        graph = cb(method) if method.code is not None else None
        print 'Decompiling method', method.name.encode('utf8'), method.descriptor.encode('utf8')
        code_ast = javamethod.generateAST(method, graph, forbidden_identifiers)
        return code_ast
    except Exception as e:
        if not skip_errors:
            raise

        import traceback
        message = traceback.format_exc()
        code_ast = javamethod.generateAST(method, None, forbidden_identifiers)
        code_ast.comments.add(message)
        print message
        return code_ast

# Method argument allows decompilng only a single method, primarily useful for debugging
def generateAST(cls, cb, skip_errors, method=None, add_throws=False):
    methods = cls.methods if method is None else [cls.methods[method]]
    fi = set(reserved_identifiers)
    for field in cls.fields:
        fi.add(field.name)
    forbidden_identifiers = frozenset(fi)

    myflags = [x.lower() for x in sorted(cls.flags) if x not in ('INTERFACE','SUPER','SYNTHETIC','ANNOTATION','ENUM')]
    isInterface = 'INTERFACE' in cls.flags

    superc = cls.supername
    interfaces = [cls.cpool.getArgsCheck('Class', index) for index in cls.interfaces_raw] # todo - change when class actually loads interfaces

    field_defs = [_getField(f) for f in cls.fields]
    method_defs = [_getMethod(m, cb, forbidden_identifiers, skip_errors) for m in methods]
    if add_throws:
        throws.addSingle(cls.env, method_defs)
    return ast2.ClassDef(' '.join(myflags), isInterface, cls.name, superc, interfaces, field_defs, method_defs)
