import the.bytecode.club.bytecodeviewer.api.*;
import java.util.ArrayList;
import org.objectweb.asm.tree.ClassNode;

public class Template extends Plugin
{
	PluginConsole gui = new PluginConsole("Java Template");
	
	/**
	 * Main function
	 */
	@Override
	public void execute(ArrayList<ClassNode> classNodeList)
	{
		gui.setVisible(true); //show the console
		out("Class Nodes: " + classNodeList.size());
		
		//iterate through each class node
		for(ClassNode cn : classNodeList)
			process(cn);
		
		BytecodeViewer.hideFrame(gui, 10000); //hides the console after 10 seconds
	}
	
	/**
	 * Process each class node
	 */
	public void process(ClassNode cn)
	{
		out("Node: " + cn.name + ".class");
		//TODO developer plugin code goes here
	}
	
	/**
	 * Print to console
	 */
	public void out(String text)
	{
		gui.appendText(text);
	}
}