
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.codehaus.janino.JaninoRuntimeException;
import org.codehaus.janino.util.resource.Resource;
import org.codehaus.janino.util.resource.ResourceFinder;


/**
 * A {@link ClassLoader} that uses a {@link org.codehaus.janino.util.resource.ResourceFinder} to find ".class" files.
 */
@SuppressWarnings({ "rawtypes", "unchecked" }) public
class ResourceFinderClassLoader extends ClassLoader {

    private final ResourceFinder resourceFinder;

    public
    ResourceFinderClassLoader(ResourceFinder resourceFinder, ClassLoader parent) {
        super(parent);
        this.resourceFinder = resourceFinder;
    }

    /** @return The underlying {@link ResourceFinder} */
    public ResourceFinder
    getResourceFinder() { return this.resourceFinder; }

    @Override protected Class
    findClass(String className) throws ClassNotFoundException {

        // Find the resource containing the class bytecode.
        Resource classFileResource = this.resourceFinder.findResource(ClassFile.getClassFileResourceName(className));
        if (classFileResource == null) throw new ClassNotFoundException(className);

        // Open the class file resource.
        InputStream is;
        try {
            is = classFileResource.open();
        } catch (IOException ex) {
            throw new JaninoRuntimeException((
                "Opening class file resource \""
                + classFileResource.getFileName()
                + "\": "
                + ex.getMessage()
            ), ex);
        }

        // Read bytecode from the resource into a byte array.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[4096];
            for (;;) {
                int bytesRead = is.read(buffer);
                if (bytesRead == -1) break;
                baos.write(buffer, 0, bytesRead);
            }
        } catch (IOException ex) {
            throw new ClassNotFoundException("Reading class file from \"" + classFileResource + "\"", ex);
        } finally {
            try { is.close(); } catch (IOException ex) {}
        }
        byte[] ba = baos.toByteArray();

        // Define the class in this ClassLoader.
        Class clazz = super.defineClass(null, ba, 0, ba.length);

        if (!clazz.getName().equals(className)) {

            // This is a really complicated case: We may find a class file on
            // the class path that seemingly defines the class we are looking
            // for, but doesn't. This is possible if the underlying file system
            // has case-insensitive file names and/or file names that are
            // limited in length (e.g. DOS 8.3).
            throw new ClassNotFoundException(className);
        }

        return clazz;
    }
}
