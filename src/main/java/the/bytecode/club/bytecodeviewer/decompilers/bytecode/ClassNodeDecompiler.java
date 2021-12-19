package the.bytecode.club.bytecodeviewer.decompilers.bytecode;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;

import static the.bytecode.club.bytecodeviewer.Constants.nl;

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

public class ClassNodeDecompiler
{
    public static PrefixedStringBuilder decompile(
            PrefixedStringBuilder sb, List<String> decompiledClasses,
            ClassNode cn) {
        List<String> unableToDecompile = new ArrayList<>();
        decompiledClasses.add(cn.name);
        sb.append(getAccessString(cn.access));
        sb.append(" ");
        sb.append(cn.name);
        if (cn.superName != null && !cn.superName.equals("java/lang/Object")) {
            sb.append(" extends ");
            sb.append(cn.superName);
        }

        int amountOfInterfaces = cn.interfaces.size();
        if (amountOfInterfaces > 0) {
            sb.append(" implements ");
            sb.append(cn.interfaces.get(0));
            for (int i = 1; i < amountOfInterfaces; i++) {
                sb.append(", ");
                sb.append(cn.interfaces.get(i));
            }
        }
        sb.append(" {");
        sb.append(nl);
        sb.append("     ");
        sb.append("<ClassVersion=" + cn.version + ">");
        sb.append(nl);

        if (cn.sourceDebug != null) {
            sb.append("     ");
            sb.append("<SourceDebug=" + cn.sourceDebug + ">");
            sb.append(nl);
        }

        if (cn.sourceFile != null) {
            sb.append("     ");
            sb.append("<SourceFile=" + cn.sourceFile + ">");
            sb.append(nl);
        }

        if (cn.signature != null) {
            sb.append("     ");
            sb.append("<Sig=" + cn.signature + ">");
        }

        for (FieldNode fn : cn.fields) {
            sb.append(nl);
            sb.append("     ");
            FieldNodeDecompiler.decompile(sb, fn);
        }
        if (cn.fields.size() > 0) {
            sb.append(nl);
        }
        for (MethodNode mn : cn.methods) {
            sb.append(nl);
            MethodNodeDecompiler.decompile(sb, mn, cn);
        }

        for (InnerClassNode o : cn.innerClasses) {
            String innerClassName = o.name;
            if ((innerClassName != null)
                    && !decompiledClasses.contains(innerClassName)) {
                decompiledClasses.add(innerClassName);
                ClassNode cn1 = BytecodeViewer.blindlySearchForClassNode(innerClassName);
                if (cn1 != null) {
                    sb.appendPrefix("     ");
                    sb.append(nl + nl);
                    sb = decompile(sb, decompiledClasses, cn1);
                    sb.trimPrefix(5);
                    sb.append(nl);
                } else {
                    unableToDecompile.add(innerClassName);
                }
            }
        }

        if (!unableToDecompile.isEmpty()) {
            sb.append("// The following inner classes couldn't be decompiled: ");
            for (String s : unableToDecompile) {
                sb.append(s);
                sb.append(" ");
            }
            sb.append(nl);
        }

        if (cn.attrs != null) {
            sb.append(nl);
            for (Attribute attr : cn.attrs) {
                //TODO: finish attributes
                sb.append(attr.type + ": ");// + attr.content.toString());
            }
        }

        //sb.append(BytecodeViewer.nl);
        sb.append("}");
        // System.out.println("Wrote end for " + cn.name +
        // " with prefix length: " + sb.prefix.length());
        return sb;
    }

    public static String getAccessString(int access) {
        List<String> tokens = new ArrayList<>();
        if ((access & Opcodes.ACC_PUBLIC) != 0)
            tokens.add("public");
        if ((access & Opcodes.ACC_PRIVATE) != 0)
            tokens.add("private");
        if ((access & Opcodes.ACC_PROTECTED) != 0)
            tokens.add("protected");
        if ((access & Opcodes.ACC_FINAL) != 0)
            tokens.add("final");
        if ((access & Opcodes.ACC_SYNTHETIC) != 0)
            tokens.add("synthetic");
        // if ((access & Opcodes.ACC_SUPER) != 0)
        // tokens.add("super"); implied by invokespecial insn
        if ((access & Opcodes.ACC_ABSTRACT) != 0)
            tokens.add("abstract");
        if ((access & Opcodes.ACC_INTERFACE) != 0)
            tokens.add("interface");
        if ((access & Opcodes.ACC_ENUM) != 0)
            tokens.add("enum");
        if ((access & Opcodes.ACC_ANNOTATION) != 0)
            tokens.add("annotation");
        if (!tokens.contains("interface") && !tokens.contains("enum")
                && !tokens.contains("annotation"))
            tokens.add("class");

        // hackery delimeters
        StringBuilder sb = new StringBuilder(tokens.get(0));
        for (int i = 1; i < tokens.size(); i++) {
            sb.append(" ");
            sb.append(tokens.get(i));
        }
        return sb.toString();
    }
}
