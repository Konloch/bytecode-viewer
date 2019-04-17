package the.bytecode.club.bytecodeviewer.util;

import java.io.*;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 *                                                                         *
 * This program is free software: you can redistribute it and/or modify    *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

/**
 * Convert the various newline conventions to the local platform's
 * newline convention. <p>
 * <p>
 * This stream can be used with the Message.writeTo method to
 * generate a message that uses the local plaform's line terminator
 * for the purpose of (e.g.) saving the message to a local file.
 */
public class NewlineOutputStream extends FilterOutputStream {
    private int lastb = -1;
    private static byte[] newline;

    public NewlineOutputStream(OutputStream os) {
        super(os);
        if (newline == null) {
            String s = System.getProperty("line.separator");
            if (s == null || s.length() <= 0)
                s = "\n";
            try {
                newline = s.getBytes("iso-8859-1");    // really us-ascii
            } catch (UnsupportedEncodingException ex) {
                // should never happen
                newline = new byte[]{(byte) '\n'};
            }
        }
    }

    public void write(int b) throws IOException {
        if (b == '\r') {
            out.write(newline);
        } else if (b == '\n') {
            if (lastb != '\r')
                out.write(newline);
        } else {
            out.write(b);
        }
        lastb = b;
    }

    public void write(byte b[]) throws IOException {
        write(b, 0, b.length);
    }

    public void write(byte b[], int off, int len) throws IOException {
        for (int i = 0; i < len; i++) {
            write(b[off + i]);
        }
    }
}