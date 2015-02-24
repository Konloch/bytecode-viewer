package the.bytecode.club.bytecodeviewer;

import org.objectweb.asm.tree.ClassNode;

/**
 * Used to represent whenever a file has been opened
 * 
 * @author Konloch
 * 
 */

public interface FileChangeNotifier {
	public void openClassFile(String name, ClassNode cn);
	public void openFile(String name, byte[] contents);
}