package the.bytecode.club.bytecodeviewer.searching;

import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;

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
 * An instruction finder that finds regex patterns in a method's instruction
 * list and returns an array with the found instructions.
 *
 * @author Frédéric Hannes
 */

public class RegexInsnFinder {

    private static final String[] opcodes = new String[]{"NOP", "ACONST_NULL",
            "ICONST_M1", "ICONST_0", "ICONST_1", "ICONST_2", "ICONST_3",
            "ICONST_4", "ICONST_5", "LCONST_0", "LCONST_1", "FCONST_0",
            "FCONST_1", "FCONST_2", "DCONST_0", "DCONST_1", "BIPUSH", "SIPUSH",
            "LDC", "LDC_W", "LDC2_W", "ILOAD", "LLOAD", "FLOAD", "DLOAD",
            "ALOAD", "ILOAD_0", "ILOAD_1", "ILOAD_2", "ILOAD_3", "LLOAD_0",
            "LLOAD_1", "LLOAD_2", "LLOAD_3", "FLOAD_0", "FLOAD_1", "FLOAD_2",
            "FLOAD_3", "DLOAD_0", "DLOAD_1", "DLOAD_2", "DLOAD_3", "ALOAD_0",
            "ALOAD_1", "ALOAD_2", "ALOAD_3", "IALOAD", "LALOAD", "FALOAD",
            "DALOAD", "AALOAD", "BALOAD", "CALOAD", "SALOAD", "ISTORE",
            "LSTORE", "FSTORE", "DSTORE", "ASTORE", "ISTORE_0", "ISTORE_1",
            "ISTORE_2", "ISTORE_3", "LSTORE_0", "LSTORE_1", "LSTORE_2",
            "LSTORE_3", "FSTORE_0", "FSTORE_1", "FSTORE_2", "FSTORE_3",
            "DSTORE_0", "DSTORE_1", "DSTORE_2", "DSTORE_3", "ASTORE_0",
            "ASTORE_1", "ASTORE_2", "ASTORE_3", "IASTORE", "LASTORE",
            "FASTORE", "DASTORE", "AASTORE", "BASTORE", "CASTORE", "SASTORE",
            "POP", "POP2", "DUP", "DUP_X1", "DUP_X2", "DUP2", "DUP2_X1",
            "DUP2_X2", "SWAP", "IADD", "LADD", "FADD", "DADD", "ISUB", "LSUB",
            "FSUB", "DSUB", "IMUL", "LMUL", "FMUL", "DMUL", "IDIV", "LDIV",
            "FDIV", "DDIV", "IREM", "LREM", "FREM", "DREM", "INEG", "LNEG",
            "FNEG", "DNEG", "ISHL", "LSHL", "ISHR", "LSHR", "IUSHR", "LUSHR",
            "IAND", "LAND", "IOR", "LOR", "IXOR", "LXOR", "IINC", "I2L", "I2F",
            "I2D", "L2I", "L2F", "L2D", "F2I", "F2L", "F2D", "D2I", "D2L",
            "D2F", "I2B", "I2C", "I2S", "LCMP", "FCMPL", "FCMPG", "DCMPL",
            "DCMPG", "IFEQ", "IFNE", "IFLT", "IFGE", "IFGT", "IFLE",
            "IF_ICMPEQ", "IF_ICMPNE", "IF_ICMPLT", "IF_ICMPGE", "IF_ICMPGT",
            "IF_ICMPLE", "IF_ACMPEQ", "IF_ACMPNE", "GOTO", "JSR", "RET",
            "TABLESWITCH", "LOOKUPSWITCH", "IRETURN", "LRETURN", "FRETURN",
            "DRETURN", "ARETURN", "RETURN", "GETSTATIC", "PUTSTATIC",
            "GETFIELD", "PUTFIELD", "INVOKEVIRTUAL", "INVOKESPECIAL",
            "INVOKESTATIC", "INVOKEINTERFACE", "INVOKEDYNAMIC", "NEW",
            "NEWARRAY", "ANEWARRAY", "ARRAYLENGTH", "ATHROW", "CHECKCAST",
            "INSTANCEOF", "MONITORENTER", "MONITOREXIT", "WIDE",
            "MULTIANEWARRAY", "IFNULL", "IFNONNULL", "GOTO_W", "JSR_W"};

    private static final String[] opcodesVar = new String[]{"ILOAD", "LLOAD",
            "FLOAD", "DLOAD", "ALOAD", "ISTORE", "LSTORE", "FSTORE", "DSTORE",
            "ASTORE", "RET"};
    private static final String opcodeVars = buildRegexItems(opcodesVar);

    private static final String[] opcodesInt = new String[]{"BIPUSH", "SIPUSH",
            "NEWARRAY"};
    private static final String opcodesInts = buildRegexItems(opcodesInt);

    private static final String[] opcodesField = new String[]{"GETSTATIC",
            "PUTSTATIC", "GETFIELD", "PUTFIELD"};
    private static final String opcodesFields = buildRegexItems(opcodesField);

    private static final String[] opcodesMethod = new String[]{"INVOKEVIRTUAL",
            "INVOKESPECIAL", "INVOKESTATIC", "INVOKEINTERFACE", "INVOKEDYNAMIC"};
    private static final String opcodesMethods = buildRegexItems(opcodesMethod);

    private static final String[] opcodesType = new String[]{"NEW", "ANEWARRAY",
            "ARRAYLENGTH", "CHECKCAST", "INSTANCEOF"};
    private static final String opcodesTypes = buildRegexItems(opcodesType);

    private static final String[] opcodesIf = new String[]{"IFEQ", "IFNE", "IFLT",
            "IFGE", "IFGT", "IFLE", "IF_ICMPEQ", "IF_ICMPNE", "IF_ICMPLT",
            "IF_ICMPGE", "IF_ICMPGT", "IF_ICMPLE", "IF_ACMPEQ", "IF_ACMPNE"};
    private static final String opcodesIfs = buildRegexItems(opcodesIf, false, false);

    private static final String[] opcodesAny = new String[]{"NOP", "ACONST_NULL",
            "ICONST_M1", "ICONST_0", "ICONST_1", "ICONST_2", "ICONST_3",
            "ICONST_4", "ICONST_5", "LCONST_0", "LCONST_1", "FCONST_0",
            "FCONST_1", "FCONST_2", "DCONST_0", "DCONST_1", "BIPUSH", "SIPUSH",
            "LDC", "LDC_W", "LDC2_W", "ILOAD", "LLOAD", "FLOAD", "DLOAD",
            "ALOAD", "IALOAD", "LALOAD", "FALOAD", "DALOAD", "AALOAD",
            "BALOAD", "CALOAD", "SALOAD", "ISTORE", "LSTORE", "FSTORE",
            "DSTORE", "ASTORE", "IASTORE", "LASTORE", "FASTORE", "DASTORE",
            "AASTORE", "BASTORE", "CASTORE", "SASTORE", "POP", "POP2", "DUP",
            "DUP_X1", "DUP_X2", "DUP2", "DUP2_X1", "DUP2_X2", "SWAP", "IADD",
            "LADD", "FADD", "DADD", "ISUB", "LSUB", "FSUB", "DSUB", "IMUL",
            "LMUL", "FMUL", "DMUL", "IDIV", "LDIV", "FDIV", "DDIV", "IREM",
            "LREM", "FREM", "DREM", "INEG", "LNEG", "FNEG", "DNEG", "ISHL",
            "LSHL", "ISHR", "LSHR", "IUSHR", "LUSHR", "IAND", "LAND", "IOR",
            "LOR", "IXOR", "LXOR", "IINC", "I2L", "I2F", "I2D", "L2I", "L2F",
            "L2D", "F2I", "F2L", "F2D", "D2I", "D2L", "D2F", "I2B", "I2C",
            "I2S", "LCMP", "FCMPL", "FCMPG", "DCMPL", "DCMPG", "IFEQ", "IFNE",
            "IFLT", "IFGE", "IFGT", "IFLE", "IF_ICMPEQ", "IF_ICMPNE",
            "IF_ICMPLT", "IF_ICMPGE", "IF_ICMPGT", "IF_ICMPLE", "IF_ACMPEQ",
            "IF_ACMPNE", "GOTO", "JSR", "RET", "TABLESWITCH", "LOOKUPSWITCH",
            "IRETURN", "LRETURN", "FRETURN", "DRETURN", "ARETURN", "RETURN",
            "GETSTATIC", "PUTSTATIC", "GETFIELD", "PUTFIELD", "INVOKEVIRTUAL",
            "INVOKESPECIAL", "INVOKESTATIC", "INVOKEINTERFACE",
            "INVOKEDYNAMIC", "NEW", "NEWARRAY", "ANEWARRAY", "ARRAYLENGTH",
            "ATHROW", "CHECKCAST", "INSTANCEOF", "MONITORENTER", "MONITOREXIT",
            "MULTIANEWARRAY", "IFNULL", "IFNONNULL"};
    private static final String opcodesAnys = buildRegexItems(opcodesAny, false,
            false);

    private static String buildRegexItems(final String[] items,
                                          final boolean capture, final boolean stdRepl) {
        if (items.length == 0)
            return "()";
        StringBuilder result = new StringBuilder((stdRepl ? "\\b" : "") + "(" + (capture ? "" : "?:")
                + items[0]);
        for (int i = 1; i < items.length; i++) {
            result.append("|").append(items[i]);
        }
        result.append(")");
        return result.toString();
    }

    private static String buildRegexItems(final String[] items) {
        return buildRegexItems(items, true, true);
    }

    public static String processRegex(final String regex) {
        String result = regex.trim();
        result = result.replaceAll("\\bANYINSN *", opcodesAnys);
        result = result.replaceAll(opcodesInts
                + "\\\\\\{\\s*(\\d+)\\s*\\\\} *", "$1\\\\{$2\\\\} ");
        result = result.replaceAll(opcodesInts + " *", "$1\\\\{\\\\d+\\\\} ");
        result = result.replaceAll(
                "\\bLDC\\\\\\{(.*?)\\\\}(?<!\\\\\\\\}) *",
                "LDC\\\\{$1\\\\}(?<!\\\\\\\\\\\\}) ");
        result = result.replaceAll("\\bLDC *",
                "LDC\\\\{.*?\\\\}(?<!\\\\\\\\\\\\}) ");
        result = result.replaceAll(opcodeVars + "(_\\d+) *", "$1$2 ");
        result = result.replaceAll(opcodeVars + "(?!_) *", "$1_\\\\d+ ");
        result = result.replaceAll(
                "\\bIINC\\\\\\{\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\\\} *",
                "IINC\\\\{$1,$2\\\\} ");
        result = result.replaceAll("\\bIINC\\\\\\{\\s*(\\d+)\\s*\\\\} *",
                "IINC\\\\{\\d+,$1\\\\} ");
        result = result.replaceAll("\\bIINC *", "IINC\\\\{\\d+,\\d+\\\\} ");
        result = result.replaceAll(opcodesFields
                        + "\\\\\\{\\s*(.*?)\\s*,\\s*(.*?)\\s*,\\s*(.*?)\\s*\\\\} *",
                "$1\\\\{$2,$3,$4\\\\} ");
        result = result.replaceAll(opcodesFields
                + "\\\\\\{((?:.(?!,))*)\\\\} *", "$1\\\\{$2,.*?,.*?\\\\} ");
        result = result.replaceAll(opcodesFields + " *", "$1\\\\{.*?\\\\} ");
        result = result.replaceAll(opcodesMethods
                        + "\\\\\\{\\s*(.*?)\\s*,\\s*(.*?)\\s*,\\s*(.*?)\\s*\\\\} *",
                "$1\\\\{$2,$3,$4\\\\} ");
        result = result.replaceAll(opcodesMethods
                + "\\\\\\{((?:.(?!,))*)\\\\} *", "$1\\\\{$2,.*?,.*?\\\\} ");
        result = result.replaceAll(opcodesMethods + " *",
                "$1\\\\{.*?,.*?,.*?\\\\} ");
        result = result.replaceAll(opcodesTypes
                + "\\\\\\{\\s*(.*?)\\s*\\\\} +", "$1\\\\{$2\\\\} ");
        result = result.replaceAll(opcodesTypes + " +", "$1\\\\{\\\\.*?\\\\} ");
        result = result
                .replaceAll(
                        "\\bMULTIANEWARRAY\\\\\\{\\s*(\\d+)\\s*,\\s*(.*?)\\s*\\\\} *",
                        "MULTIANEWARRAY\\\\{$1,$2\\\\} ");
        result = result.replaceAll(
                "\\bMULTIANEWARRAY\\\\\\{\\s*(.*?)\\s*\\\\} *",
                "MULTIANEWARRAY\\\\{\\d+,$1\\\\} ");
        result = result.replaceAll("\\bMULTIANEWARRAY *",
                "MULTIANEWARRAY\\\\{\\\\\\d+,.*?\\\\} ");
        result = result.replaceAll("\\bIFINSN *", opcodesIfs + " ");
        return result;
    }

    private MethodNode mn;
    private AbstractInsnNode[] origInstructions;
    private int[] offsets;
    private String insnString;

    public RegexInsnFinder(final ClassNode clazz, final MethodNode method) {
        setMethod(clazz, method);
    }

    private AbstractInsnNode[] cleanInsn(final InsnList insnList) {
        final List<AbstractInsnNode> il = new ArrayList<>();

        for (AbstractInsnNode node : insnList) {
            if (node.getOpcode() >= 0) {
                il.add(node);
            }
        }
        return il.toArray(new AbstractInsnNode[0]);
    }

    /**
     * Refreshes the internal instruction list when you have made changes to the
     * method.
     */
    public void refresh() {
        origInstructions = cleanInsn(mn.instructions);
        final List<AbstractInsnNode> il = new ArrayList<>();
        for (final AbstractInsnNode ain : mn.instructions.toArray())
            if (ain.getOpcode() >= 0) {
                il.add(ain);
            }
        AbstractInsnNode[] instructions = il.toArray(new AbstractInsnNode[0]);
        offsets = new int[instructions.length];
        StringBuilder insnStringBuilder = new StringBuilder();
        for (int i = 0; i < instructions.length; i++) {
            offsets[i] = -1;
            final AbstractInsnNode ain = instructions[i];
            if (ain.getOpcode() >= 0) {
                if (ain.getOpcode() >= opcodes.length) {
                    try {
                        throw new UnexpectedException(
                                "Unknown opcode encountered: "
                                        + ain.getOpcode());
                    } catch (final UnexpectedException e) {
                        BytecodeViewer.handleException(e);
                    }
                }
                offsets[i] = insnStringBuilder.length();
                insnStringBuilder.append(opcodes[ain.getOpcode()]);
                insnStringBuilder = new StringBuilder(getInsString(ain));
                insnStringBuilder.append(" ");
            }
        }
        insnString = insnStringBuilder.toString();
    }

    // Do a pattern check against each instruction directly,
    // without building a string of the whole method.
    public static boolean staticScan(ClassNode node, MethodNode mn, Pattern pattern) {
        final List<AbstractInsnNode> il = new ArrayList<>();
        for (final AbstractInsnNode ain : mn.instructions.toArray())
            if (ain.getOpcode() >= 0) {
                il.add(ain);
            }
        return il.stream().anyMatch(ain -> {
            if (ain.getOpcode() >= 0) {
                if (ain.getOpcode() >= opcodes.length) {
                    try {
                        throw new UnexpectedException(
                                "Unknown opcode encountered: "
                                        + ain.getOpcode());
                    } catch (final UnexpectedException e) {
                        BytecodeViewer.handleException(e);
                    }
                }
                String insnString = getInsString(ain);
                return pattern.matcher(insnString).find();
            }
            return false;
        });
    }

    private static String getInsString(AbstractInsnNode ain) {
        String insnString = "";
        switch (ain.getType()) {
        case AbstractInsnNode.INT_INSN:
            final IntInsnNode iin = (IntInsnNode) ain;
            insnString += "{" + iin.operand + "}";
            break;
        case AbstractInsnNode.LDC_INSN:
            final LdcInsnNode lin = (LdcInsnNode) ain;
            insnString += "{" + lin.cst.toString().replace("}", "\\}")
                    + "}";
            break;
        case AbstractInsnNode.VAR_INSN:
            final VarInsnNode vin = (VarInsnNode) ain;
            insnString += "_" + vin.var;
            break;
        case AbstractInsnNode.IINC_INSN:
            final IincInsnNode iiin = (IincInsnNode) ain;
            insnString += "{" + iiin.var + "," + iiin.incr + "}";
            break;
        case AbstractInsnNode.FIELD_INSN:
            final FieldInsnNode fin = (FieldInsnNode) ain;
            insnString += "{" + fin.desc + "," + fin.owner + ","
                    + fin.name + "}";
            break;
        case AbstractInsnNode.METHOD_INSN:
            final MethodInsnNode min = (MethodInsnNode) ain;
            insnString += "{" + min.desc + "," + min.owner + ","
                    + min.name + "}";
            break;
        case AbstractInsnNode.TYPE_INSN:
            final TypeInsnNode tin = (TypeInsnNode) ain;
            insnString += "{" + tin.desc + "}";
            break;
        case AbstractInsnNode.MULTIANEWARRAY_INSN:
            final MultiANewArrayInsnNode manain = (MultiANewArrayInsnNode) ain;
            insnString += "{" + manain.dims + "," + manain.desc + "}";
            break;
        }
        return insnString;
    }

    public void setMethod(final ClassNode ci, final MethodNode mi) {
        this.mn = mi;
        refresh();
    }

    private AbstractInsnNode[] makeResult(final int start, final int end) {
        int startIndex = 0;
        int endIndex = -1;
        for (int i = 0; i < offsets.length - 1; i++) {
            final int offset = offsets[i];
            if (offset == start) {
                startIndex = i;
            }
            if ((offset < end) && (offsets[i + 1] >= end)) {
                endIndex = i;
                break;
            }
        }
        if (endIndex == -1) {
            endIndex = offsets.length - 1;
        }
        final int length = endIndex - startIndex + 1;
        final AbstractInsnNode[] result = new AbstractInsnNode[length];
        System.arraycopy(origInstructions, startIndex, result, 0, length);
        return result;
    }

    /**
     * Searches for a regex in the instruction list and returns the first match.
     *
     * @param regex the regular expression
     * @return the matching instructions
     */
    public AbstractInsnNode[] find(final String regex) {
        try {
            final Matcher regexMatcher = Pattern.compile(processRegex(regex),
                    Pattern.MULTILINE).matcher(insnString);
            if (regexMatcher.find())
                return makeResult(regexMatcher.start(), regexMatcher.end());
        } catch (final PatternSyntaxException ex) {
            //ignore, they fucked up regex
        }
        return new AbstractInsnNode[0];
    }

    /**
     * Searches a regex in an instruction list and returns all matches.
     *
     * @param regex the regular expression
     * @return a list with all sets of matching instructions
     */
    public List<AbstractInsnNode[]> findAll(final String regex) {
        final List<AbstractInsnNode[]> results = new ArrayList<>();
        try {
            final Matcher regexMatcher = Pattern.compile(processRegex(regex),
                    Pattern.MULTILINE).matcher(insnString);
            while (regexMatcher.find()) {
                results.add(makeResult(regexMatcher.start(), regexMatcher.end()));
            }
        } catch (final PatternSyntaxException ex) {
            BytecodeViewer.handleException(ex);
        }
        return results;
    }

    /**
     * Searches for a regex in the instruction list and returns all groups for
     * the first match.
     *
     * @param regex the regular expression
     * @return the groups with matching instructions
     */
    public AbstractInsnNode[][] findGroups(final String regex) {
        try {
            final Matcher regexMatcher = Pattern.compile(processRegex(regex),
                    Pattern.MULTILINE).matcher(insnString);
            if (regexMatcher.find()) {
                final AbstractInsnNode[][] result = new AbstractInsnNode[regexMatcher
                        .groupCount() + 1][0];
                for (int i = 0; i <= regexMatcher.groupCount(); i++) {
                    result[i] = makeResult(regexMatcher.start(i),
                            regexMatcher.end(i));
                }
                return result;
            }
        } catch (final PatternSyntaxException ex) {
            BytecodeViewer.handleException(ex);
        }
        return new AbstractInsnNode[0][0];
    }

    /**
     * Searches for a regex in the instruction list and returns all groups for
     * all matches.
     *
     * @param regex the regular expression
     * @return a list with all sets of groups with matching instructions
     */
    public List<AbstractInsnNode[][]> findAllGroups(final String regex) {
        final List<AbstractInsnNode[][]> results = new ArrayList<>();
        try {
            final Matcher regexMatcher = Pattern.compile(processRegex(regex),
                    Pattern.MULTILINE).matcher(insnString);
            if (regexMatcher.find()) {
                final AbstractInsnNode[][] result = new AbstractInsnNode[regexMatcher
                        .groupCount() + 1][0];
                for (int i = 0; i <= regexMatcher.groupCount(); i++) {
                    result[i] = makeResult(regexMatcher.start(i),
                            regexMatcher.end(i));
                }
                results.add(result);
            }
        } catch (final PatternSyntaxException ex) {
            BytecodeViewer.handleException(ex);
        }
        return results;
    }
}
