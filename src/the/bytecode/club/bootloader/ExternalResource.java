package the.bytecode.club.bootloader;

import java.io.IOException;
import java.net.URL;

/**
 * @author Bibl (don't ban me pls)
 * @created 19 Jul 2015 02:30:30
 */
public abstract class ExternalResource<T> {

	private final URL location;

	public ExternalResource(URL location) {
		if(location == null)
			throw new IllegalArgumentException();
		this.location = location;
	}

	public URL getLocation() {
		return location;
	}
	
	public abstract T load() throws IOException;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExternalResource<?> other = (ExternalResource<?>) obj;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Library @" + location.toExternalForm();
	}
}