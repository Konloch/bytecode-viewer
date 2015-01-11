package the.bytecode.club.bytecodeviewer.decompilers;

import java.io.File;

import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.MiscUtils;

/**
 * 
 * @author Konloch
 * 
 */

public abstract class JavaDecompiler {

	public abstract String decompileClassNode(ClassNode cn);

	public abstract void decompileToZip(String zipName);

	public abstract void decompileToClass(String className, String classNameSaved);

	public static String getUniqueName(String start, String ext) {
		String s = null;
		boolean b = true;
		File f = null;
		String m = null;
		while (b) {
			m = MiscUtils.randomString(32);
			f = new File(start + m + ext);
			if (!f.exists()) {
				s = start + m;
				b = false;
			}
		}
		return s;
	}
	
	public static int getClassNumber(String start, String ext) {
		boolean b = true;
		int i = 0;
		while (b) {
			File tempF = new File(start + i + ext);
			if (!tempF.exists())
				b = false;
			else
				i++;
		}
		return i;
	}
}
