package the.bytecode.club.bytecodeviewer.resources.classcontainer.locations;

/**
 * Created by Bl3nd.
 * Date: 9/5/2024
 */
public class ClassMethodLocation
{
    public final String owner;
    public final String signature;
    public final String methodParameterTypes;
    public final String decRef;
    public final int line;
    public final int columnStart;
    public final int columnEnd;

    public ClassMethodLocation(String owner, String signature, String methodParameterTypes, String decRef, int line, int columnStart, int columnEnd)
    {
        this.owner = owner;
        this.signature = signature;
        this.methodParameterTypes = methodParameterTypes;
        this.decRef = decRef;
        this.line = line;
        this.columnStart = columnStart;
        this.columnEnd = columnEnd;
    }
}
