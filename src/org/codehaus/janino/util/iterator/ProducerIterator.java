
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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.codehaus.janino.util.Producer;

/**
 * An {@link Iterator} that iterates over all the objects produced by a delegate {@link Producer}.
 *
 * @param <T> The type of the products and the iterator elements
 * @see       Producer
 */
public
class ProducerIterator<T> implements Iterator<T> {

    private final Producer<T> producer;

    private static final Object UNKNOWN     = new Object();
    private static final Object AT_END      = null;
    private Object              nextElement = ProducerIterator.UNKNOWN;

    public
    ProducerIterator(Producer<T> producer) { this.producer = producer; }

    @Override public boolean
    hasNext() {
        if (this.nextElement == ProducerIterator.UNKNOWN) this.nextElement = this.producer.produce();
        return this.nextElement != ProducerIterator.AT_END;
    }

    @Override public T
    next() {
        if (this.nextElement == ProducerIterator.UNKNOWN) this.nextElement = this.producer.produce();
        if (this.nextElement == ProducerIterator.AT_END) throw new NoSuchElementException();
        @SuppressWarnings("unchecked") T result = (T) this.nextElement;
        this.nextElement = ProducerIterator.UNKNOWN;
        return result;
    }

    @Override public void
    remove() { throw new UnsupportedOperationException("remove"); }
}
