
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * A {@link org.codehaus.janino.util.resource.ResourceFinder} that provides access
 * to resource stored as byte arrays in a {@link java.util.Map}.
 */
public
class MapResourceFinder extends ResourceFinder {
    private final Map<String, byte[]> map;
    private long                      lastModified;

    public
    MapResourceFinder(Map<String, byte[]> map) { this.map = map; }

    /** @param lastModified The return value of {@link Resource#lastModified()} for the next resources found */
    public final void
    setLastModified(long lastModified) { this.lastModified = lastModified; }

    @Override public final Resource
    findResource(final String resourceName) {
        final byte[] ba = (byte[]) this.map.get(resourceName);
        if (ba == null) return null;

        return new Resource() {
            @Override public InputStream open()         { return new ByteArrayInputStream(ba); }
            @Override public String      getFileName()  { return resourceName; }
            @Override public long        lastModified() { return MapResourceFinder.this.lastModified; }
        };
    }
}
