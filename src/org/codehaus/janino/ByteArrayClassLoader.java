
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

import java.util.Map;

/** This {@link ClassLoader} allows for the loading of a set of Java&trade; classes provided in class file format. */
@SuppressWarnings({ "rawtypes", "unchecked" }) public
class ByteArrayClassLoader extends ClassLoader {

    /**
     * The given {@link Map} of classes must not be modified afterwards.
     *
     * @param classes String className => byte[] data
     */
    public
    ByteArrayClassLoader(Map<String /*className*/, byte[] /*data*/> classes) { this.classes = classes; }

    /** @see #ByteArrayClassLoader(Map) */
    public
    ByteArrayClassLoader(Map<String /*className*/, byte[] /*data*/> classes, ClassLoader parent) {
        super(parent);
        this.classes = classes;
    }

    /**
     * Implements {@link ClassLoader#findClass(String)}.
     * <p>
     * Notice that, although nowhere documented, no more than one thread at a time calls this
     * method, because {@link ClassLoader#loadClass(java.lang.String)} is
     * <code>synchronized</code>.
     */
    @Override protected Class
    findClass(String name) throws ClassNotFoundException {
        byte[] data = (byte[]) this.classes.get(name);
        if (data == null) throw new ClassNotFoundException(name);

        // Notice: Not inheriting the protection domain will cause problems with Java Web Start /
        // JNLP. See
        //     http://jira.codehaus.org/browse/JANINO-104
        //     http://www.nabble.com/-Help-jel--java.security.AccessControlException-to13073723.html
        return super.defineClass(
            name,                                 // name
            data, 0, data.length,                 // b, off, len
            this.getClass().getProtectionDomain() // protectionDomain
        );
    }

    private final Map<String /*className*/, byte[] /*data*/> classes;
}
