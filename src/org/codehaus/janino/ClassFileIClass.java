
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.util.ClassFile;
import org.codehaus.janino.util.ClassFile.ConstantClassInfo;

/** A wrapper object that turns a {@link ClassFile} object into an {@link IClass}. */
@SuppressWarnings({ "rawtypes", "unchecked" }) public
class ClassFileIClass extends IClass {
    private static final boolean DEBUG = false;

    private final ClassFile    classFile;
    private final IClassLoader iClassLoader;
    private final short        accessFlags;

    private final Map<ClassFile.FieldInfo, IField> resolvedFields = new HashMap();

    /**
     * @param classFile Source of data
     * @param iClassLoader {@link IClassLoader} through which to load other classes
     */
    public
    ClassFileIClass(ClassFile classFile, IClassLoader iClassLoader) {
        this.classFile    = classFile;
        this.iClassLoader = iClassLoader;

        // Determine class access flags.
        this.accessFlags = classFile.accessFlags;
    }

    // Implement IClass.

    @Override protected IConstructor[]
    getDeclaredIConstructors2() {
        List iConstructors = new ArrayList();

        for (ClassFile.MethodInfo mi : this.classFile.methodInfos) {
            IInvocable ii;
            try {
                ii = this.resolveMethod(mi);
            } catch (ClassNotFoundException ex) {
                throw new JaninoRuntimeException(ex.getMessage(), ex);
            }
            if (ii instanceof IConstructor) iConstructors.add(ii);
        }

        return (IConstructor[]) iConstructors.toArray(new IConstructor[iConstructors.size()]);
    }

    @Override protected IMethod[]
    getDeclaredIMethods2() {
        List<IMethod> iMethods = new ArrayList();

        for (ClassFile.MethodInfo mi : this.classFile.methodInfos) {

            // Skip JDK 1.5 synthetic methods (e.g. those generated for
            // covariant return values).
            if (Mod.isSynthetic(mi.getModifierFlags())) continue;

            IInvocable ii;
            try {
                ii = this.resolveMethod(mi);
            } catch (ClassNotFoundException ex) {
                throw new JaninoRuntimeException(ex.getMessage(), ex);
            }
            if (ii instanceof IMethod) iMethods.add((IMethod) ii);
        }

        return (IMethod[]) iMethods.toArray(new IMethod[iMethods.size()]);
    }

    @Override protected IField[]
    getDeclaredIFields2() {
        IField[] ifs = new IClass.IField[this.classFile.fieldInfos.size()];
        for (int i = 0; i < this.classFile.fieldInfos.size(); ++i) {
            try {
                ifs[i] = this.resolveField((ClassFile.FieldInfo) this.classFile.fieldInfos.get(i));
            } catch (ClassNotFoundException ex) {
                throw new JaninoRuntimeException(ex.getMessage(), ex);
            }
        }
        return ifs;
    }

    @Override protected IClass[]
    getDeclaredIClasses2() throws CompileException {
        ClassFile.InnerClassesAttribute ica = this.classFile.getInnerClassesAttribute();
        if (ica == null) return new IClass[0];

        List<IClass> res = new ArrayList();
        for (ClassFile.InnerClassesAttribute.Entry e : ica.getEntries()) {
            if (e.outerClassInfoIndex == this.classFile.thisClass) {
                try {
                    res.add(this.resolveClass(e.innerClassInfoIndex));
                } catch (ClassNotFoundException ex) {
                    throw new CompileException(ex.getMessage(), null); // SUPPRESS CHECKSTYLE AvoidHidingCause
                }
            }
        }
        return (IClass[]) res.toArray(new IClass[res.size()]);
    }

    @Override protected IClass
    getDeclaringIClass2() throws CompileException {
        ClassFile.InnerClassesAttribute ica = this.classFile.getInnerClassesAttribute();
        if (ica == null) return null;

        for (ClassFile.InnerClassesAttribute.Entry e : ica.getEntries()) {
            if (e.innerClassInfoIndex == this.classFile.thisClass) {
                // Is this an anonymous class?
                if (e.outerClassInfoIndex == 0) return null;
                try {
                    return this.resolveClass(e.outerClassInfoIndex);
                } catch (ClassNotFoundException ex) {
                    throw new CompileException(ex.getMessage(), null); // SUPPRESS CHECKSTYLE AvoidHidingCause
                }
            }
        }
        return null;
    }

    @Override protected IClass
    getOuterIClass2() throws CompileException {
        ClassFile.InnerClassesAttribute ica = this.classFile.getInnerClassesAttribute();
        if (ica == null) return null;

        for (ClassFile.InnerClassesAttribute.Entry e : ica.getEntries()) {
            if (e.innerClassInfoIndex == this.classFile.thisClass) {
                if (e.outerClassInfoIndex == 0) {

                    // Anonymous class or local class.
                    // TODO: Determine enclosing instance of anonymous class or local class
                    return null;
                } else  {

                    // Member type.
                    if (Mod.isStatic(e.innerClassAccessFlags)) return null;
                    try {
                        return this.resolveClass(e.outerClassInfoIndex);
                    } catch (ClassNotFoundException ex) {
                        throw new CompileException(ex.getMessage(), null); // SUPPRESS CHECKSTYLE AvoidHidingCause
                    }
                }
            }
        }
        return null;
    }

    @Override protected IClass
    getSuperclass2() throws CompileException {
        if (this.classFile.superclass == 0) return null;
        try {
            return this.resolveClass(this.classFile.superclass);
        } catch (ClassNotFoundException e) {
            throw new CompileException(e.getMessage(), null); // SUPPRESS CHECKSTYLE AvoidHidingCause
        }
    }

    @Override public Access
    getAccess() { return ClassFileIClass.accessFlags2Access(this.accessFlags); }

    @Override public boolean
    isFinal() { return Mod.isFinal(this.accessFlags); }

    @Override protected IClass[]
    getInterfaces2() throws CompileException { return this.resolveClasses(this.classFile.interfaces); }

    @Override public boolean
    isAbstract() { return Mod.isAbstract(this.accessFlags); }

    @Override protected String
    getDescriptor2() { return Descriptor.fromClassName(this.classFile.getThisClassName()); }

    @Override public boolean
    isInterface() { return Mod.isInterface(this.accessFlags); }

    @Override public boolean
    isArray() { return false; }

    @Override public boolean
    isPrimitive() { return false; }

    @Override public boolean
    isPrimitiveNumeric() { return false; }

    @Override protected IClass
    getComponentType2()  { return null; }

    /** Resolves all classes referenced by this class file. */
    public void
    resolveAllClasses() throws ClassNotFoundException {
        for (short i = 0; i < this.classFile.getConstantPoolSize(); ++i) {
            ClassFile.ConstantPoolInfo cpi = this.classFile.getConstantPoolInfo(i);
            if (cpi instanceof ClassFile.ConstantClassInfo) {
                this.resolveClass(i);
            } else
            if (cpi instanceof ClassFile.ConstantNameAndTypeInfo) {
                String descriptor = ((ClassFile.ConstantNameAndTypeInfo) cpi).getDescriptor(this.classFile);
                if (descriptor.charAt(0) == '(') {
                    MethodDescriptor md = new MethodDescriptor(descriptor);
                    this.resolveClass(md.returnFd);
                    for (String parameterFd : md.parameterFds) this.resolveClass(parameterFd);
                } else {
                    this.resolveClass(descriptor);
                }
            }
        }
    }

    /** @param index Index of the CONSTANT_Class_info to resolve (JVMS 4.4.1) */
    private IClass
    resolveClass(short index) throws ClassNotFoundException {
        if (ClassFileIClass.DEBUG) System.out.println("index=" + index);
        ConstantClassInfo cci = (ConstantClassInfo) this.classFile.getConstantPoolInfo(index);
        return this.resolveClass(Descriptor.fromInternalForm(cci.getName(this.classFile)));
    }

    private IClass
    resolveClass(String descriptor) throws ClassNotFoundException {
        if (ClassFileIClass.DEBUG) System.out.println("descriptor=" + descriptor);

        IClass result = (IClass) this.resolvedClasses.get(descriptor);
        if (result != null) return result;

        result = this.iClassLoader.loadIClass(descriptor);
        if (result == null) throw new ClassNotFoundException(descriptor);

        this.resolvedClasses.put(descriptor, result);
        return result;
    }
    private final Map<String /*descriptor*/, IClass> resolvedClasses = new HashMap();

    private IClass[]
    resolveClasses(short[] ifs) throws CompileException {
        IClass[] result = new IClass[ifs.length];
        for (int i = 0; i < result.length; ++i) {
            try {
                result[i] = this.resolveClass(ifs[i]);
            } catch (ClassNotFoundException e) {
                throw new CompileException(e.getMessage(), null); // SUPPRESS CHECKSTYLE AvoidHidingCause
            }
        }
        return result;
    }

    /**
     * Turn a {@link ClassFile.MethodInfo} into an {@link IInvocable}. This includes the checking and the
     * removal of the magic first parameter of an inner class constructor.
     *
     * @param methodInfo
     * @throws ClassNotFoundException
     */
    private IInvocable
    resolveMethod(final ClassFile.MethodInfo methodInfo) throws ClassNotFoundException {
        IInvocable result = (IInvocable) this.resolvedMethods.get(methodInfo);
        if (result != null) return result;

        // Determine method name.
        final String name = methodInfo.getName();

        // Determine return type.
        MethodDescriptor md = new MethodDescriptor(methodInfo.getDescriptor());

        final IClass returnType = this.resolveClass(md.returnFd);

        // Determine parameter types.
        final IClass[] parameterTypes = new IClass[md.parameterFds.length];
        for (int i = 0; i < parameterTypes.length; ++i) parameterTypes[i] = this.resolveClass(md.parameterFds[i]);

        // Determine thrown exceptions.
        IClass[]                  tes = null;
        ClassFile.AttributeInfo[] ais = methodInfo.getAttributes();
        for (ClassFile.AttributeInfo ai : ais) {
            if (ai instanceof ClassFile.ExceptionsAttribute) {
                ConstantClassInfo[] ccis = ((ClassFile.ExceptionsAttribute) ai).getExceptions(this.classFile);
                tes = new IClass[ccis.length];
                for (int i = 0; i < tes.length; ++i) {
                    tes[i] = this.resolveClass(Descriptor.fromInternalForm(ccis[i].getName(this.classFile)));
                }
            }
        }
        final IClass[] thrownExceptions = tes == null ? new IClass[0] : tes;

        // Determine access.
        final Access access = ClassFileIClass.accessFlags2Access(methodInfo.getModifierFlags());

        if ("<init>".equals(name)) {
            result = new IClass.IConstructor() {

                @Override public boolean
                isVarargs() { return Mod.isVarargs(methodInfo.getModifierFlags()); }

                @Override public IClass[]
                getParameterTypes2() throws CompileException {

                    // Process magic first parameter of inner class constructor.
                    IClass outerIClass = ClassFileIClass.this.getOuterIClass();
                    if (outerIClass != null) {
                        if (parameterTypes.length < 1) {
                            throw new JaninoRuntimeException("Inner class constructor lacks magic first parameter");
                        }
                        if (parameterTypes[0] != outerIClass) {
                            throw new JaninoRuntimeException(
                                "Magic first parameter of inner class constructor has type \""
                                + parameterTypes[0].toString()
                                + "\" instead of that of its enclosing instance (\""
                                + outerIClass.toString()
                                + "\")"
                            );
                        }
                        IClass[] tmp = new IClass[parameterTypes.length - 1];
                        System.arraycopy(parameterTypes, 1, tmp, 0, tmp.length);
                        return tmp;
                    }

                    return parameterTypes;
                }

                @Override public IClass[]          getThrownExceptions2() { return thrownExceptions; }
                @Override public Access            getAccess()            { return access; }
                @Override public Java.Annotation[] getAnnotations()       { return methodInfo.getAnnotations(); }
            };
        } else {
            result = new IClass.IMethod() {

                @Override public String
                getName() { return name; }

                @Override public IClass
                getReturnType() { return returnType; }

                @Override public boolean
                isStatic() { return Mod.isStatic(methodInfo.getModifierFlags()); }

                @Override public boolean
                isAbstract() { return Mod.isAbstract(methodInfo.getModifierFlags()); }

                @Override public boolean
                isVarargs() { return Mod.isVarargs(methodInfo.getModifierFlags()); }

                @Override public IClass[]
                getParameterTypes2() { return parameterTypes; }

                @Override public IClass[]
                getThrownExceptions2() { return thrownExceptions; }

                @Override public Access
                getAccess() { return access; }

                @Override public Java.Annotation[]
                getAnnotations() { return methodInfo.getAnnotations(); }
            };
        }
        this.resolvedMethods.put(methodInfo, result);
        return result;
    }
    private final Map<ClassFile.MethodInfo, IInvocable> resolvedMethods = new HashMap();

    private IField
    resolveField(final ClassFile.FieldInfo fieldInfo) throws ClassNotFoundException {
        IField result = (IField) this.resolvedFields.get(fieldInfo);
        if (result != null) return result;

        // Determine field name.
        final String name = fieldInfo.getName(this.classFile);

        // Determine field type.
        final String descriptor = fieldInfo.getDescriptor(this.classFile);
        final IClass type       = this.resolveClass(descriptor);

        // Determine optional "constant value" of the field (JLS7 15.28, bullet 14). If a field has a "ConstantValue"
        // attribute, we assume that it has a constant value. Notice that this assumption is not always correct,
        // because typical Java&trade; compilers do not generate a "ConstantValue" attribute for fields like
        // "int RED = 0", because "0" is the default value for an integer field.
        ClassFile.ConstantValueAttribute cva = null;
        for (ClassFile.AttributeInfo ai : fieldInfo.getAttributes()) {
            if (ai instanceof ClassFile.ConstantValueAttribute) {
                cva = (ClassFile.ConstantValueAttribute) ai;
                break;
            }
        }

        final Object optionalConstantValue = cva == null ? IClass.NOT_CONSTANT : cva.getConstantValue(this.classFile);
        final Access access                = ClassFileIClass.accessFlags2Access(fieldInfo.getModifierFlags());

        result = new IField() {
            @Override public Object            getConstantValue() { return optionalConstantValue; }
            @Override public String            getName()          { return name; }
            @Override public IClass            getType()          { return type; }
            @Override public boolean           isStatic()         { return Mod.isStatic(fieldInfo.getModifierFlags()); }
            @Override public Access            getAccess()        { return access; }
            @Override public Java.Annotation[] getAnnotations()   { return fieldInfo.getAnnotations(); }
        };
        this.resolvedFields.put(fieldInfo, result);
        return result;
    }

    private static Access
    accessFlags2Access(short accessFlags) {
        return (
            Mod.isPublicAccess(accessFlags)      ? Access.PUBLIC
            : Mod.isProtectedAccess(accessFlags) ? Access.PROTECTED
            : Mod.isPrivateAccess(accessFlags)   ? Access.PRIVATE
            : Access.DEFAULT
        );
    }
}
