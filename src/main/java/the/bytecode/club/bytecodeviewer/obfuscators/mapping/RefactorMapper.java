package the.bytecode.club.bytecodeviewer.obfuscators.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import the.bytecode.club.bytecodeviewer.obfuscators.mapping.data.FieldMappingData;
import the.bytecode.club.bytecodeviewer.obfuscators.mapping.data.MappingData;
import the.bytecode.club.bytecodeviewer.obfuscators.mapping.data.MethodMappingData;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 *                                                                         *
 * This program is free software: you can redistribute it and/or modify    *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

/**
 * @author sc4re
 */
public class RefactorMapper extends Remapper {

    protected final Map<String, MappingData> sortedClasses;
    protected final Map<String, MethodMappingData> sortedMethods;
    protected final Map<String, FieldMappingData> sortedFields;
    protected final List<String> mappingList;

    private final StringBuilder builder;

    public RefactorMapper(HookMap hookMap) {
        sortedClasses = new HashMap<>();
        sortedMethods = new HashMap<>();
        sortedFields = new HashMap<>();
        mappingList = new ArrayList<>();
        builder = new StringBuilder();
        for (MappingData hook : hookMap.getClasses()) {
            if (hook.getObfuscatedName().contains("$"))
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
            String map = type + " --> " + sortedClasses.get(type).getRefactoredName() + "\n";
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
            String map =
                    owner + "." + name + " --> " + owner + sortedFields.get(obfKey).getName().getRefactoredName() +
                            "\n";
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
            String map =
                    owner + "." + name + " --> " + owner + sortedMethods.get(obfKey).getMethodName().getRefactoredName() + "\n";
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
