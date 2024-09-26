package the.bytecode.club.bytecodeviewer.resources.classcontainer.locations;

/**
 * Created by Bl3nd.
 * Date: 9/20/2024
 */
public class ClassReferenceLocation
{
    public final String owner;
    public final String packagePath;
    public final String fieldName;
    public final String type;
    public final int line;
    public final int columnStart;
    public final int columnEnd;

    public ClassReferenceLocation(String owner, String packagePath, String fieldName, String type, int line, int columnStart, int columnEnd)
    {
        this.owner = owner;
        this.packagePath = packagePath;
        this.fieldName = fieldName;
        this.type = type;
        this.line = line;
        this.columnStart = columnStart;
        this.columnEnd = columnEnd;
    }

    @Override
    public String toString()
    {
        return "ClassClassLocation{" + "owner='" + owner + '\'' + ", fieldName='" + fieldName + '\'' + ", type='" + type + '\'' + ", line=" + line + ", columnStart=" + columnStart + ", columnEnd=" + columnEnd + '}';
    }
}
