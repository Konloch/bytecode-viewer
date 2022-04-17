package the.bytecode.club.bytecodeviewer.decompilers.bytecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
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

public class MethodNodeDecompiler {

    public static PrefixedStringBuilder decompile(PrefixedStringBuilder sb,
                                                  MethodNode m, ClassNode cn) {
        String class_;
        if (cn.name.contains("/")) {
            class_ = cn.name.substring(cn.name.lastIndexOf("/") + 1);
        } else {
            class_ = cn.name;
        }

        String s = getAccessString(m.access);
        sb.append("     ");
        sb.append(s);
        if (s.length() > 0)
            sb.append(" ");

        if (m.name.equals("<init>")) {
            sb.append(class_);
        } else if (!m.name.equals("<clinit>")) {
            Type returnType = Type.getReturnType(m.desc);
            sb.append(returnType.getClassName());
            sb.append(" ");
            sb.append(m.name);
        }

        TypeAndName[] args = new TypeAndName[0];

        if (!m.name.equals("<clinit>")) {
            sb.append("(");

            final Type[] argTypes = Type.getArgumentTypes(m.desc);
            args = new TypeAndName[argTypes.length];

            for (int i = 0; i < argTypes.length; i++) {
                final Type type = argTypes[i];

                final TypeAndName tan = new TypeAndName();
                final String argName = "arg" + i;

                tan.name = argName;
                tan.type = type;

                args[i] = tan;

                sb.append(type.getClassName() + " " + argName
                        + (i < argTypes.length - 1 ? ", " : ""));
            }

            sb.append(")");
        }

        int amountOfThrows = m.exceptions.size();
        if (amountOfThrows > 0) {
            sb.append(" throws ");
            sb.append(m.exceptions.get(0));// exceptions is list<string>
            for (int i = 1; i < amountOfThrows; i++) {
                sb.append(", ");
                sb.append(m.exceptions.get(i));
            }
        }

        if (s.contains("abstract")) {
            sb.append(" {}");
            sb.append(" //");
            sb.append(m.desc);
            sb.append(nl);
        } else {

            sb.append(" {");

            if (BytecodeViewer.viewer.debugHelpers.isSelected()) {
                if (m.name.equals("<clinit>"))
                    sb.append(" // <clinit>");
                else if (m.name.equals("<init>"))
                    sb.append(" // <init>");
            }

            sb.append(" //");
            sb.append(m.desc);

            sb.append(nl);

            if (m.signature != null) {
                sb.append("         <sig:").append(m.signature).append(">");
            }

            if (m.annotationDefault != null) {
                sb.append(m.annotationDefault);
                sb.append("\n");
            }

            InstructionPrinter insnPrinter = new InstructionPrinter(m, args);

            addAttrList(m.attrs, "attr", sb, insnPrinter);
            addAttrList(m.invisibleAnnotations, "invisAnno", sb, insnPrinter);
            addAttrList(m.invisibleAnnotations, "invisLocalVarAnno", sb,
                    insnPrinter);
            addAttrList(m.invisibleTypeAnnotations, "invisTypeAnno", sb,
                    insnPrinter);
            addAttrList(m.localVariables, "localVar", sb, insnPrinter);
            addAttrList(m.visibleAnnotations, "visAnno", sb, insnPrinter);
            addAttrList(m.visibleLocalVariableAnnotations, "visLocalVarAnno",
                    sb, insnPrinter);
            addAttrList(m.visibleTypeAnnotations, "visTypeAnno", sb,
                    insnPrinter);

            List<TryCatchBlockNode> tryCatchBlocks = m.tryCatchBlocks;
            for (int i = 0; i < tryCatchBlocks.size(); i++) {
                TryCatchBlockNode o = tryCatchBlocks.get(i);
                sb.append("         ");
                sb.append("TryCatch").append(i).append(": L");
                sb.append(insnPrinter.resolveLabel(o.start));
                sb.append(" to L");
                sb.append(insnPrinter.resolveLabel(o.end));
                sb.append(" handled by L");
                sb.append(insnPrinter.resolveLabel(o.handler));
                sb.append(": ");
                if (o.type != null)
                    sb.append(o.type);
                else
                    sb.append("Type is null.");
                sb.append(nl);
            }
            for (String insn : insnPrinter.createPrint()) {
                sb.append("         ");
                sb.append(insn);
                sb.append(nl);
            }
            sb.append("     }" + nl);
        }
        return sb;
    }

    private static void addAttrList(List<?> list, String name,
                                    PrefixedStringBuilder sb, InstructionPrinter insnPrinter) {
        if (list == null)
            return;
        if (list.size() > 0) {
            for (Object o : list) {
                sb.append("         <");
                sb.append(name);
                sb.append(":");
                sb.append(printAttr(o, insnPrinter));
                sb.append(">");
                sb.append("\n");
            }
            sb.append("\n");
        }
    }

    private static String printAttr(Object o, InstructionPrinter insnPrinter) {
        if (o instanceof LocalVariableNode) {
            LocalVariableNode lvn = (LocalVariableNode) o;
            return "index=" + lvn.index + " , name=" + lvn.name + " , desc="
                    + lvn.desc + ", sig=" + lvn.signature + ", start=L"
                    + insnPrinter.resolveLabel(lvn.start) + ", end=L"
                    + insnPrinter.resolveLabel(lvn.end);
        } else if (o instanceof AnnotationNode) {
            AnnotationNode an = (AnnotationNode) o;
            StringBuilder sb = new StringBuilder();
            sb.append("desc = ");
            sb.append(an.desc);
            sb.append(" , values = ");
            if (an.values != null) {
                sb.append(Arrays.toString(an.values.toArray()));
            } else {
                sb.append("[]");
            }
            return sb.toString();
        }
        if (o == null)
            return "";
        return o.toString();
    }

    private static String getAccessString(int access) {
        // public, protected, private, abstract, static,
        // final, synchronized, native & strictfp are permitted
        List<String> tokens = new ArrayList<>();
        if ((access & Opcodes.ACC_PUBLIC) != 0)
            tokens.add("public");
        if ((access & Opcodes.ACC_PRIVATE) != 0)
            tokens.add("private");
        if ((access & Opcodes.ACC_PROTECTED) != 0)
            tokens.add("protected");
        if ((access & Opcodes.ACC_STATIC) != 0)
            tokens.add("static");
        if ((access & Opcodes.ACC_ABSTRACT) != 0)
            tokens.add("abstract");
        if ((access & Opcodes.ACC_FINAL) != 0)
            tokens.add("final");
        if ((access & Opcodes.ACC_SYNCHRONIZED) != 0)
            tokens.add("synchronized");
        if ((access & Opcodes.ACC_NATIVE) != 0)
            tokens.add("native");
        if ((access & Opcodes.ACC_STRICT) != 0)
            tokens.add("strictfp");
        if ((access & Opcodes.ACC_BRIDGE) != 0)
            tokens.add("bridge");
        if ((access & Opcodes.ACC_SYNTHETIC) != 0)
            tokens.add("synthetic");
        if ((access & Opcodes.ACC_VARARGS) != 0)
            tokens.add("varargs");
        if (tokens.isEmpty())
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
