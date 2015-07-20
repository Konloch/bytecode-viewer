package the.bytecode.club.bootloader;

/**
 * @author Bibl (don't ban me pls)
 * @created 21 Jul 2015 00:14:53
 */
public abstract interface LoaderFactory<T> {

	public abstract ILoader<T> spawnLoader();
}