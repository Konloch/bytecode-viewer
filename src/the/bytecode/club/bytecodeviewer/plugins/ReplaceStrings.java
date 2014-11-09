package the.bytecode.club.bytecodeviewer.plugins;

import java.util.ArrayList;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Replaces all string and string[] instances with whatever.
 * 
 * @author Konloch
 *
 */

public class ReplaceStrings extends Plugin {

	PluginConsole frame = new PluginConsole("Replace Strings");
	String originalLDC;
	String newLDC;
	String className;
	boolean contains;
	
	public ReplaceStrings(String originalLDC, String newLDC, String className, boolean contains) {
		this.originalLDC = originalLDC;
		this.newLDC = newLDC;
		this.className = className;
		this.contains = contains;
	}
	
	@Override
	public void execute(ArrayList<ClassNode> classNodeList) {
		if(!className.equals("*")) {
			for(ClassNode classNode : classNodeList) {
				if(classNode.name.equals(className))
					scanClassNode(classNode);
			}
		} else {
			for(ClassNode classNode : classNodeList) {
				scanClassNode(classNode);
			}
		}
		frame.setVisible(true);
	}

	
	public void scanClassNode(ClassNode classNode) {
		for(Object o : classNode.fields.toArray()) {
			FieldNode f = (FieldNode) o;
			Object v = f.value;
			if(v instanceof String) {
				String s = (String)v;
				if(contains) {
					if(s.contains(originalLDC))
						f.value = ((String)f.value).replaceAll(originalLDC, newLDC);
				} else {
					if(s.equals(originalLDC))
						f.value = newLDC;
				}
			}
			if(v instanceof String[]) {
				for(int i = 0; i < ((String[])v).length; i++) {
					String s = ((String[])v)[i];
    				if(contains) {
    					if(s.contains(originalLDC)) {
    						f.value = ((String[])f.value)[i].replaceAll(originalLDC, newLDC);
    						String ugh = s.replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r");
                			frame.appendText(classNode.name + "." +f.name+""+f.desc+" -> \"" + ugh + "\" replaced with \"" + s.replaceAll(originalLDC, newLDC) + "\"");
    					}
    				} else {
    					if(s.equals(originalLDC)) {
    						((String[])f.value)[i] = newLDC;
    						String ugh = s.replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r");
                			frame.appendText(classNode.name + "." +f.name+""+f.desc+" -> \"" + ugh + "\" replaced with \"" + newLDC + "\"");
    					}
    				}
	        	}
			}
		}
		
		for(Object o : classNode.methods.toArray()) {
			MethodNode m = (MethodNode) o;
		
			InsnList iList = m.instructions;
			for(AbstractInsnNode a : iList.toArray()) {
	            if (a instanceof LdcInsnNode) {
	            	if(((LdcInsnNode)a).cst instanceof String) {
	            		final String s = (String) ((LdcInsnNode)a).cst;
	    				if(contains) {
	    					if(s.contains(originalLDC)) {
	    						((LdcInsnNode)a).cst = ((String)((LdcInsnNode)a).cst).replaceAll(originalLDC, newLDC);
	    						String ugh = s.replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r");
	                			frame.appendText(classNode.name + "." +m.name+""+m.desc+" -> \"" + ugh + "\" replaced with \"" + s.replaceAll(originalLDC, newLDC).replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r") + "\"");
	    					}
	    				} else {
	    					if(s.equals(originalLDC)) {
	    						((LdcInsnNode)a).cst = newLDC;
	    						String ugh = s.replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r");
	                			frame.appendText(classNode.name + "." +m.name+""+m.desc+" -> \"" + ugh + "\" replaced with \"" + newLDC.replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r") + "\"");
	    					}
	    				}
	            	}
	            }
			}
		}
	}
}
