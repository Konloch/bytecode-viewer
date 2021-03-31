DIRECTIVE = r'\.[a-z]+'
WORD = r'(?:[a-zA-Z_$\(<]|\[[A-Z\[])[\w$;\/\[\(\)<>*+-]*'
FOLLOWED_BY_WHITESPACE = r'(?=\s|\Z)'
REF = r'\[[a-z0-9_:]+\]'
LABEL_DEF = r'L\w+:'

COMMENT = r';.*'
# Match optional comment and at least one newline, followed by any number of empty/whitespace lines
NEWLINES = r'(?:{})?\n\s*'.format(COMMENT)

HEX_DIGIT = r'[0-9a-fA-F]'
ESCAPE_SEQUENCE = r'''\\(?:U00(?:10|0{hd}){hd}{{4}}|u{hd}{{4}}|x{hd}{{2}}|[btnfr'"\\0-7])'''.format(hd=HEX_DIGIT)
# See http://stackoverflow.com/questions/430759/regex-for-managing-escaped-characters-for-items-like-string-literals/5455705# 5455705
STRING_LITERAL = r'''
[bB]?(?:
    "
        [^"\n\\]*               # any number of unescaped characters
        (?:{es}[^"\n\\]*       # escape sequence followed by 0 or more unescaped
        )*
    "
|
    '
        [^'\n\\]*               # any number of unescaped characters
        (?:{es}[^'\n\\]*       # escape sequence followed by 0 or more unescaped
        )*
    '
)'''.format(es=ESCAPE_SEQUENCE)
# For error detection
STRING_START = r'''[bB]?(?:"(?:[^"\\\n]|{es})*|'(?:[^'\\\n]|{es})*)'''.format(es=ESCAPE_SEQUENCE)

# Careful here: | is not greedy so hex must come first
INT_LITERAL = r'[+-]?(?:0[xX]{hd}+|[1-9][0-9]*|0)[lL]?'.format(hd=HEX_DIGIT)
FLOAT_LITERAL = r'''(?:
    (?:
        [-+][Ii][Nn][Ff][Ii][Nn][Ii][Tt][Yy]|           # Nan and Inf both have mandatory sign
        [-+][Nn][Aa][Nn]
            (?:<0[xX]{hd}+>)?                           # Optional suffix for nonstandard NaNs
    )|
    [-+]?(?:
        \d+\.\d+(?:[eE][+-]?\d+)?|                         # decimal float
        \d+[eE][+-]?\d+|                                   # decimal float with no fraction (exponent mandatory)
        0[xX]{hd}+(?:\.{hd}+)?[pP][+-]?\d+                 # hexidecimal float
        )
    )[fF]?
'''.format(hd=HEX_DIGIT)
