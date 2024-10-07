package the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.visitors;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.expr.*;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.ClassFileContainer;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.visitors.ParserUtil.Value;

import static the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.visitors.ParserUtil.*;

/**
 * Created by Bl3nd.
 * Date: 10/1/2024
 */
class ArrayParser
{

    static void parseAccess(CompilationUnit compilationUnit, ArrayAccessExpr expr, ClassFileContainer container)
    {
        Expression valueExp = expr.getName();
        if (valueExp instanceof NameExpr)
        {
            NameExpr nameExpr = (NameExpr) valueExp;
            CallableDeclaration<?> method = findMethodForExpression(expr, compilationUnit);
            if (method == null)
            {
                method = findConstructorForExpression(expr, compilationUnit);
            }

            if (method == null)
            {
                System.err.println("ArrayAccess1 - Method not found");
                return;
            }

            Range range = nameExpr.getName().getRange().orElse(null);
            if (range == null)
                return;

            Value nameValue = new Value(nameExpr.getName(), range);
            putResolvedValues(container, "reference", method, nameExpr, nameValue);
        }

        Expression indexExp = expr.getIndex();
        if (indexExp instanceof NameExpr)
        {
            NameExpr nameExpr = (NameExpr) indexExp;
            CallableDeclaration<?> method = findMethodForExpression(expr, compilationUnit);
            if (method == null)
                method = findConstructorForExpression(expr, compilationUnit);

            if (method == null)
            {
                System.err.println("ArrayAccess2 - Method not found");
                return;
            }

            Range range = nameExpr.getName().getRange().orElse(null);
            if (range == null)
                return;

            Value indexValue = new Value(nameExpr.getName(), range);
            putResolvedValues(container, "reference", method, nameExpr, indexValue);
        }
    }

    static void parseCreation(CompilationUnit compilationUnit, ArrayCreationExpr expr,
                                     ClassFileContainer container)
    {
        expr.getLevels().forEach(level -> {
            Expression dimensionExpr = level.getDimension().orElse(null);
            if (dimensionExpr instanceof NameExpr)
            {
                NameExpr nameExpr = (NameExpr) dimensionExpr;
                CallableDeclaration<?> method = findMethodForExpression(expr, compilationUnit);
                if (method == null)
                    method = findConstructorForExpression(expr, compilationUnit);

                if (method == null)
                {
                    System.err.println("ArrayCreation - Method not found");
                    return;
                }

                Range range = nameExpr.getName().getRange().orElse(null);
                if (range == null)
                    return;

                Value dimensionValue = new Value(nameExpr.getName(), range);
                putResolvedValues(container, "reference", method, nameExpr, dimensionValue);
            }
        });
    }

    static void parseInitializer(CompilationUnit compilationUnit, ArrayInitializerExpr expr,
                                        ClassFileContainer container)
    {
        expr.getValues().forEach(value -> {
            if (value instanceof NameExpr)
            {
                NameExpr nameExpr = (NameExpr) value;
                CallableDeclaration<?> method = findMethodForExpression(expr, compilationUnit);
                if (method == null)
                    method = findConstructorForExpression(expr, compilationUnit);

                if (method == null)
                {
                    System.err.println("ArrayInitializer - Method not found");
                    return;
                }

                Range range = nameExpr.getName().getRange().orElse(null);
                if (range == null)
                    return;

                Value valueValue = new Value(nameExpr.getName(), range);
                putResolvedValues(container, "reference", method, nameExpr, valueValue);
            }
        });
    }
}
