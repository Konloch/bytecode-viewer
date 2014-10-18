package the.bytecode.club.bytecodeviewer.plugins;

import java.util.ArrayList;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;

/**
 * Simply shows all the non-empty strings in every single class
 * 
 * @author Konloch
 *
 */

public class ShowAllStrings extends Plugin {

	@Override
	public void execute(ArrayList<ClassNode> classNodeList) {
		PluginConsole frame = new PluginConsole("Show All Strings");
		BytecodeViewer.viewer.setC(true);
		for(ClassNode classNode : classNodeList) {
			for(Object o : classNode.fields.toArray()) {
				FieldNode f = (FieldNode) o;
				Object v = f.value;
				if(v instanceof String) {
					String s = (String)v;
            		if(!s.isEmpty())
            			frame.appendText(classNode.name + "." +f.name+""+f.desc+" -> \"" + s.replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r") + "\"");
				}
				if(v instanceof String[]) {
					for(int i = 0; i < ((String[])v).length; i++) {
						String s = ((String[])v)[i];
						if(!s.isEmpty())
							frame.appendText(classNode.name + "." +f.name+""+f.desc+"["+i+"] -> \"" + s.replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r") + "\"");
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
		            		if(!s.isEmpty())
		            			frame.appendText(classNode.name + "." +m.name+""+m.desc+" -> \"" + s.replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r") + "\"");
		            	}
		            }
				}
			}
		}
		BytecodeViewer.viewer.setC(false);
		frame.setVisible(true);
	}

}
