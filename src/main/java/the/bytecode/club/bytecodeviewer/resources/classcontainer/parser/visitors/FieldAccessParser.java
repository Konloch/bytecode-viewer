package the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.visitors;

import com.github.javaparser.Range;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.ClassFileContainer;

import java.util.Objects;

import static the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.visitors.ParserUtil.*;

/**
 * Created by Bl3nd.
 * Date: 9/28/2024
 */
class FieldAccessParser
{

    static void parse(ClassFileContainer container, FieldAccessExpr expr)
    {
        Range fieldRange = Objects.requireNonNull(expr.getTokenRange().orElse(null)).getEnd().getRange().orElse(null);
        if (fieldRange == null)
            return;

        Value fieldValue = new Value(expr.getName(), fieldRange);

        Expression scope = expr.getScope();

        // Ex. Clazz.field -> Clazz
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
        } // Ex. this.field
        else if (scope instanceof ThisExpr)
        {
            ThisExpr thisExpr = (ThisExpr) scope;
            try
            {
                putFieldResolvedValues(container, expr, thisExpr, fieldValue);
            } catch (UnsolvedSymbolException e)
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
            } catch (UnsolvedSymbolException e)
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
                } catch (UnsolvedSymbolException e) {
                    printException(expr, e);
                }
            }
        }
        else if (scope instanceof ThisExpr)
        {
            ThisExpr thisExpr = (ThisExpr) scope;
            try {
                putFieldResolvedValues(container, expr, thisExpr, fieldValue);
            } catch (UnsolvedSymbolException e) {
                printException(expr, e);
            }
        }
    }
}
