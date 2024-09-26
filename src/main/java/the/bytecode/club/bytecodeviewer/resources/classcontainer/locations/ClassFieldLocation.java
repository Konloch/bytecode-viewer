package the.bytecode.club.bytecodeviewer.resources.classcontainer.locations;

/**
 * Created by Bl3nd.
 * Date: 8/26/2024
 */
public class ClassFieldLocation
{
    public final String owner;
    public final String type;
    public final int line;
    public final int columnStart;
    public final int columnEnd;

    public ClassFieldLocation(final String owner, final String type, final int line, final int columnStart, final int columnEnd)
    {
        this.owner = owner;
        this.type = type;
        this.line = line;
        this.columnStart = columnStart;
        this.columnEnd = columnEnd;
    }

    @Override
    public String toString()
    {
        return "ClassFieldLocation{" + "owner='" + owner + '\'' + ", type='" + type + '\'' + ", line=" + line + ", columnStart=" + columnStart + ", columnEnd=" + columnEnd + '}';
    }
}
