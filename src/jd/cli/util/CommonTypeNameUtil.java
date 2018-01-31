package jd.cli.util;

import jd.core.util.TypeNameUtil;

public class CommonTypeNameUtil {
    public static String InternalPathToQualifiedTypeName(String internalPath) {
        String internalTypeName = internalPath.substring(0, internalPath.length() - 6);
        return TypeNameUtil.InternalTypeNameToQualifiedTypeName(internalTypeName);
    }
}
