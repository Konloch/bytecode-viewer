package the.bytecode.club.bytecodeviewer.decompilers.bytecode;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.DecompilerSettings;
import the.bytecode.club.bytecodeviewer.FileContainer;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;

import java.util.ArrayList;
import java.util.List;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 * *
 * This program is free software: you can redistribute it and/or modify    *
 * it under the terms of the GNU General Public License as published by  *
 * the Free Software Foundation, either version 3 of the License, or     *
 * (at your option) any later version.                                   *
 * *
 * This program is distributed in the hope that it will be useful,       *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 * GNU General Public License for more details.                          *
 * *
 * You should have received a copy of the GNU General Public License     *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

/**
 *
 * @author Konloch
 * @author Bibl
 *
 */

public class ClassNodeDecompiler extends Decompiler {

    public ClassNodeDecompiler() {
        for (Settings setting : Settings.values()) {
            settings.registerSetting(setting);
        }
    }

    @Override
    public String getName() {
        return "Bytecode";
    }

    public String decompileClassNode(ClassNode cn, byte[] b) {
        String containerName = null;
        for (FileContainer container : BytecodeViewer.files) {
            String name = cn.name + ".class";
            if (container.getData().containsKey(name)) {
                if (container.getClassNode(name) == cn)
                    containerName = container.name;
            }
        }
        System.out.println(containerName);

        return decompile(new PrefixedStringBuilder(), new ArrayList<String>(), containerName, cn).toString();
    }

    protected static PrefixedStringBuilder decompile(PrefixedStringBuilder sb, ArrayList<String> decompiledClasses, String containerName, ClassNode cn) {
        ArrayList<String> unableToDecompile = new ArrayList<String>();
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
            if (amountOfInterfaces > 1) {
                // sb.append(",");
            }
            for (int i = 1; i < amountOfInterfaces; i++) {
                sb.append(", ");
                sb.append(cn.interfaces.get(i));
            }
        }
        sb.append(" {");
        sb.append(BytecodeViewer.nl);
        for (FieldNode fn : (List<FieldNode>) cn.fields) {
            sb.append(BytecodeViewer.nl);
            sb.append("     ");
            FieldNodeDecompiler.decompile(sb, fn);
        }
        if (cn.fields.size() > 0) {
            sb.append(BytecodeViewer.nl);
        }
        for (MethodNode mn : (List<MethodNode>) cn.methods) {
            sb.append(BytecodeViewer.nl);
            MethodNodeDecompiler.decompile(sb, mn, cn);
        }

        for (Object o : cn.innerClasses) {
            InnerClassNode innerClassNode = (InnerClassNode) o;
            String innerClassName = innerClassNode.name;
            if ((innerClassName != null) && !decompiledClasses.contains(innerClassName)) {
                decompiledClasses.add(innerClassName);
                ClassNode cn1 = BytecodeViewer.getClassNode(containerName, innerClassName);
                if (cn1 != null) {
                    sb.appendPrefix("     ");
                    sb.append(BytecodeViewer.nl + BytecodeViewer.nl);
                    sb = decompile(sb, decompiledClasses, containerName,cn1);
                    sb.trimPrefix(5);
                    sb.append(BytecodeViewer.nl);
                } else {
                    unableToDecompile.add(innerClassName);
                }
            }
        }

        if (!unableToDecompile.isEmpty()) {
            sb.append("//the following inner classes couldn't be decompiled: ");
            for (String s : unableToDecompile) {
                sb.append(s);
                sb.append(" ");
            }
            sb.append(BytecodeViewer.nl);
        }

        sb.append("}");
        // System.out.println("Wrote end for " + cn.name +
        // " with prefix length: " + sb.prefix.length());
        return sb;
    }

    public static String getAccessString(int access) {
        List<String> tokens = new ArrayList<String>();
        if ((access & Opcodes.ACC_PUBLIC) != 0) tokens.add("public");
        if ((access & Opcodes.ACC_PRIVATE) != 0) tokens.add("private");
        if ((access & Opcodes.ACC_PROTECTED) != 0) tokens.add("protected");
        if ((access & Opcodes.ACC_FINAL) != 0) tokens.add("final");
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) tokens.add("synthetic");
        // if ((access & Opcodes.ACC_SUPER) != 0)
        // tokens.add("super"); implied by invokespecial insn
        if ((access & Opcodes.ACC_ABSTRACT) != 0) tokens.add("abstract");
        if ((access & Opcodes.ACC_INTERFACE) != 0) tokens.add("interface");
        if ((access & Opcodes.ACC_ENUM) != 0) tokens.add("enum");
        if ((access & Opcodes.ACC_ANNOTATION) != 0) tokens.add("annotation");
        if (!tokens.contains("interface") && !tokens.contains("enum") && !tokens.contains("annotation"))
            tokens.add("class");
        if (tokens.size() == 0) return "[Error parsing]";

        // hackery delimeters
        StringBuilder sb = new StringBuilder(tokens.get(0));
        for (int i = 1; i < tokens.size(); i++) {
            sb.append(" ");
            sb.append(tokens.get(i));
        }
        return sb.toString();
    }

    @Override
    public void decompileToZip(String zipName) {
    }

    public enum Settings implements DecompilerSettings.Setting {
        DEBUG_HELPERS("debug-helpers", "Debug Helpers", true),
        APPEND_BRACKETS_TO_LABELS("append-brackets-to-labels", "Append Brackets to Labels", true);

        private String name;
        private String param;
        private boolean on;

        Settings(String param, String name) {
            this(param, name, false);
        }

        Settings(String param, String name, boolean on) {
            this.name = name;
            this.param = param;
            this.on = on;
        }

        public String getText() {
            return name;
        }

        public boolean isDefaultOn() {
            return on;
        }

        public String getParam() {
            return param;
        }
    }
}