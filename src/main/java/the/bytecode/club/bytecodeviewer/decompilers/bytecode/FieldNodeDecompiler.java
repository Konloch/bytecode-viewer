package the.bytecode.club.bytecodeviewer.decompilers.bytecode;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldNode;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 *                                                                         *
 * This program is free software: you can redistribute it and/or modify    *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

/**
 * @author Konloch
 * @author Bibl
 */

public class FieldNodeDecompiler {

    public static PrefixedStringBuilder decompile(PrefixedStringBuilder sb,
                                                  FieldNode f) {
        String s = getAccessString(f.access);
        sb.append(s);
        if (s.length() > 0)
            sb.append(" ");
        sb.append(Type.getType(f.desc).getClassName());
        sb.append(" ");
        sb.append(f.name);
        if (f.value != null) {
            sb.append(" = ");
            if (f.value instanceof String) {
                sb.append("\"");
                sb.append(f.value);
                sb.append("\"");
            } else {
                sb.append(f.value);
                sb.append(" (");
                sb.append(f.value.getClass().getCanonicalName());
                sb.append(")");
            }
        }
        sb.append(";");
        return sb;
    }

    private static String getAccessString(int access) {
        List<String> tokens = new ArrayList<>();
        if ((access & Opcodes.ACC_PUBLIC) != 0)
            tokens.add("public");
        if ((access & Opcodes.ACC_PRIVATE) != 0)
            tokens.add("private");
        if ((access & Opcodes.ACC_PROTECTED) != 0)
            tokens.add("protected");
        if ((access & Opcodes.ACC_SYNTHETIC) != 0)
            tokens.add("synthetic");
        if ((access & Opcodes.ACC_STATIC) != 0)
            tokens.add("static");
        if ((access & Opcodes.ACC_FINAL) != 0)
            tokens.add("final");
        if ((access & Opcodes.ACC_TRANSIENT) != 0)
            tokens.add("transient");
        if ((access & Opcodes.ACC_VOLATILE) != 0)
            tokens.add("volatile");
        if (tokens.size() == 0)
            return "";
        // hackery delimeters
        StringBuilder sb = new StringBuilder(tokens.get(0));
        for (int i = 1; i < tokens.size(); i++) {
            sb.append(" ");
            sb.append(tokens.get(i));
        }
        return sb.toString();
    }
}