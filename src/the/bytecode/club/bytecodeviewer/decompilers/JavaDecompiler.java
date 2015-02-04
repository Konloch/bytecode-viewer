package the.bytecode.club.bytecodeviewer.decompilers;

import org.objectweb.asm.tree.ClassNode;

/**
 * 
 * @author Konloch
 * 
 */

public abstract class JavaDecompiler {

	public abstract String decompileClassNode(ClassNode cn);

	public abstract void decompileToZip(String zipName);

	public abstract void decompileToClass(String className, String classNameSaved);
}
