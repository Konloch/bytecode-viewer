package the.bytecode.club.bytecodeviewer.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Konloch
 * @since 10/2/2024
 */
public class ExceptionUtils
{
    public static String exceptionToString(Throwable e)
    {
        StringWriter exceptionWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(exceptionWriter));
        e.printStackTrace();

        return exceptionWriter.toString();
    }
}
