
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

/** An {@link IClassLoader} that loads {@link IClass}es through a reflection {@link ClassLoader}. */
@SuppressWarnings("rawtypes") public
class ClassLoaderIClassLoader extends IClassLoader {
    private static final boolean DEBUG = false;

    /** @param classLoader The delegate that loads the classes. */
    public
    ClassLoaderIClassLoader(ClassLoader classLoader) {
        super(
            null   // optionalParentIClassLoader
        );

        if (classLoader == null) throw new NullPointerException();

        this.classLoader = classLoader;
        super.postConstruct();
    }

    /**
     * Equivalent to
     * <pre>
     *   ClassLoaderIClassLoader(Thread.currentThread().getContextClassLoader())
     * </pre>
     */
    public
    ClassLoaderIClassLoader() { this(Thread.currentThread().getContextClassLoader()); }

    /** @return The delegate {@link ClassLoader} */
    public ClassLoader
    getClassLoader() { return this.classLoader; }

    @Override protected IClass
    findIClass(String descriptor) throws ClassNotFoundException {

        Class clazz;
        try {

            //
            // See also [ 931385 ] Janino 2.0 throwing exception on arrays of java.io.File:
            //
            // "ClassLoader.loadClass()" and "Class.forName()" should be identical,
            // but "ClassLoader.loadClass("[Ljava.lang.Object;")" throws a
            // ClassNotFoundException under JDK 1.5.0 beta.
            // Unclear whether this a beta version bug and SUN will fix this in the final
            // release, but "Class.forName()" seems to work fine in all cases, so we
            // use that.
            //

//            clazz = this.classLoader.loadClass(Descriptor.toClassName(descriptor));
            clazz = Class.forName(Descriptor.toClassName(descriptor), false, this.classLoader);
        } catch (ClassNotFoundException e) {
            if (e.getException() == null) {
                return null;
            } else
            {
                throw e;
            }
        }
        if (ClassLoaderIClassLoader.DEBUG) System.out.println("clazz = " + clazz);

        IClass result = new ReflectionIClass(clazz, this);
        this.defineIClass(result);
        return result;
    }

    private final ClassLoader classLoader;
}
