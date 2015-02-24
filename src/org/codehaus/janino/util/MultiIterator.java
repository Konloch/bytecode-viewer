
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.codehaus.janino.JaninoRuntimeException;

/**
 * An {@link java.util.Iterator} that traverses a {@link java.util.Collection} of {@link java.util.Iterator}s.
 *
 * @param <T> The element type of the iterator
 */
@SuppressWarnings("unchecked") public
class MultiIterator<T> implements Iterator<T> {

    private static final Iterator<?> AT_END = new Iterator<Object>() {
        @Override public boolean hasNext() { return false; }
        @Override public Object  next()    { throw new NoSuchElementException(); }
        @Override public void    remove()  { throw new UnsupportedOperationException(); }
    };

    private final Iterator<?> outer; // Over Iterators, Collections or arrays
    private Iterator<T>       inner = (Iterator<T>) MultiIterator.AT_END;

    /** @param iterators An array of {@link Iterator}s */
    public
    MultiIterator(Iterator<T>[] iterators) { this.outer = Arrays.asList(iterators).iterator(); }

    /** @param collections An array of {@link Collection}s */
    public
    MultiIterator(Collection<T>[] collections) { this.outer = Arrays.asList(collections).iterator(); }

    /** @param arrays An array of arrays */
    public
    MultiIterator(Object/*T*/[][] arrays) { this.outer = Arrays.asList(arrays).iterator(); }

    /** @param collection A {@link Collection} of {@link Collection}s, {@link Iterator}s and/or arrays */
    public
    MultiIterator(Collection<?> collection) { this.outer = collection.iterator(); }

    /** @param iterator An iterator over {@link Collection}s, {@link Iterator}s and/or arrays */
    public
    MultiIterator(Iterator<?> iterator) { this.outer = iterator; }

    /** @param array An array of {@link Collection}s, {@link Iterator}s and/or arrays */
    public
    MultiIterator(Object[] array) { this.outer = Arrays.asList(array).iterator(); }

    /** Iterates over the given {@link Collection}, prepended with the given {@link Object}. */
    public
    MultiIterator(Object/*T*/ object, Collection<T> collection) {
        this.outer = Arrays.asList(new Object[] {
            new Object[] { object },
            collection
        }).iterator();
    }

    /** Iterates over the given {@link Collection}, appended with the given {@link Object}. */
    public
    MultiIterator(Collection<T> collection, Object/*T*/ object) {
        this.outer = Arrays.asList(new Object[] {
            collection,
            new Object[] { object }
        }).iterator();
    }

    /** Iterates over the given {@link Iterator}, prepended with the given {@code prefix}. */
    public
    MultiIterator(Object/*T*/ prefix, Iterator<T> iterator) {
        this.outer = Arrays.asList(new Object[] {
            new Object[] { prefix },
            iterator
        }).iterator();
    }

    /** Iterates over the given {@link Iterator}, appended with the given <code>suffix</code>. */
    public
    MultiIterator(Iterator<T> iterator, Object/*T*/ suffix) {
        this.outer = Arrays.asList(new Object[] {
            iterator,
            new Object[] { suffix }
        }).iterator();
    }

    @Override public boolean
    hasNext() {
        for (;;) {
            if (this.inner.hasNext()) return true;
            if (!this.outer.hasNext()) return false;
            Object o = this.outer.next();
            if (o instanceof Iterator) {
                this.inner = (Iterator<T>) o;
            } else
            if (o instanceof Collection) {
                this.inner = ((Collection<T>) o).iterator();
            } else
            if (o instanceof Object[]) {
                this.inner = Arrays.asList((T[]) o).iterator();
            } else
            {
                throw new JaninoRuntimeException("Unexpected element type \"" + o.getClass().getName() + "\"");
            }
        }
    }

    @Override public T
    next() {
        if (this.hasNext()) return this.inner.next();
        throw new NoSuchElementException();
    }

    @Override public void
    remove() {
        this.inner.remove();
    }
}
