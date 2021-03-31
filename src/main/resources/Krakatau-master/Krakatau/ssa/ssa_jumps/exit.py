from .base import BaseJump

class Return(BaseJump):
    def __init__(self, parent, arguments):
        super(Return, self).__init__(parent, arguments)

class Rethrow(BaseJump):
    def __init__(self, parent, arguments):
        super(Rethrow, self).__init__(parent, arguments)
