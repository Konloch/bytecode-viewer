package the.bytecode.club.bootloader;

import org.objectweb.asm.tree.ClassNode;

/**
 * @author Bibl (don't ban me pls)
 * @created 19 Jul 2015 02:29:43
 */
public abstract interface ILoader {

	public abstract void bind(ExternalResource<JarContents<ClassNode>> resource);
	
	abstract Class<?> findClass(String name) throws ClassNotFoundException, NoClassDefFoundError;

	public abstract Class<?> loadClass(String name) throws ClassNotFoundException, NoClassDefFoundError;
}