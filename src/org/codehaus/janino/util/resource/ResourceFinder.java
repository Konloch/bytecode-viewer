
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

package org.codehaus.janino.util.resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Finds a resource by name.
 * <p>
 * Notice that there is a symmetrical concept
 * {@link org.codehaus.janino.util.resource.ResourceCreator} that creates resources for
 * writing.
 *
 * @see org.codehaus.janino.util.resource.ResourceCreator
 */
public abstract
class ResourceFinder {

    /**
     * Find a resource by name and open it for reading.
     *
     * @param resourceName Designates the resource; typically structured by slashes ("/") like
     *                     "<code>com/foo/pkg/Bar.class</code>"
     * @return             <code>null</code> if the resource could not be found
     * @throws IOException The resource was found, but there are problems opening it
     */
    public final InputStream
    findResourceAsStream(String resourceName) throws IOException {
        Resource resource = this.findResource(resourceName);
        if (resource == null) return null;
        return resource.open();
    }

    /**
     * Find a resource by name and return it as a {@link Resource} object.
     *
     * @param resourceName Designates the resource; typically structured by slashes ("/") like
     *                     "<code>com/foo/pkg/Bar.class</code>"
     * @return             <code>null</code> if the resource could not be found
     */
    public abstract Resource findResource(String resourceName);

    /** This one's useful when a resource finder is required, but cannot be created for some reason. */
    public static final ResourceFinder EMPTY_RESOURCE_FINDER = new ResourceFinder() {
        @Override public Resource findResource(String resourceName) { return null; }
        @Override public String   toString()                        { return "invalid entry"; }
    };
}
