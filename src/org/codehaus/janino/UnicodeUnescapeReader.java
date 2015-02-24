
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

package org.codehaus.janino;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 * A {@link FilterReader} that unescapes the "Unicode Escapes" as described in JLS7 3.10.6.
 * <p>
 * Notice that it is possible to formulate invalid escape sequences, e.g. "&#92;u123g" ("g" is not a valid hex
 * character). This is handled by throwing a {@link java.lang.RuntimeException}-derived {@link
 * org.codehaus.janino.UnicodeUnescapeException}.
 */
public
class UnicodeUnescapeReader extends FilterReader {

    public
    UnicodeUnescapeReader(Reader in) { super(in); }

    /**
     * Override {@link FilterReader#read()}.
     *
     * @throws UnicodeUnescapeException Invalid escape sequence encountered
     */
    @Override public int
    read() throws IOException {
        int c;

        // Read next character.
        if (this.unreadChar == -1) {
            c = this.in.read();
        } else {
            c               = this.unreadChar;
            this.unreadChar = -1;
        }

        // Check for backslash-u escape sequence, preceeded with an even number
        // of backslashes.
        if (c != '\\' || this.oddPrecedingBackslashes) {
            this.oddPrecedingBackslashes = false;
            return c;
        }

        // Read one character ahead and check if it is a "u".
        c = this.in.read();
        if (c != 'u') {
            this.unreadChar              = c;
            this.oddPrecedingBackslashes = true;
            return '\\';
        }

        // Skip redundant "u"s.
        do {
            c = this.in.read();
            if (c == -1) throw new UnicodeUnescapeException("Incomplete escape sequence");
        } while (c == 'u');

        // Decode escape sequence.
        char[] ca = new char[4];
        ca[0] = (char) c;
        if (this.in.read(ca, 1, 3) != 3) throw new UnicodeUnescapeException("Incomplete escape sequence");
        try {
            return 0xffff & Integer.parseInt(new String(ca), 16);
        } catch (NumberFormatException ex) {
            throw new UnicodeUnescapeException("Invalid escape sequence \"\\u" + new String(ca) + "\"", ex);
        }
    }

    /** Overrides {@link FilterReader#read(char[], int, int)}. */
    @Override public int
    read(char[] cbuf, int off, int len) throws IOException {
        if (len == 0) return 0;
        int res = 0;
        do {
            int c = this.read();
            if (c == -1) break;
            cbuf[off++] = (char) c;
        } while (++res < len);
        return res == 0 ? -1 : res;
    }

    private int     unreadChar = -1; // -1 == none
    private boolean oddPrecedingBackslashes;
}
