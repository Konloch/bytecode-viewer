package the.bytecode.club.bytecodeviewer.plugins;

import java.util.ArrayList;

import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;

/**
 * A simple plugin class, it will run the plugin in a background thread.
 * 
 * @author Konloch
 *
 */

public abstract class Plugin extends Thread {
	
	@Override
	public void run() {
		BytecodeViewer.viewer.setIcon(true);
		try {
			if(!BytecodeViewer.getLoadedClasses().isEmpty())
				execute(BytecodeViewer.getLoadedClasses());
			else {
				System.out.println("Plugin not ran, put some classes in first.");
				BytecodeViewer.showMessage("Plugin not ran, put some classes in first.");
			}
		} catch(Exception e) {
			new the.bytecode.club.bytecodeviewer.gui.StackTraceUI(e);
		} finally {
			finished = true;
			BytecodeViewer.viewer.setIcon(false);
		}
	}
	
	public boolean finished = false;
	public abstract void execute(ArrayList<ClassNode> classNodeList);

}
