from .base import BaseJump

class Placeholder(BaseJump):
    def __init__(self, parent, *args, **kwargs):
        super(Placeholder, self).__init__(parent)
