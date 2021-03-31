import re

# First alternative handles a single surrogate, in case input string somehow contains unmerged surrogates
NONASTRAL_REGEX = re.compile(u'[\ud800-\udfff]|[\0-\ud7ff\ue000-\uffff]+')

def encode(s):
    assert not isinstance(s, bytes)
    b = b''
    pos = 0
    while pos < len(s):
        x = ord(s[pos])
        if x >= 1<<16:
            x -= 1<<16
            high = 0xD800 + (x >> 10)
            low = 0xDC00 + (x % (1 << 10))
            b += unichr(high).encode('utf8')
            b += unichr(low).encode('utf8')
            pos += 1
        else:
            m = NONASTRAL_REGEX.match(s, pos)
            b += m.group().encode('utf8')
            pos = m.end()
    return b.replace(b'\0', b'\xc0\x80')

# Warning, decode(encode(s)) != s if s contains astral characters, as they are converted to surrogate pairs
def decode(b):
    assert isinstance(b, bytes)
    return b.replace(b'\xc0\x80', b'\0').decode('utf8')
