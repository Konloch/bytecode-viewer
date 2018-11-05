/***
 * ASM XML Adapter
 * Copyright (c) 2004-2011, Eugene Kuleshov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.objectweb.asm.xml;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * SAXAdapter
 *
 * @author Eugene Kuleshov
 */
public class SAXAdapter {

    private final ContentHandler h;

    protected SAXAdapter(final ContentHandler h) {
        this.h = h;
    }

    protected ContentHandler getContentHandler() {
        return h;
    }

    protected void addDocumentStart() {
        try {
            h.startDocument();
        } catch (SAXException ex) {
            throw new RuntimeException(ex.getMessage(), ex.getException());
        }
    }

    protected void addDocumentEnd() {
        try {
            h.endDocument();
        } catch (SAXException ex) {
            throw new RuntimeException(ex.getMessage(), ex.getException());
        }
    }

    protected final void addStart(final String name, final Attributes attrs) {
        try {
            h.startElement("", name, name, attrs);
        } catch (SAXException ex) {
            throw new RuntimeException(ex.getMessage(), ex.getException());
        }
    }

    protected final void addEnd(final String name) {
        try {
            h.endElement("", name, name);
        } catch (SAXException ex) {
            throw new RuntimeException(ex.getMessage(), ex.getException());
        }
    }

    protected final void addElement(final String name, final Attributes attrs) {
        addStart(name, attrs);
        addEnd(name);
    }
}
