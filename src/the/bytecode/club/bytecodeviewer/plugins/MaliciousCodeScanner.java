package the.bytecode.club.bytecodeviewer.plugins;

import java.util.ArrayList;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;

/**
 * The idea/core was based off of J-RET's Malicious Code Searcher
 * I improved it, and added more stuff to search for.
 * 
 * @author Konloch
 * @author WaterWolf
 *
 */

public class MaliciousCodeScanner extends Plugin {

	public boolean
			ORE,
			ONE,
			ORU,
			OIO,
			LWW,
			LHT,
			LHS,
			LIP;
	
	public MaliciousCodeScanner(boolean reflect, boolean runtime, boolean net, boolean io,
			boolean www, boolean http, boolean https, boolean ip) {
		ORE = reflect;
		ONE = net;
		ORU = runtime;
		OIO = io;
		LWW = www;
		LHT = http;
		LHS = https;
		LIP = ip;
	}
	
	@Override
	public void execute(ArrayList<ClassNode> classNodeList) {
		PluginConsole frame = new PluginConsole("Malicious Code Scanner");
		StringBuilder sb = new StringBuilder();
		for(ClassNode classNode : classNodeList) {
			for(Object o : classNode.fields.toArray()) {
				FieldNode f = (FieldNode) o;
				Object v = f.value;
				if(v instanceof String) {
					String s = (String)v;
	                if ((LWW && s.contains("www.")) ||
		                	(LHT && s.contains("http://")) ||
		                	(LHS && s.contains("https://")) ||
		                	(ORE && s.contains("java/lang/Runtime")) ||
		                	(ORE && s.contains("java.lang.Runtime")) ||
		                	(LIP && s.matches("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b")))
		                	sb.append("Found LDC \"" + s + "\" at field " + classNode.name + "." +f.name+"("+f.desc+")"+BytecodeViewer.nl);
				}
				if(v instanceof String[]) {
					for(int i = 0; i < ((String[])v).length; i++) {
						String s = ((String[])v)[i];
			                if ((LWW && s.contains("www.")) ||
			                	(LHT && s.contains("http://")) ||
			                	(LHS && s.contains("https://")) ||
			                	(ORE && s.contains("java/lang/Runtime")) ||
			                	(ORE && s.contains("java.lang.Runtime")) ||
			                	(LIP && s.matches("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b")))
			                	sb.append("Found LDC \"" + s + "\" at field " + classNode.name + "." +f.name+"("+f.desc+")"+BytecodeViewer.nl);
					}
				}
			}
			
			for(Object o : classNode.methods.toArray()) {
				MethodNode m = (MethodNode) o;
			
				InsnList iList = m.instructions;
				for(AbstractInsnNode a : iList.toArray()) {
		            if (a instanceof MethodInsnNode) {
		                final MethodInsnNode min = (MethodInsnNode) a;
		                if ((ORE && min.owner.startsWith("java/lang/reflect")) ||
		                	(ONE && min.owner.startsWith("java/net")) ||
		                	(ORU && min.owner.equals("java/lang/Runtime")) ||
		                	(OIO && min.owner.startsWith("java/io")))
		                {
		                	sb.append("Found Method call to " + min.owner + "." + min.name + "(" + min.desc + ") at " + classNode.name + "." +m.name+"("+m.desc+")"+BytecodeViewer.nl);
		                }
		            }
		            if (a instanceof LdcInsnNode) {
		            	if(((LdcInsnNode)a).cst instanceof String) {
			                final String s = (String) ((LdcInsnNode)a).cst;
			                if ((LWW && s.contains("www.")) ||
			                	(LHT && s.contains("http://")) ||
			                	(LHS && s.contains("https://")) ||
			                	(ORE && s.contains("java/lang/Runtime")) ||
			                	(ORE && s.contains("java.lang.Runtime")) ||
			                	(LIP && s.matches("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b")))
			                {
			                	sb.append("Found LDC \"" + s + "\" at method " + classNode.name + "." +m.name+"("+m.desc+")"+BytecodeViewer.nl);
			                }
		            	}
		            }
				}
			}
		}
		
		frame.appendText(sb.toString());
		frame.setVisible(true);
	}

}
