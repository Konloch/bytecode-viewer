package the.bytecode.club.bytecodeviewer.resources.classcontainer.locations;

/**
 * Created by Bl3nd.
 * Date: 9/5/2024
 */
public class ClassLocalVariableLocation
{
    public final String owner;
    public final String method;
    public final String decRef;
    public final int line;
    public final int columnStart;
    public final int columnEnd;

    public ClassLocalVariableLocation(String owner, String method, String decRef, int line, int columnStart, int columnEnd)
    {
        this.owner = owner;
        this.method = method;
        this.decRef = decRef;
        this.line = line;
        this.columnStart = columnStart;
        this.columnEnd = columnEnd;
    }
}
