package the.bytecode.club.bytecodeviewer.obfuscators.mapping.data;

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
            return other.name == null;
        } else return name.equals(other.name);
    }
}