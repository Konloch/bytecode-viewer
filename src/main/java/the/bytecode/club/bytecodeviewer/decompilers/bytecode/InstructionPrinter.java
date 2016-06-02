package the.bytecode.club.bytecodeviewer.decompilers.bytecode;

import eu.bibl.banalysis.asm.desc.OpcodeInfo;
import org.apache.commons.lang3.StringEscapeUtils;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.*;

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
public class InstructionPrinter {

    /** The MethodNode to print **/
    protected MethodNode mNode;
    private TypeAndName[] args;

    protected int[] pattern;
    protected boolean match;
    protected InstructionSearcher searcher;

    protected List<AbstractInsnNode> matchedInsns;
    protected Map<LabelNode, Integer> labels;

    public InstructionPrinter(MethodNode m, TypeAndName[] args) {
        this.args = args;
        mNode = m;
        labels = new HashMap<LabelNode, Integer>();
        // matchedInsns = new ArrayList<AbstractInsnNode>(); // ingnored because
        // match = false
        match = false;
    }

    public InstructionPrinter(MethodNode m, InstructionPattern pattern, TypeAndName[] args) {
        this.args = args;
        mNode = m;
        labels = new HashMap<LabelNode, Integer>();
        searcher = new InstructionSearcher(m.instructions, pattern);
        match = searcher.search();
        if (match) {
            for (AbstractInsnNode[] ains : searcher.getMatches()) {
                for (AbstractInsnNode ain : ains) {
                    matchedInsns.add(ain);
                }
            }
        }
    }

    /**
     * Creates the print
     *
     * @return The print as an ArrayList
     */
    public ArrayList<String> createPrint() {
        ArrayList<String> info = new ArrayList<String>();
        ListIterator<?> it = mNode.instructions.iterator();
        boolean firstLabel = false;
        while (it.hasNext()) {
            AbstractInsnNode ain = (AbstractInsnNode) it.next();
            String line = "";
            if (ain instanceof VarInsnNode) {
                line = printVarInsnNode((VarInsnNode) ain, it);
            } else if (ain instanceof IntInsnNode) {
                line = printIntInsnNode((IntInsnNode) ain, it);
            } else if (ain instanceof FieldInsnNode) {
                line = printFieldInsnNode((FieldInsnNode) ain, it);
            } else if (ain instanceof MethodInsnNode) {
                line = printMethodInsnNode((MethodInsnNode) ain, it);
            } else if (ain instanceof LdcInsnNode) {
                line = printLdcInsnNode((LdcInsnNode) ain, it);
            } else if (ain instanceof InsnNode) {
                line = printInsnNode((InsnNode) ain, it);
            } else if (ain instanceof JumpInsnNode) {
                line = printJumpInsnNode((JumpInsnNode) ain, it);
            } else if (ain instanceof LineNumberNode) {
                line = printLineNumberNode((LineNumberNode) ain, it);
            } else if (ain instanceof LabelNode) {
                if (firstLabel && Decompiler.BYTECODE.getSettings().isSelected(ClassNodeDecompiler.Settings.APPEND_BRACKETS_TO_LABELS))
                    info.add("}");

                line = printLabelnode((LabelNode) ain);

                if (Decompiler.BYTECODE.getSettings().isSelected(ClassNodeDecompiler.Settings.APPEND_BRACKETS_TO_LABELS)) {
                    if (!firstLabel) firstLabel = true;
                    line += " {";
                }
            } else if (ain instanceof TypeInsnNode) {
                line = printTypeInsnNode((TypeInsnNode) ain);
            } else if (ain instanceof FrameNode) {
                line = "";
            } else if (ain instanceof IincInsnNode) {
                line = printIincInsnNode((IincInsnNode) ain);
            } else if (ain instanceof TableSwitchInsnNode) {
                line = printTableSwitchInsnNode((TableSwitchInsnNode) ain);
            } else if (ain instanceof LookupSwitchInsnNode) {
                line = printLookupSwitchInsnNode((LookupSwitchInsnNode) ain);
            } else if (ain instanceof InvokeDynamicInsnNode) {
                line = printInvokeDynamicInsNode((InvokeDynamicInsnNode) ain);
            } else {
                line += "UNADDED OPCODE: " + nameOpcode(ain.opcode()) + " " + ain.toString();
            }
            if (!line.equals("")) {
                if (match) if (matchedInsns.contains(ain)) line = "   -> " + line;

                info.add(line);
            }
        }
        if (firstLabel && Decompiler.BYTECODE.getSettings().isSelected(ClassNodeDecompiler.Settings.APPEND_BRACKETS_TO_LABELS)) info.add("}");
        return info;
    }

    protected String printVarInsnNode(VarInsnNode vin, ListIterator<?> it) {
        StringBuilder sb = new StringBuilder();
        sb.append(nameOpcode(vin.opcode()));
        sb.append(vin.var);
        if (Decompiler.BYTECODE.getSettings().isSelected(ClassNodeDecompiler.Settings.DEBUG_HELPERS)) {
            if (vin.var == 0 && !Modifier.isStatic(mNode.access)) {
                sb.append(" // reference to self");
            } else {
                final int refIndex = vin.var - (Modifier.isStatic(mNode.access) ? 0 : 1);
                if (refIndex >= 0 && refIndex < args.length - 1) {
                    sb.append(" // reference to " + args[refIndex].name);
                }
            }
        }

        return sb.toString();
    }

    protected String printIntInsnNode(IntInsnNode iin, ListIterator<?> it) {
        return nameOpcode(iin.opcode()) + " " + iin.operand;
    }

    protected String printFieldInsnNode(FieldInsnNode fin, ListIterator<?> it) {
        String desc = Type.getType(fin.desc).getClassName();
        if (desc == null || desc.equals("null")) desc = fin.desc;
        return nameOpcode(fin.opcode()) + " " + fin.owner + "." + fin.name + ":" + desc;
    }

    protected String printMethodInsnNode(MethodInsnNode min, ListIterator<?> it) {
        StringBuilder sb = new StringBuilder();
        sb.append(nameOpcode(min.opcode()) + " " + min.owner + " " + min.name + "(");

        String desc = min.desc;
        try {
            if (Type.getType(min.desc) != null) desc = Type.getType(min.desc).getClassName();

            if (desc == null || desc.equals("null")) desc = min.desc;
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {

        }

        sb.append(desc);

        sb.append(");");

        return sb.toString();
    }

    protected String printLdcInsnNode(LdcInsnNode ldc, ListIterator<?> it) {
        if (ldc.cst instanceof String)
            return nameOpcode(ldc.opcode()) + " \"" + StringEscapeUtils.escapeJava(ldc.cst.toString()) + "\" (" + ldc.cst.getClass().getCanonicalName() + ")";

        return nameOpcode(ldc.opcode()) + " " + StringEscapeUtils.escapeJava(ldc.cst.toString()) + " (" + ldc.cst.getClass().getCanonicalName() + ")";
    }

    protected String printInsnNode(InsnNode in, ListIterator<?> it) {
        return nameOpcode(in.opcode());
    }

    protected String printJumpInsnNode(JumpInsnNode jin, ListIterator<?> it) {
        String line = nameOpcode(jin.opcode()) + " L" + resolveLabel(jin.label);
        return line;
    }

    protected String printLineNumberNode(LineNumberNode lin, ListIterator<?> it) {
        return "";
    }

    protected String printLabelnode(LabelNode label) {
        return "L" + resolveLabel(label);
    }

    protected String printTypeInsnNode(TypeInsnNode tin) {
        try {
            String desc = tin.desc;
            try {
                if (Type.getType(tin.desc) != null) desc = Type.getType(tin.desc).getClassName();

                if (desc == null || desc.equals("null")) desc = tin.desc;
            } catch (java.lang.ArrayIndexOutOfBoundsException e) {

            }
            return nameOpcode(tin.opcode()) + " " + desc;
        } catch (Exception e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }
        return "//error";
    }

    protected String printIincInsnNode(IincInsnNode iin) {
        return nameOpcode(iin.opcode()) + " " + iin.var + " " + iin.incr;
    }

    protected String printTableSwitchInsnNode(TableSwitchInsnNode tin) {
        String line = nameOpcode(tin.opcode()) + " \n";
        List<?> labels = tin.labels;
        int count = 0;
        for (int i = tin.min; i < tin.max + 1; i++) {
            line += "                val: " + i + " -> " + "L" + resolveLabel((LabelNode) labels.get(count++)) + "\n";
        }
        line += "                default" + " -> L" + resolveLabel(tin.dflt) + "";
        return line;
    }

    protected String printLookupSwitchInsnNode(LookupSwitchInsnNode lin) {
        String line = nameOpcode(lin.opcode()) + ": \n";
        List<?> keys = lin.keys;
        List<?> labels = lin.labels;

        for (int i = 0; i < keys.size(); i++) {
            int key = (Integer) keys.get(i);
            LabelNode label = (LabelNode) labels.get(i);
            line += "                val: " + key + " -> " + "L" + resolveLabel(label) + "\n";
        }
        line += "                default" + " -> L" + resolveLabel(lin.dflt) + "";
        return line;
    }

    protected String printInvokeDynamicInsNode(InvokeDynamicInsnNode idin) {
        StringBuilder sb = new StringBuilder();
        sb.append(nameOpcode(idin.opcode()) + " " + idin.bsm.getName() + "(");

        String desc = idin.desc;
        String partedDesc = idin.desc.substring(2);
        try {
            if (Type.getType(partedDesc) != null) desc = Type.getType(partedDesc).getClassName();

            if (desc == null || desc.equals("null")) desc = idin.desc;
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {

        }

        sb.append(desc);

        sb.append(");");

        return sb.toString();
    }

    protected String nameOpcode(int opcode) {
        return "    " + OpcodeInfo.OPCODES.get(opcode).toLowerCase();
    }

    protected int resolveLabel(LabelNode label) {
        if (labels.containsKey(label)) {
            return labels.get(label);
        } else {
            int newLabelIndex = labels.size() + 1;
            labels.put(label, newLabelIndex);
            return newLabelIndex;
        }
    }

    public static void saveTo(File file, InstructionPrinter printer) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String s : printer.createPrint()) {
                bw.write(s);
                bw.newLine();
            }
        } catch (IOException e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }
    }
}