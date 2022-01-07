import java.util.List;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.api.*;

public class Template extends Plugin {

	PluginConsole gui;

	/**
	 * Main function
	 */
	@Override
	public void execute(List<ClassNode> classNodeList) {
		// Create console
		gui = new PluginConsole("Java Template");
		gui.setVisible(true); // Show the console

		// Debug text
		out("Class Nodes: " + classNodeList.size());

		// Iterate through each class node
		for (ClassNode cn : classNodeList)
			process(cn);

		BCV.hideFrame(gui, 10000); // Hides the console after 10 seconds
	}

	/**
	 * Process each class node
	 */
	public void process(ClassNode cn) {
		out("Node: " + cn.name + ".class");
		// TODO developer plugin code goes here
	}

	/**
	 * Print to console
	 */
	public void out(String text) {
		gui.appendText(text);
	}

}
