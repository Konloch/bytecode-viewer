package the.bytecode.club.bootloader.resource;

import java.io.IOException;
import java.net.URL;

/**
 * @author Bibl (don't ban me pls)
 * @created 21 Jul 2015 00:29:11
 */
public class EmptyExternalResource<T> extends ExternalResource<T> {

	/**
	 * @param location
	 */
	public EmptyExternalResource(URL location) {
		super(location);
	}

	/* (non-Javadoc)
	 * @see the.bytecode.club.bootloader.resource.ExternalResource#load()
	 */
	@Override
	public T load() throws IOException {
		throw new UnsupportedOperationException();
	}
}