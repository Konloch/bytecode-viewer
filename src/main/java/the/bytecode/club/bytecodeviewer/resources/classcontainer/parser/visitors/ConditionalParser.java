package the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.visitors;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.ClassFileContainer;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.visitors.ParserUtil.Value;

import static the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.visitors.ParserUtil.*;

/**
 * Created by Bl3nd.
 * Date: 10/1/2024
 */
class ConditionalParser
{

    static void parse(CompilationUnit compilationUnit, ConditionalExpr expr, ClassFileContainer container)
    {
        CallableDeclaration<?> method = findMethodForExpression(expr, compilationUnit);
        if (method == null)
            method = findConstructorForExpression(expr, compilationUnit);

        if (method == null)
            return;

        Expression elseExpr = expr.getElseExpr();
        if (elseExpr instanceof NameExpr)
        {
            NameExpr nameExpr = (NameExpr) elseExpr;
            Range range = nameExpr.getName().getRange().orElse(null);
            if (range == null)
                return;

            Value elseValue = new Value(nameExpr.getName(), range);
            putResolvedValues(container, "reference", method, nameExpr, elseValue);
        }

        Expression thenExpr = expr.getThenExpr();
        if (thenExpr instanceof NameExpr)
        {
            NameExpr nameExpr = (NameExpr) thenExpr;
            Range range = nameExpr.getName().getRange().orElse(null);
            if (range == null)
                return;

            Value thenValue = new Value(nameExpr.getName(), range);
            putResolvedValues(container, "reference", method, nameExpr, thenValue);
        }
    }
}
