package the.bytecode.club.bytecodeviewer.gui;

import javax.swing.JPanel;
import org.objectweb.asm.tree.ClassNode;

public abstract class Viewer extends JPanel {
	
	public ClassNode cn;
	public String name;
	
	private static final long serialVersionUID = -2965538493489119191L;

}
