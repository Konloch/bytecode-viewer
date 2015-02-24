package the.bytecode.club.bytecodeviewer.gui;

import javax.swing.JInternalFrame;

import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.FileChangeNotifier;

/**
 * Used to represent all the panes inside of Bytecode Viewer, this is temp code
 * that was included from porting in J-RET, this needs to be re-written.
 * 
 * @author Konloch
 * @author WaterWolf
 * 
 */

public abstract class VisibleComponent extends JInternalFrame implements
		FileChangeNotifier {

	private static final long serialVersionUID = -6453413772343643526L;

	public VisibleComponent(final String title) {
		super(title, false, false, false, false);
		this.setFrameIcon(null);
	}

	@SuppressWarnings("unused")
	private VisibleComponent() { // because we want to enforce the title
									// argument

	}

	@Override
	public void openClassFile(final String name, final ClassNode cn) {
	}
	@Override
	public void openFile(final String name, byte[] contents) {
	}

}
