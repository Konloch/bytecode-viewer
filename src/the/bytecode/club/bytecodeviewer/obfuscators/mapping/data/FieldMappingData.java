package the.bytecode.club.bytecodeviewer.obfuscators.mapping.data;


public class FieldMappingData {
	
	protected String fieldOwner;
	protected MappingData name;
	protected String desc;

	public FieldMappingData(MappingData name, String desc) {
		this("", name, desc);
	}
	
	public FieldMappingData(String fieldOwner, MappingData name, String desc) {
		this.fieldOwner = fieldOwner;
		this.name = name;
		this.desc = desc;
	}
	
	public String getFieldOwner() {
		return fieldOwner;
	}
	
	public FieldMappingData setFieldOwner(String fieldOwner) {
		this.fieldOwner = fieldOwner;
		return this;
	}
	
	public MappingData getName() {
		return name;
	}
	
	public FieldMappingData setName(MappingData name) {
		this.name = name;
		return this;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public FieldMappingData setDesc(String desc) {
		this.desc = desc;
		return this;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((desc == null) ? 0 : desc.hashCode());
		result = (prime * result) + ((fieldOwner == null) ? 0 : fieldOwner.hashCode());
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
		FieldMappingData other = (FieldMappingData) obj;
		if (desc == null) {
			if (other.desc != null)
				return false;
		} else if (!desc.equals(other.desc))
			return false;
		if (fieldOwner == null) {
			if (other.fieldOwner != null)
				return false;
		} else if (!fieldOwner.equals(other.fieldOwner))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}