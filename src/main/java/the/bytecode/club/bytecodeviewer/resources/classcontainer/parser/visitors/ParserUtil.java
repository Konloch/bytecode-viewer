package the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.visitors;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import org.jetbrains.annotations.Nullable;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.ClassFileContainer;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.locations.ClassFieldLocation;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.locations.ClassLocalVariableLocation;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.locations.ClassParameterLocation;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.locations.ClassReferenceLocation;

/**
 * Created by Bl3nd.
 * Date: 9/28/2024
 */
class ParserUtil
{

    static class Value
    {

        final String name;
        final int line;
        final int columnStart;
        final int columnEnd;

        public Value(SimpleName simpleName, Range range)
        {
            this.name = simpleName.getIdentifier();
            this.line = range.begin.line;
            this.columnStart = range.begin.column;
            this.columnEnd = range.end.column;
        }
    }

    /**
     * Put resolved value types (field, variable and parameter) that have a method as an owner.
     *
     * @param container   The class container
     * @param method      The owner method of the type
     * @param resolveExpr The {@code NameExpr}
     * @param value       The value
     */
    static void putResolvedValues(ClassFileContainer container, String decRef, CallableDeclaration<?> method,
                                  NameExpr resolveExpr, Value value)
    {
        ResolvedValueDeclaration vd = resolveExpr.resolve();
        if (vd.isField())
        {
            container.putField(value.name, new ClassFieldLocation(getOwner(container), decRef,
                value.line, value.columnStart, value.columnEnd + 1));
        }
        else if (vd.isVariable())
        {
            container.putLocalVariable(value.name, new ClassLocalVariableLocation(getOwner(container)
                , getMethod(method), decRef, value.line, value.columnStart,
                value.columnEnd + 1));
        }
        else if (vd.isParameter())
        {
            container.putParameter(value.name, new ClassParameterLocation(getOwner(container),
                getMethod(method), decRef, value.line, value.columnStart,
                value.columnEnd + 1));
        }
    }

    /**
     * Put resolved value types (field, variable and parameter) that are in a static block.
     *
     * @param container   The class container
     * @param resolveExpr The {@code NameExpr}
     * @param value       The value
     */
    static void putResolvedValues(ClassFileContainer container, String decRef, NameExpr resolveExpr, Value value)
    {
        ResolvedValueDeclaration vd = resolveExpr.resolve();
        if (vd.isField())
        {
            container.putField(value.name, new ClassFieldLocation(getOwner(container), decRef,
                value.line, value.columnStart, value.columnEnd + 1));
        }
        else if (vd.isVariable())
        {
            container.putLocalVariable(value.name, new ClassLocalVariableLocation(getOwner(container)
                , "static", decRef, value.line, value.columnStart, value.columnEnd + 1));
        }
        else if (vd.isParameter())
        {
            container.putParameter(value.name, new ClassParameterLocation(getOwner(container),
                "static", decRef, value.line, value.columnStart, value.columnEnd + 1));
        }
    }

    static void putParameter(ClassFileContainer container, Value parameter, String method, String decRef)
    {
        container.putParameter(parameter.name, new ClassParameterLocation(getOwner(container), method, decRef,
            parameter.line, parameter.columnStart, parameter.columnEnd + 1));
    }

    static void putLocalVariable(ClassFileContainer container, Value variable, String method, String decRef)
    {
        container.putLocalVariable(variable.name, new ClassLocalVariableLocation(getOwner(container), method, decRef,
            variable.line, variable.columnStart, variable.columnEnd + 1));
    }

    /**
     * Put both the class and field reference.
     *
     * @param container   The class container
     * @param visitedExpr The main expression
     * @param resolveExpr The expression to resolve
     * @param scopeValue  The scope value
     * @param fieldValue  The field value
     */
    static void putClassResolvedValues(ClassFileContainer container, Expression visitedExpr, Expression resolveExpr,
                                       Value scopeValue, Value fieldValue)
    {
        ResolvedType resolvedType = visitedExpr.getSymbolResolver().calculateType(resolveExpr);
        if (!resolvedType.isReferenceType())
            return;

        String qualifiedName = resolvedType.asReferenceType().getQualifiedName();
        String className = qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
        String packageName = "";
        if (qualifiedName.contains("."))
            packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf('.')).replace('.', '/');

        container.putClassReference(className, new ClassReferenceLocation(ParserUtil.getOwner(container), packageName
            , fieldValue.name, "reference", scopeValue.line, scopeValue.columnStart, scopeValue.columnEnd + 1));
        container.putField(fieldValue.name, new ClassFieldLocation(scopeValue.name, "reference", fieldValue.line,
            fieldValue.columnStart, fieldValue.columnEnd + 1));
    }

    /**
     * Put only the class reference.
     *
     * @param container   The class container
     * @param visitedExpr The main expression
     * @param resolveExpr The expression to resolve
     * @param scopeValue  The scope value
     */
    static void putClassResolvedValues(ClassFileContainer container, Expression visitedExpr,
                                       Expression resolveExpr, Value scopeValue)
    {
        ResolvedType resolvedType = visitedExpr.getSymbolResolver().calculateType(resolveExpr);
        if (!resolvedType.isReferenceType())
            return;

        ResolvedReferenceType referenceType = resolvedType.asReferenceType();
        if (!referenceType.hasName())
            return;

        String qualifiedName = referenceType.getQualifiedName();
        String className = qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
        String packageName = "";
        if (qualifiedName.contains("."))
            packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf('.')).replace('.', '/');

        container.putClassReference(className, new ClassReferenceLocation(ParserUtil.getOwner(container), packageName
            , "", "reference", scopeValue.line, scopeValue.columnStart, scopeValue.columnEnd + 1));
    }

    /**
     * Put only a class field reference.
     *
     * @param container   The class container
     * @param visitedExpr The main expression
     * @param resolveExpr The expression to resolve
     * @param fieldValue  The field value
     */
    static void putFieldResolvedValues(ClassFileContainer container, Expression visitedExpr,
                                       Expression resolveExpr, Value fieldValue)
    {
        ResolvedType resolvedType = visitedExpr.getSymbolResolver().calculateType(resolveExpr);
        if (!resolvedType.isReferenceType())
            return;

        String qualifiedName = resolvedType.asReferenceType().getQualifiedName();
        String className = qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
        container.putField(fieldValue.name, new ClassFieldLocation(className, "reference", fieldValue.line,
            fieldValue.columnStart, fieldValue.columnEnd + 1));
    }

    static void printException(Object o, Exception e)
    {
        System.err.println(o.getClass().getSimpleName() + ": " + e.getMessage());
    }

    static String getOwner(ClassFileContainer container)
    {
        return container.getName();
    }

    static String getMethod(CallableDeclaration<?> method)
    {
        return method.getDeclarationAsString(false, false);
    }

    static @Nullable String findMethodOwnerFor(CompilationUnit compilationUnit, Node node)
    {
        if (node instanceof CallableDeclaration<?>)
        {
            return ((CallableDeclaration<?>) node).getDeclarationAsString(false, false);
        }
        else if (node instanceof CatchClause)
        {
            TryStmt statement = (TryStmt) node.getParentNode().orElse(null);
            if (statement == null)
                return null;

            CallableDeclaration<?> method = findMethodForStatement(statement, compilationUnit);
            if (method == null)
            {
                method = findConstructorForStatement(statement, compilationUnit);
                if (method == null)
                {
                    if (findInitializerForStatement(statement, compilationUnit) != null)
                        return "static";

                    return null;
                }
            }

            return method.getDeclarationAsString(false, false);
        }
        else if (node instanceof Statement)
        {
            CallableDeclaration<?> method = findMethodForStatement((Statement) node, compilationUnit);
            if (method == null)
            {
                method = findConstructorForStatement((Statement) node, compilationUnit);
                if (method == null)
                {
                    if (findInitializerForStatement((Statement) node, compilationUnit) != null)
                        return "static";

                    return null;
                }
            }

            return method.getDeclarationAsString(false, false);
        }
        else if (node instanceof Expression)
        {
            CallableDeclaration<?> method = findMethodForExpression((Expression) node, compilationUnit);
            if (method == null)
            {
                method = findConstructorForExpression((Expression) node, compilationUnit);
                if (method == null)
                {
                    if (findInitializerForExpression((Expression) node, compilationUnit) != null)
                        return "static";

                    return null;
                }
            }

            return method.getDeclarationAsString(false, false);
        }

        return null;
    }

    /**
     * Look through the {@link CompilationUnit} for the specific statement within its methods.
     *
     * @param statement The {@code statement} we are looking for
     * @param cu        The {@code CompilationUnit}
     * @return the method that contains the {@code statement}.
     */
    static MethodDeclaration findMethodForStatement(Statement statement, CompilationUnit cu)
    {
        final boolean[] contains = {false};
        final MethodDeclaration[] methodDeclaration = {null};
        cu.accept(new VoidVisitorAdapter<Void>()
        {
            @Override
            public void visit(MethodDeclaration n, Void arg)
            {
                super.visit(n, arg);
                if (!n.isAbstract()/* && !contains[0]*/)
                {
                    for (Statement statement1 : n.getBody().get().getStatements())
                    {
                        if (statement1.containsWithinRange(statement))
                        {
                            contains[0] = true;
                            methodDeclaration[0] = n;
                            break;
                        }
                    }
                }
            }
        }, null);

        if (contains[0])
        {
            return methodDeclaration[0];
        }

        return null;
    }

    /**
     * Look through the {@link CompilationUnit} for the specific statement within its methods.
     *
     * @param expression The {@code expression} we are looking for
     * @param cu         The {@code CompilationUnit}
     * @return the method that contains the {@code expression}.
     */
    static MethodDeclaration findMethodForExpression(Expression expression, CompilationUnit cu)
    {
        final boolean[] contains = {false};
        final MethodDeclaration[] methodDeclaration = {null};
        cu.accept(new VoidVisitorAdapter<Void>()
        {
            @Override
            public void visit(MethodDeclaration n, Void arg)
            {
                super.visit(n, arg);
                if (!n.isAbstract()/* && !contains[0]*/)
                {
                    for (Statement statement : n.getBody().get().getStatements())
                    {
                        if (statement.containsWithinRange(expression))
                        {
                            contains[0] = true;
                            methodDeclaration[0] = n;
                            break;
                        }
                    }
                }
            }
        }, null);

        if (contains[0])
        {
            return methodDeclaration[0];
        }

        return null;
    }

    /**
     * Look through the {@link CompilationUnit} for the specific statement within its methods.
     *
     * @param statement The {@code statement} we are looking for
     * @param cu        The {@code CompilationUnit}
     * @return the constructor that contains the {@code statement}.
     */
    static ConstructorDeclaration findConstructorForStatement(Statement statement, CompilationUnit cu)
    {
        final boolean[] contains = {false};
        final ConstructorDeclaration[] constructorDeclaration = {null};
        cu.accept(new VoidVisitorAdapter<Void>()
        {
            @Override
            public void visit(ConstructorDeclaration n, Void arg)
            {
                super.visit(n, arg);
                if (contains[0])
                    return;

                for (Statement statement1 : n.getBody().getStatements())
                {
                    if (statement1.containsWithinRange(statement))
                    {
                        contains[0] = true;
                        constructorDeclaration[0] = n;
                        break;
                    }
                }
            }
        }, null);

        if (contains[0])
        {
            return constructorDeclaration[0];
        }

        return null;
    }

    /**
     * Look through the {@link CompilationUnit} for the specific statement within its methods.
     *
     * @param expression The {@code expression} we are looking for
     * @param cu         The {@code CompilationUnit}
     * @return the constructor that contains the {@code expression}.
     */
    static ConstructorDeclaration findConstructorForExpression(Expression expression, CompilationUnit cu)
    {
        final boolean[] contains = {false};
        final ConstructorDeclaration[] constructorDeclaration = {null};
        cu.accept(new VoidVisitorAdapter<Void>()
        {
            @Override
            public void visit(ConstructorDeclaration n, Void arg)
            {
                super.visit(n, arg);
                if (contains[0])
                    return;

                for (Statement statement1 : n.getBody().getStatements())
                {
                    if (statement1.containsWithinRange(expression))
                    {
                        contains[0] = true;
                        constructorDeclaration[0] = n;
                    }
                }
            }
        }, null);

        if (contains[0])
        {
            return constructorDeclaration[0];
        }

        return null;
    }

    /**
     * Look through the {@link CompilationUnit} for the specific statement within its methods.
     *
     * @param statement The {@code statement} we are looking for
     * @param cu        The {@code CompilationUnit}
     * @return the initializer that contains the {@code statement}.
     */
    static InitializerDeclaration findInitializerForStatement(Statement statement, CompilationUnit cu)
    {
        final boolean[] contains = {false};
        final InitializerDeclaration[] initializerDeclaration = {null};
        cu.accept(new VoidVisitorAdapter<Void>()
        {
            @Override
            public void visit(InitializerDeclaration n, Void arg)
            {
                super.visit(n, arg);
                if (contains[0])
                    return;

                for (Statement statement : n.getBody().getStatements())
                {
                    if (statement.containsWithinRange(statement))
                    {
                        contains[0] = true;
                        initializerDeclaration[0] = n;
                    }
                }
            }
        }, null);

        if (contains[0])
        {
            return initializerDeclaration[0];
        }
        else
        {
            return null;
        }
    }

    /**
     * Look through the {@link CompilationUnit} for the specific statement within its methods.
     *
     * @param expression The {@code expression} we are looking for
     * @param cu         The {@code CompilationUnit}
     * @return the initializer that contains the {@code expression}.
     */
    static InitializerDeclaration findInitializerForExpression(Expression expression, CompilationUnit cu)
    {
        final boolean[] contains = {false};
        final InitializerDeclaration[] initializerDeclaration = {null};
        cu.accept(new VoidVisitorAdapter<Void>()
        {
            @Override
            public void visit(InitializerDeclaration n, Void arg)
            {
                super.visit(n, arg);
                if (contains[0])
                    return;

                for (Statement statement : n.getBody().getStatements())
                {
                    if (statement.containsWithinRange(expression))
                    {
                        contains[0] = true;
                        initializerDeclaration[0] = n;
                    }
                }
            }
        }, null);

        if (contains[0])
        {
            return initializerDeclaration[0];
        }

        return null;
    }
}
