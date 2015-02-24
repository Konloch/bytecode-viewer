
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

package org.codehaus.janino.util.iterator;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.janino.JaninoRuntimeException;
import org.codehaus.janino.util.Producer;

/**
 * An {@link Iterator} that finds the normal {@link File}s who's names are
 * {@link FilenameFilter#accept(java.io.File, java.lang.String) accepted} by the
 * <code>fileNameFilter</code> and
 * <ul>
 *   <li>
 *     that exist in the given <code>rootDirectory</code>,
 *   </li>
 *   <li>
 *     and those that exist in all subdirectories of the
 *     <code>rootDirectory</code> who's names are
 *     {@link FilenameFilter#accept(java.io.File, java.lang.String)}ed by the
 *     <code>directoryNameFilter</code>
 *   </li>
 * </ul>
 */
@SuppressWarnings({ "rawtypes", "unchecked" }) public
class DirectoryIterator extends ProducerIterator<File> {
    public
    DirectoryIterator(
        final File           rootDirectory,
        final FilenameFilter directoryNameFilter,
        final FilenameFilter fileNameFilter
    ) {
        super(new Producer() {
            private final List<State> stateStack = DirectoryIterator.newArrayList(new State(rootDirectory));

            @Override public Object
            produce() {
                while (!this.stateStack.isEmpty()) {
                    State state = (State) this.stateStack.get(this.stateStack.size() - 1);
                    if (state.directories.hasNext()) {
                        this.stateStack.add(new State((File) state.directories.next()));
                    } else
                    if (state.files.hasNext()) {
                        File file = (File) state.files.next();
                        return file;
                    } else
                    {
                        this.stateStack.remove(this.stateStack.size() - 1);
                    }
                }
                return null;
            }

            class State {
                State(File dir) {
                    File[] entries = dir.listFiles();
                    if (entries == null) {
                        throw new JaninoRuntimeException("Directory \"" + dir + "\" could not be read");
                    }
                    List<File> directoryList = new ArrayList();
                    List<File> fileList      = new ArrayList();
                    for (File entry : entries) {
                        if (entry.isDirectory()) {
                            if (directoryNameFilter.accept(dir, entry.getName())) directoryList.add(entry);
                        } else
                        if (entry.isFile()) {
                            if (fileNameFilter.accept(dir, entry.getName())) fileList.add(entry);
                        }
                    }
                    this.directories = directoryList.iterator();
                    this.files       = fileList.iterator();
                }
                final Iterator<File> directories;
                final Iterator<File> files;
            }
        });
    }

    /**
     * Create an {@link Iterator} that returns all matching {@link File}s locatable in a <i>set</i> of root
     * directories.
     *
     * @see #DirectoryIterator(File, FilenameFilter, FilenameFilter)
     */
    public static Iterator<File>
    traverseDirectories(
        File[]         rootDirectories,
        FilenameFilter directoryNameFilter,
        FilenameFilter fileNameFilter
    ) {
        List<Iterator<File>> result = new ArrayList();
        for (File rootDirectory : rootDirectories) {
            result.add(new DirectoryIterator(rootDirectory, directoryNameFilter, fileNameFilter));
        }
        return new MultiDimensionalIterator(result.iterator(), 2);
    }

    private static ArrayList
    newArrayList(Object initialElement) {
        ArrayList result = new ArrayList();
        result.add(initialElement);
        return result;
    }
}
