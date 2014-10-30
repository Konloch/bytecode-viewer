package the.bytecode.club.bytecodeviewer.plugins;

import java.util.ArrayList;

import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;

/**
 * Coming soon.
 * 
 * @author Konloch
 *
 */

public class ZKMStringDecrypter extends Plugin {

	@Override
	public void execute(ArrayList<ClassNode> classNodeList) {
		for(ClassNode classNode : classNodeList) {
			
		}
		BytecodeViewer.showMessage("This is a planned feature.");
	}

}
