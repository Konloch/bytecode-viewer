
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

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.janino.IClass.IConstructor;
import org.codehaus.janino.IClass.IMethod;
import org.codehaus.janino.util.resource.JarDirectoriesResourceFinder;
import org.codehaus.janino.util.resource.PathResourceFinder;
import org.codehaus.janino.util.resource.ResourceFinder;

/** Loads an {@link IClass} by type name. */
@SuppressWarnings({ "rawtypes", "unchecked" }) public abstract
class IClassLoader {
    private static final boolean DEBUG = false;

    // The following are constants, but cannot be declared FINAL, because they are only initialized by
    // "postConstruct()".

    // CHECKSTYLE MemberName:OFF
    // CHECKSTYLE AbbreviationAsWordInName:OFF
    /** Representation of the {@link java.lang.Override} annotation. */
    public IClass ANNO_java_lang_Override;

    /** Representation of the {@link java.lang.AssertionError} type. */
    public IClass TYPE_java_lang_AssertionError;
    /** Representation of the {@link java.lang.Boolean} type. */
    public IClass TYPE_java_lang_Boolean;
    /** Representation of the {@link java.lang.Byte} type. */
    public IClass TYPE_java_lang_Byte;
    /** Representation of the {@link java.lang.Character} type. */
    public IClass TYPE_java_lang_Character;
    /** Representation of the {@link java.lang.Class} type. */
    public IClass TYPE_java_lang_Class;
    /** Representation of the {@link java.lang.Cloneable} type. */
    public IClass TYPE_java_lang_Cloneable;
    /** Representation of the {@link java.lang.Double} type. */
    public IClass TYPE_java_lang_Double;
    /** Representation of the {@link java.lang.Exception} type. */
    public IClass TYPE_java_lang_Exception;
    /** Representation of the {@link java.lang.Error} type. */
    public IClass TYPE_java_lang_Error;
    /** Representation of the {@link java.lang.Float} type. */
    public IClass TYPE_java_lang_Float;
    /** Representation of the {@link java.lang.Integer} type. */
    public IClass TYPE_java_lang_Integer;
    /** Representation of the {@link java.lang.Iterable} type. */
    public IClass TYPE_java_lang_Iterable;
    /** Representation of the {@link java.lang.Long} type. */
    public IClass TYPE_java_lang_Long;
    /** Representation of the {@link java.lang.Object} type. */
    public IClass TYPE_java_lang_Object;
    /** Representation of the {@link java.lang.RuntimeException} type. */
    public IClass TYPE_java_lang_RuntimeException;
    /** Representation of the {@link java.lang.Short} type. */
    public IClass TYPE_java_lang_Short;
    /** Representation of the {@link java.lang.String} type. */
    public IClass TYPE_java_lang_String;
    /** Representation of the {@link java.lang.StringBuilder} type. */
    public IClass TYPE_java_lang_StringBuilder;
    /** Representation of the {@link java.lang.Throwable} type. */
    public IClass TYPE_java_lang_Throwable;
    /** Representation of the {@link java.io.Serializable} type. */
    public IClass TYPE_java_io_Serializable;
    /** Representation of the {@link java.util.Iterator} type. */
    public IClass TYPE_java_util_Iterator;

    /** Representation of the {@link Iterable#iterator()} method. */
    public IMethod METH_java_lang_Iterable__iterator;
    /** Representation of the {@link String#concat(String)} method. */
    public IMethod METH_java_lang_String__concat__java_lang_String;
    /** Representation of the {@link String#valueOf(int)} method. */
    public IMethod METH_java_lang_String__valueOf__int;
    /** Representation of the {@link String#valueOf(long)} method. */
    public IMethod METH_java_lang_String__valueOf__long;
    /** Representation of the {@link String#valueOf(float)} method. */
    public IMethod METH_java_lang_String__valueOf__float;
    /** Representation of the {@link String#valueOf(double)} method. */
    public IMethod METH_java_lang_String__valueOf__double;
    /** Representation of the {@link String#valueOf(char)} method. */
    public IMethod METH_java_lang_String__valueOf__char;
    /** Representation of the {@link String#valueOf(boolean)} method. */
    public IMethod METH_java_lang_String__valueOf__boolean;
    /** Representation of the {@link String#valueOf(Object)} method. */
    public IMethod METH_java_lang_String__valueOf__java_lang_Object;
    /** Representation of the {@link StringBuilder#append(String)} method. */
    public IMethod METH_java_lang_StringBuilder__append__java_lang_String;
    /** Representation of the {@link StringBuilder#toString()} method. */
    public IMethod METH_java_lang_StringBuilder__toString;
    /** Representation of the {@link java.util.Iterator#hasNext()} method. */
    public IMethod METH_java_util_Iterator__hasNext;
    /** Representation of the {@link java.util.Iterator#next()} method. */
    public IMethod METH_java_util_Iterator__next;

    /** Representation of the {@link StringBuilder#StringBuilder(String)} constructor. */
    public IConstructor CTOR_java_lang_StringBuilder__java_lang_String;
    // CHECKSTYLE AbbreviationAsWordInName:ON
    // CHECKSTYLE MemberName:ON

    public
    IClassLoader(IClassLoader optionalParentIClassLoader) {
        this.optionalParentIClassLoader = optionalParentIClassLoader;
    }

    /**
     * This method must be called by the constructor of the directly derived
     * class. (The reason being is that this method invokes abstract
     * {@link #loadIClass(String)} which will not work until the implementing
     * class is constructed.)
     */
    protected final void
    postConstruct() {
        try {
            this.ANNO_java_lang_Override = this.loadIClass(Descriptor.JAVA_LANG_OVERRIDE);

            this.TYPE_java_lang_AssertionError   = this.loadIClass(Descriptor.JAVA_LANG_ASSERTIONERROR);
            this.TYPE_java_lang_Boolean          = this.loadIClass(Descriptor.JAVA_LANG_BOOLEAN);
            this.TYPE_java_lang_Byte             = this.loadIClass(Descriptor.JAVA_LANG_BYTE);
            this.TYPE_java_lang_Character        = this.loadIClass(Descriptor.JAVA_LANG_CHARACTER);
            this.TYPE_java_lang_Class            = this.loadIClass(Descriptor.JAVA_LANG_CLASS);
            this.TYPE_java_lang_Cloneable        = this.loadIClass(Descriptor.JAVA_LANG_CLONEABLE);
            this.TYPE_java_lang_Double           = this.loadIClass(Descriptor.JAVA_LANG_DOUBLE);
            this.TYPE_java_lang_Exception        = this.loadIClass(Descriptor.JAVA_LANG_EXCEPTION);
            this.TYPE_java_lang_Error            = this.loadIClass(Descriptor.JAVA_LANG_ERROR);
            this.TYPE_java_lang_Float            = this.loadIClass(Descriptor.JAVA_LANG_FLOAT);
            this.TYPE_java_lang_Integer          = this.loadIClass(Descriptor.JAVA_LANG_INTEGER);
            this.TYPE_java_lang_Iterable         = this.loadIClass(Descriptor.JAVA_LANG_ITERABLE);
            this.TYPE_java_lang_Long             = this.loadIClass(Descriptor.JAVA_LANG_LONG);
            this.TYPE_java_lang_Object           = this.loadIClass(Descriptor.JAVA_LANG_OBJECT);
            this.TYPE_java_lang_RuntimeException = this.loadIClass(Descriptor.JAVA_LANG_RUNTIMEEXCEPTION);
            this.TYPE_java_lang_Short            = this.loadIClass(Descriptor.JAVA_LANG_SHORT);
            this.TYPE_java_lang_String           = this.loadIClass(Descriptor.JAVA_LANG_STRING);
            this.TYPE_java_lang_StringBuilder    = this.loadIClass(Descriptor.JAVA_LANG_STRINGBUILDER);
            this.TYPE_java_lang_Throwable        = this.loadIClass(Descriptor.JAVA_LANG_THROWABLE);
            this.TYPE_java_io_Serializable       = this.loadIClass(Descriptor.JAVA_IO_SERIALIZABLE);
            this.TYPE_java_util_Iterator         = this.loadIClass(Descriptor.JAVA_UTIL_ITERATOR);

            // CHECKSTYLE LineLength:OFF
            // CHECKSTYLE Whitespace:OFF
            this.METH_java_lang_Iterable__iterator                      = this.TYPE_java_lang_Iterable     .findIMethod("iterator", new IClass[0]);
            this.METH_java_lang_String__concat__java_lang_String        = this.TYPE_java_lang_String       .findIMethod("concat",   new IClass[] { this.TYPE_java_lang_String });
            this.METH_java_lang_String__valueOf__int                    = this.TYPE_java_lang_String       .findIMethod("valueOf",  new IClass[] { IClass.INT });
            this.METH_java_lang_String__valueOf__long                   = this.TYPE_java_lang_String       .findIMethod("valueOf",  new IClass[] { IClass.LONG });
            this.METH_java_lang_String__valueOf__float                  = this.TYPE_java_lang_String       .findIMethod("valueOf",  new IClass[] { IClass.FLOAT });
            this.METH_java_lang_String__valueOf__double                 = this.TYPE_java_lang_String       .findIMethod("valueOf",  new IClass[] { IClass.DOUBLE });
            this.METH_java_lang_String__valueOf__char                   = this.TYPE_java_lang_String       .findIMethod("valueOf",  new IClass[] { IClass.CHAR });
            this.METH_java_lang_String__valueOf__boolean                = this.TYPE_java_lang_String       .findIMethod("valueOf",  new IClass[] { IClass.BOOLEAN });
            this.METH_java_lang_String__valueOf__java_lang_Object       = this.TYPE_java_lang_String       .findIMethod("valueOf",  new IClass[] { this.TYPE_java_lang_Object });
            this.METH_java_lang_StringBuilder__append__java_lang_String = this.TYPE_java_lang_StringBuilder.findIMethod("append",   new IClass[] { this.TYPE_java_lang_String });
            this.METH_java_lang_StringBuilder__toString                 = this.TYPE_java_lang_StringBuilder.findIMethod("toString", new IClass[0]);
            this.METH_java_util_Iterator__hasNext                       = this.TYPE_java_util_Iterator     .findIMethod("hasNext",  new IClass[0]);
            this.METH_java_util_Iterator__next                          = this.TYPE_java_util_Iterator     .findIMethod("next",     new IClass[0]);

            this.CTOR_java_lang_StringBuilder__java_lang_String   = this.TYPE_java_lang_StringBuilder.findIConstructor(new IClass[] { this.TYPE_java_lang_String });
            // CHECKSTYLE Whitespace:ON
            // CHECKSTYLE LineLength:ON
        } catch (Exception e) {
            throw new JaninoRuntimeException("Cannot load simple types", e);
        }
    }

    /**
     * Get an {@link IClass} by field descriptor.
     *
     * @param fieldDescriptor         E.g. 'Lpkg1/pkg2/Outer$Inner;'
     * @return                        {@code null} if an {@link IClass} could not be loaded
     * @throws ClassNotFoundException An exception was raised while loading the {@link IClass}
     */
    public final IClass
    loadIClass(String fieldDescriptor) throws ClassNotFoundException {
        if (IClassLoader.DEBUG) System.out.println(this + ": Load type \"" + fieldDescriptor + "\"");

        if (Descriptor.isPrimitive(fieldDescriptor)) {
            return (
                fieldDescriptor.equals(Descriptor.VOID)    ? IClass.VOID :
                fieldDescriptor.equals(Descriptor.BYTE)    ? IClass.BYTE :
                fieldDescriptor.equals(Descriptor.CHAR)    ? IClass.CHAR :
                fieldDescriptor.equals(Descriptor.DOUBLE)  ? IClass.DOUBLE :
                fieldDescriptor.equals(Descriptor.FLOAT)   ? IClass.FLOAT :
                fieldDescriptor.equals(Descriptor.INT)     ? IClass.INT :
                fieldDescriptor.equals(Descriptor.LONG)    ? IClass.LONG :
                fieldDescriptor.equals(Descriptor.SHORT)   ? IClass.SHORT :
                fieldDescriptor.equals(Descriptor.BOOLEAN) ? IClass.BOOLEAN :
                null
            );
        }

        // Ask parent IClassLoader first.
        if (this.optionalParentIClassLoader != null) {
            IClass res = this.optionalParentIClassLoader.loadIClass(fieldDescriptor);
            if (res != null) return res;
        }

        // We need to synchronize here because "unloadableIClasses" and
        // "loadedIClasses" are unsynchronized containers.
        IClass result;
        synchronized (this) {

            // Class could not be loaded before?
            if (this.unloadableIClasses.contains(fieldDescriptor)) return null;

            // Class already loaded?
            result = (IClass) this.loadedIClasses.get(fieldDescriptor);
            if (result != null) return result;

            // Special handling for array types.
            if (Descriptor.isArrayReference(fieldDescriptor)) {

                // Load the component type.
                IClass componentIClass = this.loadIClass(
                    Descriptor.getComponentDescriptor(fieldDescriptor)
                );
                if (componentIClass == null) return null;

                // Now get and define the array type.
                IClass arrayIClass = componentIClass.getArrayIClass(this.TYPE_java_lang_Object);
                this.loadedIClasses.put(fieldDescriptor, arrayIClass);
                return arrayIClass;
            }

            if (IClassLoader.DEBUG) System.out.println("call IClassLoader.findIClass(\"" + fieldDescriptor + "\")");

            // Load the class through the {@link #findIClass(String)} method implemented by the
            // derived class.
            result = this.findIClass(fieldDescriptor);
            if (result == null) {
                this.unloadableIClasses.add(fieldDescriptor);
                return null;
            }
        }

        if (!result.getDescriptor().equalsIgnoreCase(fieldDescriptor)) {
            throw new JaninoRuntimeException(
                "\"findIClass()\" returned \""
                + result.getDescriptor()
                + "\" instead of \""
                + fieldDescriptor
                + "\""
            );
        }

        if (IClassLoader.DEBUG) System.out.println(this + ": Loaded type \"" + fieldDescriptor + "\" as " + result);

        return result;
    }

    /**
     * Find a new {@link IClass} by descriptor; return <code>null</code> if a class
     * for that <code>descriptor</code> could not be found.
     * <p>
     * Similar {@link java.lang.ClassLoader#findClass(java.lang.String)}, this method
     * must
     * <ul>
     *   <li>Get an {@link IClass} object from somewhere for the given type
     *   <li>Call {@link #defineIClass(IClass)} with that {@link IClass} object as
     *       the argument
     *   <li>Return the {@link IClass} object
     * </ul>
     * <p>
     * The format of a <code>descriptor</code> is defined in JVMS 4.3.2. Typical
     * descriptors are:
     * <ul>
     *   <li><code>I</code> (Integer)
     *   <li><code>Lpkg1/pkg2/Cls;</code> (Class declared in package)
     *   <li><code>Lpkg1/pkg2/Outer$Inner;</code> Member class
     * </ul>
     * Notice that this method is never called for array types.
     * <p>
     * Notice that this method is never called from more than one thread at a time.
     * In other words, implementations of this method need not be synchronized.
     *
     * @return <code>null</code> if a class with that descriptor could not be found
     * @throws ClassNotFoundException if an exception was raised while loading the class
     */
    protected abstract IClass findIClass(String descriptor) throws ClassNotFoundException;

    /**
     * Define an {@link IClass} in the context of this {@link IClassLoader}.
     * If an {@link IClass} with that descriptor already exists, a
     * {@link RuntimeException} is thrown.
     * <p>
     * This method should only be called from an implementation of
     * {@link #findIClass(String)}.
     *
     * @throws RuntimeException A different {@link IClass} object is already defined for this type
     */
    protected final void
    defineIClass(IClass iClass) {
        String descriptor = iClass.getDescriptor();

        // Already defined?
        IClass loadedIClass = (IClass) this.loadedIClasses.get(descriptor);
        if (loadedIClass != null) {
            if (loadedIClass == iClass) return;
            throw new JaninoRuntimeException("Non-identical definition of IClass \"" + descriptor + "\"");
        }

        // Define.
        this.loadedIClasses.put(descriptor, iClass);
        if (IClassLoader.DEBUG) System.out.println(this + ": Defined type \"" + descriptor + "\"");
    }

    /**
     * Create an {@link IClassLoader} that looks for classes in the given "boot class
     * path", then in the given "extension directories", and then in the given
     * "class path".
     * <p>
     * The default for the <code>optionalBootClassPath</code> is the path defined in
     * the system property "sun.boot.class.path", and the default for the
     * <code>optionalExtensionDirs</code> is the path defined in the "java.ext.dirs"
     * system property.
     */
    public static IClassLoader
    createJavacLikePathIClassLoader(
        final File[] optionalBootClassPath,
        final File[] optionalExtDirs,
        final File[] classPath
    ) {
        ResourceFinder bootClassPathResourceFinder = new PathResourceFinder(
            optionalBootClassPath == null
            ? PathResourceFinder.parsePath(System.getProperty("sun.boot.class.path"))
            : optionalBootClassPath
        );
        ResourceFinder extensionDirectoriesResourceFinder = new JarDirectoriesResourceFinder(
            optionalExtDirs == null
            ? PathResourceFinder.parsePath(System.getProperty("java.ext.dirs"))
            : optionalExtDirs
        );
        final ResourceFinder classPathResourceFinder = new PathResourceFinder(classPath);

        // We can load classes through "ResourceFinderIClassLoader"s, which means
        // they are read into "ClassFile" objects, or we can load classes through
        // "ClassLoaderIClassLoader"s, which means they are loaded into the JVM.
        //
        // In my environment, the latter is slightly faster. No figures about
        // resource usage yet.
        //
        // In applications where the generated classes are not loaded into the
        // same JVM instance, we should avoid to use the
        // ClassLoaderIClassLoader, because that assumes that final fields have
        // a constant value, even if not compile-time-constant but only
        // initialization-time constant. The classical example is
        // "File.separator", which is non-blank final, but not compile-time-
        // constant.
        IClassLoader icl;
        icl = new ResourceFinderIClassLoader(bootClassPathResourceFinder, null);
        icl = new ResourceFinderIClassLoader(extensionDirectoriesResourceFinder, icl);
        icl = new ResourceFinderIClassLoader(classPathResourceFinder, icl);
        return icl;
    }

    private final IClassLoader                       optionalParentIClassLoader;
    private final Map<String /*descriptor*/, IClass> loadedIClasses     = new HashMap();
    private final Set<String /*descriptor*/>         unloadableIClasses = new HashSet();
}
