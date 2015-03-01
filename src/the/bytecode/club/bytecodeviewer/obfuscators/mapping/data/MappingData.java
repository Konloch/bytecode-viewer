package the.bytecode.club.bytecodeviewer.obfuscators.mapping.data;

public class MappingData {

	protected String obfuscatedName;
	protected String refactoredName;

	public MappingData(String refactoredName) {
		this("", refactoredName);
	}

	public MappingData(String obfuscatedName, String refactoredName) {
		this.obfuscatedName = obfuscatedName;
		this.refactoredName = refactoredName;
	}

	public String getObfuscatedName() {
		return obfuscatedName;
	}

	public MappingData setObfuscatedName(String obfuscatedName) {
		this.obfuscatedName = obfuscatedName;
		return this;
	}

	public String getRefactoredName() {
		return refactoredName;
	}

	public MappingData setRefactoredName(String refactoredName) {
		this.refactoredName = refactoredName;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((obfuscatedName == null) ? 0 : obfuscatedName.hashCode());
		result = (prime * result) + ((refactoredName == null) ? 0 : refactoredName.hashCode());
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
		MappingData other = (MappingData) obj;
		if (obfuscatedName == null) {
			if (other.obfuscatedName != null)
				return false;
		} else if (!obfuscatedName.equals(other.obfuscatedName))
			return false;
		if (refactoredName == null) {
			if (other.refactoredName != null)
				return false;
		} else if (!refactoredName.equals(other.refactoredName))
			return false;
		return true;
	}
}