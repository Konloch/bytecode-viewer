package the.bytecode.club.bytecodeviewer.decompilers.java;

import java.io.File;

import org.objectweb.asm.tree.ClassNode;

/**
 * 
 * @author Konloch
 * 
 */

public abstract class JavaDecompiler {

	public abstract String decompileClassNode(ClassNode cn);

	public abstract void decompileToZip(String zipName);

	File tempF = null;

	public int getClassNumber(String start, String ext) {
		boolean b = true;
		int i = 0;
		while (b) {
			tempF = new File(start + i + ext);
			if (!tempF.exists())
				b = false;
			else
				i++;
		}
		return i;
	}
}
