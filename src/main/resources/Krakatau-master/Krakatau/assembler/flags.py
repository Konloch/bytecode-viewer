_pairs = [
    ('public', 0x0001),
    ('private', 0x0002),
    ('protected', 0x0004),
    ('static', 0x0008),
    ('final', 0x0010),
    ('super', 0x0020),
    # todo - order attributes properly by context
    ('transitive', 0x0020),
    ('open', 0x0020),
    ('synchronized', 0x0020),
    ('volatile', 0x0040),
    ('static_phase', 0x0040),
    ('bridge', 0x0040),
    ('transient', 0x0080),
    ('varargs', 0x0080),
    ('native', 0x0100),
    ('interface', 0x0200),
    ('abstract', 0x0400),
    ('strict', 0x0800),
    ('synthetic', 0x1000),
    ('annotation', 0x2000),
    ('enum', 0x4000),
    ('module', 0x8000),
    ('mandated', 0x8000),
]

FLAGS = dict(_pairs)
# Treat strictfp as flag too to reduce confusion
FLAGS['strictfp'] = FLAGS['strict']

def _make_dict(priority):
    d = {v:k for k,v in reversed(_pairs)}
    # ensure that the specified flags have priority
    for flag in priority.split():
        d[FLAGS[flag]] = flag
    return d

RFLAGS_CLASS = _make_dict('super module')
RFLAGS_FIELD = _make_dict('volatile transient')
RFLAGS_METHOD = _make_dict('synchronized bridge varargs')
RFLAGS_MOD_REQUIRES = _make_dict('transitive static_phase mandated')
RFLAGS_MOD_OTHER = _make_dict('open mandated')
