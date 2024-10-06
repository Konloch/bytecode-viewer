package the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.visitors;

import com.github.javaparser.Range;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.ClassFileContainer;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.visitors.ParserUtil.Value;

import static the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.visitors.ParserUtil.*;

/**
 * Created by Bl3nd.
 * Date: 9/28/2024
 */
class MethodCallParser
{

    static void parse(ClassFileContainer container, MethodCallExpr expr, CallableDeclaration<?> method)
    {
        if (expr.hasScope())
        {
            Expression scope = expr.getScope().orElse(null);

            /*
            Ex.
            field.method -> field
            variable.method -> variable
            parameter.method -> parameter
             */
            if (scope instanceof NameExpr)
            {
                NameExpr nameExpr = (NameExpr) scope;
                Range range = nameExpr.getName().getRange().orElse(null);
                if (range == null)
                    return;

                Value scopeValue = new Value(nameExpr.getName(), range);

                try
                {
                    putResolvedValues(container, "reference", method, nameExpr, scopeValue);
                }
                catch (UnsolvedSymbolException ignored)
                {
                    try
                    {
                        putClassResolvedValues(container, expr, nameExpr, scopeValue);
                    } catch (UnsolvedSymbolException e)
                    {
                        printException(expr, e);
                    }
                }
            }
        }

        // Ex. method(arg, arg, ...)
        expr.getArguments().forEach(argument ->
        {
            if (argument instanceof NameExpr)
            {
                NameExpr nameExpr = (NameExpr) argument;
                Range range = nameExpr.getName().getRange().orElse(null);
                if (range == null)
                    return;

                Value argName = new Value(nameExpr.getName(), range);
                try {
                    putResolvedValues(container, "reference", method, nameExpr, argName);
                } catch (UnsolvedSymbolException e)
                {
                    printException(expr, e);
                }
            }
        });
    }

    static void parseStatic(ClassFileContainer container, MethodCallExpr expr)
    {
        if (expr.hasScope())
        {
            /*
            Ex.
            field.method -> field
            variable.method -> variable
            parameter.method -> parameter
             */
            Expression scope = expr.getScope().orElse(null);
            if (scope instanceof NameExpr)
            {
                NameExpr nameExpr = (NameExpr) scope;
                Range range = nameExpr.getName().getRange().orElse(null);
                if (range == null)
                {
                    return;
                }

                Value scopeValue = new Value(nameExpr.getName(), range);

                try {
                    putResolvedValues(container, "reference", nameExpr, scopeValue);
                } catch (UnsolvedSymbolException ignored)
                {
                    try
                    {
                        putClassResolvedValues(container, expr, nameExpr, scopeValue);
                    } catch (UnsolvedSymbolException e)
                    {
                        printException(expr, e);
                    }
                }
            }
        }

        expr.getArguments().forEach(argument ->
        {
            if (argument instanceof NameExpr)
            {
                NameExpr nameExpr = (NameExpr) argument;
                Range range = nameExpr.getName().getRange().orElse(null);
                if (range == null)
                {
                    return;
                }

                Value argValue = new Value(nameExpr.getName(), range);

                try
                {
                    putResolvedValues(container, "reference", nameExpr, argValue);
                } catch (UnsolvedSymbolException e)
                {
                    printException(expr, e);
                }
            }
        });
    }
}
