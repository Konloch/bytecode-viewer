package the.bytecode.club.bytecodeviewer.obfuscators.mapping;

import java.util.ArrayList;
import java.util.List;
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

public class HookMap {

    protected List<MappingData> classes;
    protected List<FieldMappingData> fields;
    protected List<MethodMappingData> methods;

    public HookMap() {
        classes = new ArrayList<>();
        fields = new ArrayList<>();
        methods = new ArrayList<>();
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