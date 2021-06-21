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

public class MethodMappingData {

    protected String methodOwner;
    protected MappingData methodName;
    protected String methodDesc;

    public MethodMappingData(MappingData methodName, String methodDesc) {
        this("", methodName, methodDesc);
    }

    public MethodMappingData(String methodOwner, MappingData methodName, String methodDesc) {
        this.methodOwner = methodOwner;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    public String getMethodOwner() {
        return methodOwner;
    }

    public MethodMappingData setMethodOwner(String methodOwner) {
        this.methodOwner = methodOwner;
        return this;
    }

    public MappingData getMethodName() {
        return methodName;
    }

    public MethodMappingData setMethodName(MappingData methodName) {
        this.methodName = methodName;
        return this;
    }

    public String getMethodDesc() {
        return methodDesc;
    }

    public MethodMappingData setMethodDesc(String methodDesc) {
        this.methodDesc = methodDesc;
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((methodDesc == null) ? 0 : methodDesc.hashCode());
        result = (prime * result) + ((methodName == null) ? 0 : methodName.hashCode());
        result = (prime * result) + ((methodOwner == null) ? 0 : methodOwner.hashCode());
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
        MethodMappingData other = (MethodMappingData) obj;
        if (methodDesc == null) {
            if (other.methodDesc != null)
                return false;
        } else if (!methodDesc.equals(other.methodDesc))
            return false;
        if (methodName == null) {
            if (other.methodName != null)
                return false;
        } else if (!methodName.equals(other.methodName))
            return false;
        if (methodOwner == null) {
            return other.methodOwner == null;
        } else return methodOwner.equals(other.methodOwner);
    }
}