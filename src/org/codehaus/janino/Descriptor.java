
/*
 * Janino - An embedded Java[TM] compiler
 *
 * Copyright (c) 2001-2010, Arno Unkrig
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *       following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *       following disclaimer in the documentation and/or other materials provided with the distribution.
 *    3. The name of the author may not be used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.codehaus.janino;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class that defines useful methods for handling "field descriptors"
 * (JVMS 4.3.2) and "method descriptors" (JVMS 4.3.3).<p>
 * Typical descriptors are:
 * <ul>
 *   <li><code>I</code> Integer
 *   <li><code>[I</code> Array of integer
 *   <li><code>Lpkg1/pkg2/Cls;</code> Class
 *   <li><code>Lpkg1/pkg2/Outer$Inner;</code> Member class
 * </ul>
 */
@SuppressWarnings({ "rawtypes", "unchecked" }) public final
class Descriptor {
    private Descriptor() {}

    /** @return Whether this {@link Descriptor} describes a reference (i.e. non-primitive) type */
    public static boolean
    isReference(String d) { return d.length() > 1; }

    /**
     * @return Whether this {@link Descriptor} describes a class or an interface (and not an array or a primitive type)
     */
    public static boolean
    isClassOrInterfaceReference(String d) { return d.charAt(0) == 'L'; }

    /** @return Whether this {@link Descriptor} describes an array type */
    public static boolean
    isArrayReference(String d) { return d.charAt(0) == '['; }

    /**
     * @return                        The descriptor of the component of the array type {@code d}
     * @throws JaninoRuntimeException {@code d} does not describe an array type
     */
    public static String
    getComponentDescriptor(String d) {
        if (d.charAt(0) != '[') {
            throw new JaninoRuntimeException(
                "Cannot determine component descriptor from non-array descriptor \""
                + d
                + "\""
            );
        }
        return d.substring(1);
    }

    /**
     * @return The number of slots (1 or two) that a value of the type described by {@code d} occupies on the operand
     *         stack or in the local variable array, or 0 iff {@code d} describes the type VOID
     */
    public static short
    size(String d) {
        if (d.equals(Descriptor.VOID)) return 0;
        if (Descriptor.hasSize1(d)) return 1;
        if (Descriptor.hasSize2(d)) return 2;
        throw new JaninoRuntimeException("No size defined for type \"" + Descriptor.toString(d) + "\"");
    }

    /** @return {@code true} iff {@code d} describes a primitive type except LONG and DOUBLE, or a reference type */
    public static boolean
    hasSize1(String d) {
        if (d.length() == 1) return "BCFISZ".indexOf(d) != -1;
        return Descriptor.isReference(d);
    }

    /** @return {@code true} iff {@code d} LONG or DOUBLE */
    public static boolean
    hasSize2(String d) {
        return d.equals(Descriptor.LONG) || d.equals(Descriptor.DOUBLE);
    }

    /**
     * Pretty-prints the given descriptor.
     *
     * @param d A valid field or method descriptor
     */
    public static String
    toString(String d) {
        int           idx = 0;
        StringBuilder sb  = new StringBuilder();
        if (d.charAt(0) == '(') {
            ++idx;
            sb.append("(");
            while (idx < d.length() && d.charAt(idx) != ')') {
                if (idx != 1) sb.append(", ");
                idx = Descriptor.toString(d, idx, sb);
            }
            if (idx >= d.length()) throw new JaninoRuntimeException("Invalid descriptor \"" + d + "\"");
            sb.append(") => ");
            ++idx;
        }
        Descriptor.toString(d, idx, sb);
        return sb.toString();
    }
    private static int
    toString(String d, int idx, StringBuilder sb) {
        int dimensions = 0;
        while (idx < d.length() && d.charAt(idx) == '[') {
            ++dimensions;
            ++idx;
        }
        if (idx >= d.length()) throw new JaninoRuntimeException("Invalid descriptor \"" + d + "\"");
        switch (d.charAt(idx)) {
        case 'L':
            {
                int idx2 = d.indexOf(';', idx);
                if (idx2 == -1) throw new JaninoRuntimeException("Invalid descriptor \"" + d + "\"");
                sb.append(d.substring(idx + 1, idx2).replace('/', '.'));
                idx = idx2;
            }
            break;
        case 'V':
            sb.append("void");
            break;
        case 'B':
            sb.append("byte");
            break;
        case 'C':
            sb.append("char");
            break;
        case 'D':
            sb.append("double");
            break;
        case 'F':
            sb.append("float");
            break;
        case 'I':
            sb.append("int");
            break;
        case 'J':
            sb.append("long");
            break;
        case 'S':
            sb.append("short");
            break;
        case 'Z':
            sb.append("boolean");
            break;
        default:
            throw new JaninoRuntimeException("Invalid descriptor \"" + d + "\"");
        }
        for (; dimensions > 0; --dimensions) sb.append("[]");
        return idx + 1;
    }

    /** Converts a class name as defined by "Class.getName()" into a descriptor. */
    public static String
    fromClassName(String className) {
        String res = (String) Descriptor.CLASS_NAME_TO_DESCRIPTOR.get(className);
        if (res != null) { return res; }
        if (className.startsWith("[")) return className.replace('.', '/');
        return 'L' + className.replace('.', '/') + ';';
    }

    /**
     * Convert a class name in the "internal form" as described in JVMS 4.2 into a descriptor.
     * <p>
     * Also implement the encoding of array types as described in JVMS 4.4.1.
     */
    public static String
    fromInternalForm(String internalForm) {
        if (internalForm.charAt(0) == '[') return internalForm;
        return 'L' + internalForm + ';';
    }

    /** Converts a field descriptor into a class name as defined by {@link Class#getName()}. */
    public static String
    toClassName(String d) {
        String res = (String) Descriptor.DESCRIPTOR_TO_CLASSNAME.get(d);
        if (res != null) { return res; }

        char firstChar = d.charAt(0);
        if (firstChar == 'L' && d.endsWith(";")) {
            // Class or interface -- convert "Ljava/lang/String;" to "java.lang.String".
            return d.substring(1, d.length() - 1).replace('/', '.');
        }
        if (firstChar == '[') {
            // Array type -- convert "[Ljava/lang/String;" to "[Ljava.lang.String;".
            return d.replace('/', '.');
        }
        throw new JaninoRuntimeException("(Invalid field descriptor \"" + d + "\")");
    }

    /** Converts a descriptor into the "internal form" as defined by JVMS 4.2. */
    public static String
    toInternalForm(String d) {
        if (d.charAt(0) != 'L') {
            throw new JaninoRuntimeException(
                "Attempt to convert non-class descriptor \""
                + d
                + "\" into internal form"
            );
        }
        return d.substring(1, d.length() - 1);
    }

    /** @return Whether {@code d} describes a primitive type or VOID */
    public static boolean
    isPrimitive(String d) { return d.length() == 1 && "VBCDFIJSZ".indexOf(d.charAt(0)) != -1; }

    /** @return Whether {@code d} describes a primitive type except BOOLEAN and VOID */
    public static boolean
    isPrimitiveNumeric(String d) { return d.length() == 1 && "BDFIJSC".indexOf(d.charAt(0)) != -1; }

    /**
     * Returns the package name of a class or interface reference descriptor,
     * or <code>null</code> if the class or interface is declared in the
     * default package.
     */
    public static String
    getPackageName(String d) {
        if (d.charAt(0) != 'L') {
            throw new JaninoRuntimeException("Attempt to get package name of non-class descriptor \"" + d + "\"");
        }
        int idx = d.lastIndexOf('/');
        return idx == -1 ? null : d.substring(1, idx).replace('/', '.');
    }

    /** Checks whether two reference types are declared in the same package. */
    public static boolean
    areInSamePackage(String d1, String d2) {
        String packageName1 = Descriptor.getPackageName(d1);
        String packageName2 = Descriptor.getPackageName(d2);
        return packageName1 == null ? packageName2 == null : packageName1.equals(packageName2);
    }

    /** The field descriptor for the type VOID. */
    public static final String VOID = "V";

    // Primitive types.

    /** The field descriptor for the primitive type BYTE. */
    public static final String BYTE = "B";
    /** The field descriptor for the primitive type CHAR. */
    public static final String CHAR = "C";
    /** The field descriptor for the primitive type DOUBLE. */
    public static final String DOUBLE = "D";
    /** The field descriptor for the primitive type FLOAT. */
    public static final String FLOAT = "F";
    /** The field descriptor for the primitive type INT. */
    public static final String INT = "I";
    /** The field descriptor for the primitive type LONG. */
    public static final String LONG = "J";
    /** The field descriptor for the primitive type SHORT. */
    public static final String SHORT = "S";
    /** The field descriptor for the primitive type BOOLEAN. */
    public static final String BOOLEAN = "Z";

    // Annotations.

    /** The field descriptor for the annotation {@link java.lang.Override}. */
    public static final String JAVA_LANG_OVERRIDE = "Ljava/lang/Override;";

    // Classes.

    /** The field descriptor for the class {@link java.lang.AssertionError}. */
    public static final String JAVA_LANG_ASSERTIONERROR = "Ljava/lang/AssertionError;";
    /** The field descriptor for the class {@link java.lang.Boolean}. */
    public static final String JAVA_LANG_BOOLEAN = "Ljava/lang/Boolean;";
    /** The field descriptor for the class {@link java.lang.Byte}. */
    public static final String JAVA_LANG_BYTE = "Ljava/lang/Byte;";
    /** The field descriptor for the class {@link java.lang.Character}. */
    public static final String JAVA_LANG_CHARACTER = "Ljava/lang/Character;";
    /** The field descriptor for the class {@link java.lang.Class}. */
    public static final String JAVA_LANG_CLASS = "Ljava/lang/Class;";
    /** The field descriptor for the class {@link java.lang.Double}. */
    public static final String JAVA_LANG_DOUBLE = "Ljava/lang/Double;";
    /** The field descriptor for the class {@link java.lang.Exception}. */
    public static final String JAVA_LANG_EXCEPTION = "Ljava/lang/Exception;";
    /** The field descriptor for the class {@link java.lang.Error}. */
    public static final String JAVA_LANG_ERROR = "Ljava/lang/Error;";
    /** The field descriptor for the class {@link java.lang.Float}. */
    public static final String JAVA_LANG_FLOAT = "Ljava/lang/Float;";
    /** The field descriptor for the class {@link java.lang.Integer}. */
    public static final String JAVA_LANG_INTEGER = "Ljava/lang/Integer;";
    /** The field descriptor for the class {@link java.lang.Long}. */
    public static final String JAVA_LANG_LONG = "Ljava/lang/Long;";
    /** The field descriptor for the class {@link java.lang.Object}. */
    public static final String JAVA_LANG_OBJECT = "Ljava/lang/Object;";
    /** The field descriptor for the class {@link java.lang.RuntimeException}. */
    public static final String JAVA_LANG_RUNTIMEEXCEPTION = "Ljava/lang/RuntimeException;";
    /** The field descriptor for the class {@link java.lang.Short}. */
    public static final String JAVA_LANG_SHORT = "Ljava/lang/Short;";
    /** The field descriptor for the class {@link java.lang.String}. */
    public static final String JAVA_LANG_STRING = "Ljava/lang/String;";
    /** The field descriptor for the class {@link java.lang.StringBuilder}. */
    public static final String JAVA_LANG_STRINGBUILDER = "Ljava/lang/StringBuilder;"; // Since 1.5!
    /** The field descriptor for the class {@link java.lang.Throwable}. */
    public static final String JAVA_LANG_THROWABLE = "Ljava/lang/Throwable;";

    // Interfaces.

    /** The field descriptor for the interface {@link java.io.Serializable}. */
    public static final String JAVA_IO_SERIALIZABLE = "Ljava/io/Serializable;";
    /** The field descriptor for the interface {@link java.lang.Cloneable}. */
    public static final String JAVA_LANG_CLONEABLE = "Ljava/lang/Cloneable;";
    /** The field descriptor for the interface {@link java.lang.Iterable}. */
    public static final String JAVA_LANG_ITERABLE = "Ljava/lang/Iterable;";
    /** The field descriptor for the interface {@link java.util.Iterator}. */
    public static final String JAVA_UTIL_ITERATOR = "Ljava/util/Iterator;";

    private static final Map<String, String> DESCRIPTOR_TO_CLASSNAME;
    static {
        Map<String, String> m = new HashMap();

        m.put(Descriptor.VOID, "void");

        // Primitive types.
        m.put(Descriptor.BYTE,    "byte");
        m.put(Descriptor.CHAR,    "char");
        m.put(Descriptor.DOUBLE,  "double");
        m.put(Descriptor.FLOAT,   "float");
        m.put(Descriptor.INT,     "int");
        m.put(Descriptor.LONG,    "long");
        m.put(Descriptor.SHORT,   "short");
        m.put(Descriptor.BOOLEAN, "boolean");

        // Annotations.
        m.put(Descriptor.JAVA_LANG_OVERRIDE, "java.lang.Override");

        // Classes.
        m.put(Descriptor.JAVA_LANG_ASSERTIONERROR,   "java.lang.AssertionError");
        m.put(Descriptor.JAVA_LANG_BOOLEAN,          "java.lang.Boolean");
        m.put(Descriptor.JAVA_LANG_BYTE,             "java.lang.Byte");
        m.put(Descriptor.JAVA_LANG_CHARACTER,        "java.lang.Character");
        m.put(Descriptor.JAVA_LANG_CLASS,            "java.lang.Class");
        m.put(Descriptor.JAVA_LANG_DOUBLE,           "java.lang.Double");
        m.put(Descriptor.JAVA_LANG_EXCEPTION,        "java.lang.Exception");
        m.put(Descriptor.JAVA_LANG_ERROR,            "java.lang.Error");
        m.put(Descriptor.JAVA_LANG_FLOAT,            "java.lang.Float");
        m.put(Descriptor.JAVA_LANG_INTEGER,          "java.lang.Integer");
        m.put(Descriptor.JAVA_LANG_LONG,             "java.lang.Long");
        m.put(Descriptor.JAVA_LANG_OBJECT,           "java.lang.Object");
        m.put(Descriptor.JAVA_LANG_RUNTIMEEXCEPTION, "java.lang.RuntimeException");
        m.put(Descriptor.JAVA_LANG_SHORT,            "java.lang.Short");
        m.put(Descriptor.JAVA_LANG_STRING,           "java.lang.String");
        m.put(Descriptor.JAVA_LANG_STRINGBUILDER,    "java.lang.StringBuilder");
        m.put(Descriptor.JAVA_LANG_THROWABLE,        "java.lang.Throwable");

        // Interfaces.
        m.put(Descriptor.JAVA_IO_SERIALIZABLE, "java.io.Serializable");
        m.put(Descriptor.JAVA_LANG_CLONEABLE,  "java.lang.Cloneable");
        m.put(Descriptor.JAVA_LANG_ITERABLE,   "java.lang.Iterable");
        m.put(Descriptor.JAVA_UTIL_ITERATOR,   "java.util.Iterator");

        DESCRIPTOR_TO_CLASSNAME = Collections.unmodifiableMap(m);
    }

    private static final Map<String, String> CLASS_NAME_TO_DESCRIPTOR;

    static {
        Map<String, String> m = new HashMap();
        for (Map.Entry<String, String> e : Descriptor.DESCRIPTOR_TO_CLASSNAME.entrySet()) {
            m.put(e.getValue(), e.getKey());
        }
        CLASS_NAME_TO_DESCRIPTOR = Collections.unmodifiableMap(m);
    }
}
