package the.bytecode.club.bytecodeviewer.obfuscators.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.commons.Remapper;

import the.bytecode.club.bytecodeviewer.obfuscators.mapping.data.FieldMappingData;
import the.bytecode.club.bytecodeviewer.obfuscators.mapping.data.MappingData;
import the.bytecode.club.bytecodeviewer.obfuscators.mapping.data.MethodMappingData;

/**
 * @author sc4re
 */
public class RefactorMapper extends Remapper {

    protected final Map<String, MappingData> sortedClasses;
    protected final Map<String, MethodMappingData> sortedMethods;
    protected final Map<String, FieldMappingData> sortedFields;
    protected final List<String> mappingList;

    private StringBuilder builder;
    
    public RefactorMapper(HookMap hookMap) {
        sortedClasses = new HashMap<>();
        sortedMethods = new HashMap<>();
        sortedFields = new HashMap<>();
        mappingList = new ArrayList<>();
        builder = new StringBuilder();
        for(MappingData hook : hookMap.getClasses()){
            if(hook.getObfuscatedName().contains("$"))
                continue;
            String obfuscatedName = hook.getObfuscatedName();
            String refactoredName = hook.getRefactoredName();
            sortedClasses.put(obfuscatedName, hook);
            sortedClasses.put(refactoredName, hook);
        }
        for (MethodMappingData hook : hookMap.getMethods()) {
            String obfuscatedName = hook.getMethodName().getObfuscatedName();
            String obfuscatedDesc = hook.getMethodDesc();
            String obfuscatedCname = hook.getMethodOwner();
            sortedMethods.put(obfuscatedCname + "$$$$" + obfuscatedName + "$$$$" + obfuscatedDesc, hook);
        }
        for (FieldMappingData hook : hookMap.getFields()) {
            String obfuscatedName = hook.getName().getObfuscatedName();
            String obfuscatedDesc = hook.getDesc();
            String obfuscatedCname = hook.getFieldOwner();
            sortedFields.put(obfuscatedCname + "$$$$" + obfuscatedName + "$$$$" + obfuscatedDesc, hook);
        }
    }

    @Override
    public String map(String type) {
        if (sortedClasses.containsKey(type)) {
        	String map = new String(type + " --> " + sortedClasses.get(type).getRefactoredName() + "\n");
        	if (!mappingList.contains(map))
        		mappingList.add(map);
        	
            return sortedClasses.get(type).getRefactoredName();
        }
        return type;
    }

    @Override
    public String mapFieldName(String owner, String name, String desc) {
        String obfKey = owner + "$$$$" + name + "$$$$" + desc;
        if (sortedFields.containsKey(obfKey)) {
        	String map = new String(owner + "." + name + " --> " + owner + sortedFields.get(obfKey).getName().getRefactoredName() + "\n");
        	if (!mappingList.contains(map))
        		mappingList.add(map);
            name = sortedFields.get(obfKey).getName().getRefactoredName();
        }
        return name;
    }

    @Override
    public String mapMethodName(String owner, String name, String desc) {
        String obfKey = owner + "$$$$" + name + "$$$$" + desc;
        if (sortedMethods.containsKey(obfKey)) {
        	String map = new String(owner + "." + name + " --> " + owner + sortedMethods.get(obfKey).getMethodName().getRefactoredName() + "\n");
        	if (!mappingList.contains(map))
        		mappingList.add(map);
            name = sortedMethods.get(obfKey).getMethodName().getRefactoredName();
        }
        return name;
    }

	public void printMap() {
		for (String map : mappingList) {
			builder.append(map);
		}
		System.out.println(builder.toString());
	}
}
