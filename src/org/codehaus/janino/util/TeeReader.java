
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

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * A {@link java.io.FilterReader} that copies the bytes being passed through
 * to a given {@link java.io.Writer}. This is in analogy with the UNIX "tee" command.
 */
public
class TeeReader extends FilterReader {
    private final Writer  out;
    private final boolean closeWriterOnEOF;

    public
    TeeReader(Reader in, Writer out, boolean closeWriterOnEof) {
        super(in);
        this.out              = out;
        this.closeWriterOnEOF = closeWriterOnEof;
    }

    @Override public void
    close() throws IOException {
        this.in.close();
        this.out.close();
    }

    @Override public int
    read() throws IOException {
        int c = this.in.read();
        if (c == -1) {
            if (this.closeWriterOnEOF) {
                this.out.close();
            } else {
                this.out.flush();
            }
        } else {
            this.out.write(c);
        }
        return c;
    }

    @Override public int
    read(char[] cbuf, int off, int len) throws IOException {
        int bytesRead = this.in.read(cbuf, off, len);
        if (bytesRead == -1) {
            if (this.closeWriterOnEOF) {
                this.out.close();
            } else {
                this.out.flush();
            }
        } else {
            this.out.write(cbuf, off, bytesRead);
        }
        return bytesRead;
    }
}
