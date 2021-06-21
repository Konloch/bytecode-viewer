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
            return other.refactoredName == null;
        } else return refactoredName.equals(other.refactoredName);
    }
}