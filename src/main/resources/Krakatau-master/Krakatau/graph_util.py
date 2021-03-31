import itertools

def tarjanSCC(roots, getChildren):
    """Return a list of strongly connected components in a graph. If getParents is passed instead of getChildren, the result will be topologically sorted.

    roots - list of root nodes to search from
    getChildren - function which returns children of a given node
    """

    sccs = []
    indexCounter = itertools.count()
    index = {}
    lowlink = {}
    removed = set()
    subtree = []

    # Use iterative version to avoid stack limits for large datasets
    stack = [(node, 0) for node in roots]
    while stack:
        current, state = stack.pop()
        if state == 0: # before recursing
            if current not in index: # if it's in index, it was already visited (possibly earlier on the current search stack)
                lowlink[current] = index[current] = next(indexCounter)
                subtree.append(current)

                stack.append((current, 1))
                stack.extend((child, 0) for child in getChildren(current) if child not in removed)
        else: # after recursing
            children = [child for child in getChildren(current) if child not in removed]
            for child in children:
                if index[child] <= index[current]: # backedge (or selfedge)
                    lowlink[current] = min(lowlink[current], index[child])
                else:
                    lowlink[current] = min(lowlink[current], lowlink[child])
                assert lowlink[current] <= index[current]

            if index[current] == lowlink[current]:
                scc = []
                while not scc or scc[-1] != current:
                    scc.append(subtree.pop())

                sccs.append(tuple(scc))
                removed.update(scc)
    return sccs

def topologicalSort(roots, getParents):
    """Return a topological sorting of nodes in a graph.

    roots - list of root nodes to search from
    getParents - function which returns the parents of a given node
    """

    results = []
    visited = set()

    # Use iterative version to avoid stack limits for large datasets
    stack = [(node,0) for node in roots]
    while stack:
        current, state = stack.pop()
        if state == 0: # before recursing
            if current not in visited:
                visited.add(current)
                stack.append((current,1))
                stack.extend((parent,0) for parent in getParents(current))
        else: # after recursing
            assert current in visited
            results.append(current)
    return results
