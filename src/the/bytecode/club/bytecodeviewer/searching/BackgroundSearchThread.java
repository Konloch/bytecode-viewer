package the.bytecode.club.bytecodeviewer.searching;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;

/**
 * A simple class to make searching run in a background thread.
 * 
 * @author Konloch
 *
 */

public abstract class BackgroundSearchThread extends Thread {

	public BackgroundSearchThread() {
		
	}
	
	public BackgroundSearchThread(boolean finished) {
		this.finished = finished;
	}
	
	public boolean finished = false;
	
	public abstract void doSearch();
	
	@Override
	public void run() {
		BytecodeViewer.viewer.setIcon(true);
		doSearch();
		finished = true;
		BytecodeViewer.viewer.setIcon(false);
	}
	
}
