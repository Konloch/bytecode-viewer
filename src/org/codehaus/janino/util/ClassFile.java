
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

package org.codehaus.janino.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UTFDataFormatException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.janino.Descriptor;
import org.codehaus.janino.JaninoRuntimeException;
import org.codehaus.janino.Java;
import org.codehaus.janino.Mod;

/**
 * An object that represents the Java&trade; "class file" format.
 * <p>
 * {@link #ClassFile(InputStream)} creates a {@link ClassFile} object from the bytecode
 * read from the given {@link InputStream}.
 * <p>
 * {@link #store(OutputStream)} generates Java&trade; bytecode
 * which is suitable for being processed by a Java&trade; virtual
 * machine.
 */
@SuppressWarnings({ "rawtypes", "unchecked" }) public
class ClassFile {

    /**
     * Construct from parsed components.
     * @param accessFlags as defined by {@link org.codehaus.janino.Mod}
     * @param thisClassFd the field descriptor for this class
     * @param superclassFd the field descriptor for the extended class (e.g. "Ljava/lang/Object;")
     * @param interfaceFds the field descriptors for the implemented interfaces
     */
    public
    ClassFile(short accessFlags, String thisClassFd, String superclassFd, String[] interfaceFds) {
        this.majorVersion  = ClassFile.MAJOR_VERSION_JDK_1_1;
        this.minorVersion  = ClassFile.MINOR_VERSION_JDK_1_1;

        this.constantPool  = new ArrayList();
        this.constantPool.add(null); // Add fake "0" index entry.
        this.constantPoolMap = new HashMap();

        this.accessFlags   = accessFlags;
        this.thisClass     = this.addConstantClassInfo(thisClassFd);
        this.superclass    = this.addConstantClassInfo(superclassFd);
        this.interfaces    = new short[interfaceFds.length];
        for (int i = 0; i < interfaceFds.length; ++i) {
            this.interfaces[i] = this.addConstantClassInfo(interfaceFds[i]);
        }

        this.fieldInfos    = new ArrayList();
        this.methodInfos   = new ArrayList();
        this.attributes    = new ArrayList();
    }

    /**
     * Adds a "SourceFile" attribute to this class file. (Does not check whether one exists already.)
     * @param sourceFileName
     */
    public void
    addSourceFileAttribute(String sourceFileName) {
        this.attributes.add(new SourceFileAttribute(
            this.addConstantUtf8Info("SourceFile"),  // attributeNameIndex
            this.addConstantUtf8Info(sourceFileName) // sourceFileIndex
        ));
    }

    /** Adds the "Deprecated" attribute to this class. */
    public void
    addDeprecatedAttribute() {
        this.attributes.add(new DeprecatedAttribute(this.addConstantUtf8Info("Deprecated")));
    }

    /**
     * Find the "InnerClasses" attribute of this class file
     * @return <code>null</code> if this class has no "InnerClasses" attribute
     */
    public InnerClassesAttribute
    getInnerClassesAttribute() {
        Short ni = (Short) this.constantPoolMap.get(new ConstantUtf8Info("InnerClasses"));
        if (ni == null) return null;

        for (Iterator<AttributeInfo> it = this.attributes.iterator(); it.hasNext();) {
            AttributeInfo ai = (AttributeInfo) it.next();
            if (ai.nameIndex == ni.shortValue() && ai instanceof InnerClassesAttribute) {
                return (InnerClassesAttribute) ai;
            }
        }
        return null;
    }

    /**
     * Create an "InnerClasses" attribute if it does not exist, then add the given entry
     * to the "InnerClasses" attribute.
     * @param e
     */
    public void
    addInnerClassesAttributeEntry(InnerClassesAttribute.Entry e) {
        InnerClassesAttribute ica = this.getInnerClassesAttribute();
        if (ica == null) {
            ica = new InnerClassesAttribute(this.addConstantUtf8Info("InnerClasses"));
            this.attributes.add(ica);
        }
        ica.getEntries().add(e);
        return;
    }

    /**
     * Read "class file" data from a {@link InputStream} and construct a {@link ClassFile} object from it.
     * <p>
     * If the {@link ClassFile} is created with this constructor, then most modifying operations lead to a {@link
     * UnsupportedOperationException}; only fields, methods and attributes can be added.
     */
    public
    ClassFile(InputStream inputStream) throws IOException {
        DataInputStream dis = (
            inputStream instanceof DataInputStream
            ? (DataInputStream) inputStream :
            new DataInputStream(inputStream)
        );

        int magic = dis.readInt();                           // magic
        if (magic != ClassFile.CLASS_FILE_MAGIC) throw new ClassFormatError("Invalid magic number");

        this.minorVersion = dis.readShort();                 // minor_version
        this.majorVersion = dis.readShort();                 // major_version

        // Explicitly DO NOT CHECK the major and minor version of the CLASS file, because SUN increase them with each
        // platform update while keeping them backwards compatible.

//        if (!ClassFile.isRecognizedVersion(this.majorVersion, this.minorVersion)) {
//            throw new ClassFormatError(
//                "Unrecognized class file format version "
//                + this.majorVersion
//                + "/"
//                + this.minorVersion
//            );
//        }

        this.constantPool    = new ArrayList();
        this.constantPoolMap = new HashMap();
        this.loadConstantPool(dis);                          // constant_pool_count, constant_pool

        this.accessFlags  = dis.readShort();                 // access_flags
        this.thisClass    = dis.readShort();                 // this_class
        this.superclass   = dis.readShort();                 // super_class
        this.interfaces   = ClassFile.readShortArray(dis);   // interfaces_count, interfaces

        this.fieldInfos  = Collections.unmodifiableList(this.loadFields(dis));     // fields_count, fields
        this.methodInfos = Collections.unmodifiableList(this.loadMethods(dis));    // methods_count, methods
        this.attributes  = Collections.unmodifiableList(this.loadAttributes(dis)); // attributes_count, attributes
    }

    /** @return The fully qualified name of this class, e.g. "pkg1.pkg2.Outer$Inner" */
    public String
    getThisClassName() {
        ConstantClassInfo cci = (ConstantClassInfo) this.getConstantPoolInfo(this.thisClass);
        return cci.getName(this).replace('/', '.');
    }

    /**
     * Sets the major and minor class file version numbers (JVMS 4.1). The class file version defaults to the JDK 1.1
     * values (45.3) which execute on virtually every JVM.
     */
    public void
    setVersion(short majorVersion, short minorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    /** @return The current major class file version number */
    public short
    getMajorVersion() { return this.majorVersion; }

    /** @return The current minor class file version number */
    public short
    getMinorVersion() { return this.minorVersion; }

    /**
     * Return the constant index number for a "CONSTANT_Class_info" structure to the class file. If the
     * class hasn't been added before, add it to the constant pool. Otherwise return the constant number for
     * that element of the pool.
     *
     * @see <a href="http://java.sun.com/docs/books/vmspec/2nd-edition/html/ClassFile.doc.html#1221">JVM specification,
     *      section 4.4.1</a>
     */
    public short
    addConstantClassInfo(String typeFd) {
        String s;
        if (Descriptor.isClassOrInterfaceReference(typeFd)) {
            s = Descriptor.toInternalForm(typeFd);
        } else
        if (Descriptor.isArrayReference(typeFd)) {
            s = typeFd;
        } else
        {
            throw new JaninoRuntimeException("\"" + Descriptor.toString(typeFd) + "\" is neither a class nor an array");
        }

        return this.addToConstantPool(new ConstantClassInfo(this.addConstantUtf8Info(s)));
    }

    /**
     * Add a "CONSTANT_Fieldref_info" structure to the class file.
     *
     * @see <a href="http://java.sun.com/docs/books/vmspec/2nd-edition/html/ClassFile.doc.html#42041">JVM specification,
     *      section 4.4.2</a>
     */
    public short
    addConstantFieldrefInfo(String classFd, String fieldName, String fieldFd) {
        return this.addToConstantPool(new ConstantFieldrefInfo(
            this.addConstantClassInfo(classFd),
            this.addConstantNameAndTypeInfo(fieldName, fieldFd)
        ));
    }

    /**
     * Add a "CONSTANT_Methodref_info" structure to the class file.
     *
     * @see <a href="http://java.sun.com/docs/books/vmspec/2nd-edition/html/ClassFile.doc.html#42041">JVM specification,
     *      section 4.4.2</a>
     */
    public short
    addConstantMethodrefInfo(String classFd, String methodName, String methodMd) {
        return this.addToConstantPool(new ConstantMethodrefInfo(
            this.addConstantClassInfo(classFd),
            this.addConstantNameAndTypeInfo(methodName, methodMd)
        ));
    }

    /**
     * Add a "CONSTANT_InterfaceMethodref_info" structure to the class file.
     *
     * @see <a href="http://java.sun.com/docs/books/vmspec/2nd-edition/html/ClassFile.doc.html#42041">JVM specification,
     *      section 4.4.2</a>
     */
    public short
    addConstantInterfaceMethodrefInfo(String classFd, String methodName, String methodMd) {
        return this.addToConstantPool(new ConstantInterfaceMethodrefInfo(
            this.addConstantClassInfo(classFd),
            this.addConstantNameAndTypeInfo(methodName, methodMd)
        ));
    }

    /**
     * Add a "CONSTANT_String_info" structure to the class file.
     *
     * @see <a href="http://java.sun.com/docs/books/vmspec/2nd-edition/html/ClassFile.doc.html#29297">JVM specification,
     *      section 4.4.3</a>
     */
    public short
    addConstantStringInfo(String string) {
        return this.addToConstantPool(new ConstantStringInfo(this.addConstantUtf8Info(string)));
    }

    /**
     * Add a "CONSTANT_Integer_info" structure to the class file.
     *
     * @see <a href="http://java.sun.com/docs/books/vmspec/2nd-edition/html/ClassFile.doc.html#21942">JVM specification,
     *      section 4.4.4</a>
     */
    public short
    addConstantIntegerInfo(final int value) {
        return this.addToConstantPool(new ConstantIntegerInfo(value));
    }

    /**
     * Add a "CONSTANT_Float_info" structure to the class file.
     *
     * @see <a href="http://java.sun.com/docs/books/vmspec/2nd-edition/html/ClassFile.doc.html#21942">JVM specification,
     *      section 4.4.4</a>
     */
    public short
    addConstantFloatInfo(final float value) {
        return this.addToConstantPool(new ConstantFloatInfo(value));
    }

    /**
     * Add a "CONSTANT_Long_info" structure to the class file.
     *
     * @see <a href="http://java.sun.com/docs/books/vmspec/2nd-edition/html/ClassFile.doc.html#1348">JVM specification,
     *      section 4.4.5</a>
     */
    public short
    addConstantLongInfo(final long value) {
        return this.addToConstantPool(new ConstantLongInfo(value));
    }

    /**
     * Add a "CONSTANT_Double_info" structure to the class file.
     *
     * @see <a href="http://java.sun.com/docs/books/vmspec/2nd-edition/html/ClassFile.doc.html#1348">JVM specification,
     *      section 4.4.5</a>
     */
    public short
    addConstantDoubleInfo(final double value) {
        return this.addToConstantPool(new ConstantDoubleInfo(value));
    }

    /**
     * Add a "CONSTANT_NameAndType_info" structure to the class file.
     *
     * @see <a href="http://java.sun.com/docs/books/vmspec/2nd-edition/html/ClassFile.doc.html#1327">JVM specification,
     *      section 4.4.6</a>
     */
    private short
    addConstantNameAndTypeInfo(String name, String descriptor) {
        return this.addToConstantPool(new ConstantNameAndTypeInfo(
            this.addConstantUtf8Info(name),
            this.addConstantUtf8Info(descriptor)
        ));
    }

    /**
     * Adds a "CONSTANT_Utf8_info" structure to the class file if no equal entry exists.
     *
     * @return The index of the already existing or newly created entry
     * @see    <a href="http://java.sun.com/docs/books/vmspec/2nd-edition/html/ClassFile.doc.html#7963">JVM
     *         specification, section 4.4.7</a>
     */
    public short
    addConstantUtf8Info(final String s) {
        return this.addToConstantPool(new ConstantUtf8Info(s));
    }

    /** Convenience method that adds a String, Integer, Float, Long or Double ConstantInfo. */
    private short
    addConstantSIFLDInfo(Object cv) { // SUPPRESS CHECKSTYLE AbbreviationAsWord
        if (cv instanceof String) {
            return this.addConstantStringInfo((String) cv);
        } else
        if (cv instanceof Byte || cv instanceof Short || cv instanceof Integer) {
            return this.addConstantIntegerInfo(((Number) cv).intValue());
        } else
        if (cv instanceof Boolean) {
            return this.addConstantIntegerInfo(((Boolean) cv).booleanValue() ? 1 : 0);
        } else
        if (cv instanceof Character) {
            return this.addConstantIntegerInfo(((Character) cv).charValue());
        } else
        if (cv instanceof Float) {
            return this.addConstantFloatInfo(((Float) cv).floatValue());
        } else
        if (cv instanceof Long) {
            return this.addConstantLongInfo(((Long) cv).longValue());
        } else
        if (cv instanceof Double) {
            return this.addConstantDoubleInfo(((Double) cv).doubleValue());
        } else
        {
            throw new JaninoRuntimeException("Unexpected constant value type \"" + cv.getClass().getName() + "\"");
        }
    }

    /**
     * Adds an entry to the constant pool and returns its index, or, if an equal entry already exists in the constant
     * pool, returns the index of that entry.
     */
    private short
    addToConstantPool(ConstantPoolInfo cpi) {
        Short index = (Short) this.constantPoolMap.get(cpi);
        if (index != null) return index.shortValue();

        int res = this.constantPool.size();
        if (res > 0xFFFF) {
            throw new JaninoRuntimeException("Constant pool has grown past JVM limit of 0xFFFF");
        }
        this.constantPool.add(cpi);
        if (cpi.isWide()) this.constantPool.add(null);

        this.constantPoolMap.put(cpi, new Short((short) res));
        return (short) res;
    }

    /**
     * Creates a {@link FieldInfo} and adds it to this class. The return value can be used e.g. to add attributes
     * ({@code Deprecated}, ...) to the field.
     */
    public FieldInfo
    addFieldInfo(
        Java.Modifiers modifiers,
        String         fieldName,
        String         fieldTypeFd,
        Object         optionalConstantValue
    ) {
        List<AttributeInfo> attributes = new ArrayList();
        if (optionalConstantValue != null) {
            attributes.add(new ConstantValueAttribute(
                this.addConstantUtf8Info("ConstantValue"),
                this.addConstantSIFLDInfo(optionalConstantValue)
            ));
        }
        FieldInfo fi = new FieldInfo(
            modifiers,                             // modifierAndAnnotations
            this.addConstantUtf8Info(fieldName),   // nameIndex
            this.addConstantUtf8Info(fieldTypeFd), // descriptorIndex
            attributes                             // attributes
        );
        this.fieldInfos.add(fi);
        return fi;
    }

    /**
     * Creates a {@link MethodInfo} and adds it to this class. The return value can be used e.g. to add attributes
     * ({@code Code}, {@code Deprecated}, {@code Exceptions}, ...) to the method.
     */
    public MethodInfo
    addMethodInfo(Java.Modifiers modifiers, String methodName, String methodMd) {
        MethodInfo mi = new MethodInfo(
            modifiers,                            // modifiers
            this.addConstantUtf8Info(methodName), // nameIndex
            this.addConstantUtf8Info(methodMd),   // desriptorIndex
            new ArrayList()                       // attributes
        );
        this.methodInfos.add(mi);
        return mi;
    }

    /** @return The (read-only) constant pool entry indexed by {@code index} */
    public ConstantPoolInfo
    getConstantPoolInfo(short index) { return (ConstantPoolInfo) this.constantPool.get(0xffff & index); }

    /** @return The size of the constant pool */
    public int
    getConstantPoolSize() { return this.constantPool.size(); }

    /**
     * @param index Index to a <code>CONSTANT_Utf8_info</code> in the constant pool
     * @return The string represented by the structure
     */
    public String
    getConstantUtf8(short index) {
        ConstantUtf8Info cui = (ConstantUtf8Info) this.getConstantPoolInfo(index);
        return cui.s;
    }

    /** u4 length, u1[length] */
    private static byte[]
    readLengthAndBytes(DataInputStream dis) throws IOException {
        byte[] ba = new byte[dis.readInt()];
        dis.readFully(ba);
        return ba;
    }

    /** u2 length, u2[length] */
    private static short[]
    readShortArray(DataInputStream dis) throws IOException {
        short   count  = dis.readShort();
        short[] result = new short[count];
        for (int i = 0; i < count; ++i) result[i] = dis.readShort();
        return result;
    }

    /** u2 constant_pool_count, constant_pool[constant_pool_count] */
    private void
    loadConstantPool(DataInputStream dis) throws IOException {
        this.constantPool.clear();
        this.constantPoolMap.clear();

        short constantPoolCount = dis.readShort(); // constant_pool_count
        this.constantPool.add(null);
        for (short i = 1; i < constantPoolCount; ++i) {
            ConstantPoolInfo cpi = ConstantPoolInfo.loadConstantPoolInfo(dis);
            this.constantPool.add(cpi);
            this.constantPoolMap.put(cpi, new Short(i));
            if (cpi.isWide()) {
                this.constantPool.add(null);
                ++i;
            }
        }
    }

    /** u2 fields_count, fields[fields_count] */
    private List<FieldInfo>
    loadFields(DataInputStream dis) throws IOException {
        short           fieldsCount = dis.readShort();
        List<FieldInfo> fields      = new ArrayList(fieldsCount);
        for (int i = 0; i < fieldsCount; ++i) {
            short               mods      = dis.readShort();
            short               nameIndex = dis.readShort();
            short               descIndex = dis.readShort();
            List<AttributeInfo> atts      = this.loadAttributes(dis);
            fields.add(new FieldInfo(
                ClassFile.buildModsAndAnns(mods, atts),
                nameIndex,
                descIndex,
                atts
            ));
        }
        return fields;
    }

    /**
     * Extract annotations from list of attributes
     * and build a ModifiersAndAnnotations object
     * @param mods
     * @param attributes
     */
    private static Java.Modifiers
    buildModsAndAnns(short mods, List attributes) {
        //TODO: extract the annotations
        return new Java.Modifiers(mods);
    }

    /** u2 methods_count, methods[methods_count] */
    private List<MethodInfo>
    loadMethods(DataInputStream dis) throws IOException {
        short            methodsCount = dis.readShort();
        List<MethodInfo> methods      = new ArrayList(methodsCount);
        for (int i = 0; i < methodsCount; ++i) methods.add(this.loadMethodInfo(dis));
        return methods;
    }

    /** u2 attributes_count, attributes[attributes_count] */
    private List<AttributeInfo>
    loadAttributes(DataInputStream dis) throws IOException {
        short               attributesCount = dis.readShort();
        List<AttributeInfo> attributes      = new ArrayList(attributesCount);
        for (int i = 0; i < attributesCount; ++i) attributes.add(this.loadAttribute(dis));
        return attributes;
    }

    /**
     * Write {@link ClassFile} to an {@link OutputStream}, in "class file" format.
     * <p>
     * Notice that if an {@link IOException} is thrown, the class file is
     * probably written incompletely and thus invalid. The calling method must take
     * care of this situation, e.g. by closing the output stream and then deleting the
     * file.
     * @param os
     * @throws IOException
     */
    public void
    store(OutputStream os) throws IOException {
        DataOutputStream dos = os instanceof DataOutputStream ? (DataOutputStream) os : new DataOutputStream(os);

        dos.writeInt(ClassFile.CLASS_FILE_MAGIC);            // magic
        dos.writeShort(this.minorVersion);                   // minor_version
        dos.writeShort(this.majorVersion);                   // major_version
        ClassFile.storeConstantPool(dos, this.constantPool); // constant_pool_count, constant_pool
        dos.writeShort(this.accessFlags);                    // access_flags
        dos.writeShort(this.thisClass);                      // this_class
        dos.writeShort(this.superclass);                     // super_class
        ClassFile.storeShortArray(dos, this.interfaces);     // interfaces_count, interfaces
        ClassFile.storeFields(dos, this.fieldInfos);         // fields_count, fields
        ClassFile.storeMethods(dos, this.methodInfos);       // methods_count, methods
        //TODO: need annotation(s) for classes?
        ClassFile.storeAttributes(dos, this.attributes, null); // attributes_count, attributes
    }

    /** u2 constant_pool_count, constant_pool[constant_pool_count - 1] */
    private static void
    storeConstantPool(DataOutputStream dos, List<ConstantPoolInfo> constantPool) throws IOException {
        dos.writeShort(constantPool.size());
        for (int i = 1; i < constantPool.size(); ++i) {
            ConstantPoolInfo cpi = (ConstantPoolInfo) constantPool.get(i);
            if (cpi == null) continue; // (Double or Long CPI.)
            cpi.store(dos);
        }
    }

    /** u2 count, u2[count] */
    private static void
    storeShortArray(DataOutputStream dos, short[] sa) throws IOException {
        dos.writeShort(sa.length);
        for (short s : sa) dos.writeShort(s);
    }

    /** u2 fields_count, fields[fields_count] */
    private static void
    storeFields(DataOutputStream dos, List<FieldInfo> fieldInfos) throws IOException {
        dos.writeShort(fieldInfos.size());
        for (FieldInfo fieldInfo : fieldInfos) fieldInfo.store(dos);
    }

    /** u2 methods_count, methods[methods_count] */
    private static void
    storeMethods(DataOutputStream dos, List<MethodInfo> methodInfos) throws IOException {
        dos.writeShort(methodInfos.size());
        for (MethodInfo methodInfo : methodInfos) methodInfo.store(dos);
    }

    /** u2 attributes_count, attributes[attributes_count] */
    private static void
    storeAttributes(DataOutputStream dos, List<AttributeInfo> attributeInfos, Java.Annotation[] annotations)
    throws IOException {
        //TODO: write annotation attributes
        dos.writeShort(attributeInfos.size());
        for (AttributeInfo attributeInfo : attributeInfos) attributeInfo.store(dos);
    }

    /**
     * Construct the name of a resource that could contain the source code of
     * the class with the given name.
     * <p>
     * Notice that member types are declared inside a different type, so the relevant source file
     * is that of the outermost declaring class.
     *
     * @param className Fully qualified class name, e.g. "pkg1.pkg2.Outer$Inner"
     * @return the name of the resource, e.g. "pkg1/pkg2/Outer.java"
     */
    public static String
    getSourceResourceName(String className) {

        // Strip nested type suffixes.
        {
            int idx = className.lastIndexOf('.') + 1;
            idx = className.indexOf('$', idx);
            if (idx != -1) className = className.substring(0, idx);
        }

        return className.replace('.', '/') + ".java";
    }

    /**
     * Construct the name of a resource that could contain the class file of the
     * class with the given name.
     *
     * @param className Fully qualified class name, e.g. "pkg1.pkg2.Outer$Inner"
     * @return the name of the resource, e.g. "pkg1/pkg2/Outer$Inner.class"
     */
    public static String
    getClassFileResourceName(String className) { return className.replace('.', '/') + ".class"; }

    /** Return the byte code of this {@link ClassFile} as a byte array. */
    public byte[]
    toByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            this.store(baos);
        } catch (IOException ex) {
            // ByteArrayOutputStream should never throw IOExceptions.
            throw new JaninoRuntimeException(ex.toString(), ex);
        }
        return baos.toByteArray();
    }

    private static final int CLASS_FILE_MAGIC = 0xcafebabe;

    // CHECKSTYLE JavadocVariable:OFF
    public static final short MAJOR_VERSION_JDK_1_1 = 45;
    public static final short MINOR_VERSION_JDK_1_1 = 3;
    public static final short MAJOR_VERSION_JDK_1_2 = 46;
    public static final short MINOR_VERSION_JDK_1_2 = 0;
    public static final short MAJOR_VERSION_JDK_1_3 = 47;
    public static final short MINOR_VERSION_JDK_1_3 = 0;
    public static final short MAJOR_VERSION_JDK_1_4 = 48;
    public static final short MINOR_VERSION_JDK_1_4 = 0;
    public static final short MAJOR_VERSION_JDK_1_5 = 49;
    public static final short MINOR_VERSION_JDK_1_5 = 0;
    // CHECKSTYLE JavadocVariable:ON

    private short                        majorVersion;
    private short                        minorVersion;
    private final List<ConstantPoolInfo> constantPool;

    /**
     * The access flags of the class.
     *
     * @see Mod#PUBLIC and consorts
     */
    public final short accessFlags;

    /** The constant pool index of the {@link ConstantClassInfo} that describes THIS class. */
    public final short thisClass;

    /**
     * The constant pool index of the {@link ConstantClassInfo} that describes the superclass of THIS class. Zero
     * for class {@link Object}, {@link Object} for interfaces.
     */
    public final short superclass;

    /**
     * The constant pool indexes of {@link ConstantClassInfo} which describes the interfaces that this class
     * implements, resp. that this interface extends.
     */
    public final short[] interfaces;

    /** The {@link FieldInfo}s of the field members of this class or interface. */
    public final List<FieldInfo> fieldInfos;

    /** The {@link MethodInfo}s of the methods of this class or interface. */
    public final List<MethodInfo> methodInfos;

    /** The {@link AttributeInfo}s of the attributes of this class or interface. */
    private final List<AttributeInfo> attributes;

    // Convenience.
    private final Map<ConstantPoolInfo, Short> constantPoolMap;

    /** Base for various the constant pool table entry types. */
    public abstract static
    class ConstantPoolInfo {

        /**
         * Stores this CP entry into the given {@link DataOutputStream}.
         * <p>
         * See JVMS7 4.4.1 and following
         */
        protected abstract void store(DataOutputStream dos) throws IOException;

        /** @return Whether this CP entry is "wide" in the sense of JVMS7 4.4.5 */
        protected abstract boolean isWide();

        private static ConstantPoolInfo
        loadConstantPoolInfo(DataInputStream dis) throws IOException {

            byte tag = dis.readByte();
//System.out.println("tag=" + tag);
            switch (tag) {

            case 7:
                return new ConstantClassInfo(dis.readShort());

            case 9:
                return new ConstantFieldrefInfo(dis.readShort(), dis.readShort());

            case 10:
                return new ConstantMethodrefInfo(dis.readShort(), dis.readShort());

            case 11:
                return new ConstantInterfaceMethodrefInfo(dis.readShort(), dis.readShort());

            case 8:
                return new ConstantStringInfo(dis.readShort());

            case 3:
                return new ConstantIntegerInfo(dis.readInt());

            case 4:
                return new ConstantFloatInfo(dis.readFloat());

            case 5:
                return new ConstantLongInfo(dis.readLong());

            case 6:
                return new ConstantDoubleInfo(dis.readDouble());

            case 12:
                return new ConstantNameAndTypeInfo(dis.readShort(), dis.readShort());

            case 1:
                return new ConstantUtf8Info(dis.readUTF());

            default:
                throw new ClassFormatError("Invalid constant pool tag " + tag);
            }
        }
    }

    /**
     * Intermediate base class for constant pool table entry types that have 'value' semantics: Double, Float,
     * Integer, Long, String
     */
    public abstract static
    class ConstantValuePoolInfo extends ConstantPoolInfo {

        /**
         * @return The value that this constant pool table entry represents; the actual type is {@link Double}, {@link
         *         Float}, {@link Integer}, {@link Long} or {@link String}
         */
        public abstract Object getValue(ClassFile classFile);
    }

    /** See JVMS7 4.4.1. */
    public static
    class ConstantClassInfo extends ConstantPoolInfo {
        private final short nameIndex;

        public ConstantClassInfo(short nameIndex) { this.nameIndex = nameIndex; }

        /** @return The class's or interface's name in "internal form" (JVMS7 4.2.1) */
        public String
        getName(ClassFile classFile) { return classFile.getConstantUtf8(this.nameIndex); }

        // Implement ConstantPoolInfo.

        @Override public boolean
        isWide() { return false; }

        @Override public void
        store(DataOutputStream dos) throws IOException {
            dos.writeByte(7);
            dos.writeShort(this.nameIndex);
        }

        @Override public boolean
        equals(Object o) {
            return o instanceof ConstantClassInfo && ((ConstantClassInfo) o).nameIndex == this.nameIndex;
        }

        @Override public int
        hashCode() { return this.nameIndex; }
    }

    /** See JVMS7 4.4.2. */
    public static
    class ConstantFieldrefInfo extends ConstantPoolInfo {

        private final short classIndex;
        private final short nameAndTypeIndex;

        public
        ConstantFieldrefInfo(short classIndex, short nameAndTypeIndex) {
            this.classIndex       = classIndex;
            this.nameAndTypeIndex = nameAndTypeIndex;
        }

        /** @return The {@link ConstantNameAndTypeInfo} of this {@link ConstantFieldrefInfo} */
        public ConstantNameAndTypeInfo
        getNameAndType(ClassFile classFile) {
            return (ClassFile.ConstantNameAndTypeInfo) classFile.getConstantPoolInfo(this.nameAndTypeIndex);
        }

        // Implement ConstantPoolInfo.

        @Override public boolean
        isWide() { return false; }

        @Override public void
        store(DataOutputStream dos) throws IOException {
            dos.writeByte(9);
            dos.writeShort(this.classIndex);
            dos.writeShort(this.nameAndTypeIndex);
        }

        @Override public boolean
        equals(Object o) {
            return (
                o instanceof ConstantFieldrefInfo
                && ((ConstantFieldrefInfo) o).classIndex       == this.classIndex
                && ((ConstantFieldrefInfo) o).nameAndTypeIndex == this.nameAndTypeIndex
            );
        }

        @Override public int
        hashCode() { return this.classIndex + (this.nameAndTypeIndex << 16); }
    }

    /** See JVMS7 4.4.2. */
    public static
    class ConstantMethodrefInfo extends ConstantPoolInfo {

        private final short classIndex;
        private final short nameAndTypeIndex;

        public
        ConstantMethodrefInfo(short classIndex, short nameAndTypeIndex) {
            this.classIndex       = classIndex;
            this.nameAndTypeIndex = nameAndTypeIndex;
        }

        /** @return The {@link ConstantNameAndTypeInfo} of this {@link ConstantMethodrefInfo} */
        public ConstantNameAndTypeInfo
        getNameAndType(ClassFile classFile) {
            return (ClassFile.ConstantNameAndTypeInfo) classFile.getConstantPoolInfo(this.nameAndTypeIndex);
        }

        // Implement ConstantPoolInfo.

        @Override public boolean
        isWide() { return false; }

        @Override public void
        store(DataOutputStream dos) throws IOException {
            dos.writeByte(10);
            dos.writeShort(this.classIndex);
            dos.writeShort(this.nameAndTypeIndex);
        }

        @Override public boolean
        equals(Object o) {
            return (
                o instanceof ConstantMethodrefInfo
                && ((ConstantMethodrefInfo) o).classIndex       == this.classIndex
                && ((ConstantMethodrefInfo) o).nameAndTypeIndex == this.nameAndTypeIndex
            );
        }

        @Override public int
        hashCode() { return this.classIndex + (this.nameAndTypeIndex << 16); }
    }

    /** See JVMS7 4.4.2. */
    public static
    class ConstantInterfaceMethodrefInfo extends ConstantPoolInfo {
        private final short classIndex;
        private final short nameAndTypeIndex;

        public
        ConstantInterfaceMethodrefInfo(short classIndex, short nameAndTypeIndex) {
            this.classIndex       = classIndex;
            this.nameAndTypeIndex = nameAndTypeIndex;
        }

        /** @return The {@link ConstantNameAndTypeInfo} of this {@link ConstantInterfaceMethodrefInfo} */
        public ConstantNameAndTypeInfo
        getNameAndType(ClassFile classFile) {
            return (ClassFile.ConstantNameAndTypeInfo) classFile.getConstantPoolInfo(this.nameAndTypeIndex);
        }

        // Implement ConstantPoolInfo.

        @Override public boolean
        isWide() { return false; }

        @Override public void
        store(DataOutputStream dos) throws IOException {
            dos.writeByte(11);
            dos.writeShort(this.classIndex);
            dos.writeShort(this.nameAndTypeIndex);
        }

        @Override public boolean
        equals(Object o) {
            return (
                o instanceof ConstantInterfaceMethodrefInfo
                && ((ConstantInterfaceMethodrefInfo) o).classIndex ==       this.classIndex
                && ((ConstantInterfaceMethodrefInfo) o).nameAndTypeIndex == this.nameAndTypeIndex
            );
        }

        @Override public int
        hashCode() { return this.classIndex + (this.nameAndTypeIndex << 16); }
    }

    /** See JVMS7 4.4.3. */
    static
    class ConstantStringInfo extends ConstantValuePoolInfo {
        private final short stringIndex;

        public
        ConstantStringInfo(short stringIndex) { this.stringIndex = stringIndex; }

        // Implement ConstantValuePoolInfo.
        @Override public Object
        getValue(ClassFile classFile) { return classFile.getConstantUtf8(this.stringIndex); }

        // Implement ConstantPoolInfo.

        @Override public boolean
        isWide() { return false; }

        @Override public void
        store(DataOutputStream dos) throws IOException {
            dos.writeByte(8);
            dos.writeShort(this.stringIndex);
        }

        @Override public boolean
        equals(Object o) {
            return o instanceof ConstantStringInfo && ((ConstantStringInfo) o).stringIndex == this.stringIndex;
        }

        @Override public int
        hashCode() { return this.stringIndex; }
    }

    /** See JVMS7 4.4.4. */
    private static
    class ConstantIntegerInfo extends ConstantValuePoolInfo {
        private final int value;

        public
        ConstantIntegerInfo(int value) { this.value = value; }

        // Implement ConstantValuePoolInfo.
        @Override public Object
        getValue(ClassFile classFile) { return new Integer(this.value); }

        // Implement ConstantPoolInfo.

        @Override public boolean
        isWide() { return false; }

        @Override public void
        store(DataOutputStream dos) throws IOException {
            dos.writeByte(3);
            dos.writeInt(this.value);
        }

        @Override public boolean
        equals(Object o) { return o instanceof ConstantIntegerInfo && ((ConstantIntegerInfo) o).value == this.value; }

        @Override public int
        hashCode() { return this.value; }
    }

    /** See JVMS7 4.4.4. */
    private static
    class ConstantFloatInfo extends ConstantValuePoolInfo {

        private final float value;

        public
        ConstantFloatInfo(float value) { this.value = value; }

        // Implement ConstantValuePoolInfo.
        @Override public Object getValue(ClassFile classFile) { return new Float(this.value); }

        // Implement ConstantPoolInfo.

        @Override public boolean
        isWide() { return false; }

        @Override public void
        store(DataOutputStream dos) throws IOException {
            dos.writeByte(4);
            dos.writeFloat(this.value);
        }

        @Override public boolean
        equals(Object o) {
            return o instanceof ConstantFloatInfo && ((ConstantFloatInfo) o).value == this.value;
        }

        @Override public int
        hashCode() { return Float.floatToIntBits(this.value); }
    }

    /** See JVMS7 4.4.5. */
    private static
    class ConstantLongInfo extends ConstantValuePoolInfo {

        private final long value;

        public
        ConstantLongInfo(long value) { this.value = value; }

        // Implement ConstantValuePoolInfo.
        @Override public Object getValue(ClassFile classFile) { return this.value; }

        // Implement ConstantPoolInfo.

        @Override public boolean
        isWide() { return true; }

        @Override public void
        store(DataOutputStream dos) throws IOException {
            dos.writeByte(5);
            dos.writeLong(this.value);
        }

        @Override public boolean
        equals(Object o) {
            return o instanceof ConstantLongInfo && ((ConstantLongInfo) o).value == this.value;
        }

        @Override public int
        hashCode() { return (int) this.value ^ (int) (this.value >> 32); }
    }

    /** See JVMS7 4.4.5. */
    private static
    class ConstantDoubleInfo extends ConstantValuePoolInfo {
        private final double value;

        public
        ConstantDoubleInfo(double value) { this.value = value; }

        // Implement ConstantValuePoolInfo.
        @Override public Object getValue(ClassFile classFile) { return new Double(this.value); }

        // Implement ConstantPoolInfo.

        @Override public boolean
        isWide() { return true; }

        @Override public void
        store(DataOutputStream dos) throws IOException {
            dos.writeByte(6);
            dos.writeDouble(this.value);
        }

        @Override public boolean
        equals(Object o) { return o instanceof ConstantDoubleInfo && ((ConstantDoubleInfo) o).value == this.value; }

        @Override public int
        hashCode() {
            long bits = Double.doubleToLongBits(this.value);
            return (int) bits ^ (int) (bits >> 32);
        }
    }

    /** See JVMS7 4.4.6. */
    public static
    class ConstantNameAndTypeInfo extends ConstantPoolInfo {

        private final short nameIndex;
        private final short descriptorIndex;

        public
        ConstantNameAndTypeInfo(short nameIndex, short descriptorIndex) {
            this.nameIndex       = nameIndex;
            this.descriptorIndex = descriptorIndex;
        }

        /** @return The (field or method) descriptor related to the name */
        public String
        getDescriptor(ClassFile classFile) {
            return classFile.getConstantUtf8(this.descriptorIndex);
        }

        // Implement ConstantPoolInfo.

        @Override public boolean
        isWide() { return false; }

        @Override public void
        store(DataOutputStream dos) throws IOException {
            dos.writeByte(12);
            dos.writeShort(this.nameIndex);
            dos.writeShort(this.descriptorIndex);
        }

        @Override public boolean
        equals(Object o) {
            return (
                o instanceof ConstantNameAndTypeInfo
                && ((ConstantNameAndTypeInfo) o).nameIndex       == this.nameIndex
                && ((ConstantNameAndTypeInfo) o).descriptorIndex == this.descriptorIndex
            );
        }

        @Override public int
        hashCode() { return this.nameIndex + (this.descriptorIndex << 16); }
    }

    /** See JVMS7 4.4.7. */
    public static
    class ConstantUtf8Info extends ConstantPoolInfo {
        private final String s;

        public
        ConstantUtf8Info(String s) {
            if (s == null) throw new JaninoRuntimeException();
            this.s = s;
        }

        /** @return The string contained in this {@link ConstantUtf8Info} */
        public String
        getString() { return this.s; }

        // Implement ConstantPoolInfo.

        @Override public boolean
        isWide() { return false; }

        @Override public void
        store(DataOutputStream dos) throws IOException {
            dos.writeByte(1);
            try {
                dos.writeUTF(this.s);
            } catch (UTFDataFormatException e) {
                // SUPPRESS CHECKSTYLE AvoidHidingCause
                throw new ClassFormatError("String constant too long to store in class file");
            }
        }

        @Override public boolean
        equals(Object o) {
            return o instanceof ConstantUtf8Info && ((ConstantUtf8Info) o).s.equals(this.s);
        }

        @Override public int
        hashCode() { return this.s.hashCode(); }
    }

    /** Representation of a "method_info" structure, as defined by JVMS7 4.6. */
    public
    class MethodInfo {

        private final Java.Modifiers      modifiers;
        private final short               nameIndex;
        private final short               descriptorIndex;
        private final List<AttributeInfo> attributes;

        /** Initializes the "method_info" structure. */
        public
        MethodInfo(
            Java.Modifiers      modifiers,
            short               nameIndex,
            short               descriptorIndex,
            List<AttributeInfo> attributes
        ) {
            this.modifiers       = modifiers;
            this.nameIndex       = nameIndex;
            this.descriptorIndex = descriptorIndex;
            this.attributes      = attributes;
        }

        /** @return The {@link ClassFile} that contains this {@link MethodInfo} object */
        public ClassFile
        getClassFile() { return ClassFile.this; }

        /** @return The modifier flags of this method; or'ed values are the constants declared in {@link Mod}. */
        public short getModifierFlags() { return this.modifiers.flags; }

        /** @return The annotations of this method */
        public Java.Annotation[] getAnnotations() { return this.modifiers.annotations; }

        /** @return The method's name */
        public String
        getName() { return ClassFile.this.getConstantUtf8(this.nameIndex); }

        /** @return The method descriptor describing this method */
        public String
        getDescriptor() { return ClassFile.this.getConstantUtf8(this.descriptorIndex); }

        /** @return The attributes of this method */
        public AttributeInfo[]
        getAttributes() {
            return (AttributeInfo[]) this.attributes.toArray(new AttributeInfo[this.attributes.size()]);
        }

        /** Adds the given {@code attribute} to this method. */
        public void
        addAttribute(AttributeInfo attribute) { this.attributes.add(attribute); }

        /** Writes this object to a {@link DataOutputStream}, in the format described inJVMS7 4.6. */
        public void
        store(DataOutputStream dos) throws IOException {
            dos.writeShort(this.modifiers.flags); // access_flags
            dos.writeShort(this.nameIndex);       // name_index
            dos.writeShort(this.descriptorIndex); // descriptor_index
            ClassFile.storeAttributes(            // attributes_count, attributes
                dos,
                this.attributes,
                this.modifiers.annotations
            );
        }
    }

    private MethodInfo
    loadMethodInfo(DataInputStream dis) throws IOException {
        short               mods       = dis.readShort();
        short               nameIndex  = dis.readShort();
        short               descIndex  = dis.readShort();
        List<AttributeInfo> attributes = this.loadAttributes(dis);
        return new MethodInfo(
            ClassFile.buildModsAndAnns(mods, attributes),
            nameIndex,
            descIndex,
            attributes
        );
    }

    /** Representation of a "method_info" structure, as defined by JVMS7 4.5. */
    public static
    class FieldInfo {

        public
        FieldInfo(
            Java.Modifiers      modifiers,
            short               nameIndex,
            short               descriptorIndex,
            List<AttributeInfo> attributes
        ) {
            this.modifiers       = modifiers;
            this.nameIndex       = nameIndex;
            this.descriptorIndex = descriptorIndex;
            this.attributes      = attributes;
        }

        /** @return The modifier flags of the field; or'ed values are the constants declared in {@link Mod} */
        public short getModifierFlags() { return this.modifiers.flags; }

        /** @return The annotations of this field */
        public Java.Annotation[] getAnnotations() { return this.modifiers.annotations; }

        /** @return The field's name */
        public String
        getName(ClassFile classFile) { return classFile.getConstantUtf8(this.nameIndex); }

        /** @return The field descriptor describing this field */
        public String
        getDescriptor(ClassFile classFile) { return classFile.getConstantUtf8(this.descriptorIndex); }

        /** @return The attributes of this field */
        public AttributeInfo[]
        getAttributes() {
            return (AttributeInfo[]) this.attributes.toArray(new AttributeInfo[this.attributes.size()]);
        }

        /** Adds the given {@code attribute} to this field. */
        public void
        addAttribute(AttributeInfo attribute) { this.attributes.add(attribute); }

        /** Writes this object to a {@link DataOutputStream}, in the format described inJVMS7 4.5. */
        public void
        store(DataOutputStream dos) throws IOException {
            dos.writeShort(this.modifiers.flags); // access_flags
            dos.writeShort(this.nameIndex);       // name_index
            dos.writeShort(this.descriptorIndex); // descriptor_index
            ClassFile.storeAttributes(            // attibutes_count, attributes
                dos,
                this.attributes,
                this.modifiers.annotations
            );
        }

        private final Java.Modifiers      modifiers;
        private final short               nameIndex;
        private final short               descriptorIndex;
        private final List<AttributeInfo> attributes;
    }

    /** Representation of a class file attribute (see JVMS7 4.7). */
    public abstract static
    class AttributeInfo {

        public
        AttributeInfo(short nameIndex) { this.nameIndex = nameIndex; }

        /** Writes this attribute to a {@link DataOutputStream}, in the format described in JVMS7 4.7. */
        public void
        store(DataOutputStream dos) throws IOException {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            this.storeBody(new DataOutputStream(baos));

            dos.writeShort(this.nameIndex); // attribute_name_index;
            dos.writeInt(baos.size());      // attribute_length
            baos.writeTo(dos);              // info
        }

        /** Writes the body of this attribute in an attribute-type dependent way; see JVMS7 4.7.2 and following. */
        protected abstract void
        storeBody(DataOutputStream dos) throws IOException;

        private final short nameIndex;
    }

    /**
     * Load one class file attribute. The returned object will be of
     * {@link AttributeInfo}-derived type, depending on the attribute's name; e.g. if the
     * name of the attribute is "SourceFile", then the returned object will be of type
     * {@link SourceFileAttribute}.
     */
    private AttributeInfo
    loadAttribute(DataInputStream dis) throws IOException {

        short attributeNameIndex = dis.readShort(); // attribute_name_index
        int   attributeLength    = dis.readInt();   // attribute_length

        final byte[] ba = new byte[attributeLength];
        dis.readFully(ba);
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream      bdis = new DataInputStream(bais);

        String        attributeName = this.getConstantUtf8(attributeNameIndex);
        AttributeInfo result;
        if ("ConstantValue".equals(attributeName)) {
            result = ConstantValueAttribute.loadBody(attributeNameIndex, bdis);
        } else
        if ("Code".equals(attributeName)) {
            result = CodeAttribute.loadBody(attributeNameIndex, this, bdis);
        } else
        if ("Exceptions".equals(attributeName)) {
            result = ExceptionsAttribute.loadBody(attributeNameIndex, bdis);
        } else
        if ("InnerClasses".equals(attributeName)) {
            result = InnerClassesAttribute.loadBody(attributeNameIndex, bdis);
        } else
        if ("Synthetic".equals(attributeName)) {
            result = SyntheticAttribute.loadBody(attributeNameIndex, bdis);
        } else
        if ("SourceFile".equals(attributeName)) {
            result = SourceFileAttribute.loadBody(attributeNameIndex, bdis);
        } else
        if ("LineNumberTable".equals(attributeName)) {
            result = LineNumberTableAttribute.loadBody(attributeNameIndex, bdis);
        } else
        if ("LocalVariableTable".equals(attributeName)) {
            result = LocalVariableTableAttribute.loadBody(attributeNameIndex, bdis);
        } else
        if ("Deprecated".equals(attributeName)) {
            result = DeprecatedAttribute.loadBody(attributeNameIndex, bdis);
        } else
        {
            return new AttributeInfo(attributeNameIndex) {
                @Override protected void storeBody(DataOutputStream dos) throws IOException { dos.write(ba); }
            };
        }

        if (bais.available() > 0) {
            throw new ClassFormatError(
                (ba.length - bais.available())
                + " bytes of trailing garbage in body of attribute \""
                + attributeName
                + "\""
            );
        }

        return result;
    }

    /** Representation of a "ConstantValue" attribute (see JVMS 4.7.2). */
    public static
    class ConstantValueAttribute extends AttributeInfo {

        private final short constantValueIndex;

        ConstantValueAttribute(short attributeNameIndex, short constantValueIndex) {
            super(attributeNameIndex);
            this.constantValueIndex = constantValueIndex;
        }

        /** @return The constant value contained in this attribute */
        public ConstantValuePoolInfo
        getConstantValue(ClassFile classFile) {
            return (ConstantValuePoolInfo) classFile.getConstantPoolInfo(this.constantValueIndex);
        }

        private static AttributeInfo
        loadBody(short attributeNameIndex, DataInputStream dis) throws IOException {
            return new ConstantValueAttribute(
                attributeNameIndex, // attributeNameIndex
                dis.readShort()     // constantValueIndex
            );
        }

        // Implement "AttributeInfo".
        @Override protected void
        storeBody(DataOutputStream dos) throws IOException {
            dos.writeShort(this.constantValueIndex);
        }
    }

    /** Representation of an "Exceptions" attribute (see JVMS 4.7.4). */
    public static
    class ExceptionsAttribute extends AttributeInfo {

        private final short[] exceptionIndexes;

        public
        ExceptionsAttribute(short attributeNameIndex, short[] exceptionIndexes) {
            super(attributeNameIndex);
            this.exceptionIndexes = exceptionIndexes;
        }

        /** @return The exception types contained in this {@link ExceptionsAttribute} */
        public ConstantClassInfo[]
        getExceptions(ClassFile classFile) {
            ConstantClassInfo[] es = new ConstantClassInfo[this.exceptionIndexes.length];
            for (int i = 0; i < es.length; i++) {
                es[i] = (ConstantClassInfo) classFile.getConstantPoolInfo(this.exceptionIndexes[i]);
            }
            return es;
        }

        private static AttributeInfo
        loadBody(short attributeNameIndex, DataInputStream dis) throws IOException {
            return new ExceptionsAttribute(
                attributeNameIndex,           // attributeNameIndex
                ClassFile.readShortArray(dis) // exceptionIndexes
            );
        }

        // Implement "AttributeInfo".
        @Override protected void
        storeBody(DataOutputStream dos) throws IOException {
            ClassFile.storeShortArray(dos, this.exceptionIndexes);
        }
    }

    /** Representation of an "InnerClasses" attribute (see JVMS 4.7.5). */
    public static
    class InnerClassesAttribute extends AttributeInfo {

        private final List<InnerClassesAttribute.Entry> entries;

        InnerClassesAttribute(short attributeNameIndex) {
            super(attributeNameIndex);
            this.entries = new ArrayList();
        }
        InnerClassesAttribute(short attributeNameIndex, Entry[] entries) {
            super(attributeNameIndex);
            this.entries = new ArrayList(Arrays.asList(entries));
        }

        /**
         * @return A {@code List<InnerClassesAttribute.Entry>}: The {@link Entry}s contained in this {@link
         *         InnerClassesAttribute}, see JVMS7 4.7.6
         */
        public List<InnerClassesAttribute.Entry>
        getEntries() { return this.entries; }

        private static AttributeInfo
        loadBody(short attributeNameIndex, DataInputStream dis) throws IOException {

            Entry[] ics = new Entry[dis.readShort()]; // number_of_classes
            for (short i = 0; i < ics.length; ++i) {  // classes
                ics[i] = new InnerClassesAttribute.Entry(
                    dis.readShort(), // innerClassInfoIndex
                    dis.readShort(), // outerClassInfoIndex
                    dis.readShort(), // innerNameIndex
                    dis.readShort()  // innerClassAccessFlags
                );
            }
            return new InnerClassesAttribute(attributeNameIndex, ics);
        }

        // Implement "AttributeInfo".
        @Override protected void
        storeBody(DataOutputStream dos) throws IOException {

            dos.writeShort(this.entries.size());
            for (InnerClassesAttribute.Entry e : this.entries) {
                dos.writeShort(e.innerClassInfoIndex);
                dos.writeShort(e.outerClassInfoIndex);
                dos.writeShort(e.innerNameIndex);
                dos.writeShort(e.innerClassAccessFlags);
            }
        }

        /** The structure of the {@code classes} array as described in JVMS7 4.7.6. */
        public static
        class Entry {

            /** The fields of the {@code classes} array as described in JVMS7 4.7.6. */
            public final short innerClassInfoIndex, outerClassInfoIndex, innerNameIndex, innerClassAccessFlags;

            public
            Entry(
                short innerClassInfoIndex,
                short outerClassInfoIndex,
                short innerNameIndex,
                short innerClassAccessFlags
            ) {
                this.innerClassInfoIndex   = innerClassInfoIndex;
                this.outerClassInfoIndex   = outerClassInfoIndex;
                this.innerNameIndex        = innerNameIndex;
                this.innerClassAccessFlags = innerClassAccessFlags;
            }
        }
    }

    /** Representation of a "Synthetic" attribute (see JVMS 4.7.6). */
    public static
    class SyntheticAttribute extends AttributeInfo {

        SyntheticAttribute(short attributeNameIndex) {
            super(attributeNameIndex);
        }

        private static AttributeInfo
        loadBody(short attributeNameIndex, DataInputStream dis) {
            return new SyntheticAttribute(attributeNameIndex);
        }

        // Implement "AttributeInfo".
        @Override protected void
        storeBody(DataOutputStream dos) {
            ;
        }
    }

    /** Representation of a "SourceFile" attribute (see JVMS 4.7.7). */
    public static
    class SourceFileAttribute extends AttributeInfo {

        private final short sourceFileIndex;

        public
        SourceFileAttribute(short attributeNameIndex, short sourceFileIndex) {
            super(attributeNameIndex);
            this.sourceFileIndex = sourceFileIndex;
        }

        private static AttributeInfo
        loadBody(short attributeNameIndex, DataInputStream dis) throws IOException {
            return new SourceFileAttribute(
                attributeNameIndex, // attributeNameIndex
                dis.readShort()     // sourceFileNameIndex
            );
        }

        // Implement "AttributeInfo".
        @Override protected void
        storeBody(DataOutputStream dos) throws IOException { dos.writeShort(this.sourceFileIndex); }
    }

    /** Representation of a "LineNumberTable" attribute (see JVMS 4.7.8). */
    public static
    class LineNumberTableAttribute extends AttributeInfo {

        private final Entry[] entries;

        public
        LineNumberTableAttribute(short attributeNameIndex, Entry[] entries) {
            super(attributeNameIndex);
            this.entries = entries;
        }

        private static AttributeInfo
        loadBody(short attributeNameIndex, DataInputStream dis) throws IOException {

            Entry[] lntes = new Entry[dis.readShort()]; // line_number_table_length
            for (short i = 0; i < lntes.length; ++i) {  // line_number_table
                lntes[i] = new LineNumberTableAttribute.Entry(
                    dis.readShort(), // startPC
                    dis.readShort()  // lineNumber
                );
            }
            return new LineNumberTableAttribute(attributeNameIndex, lntes);
        }

        // Implement "AttributeInfo".
        @Override protected void
        storeBody(DataOutputStream dos) throws IOException {
            dos.writeShort(this.entries.length);            // line_number_table_length
            for (Entry entry : this.entries) {
                dos.writeShort(entry.startPC);
                dos.writeShort(entry.lineNumber);
            }
        }

        /** The structure of the entries in the {@code line_number_table}, as described in JVMS7 4.7.12. */
        public static
        class Entry {

            /** The fields of the entries in the {@code line_number_table}, as described in JVMS7 4.7.12. */
            public final int startPC, lineNumber;

            public
            Entry(int startPc, int lineNumber) {
                this.startPC    = startPc;
                this.lineNumber = lineNumber;
            }
        }
    }

    /** Representation of a "LocalVariableTable" attribute (see JVMS 4.7.9). */
    public static
    class LocalVariableTableAttribute extends AttributeInfo {

        private final Entry[] entries;

        public
        LocalVariableTableAttribute(short attributeNameIndex, Entry[] entries) {
            super(attributeNameIndex);
            this.entries = entries;
        }

        private static AttributeInfo
        loadBody(short attributeNameIndex, DataInputStream dis) throws IOException {
            short   localVariableTableLength = dis.readShort(); // local_variable_table_length
            Entry[] lvtes                    = new Entry[localVariableTableLength];
            for (short i = 0; i < localVariableTableLength; ++i) { // local_variable_table
                lvtes[i] = new LocalVariableTableAttribute.Entry(
                    dis.readShort(), // startPC
                    dis.readShort(), // length
                    dis.readShort(), // nameIndex
                    dis.readShort(), // descriptorIndex
                    dis.readShort()  // index
                );
            }
            return new LocalVariableTableAttribute(attributeNameIndex, lvtes);
        }

        // Implement "AttributeInfo".

        @Override protected void
        storeBody(DataOutputStream dos) throws IOException {
            dos.writeShort(this.entries.length); // local_variable_table_length
            for (Entry lnte : this.entries) {    // local_variable_table
                dos.writeShort(lnte.startPC);         // start_pc;
                dos.writeShort(lnte.length);          // length
                dos.writeShort(lnte.nameIndex);       // name_index
                dos.writeShort(lnte.descriptorIndex); // descriptor_index
                dos.writeShort(lnte.index);           // index
            }
        }

        /** The structure of the entries in the {@code local_variable_table}, as described in JVMS7 4.7.13. */
        public static
        class Entry {

            /** The fields of the entries in the {@code local_variable_table}, as described in JVMS7 4.7.13. */
            public final short startPC, length, nameIndex, descriptorIndex, index;

            public
            Entry(short startPc, short length, short nameIndex, short descriptorIndex, short index) {
                this.startPC         = startPc;
                this.length          = length;
                this.nameIndex       = nameIndex;
                this.descriptorIndex = descriptorIndex;
                this.index           = index;
            }
        }
    }

    /** Representation of a "Deprecated" attribute (see JVMS 4.7.10). */
    public static
    class DeprecatedAttribute extends AttributeInfo {

        public
        DeprecatedAttribute(short attributeNameIndex) { super(attributeNameIndex); }

        private static AttributeInfo
        loadBody(short attributeNameIndex, DataInputStream dis) {
            return new DeprecatedAttribute(attributeNameIndex);
        }

        // Implement "AttributeInfo".
        @Override protected void
        storeBody(DataOutputStream dos) {
            ;
        }
    }

    /** Representation of an unmodifiable "Code" attribute, as read from a class file. */
    private static
    class CodeAttribute extends AttributeInfo {

        private final short                 maxStack;
        private final short                 maxLocals;
        private final byte[]                code;
        private final ExceptionTableEntry[] exceptionTableEntries;
        private final AttributeInfo[]       attributes;

        CodeAttribute(
            short                 attributeNameIndex,
            short                 maxStack,
            short                 maxLocals,
            byte[]                code,
            ExceptionTableEntry[] exceptionTableEntries,
            AttributeInfo[]       attributes
        ) {
            super(attributeNameIndex);
            this.maxStack              = maxStack;
            this.maxLocals             = maxLocals;
            this.code                  = code;
            this.exceptionTableEntries = exceptionTableEntries;
            this.attributes            = attributes;
        }

        public static AttributeInfo
        loadBody(short attributeNameIndex, ClassFile classFile, DataInputStream dis) throws IOException {
            final short  maxStack  = dis.readShort();                   // max_stack
            final short  maxLocals = dis.readShort();                   // max_locals
            final byte[] code      = ClassFile.readLengthAndBytes(dis); // code_length, code

            ExceptionTableEntry[] etes = new ExceptionTableEntry[dis.readShort()]; // exception_table_length
            for (int i = 0; i < etes.length; ++i) {                                // exception_table
                etes[i] = new ExceptionTableEntry(
                    dis.readShort(), // startPC
                    dis.readShort(), // endPC
                    dis.readShort(), // handlerPC
                    dis.readShort()  // catchType
                );
            }

            AttributeInfo[] attributes = new AttributeInfo[dis.readShort()];       // attributes_count
            for (int i = 0; i < attributes.length; ++i) {                          // attributes
                attributes[i] = classFile.loadAttribute(dis);
            }

            return new CodeAttribute(
                attributeNameIndex, // attributeNameIndex
                maxStack,           // maxStack
                maxLocals,          // maxLocals
                code,               // code
                etes,               // exceptionTableEntries
                attributes          // attributes
            );
        }

        @Override protected void
        storeBody(DataOutputStream dos) throws IOException {
            dos.writeShort(this.maxStack);                               // max_stack
            dos.writeShort(this.maxLocals);                              // max_locals
            dos.writeInt(this.code.length);                              // code_length
            dos.write(this.code);                                        // code
            dos.writeShort(this.exceptionTableEntries.length);           // exception_table_length
            for (ExceptionTableEntry ete : this.exceptionTableEntries) { // exception_table
                dos.writeShort(ete.startPc);   // start_pc
                dos.writeShort(ete.endPc);     // end_pc
                dos.writeShort(ete.handlerPc); // handler_pc
                dos.writeShort(ete.catchType); // catch_type
            }
            dos.writeShort(this.attributes.length);                      // attributes_count
            for (AttributeInfo ai : this.attributes) ai.store(dos);      // attributes
        }

        /** Representation of an entry in the "exception_table" of a "Code" attribute (see JVMS 4.7.3). */
        private static
        class ExceptionTableEntry {

            final short startPc, endPc, handlerPc, catchType;

            public
            ExceptionTableEntry(short startPc, short endPc, short handlerPc, short catchType) {
                this.startPc   = startPc;
                this.endPc     = endPc;
                this.handlerPc = handlerPc;
                this.catchType = catchType;
            }
        }
    }
}
