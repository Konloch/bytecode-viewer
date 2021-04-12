/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2011 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package the.bytecode.club.bytecodeviewer.obfuscators.mapping;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.signature.SignatureWriter;

/**
 * A class responsible for remapping types and names. Subclasses can override
 * the following methods:
 * <p>
 * <ul>
 * <li>{@link #map(String)} - map type</li>
 * <li>{@link #mapFieldName(String, String, String)} - map field name</li>
 * <li>{@link #mapMethodName(String, String, String)} - map method name</li>
 * </ul>
 *
 * @author Eugene Kuleshov
 */
public abstract class Remapper extends org.objectweb.asm.commons.Remapper {

    @Override
    public String mapDesc(String desc) {
        Type t = Type.getType(desc);
        switch (t.getSort()) {
        case Type.ARRAY:
            StringBuilder s = new StringBuilder(mapDesc(t.getElementType().getDescriptor()));
            for (int i = 0; i < t.getDimensions(); ++i) {
                s.insert(0, '[');
            }
            return s.toString();
        case Type.OBJECT:
            String newType = map(t.getInternalName());
            if (newType != null) {
                return 'L' + newType + ';';
            }
        }
        return desc;
    }

    private Type mapType(Type t) {
        switch (t.getSort()) {
        case Type.ARRAY:
            StringBuilder s = new StringBuilder(mapDesc(t.getElementType().getDescriptor()));
            for (int i = 0; i < t.getDimensions(); ++i) {
                s.insert(0, '[');
            }
            return Type.getType(s.toString());
        case Type.OBJECT:
            s = new StringBuilder(map(t.getInternalName()));
            return Type.getObjectType(s.toString());
        case Type.METHOD:
            return Type.getMethodType(mapMethodDesc(t.getDescriptor()));
        }
        return t;
    }

    @Override
    public String mapType(String type) {
        if (type == null) {
            return null;
        }
        return mapType(Type.getObjectType(type)).getInternalName();
    }

    @Override
    public String[] mapTypes(String[] types) {
        String[] newTypes = null;
        boolean needMapping = false;
        for (int i = 0; i < types.length; i++) {
            String type = types[i];
            String newType = map(type);
            if (newType != null && newTypes == null) {
                newTypes = new String[types.length];
                if (i > 0) {
                    System.arraycopy(types, 0, newTypes, 0, i);
                }
                needMapping = true;
            }
            if (needMapping) {
                newTypes[i] = newType == null ? type : newType;
            }
        }
        return needMapping ? newTypes : types;
    }

    @Override
    public String mapMethodDesc(String desc) {
        if ("()V".equals(desc)) {
            return desc;
        }

        Type[] args = Type.getArgumentTypes(desc);
        StringBuilder sb = new StringBuilder("(");
        for (Type arg : args) {
            sb.append(mapDesc(arg.getDescriptor()));
        }
        Type returnType = Type.getReturnType(desc);
        if (returnType == Type.VOID_TYPE) {
            sb.append(")V");
            return sb.toString();
        }
        sb.append(')').append(mapDesc(returnType.getDescriptor()));
        return sb.toString();
    }

    @Override
    public Object mapValue(Object value) {
        if (value instanceof Type) {
            return mapType((Type) value);
        }
        if (value instanceof Handle) {
            Handle h = (Handle) value;
            return new Handle(h.getTag(), mapType(h.getOwner()), mapMethodName(
                    h.getOwner(), h.getName(), h.getDesc()),
                    mapMethodDesc(h.getDesc()), h.getTag() == Opcodes.H_INVOKEINTERFACE);
        }
        return value;
    }

    /**
     * @param typeSignature true if signature is a FieldTypeSignature, such as the
     *                      signature parameter of the ClassVisitor.visitField or
     *                      MethodVisitor.visitLocalVariable methods
     */
    @Override
    public String mapSignature(String signature, boolean typeSignature) {
        if (signature == null) {
            return null;
        }
        SignatureReader r = new SignatureReader(signature);
        SignatureWriter w = new SignatureWriter();
        SignatureVisitor a = createSignatureRemapper(w);
        if (typeSignature) {
            r.acceptType(a);
        } else {
            r.accept(a);
        }
        return w.toString();
    }

    @Override
    protected SignatureVisitor createSignatureRemapper(SignatureVisitor v) {
        return new RemappingSignatureAdapter(v, this);
    }

    /**
     * Map method name to the new name. Subclasses can override.
     *
     * @param owner owner of the method.
     * @param name  name of the method.
     * @param desc  descriptor of the method.
     * @return new name of the method
     */
    @Override
    public String mapMethodName(String owner, String name, String desc) {
        return name;
    }

    /**
     * Map invokedynamic method name to the new name. Subclasses can override.
     *
     * @param name name of the invokedynamic.
     * @param desc descriptor of the invokedynamic.
     * @return new invokdynamic name.
     */
    @Override
    public String mapInvokeDynamicMethodName(String name, String desc) {
        return name;
    }

    /**
     * Map field name to the new name. Subclasses can override.
     *
     * @param owner owner of the field.
     * @param name  name of the field
     * @param desc  descriptor of the field
     * @return new name of the field.
     */
    @Override
    public String mapFieldName(String owner, String name, String desc) {
        return name;
    }

    /**
     * Map type name to the new name. Subclasses can override.
     */
    @Override
    public String map(String typeName) {
        return typeName;
    }
}
