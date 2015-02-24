
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

import java.util.ListIterator;

/**
 * A {@link java.util.ListIterator} that reverses the direction of all operations
 * of a delegate {@link java.util.ListIterator}.
 *
 * @param <T> The element type of the list iterator
 */
public
class ReverseListIterator<T> extends FilterListIterator<T> {

    public
    ReverseListIterator(ListIterator<T> delegate) { super(delegate); }

    /** Calls {@link #delegate}.{@link java.util.ListIterator#hasPrevious()} */
    @Override public boolean
    hasNext() { return super.hasPrevious(); }

    /** Calls {@link #delegate}.{@link java.util.ListIterator#hasNext()} */
    @Override public boolean
    hasPrevious() { return super.hasNext(); }

    /** Calls {@link #delegate}.{@link java.util.ListIterator#previous()} */
    @Override public T
    next() { return super.previous(); }

    /** Calls {@link #delegate}.{@link java.util.ListIterator#next()} */
    @Override public T
    previous() { return super.next(); }

    /** Throws an {@link UnsupportedOperationException}. */
    @Override public int
    nextIndex() { throw new UnsupportedOperationException(); }

    /** Throws an {@link UnsupportedOperationException}. */
    @Override public int
    previousIndex() { throw new UnsupportedOperationException(); }
}
