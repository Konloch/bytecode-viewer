package the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.visitors;

import com.github.javaparser.Range;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.ClassFileContainer;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.locations.ClassFieldLocation;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.locations.ClassLocalVariableLocation;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.locations.ClassParameterLocation;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.locations.ClassReferenceLocation;

import java.util.Objects;

import static the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.visitors.ParserUtil.*;

/**
 * Created by Bl3nd.
 * Date: 9/28/2024
 */
class FieldAccessParser
{

    /**
     * Solve a field that is accessed through a lambda and not within a method or constructor
     *
     * @param container The {@link ClassFileContainer}
     * @param expr The {@link FieldAccessExpr}
     * @param className The class name of the class that is accessing the field
     */
    static void parse(ClassFileContainer container, FieldAccessExpr expr, String className)
    {
        Range fieldRange = Objects.requireNonNull(expr.getTokenRange().orElse(null)).getEnd().getRange().orElse(null);
        if (fieldRange == null)
            return;

        Value fieldValue = new Value(expr.getName(), fieldRange);

        Expression scope = expr.getScope();
        if (scope instanceof NameExpr)
        {
            NameExpr nameExpr = (NameExpr) scope;
            Range scopeRange = nameExpr.getRange().orElse(null);
            if (scopeRange == null)
                return;

            Value scopeValue = new Value(nameExpr.getName(), scopeRange);
            try
            {
                ResolvedValueDeclaration vd = nameExpr.resolve();
                if (vd.isField())
                {
                    container.putField(scopeValue.name, new ClassFieldLocation(getOwner(container), "reference",
                        scopeValue.line, scopeValue.columnStart, scopeValue.columnEnd + 1));
                }
                else if (vd.isVariable())
                {
                    container.putLocalVariable(scopeValue.name, new ClassLocalVariableLocation(getOwner(container),
                        className, "reference", scopeValue.line, scopeValue.columnStart, scopeValue.columnEnd + 1));
                }
                else if (vd.isParameter())
                {
                    container.putParameter(scopeValue.name, new ClassParameterLocation(getOwner(container), className,
                        "reference", scopeValue.line, scopeValue.columnStart, scopeValue.columnEnd + 1));
                }

                putFieldResolvedValues(container, expr, nameExpr, fieldValue);
            }
            catch (Exception e)
            {
                printException(expr, e);
            }
        }
    }

    static void parse(ClassFileContainer container, FieldAccessExpr expr, CallableDeclaration<?> method)
    {
        Range fieldRange = Objects.requireNonNull(expr.getTokenRange().orElse(null)).getEnd().getRange().orElse(null);
        if (fieldRange == null)
            return;

        Value fieldValue = new Value(expr.getName(), fieldRange);

        Expression scope = expr.getScope();

        // Ex. Clazz.field -> Clazz or c.field -> c
        if (scope instanceof NameExpr)
        {
            NameExpr nameExpr = (NameExpr) scope;
            Range scopeRange = nameExpr.getRange().orElse(null);
            if (scopeRange == null)
                return;

            Value scopeValue = new Value(nameExpr.getName(), scopeRange);

            try
            {
                // Scope
                putResolvedValues(container, "reference", method, nameExpr, scopeValue);

                // Field
                putFieldResolvedValues(container, expr, nameExpr, fieldValue);
            }
            catch (UnsolvedSymbolException ignore)
            {
                try
                {
                    putClassResolvedValues(container, expr, nameExpr, scopeValue, fieldValue);
                }
                catch (UnsolvedSymbolException e)
                {
                    printException(expr, e);
                }
            }
        } // Ex. this.field
        else if (scope instanceof ThisExpr)
        {
            ThisExpr thisExpr = (ThisExpr) scope;
            try
            {
                putFieldResolvedValues(container, expr, thisExpr, fieldValue);
            }
            catch (UnsolvedSymbolException e)
            {
                printException(expr, e);
            }
        }
        else if (scope instanceof EnclosedExpr)
        {
            EnclosedExpr enclosedExpr = (EnclosedExpr) scope;
            try
            {
                putFieldResolvedValues(container, expr, enclosedExpr, fieldValue);
            }
            catch (UnsolvedSymbolException e)
            {
                printException(expr, e);
            }
        }
        else
        {
            try
            {
                // If the scope is something like 'this.field1' and similar
                ResolvedType resolvedType = expr.getScope().calculateResolvedType();
                if (!resolvedType.isReferenceType())
                    return;

                String qualifiedName = resolvedType.asReferenceType().getQualifiedName();
                String className = qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
                // If the class is not registered as a reference yet
                if (container.getClassReferenceLocationsFor(className) == null)
                {
                    String packageName = "";
                    if (qualifiedName.contains("."))
                        packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf('.')).replace('.', '/');

                    // For this purpose, we do not care about its line, columnStart or columnEnd
                    container.putClassReference(className, new ClassReferenceLocation(className, packageName,
                        fieldValue.name, "reference", -1, -1, -1));
                }

                container.putField(fieldValue.name, new ClassFieldLocation(className, "reference", fieldValue.line,
                    fieldValue.columnStart, fieldValue.columnEnd + 1));
            }
            catch (Exception e)
            {
                printException(expr, e);
            }
        }
    }

    static void parseStatic(ClassFileContainer container, FieldAccessExpr expr)
    {
        Range fieldRange = Objects.requireNonNull(expr.getTokenRange().orElse(null)).getEnd().getRange().orElse(null);
        if (fieldRange == null)
            return;

        Value fieldValue = new Value(expr.getName(), fieldRange);

        Expression scope = expr.getScope();

        if (scope instanceof NameExpr)
        {
            NameExpr nameExpr = (NameExpr) scope;
            Range scopeRange = nameExpr.getRange().orElse(null);
            if (scopeRange == null)
                return;

            Value scopeValue = new Value(nameExpr.getName(), scopeRange);

            try
            {
                putClassResolvedValues(container, expr, nameExpr, scopeValue, fieldValue);
            }
            catch (UnsolvedSymbolException ignore)
            {
                try
                {
                    putClassResolvedValues(container, expr, nameExpr, scopeValue, fieldValue);
                }
                catch (UnsolvedSymbolException e)
                {
                    printException(expr, e);
                }
            }
        }
        else if (scope instanceof ThisExpr)
        {
            ThisExpr thisExpr = (ThisExpr) scope;
            try
            {
                putFieldResolvedValues(container, expr, thisExpr, fieldValue);
            }
            catch (UnsolvedSymbolException e)
            {
                printException(expr, e);
            }
        }
    }
}
