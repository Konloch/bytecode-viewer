package the.bytecode.club.bytecodeviewer.api;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
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
	 */
	public static ClassNode bytesToNode(final byte[] b)
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
	
	/**
	 * Writes a valid byte[] from the provided classnode
	 */
	public static byte[] nodeToBytes(ClassNode cn)
	{
		final ClassWriter cw = new ClassWriter(0);
		
		try {
			cn.accept(cw);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				Thread.sleep(200);
				cn.accept(cw);
			} catch (InterruptedException ignored) { }
		}
		
		return cw.toByteArray();
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
