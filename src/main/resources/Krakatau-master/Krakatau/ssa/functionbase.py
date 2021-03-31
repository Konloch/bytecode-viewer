class SSAFunctionBase(object):
    def __init__(self, parent, arguments):
        self.parent = parent
        self.params = list(arguments)
        assert None not in self.params

    def replaceVars(self, rdict):
        self.params = [rdict.get(x,x) for x in self.params]
        assert None not in self.params
