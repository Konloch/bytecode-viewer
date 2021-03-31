import os.path
import zipfile

from .classfile import ClassFile
from .classfileformat.reader import Reader
from .error import ClassLoaderError

class Environment(object):
    def __init__(self):
        self.classes = {}
        self.path = []
        self._open = {}

    def addToPath(self, path):
        self.path.append(path)

    def _getSuper(self, name):
        return self.getClass(name).supername

    def getClass(self, name, partial=False):
        try:
            result = self.classes[name]
        except KeyError:
            result = self._loadClass(name)
        if not partial:
            result.loadElements()
        return result

    def isSubclass(self, name1, name2):
        if name2 == 'java/lang/Object':
            return True

        while name1 != 'java/lang/Object':
            if name1 == name2:
                return True
            name1 = self._getSuper(name1)
        return False

    def commonSuperclass(self, name1, name2):
        a, b = name1, name2
        supers = {a}
        while a != b and a != 'java/lang/Object':
            a = self._getSuper(a)
            supers.add(a)

        while b not in supers:
            b = self._getSuper(b)
        return b

    def isInterface(self, name, forceCheck=False):
        try:
            class_ = self.getClass(name, partial=True)
            return 'INTERFACE' in class_.flags
        except ClassLoaderError as e:
            if forceCheck:
                raise e
            # If class is not found, assume worst case, that it is a interface
            return True

    def isFinal(self, name):
        try:
            class_ = self.getClass(name, partial=True)
            return 'FINAL' in class_.flags
        except ClassLoaderError as e:
            return False

    def _searchForFile(self, name):
        name += '.class'
        for place in self.path:
            try:
                archive = self._open[place]
            except KeyError: # plain folder
                try:
                    path = os.path.join(place, name)
                    with open(path, 'rb') as file_:
                        return file_.read()
                except IOError:
                    print 'failed to open', path.encode('utf8')
            else: # zip archive
                try:
                    return archive.read(name)
                except KeyError:
                    pass

    def _loadClass(self, name):
        print "Loading", name[:70]
        data = self._searchForFile(name)

        if data is None:
            raise ClassLoaderError('ClassNotFoundException', name)

        stream = Reader(data=data)
        new = ClassFile(stream)
        new.env = self
        self.classes[new.name] = new
        return new

    # Context Manager methods to manager our zipfiles
    def __enter__(self):
        assert not self._open
        for place in self.path:
            if place.endswith('.jar') or place.endswith('.zip'):
                self._open[place] = zipfile.ZipFile(place, 'r').__enter__()
        return self

    def __exit__(self, type_, value, traceback):
        for place in reversed(self.path):
            if place in self._open:
                self._open[place].__exit__(type_, value, traceback)
                del self._open[place]
