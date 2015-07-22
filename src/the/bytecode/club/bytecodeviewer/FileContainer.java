package the.bytecode.club.bytecodeviewer;

import java.io.File;
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
	
	public FileContainer(File f) {
		this.file = f;
		this.name = f.getName();
	}
	
	public File file;
	public String name;
	
	public HashMap<String, byte[]> files = new HashMap<String, byte[]>();
	public ArrayList<ClassNode> classes = new ArrayList<ClassNode>();

}
