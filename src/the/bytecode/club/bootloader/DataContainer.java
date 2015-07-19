package the.bytecode.club.bootloader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * @author Bibl (don't ban me pls)
 * @created ages ago
 */
public abstract class DataContainer<T> extends ArrayList<T> {

	private static final long serialVersionUID = -9022506488647444546L;

	public DataContainer() {
		this(16);
	}

	public DataContainer(int cap) {
		super(cap);
	}

	public DataContainer(Collection<T> data) {
		addAll(data);
	}

	public abstract Map<String, T> namedMap();
}