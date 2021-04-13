package jd.cli.util;

public class CommonTypeNameUtil {
    public static String InternalPathToQualifiedTypeName(String internalPath) {
        String internalTypeName = internalPath.substring(0, internalPath.length() - 6);
        return InternalTypeNameToQualifiedTypeName(internalTypeName);
    }

    public static String InternalTypeNameToQualifiedTypeName(String path) {
        return path.replace('/', '.').replace('$', '.');
    }
}
