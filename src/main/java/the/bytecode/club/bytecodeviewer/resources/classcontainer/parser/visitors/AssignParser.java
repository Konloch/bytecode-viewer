package the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.visitors;

import com.github.javaparser.Range;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.ClassFileContainer;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.visitors.ParserUtil.Value;

import static the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.visitors.ParserUtil.printException;
import static the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.visitors.ParserUtil.putResolvedValues;

/**
 * Created by Bl3nd.
 * Date: 9/29/2024
 */
class AssignParser
{

    static void parse(ClassFileContainer container, AssignExpr expr, CallableDeclaration<?> method)
    {
        if (expr.getValue() instanceof NameExpr)
        {
            NameExpr nameExpr = (NameExpr) expr.getValue();
            Range range = nameExpr.getName().getRange().orElse(null);
            if (range == null)
                return;

            Value value = new Value(nameExpr.getName(), range);
            try
            {
                putResolvedValues(container, "reference", method, nameExpr, value);
            }
            catch (UnsolvedSymbolException e)
            {
                printException(expr, e);
            }
        }

        if (expr.getTarget() instanceof NameExpr)
        {
            NameExpr nameExpr = (NameExpr) expr.getTarget();
            try
            {
                SimpleName simpleName = nameExpr.getName();
                Range range = simpleName.getRange().orElse(null);
                if (range == null)
                    return;

                Value target = new Value(nameExpr.getName(), range);
                putResolvedValues(container, "reference", method, nameExpr, target);
            }
            catch (UnsolvedSymbolException e)
            {
                printException(expr, e);
            }
        }
    }

    static void parseStatic(ClassFileContainer container, AssignExpr expr)
    {
        if (expr.getValue() instanceof NameExpr)
        {
            NameExpr nameExpr = (NameExpr) expr.getValue();
            Range range = nameExpr.getName().getRange().orElse(null);
            if (range == null)
                return;

            Value value = new Value(nameExpr.getName(), range);
            try
            {
                putResolvedValues(container, "reference", nameExpr, value);
            }
            catch (UnsolvedSymbolException e)
            {
                printException(expr, e);
            }
        }

        if (expr.getTarget() instanceof NameExpr)
        {
            NameExpr nameExpr = (NameExpr) expr.getTarget();
            try
            {
                Range range = nameExpr.getName().getRange().orElse(null);
                if (range == null)
                    return;

                Value target = new Value(nameExpr.getName(), range);
                putResolvedValues(container, "reference", nameExpr, target);
            }
            catch (UnsolvedSymbolException e)
            {
                printException(expr, e);
            }
        }
    }
}
