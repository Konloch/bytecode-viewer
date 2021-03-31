def thunk(initial):
    stack = [initial]
    while stack:
        try:
            stack.append(next(stack[-1]))
        except StopIteration:
            stack.pop()
