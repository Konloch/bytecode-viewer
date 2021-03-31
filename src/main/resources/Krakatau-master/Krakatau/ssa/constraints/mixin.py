class ValueType(object):
    '''Define _key() and inherit from this class to implement comparison and hashing'''
    # def __init__(self, *args, **kwargs): super(ValueType, self).__init__(*args, **kwargs)
    def __eq__(self, other): return type(self) == type(other) and self._key() == other._key()
    def __ne__(self, other): return type(self) != type(other) or self._key() != other._key()
    def __hash__(self): return hash(self._key())   
