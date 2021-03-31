class ClassLoaderError(Exception):
    def __init__(self, typen=None, data=""):
        self.type = typen
        self.data = data

        message = "\n{}: {}".format(typen, data) if typen else data
        super(ClassLoaderError, self).__init__(message)

class VerificationError(Exception):
    def __init__(self, message, data=None):
        super(VerificationError, self).__init__(message)
        self.data = data
