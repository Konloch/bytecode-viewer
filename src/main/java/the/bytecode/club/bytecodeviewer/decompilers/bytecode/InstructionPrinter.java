package the.bytecode.club.bytecodeviewer.decompilers.bytecode;

import eu.bibl.banalysis.asm.desc.OpcodeInfo;
import org.apache.commons.text.StringEscapeUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

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
 * @author GraxCode
 */
public class InstructionPrinter implements Opcodes {

  /**
   * The MethodNode to print
   **/
  private final MethodNode mNode;
  private final TypeAndName[] args;

  protected int[] pattern;
  protected boolean match;

  protected List<AbstractInsnNode> matchedInsns;
  protected Map<LabelNode, Integer> labels;
  private boolean firstLabel = false;
  private final List<String> info = new ArrayList<>();

  public InstructionPrinter(MethodNode m, TypeAndName[] args) {
    this.args = args;
    mNode = m;
    labels = new HashMap<>();
    precalculateLabelIndexes(m);
    // matchedInsns = new ArrayList<AbstractInsnNode>(); // ingnored because
    // match = false
    match = false;
  }

  public InstructionPrinter(MethodNode m, InstructionPattern pattern, TypeAndName[] args) {
    this(m, args);
    InstructionSearcher searcher = new InstructionSearcher(m.instructions, pattern);
    match = searcher.search();
    if (match) {
      for (AbstractInsnNode[] ains : searcher.getMatches()) {
        Collections.addAll(matchedInsns, ains);
      }
    }
  }

  private void precalculateLabelIndexes(MethodNode m) {
    int lIdx = 0;
    for (AbstractInsnNode ain : m.instructions) {
      if (ain.getType() == AbstractInsnNode.LABEL) {
        labels.put((LabelNode) ain, lIdx++);
      }
    }
  }

  /**
   * Creates the print
   *
   * @return The print as an ArrayList
   */
  public List<String> createPrint() {
    firstLabel = false;
    info.clear();
    for (AbstractInsnNode ain : mNode.instructions) {
      String line = printInstruction(ain);
      if (!line.isEmpty()) {
        if (match) if (matchedInsns.contains(ain)) line = "   -> " + line;

        info.add(line);
      }
    }
    if (firstLabel && BytecodeViewer.viewer.appendBracketsToLabels.isSelected()) info.add("}");
    return info;
  }

  public String printInstruction(AbstractInsnNode ain) {
    String line = "";
    if (ain instanceof VarInsnNode) {
      line = printVarInsnNode((VarInsnNode) ain);
    } else if (ain instanceof IntInsnNode) {
      line = printIntInsnNode((IntInsnNode) ain);
    } else if (ain instanceof FieldInsnNode) {
      line = printFieldInsnNode((FieldInsnNode) ain);
    } else if (ain instanceof MethodInsnNode) {
      line = printMethodInsnNode((MethodInsnNode) ain);
    } else if (ain instanceof LdcInsnNode) {
      line = printLdcInsnNode((LdcInsnNode) ain);
    } else if (ain instanceof InsnNode) {
      line = printInsnNode((InsnNode) ain);
    } else if (ain instanceof JumpInsnNode) {
      line = printJumpInsnNode((JumpInsnNode) ain);
    } else if (ain instanceof LineNumberNode) {
      line = printLineNumberNode((LineNumberNode) ain);
    } else if (ain instanceof LabelNode) {
      if (firstLabel && BytecodeViewer.viewer.appendBracketsToLabels.isSelected()) info.add("}");

      LabelNode label = (LabelNode) ain;
      if (mNode.tryCatchBlocks != null) {
        List<TryCatchBlockNode> tcbs = mNode.tryCatchBlocks;
        String starting = tcbs.stream().filter(tcb -> tcb.start == label).map(tcb -> "start TCB" + tcbs.indexOf(tcb)).collect(Collectors.joining(", "));
        String ending = tcbs.stream().filter(tcb -> tcb.end == label).map(tcb -> "end TCB" + tcbs.indexOf(tcb)).collect(Collectors.joining(", "));
        String handlers = tcbs.stream().filter(tcb -> tcb.handler == label).map(tcb -> "handle TCB" + tcbs.indexOf(tcb)).collect(Collectors.joining(", "));
        if (!ending.isEmpty()) info.add("// " + ending);
        if (!starting.isEmpty()) info.add("// " + starting);
        if (!handlers.isEmpty()) info.add("// " + handlers);
      }
      line = printLabelNode((LabelNode) ain);

      if (BytecodeViewer.viewer.appendBracketsToLabels.isSelected()) {
        if (!firstLabel) firstLabel = true;
        line += " {";
      }
    } else if (ain instanceof TypeInsnNode) {
      line = printTypeInsnNode((TypeInsnNode) ain);
    } else if (ain instanceof FrameNode) {
      line = printFrameNode((FrameNode) ain);
    } else if (ain instanceof IincInsnNode) {
      line = printIincInsnNode((IincInsnNode) ain);
    } else if (ain instanceof TableSwitchInsnNode) {
      line = printTableSwitchInsnNode((TableSwitchInsnNode) ain);
    } else if (ain instanceof LookupSwitchInsnNode) {
      line = printLookupSwitchInsnNode((LookupSwitchInsnNode) ain);
    } else if (ain instanceof InvokeDynamicInsnNode) {
      line = printInvokeDynamicInsNode((InvokeDynamicInsnNode) ain);
    } else if (ain instanceof MultiANewArrayInsnNode) {
      line = printMultiANewArrayInsNode((MultiANewArrayInsnNode) ain);
    } else {
      line += "UNADDED OPCODE: " + nameOpcode(ain.getOpcode()) + " " + ain;
    }

    return line;
  }

  protected String printVarInsnNode(VarInsnNode vin) {
    StringBuilder sb = new StringBuilder();
    sb.append(nameOpcode(vin.getOpcode()));
    sb.append(" ");
    sb.append(vin.var);
    if (BytecodeViewer.viewer.debugHelpers.isSelected()) {
      if (vin.var == 0 && !Modifier.isStatic(mNode.access)) {
        sb.append(" // reference to self");
      } else {
        final int refIndex = vin.var - (Modifier.isStatic(mNode.access) ? 0 : 1);
        if (refIndex >= 0 && refIndex < args.length - 1) {
          sb.append(" // reference to ").append(args[refIndex].name);
        }
      }
    }

    return sb.toString();
  }

  protected String printIntInsnNode(IntInsnNode iin) {
    return nameOpcode(iin.getOpcode()) + " " + iin.operand;
  }

  protected String printFieldInsnNode(FieldInsnNode fin) {
    String desc = Type.getType(fin.desc).getClassName();
    if (desc.equals("null")) desc = fin.desc;
    return nameOpcode(fin.getOpcode()) + " " + fin.owner + "." + fin.name + ":" + desc;
  }

  protected String printMethodInsnNode(MethodInsnNode min) {
    StringBuilder sb = new StringBuilder();
    sb.append(nameOpcode(min.getOpcode())).append(" ").append(min.owner).append(".").append(min.name);

    String desc = min.desc;
    try {
      if (Type.getType(min.desc) != null) desc = Type.getType(min.desc).getClassName();
    } catch (java.lang.AssertionError e) {
      //e.printStackTrace();
    } catch (java.lang.Exception e) {
      e.printStackTrace();
    }

    if (desc == null || desc.equals("null")) desc = min.desc;

    sb.append(desc);

    return sb.toString();
  }

  protected String printLdcInsnNode(LdcInsnNode ldc) {
    if (ldc.cst instanceof String)
      return nameOpcode(ldc.getOpcode()) + " \"" + StringEscapeUtils.escapeJava(ldc.cst.toString()) + "\" (" + ldc.cst.getClass().getCanonicalName() + ")";

    return nameOpcode(ldc.getOpcode()) + " " + StringEscapeUtils.escapeJava(ldc.cst.toString()) + " (" + ldc.cst.getClass().getCanonicalName() + ")";
  }

  protected String printInsnNode(InsnNode in) {
    return nameOpcode(in.getOpcode());
  }

  protected String printJumpInsnNode(JumpInsnNode jin) {
    return nameOpcode(jin.getOpcode()) + " L" + resolveLabel(jin.label);
  }

  protected String printLineNumberNode(LineNumberNode lnn) {
    if(BytecodeViewer.viewer.printLineNumbers.isSelected())
      return "// line " + lnn.line;
    
    return "";
  }

  protected String printLabelNode(LabelNode label) {
    return "L" + resolveLabel(label);
  }

  protected String printTypeInsnNode(TypeInsnNode tin) {
    try {
      String desc = tin.desc;
      try {
        if (Type.getType(tin.desc) != null) desc = Type.getType(tin.desc).getClassName();

        if (desc.equals("null")) desc = tin.desc;
      } catch (java.lang.ArrayIndexOutOfBoundsException ignored) {

      }
      return nameOpcode(tin.getOpcode()) + " " + desc;
    } catch (Exception e) {
      return nameOpcode(tin.getOpcode()) + " " + tin.desc;
    }
  }

  protected String printIincInsnNode(IincInsnNode iin) {
    return nameOpcode(iin.getOpcode()) + " " + iin.var + " " + iin.incr;
  }

  protected String printTableSwitchInsnNode(TableSwitchInsnNode tin) {
    StringBuilder line = new StringBuilder(nameOpcode(tin.getOpcode()) + " \n");
    List<?> labels = tin.labels;
    int count = 0;
    for (int i = tin.min; i < tin.max + 1; i++) {
      line.append("                val: ").append(i).append(" -> ").append("L").append(resolveLabel((LabelNode) labels.get(count++))).append("\n");
    }
    line.append("                default" + " -> L").append(resolveLabel(tin.dflt));
    return line.toString();
  }

  protected String printLookupSwitchInsnNode(LookupSwitchInsnNode lin) {
    StringBuilder line = new StringBuilder(nameOpcode(lin.getOpcode()) + ": \n");
    List<?> keys = lin.keys;
    List<?> labels = lin.labels;

    for (int i = 0; i < keys.size(); i++) {
      int key = (Integer) keys.get(i);
      LabelNode label = (LabelNode) labels.get(i);
      line.append("                val: ").append(key).append(" -> ").append("L").append(resolveLabel(label)).append("\n");
    }

    line.append("                default" + " -> L").append(resolveLabel(lin.dflt));
    return line.toString();
  }

  protected String printInvokeDynamicInsNode(InvokeDynamicInsnNode idin) {
    StringBuilder sb = new StringBuilder();
    sb.append(nameOpcode(idin.getOpcode())).append(" ").append(idin.bsm.getOwner()).append('.').append(idin.bsm.getName()).append(idin.bsm.getDesc()).append(" : ").append(idin.name).append(idin.desc);

    if (idin.bsmArgs != null) {
      for (Object o : idin.bsmArgs) {
        sb.append(" ");
        sb.append(o.toString());
      }
    }

    return sb.toString();
  }

  protected String printMultiANewArrayInsNode(MultiANewArrayInsnNode mana) {
    return nameOpcode(mana.getOpcode()) + " " + mana.dims + " : " + mana.desc;
  }

  private String printFrameNode(FrameNode frame) {
    StringBuilder sb = new StringBuilder();
    sb.append(nameFrameType(frame.type)).append(" ");
    sb.append("(Locals");
    if (frame.local != null && !frame.local.isEmpty()) {
      sb.append("[").append(frame.local.size()).append("]: ");
      sb.append(frame.local.stream().map(this::printFrameObject).collect(Collectors.joining(", ")));
    } else {
      sb.append("[0]");
    }
    sb.append(") ");

    sb.append("(Stack");
    if (frame.stack != null && !frame.stack.isEmpty()) {
      sb.append("[").append(frame.stack.size()).append("]: ");
      sb.append(frame.stack.stream().map(this::printFrameObject).collect(Collectors.joining(", ")));
    } else {
      sb.append("[0]");
    }
    sb.append(") ");

    return sb.toString();
  }

  private String printFrameObject(Object obj) {
    if (obj instanceof LabelNode) return "label [L" + resolveLabel((LabelNode) obj) + "]";
    if (obj instanceof Integer) {
      switch ((int) obj) {
        case 0:
          return "top";
        case 1:
          return "int";
        case 2:
          return "float";
        case 3:
          return "double";
        case 4:
          return "long";
        case 5:
          return "null";
        case 6:
          return "uninitialized this";
        default:
          return "unknown";
      }
    }
    if (obj instanceof String) return obj.toString();
    return "unknown [" + obj.toString() + "]";
  }

  private String nameFrameType(int type) {
    switch (type) {
      case F_NEW:
        return "    f_new";
      case F_FULL:
        return "    f_full";
      case F_APPEND:
        return "    f_append";
      case F_CHOP:
        return "    f_chop";
      case F_SAME:
        return "    f_same";
      case F_SAME1:
        return "    f_same1";
      default:
        return "    f_unknown" + type;
    }
  }

  protected String nameOpcode(int opcode) {
    return "    " + OpcodeInfo.OPCODES.get(opcode).toLowerCase();
  }

  protected int resolveLabel(LabelNode label) {
    if (labels.containsKey(label)) {
      return labels.get(label);
    } else {
            /*int newLabelIndex = labels.size() + 1;
            labels.put(label, newLabelIndex);
            return newLabelIndex;*/
      throw new IllegalStateException("LabelNode index not found. (Label not in InsnList?)");
    }
  }

  public static void saveTo(File file, InstructionPrinter printer) {
    try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw)) {
      for (String s : printer.createPrint()) {
        bw.write(s);
        bw.newLine();
      }
    } catch (IOException e) {
      BytecodeViewer.handleException(e);
    }
  }

}
