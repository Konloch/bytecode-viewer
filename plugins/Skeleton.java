import the.bytecode.club.bytecodeviewer.api.*;
import java.util.ArrayList;
import org.objectweb.asm.tree.ClassNode;

public class Skeleton extends Plugin {

	@Override
	public void execute(ArrayList<ClassNode> classNodesList) {
		PluginConsole gui = new PluginConsole("Skeleton");
		gui.setVisible(true);
		gui.appendText("executed skeleton");
	}
}