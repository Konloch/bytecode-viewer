import itertools

def update(self, items):
    self.entryBlock = items[0].entryBlock
    self.nodes = frozenset.union(*(i.nodes for i in items))
    temp = set(self.nodes)
    siter = itertools.chain.from_iterable(i.successors for i in items)
    self.successors = [n for n in siter if not n in temp and not temp.add(n)]

class SEBlockItem(object):
    def __init__(self, node):
        self.successors = node.norm_suc_nl # don't include backedges or exceptional edges
        self.node = node
        self.nodes = frozenset([node])
        self.entryBlock = node

    def getScopes(self): return ()

class SEScope(object):
    def __init__(self, items):
        self.items = items
        update(self, items)

    def getScopes(self): return ()

class SEWhile(object):
    def __init__(self, scope):
        self.body = scope
        update(self, [scope])

    def getScopes(self): return self.body,

class SETry(object):
    def __init__(self, tryscope, catchscope, toptts, catchvar):
        self.scopes = tryscope, catchscope
        self.toptts = toptts
        self.catchvar = catchvar # none if ignored
        update(self, self.scopes)

    def getScopes(self): return self.scopes

class SEIf(object):
    def __init__(self, head, newscopes):
        assert len(newscopes) == 2
        self.scopes = newscopes
        self.head = head
        update(self, [head] + newscopes)

    def getScopes(self): return self.scopes

class SESwitch(object):
    def __init__(self, head, newscopes):
        self.scopes = newscopes
        self.head = head
        self.ordered = newscopes
        update(self, [head] + newscopes)

        jump = head.node.block.jump
        keysets = {head.node.blockdict[b.key,False]:jump.reverse.get(b) for b in jump.getNormalSuccessors()}
        assert keysets.values().count(None) == 1
        self.ordered_keysets = [keysets[item.entryBlock] for item in newscopes]

    def getScopes(self): return self.scopes
