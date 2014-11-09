package the.bytecode.club.bytecodeviewer.decompilers.bytecode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.decompilers.bytecode.TypeAndName;
import eu.bibl.banalysis.asm.desc.OpcodeInfo;

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
		// matchedInsns = new ArrayList<AbstractInsnNode>(); // ingnored because match = false
		match = false;
	}
	
	public InstructionPrinter(MethodNode m, InstructionPattern pattern, TypeAndName[] args) {
		this.args = args;
		mNode = m;
		labels = new HashMap<LabelNode, Integer>();
		searcher = new InstructionSearcher(m.instructions, pattern);
		match = searcher.search();
		if (match) {
			for(AbstractInsnNode[] ains : searcher.getMatches()) {
				for(AbstractInsnNode ain : ains) {
					matchedInsns.add(ain);
				}
			}
		}
	}
	
	/**
	 * Creates the print
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
				if(firstLabel && BytecodeViewer.viewer.chckbxmntmAppendBrackets.isSelected())
					info.add("}");
				
				line = printLabelnode((LabelNode) ain);
				
				if(BytecodeViewer.viewer.chckbxmntmAppendBrackets.isSelected()) {
					if(!firstLabel)
						firstLabel = true;
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
			} else {
				line += "UNADDED OPCODE: " + nameOpcode(ain.getOpcode()) + " " + ain.toString();
			}
			if (!line.equals("")) {
				if (match)
					if (matchedInsns.contains(ain))
						line = "   -> " + line;
				
				info.add(line);
			}
		}
		if(firstLabel && BytecodeViewer.viewer.chckbxmntmAppendBrackets.isSelected())
			info.add("}");
		return info;
	}
	
	protected String printVarInsnNode(VarInsnNode vin, ListIterator<?> it) {
		StringBuilder sb = new StringBuilder();
		sb.append(nameOpcode(vin.getOpcode()));
		sb.append(vin.var);
        if (BytecodeViewer.viewer.debugHelpers.isSelected()) {
            if (vin.var == 0 && !Modifier.isStatic(mNode.access)) {
                sb.append(" // reference to self");
            } else {
                final int refIndex = vin.var - (Modifier.isStatic(mNode.access) ? 0 : 1);
                if (refIndex >= 0 && refIndex < args.length-1) {
                    sb.append(" // reference to " + args[refIndex].name);
                }
            }
        }
        
        return sb.toString();
	}
	
	protected String printIntInsnNode(IntInsnNode iin, ListIterator<?> it) {
		return nameOpcode(iin.getOpcode()) + " " + iin.operand;
	}
	
	protected String printFieldInsnNode(FieldInsnNode fin, ListIterator<?> it) {
		return nameOpcode(fin.getOpcode()) + " " + fin.owner + "." + fin.name + ":" + Type.getType(fin.desc).getClassName();
	}
	
	protected String printMethodInsnNode(MethodInsnNode min, ListIterator<?> it) {
		StringBuilder sb = new StringBuilder();
		sb.append(nameOpcode(min.getOpcode()) + " " + min.owner + " " + min.name + "(");
		
		if(Type.getType(min.desc).getClassName() == null ||
			Type.getType(min.desc).getClassName().equalsIgnoreCase("null"))
		{
			//sb.append(min.desc);
		} else {
			sb.append(Type.getType(min.desc).getClassName());
		}
		sb.append(");");
		
		return sb.toString();
	}

	protected String printLdcInsnNode(LdcInsnNode ldc, ListIterator<?> it) {
    	if(BytecodeViewer.viewer.chckbxmntmNewCheckItem.isSelected()) { //ascii only
			if (ldc.cst instanceof String)
				return nameOpcode(ldc.getOpcode()) + " \"" + StringEscapeUtils.escapeJava(ldc.cst.toString()) + "\" (" + ldc.cst.getClass().getCanonicalName() + ")";
				
			return nameOpcode(ldc.getOpcode()) + " " + StringEscapeUtils.escapeJava(ldc.cst.toString()) + " (" + ldc.cst.getClass().getCanonicalName() + ")";

		} else {
			if (ldc.cst instanceof String)
				return nameOpcode(ldc.getOpcode()) + " \"" + ((String)ldc.cst).replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r").replaceAll("\\\"", "\\\\\"") + "\" (" + ldc.cst.getClass().getCanonicalName() + ")";
				
			return nameOpcode(ldc.getOpcode()) + " " + ldc.cst + " (" + ldc.cst.getClass().getCanonicalName() + ")";
		}
	}
	
	protected String printInsnNode(InsnNode in, ListIterator<?> it) {
		return nameOpcode(in.getOpcode());
	}
	
	protected String printJumpInsnNode(JumpInsnNode jin, ListIterator<?> it) {
		String line = nameOpcode(jin.getOpcode()) + " L" + resolveLabel(jin.label);
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
			return nameOpcode(tin.getOpcode()) + " " + Type.getType(tin.desc).getClassName();
		} catch(Exception e) {
			new the.bytecode.club.bytecodeviewer.gui.StackTraceUI(e);
		}
		return "//error";
	}
	
	protected String printIincInsnNode(IincInsnNode iin) {
		return nameOpcode(iin.getOpcode()) + " " + iin.var + " " + iin.incr;
	}

	protected String printTableSwitchInsnNode(TableSwitchInsnNode tin) {
		String line = nameOpcode(tin.getOpcode()) + " \n";
		List<?> labels = tin.labels;
		int count = 0;
		for (int i = tin.min; i < tin.max; i++) {
			line += "                val: " + i + " -> " + "L" + resolveLabel((LabelNode) labels.get(count++)) + "\n";
		}
		line += "                default" + " -> L" + resolveLabel(tin.dflt) + "";
		return line;
	}

	protected String printLookupSwitchInsnNode(LookupSwitchInsnNode lin) {
		String line = nameOpcode(lin.getOpcode()) + ": \n";
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
	
	/**
	 * Creates the print
	 * @return The print as a string array
	 */
	public String[] getLines() {
		ArrayList<String> lines = createPrint();
		return lines.toArray(new String[lines.size()]);
	}
	
	/**
	 * Static method to print
	 * @param lines To print
	 */
	public static void consolePrint(String[] lines) {
		for(String line : lines) {
			System.out.println(line);
		}
	}
	
	public static void saveTo(File file, InstructionPrinter printer) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for(String s : printer.createPrint()) {
				bw.write(s);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			new the.bytecode.club.bytecodeviewer.gui.StackTraceUI(e);
		}
	}
}