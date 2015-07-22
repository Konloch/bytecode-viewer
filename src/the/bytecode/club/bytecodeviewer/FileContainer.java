package the.bytecode.club.bytecodeviewer;

import java.util.ArrayList;
import java.util.HashMap;

import org.objectweb.asm.tree.ClassNode;

/**
 * Represents a file container
 * 
 * @author Konloch
 *
 */

public class FileContainer {
	
	public String name;
	
	public HashMap<String, byte[]> files = new HashMap<String, byte[]>();
	public ArrayList<ClassNode> classes = new ArrayList<ClassNode>();

}
