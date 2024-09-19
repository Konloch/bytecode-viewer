package the.bytecode.club.bytecodeviewer.resources.classcontainer.parser;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.ClassFileContainer;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.locations.ClassFieldLocation;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.locations.ClassLocalVariableLocation;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.locations.ClassMethodLocation;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.locations.ClassParameterLocation;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Our custom visitor that allows us to get the information from JavaParser we need.
 * <p>
 * Created by Bl3nd.
 * Date: 9/5/2024
 */
@SuppressWarnings("OptionalGetWithoutIsPresent")
public class MyVoidVisitor extends VoidVisitorAdapter<Object>
{
    private final ClassFileContainer classFileContainer;
    private final CompilationUnit compilationUnit;

    public MyVoidVisitor(ClassFileContainer container, CompilationUnit compilationUnit)
    {
        this.classFileContainer = container;
        this.compilationUnit = compilationUnit;
    }

    private String getOwner()
    {
        return this.classFileContainer.getName();
    }

    private String getMethod(CallableDeclaration<?> method)
    {
        return method.getDeclarationAsString(false, false);
    }

    /**
     * Visit all {@link FieldDeclaration}s.
     * <p>
     * This only worries about the field that is being declared.
     * </p>
     *
     * @param n   The current {@code FieldDeclaration}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(FieldDeclaration n, Object arg)
    {
        super.visit(n, arg);
        n.getVariables().forEach(variableDeclarator -> {
            SimpleName name = variableDeclarator.getName();
            String fieldName = name.getIdentifier();
            Range range = name.getRange().get();
            int line = range.begin.line;
            int columnStart = range.begin.column;
            int columnEnd = range.end.column;
            this.classFileContainer.putField(fieldName, new ClassFieldLocation(getOwner(), "declaration", line, columnStart, columnEnd + 1));
        });
    }

    @Override
    public void visit(ImportDeclaration n, Object arg)
    {
        super.visit(n, arg);
        if (!n.isAsterisk())
        {
            Name class_ = n.getName();
            String className = class_.getIdentifier();
            String package_ = Objects.requireNonNull(class_.getQualifier().orElse(null)).asString();
            package_ = package_.replace('.', '/');
            this.classFileContainer.putImport(className, package_);
        }
    }

    /**
     * Visit all {@link FieldAccessExpr}s.
     * <p>
     * This finds the field accesses.
     * </p>
     *
     * @param n   The current {@code FieldAccessExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(FieldAccessExpr n, Object arg)
    {
        super.visit(n, arg);
        SimpleName simpleName = n.getName();
        String fieldName = simpleName.getIdentifier();
        Range range = n.getTokenRange().get().getEnd().getRange().get();
        int line = range.begin.line;
        int columnStart = range.begin.column;
        int columnEnd = range.end.column;

        if (n.hasScope())
        {
            CallableDeclaration<?> method = findMethodForExpression(n, this.compilationUnit);
            if (method == null)
            {
                method = findConstructorForExpression(n, this.compilationUnit);
            }

            if (method != null)
            {
                Expression scope = n.getScope();
                if (scope instanceof NameExpr)
                {
                    NameExpr nameExpr = (NameExpr) scope;
                    SimpleName simpleName1 = nameExpr.getName();
                    String name1 = simpleName1.getIdentifier();
                    Range range1 = nameExpr.getRange().get();
                    int line1 = range1.begin.line;
                    int columnStart1 = range1.begin.column;
                    int columnEnd1 = range1.end.column;
                    try
                    {
                        ResolvedValueDeclaration vd = nameExpr.resolve();
                        if (vd.isField())
                        {
                            this.classFileContainer.putField(name1, new ClassFieldLocation(getOwner(), "reference", line1, columnStart1, columnEnd1 + 1));
                        } else if (vd.isVariable())
                        {
                            this.classFileContainer.putLocalVariable(name1, new ClassLocalVariableLocation(getOwner(), getMethod(method), "reference", line1, columnStart1, columnEnd1 + 1));
                        } else if (vd.isParameter())
                        {
                            this.classFileContainer.putParameter(name1, new ClassParameterLocation(getOwner(), getMethod(method), "reference", line1, columnStart1, columnEnd1 + 1));
                        }
                    } catch (UnsolvedSymbolException ignore)
                    {
                        ResolvedType resolvedType = n.getSymbolResolver().calculateType(nameExpr);
                        String qualifiedName = resolvedType.asReferenceType().getQualifiedName();
                        String className = qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
                        String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));
                        this.classFileContainer.putImport(className, packageName.replace('.', '/'));
                        this.classFileContainer.putField(fieldName, new ClassFieldLocation(name1, "reference", line, columnStart, columnEnd + 1));
                    }
                }
            }
        }
    }

    /**
     * Visit all {@link ConstructorDeclaration}s.
     * <p>
     * This deals with the parameters and the constructor. Not the body of the constructor.
     * </p>
     *
     * @param n   The current {@code ConstructorDeclaration}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(ConstructorDeclaration n, Object arg)
    {
        super.visit(n, arg);
        StringBuilder parameterTypes = new StringBuilder();
        AtomicInteger count = new AtomicInteger();
        n.getParameters().forEach(parameter -> {
            SimpleName name = parameter.getName();
            String parameterName = name.getIdentifier();
            Range range = name.getRange().get();
            int line = range.begin.line;
            int columnStart = range.begin.column;
            int columnEnd = range.end.column;
            this.classFileContainer.putParameter(parameterName, new ClassParameterLocation(getOwner(), n.getDeclarationAsString(false, false),
                    "declaration", line, columnStart, columnEnd + 1));
            count.getAndIncrement();
            parameterTypes.append(parameter.getTypeAsString());
            if (n.getParameters().size() > 1 && count.get() != n.getParameters().size())
            {
                parameterTypes.append(", ");
            }
        });

        SimpleName simpleName = n.getName();
        String constructorName = simpleName.getIdentifier();
        Range range = simpleName.getRange().get();
        int line = range.begin.line;
        int columnStart = range.begin.column;
        int columnEnd = range.end.column;
        this.classFileContainer.putMethod(constructorName, new ClassMethodLocation(getOwner(), parameterTypes.toString(), "declaration", line,
                columnStart,
                columnEnd + 1));
    }

    /**
     * Find all {@link ExplicitConstructorInvocationStmt}s.
     * <p>
     * E.g. {@code this()} or {@code this(param, ...)} and {@code super()} or {@code super(param, ...)}.
     * </p>
     *
     * @param n   The current {@code ExplicitConstructorInvocationStmt}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(ExplicitConstructorInvocationStmt n, Object arg)
    {
        super.visit(n, arg);
        ConstructorDeclaration constructor = findConstructorForStatement(n, this.compilationUnit);
        if (constructor != null)
        {
            n.getArguments().forEach(argument -> {
                if (argument instanceof NameExpr)
                {
                    NameExpr nameExpr = (NameExpr) argument;
                    ResolvedValueDeclaration vd = nameExpr.resolve();
                    SimpleName simpleName = nameExpr.getName();
                    String name = simpleName.getIdentifier();
                    Range range = simpleName.getRange().get();
                    int line = range.begin.line;
                    int columnStart = range.begin.column;
                    int columnEnd = range.end.column;
                    if (vd.isField())
                    {
                        this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                    } else if (vd.isVariable())
                    {
                        this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), constructor.getDeclarationAsString(false, false), "reference", line, columnStart,
                                columnEnd + 1));
                    } else if (vd.isParameter())
                    {
                        this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(),
                                constructor.getDeclarationAsString(false, false),
                                "reference", line, columnStart, columnEnd + 1));
                    }
                } else
                {
                    if ((argument instanceof LongLiteralExpr) || (argument instanceof StringLiteralExpr) || (argument instanceof CharLiteralExpr) || argument instanceof BooleanLiteralExpr || argument instanceof NullLiteralExpr || argument instanceof IntegerLiteralExpr)
                    {
                        return;
                    }

                    System.err.println(argument.getClass().getSimpleName());
                }
            });
        }
    }

    /**
     * Visit all {@link MethodDeclaration}s.
     * <p>
     * This deals with the parameters and the method name.
     * </p>
     *
     * @param n   The current {@code MethodDeclaration}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(MethodDeclaration n, Object arg)
    {
        super.visit(n, arg);
        StringBuilder parameterTypes = new StringBuilder();
        AtomicInteger count = new AtomicInteger();
        n.getParameters().forEach(parameter -> {
            SimpleName name = parameter.getName();
            String parameterName = name.getIdentifier();
            Range range = name.getRange().get();
            int line = range.begin.line;
            int columnStart = range.begin.column;
            int columnEnd = range.end.column;
            this.classFileContainer.putParameter(parameterName, new ClassParameterLocation(getOwner(), n.getDeclarationAsString(false, false),
                    "declaration", line, columnStart, columnEnd + 1));
            count.getAndIncrement();
            parameterTypes.append(parameter.getTypeAsString());
            if (n.getParameters().size() > 1 && count.get() != n.getParameters().size())
            {
                parameterTypes.append(", ");
            }
        });

        SimpleName methodSimpleName = n.getName();
        String methodName = methodSimpleName.getIdentifier();
        Range range = methodSimpleName.getRange().get();
        int line = range.begin.line;
        int columnStart = range.begin.column;
        int columnEnd = range.end.column;
        this.classFileContainer.putMethod(methodName, new ClassMethodLocation(getOwner(), parameterTypes.toString(), "declaration", line, columnStart,
                columnEnd + 1));
    }

    /**
     * Visit all {@link MethodCallExpr}s.
     *
     * @param n   The current {@code MethodCallExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(MethodCallExpr n, Object arg)
    {
        super.visit(n, arg);
        CallableDeclaration<?> method = findMethodForExpression(n, this.compilationUnit);
        InitializerDeclaration staticInitializer = null;
        if (method == null)
        {
            method = findConstructorForExpression(n, this.compilationUnit);
            if (method == null)
            {
                staticInitializer = findInitializerForExpression(n, this.compilationUnit);
            }
        }

        if (method != null)
        {
            if (n.hasScope())
            {
                Expression scope = n.getScope().get();
                if (scope instanceof NameExpr)
                {
                    NameExpr nameExpr = (NameExpr) scope;
                    try
                    {
                        ResolvedValueDeclaration vd = nameExpr.resolve();
                        SimpleName simpleName = nameExpr.getName();
                        String name = simpleName.getIdentifier();
                        Range range = simpleName.getRange().get();
                        int line = range.begin.line;
                        int columnStart = range.begin.column;
                        int columnEnd = range.end.column;
                        if (vd.isField())
                        {
                            this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                        } else if (vd.isVariable())
                        {
                            this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                        } else if (vd.isParameter())
                        {
                            this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                        }
                    } catch (UnsolvedSymbolException ignored)
                    {

                    }
                }
            }

            CallableDeclaration<?> finalMethod = method;
            n.getArguments().forEach(argument -> {
                if (argument instanceof NameExpr)
                {
                    NameExpr nameExpr = (NameExpr) argument;
                    ResolvedValueDeclaration vd = nameExpr.resolve();
                    SimpleName simpleName = nameExpr.getName();
                    String name = simpleName.getIdentifier();
                    Range range = simpleName.getRange().get();
                    int line = range.begin.line;
                    int columnStart = range.begin.column;
                    int columnEnd = range.end.column;
                    if (vd.isField())
                    {
                        this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                    } else if (vd.isVariable())
                    {
                        this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), getMethod(finalMethod), "reference", line, columnStart, columnEnd + 1));
                    } else if (vd.isParameter())
                    {
                        this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), getMethod(finalMethod), "reference", line, columnStart, columnEnd + 1));
                    }
                }
            });
        } else if (staticInitializer != null)
        {
            if (n.hasScope())
            {
                Expression scope = n.getScope().get();
                if (scope instanceof NameExpr)
                {
                    NameExpr nameExpr = (NameExpr) scope;
                    try
                    {
                        ResolvedValueDeclaration vd = nameExpr.resolve();
                        SimpleName simpleName = nameExpr.getName();
                        String name = simpleName.getIdentifier();
                        Range range = simpleName.getRange().get();
                        int line = range.begin.line;
                        int columnStart = range.begin.column;
                        int columnEnd = range.end.column;
                        if (vd.isField())
                        {
                            this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                        } else if (vd.isVariable())
                        {
                            this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), "static", "reference", line, columnStart, columnEnd + 1));
                        } else if (vd.isParameter())
                        {
                            this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), "static", "reference", line, columnStart, columnEnd + 1));
                        }
                    } catch (UnsolvedSymbolException ignored)
                    {

                    }
                }
            }

            n.getArguments().forEach(argument -> {
                if (argument instanceof NameExpr)
                {
                    NameExpr nameExpr = (NameExpr) argument;
                    ResolvedValueDeclaration vd = nameExpr.resolve();
                    SimpleName simpleName = nameExpr.getName();
                    String name = simpleName.getIdentifier();
                    Range range = simpleName.getRange().get();
                    int line = range.begin.line;
                    int columnStart = range.begin.column;
                    int columnEnd = range.end.column;
                    if (vd.isField())
                    {
                        this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                    } else if (vd.isVariable())
                    {
                        this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), "static", "reference", line, columnStart, columnEnd + 1));
                    } else if (vd.isParameter())
                    {
                        this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), "static", "reference", line, columnStart, columnEnd + 1));
                    }
                }
            });
        }
    }

    /**
     * Visit all {@link ObjectCreationExpr}s.
     *
     * @param n   The current {@code ObjectCreationExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(ObjectCreationExpr n, Object arg)
    {
        super.visit(n, arg);
        CallableDeclaration<?> method = findMethodForExpression(n, this.compilationUnit);
        if (method == null)
        {
            method = findConstructorForExpression(n, this.compilationUnit);
        }

        if (method != null)
        {
            CallableDeclaration<?> finalMethod = method;
            n.getArguments().forEach(argument -> {
                if (argument instanceof NameExpr)
                {
                    NameExpr nameExpr = (NameExpr) argument;
                    ResolvedValueDeclaration vd = nameExpr.resolve();
                    SimpleName simpleName = nameExpr.getName();
                    String name = simpleName.getIdentifier();
                    Range range = simpleName.getRange().get();
                    int line = range.begin.line;
                    int columnStart = range.begin.column;
                    int columnEnd = range.end.column;
                    if (vd.isField())
                    {
                        this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                    } else if (vd.isVariable())
                    {
                        this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), getMethod(finalMethod), "reference", line, columnStart, columnEnd + 1));
                    } else if (vd.isParameter())
                    {
                        this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), getMethod(finalMethod), "reference", line, columnStart, columnEnd + 1));
                    }
                }
            });
        }
    }

    /**
     * Visit all {@link VariableDeclarationExpr}s.
     *
     * @param n   The current {@code VariableDeclarationExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(VariableDeclarationExpr n, Object arg)
    {
        super.visit(n, arg);
        CallableDeclaration<?> method = findMethodForExpression(n, this.compilationUnit);
        InitializerDeclaration staticInitializer = null;
        if (method == null)
        {
            method = findConstructorForExpression(n, this.compilationUnit);
            if (method == null)
            {
                staticInitializer = findInitializerForExpression(n, this.compilationUnit);
            }
        }

        if (method != null)
        {
            CallableDeclaration<?> finalMethod = method;
            n.getVariables().forEach(variable -> {
                ResolvedValueDeclaration vd = variable.resolve();
                SimpleName simpleName = variable.getName();
                String name = simpleName.getIdentifier();
                Range range = simpleName.getRange().get();
                int line = range.begin.line;
                int columnStart = range.begin.column;
                int columnEnd = range.end.column;
                this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), getMethod(finalMethod), "declaration", line, columnStart, columnEnd + 1));
            });
        } else if (staticInitializer != null)
        {
            n.getVariables().forEach(variable -> {
                ResolvedValueDeclaration vd = variable.resolve();
                SimpleName simpleName = variable.getName();
                String name = simpleName.getIdentifier();
                Range range = simpleName.getRange().get();
                int line = range.begin.line;
                int columnStart = range.begin.column;
                int columnEnd = range.end.column;
                this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), "static", "declaration", line, columnStart, columnEnd + 1));
            });
        }
    }

    /**
     * Visit all {@link AssignExpr}s.
     *
     * @param n   The current {@code AssignExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(AssignExpr n, Object arg)
    {
        super.visit(n, arg);
        CallableDeclaration<?> method = findMethodForExpression(n, this.compilationUnit);
        InitializerDeclaration staticInitializer = null;
        if (method == null)
        {
            method = findConstructorForExpression(n, this.compilationUnit);
            if (method == null)
            {
                staticInitializer = findInitializerForExpression(n, this.compilationUnit);
            }
        }

        if (method != null)
        {
            Expression value = n.getValue();
            if (value instanceof NameExpr)
            {
                NameExpr nameExpr = (NameExpr) value;
                ResolvedValueDeclaration vd = nameExpr.resolve();
                SimpleName simpleName = nameExpr.getName();
                String name = simpleName.getIdentifier();
                Range range = simpleName.getRange().get();
                int line = range.begin.line;
                int columnStart = range.begin.column;
                int columnEnd = range.end.column;
                if (vd.isField())
                {
                    this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isVariable())
                {
                    this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isParameter())
                {
                    this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                }
            }

            Expression target = n.getTarget();
            if (target instanceof NameExpr)
            {
                NameExpr nameExpr = (NameExpr) target;
                try
                {
                    ResolvedValueDeclaration vd = nameExpr.resolve();
                    SimpleName simpleName = nameExpr.getName();
                    String name = simpleName.getIdentifier();
                    Range range = simpleName.getRange().get();
                    int line = range.begin.line;
                    int columnStart = range.begin.column;
                    int columnEnd = range.end.column;
                    if (vd.isField())
                    {
                        this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                    } else if (vd.isVariable())
                    {
                        this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                    } else if (vd.isParameter())
                    {
                        this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                    }
                } catch (UnsolvedSymbolException e)
                {
                    System.err.println(nameExpr.getName().getIdentifier() + " not resolved. " + e.getMessage());
                }
            }
        } else if (staticInitializer != null)
        {
            Expression value = n.getValue();
            if (value instanceof NameExpr)
            {
                NameExpr nameExpr = (NameExpr) value;
                ResolvedValueDeclaration vd = nameExpr.resolve();
                SimpleName simpleName = nameExpr.getName();
                String name = simpleName.getIdentifier();
                Range range = simpleName.getRange().get();
                int line = range.begin.line;
                int columnStart = range.begin.column;
                int columnEnd = range.end.column;
                if (vd.isField())
                {
                    this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isVariable())
                {
                    this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), "static", "reference", line, columnStart, columnEnd + 1));
                }/* else if (vd.isParameter()) {
					this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), "static", "reference", line, columnStart, columnEnd + 1));
				}*/
            }

            Expression target = n.getTarget();
            if (target instanceof NameExpr)
            {
                NameExpr nameExpr = (NameExpr) target;
                try
                {
                    ResolvedValueDeclaration vd = nameExpr.resolve();
                    SimpleName simpleName = nameExpr.getName();
                    String name = simpleName.getIdentifier();
                    Range range = simpleName.getRange().get();
                    int line = range.begin.line;
                    int columnStart = range.begin.column;
                    int columnEnd = range.end.column;
                    if (vd.isField())
                    {
                        this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                    } else if (vd.isVariable())
                    {
                        this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), "static", "reference", line, columnStart, columnEnd + 1));
                    }/* else if (vd.isParameter()) {
						System.err.println("AssignExpr - parameter2");
					}*/
                } catch (UnsolvedSymbolException e)
                {
                    System.err.println(nameExpr.getName().getIdentifier() + " not resolved. " + e.getMessage());
                }
            }
        }
    }

    /**
     * Visit all {@link CatchClause}s.
     *
     * @param n   The current {@code CatchClause}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(CatchClause n, Object arg)
    {
        super.visit(n, arg);
        TryStmt parentNode = (TryStmt) n.getParentNode().get();
        CallableDeclaration<?> method = findMethodForStatement(parentNode, this.compilationUnit);
        if (method == null)
        {
            method = findConstructorForStatement(parentNode, this.compilationUnit);
        }

        if (method != null)
        {
            SimpleName simpleName = n.getParameter().getName();
            String name = simpleName.getIdentifier();
            Range range = simpleName.getRange().get();
            int line = range.begin.line;
            int columnStart = range.begin.column;
            int columnEnd = range.end.column;
            this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), getMethod(method), "declaration", line, columnStart, columnEnd + 1));
        }
    }

    /**
     * Visit all {@link ThrowStmt}s.
     *
     * @param n   The current {@code ThrowStmt}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(ThrowStmt n, Object arg)
    {
        super.visit(n, arg);
        Expression expression = n.getExpression();
        if (expression instanceof NameExpr)
        {
            NameExpr nameExpr = (NameExpr) expression;
            CallableDeclaration<?> method = findMethodForStatement(n, this.compilationUnit);
            if (method == null)
            {
                method = findConstructorForStatement(n, this.compilationUnit);
            }

            if (method != null)
            {
                ResolvedValueDeclaration vd = nameExpr.resolve();
                SimpleName simpleName = nameExpr.getName();
                String name = simpleName.getIdentifier();
                Range range = simpleName.getRange().get();
                int line = range.begin.line;
                int columnStart = range.begin.column;
                int columnEnd = range.end.column;
                if (vd.isField())
                {
                    this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isVariable())
                {
                    this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isParameter())
                {
                    this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                }
            }
        }
    }

    /**
     * Visit all {@link InstanceOfExpr}s.
     *
     * @param n   The current {@code InstanceOfExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(InstanceOfExpr n, Object arg)
    {
        super.visit(n, arg);
        CallableDeclaration<?> method = findMethodForExpression(n, this.compilationUnit);
        if (method == null)
        {
            method = findConstructorForExpression(n, this.compilationUnit);
        }

        if (method != null)
        {
            Expression expression = n.getExpression();
            if (expression instanceof NameExpr)
            {
                NameExpr nameExpr = (NameExpr) expression;
                ResolvedValueDeclaration vd = nameExpr.resolve();
                SimpleName simpleName = nameExpr.getName();
                String name = simpleName.getIdentifier();
                Range range = simpleName.getRange().get();
                int line = range.begin.line;
                int columnStart = range.begin.column;
                int columnEnd = range.end.column;
                if (vd.isField())
                {
                    this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isVariable())
                {
                    this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isParameter())
                {
                    this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                }
            }
        }
    }

    /**
     * Visit all {@link BinaryExpr}s.
     *
     * @param n   The current {@code BinaryExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(BinaryExpr n, Object arg)
    {
        super.visit(n, arg);
        CallableDeclaration<?> method = findMethodForExpression(n, this.compilationUnit);
        if (method == null)
        {
            method = findConstructorForExpression(n, this.compilationUnit);
        }

        if (method != null)
        {
            Expression left = n.getLeft();
            if (left instanceof NameExpr)
            {
                NameExpr nameExpr = (NameExpr) left;
                ResolvedValueDeclaration vd = nameExpr.resolve();
                SimpleName simpleName = nameExpr.getName();
                String name = simpleName.getIdentifier();
                Range range = simpleName.getRange().get();
                int line = range.begin.line;
                int columnStart = range.begin.column;
                int columnEnd = range.end.column;
                if (vd.isField())
                {
                    this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isVariable())
                {
                    this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isParameter())
                {
                    this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                }
            }

            Expression right = n.getRight();
            if (right instanceof NameExpr)
            {
                NameExpr nameExpr = (NameExpr) right;
                ResolvedValueDeclaration vd = nameExpr.resolve();
                SimpleName simpleName = nameExpr.getName();
                String name = simpleName.getIdentifier();
                Range range = simpleName.getRange().get();
                int line = range.begin.line;
                int columnStart = range.begin.column;
                int columnEnd = range.end.column;
                if (vd.isField())
                {
                    this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isVariable())
                {
                    this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isParameter())
                {
                    this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                }
            }
        }
    }

    /**
     * Visit all {@link CastExpr}s.
     *
     * @param n   The current {@code CastExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(CastExpr n, Object arg)
    {
        super.visit(n, arg);
        CallableDeclaration<?> method = findMethodForExpression(n, this.compilationUnit);
        if (method == null)
        {
            method = findConstructorForExpression(n, this.compilationUnit);
        }

        if (method != null)
        {
            Expression expression = n.getExpression();
            if (expression instanceof NameExpr)
            {
                NameExpr nameExpr = (NameExpr) expression;
                ResolvedValueDeclaration vd = nameExpr.resolve();
                SimpleName simpleName = nameExpr.getName();
                String name = simpleName.getIdentifier();
                Range range = simpleName.getRange().get();
                int line = range.begin.line;
                int columnStart = range.begin.column;
                int columnEnd = range.end.column;
                if (vd.isField())
                {
                    this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isVariable())
                {
                    this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isParameter())
                {
                    this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                }
            }
        }
    }

    /**
     * Visit all {@link IfStmt}s.
     *
     * @param n   The current {@code IfStmt}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(IfStmt n, Object arg)
    {
        super.visit(n, arg);
        Expression condition = n.getCondition();
        if (condition instanceof NameExpr)
        {
            NameExpr nameExpr = (NameExpr) condition;
            CallableDeclaration<?> method = findMethodForStatement(n, this.compilationUnit);
            InitializerDeclaration staticInitializer = null;
            if (method == null)
            {
                method = findConstructorForStatement(n, this.compilationUnit);
                if (method == null)
                {
                    staticInitializer = findInitializerForStatement(n, this.compilationUnit);
                }
            }

            if (method != null)
            {
                ResolvedValueDeclaration vd = nameExpr.resolve();
                SimpleName simpleName = nameExpr.getName();
                String name = simpleName.getIdentifier();
                Range range = simpleName.getRange().get();
                int line = range.begin.line;
                int columnStart = range.begin.column;
                int columnEnd = range.end.column;
                if (vd.isField())
                {
                    this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isVariable())
                {
                    this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isParameter())
                {
                    this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                }
            } else if (staticInitializer != null)
            {
                ResolvedValueDeclaration vd = nameExpr.resolve();
                SimpleName simpleName = nameExpr.getName();
                String name = simpleName.getIdentifier();
                Range range = simpleName.getRange().get();
                int line = range.begin.line;
                int columnStart = range.begin.column;
                int columnEnd = range.end.column;
                if (vd.isField())
                {
                    this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isVariable())
                {
                    this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), "static", "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isParameter())
                {
                    this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), "static", "reference", line, columnStart, columnEnd + 1));
                }
            }
        }
    }

    /**
     * Visit all {@link ArrayInitializerExpr}s.
     *
     * @param n   The current {@code ArrayInitializerExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(ArrayInitializerExpr n, Object arg)
    {
        super.visit(n, arg);
        n.getValues().forEach(value -> {
            if (value instanceof NameExpr)
            {
                NameExpr nameExpr = (NameExpr) value;
                CallableDeclaration<?> method = findMethodForExpression(n, this.compilationUnit);
                if (method == null)
                {
                    method = findConstructorForExpression(n, this.compilationUnit);
                }

                if (method != null)
                {
                    ResolvedValueDeclaration vd = nameExpr.resolve();
                    SimpleName simpleName = nameExpr.getName();
                    String name = simpleName.getIdentifier();
                    Range range = simpleName.getRange().get();
                    int line = range.begin.line;
                    int columnStart = range.begin.column;
                    int columnEnd = range.end.column;
                    if (vd.isField())
                    {
                        this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                    } else if (vd.isVariable())
                    {
                        this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                    } else if (vd.isParameter())
                    {
                        this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                    }
                }
            }
        });
    }

    /**
     * Visit all {@link ArrayCreationExpr}s.
     *
     * @param n   The current {@code ArrayCreationExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(ArrayCreationExpr n, Object arg)
    {
        super.visit(n, arg);
        n.getLevels().forEach(level -> {
            Expression dimension = level.getDimension().orElse(null);
            if (dimension instanceof NameExpr)
            {
                NameExpr nameExpr = (NameExpr) dimension;
                CallableDeclaration<?> method = findMethodForExpression(n, this.compilationUnit);
                if (method == null)
                {
                    method = findConstructorForExpression(n, this.compilationUnit);
                }

                if (method != null)
                {
                    ResolvedValueDeclaration vd = nameExpr.resolve();
                    SimpleName simpleName = nameExpr.getName();
                    String name = simpleName.getIdentifier();
                    Range range = simpleName.getRange().get();
                    int line = range.begin.line;
                    int columnStart = range.begin.column;
                    int columnEnd = range.end.column;
                    if (vd.isField())
                    {
                        this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                    } else if (vd.isVariable())
                    {
                        this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                    } else if (vd.isParameter())
                    {
                        this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                    }
                }
            }
        });
    }

    /**
     * Visit all {@link ArrayAccessExpr}s.
     *
     * @param n   The current {@code ArrayAccessExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(ArrayAccessExpr n, Object arg)
    {
        super.visit(n, arg);
        Expression expression = n.getName();
        if (expression instanceof NameExpr)
        {
            NameExpr nameExpr = (NameExpr) expression;
            CallableDeclaration<?> method = findMethodForExpression(n, this.compilationUnit);
            if (method == null)
            {
                method = findConstructorForExpression(n, this.compilationUnit);
            }

            if (method != null)
            {
                ResolvedValueDeclaration vd = nameExpr.resolve();
                SimpleName simpleName = nameExpr.getName();
                String name = simpleName.getIdentifier();
                Range range = simpleName.getRange().get();
                int line = range.begin.line;
                int columnStart = range.begin.column;
                int columnEnd = range.end.column;
                if (vd.isField())
                {
                    this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isVariable())
                {
                    this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isParameter())
                {
                    this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                }
            }
        }

        Expression index = n.getIndex();
        if (index instanceof NameExpr)
        {
            NameExpr nameExpr = (NameExpr) index;
            CallableDeclaration<?> method = findMethodForExpression(n, this.compilationUnit);
            if (method == null)
            {
                method = findConstructorForExpression(n, this.compilationUnit);
            }

            if (method != null)
            {
                ResolvedValueDeclaration vd = nameExpr.resolve();
                SimpleName simpleName = nameExpr.getName();
                String name = simpleName.getIdentifier();
                Range range = simpleName.getRange().get();
                int line = range.begin.line;
                int columnStart = range.begin.column;
                int columnEnd = range.end.column;
                if (vd.isField())
                {
                    this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isVariable())
                {
                    this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isParameter())
                {
                    this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                }
            }
        }
    }

    /**
     * Visit all {@link ForEachStmt}s.
     *
     * @param n   The current {@code ForEachStmt}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(ForEachStmt n, Object arg)
    {
        super.visit(n, arg);
        Expression iterable = n.getIterable();
        if (iterable instanceof NameExpr)
        {
            NameExpr nameExpr = (NameExpr) iterable;
            CallableDeclaration<?> method = findMethodForStatement(n, this.compilationUnit);
            if (method == null)
            {
                method = findConstructorForStatement(n, this.compilationUnit);
            }

            if (method != null)
            {
                ResolvedValueDeclaration vd = nameExpr.resolve();
                SimpleName simpleName = nameExpr.getName();
                String name = simpleName.getIdentifier();
                Range range = simpleName.getRange().get();
                int line = range.begin.line;
                int columnStart = range.begin.column;
                int columnEnd = range.end.column;
                if (vd.isField())
                {
                    this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isVariable())
                {
                    this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isParameter())
                {
                    this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                }
            }
        }
    }

    /**
     * Visit all {@link ReturnStmt}s.
     *
     * @param n   The current {@code ReturnStmt}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(ReturnStmt n, Object arg)
    {
        super.visit(n, arg);
        Expression expression = n.getExpression().orElse(null);
        if (expression instanceof NameExpr)
        {
            NameExpr nameExpr = (NameExpr) expression;
            CallableDeclaration<?> method = findMethodForStatement(n, this.compilationUnit);
            if (method == null)
            {
                method = findConstructorForStatement(n, this.compilationUnit);
            }

            if (method != null)
            {
                ResolvedValueDeclaration vd = nameExpr.resolve();
                SimpleName simpleName = nameExpr.getName();
                String name = simpleName.getIdentifier();
                Range range = simpleName.getRange().get();
                int line = range.begin.line;
                int columnStart = range.begin.column;
                int columnEnd = range.end.column;
                if (vd.isField())
                {
                    this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isVariable())
                {
                    this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isParameter())
                {
                    this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                }
            }
        }
    }

    /**
     * Visit all {@link UnaryExpr}s.
     *
     * @param n   The current {@code UnaryExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(UnaryExpr n, Object arg)
    {
        super.visit(n, arg);
        Expression expression = n.getExpression();
        if (expression instanceof NameExpr)
        {
            NameExpr nameExpr = (NameExpr) expression;
            CallableDeclaration<?> method = findMethodForExpression(n, this.compilationUnit);
            if (method == null)
            {
                method = findConstructorForExpression(n, this.compilationUnit);
            }

            if (method != null)
            {
                try
                {
                    ResolvedValueDeclaration vd = nameExpr.resolve();
                    SimpleName simpleName = nameExpr.getName();
                    String name = simpleName.getIdentifier();
                    Range range = simpleName.getRange().get();
                    int line = range.begin.line;
                    int columnStart = range.begin.column;
                    int columnEnd = range.end.column;
                    if (vd.isField())
                    {
                        this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                    } else if (vd.isVariable())
                    {
                        this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                    } else if (vd.isParameter())
                    {
                        this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                    }
                } catch (UnsolvedSymbolException ignored)
                {
                }
            }
        }
    }

    /**
     * Visit all {@link ConditionalExpr}s.
     *
     * @param n   The current {@code ConditionalExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(ConditionalExpr n, Object arg)
    {
        super.visit(n, arg);
        Expression elseExpr = n.getElseExpr();
        if (elseExpr instanceof NameExpr)
        {
            NameExpr nameExpr = (NameExpr) elseExpr;
            CallableDeclaration<?> method = findMethodForExpression(n, this.compilationUnit);
            if (method == null)
            {
                method = findConstructorForExpression(n, this.compilationUnit);
            }

            if (method != null)
            {
                ResolvedValueDeclaration vd = nameExpr.resolve();
                SimpleName simpleName = nameExpr.getName();
                String name = simpleName.getIdentifier();
                Range range = simpleName.getRange().get();
                int line = range.begin.line;
                int columnStart = range.begin.column;
                int columnEnd = range.end.column;
                if (vd.isField())
                {
                    this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isVariable())
                {
                    this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isParameter())
                {
                    this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                }
            }
        }

        Expression thenExpr = n.getThenExpr();
        if (thenExpr instanceof NameExpr)
        {
            NameExpr nameExpr = (NameExpr) thenExpr;
            CallableDeclaration<?> method = findMethodForExpression(n, this.compilationUnit);
            if (method == null)
            {
                method = findConstructorForExpression(n, this.compilationUnit);
            }

            if (method != null)
            {
                ResolvedValueDeclaration vd = nameExpr.resolve();
                SimpleName simpleName = nameExpr.getName();
                String name = simpleName.getIdentifier();
                Range range = simpleName.getRange().get();
                int line = range.begin.line;
                int columnStart = range.begin.column;
                int columnEnd = range.end.column;
                if (vd.isField())
                {
                    this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isVariable())
                {
                    this.classFileContainer.putLocalVariable(name, new ClassLocalVariableLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd + 1));
                } else if (vd.isParameter())
                {
                    this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), getMethod(method), "reference", line, columnStart, columnEnd));
                }
            }
        }
    }

    /**
     * Visit all {@link LambdaExpr}'s.
     *
     * @param n   The current {@code LambdaExpr}
     * @param arg Don't worry about it.
     */
    @Override
    public void visit(LambdaExpr n, Object arg)
    {
        super.visit(n, arg);
        CallableDeclaration<?> method = findMethodForExpression(n, this.compilationUnit);
        if (method == null)
        {
            method = findConstructorForExpression(n, this.compilationUnit);
        }

        if (method != null)
        {
            CallableDeclaration<?> finalMethod = method;
            n.getParameters().forEach(parameter -> {
                SimpleName simpleName = parameter.getName();
                String name = simpleName.getIdentifier();
                Range range = parameter.getRange().get();
                int line = range.begin.line;
                int columnStart = range.begin.column;
                int columnEnd = range.end.column;
                this.classFileContainer.putParameter(name, new ClassParameterLocation(getOwner(), getMethod(finalMethod), "declaration", line, columnStart, columnEnd + 1));
            });
        }
    }

    /**
     * Visit all {@link EnumDeclaration}'s.
     *
     * @param n   The current {@code EnumDeclaration}
     * @param arg Don't worry about it.
     */
    @Override
    public void visit(EnumDeclaration n, Object arg)
    {
        super.visit(n, arg);
        n.getEntries().forEach(entry -> {
            SimpleName simpleName = entry.getName();
            String name = simpleName.getIdentifier();
            Range range = simpleName.getRange().get();
            int line = range.begin.line;
            int columnStart = range.begin.column;
            int columnEnd = range.end.column;
            this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(), "declaration", line, columnStart, columnEnd + 1));
        });
    }

    /**
     * Look through the {@link CompilationUnit} for the specific statement within its methods.
     *
     * @param statement The {@code statement} we are looking for
     * @param cu        The {@code CompilationUnit}
     * @return the method that contains the {@code statement}.
     */
    private MethodDeclaration findMethodForStatement(Statement statement, CompilationUnit cu)
    {
        final boolean[] contains = {false};
        final MethodDeclaration[] methodDeclaration = {null};
        cu.accept(new VoidVisitorAdapter<Void>()
        {
            @Override
            public void visit(MethodDeclaration n, Void arg)
            {
                super.visit(n, arg);
                if (!n.isAbstract())
                {
                    for (Statement statement1 : n.getBody().get().getStatements())
                    {
                        if (statement1.containsWithinRange(statement))
                        {
                            contains[0] = true;
                            methodDeclaration[0] = n;
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
    private MethodDeclaration findMethodForExpression(Expression expression, CompilationUnit cu)
    {
        final boolean[] contains = {false};
        final MethodDeclaration[] methodDeclaration = {null};
        cu.accept(new VoidVisitorAdapter<Void>()
        {
            @Override
            public void visit(MethodDeclaration n, Void arg)
            {
                super.visit(n, arg);
                if (!n.isAbstract())
                {
                    for (Statement statement : n.getBody().get().getStatements())
                    {
                        if (statement.containsWithinRange(expression))
                        {
                            contains[0] = true;
                            methodDeclaration[0] = n;
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
    private ConstructorDeclaration findConstructorForStatement(Statement statement, CompilationUnit cu)
    {
        final boolean[] contains = {false};
        final ConstructorDeclaration[] constructorDeclaration = {null};
        cu.accept(new VoidVisitorAdapter<Void>()
        {
            @Override
            public void visit(ConstructorDeclaration n, Void arg)
            {
                super.visit(n, arg);
                for (Statement statement1 : n.getBody().getStatements())
                {
                    if (statement1.containsWithinRange(statement))
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
     * @param expression The {@code expression} we are looking for
     * @param cu         The {@code CompilationUnit}
     * @return the constructor that contains the {@code expression}.
     */
    private ConstructorDeclaration findConstructorForExpression(Expression expression, CompilationUnit cu)
    {
        final boolean[] contains = {false};
        final ConstructorDeclaration[] constructorDeclaration = {null};
        cu.accept(new VoidVisitorAdapter<Void>()
        {
            @Override
            public void visit(ConstructorDeclaration n, Void arg)
            {
                super.visit(n, arg);
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
    private InitializerDeclaration findInitializerForStatement(Statement statement, CompilationUnit cu)
    {
        final boolean[] contains = {false};
        final InitializerDeclaration[] initializerDeclaration = {null};
        cu.accept(new VoidVisitorAdapter<Void>()
        {
            @Override
            public void visit(InitializerDeclaration n, Void arg)
            {
                super.visit(n, arg);
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
        } else
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
    private InitializerDeclaration findInitializerForExpression(Expression expression, CompilationUnit cu)
    {
        final boolean[] contains = {false};
        final InitializerDeclaration[] initializerDeclaration = {null};
        cu.accept(new VoidVisitorAdapter<Void>()
        {
            @Override
            public void visit(InitializerDeclaration n, Void arg)
            {
                super.visit(n, arg);
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