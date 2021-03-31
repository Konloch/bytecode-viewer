# double quote, backslash, and newlines are forbidden
ok_chars =  " !#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[]^_`abcdefghijklmnopqrstuvwxyz{|}~"
ok_chars = frozenset(ok_chars)

# these characters cannot use unicode escape codes due to the way Java escaping works
late_escape = {u'\u0009':r'\t', u'\u000a':r'\n', u'\u000d':r'\r', u'\u0022':r'\"', u'\u005c':r'\\'}

def escapeString(u):
    if set(u) <= ok_chars:
        return u

    escaped = []
    for c in u:
        if c in ok_chars:
            escaped.append(c)
        elif c in late_escape:
            escaped.append(late_escape[c])
        else:
            i = ord(c)
            if i <= 0xFFFF:
                escaped.append(r'\u{0:04x}'.format(i))
            else:
                i -= 0x10000
                high = 0xD800 + (i>>10)
                low = 0xDC00 + (i & 0x3FF)
                escaped.append(r'\u{0:04x}\u{1:04x}'.format(high,low))
    return ''.join(escaped)
