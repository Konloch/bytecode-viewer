
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An {@link java.util.Iterator} that iterates over a delegate, which produces
 * arrays, {@link java.util.Collection}s, {@link java.util.Enumeration}s or
 * {@link java.util.Iterator}s. This {@link java.util.Iterator} returns the
 * elements of these objects.
 * <p>
 * The count of dimensions is declared at construction. Count "1" produces an
 * {@link java.util.Iterator} that adds no functionality to its delegate, count
 * "2" produces an {@link Iterator} that behaves as explained above, and so
 * forth.
 */
@SuppressWarnings("rawtypes") public
class MultiDimensionalIterator implements Iterator {
    private final Iterator[]      nest;
    private static final Iterator EMPTY_ITERATOR = new Iterator() {
        @Override public boolean hasNext() { return false; }
        @Override public Object  next()    { throw new NoSuchElementException(); }
        @Override public void    remove()  { throw new UnsupportedOperationException("remove"); }
    };

    public
    MultiDimensionalIterator(Iterator delegate, int dimensionCount) {
        this.nest    = new Iterator[dimensionCount];
        this.nest[0] = delegate;
        for (int i = 1; i < dimensionCount; ++i) this.nest[i] = MultiDimensionalIterator.EMPTY_ITERATOR;
    }

    /** @throws UniterableElementException */
    @SuppressWarnings("unchecked") @Override public boolean
    hasNext() {

        // Unroll this check because it is so performance critical:
        if (this.nest[this.nest.length - 1].hasNext()) return true;

        int i = this.nest.length - 2;
        if (i < 0) return false;

        for (;;) {
            if (!this.nest[i].hasNext()) {
                if (i == 0) return false;
                --i;
            } else {
                if (i == this.nest.length - 1) return true;
                Object o = this.nest[i].next();
                if (o instanceof Iterator) {
                    this.nest[++i] = (Iterator) o;
                } else
                if (o instanceof Object[]) {
                    this.nest[++i] = Arrays.asList((Object[]) o).iterator();
                } else
                if (o instanceof Collection) {
                    this.nest[++i] = ((Collection) o).iterator();
                } else
                if (o instanceof Enumeration) {
                    this.nest[++i] = new EnumerationIterator<Object>((Enumeration) o);
                } else
                {
                    throw new UniterableElementException();
                }
            }
        }
    }

    @Override public Object
    next() {
        if (!this.hasNext()) throw new NoSuchElementException();
        return this.nest[this.nest.length - 1].next();
    }

    @Override public void remove() { throw new UnsupportedOperationException("remove"); }
}
