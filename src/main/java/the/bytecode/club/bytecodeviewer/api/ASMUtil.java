package the.bytecode.club.bytecodeviewer.api;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author Konloch
 * @since 6/27/2021
 */
public class ASMUtil
{
	/**
	 * Creates a new ClassNode instances from the provided byte[]
	 *
	 * @param b the class file's byte[]
	 * @return the ClassNode instance
	 */
	public static ClassNode getClassNode(final byte[] b)
	{
		ClassReader cr = new ClassReader(b);
		ClassNode cn = new ClassNode();
		try {
			cr.accept(cn, ClassReader.EXPAND_FRAMES);
		} catch (Exception e) {
			cr.accept(cn, ClassReader.SKIP_FRAMES);
		}
		return cn;
	}
	
	public static MethodNode getMethodByName(ClassNode cn, String name)
	{
		for(MethodNode m : cn.methods)
		{
			if(m.name.equals(name))
				return m;
		}
		
		return null;
	}
}
