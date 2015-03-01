package the.bytecode.club.bytecodeviewer.obfuscators.mapping;

import java.util.ArrayList;
import java.util.List;

import the.bytecode.club.bytecodeviewer.obfuscators.mapping.data.FieldMappingData;
import the.bytecode.club.bytecodeviewer.obfuscators.mapping.data.MappingData;
import the.bytecode.club.bytecodeviewer.obfuscators.mapping.data.MethodMappingData;

public class HookMap {
	
	protected List<MappingData> classes;
	protected List<FieldMappingData> fields;
	protected List<MethodMappingData> methods;
	
	public HookMap() {
		classes = new ArrayList<MappingData>();
		fields = new ArrayList<FieldMappingData>();
		methods = new ArrayList<MethodMappingData>();
	}
	
	public void addClass(MappingData clazz) {
		classes.add(clazz);
	}
	
	public void addField(FieldMappingData field) {
		fields.add(field);
	}
	
	public void addMethod(MethodMappingData method) {
		methods.add(method);
	}
	
	public List<MappingData> getClasses() {
		return classes;
	}
	
	public List<FieldMappingData> getFields() {
		return fields;
	}
	
	public List<MethodMappingData> getMethods() {
		return methods;
	}

}