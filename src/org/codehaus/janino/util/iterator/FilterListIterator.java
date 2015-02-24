
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
 * An {@link java.util.ListIterator} that retrieves its elements from a delegate {@link java.util.ListIterator}. The
 * default implementation simply passes all method invocations to the delegate.
 *
 * @param <T> The element type of the list iterator
 */
public abstract
class FilterListIterator<T> implements ListIterator<T> {

    /** @see FilterListIterator */
    protected final ListIterator<T> delegate;

    public
    FilterListIterator(ListIterator<T> delegate) {
        this.delegate = delegate;
    }

    /** Calls {@link #delegate}.{@link java.util.ListIterator#hasNext()} */
    @Override public boolean
    hasNext() { return this.delegate.hasNext(); }

    /** Calls {@link #delegate}.{@link java.util.ListIterator#next()} */
    @Override public T
    next() { return this.delegate.next(); }

    /** Calls {@link #delegate}.{@link java.util.ListIterator#hasPrevious()} */
    @Override public boolean
    hasPrevious() { return this.delegate.hasPrevious(); }

    /** Calls {@link #delegate}.{@link java.util.ListIterator#previous()} */
    @Override public T
    previous() { return this.delegate.previous(); }

    /** Calls {@link #delegate}.{@link java.util.ListIterator#nextIndex()} */
    @Override public int
    nextIndex() { return this.delegate.nextIndex(); }

    /** Calls {@link #delegate}.{@link java.util.ListIterator#previousIndex()} */
    @Override public int
    previousIndex() { return this.delegate.previousIndex(); }

    /** Calls {@link #delegate}.{@link java.util.ListIterator#remove()} */
    @Override public void
    remove() { this.delegate.remove(); }

    /** Calls {@link #delegate}.{@link java.util.ListIterator#set(java.lang.Object)} */
    @Override public void
    set(T o) { this.delegate.set(o); }

    /** Calls {@link #delegate}.{@link java.util.ListIterator#add(java.lang.Object)} */
    @Override public void
    add(T o) { this.delegate.add(o); }
}
