package the.bytecode.club.bytecodeviewer.gui;

/**
 * Allows us to run a background thread
 * 
 * @author Konloch
 *
 */

public abstract class PaneUpdaterThread extends Thread {
	
	public abstract void doShit();
	
	@Override
	public void run() {
		doShit();
	}
	
}