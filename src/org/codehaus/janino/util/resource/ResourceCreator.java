
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
import java.io.OutputStream;

/**
 * Opens a resource, characterized by a name, for writing.
 * <p>
 * There also exists a concept {@link org.codehaus.janino.util.resource.ResourceFinder} that
 * finds {@link org.codehaus.janino.util.resource.Resource}s for reading.
 *
 * @see org.codehaus.janino.util.resource.ResourceFinder
 */
public
interface ResourceCreator {

    /**
     * Create the designated resource.
     *
     * @param resourceName Designates the resource; typically structured by slashes ("/") like
     *                     "<code>com/foo/pkg/Bar.class</code>"
     * @return             Bytes written to this {@link OutputStream} are stored in the resource
     * @throws IOException Problems creating the resource
     */
    OutputStream createResource(String resourceName) throws IOException;

    /**
     * Deletes the resource with the given name.
     *
     * @return <code>false</code> if the resource could not be deleted
     */
    boolean deleteResource(String resourceName);
}
