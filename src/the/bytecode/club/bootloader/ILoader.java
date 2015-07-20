package the.bytecode.club.bootloader;

import the.bytecode.club.bootloader.resource.ExternalResource;

/**
 * @author Bibl (don't ban me pls)
 * @created 19 Jul 2015 02:29:43
 */
public abstract interface ILoader<T> {

	public abstract void bind(ExternalResource<T> resource);
	
	abstract Class<?> findClass(String name) throws ClassNotFoundException, NoClassDefFoundError;

	public abstract Class<?> loadClass(String name) throws ClassNotFoundException, NoClassDefFoundError;
}