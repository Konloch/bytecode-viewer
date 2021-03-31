# Override this to rename classes
class DefaultVisitor(object):
    def visit(self, obj):
        return obj.print_(self, self.visit)

    # Experimental - don't use!
    def toTree(self, obj):
        if obj is None:
            return None
        return obj.tree(self, self.toTree)

    def className(self, name): return name
    def methodName(self, cls, name, desc): return name
    def fieldName(self, cls, name, desc): return name
