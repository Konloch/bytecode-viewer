from collections import defaultdict as ddict

from .. import graph_util
from ..ssa import objtypes

from . import ast

def flattenDict(replace):
    for k in list(replace):
        while replace[k] in replace:
            replace[k] = replace[replace[k]]

# The basic block in our temporary CFG
# instead of code, it merely contains a list of defs and uses
# This is an extended basic block, i.e. it only terminates in a normal jump(s).
# exceptions can be thrown from various points within the block
class DUBlock(object):
    def __init__(self, key):
        self.key = key
        self.caught_excepts = ()
        self.lines = []     # 3 types of lines: ('use', var), ('def', (var, var2_opt)), or ('canthrow', None)
        self.e_successors = []
        self.n_successors = []
        self.vars = None # vars used or defined within the block. Does NOT include caught exceptions

    def canThrow(self): return ('canthrow', None) in self.lines

    def recalcVars(self):
        self.vars = set()
        for line_t, data in self.lines:
            if line_t == 'use':
                self.vars.add(data)
            elif line_t == 'def':
                self.vars.add(data[0])
                if data[1] is not None:
                    self.vars.add(data[1])

    def replace(self, replace):
        if not self.vars.isdisjoint(replace):
            newlines = []
            for line_t, data in self.lines:
                if line_t == 'use':
                    data = replace.get(data, data)
                elif line_t == 'def':
                    data = replace.get(data[0], data[0]), replace.get(data[1], data[1])
                newlines.append((line_t, data))
            self.lines = newlines
            for k, v in replace.items():
                if k in self.vars:
                    self.vars.remove(k)
                    self.vars.add(v)

    def simplify(self):
        # try to prune redundant instructions
        last = None
        newlines = []
        for line in self.lines:
            if line[0] == 'def':
                if line[1][0] == line[1][1]:
                    continue
            elif line == last:
                continue
            newlines.append(line)
            last = line
        self.lines = newlines
        self.recalcVars()

def varOrNone(expr):
    return expr if isinstance(expr, ast.Local) else None

def canThrow(expr):
    if isinstance(expr, (ast.ArrayAccess, ast.ArrayCreation, ast.Cast, ast.ClassInstanceCreation, ast.FieldAccess, ast.MethodInvocation)):
        return True
    if isinstance(expr, ast.BinaryInfix) and expr.opstr in ('/','%'): # check for possible division by 0
        return expr.dtype not in (objtypes.FloatTT, objtypes.DoubleTT)
    return False

def visitExpr(expr, lines):
    if expr is None:
        return
    if isinstance(expr, ast.Local):
        lines.append(('use', expr))

    if isinstance(expr, ast.Assignment):
        lhs, rhs = map(varOrNone, expr.params)

        # with assignment we need to only visit LHS if it isn't a local in order to avoid spurious uses
        # also, we need to visit RHS before generating the def
        if lhs is None:
            visitExpr(expr.params[0], lines)
        visitExpr(expr.params[1], lines)
        if lhs is not None:
            lines.append(('def', (lhs, rhs)))
    else:
        for param in expr.params:
            visitExpr(param, lines)

    if canThrow(expr):
        lines.append(('canthrow', None))

class DUGraph(object):
    def __init__(self):
        self.blocks = []
        self.entry = None

    def makeBlock(self, key, break_dict, caught_except, myexcept_parents):
        block = DUBlock(key)
        self.blocks.append(block)

        for parent in break_dict[block.key]:
            parent.n_successors.append(block)
        del break_dict[block.key]

        assert (myexcept_parents is None) == (caught_except is None)
        if caught_except is not None: # this is the head of a catch block:
            block.caught_excepts = (caught_except,)
            for parent in myexcept_parents:
                parent.e_successors.append(block)
        return block

    def finishBlock(self, block, catch_stack):
        # register exception handlers for completed old block and calculate var set
        assert(block.vars is None) # make sure it wasn't finished twice
        if block.canThrow():
            for clist in catch_stack:
                clist.append(block)
        block.recalcVars()

    def visitScope(self, scope, break_dict, catch_stack, caught_except=None, myexcept_parents=None, head_block=None):
        # catch_stack is copy on modify
        if head_block is None:
            head_block = block = self.makeBlock(scope.continueKey, break_dict, caught_except, myexcept_parents)
        else:
            block = head_block

        for stmt in scope.statements:
            if isinstance(stmt, (ast.ExpressionStatement, ast.ThrowStatement, ast.ReturnStatement)):
                visitExpr(stmt.expr, block.lines)
                if isinstance(stmt, ast.ThrowStatement):
                    block.lines.append(('canthrow', None))
                continue

            # compound statements
            assert stmt.continueKey is not None
            if isinstance(stmt, (ast.IfStatement, ast.SwitchStatement)):
                visitExpr(stmt.expr, block.lines)

                if isinstance(stmt, ast.SwitchStatement):
                    ft = not stmt.hasDefault()
                else:
                    ft = len(stmt.getScopes()) == 1

                for sub in stmt.getScopes():
                    break_dict[sub.continueKey].append(block)
                    self.visitScope(sub, break_dict, catch_stack)
                if ft:
                    break_dict[stmt.breakKey].append(block)

            elif isinstance(stmt, ast.WhileStatement):
                if stmt.expr != ast.Literal.TRUE: # while(cond)
                    assert stmt.breakKey is not None
                    self.finishBlock(block, catch_stack)
                    block = self.makeBlock(stmt.continueKey, break_dict, None, None)
                    visitExpr(stmt.expr, block.lines)
                    break_dict[stmt.breakKey].append(block)

                break_dict[stmt.continueKey].append(block)
                body_block = self.visitScope(stmt.getScopes()[0], break_dict, catch_stack)
                continue_target = body_block if stmt.expr == ast.Literal.TRUE else block

                for parent in break_dict[stmt.continueKey]:
                    parent.n_successors.append(continue_target)
                del break_dict[stmt.continueKey]

            elif isinstance(stmt, ast.TryStatement):
                new_stack = catch_stack + [[] for _ in stmt.pairs]

                break_dict[stmt.tryb.continueKey].append(block)
                self.visitScope(stmt.tryb, break_dict, new_stack)

                for cdecl, catchb in stmt.pairs:
                    parents = new_stack.pop()
                    self.visitScope(catchb, break_dict, catch_stack, cdecl.local, parents)
                assert new_stack == catch_stack
            else:
                assert isinstance(stmt, ast.StatementBlock)
                break_dict[stmt.continueKey].append(block)
                self.visitScope(stmt, break_dict, catch_stack, head_block=block)

            if not isinstance(stmt, ast.StatementBlock): # if we passed it to subscope, it will be finished in the subcall
                self.finishBlock(block, catch_stack)

            if stmt.breakKey is not None: # start new block after return from compound statement
                block = self.makeBlock(stmt.breakKey, break_dict, None, None)
            else:
                block = None # should never be accessed anyway if we're exiting abruptly

        if scope.jumpKey is not None:
            break_dict[scope.jumpKey].append(block)

        if block is not None:
            self.finishBlock(block, catch_stack)
        return head_block # head needs to be returned in case of loops so we can fix up backedges

    def makeCFG(self, root):
        break_dict = ddict(list)
        self.visitScope(root, break_dict, [])
        self.entry = self.blocks[0] # entry point should always be first block generated

        reached = graph_util.topologicalSort([self.entry], lambda block:(block.n_successors + block.e_successors))
        # if len(reached) != len(self.blocks):
        #     print 'warning, {} blocks unreachable!'.format(len(self.blocks) - len(reached))
        self.blocks = reached

    def replace(self, replace):
        flattenDict(replace)
        for block in self.blocks:
            block.replace(replace)

    def simplify(self):
        for block in self.blocks:
            block.simplify()

def makeGraph(root):
    g = DUGraph()
    g.makeCFG(root)
    return g
