package the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.visitors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.SimpleName;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.ClassFileContainer;

import static the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.visitors.ParserUtil.*;

/**
 * Created by Bl3nd.
 * Date: 10/1/2024
 */
public class ParameterParser
{

    public static void parse(CompilationUnit compilationUnit, Parameter p, ClassFileContainer container)
    {
        Node node = p.getParentNode().orElse(null);
        if (node == null)
            return;

        String methodName = findMethodOwnerFor(compilationUnit, node);
        if (methodName == null) {
            System.err.println("Parameter - Method not found");
            return;
        }

        SimpleName name = p.getName();
        name.getRange().ifPresent(range -> {
            Value parameter = new Value(name, range);
            putParameter(container, parameter, methodName, "declaration");
        });
    }
}
