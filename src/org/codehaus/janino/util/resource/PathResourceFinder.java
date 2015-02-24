
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipFile;

import org.codehaus.janino.util.iterator.TransformingIterator;


/**
 * A {@link org.codehaus.janino.util.resource.ResourceFinder} that finds its resources along a "path"
 * consisting of JAR file names, ZIP file names, and directory names.
 * @see org.codehaus.janino.util.resource.ZipFileResourceFinder
 * @see org.codehaus.janino.util.resource.DirectoryResourceFinder
 */
@SuppressWarnings({ "rawtypes", "unchecked" }) public
class PathResourceFinder extends LazyMultiResourceFinder {

    /** @param entries The entries of the "path" */
    public
    PathResourceFinder(final File[] entries) {
        super(PathResourceFinder.createIterator(Arrays.asList(entries).iterator()));
    }

    /** @param entries The entries of the "path" (type must be {@link File}) */
    public
    PathResourceFinder(Iterator<ResourceFinder> entries) { super(entries); }

    /** @param path A java-like path, i.e. a "path separator"-separated list of entries. */
    public
    PathResourceFinder(String path) { this(PathResourceFinder.parsePath(path)); }

    private static Iterator<ResourceFinder>
    createIterator(final Iterator<File> entries) {
        return new TransformingIterator/*<File, ResourceFinder>*/(entries) {
            @Override protected Object transform(Object o) { return PathResourceFinder.createResourceFinder((File) o); }
        };
    }

    /**
     * Break a given string up by the system-dependent path-separator character (on UNIX systems,
     * this character is ':'; on Microsoft Windows systems it is ';'). Empty components are
     * ignored.
     * <p>
     * UNIX Examples:
     * <dl>
     *   <dt>A:B:C          <dd>A, B, C
     *   <dt>::B:           <dd>B
     *   <dt>:A             <dd>A
     *   <dt>(Empty string) <dd>(Zero components)
     * </dl>
     *
     * @see File#pathSeparatorChar
     */
    public static File[]
    parsePath(String s) {
        int        from = 0;
        List<File> l    = new ArrayList();
        for (;;) {
            int to = s.indexOf(File.pathSeparatorChar, from);
            if (to == -1) {
                if (from != s.length()) l.add(new File(s.substring(from)));
                break;
            }
            if (to != from) l.add(new File(s.substring(from, to)));
            from = to + 1;
        }
        return (File[]) l.toArray(new File[l.size()]);
    }

    /**
     * A factory method that creates a Java classpath-style ResourceFinder as
     * follows:
     * <table>
     *   <tr><th><code>entry</code></th><th>Returned {@link ResourceFinder}</th></tr>
     *   <tr><td>"*.jar" file</td><td>{@link ZipFileResourceFinder}</td></tr>
     *   <tr><td>"*.zip" file</td><td>{@link ZipFileResourceFinder}</td></tr>
     *   <tr><td>directory</td><td>{@link DirectoryResourceFinder}</td></tr>
     *   <tr><td>any other</td><td>A {@link ResourceFinder} that never finds a resource</td></tr>
     * </table>
     * @return a valid {@link ResourceFinder}
     */
    private static ResourceFinder
    createResourceFinder(final File entry) {

        // ZIP file or JAR file.
        if (
            (entry.getName().endsWith(".jar") || entry.getName().endsWith(".zip"))
            && entry.isFile()
        ) {
            try {
                return new ZipFileResourceFinder(new ZipFile(entry));
            } catch (IOException e) {
                return ResourceFinder.EMPTY_RESOURCE_FINDER;
            }
        }

        // Directory.
        if (entry.isDirectory()) {
            return new DirectoryResourceFinder(entry);
        }

        // Invalid entry.
        return ResourceFinder.EMPTY_RESOURCE_FINDER;
    }
}
