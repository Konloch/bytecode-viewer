package the.bytecode.club.bytecodeviewer.resources.classcontainer.parser;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Bl3nd.
 * Date: 9/5/2024
 */
public class TokenUtil
{
    public static Token getToken(final RSyntaxTextArea textArea, final @NotNull Token token)
    {
        String lexeme = token.getLexeme();
        return lexeme.isEmpty()
            || lexeme.equals(".")
            || lexeme.equals("(")
            || lexeme.equals(")")
            || lexeme.equals("[")
            || lexeme.equals("~")
            || lexeme.equals("-")
            || lexeme.equals("+")
            || lexeme.equals(" ")
            || lexeme.equals(";")
            || lexeme.equals(",")
            || lexeme.equals(">") ? textArea.modelToToken(textArea.getCaretPosition() - 1) : token;
    }
}
