
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

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.janino.util.ClassFile;
import org.codehaus.janino.util.resource.Resource;
import org.codehaus.janino.util.resource.ResourceFinder;


/**
 * This {@link org.codehaus.janino.IClassLoader} loads IClasses through a
 * a {@link org.codehaus.janino.util.resource.ResourceFinder} that designates
 * {@link org.codehaus.janino.util.ClassFile}s.
 */
public
class ResourceFinderIClassLoader extends IClassLoader {
    private final ResourceFinder resourceFinder;

    public
    ResourceFinderIClassLoader(ResourceFinder resourceFinder, IClassLoader optionalParentIClassLoader) {
        super(optionalParentIClassLoader);
        this.resourceFinder = resourceFinder;
        this.postConstruct();
    }

    @Override protected IClass
    findIClass(String descriptor) throws ClassNotFoundException {
        String className = Descriptor.toClassName(descriptor);

        // Find the class file resource.
        Resource classFileResource = this.resourceFinder.findResource(ClassFile.getClassFileResourceName(className));
        if (classFileResource == null) return null;

        // Open the class file resource.
        InputStream is;
        try {
            is = classFileResource.open();
        } catch (IOException ex) {
            throw new ClassNotFoundException("Opening resource \"" + classFileResource.getFileName() + "\"", ex);
        }

        // Load the IClass from the class file.
        ClassFile cf;
        try {
            cf = new ClassFile(is);
        } catch (IOException e) {
            throw new ClassNotFoundException("Reading resource \"" + classFileResource.getFileName() + "\"", e);
        } finally {
            try { is.close(); } catch (IOException e) {}
        }
        IClass iClass = new ClassFileIClass(cf, this);
        this.defineIClass(iClass);
        return iClass;
    }
}
