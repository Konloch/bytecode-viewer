import java.util.List;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.api.*;

/**
 ** [Plugin Description Goes Here]
 **
 ** @author [Your Name Goes Here]
 **/

public class Template extends Plugin {

	PluginConsole gui;

	/**
	 * Execute function - this gets executed when the plugin is ran
	 */
	@Override
	public void execute(List<ClassNode> classNodeList) {
		// Create & show the console
		gui = new PluginConsole("Java Template");
		gui.setVisible(true);
		
		// Print out to the console
		print("Class Nodes: " + classNodeList.size());

		// Iterate through each class node
		for (ClassNode cn : classNodeList)
			processClassNode(cn);
		
		// Hide the console after 10 seconds
		BCV.hideFrame(gui, 10000);
	}

	/**
	 * Process each class node
	 */
	public void processClassNode(ClassNode cn) {
		print("Node: " + cn.name + ".class");
		
		//TODO developer plugin code goes here
	}

	/**
	 * Print to console
	 */
	public void print(String text) {
		gui.appendText(text);
	}

}