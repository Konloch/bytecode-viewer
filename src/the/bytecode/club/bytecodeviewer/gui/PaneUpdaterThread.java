package the.bytecode.club.bytecodeviewer.gui;

import javax.swing.JEditorPane;

/**
 * Allows us to run a background thread then update the two JEditorPanes
 * 
 * @author Konloch
 *
 */

public abstract class PaneUpdaterThread extends Thread {

	JEditorPane p1;
	JEditorPane p2;
	public PaneUpdaterThread(JEditorPane p1, JEditorPane p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public abstract void doShit();
	
	@Override
	public void run() {
		doShit();
	}
	
}