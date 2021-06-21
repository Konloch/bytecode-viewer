package the.bytecode.club.bytecodeviewer.plugin.preinstalled;

import java.util.ArrayList;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.api.PluginConsole;

import static the.bytecode.club.bytecodeviewer.Constants.*;

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
 * The idea/core was based off of J-RET's Malicious Code Searcher I improved it,
 * and added more stuff to search for.
 *
 * @author Konloch
 * @author Adrianherrera
 * @author WaterWolf
 */

public class MaliciousCodeScanner extends Plugin {

    public boolean ORE, ONE, ORU, OIO, LWW, LHT, LHS, LIP, NSM, ROB;

    public MaliciousCodeScanner(boolean reflect, boolean runtime, boolean net,
                                boolean io, boolean www, boolean http, boolean https, boolean ip,
                                boolean nullSecMan, boolean robot) {
        ORE = reflect;
        ONE = net;
        ORU = runtime;
        OIO = io;
        LWW = www;
        LHT = http;
        LHS = https;
        LIP = ip;
        NSM = nullSecMan;
        ROB = robot;
    }

    @Override
    public void execute(ArrayList<ClassNode> classNodeList) {
        PluginConsole frame = new PluginConsole("Malicious Code Scanner");
        StringBuilder sb = new StringBuilder();
        for (ClassNode classNode : classNodeList) {
            for (Object o : classNode.fields.toArray()) {
                FieldNode f = (FieldNode) o;
                Object v = f.value;
                if (v instanceof String) {
                    String s = (String) v;
                    if ((LWW && s.contains("www."))
                            || (LHT && s.contains("http://"))
                            || (LHS && s.contains("https://"))
                            || (ORE && s.contains("java/lang/Runtime"))
                            || (ORE && s.contains("java.lang.Runtime"))
                            || (ROB && s.contains("java.awt.Robot"))
                            || (ROB && s.contains("java/awt/Robot"))
                            || (LIP && s
                            .matches("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b")))
                        sb.append("Found LDC \"").append(s).append("\" at field ").append(classNode.name).append(".")
                                .append(f.name).append("(").append(f.desc).append(")").append(nl);
                }
                if (v instanceof String[]) {
                    for (int i = 0; i < ((String[]) v).length; i++) {
                        String s = ((String[]) v)[i];
                        if ((LWW && s.contains("www."))
                                || (LHT && s.contains("http://"))
                                || (LHS && s.contains("https://"))
                                || (ORE && s.contains("java/lang/Runtime"))
                                || (ORE && s.contains("java.lang.Runtime"))
                                || (ROB && s.contains("java.awt.Robot"))
                                || (ROB && s.contains("java/awt/Robot"))
                                || (LIP && s
                                .matches("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b")))
                            sb.append("Found LDC \"").append(s).append("\" at field ").append(classNode.name)
                                    .append(".").append(f.name).append("(").append(f.desc).append(")")
                                    .append(nl);
                    }
                }
            }

            boolean prevInsn_aconst_null = false;

            for (Object o : classNode.methods.toArray()) {
                MethodNode m = (MethodNode) o;

                InsnList iList = m.instructions;
                for (AbstractInsnNode a : iList.toArray()) {
                    if (a instanceof MethodInsnNode) {
                        final MethodInsnNode min = (MethodInsnNode) a;
                        if ((ORE && min.owner.startsWith("java/lang/reflect"))
                                || (ONE && min.owner.startsWith("java/net"))
                                || (ORU && min.owner.equals("java/lang/Runtime"))
                                || (ROB && min.owner.equals("java/awt/Robot"))
                                || (OIO && min.owner.startsWith("java/io"))) {
                            sb.append("Found Method call to ").append(min.owner).append(".").append(min.name)
                                    .append("(").append(min.desc).append(") at ").append(classNode.name).append(".")
                                    .append(m.name).append("(").append(m.desc).append(")").append(nl);
                        }
                    }
                    if (a instanceof LdcInsnNode) {
                        if (((LdcInsnNode) a).cst instanceof String) {
                            final String s = (String) ((LdcInsnNode) a).cst;
                            if ((LWW && s.contains("www."))
                                    || (LHT && s.contains("http://"))
                                    || (LHS && s.contains("https://"))
                                    || (ORE && s.contains("java/lang/Runtime"))
                                    || (ORE && s.contains("java.lang.Runtime"))
                                    || (ROB && s.contains("java.awt.Robot"))
                                    || (ROB && s.contains("java/awt/Robot"))
                                    || (LIP && s
                                    .matches("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b"))) {
                                sb.append("Found LDC \"").append(s).append("\" at method ").append(classNode.name)
                                        .append(".").append(m.name).append("(").append(m.desc).append(")")
                                        .append(nl);
                            }
                        }
                    }

                    // Check if the security manager is getting set to null
                    if ((a instanceof InsnNode)
                            && (a.getOpcode() == Opcodes.ACONST_NULL)) {
                        prevInsn_aconst_null = true;
                    } else if ((a instanceof MethodInsnNode)
                            && (a.getOpcode() == Opcodes.INVOKESTATIC)) {
                        final String owner = ((MethodInsnNode) a).owner;
                        final String name = ((MethodInsnNode) a).name;
                        if ((NSM && prevInsn_aconst_null
                                && owner.equals("java/lang/System") && name
                                .equals("setSecurityManager"))) {
                            sb.append("Found Security Manager set to null at method ").append(classNode.name)
                                    .append(".").append(m.name).append("(").append(m.desc).append(")")
                                    .append(nl);
                            prevInsn_aconst_null = false;
                        }
                    } else {
                        prevInsn_aconst_null = false;
                    }
                }
            }
        }

        frame.appendText(sb.toString());
        frame.setVisible(true);
    }
}