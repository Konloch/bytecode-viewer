from __future__ import print_function

import collections
import re
import sys

from . import token_regexes as res

class AsssemblerError(Exception):
    pass

Token = collections.namedtuple('Token', 'type val pos')

TOKENS = [
    ('WHITESPACE', r'[ \t]+'),
    ('WORD', res.WORD + res.FOLLOWED_BY_WHITESPACE),
    ('DIRECTIVE', res.DIRECTIVE + res.FOLLOWED_BY_WHITESPACE),
    ('LABEL_DEF', res.LABEL_DEF + res.FOLLOWED_BY_WHITESPACE),
    ('NEWLINES', res.NEWLINES),
    ('REF', res.REF + res.FOLLOWED_BY_WHITESPACE),
    ('COLON', r':' + res.FOLLOWED_BY_WHITESPACE),
    ('EQUALS', r'=' + res.FOLLOWED_BY_WHITESPACE),
    ('INT_LITERAL', res.INT_LITERAL + res.FOLLOWED_BY_WHITESPACE),
    ('DOUBLE_LITERAL', res.FLOAT_LITERAL + res.FOLLOWED_BY_WHITESPACE),
    ('STRING_LITERAL', res.STRING_LITERAL + res.FOLLOWED_BY_WHITESPACE),
]
REGEX = re.compile('|'.join('(?P<{}>{})'.format(*pair) for pair in TOKENS), re.VERBOSE)
# For error detection
STRING_START_REGEX = re.compile(res.STRING_START)
WORD_LIKE_REGEX = re.compile(r'.\S*')

MAXLINELEN = 80
def formatError(source, filename, message, point, point2):
    try:
        start = source.rindex('\n', 0, point) + 1
    except ValueError:
        start = 0
    line_start = start

    try:
        end = source.index('\n', start) + 1
    except ValueError:    # pragma: no cover
        end = len(source) + 1

    # Find an 80 char section of the line around the point of interest to display
    temp = min(point2, point + MAXLINELEN//2)
    if temp < start + MAXLINELEN:
        end = min(end, start + MAXLINELEN)
    elif point >= end - MAXLINELEN:
        start = max(start, end - MAXLINELEN)
    else:
        mid = (point + temp) // 2
        start = max(start, mid - MAXLINELEN//2)
        end = min(end, start + MAXLINELEN)
    point2 = min(point2, end)
    assert line_start <= start <= point < point2 <= end

    pchars = [' '] * (end - start)
    for i in range(point - start, point2 - start):
        pchars[i] = '~'
    pchars[point - start] = '^'
    lineno = source[:line_start].count('\n') + 1
    colno = point - line_start + 1
    return '{}:{}:{}: {}\n{}\n{}'.format(filename, lineno, colno,
        message, source[start:end].rstrip('\n'), ''.join(pchars))

class Tokenizer(object):
    def __init__(self, source, filename):
        self.s = source
        self.pos = 0
        self.atlineend = True
        if isinstance(filename, bytes):
            filename = filename.decode()
        self.filename = filename.rpartition('/')[-1]

    def error(self, error, *notes):
        printerr = lambda s: print(s, file=sys.stderr)
        message, point, point2 = error
        printerr(formatError(self.s, self.filename, 'error: ' + message, point, point2))
        for message, point, point2 in notes:
            printerr(formatError(self.s, self.filename, 'note: ' + message, point, point2))
        raise AsssemblerError()

    def _nextsub(self):
        match = REGEX.match(self.s, self.pos)
        if match is None:
            if self.atend():
                return Token('EOF', '', self.pos)
            else:
                str_match = STRING_START_REGEX.match(self.s, self.pos)
                if str_match is not None:
                    self.error(('Invalid escape sequence or character in string literal', str_match.end(), str_match.end()+1))

                match = WORD_LIKE_REGEX.match(self.s, self.pos)
                return Token('INVALID_TOKEN', match.group(), match.start())
        assert match.start() == match.pos == self.pos

        self.pos = match.end()
        return Token(match.lastgroup, match.group(), match.start())

    def next(self):
        tok = self._nextsub()
        while tok.type == 'WHITESPACE' or self.atlineend and tok.type == 'NEWLINES':
            tok = self._nextsub()
        self.atlineend = tok.type == 'NEWLINES'

        if tok.type == 'INT_LITERAL' and tok.val.lower().endswith('l'):
            return tok._replace(type='LONG_LITERAL')
        elif tok.type == 'DOUBLE_LITERAL' and tok.val.lower().endswith('f'):
            return tok._replace(type='FLOAT_LITERAL')
        return tok

    def atend(self): return self.pos == len(self.s)
