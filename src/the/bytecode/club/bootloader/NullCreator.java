package the.bytecode.club.bootloader;

/**
 * @author Bibl (don't ban me pls)
 * @created ages ago
 */
public class NullCreator<V> implements ValueCreator<V> {

	@Override
	public V create() {
		return null;
	}
}