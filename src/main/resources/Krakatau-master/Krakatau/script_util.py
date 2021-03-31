from __future__ import print_function

import collections
import errno
from functools import partial
import hashlib
import os
import os.path
import platform
import zipfile

# Various utility functions for the top level scripts (decompile.py, assemble.py, disassemble.py)

copyright = '''Krakatau  Copyright (C) 2012-18  Robert Grosse
This program is provided as open source under the GNU General Public License.
See LICENSE.TXT for more details.
'''

_osname = platform.system().lower()
IS_WINDOWS = 'win' in _osname and 'darwin' not in _osname and 'cygwin' not in _osname

def findFiles(target, recursive, prefix):
    if target.endswith('.jar'):
        with zipfile.ZipFile(target, 'r') as archive:
            return [name.encode('utf8') for name in archive.namelist() if name.endswith(prefix)]
    else:
        if recursive:
            assert os.path.isdir(target)
            targets = []

            for root, dirs, files in os.walk(target):
                targets += [os.path.join(root, fname) for fname in files if fname.endswith(prefix)]
            return targets
        else:
            return [target]

def normalizeClassname(name):
    if name.endswith('.class'):
        name = name[:-6]
    # Replacing backslashes is ugly since they can be in valid classnames too, but this seems the best option
    return name.replace('\\','/').replace('.','/')

# Windows stuff
illegal_win_chars = frozenset('<>;:|?*\\/"%')
pref_disp_chars = frozenset('abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_$0123456789')

# Prevent creating filename parts matching the legacy device filenames. While Krakatau can create these files
# just fine thanks to using \\?\ paths, the resulting files are impossible to open or delete in Windows Explorer
# or with similar tools, so they are a huge pain to deal with. Therefore, we don't generate them at all.
illegal_parts = frozenset(['CON', 'PRN', 'AUX', 'NUL', 'COM1', 'COM2', 'COM3', 'COM4', 'COM5', 'COM6', 'COM7', 'COM8',
    'COM9', 'LPT1', 'LPT2', 'LPT3', 'LPT4', 'LPT5', 'LPT6', 'LPT7', 'LPT8', 'LPT9'])

class PathSanitizer(object):
    def __init__(self, base, suffix):
        self.base = base
        self.suffix = suffix

    def is_part_ok(self, s, parents):
        if not 1 <= len(s) <= self.MAX_PART_LEN:
            return False
        # avoid potential collision with hashed parts
        if len(s) >= 66 and '__' in s:
            return False
        # . cannot appear in a valid class name, but might as well specifically exclude these, just in case
        if s.startswith('.') or '..' in s:
            return False
        return '\x1f' < min(s) <= max(s) < '\x7f'

    def hash(self, s, suffix):
        left = ''.join(c for c in s if c in pref_disp_chars)
        right = '__' + hashlib.sha256(s.encode('utf8')).hexdigest() + suffix
        return left[:self.MAX_PART_LEN - len(right)] + right

    def sanitize(self, path):
        if isinstance(path, bytes):
            path = path.decode()

        oldparts = path.split('/')
        newparts = []
        for i, part in enumerate(oldparts):
            suffix = self.suffix if i + 1 == len(oldparts) else ''
            if self.is_part_ok(part + suffix, newparts):
                newparts.append(part + suffix)
            else:
                newparts.append(self.hash(part, suffix))

        result = self.format_path([self.base] + newparts)
        if len(result) > self.MAX_PATH_LEN:
            result = self.format_path([self.base, self.hash(path, self.suffix)])
        assert result.endswith(self.suffix)
        return result

class LinuxPathSanitizer(PathSanitizer):
    MAX_PART_LEN = 255
    MAX_PATH_LEN = 4095

    def __init__(self, *args):
        PathSanitizer.__init__(self, *args)

    def format_path(self, parts):
        return os.path.join(*parts)

class WindowsPathSanitizer(PathSanitizer):
    MAX_PART_LEN = 255
    MAX_PATH_LEN = 32000 # close enough

    def __init__(self, *args):
        PathSanitizer.__init__(self, *args)
        # keep track of previous paths to detect case-insensitive collisions
        self.prevs = collections.defaultdict(dict)

    def is_part_ok(self, s, parents):
        if not PathSanitizer.is_part_ok(self, s, parents):
            return False
        if s.upper() in illegal_parts:
            return False
        # make sure nothing in the current directory is a case insensitive collision
        if self.prevs[tuple(parents)].setdefault(s.lower(), s) != s:
            return False
        return illegal_win_chars.isdisjoint(s)

    def format_path(self, parts):
        return '\\\\?\\' + '\\'.join(parts)

class DirectoryWriter(object):
    def __init__(self, base_path, suffix):
        if base_path is None:
            base_path = os.getcwd()
        else:
            if not isinstance(base_path, str):
                base_path = base_path.decode('utf8')
            base_path = os.path.abspath(base_path)

        if IS_WINDOWS:
            self.makepath = WindowsPathSanitizer(base_path, suffix).sanitize
        else:
            self.makepath = LinuxPathSanitizer(base_path, suffix).sanitize

    def write(self, cname, data):
        out = self.makepath(cname)
        dirpath = os.path.dirname(out)

        try:
            if dirpath:
                os.makedirs(dirpath)
        except OSError as exc:
            if exc.errno != errno.EEXIST:
                raise

        mode = 'wb' if isinstance(data, bytes) else 'w'
        with open(out, mode) as f:
            f.write(data)
        return out

    def __enter__(self): return self
    def __exit__(self, *args): pass

class JarWriter(object):
    def __init__(self, base_path, suffix):
        self.zip = zipfile.ZipFile(base_path, mode='w')
        self.suffix = suffix

    def write(self, cname, data):
        info = zipfile.ZipInfo(cname + self.suffix, (1980, 1, 1, 0, 0, 0))
        self.zip.writestr(info, data)
        return 'zipfile'

    def __enter__(self): self.zip.__enter__(); return self
    def __exit__(self, *args): self.zip.__exit__(*args)

class MockWriter(object):
    def __init__(self): self.results = []
    def write(self, cname, data): self.results.append((cname, data))
    def __enter__(self): return self
    def __exit__(self, *args): pass

def makeWriter(base_path, suffix):
    if base_path is not None:
        if base_path.endswith('.zip') or base_path.endswith('.jar'):
            return JarWriter(base_path, suffix)
    return DirectoryWriter(base_path, suffix)

###############################################################################
def ignore(*args, **kwargs):
    pass

class Logger(object):
    def __init__(self, level):
        lvl = ['info', 'warning'].index(level)
        self.info = print if lvl <= 0 else ignore
        self.warn = print if lvl <= 1 else ignore
