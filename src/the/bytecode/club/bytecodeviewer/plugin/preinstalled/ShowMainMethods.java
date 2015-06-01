package the.bytecode.club.bytecodeviewer.plugin.preinstalled;

import java.util.ArrayList;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.api.PluginConsole;

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
		for (ClassNode classNode : classNodeList) {
			for (Object o : classNode.methods.toArray()) {
				MethodNode m = (MethodNode) o;

				if (m.name.equals("main")
						&& m.desc.equals("([Ljava/lang/String;)V"))
					frame.appendText(classNode.name + "." + m.name + ""
							+ m.desc);
			}
		}
		frame.setVisible(true);
	}

}
