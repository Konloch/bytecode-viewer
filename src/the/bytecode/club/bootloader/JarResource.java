package the.bytecode.club.bootloader;

import java.util.Arrays;

/**
 * @author Bibl (don't ban me pls)
 * @created ages ago
 */
public class JarResource {
	
	private final String name;
	private final byte[] data;
	
	public JarResource(String name, byte[] data) {
		this.name = name;
		this.data = data;
	}
	
	public String getName() {
		return name;
	}
	
	public byte[] getData() {
		return data;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + Arrays.hashCode(data);
		result = (prime * result) + ((name == null) ? 0 : name.hashCode());
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
		JarResource other = (JarResource) obj;
		if (!Arrays.equals(data, other.data))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}