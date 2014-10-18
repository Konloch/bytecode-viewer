package the.bytecode.club.bytecodeviewer.plugins;

import java.util.ArrayList;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;

/**
 * Simply shows all classes that have a public static void main(String[])
 * 
 * @author Konloch
 *
 */

public class ShowMainMethods extends Plugin {

	@Override
	public void execute(ArrayList<ClassNode> classNodeList) {
		PluginConsole frame = new PluginConsole("Show Main Methods");
		BytecodeViewer.viewer.setC(true);
		for(ClassNode classNode : classNodeList) {
			for(Object o : classNode.methods.toArray()) {
				MethodNode m = (MethodNode) o;
			
				if(m.name.equals("main") && m.desc.equals("([Ljava/lang/String;)V"))
        			frame.appendText(classNode.name + "." +m.name+""+m.desc);
			}
		}
		BytecodeViewer.viewer.setC(false);
		frame.setVisible(true);
	}

}
