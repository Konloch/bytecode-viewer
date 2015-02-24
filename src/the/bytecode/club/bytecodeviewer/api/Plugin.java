package the.bytecode.club.bytecodeviewer.api;

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
			if(BytecodeViewer.getLoadedClasses().isEmpty()) {
				BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
				return;
			}
			execute(BytecodeViewer.getLoadedClasses());
		} catch (Exception e) {
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
		} finally {
			finished = true;
			BytecodeViewer.viewer.setIcon(false);
		}
	}

	private boolean finished = false;

	/**
	 * When the plugin is finally finished, this will return true
	 * 
	 * @return true if the plugin is finished executing
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * If for some reason your plugin needs to keep the thread alive, yet will
	 * still be considered finished (EZ-Injection), you can call this function
	 * and it will set the finished boolean to true.
	 */
	public void setFinished() {
		finished = true;
	}

	/**
	 * Whenever the plugin is started, this method is called
	 * 
	 * @param classNodeList
	 *            all of the loaded classes for easy access.
	 */
	public abstract void execute(ArrayList<ClassNode> classNodeList);

}
