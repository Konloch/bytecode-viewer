
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

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A {@link java.util.Collection} that lazily reads its elements from an
 * {@link java.util.Iterator}.
 * <p>
 * In other words, you can call {@link #iterator()} as often as you want, but the
 * {@link IteratorCollection} will iterate over its delegate only once.
 *
 * @param <T> The element type of the iterator and the collection
 */
@SuppressWarnings({ "rawtypes", "unchecked" }) public
class IteratorCollection<T> extends AbstractCollection<T> {

    /** The delegate. */
    private final Iterator<T> iterator;

    /** Lazily-filled collection of the elements delivered by the delegate. */
    private final List/*<T>*/ elements = new ArrayList();

    public
    IteratorCollection(Iterator iterator) { this.iterator = iterator; }

    @Override public Iterator<T>
    iterator() {
        return new Iterator/*<T>*/() {

            private Iterator/*<T>*/ elementsIterator = IteratorCollection.this.elements.iterator();

            @Override public Object
            next() {
                if (this.elementsIterator != null) {
                    if (this.elementsIterator.hasNext()) return this.elementsIterator.next();
                    this.elementsIterator = null;
                }
                Object o = IteratorCollection.this.iterator.next();
                IteratorCollection.this.elements.add(o);
                return o;
            }

            @Override public boolean
            hasNext() {
                return (
                    (this.elementsIterator != null && this.elementsIterator.hasNext())
                    || IteratorCollection.this.iterator.hasNext()
                );
            }

            @Override public void
            remove() { throw new UnsupportedOperationException(); }
        };
    }

    @Override public int
    size() {
        int size = 0;
        for (@SuppressWarnings("unused") Object o : this) ++size;
        return size;
    }
}
