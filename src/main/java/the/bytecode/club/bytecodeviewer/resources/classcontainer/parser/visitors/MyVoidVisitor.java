package the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.visitors;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.IntersectionType;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedConstructorDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.ClassFileContainer;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.locations.ClassFieldLocation;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.locations.ClassMethodLocation;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.locations.ClassReferenceLocation;

import java.util.Objects;

import static the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.visitors.ParserUtil.*;

/**
 * Our custom visitor that allows us to get the information from JavaParser we need.
 * <p>
 * Created by Bl3nd.
 * Date: 9/5/2024
 */
public class MyVoidVisitor extends VoidVisitorAdapter<Object>
{

    /*
    Any issues related to JavaParser that we cannot fix:

    TODO: Ambiguous method call: JavaParser Issue #3037 / resulting from MethodCallExpr -> FieldAccessExpr
     */

    private static final boolean DEBUG = false;

    private final ClassFileContainer classFileContainer;
    private final CompilationUnit compilationUnit;


    public MyVoidVisitor(ClassFileContainer container, CompilationUnit compilationUnit)
    {
        this.classFileContainer = container;
        this.compilationUnit = compilationUnit;
    }

    @Override
    public void visit(AnnotationDeclaration n, Object arg)
    {
        super.visit(n, arg);
        if (DEBUG) System.err.println("AnnotationDeclaration");
    }

    @Override
    public void visit(AnnotationMemberDeclaration n, Object arg)
    {
        super.visit(n, arg);
        if (DEBUG) System.err.println("AnnotationMemberDeclaration");
    }


    /**
     * Visit all {@link ArrayAccessExpr}s.
     * <p>
     * Ex. {@code getNames()[15 * 15]}
     * </p>
     *
     * @param n   The current {@code ArrayAccessExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(ArrayAccessExpr n, Object arg)
    {
        super.visit(n, arg);
        try
        {
            ArrayParser.parseAccess(compilationUnit, n, classFileContainer);
        }
        catch (Exception e)
        {
            printException(n, e);
        }
    }

    /**
     * Visit all {@link ArrayCreationExpr}s.
     * <p>
     * Ex. {@code new int[5] and new int[1][2]}
     * </p>
     *
     * @param n   The current {@code ArrayCreationExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(ArrayCreationExpr n, Object arg)
    {
        super.visit(n, arg);
        try
        {
            ArrayParser.parseCreation(compilationUnit, n, classFileContainer);
        }
        catch (Exception e)
        {
            printException(n, e);
        }
    }

    /**
     * Visit all {@link ArrayInitializerExpr}s.
     * <p>
     * Ex. {@code new int[][] {{1, 1}, {2, 2}}}
     * </p>
     *
     * @param n   The current {@code ArrayInitializerExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(ArrayInitializerExpr n, Object arg)
    {
        super.visit(n, arg);
        try
        {
            ArrayParser.parseInitializer(compilationUnit, n, classFileContainer);
        }
        catch (Exception e)
        {
            printException(n, e);
        }
    }

    /**
     * Visit all {@link AssignExpr}s.
     * <p>
     * Ex. {@code a = 5}
     * </p>
     *
     * @param n   The current {@code AssignExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(AssignExpr n, Object arg)
    {
        super.visit(n, arg);
        try
        {
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
                AssignParser.parse(classFileContainer, n, method);

            if (staticInitializer != null)
                AssignParser.parseStatic(classFileContainer, n);
        }
        catch (Exception e)
        {
            printException(n, e);
        }
    }

    /**
     * Visit all {@link BinaryExpr}s.
     * <p>
     * Ex. {@code a && b}
     * </p>
     *
     * @param n   The current {@code BinaryExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(BinaryExpr n, Object arg)
    {
        super.visit(n, arg);
        try
        {
            CallableDeclaration<?> method = findMethodForExpression(n, this.compilationUnit);
            if (method == null)
            {
                method = findConstructorForExpression(n, this.compilationUnit);
            }

            if (method != null)
            {
                Expression leftExpr = n.getLeft();
                if (leftExpr instanceof NameExpr)
                {
                    NameExpr nameExpr = (NameExpr) leftExpr;
                    Range range = nameExpr.getName().getRange().orElse(null);
                    if (range == null)
                        return;

                    Value left = new Value(nameExpr.getName(), range);
                    putResolvedValues(classFileContainer, "reference", method, nameExpr, left);
                }

                Expression rightExpr = n.getRight();
                if (rightExpr instanceof NameExpr)
                {
                    NameExpr nameExpr = (NameExpr) rightExpr;
                    Range range = nameExpr.getName().getRange().orElse(null);
                    if (range == null)
                        return;

                    Value right = new Value(nameExpr.getName(), range);
                    putResolvedValues(classFileContainer, "reference", method, nameExpr, right);
                }
            }
        }
        catch (Exception e)
        {
            printException(n, e);
        }
    }

    /**
     * Visit all {@link CastExpr}s.
     * <p>
     * Ex. {@code (long) 15}
     * </p>
     *
     * @param n   The current {@code CastExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(CastExpr n, Object arg)
    {
        super.visit(n, arg);
        try
        {
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
                    Range range = nameExpr.getName().getRange().orElse(null);
                    if (range == null)
                        return;

                    Value value = new Value(nameExpr.getName(), range);
                    putResolvedValues(classFileContainer, "reference", method, nameExpr, value);
                }
            }
        }
        catch (Exception e)
        {
            printException(n, e);
        }
    }

    /**
     * Visit all {@link ClassOrInterfaceDeclaration}s.
     * <p>
     * Ex. {@code class X{...}}
     * </p>
     *
     * @param n   The current {@code ClassOrInterfaceDeclaration}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(ClassOrInterfaceDeclaration n, Object arg)
    {
        super.visit(n, arg);
        try
        {
            SimpleName name = n.getName();
            Range range = name.getRange().orElse(null);
            if (range == null)
                return;

            Value value = new Value(n.getName(), range);

            ResolvedReferenceTypeDeclaration resolve = n.resolve();
            this.classFileContainer.putClassReference(resolve.getName(),
                    new ClassReferenceLocation(getOwner(classFileContainer),
                            resolve.getPackageName(), "", "declaration", value.line, value.columnStart, value.columnEnd + 1));
        }
        catch (Exception e)
        {
            printException(n, e);
        }
    }

    /**
     * Visit all {@link ClassOrInterfaceType}s.
     * <p>
     * Examples:<br>
     * {@code Object}<br>
     * {@code HashMap<String, String>}<br>
     * {@code java.util.Punchcard}
     * </p>
     *
     * @param n   The current {@code ClassOrInterfaceType}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(ClassOrInterfaceType n, Object arg)
    {
        super.visit(n, arg);
        try
        {
            Range range = n.getName().getRange().orElse(null);
            if (range == null)
                return;

            Value classValue = new Value(n.getName(), range);

            ResolvedType resolve = n.resolve();

            if (!resolve.isReferenceType())
                return;

            ResolvedReferenceType referenceType = resolve.asReferenceType();

            // Anonymous class
            if (!referenceType.hasName())
                return;

            String qualifiedName = referenceType.getQualifiedName();
            String packagePath = "";
            if (qualifiedName.contains("."))
                packagePath = qualifiedName.substring(0, qualifiedName.lastIndexOf('.')).replace('.', '/');

            this.classFileContainer.putClassReference(classValue.name,
                    new ClassReferenceLocation(getOwner(classFileContainer),
                            packagePath, "", "reference", classValue.line, classValue.columnStart, classValue.columnEnd + 1));
        }
        catch (Exception e)
        {
            printException(n, e);
        }
    }

    /**
     * Visit all {@link ConditionalExpr}s.
     * <p>
     * Ex. {@code if (a)}
     * </p>
     *
     * @param n   The current {@code ConditionalExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(ConditionalExpr n, Object arg)
    {
        super.visit(n, arg);
        try
        {
            ConditionalParser.parse(compilationUnit, n, classFileContainer);
        }
        catch (Exception e)
        {
            printException(n, e);
        }
    }

    /**
     * Visit all {@link ConstructorDeclaration}s.
     * <p>
     * Ex. {@code X { X(){} } }
     * </p>
     *
     * @param n   The current {@code ConstructorDeclaration}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(ConstructorDeclaration n, Object arg)
    {
        super.visit(n, arg);
        try
        {
            Node node = n.getParentNode().orElse(null);
            if (node == null)
            {
                System.err.println("ConstructorDeclaration: parent node is null");
                return;
            }

            if (node instanceof ObjectCreationExpr)
            {
                NodeList<BodyDeclaration<?>> bodyDeclarations =
                    ((ObjectCreationExpr) node).getAnonymousClassBody().orElse(null);
                if (bodyDeclarations != null)
                {
                    if (Objects.requireNonNull(bodyDeclarations.getFirst().orElse(null)).equals(n))
                        return;
                }
            }

            ResolvedConstructorDeclaration resolve = n.resolve();
            String signature = resolve.getQualifiedSignature();
            String parameters = "";
            if (resolve.getNumberOfParams() != 0)
                parameters = signature.substring(signature.indexOf('(') + 1, signature.lastIndexOf(')'));

            Range range = n.getName().getRange().orElse(null);
            if (range == null)
                return;

            Value constructor = new Value(n.getName(), range);
            this.classFileContainer.putMethod(constructor.name, new ClassMethodLocation(resolve.getClassName(),
                signature, parameters, "declaration", constructor.line, constructor.columnStart,
                constructor.columnEnd + 1));
        }
        catch (Exception e)
        {
            printException(n, e);
        }
    }

    /**
     * Visit all {@link DoStmt}s.
     * <p>
     * Ex. {@code do {...} while (a == 0)}
     * </p>
     *
     * @param n   The current {@code DoStmt}
     * @param arg Don't worry about it.
     */
    @Override
    public void visit(DoStmt n, Object arg)
    {
        super.visit(n, arg);
        if (DEBUG) System.err.println("DoStmt");
    }

    /**
     * Visit all {@link EnumDeclaration}s.
     * <p>
     * Ex. {@code enum X {...}}
     * </p>
     *
     * @param n   The current {@code EnumDeclaration}
     * @param arg Don't worry about it.
     */
    @Override
    public void visit(EnumDeclaration n, Object arg)
    {
        super.visit(n, arg);
        n.getEntries().forEach(entry ->
        {
            SimpleName simpleName = entry.getName();
            String name = simpleName.getIdentifier();
            Range range = simpleName.getRange().orElse(null);
            if (range == null)
                return;

            int line = range.begin.line;
            int columnStart = range.begin.column;
            int columnEnd = range.end.column;
            this.classFileContainer.putField(name, new ClassFieldLocation(getOwner(classFileContainer), "declaration", line, columnStart, columnEnd + 1));
        });
    }

    /**
     * Find all {@link ExplicitConstructorInvocationStmt}s.
     * <p>
     * Examples:<br>
     * {@code class X{ X(){super(15);} }}<br>
     * {@code class X{ X(){this(1, 2);} }}
     * </p>
     *
     * @param n   The current {@code ExplicitConstructorInvocationStmt}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(ExplicitConstructorInvocationStmt n, Object arg)
    {
        super.visit(n, arg);
        try
        {
            ConstructorDeclaration constructor = findConstructorForStatement(n, this.compilationUnit);
            if (constructor != null)
            {
                n.getArguments().forEach(argument ->
                {
                    if (argument instanceof NameExpr)
                    {
                        NameExpr nameExpr = (NameExpr) argument;
                        Range range = nameExpr.getName().getRange().orElse(null);
                        if (range == null)
                            return;

                        Value argumentValue = new Value(nameExpr.getName(), range);

                        putResolvedValues(classFileContainer, "reference", constructor, nameExpr, argumentValue);
                    }
                });
            }
        }
        catch (Exception e)
        {
            printException(n, e);
        }
    }

    /**
     * Visit all {@link FieldAccessExpr}s.
     * <p>
     * Ex. {@code person.name}
     * </p>
     *
     * @param n   The current {@code FieldAccessExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(FieldAccessExpr n, Object arg)
    {
        super.visit(n, arg);
        try
        {
            InitializerDeclaration initializer = findInitializerForExpression(n, this.compilationUnit);

            if (initializer == null)
                FieldAccessParser.parse(classFileContainer, n);
            else
                FieldAccessParser.parseStatic(classFileContainer, n);
        }
        catch (Exception e)
        {
            printException(n, e);
        }
    }

    /**
     * Visit all {@link FieldDeclaration}s.
     * <p>
     * Ex. {@code private static int a = 15}
     * </p>
     *
     * @param n   The current {@code FieldDeclaration}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(FieldDeclaration n, Object arg)
    {
        super.visit(n, arg);
        n.getVariables().forEach(variableDeclarator ->
        {
            Range range = variableDeclarator.getName().getRange().orElse(null);
            if (range == null)
                return;

            Value field = new Value(variableDeclarator.getName(), range);

            this.classFileContainer.putField(field.name, new ClassFieldLocation(getOwner(classFileContainer),
                "declaration", field.line, field.columnStart, field.columnEnd + 1));
        });
    }

    /**
     * Visit all {@link ForEachStmt}s.
     * <p>
     * Ex. {@code for (Object o : objects) {...}}
     * </p>
     *
     * @param n   The current {@code ForEachStmt}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(ForEachStmt n, Object arg)
    {
        super.visit(n, arg);
        try
        {
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
                    Range range = nameExpr.getName().getRange().orElse(null);
                    if (range == null)
                        return;

                    Value value = new Value(nameExpr.getName(), range);
                    putResolvedValues(classFileContainer, "reference", method, nameExpr, value);
                }
            }
        }
        catch (Exception e)
        {
            printException(n, e);
        }
    }

    /**
     * Visit all {@link IfStmt}s.
     * <p>
     * Ex. {@code if (a == 5) hurray() else boo()}
     * </p>
     *
     * @param n   The current {@code IfStmt}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(IfStmt n, Object arg)
    {
        super.visit(n, arg);
        try
        {
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
                    Range range = nameExpr.getName().getRange().orElse(null);
                    if (range == null)
                        return;

                    Value value = new Value(nameExpr.getName(), range);
                    putResolvedValues(classFileContainer, "reference", method, nameExpr, value);
                }
                else if (staticInitializer != null)
                {
                    Range range = nameExpr.getName().getRange().orElse(null);
                    if (range == null)
                        return;

                    Value value = new Value(nameExpr.getName(), range);
                    putResolvedValues(classFileContainer, "reference", nameExpr, value);
                }
            }
        }
        catch (Exception e)
        {
            printException(n, e);
        }
    }

    /**
     * Visit all {@link InstanceOfExpr}s.
     * <p>
     * Ex. {@code tool instanceof Drill}
     * </p>
     *
     * @param n   The current {@code InstanceOfExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(InstanceOfExpr n, Object arg)
    {
        super.visit(n, arg);
        try
        {
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
                    Range range = nameExpr.getName().getRange().orElse(null);
                    if (range == null)
                        return;

                    Value value = new Value(nameExpr.getName(), range);
                    putResolvedValues(classFileContainer, "reference", method, nameExpr, value);
                }
            }
        }
        catch (Exception e)
        {
            printException(n, e);
        }
    }

    /**
     * Visit all {@link IntersectionType}s.
     * <p>
     * Ex. {@code Serializable & Cloneable}
     * </p>
     *
     * @param n   The current {@code IntersectionType}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(IntersectionType n, Object arg)
    {
        super.visit(n, arg);
        if (DEBUG) System.err.println("IntersectionType");
    }

    /**
     * Visit all {@link LabeledStmt}s.
     * <p>
     * Ex. {@code label123: println("continuing")}
     * </p>
     *
     * @param n   The current {@code LabeledStmt}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(LabeledStmt n, Object arg)
    {
        super.visit(n, arg);
        if (DEBUG) System.err.println("LabeledStmt");
    }

    /**
     * Visit all {@link LambdaExpr}s.
     * <p>
     * Ex. {@code (a, b) -> a + b}
     * </p>
     *
     * @param n   The current {@code LambdaExpr}
     * @param arg Don't worry about it.
     */
    @Override
    public void visit(LambdaExpr n, Object arg)
    {
        super.visit(n, arg); // We already do parameters
    }

    /**
     * Visit all {@link LocalClassDeclarationStmt}s.
     * <p>
     * Ex. {@code class X { void m() { class Y() {} }}}
     * </p>
     *
     * @param n   The current {@code LocalClassDeclarationStmt}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(LocalClassDeclarationStmt n, Object arg)
    {
        super.visit(n, arg); // We already do class declarations
    }

    /**
     * Visit all {@link MarkerAnnotationExpr}s.
     * <p>
     * Ex. {@code @Override}
     * </p>
     *
     * @param n   The current {@code MarkerAnnotationExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(MarkerAnnotationExpr n, Object arg)
    {
        super.visit(n, arg);
        if (DEBUG) System.err.println("MarkerAnnotationExpr");
    }

    /**
     * Visit all {@link MethodCallExpr}s.
     * <p>
     * Ex. {@code circle.circumference()}
     * </p>
     *
     * @param n   The current {@code MethodCallExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(MethodCallExpr n, Object arg)
    {
        super.visit(n, arg);
        try
        {
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

            ResolvedMethodDeclaration resolve = n.resolve();
            String signature = resolve.getQualifiedSignature();
            String parameters = "";
            if (resolve.getNumberOfParams() != 0)
            {
                parameters = signature.substring(signature.indexOf('(') + 1, signature.lastIndexOf(')'));
            }

            Range methodRange = n.getName().getRange().orElse(null);
            if (methodRange == null)
                return;

            Value methodCall = new Value(n.getName(), methodRange);
            this.classFileContainer.putMethod(methodCall.name,
                new ClassMethodLocation(resolve.getClassName(), signature, parameters, "reference", methodCall.line,
                    methodCall.columnStart, methodCall.columnEnd + 1));

            if (method != null)
            {
                MethodCallParser.parse(classFileContainer, n, method);
            }
            else if (staticInitializer != null)
            {
                MethodCallParser.parseStatic(classFileContainer, n);
            }
        }
        catch (Exception e)
        {
            printException(n, e);
        }
    }

    /**
     * Visit all {@link MethodDeclaration}s.
     * <p>
     * Ex. {@code public int abc(){return 1;}}
     * </p>
     *
     * @param n   The current {@code MethodDeclaration}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(MethodDeclaration n, Object arg)
    {
        super.visit(n, arg);
        try
        {
            ResolvedMethodDeclaration resolve = n.resolve();
            String signature = resolve.getQualifiedSignature();
            String parameters = "";
            if (resolve.getNumberOfParams() != 0)
            {
                parameters = signature.substring(signature.indexOf('(') + 1, signature.lastIndexOf(')'));
            }

            Range methodRange = n.getName().getRange().orElse(null);
            if (methodRange == null)
                return;

            Value method = new Value(n.getName(), methodRange);
            this.classFileContainer.putMethod(method.name, new ClassMethodLocation(resolve.getClassName(), signature,
                parameters, "declaration", method.line, method.columnStart, method.columnEnd + 1));
        }
        catch (Exception e)
        {
            printException(n, e);
        }
    }

    /**
     * Visit all {@link MethodReferenceExpr}s.
     * <p>
     * Ex. {@code System.out::println}
     * </p>
     *
     * @param n   The current {@code MethodReferenceExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(MethodReferenceExpr n, Object arg)
    {
        super.visit(n, arg);
        if (DEBUG) System.err.println("MethodReferenceExpr");
    }


    @Override
    public void visit(NormalAnnotationExpr n, Object arg)
    {
        super.visit(n, arg);
        if (DEBUG) System.err.println("NormalAnnotationExpr");
    }

    /**
     * Visit all {@link ObjectCreationExpr}s.
     * <p>
     * Ex. {@code new HashMap.Entry(15)}
     * </p>
     *
     * @param n   The current {@code ObjectCreationExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(ObjectCreationExpr n, Object arg)
    {
        super.visit(n, arg);
        try
        {
            CallableDeclaration<?> method = findMethodForExpression(n, this.compilationUnit);
            if (method == null)
            {
                method = findConstructorForExpression(n, this.compilationUnit);
            }

            if (method != null)
            {
                CallableDeclaration<?> finalMethod = method;
                n.getArguments().forEach(argument ->
                {
                    if (argument instanceof NameExpr)
                    {
                        NameExpr nameExpr = (NameExpr) argument;
                        Range range = nameExpr.getName().getRange().orElse(null);
                        if (range == null)
                        {
                            return;
                        }

                        Value argumentValue = new Value(nameExpr.getName(), range);
                        putResolvedValues(classFileContainer, "reference", finalMethod, nameExpr, argumentValue);
                    }
                });
            }
        }
        catch (Exception e)
        {
            printException(n, e);
        }
    }

    /**
     * Visit all {@link Parameter}s.
     * <p>
     * Ex. {@code int abc(String x)}
     * </p>
     *
     * @param n   The current {@code Parameter}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(Parameter n, Object arg)
    {
        super.visit(n, arg);
        ParameterParser.parse(compilationUnit, n, classFileContainer);
    }

    /**
     * Visit all {@link ReturnStmt}s.
     * <p>
     * Ex. {@code return 5 * 5}
     * </p>
     *
     * @param n   The current {@code ReturnStmt}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(ReturnStmt n, Object arg)
    {
        super.visit(n, arg);
        try
        {
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
                    Range range = nameExpr.getName().getRange().orElse(null);
                    if (range == null)
                        return;

                    Value value = new Value(nameExpr.getName(), range);
                    putResolvedValues(classFileContainer, "reference", method, nameExpr, value);
                }
            }
        }
        catch (Exception e)
        {
            printException(n, e);
        }
    }

    @Override
    public void visit(SingleMemberAnnotationExpr n, Object arg)
    {
        super.visit(n, arg);
        if (DEBUG) System.err.println("SingleMemberAnnotationExpr");
    }

    /**
     * Visit all {@link ThrowStmt}s.
     * <p>
     * Ex. {@code throw new Exception()}
     * </p>
     *
     * @param n   The current {@code ThrowStmt}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(ThrowStmt n, Object arg)
    {
        super.visit(n, arg);
        try
        {
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
                    Range range = nameExpr.getName().getRange().orElse(null);
                    if (range == null)
                        return;

                    Value value = new Value(nameExpr.getName(), range);
                    putResolvedValues(classFileContainer, "reference", method, nameExpr, value);
                }
            }
        }
        catch (Exception e)
        {
            printException(n, e);
        }
    }

    @Override
    public void visit(TypeExpr n, Object arg)
    {
        super.visit(n, arg);
        if (DEBUG) System.err.println("TypeExpr");
    }

    @Override
    public void visit(TypeParameter n, Object arg)
    {
        super.visit(n, arg);

        Range range = n.getName().getRange().orElse(null);
        if (range == null)
            return;

        Value typeParameter = new Value(n.getName(), range);
        // TODO: Figure out the best way to implement this.
        if (DEBUG) System.err.println("TypeParameter");
    }

    /**
     * Visit all {@link UnaryExpr}s.
     * <p>
     * Ex. {@code 11++}
     * </p>
     *
     * @param n   The current {@code UnaryExpr}
     * @param arg Don't worry about it
     */
    @Override
    public void visit(UnaryExpr n, Object arg)
    {
        super.visit(n, arg);
        try
        {
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
                    Range range = nameExpr.getName().getRange().orElse(null);
                    if (range == null)
                        return;

                    Value value = new Value(nameExpr.getName(), range);
                    putResolvedValues(classFileContainer, "reference", method, nameExpr, value);
                }
            }
        }
        catch (Exception e)
        {
            printException(n, e);
        }
    }

    /**
     * Visit all {@link VariableDeclarationExpr}s.
     * <p>
     * Ex. {@code final int x = 3, y = 55}
     * </p>
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
                Range range = variable.getName().getRange().orElse(null);
                if (range == null)
                    return;

                Value value = new Value(variable.getName(), range);
                putLocalVariable(classFileContainer, value, getMethod(finalMethod), "declaration");
            });
        }
        else if (staticInitializer != null)
        {
            n.getVariables().forEach(variable -> {
                Range range = variable.getName().getRange().orElse(null);
                if (range == null)
                    return;

                Value value = new Value(variable.getName(), range);
                putLocalVariable(classFileContainer, value, "static", "declaration");
            });
        }
    }
}
