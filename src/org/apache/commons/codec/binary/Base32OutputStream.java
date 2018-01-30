/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.codec.binary;

import java.io.OutputStream;

/**
 * Provides Base32 encoding and decoding in a streaming fashion (unlimited size). When encoding the default lineLength
 * is 76 characters and the default lineEnding is CRLF, but these can be overridden by using the appropriate
 * constructor.
 * <p>
 * The default behaviour of the Base32OutputStream is to ENCODE, whereas the default behaviour of the Base32InputStream
 * is to DECODE. But this behaviour can be overridden by using a different constructor.
 * </p>
 * <p>
 * Since this class operates directly on byte streams, and not character streams, it is hard-coded to only encode/decode
 * character encodings which are compatible with the lower 127 ASCII chart (ISO-8859-1, Windows-1252, UTF-8, etc).
 * </p>
 * <p>
 * <b>Note:</b> It is mandatory to close the stream after the last byte has been written to it, otherwise the
 * final padding will be omitted and the resulting data will be incomplete/inconsistent.
 * </p>
 *
 * @version $Id$
 * @see <a href="http://www.ietf.org/rfc/rfc4648.txt">RFC 4648</a>
 * @since 1.5
 */
public class Base32OutputStream extends BaseNCodecOutputStream {

    /**
     * Creates a Base32OutputStream such that all data written is Base32-encoded to the original provided OutputStream.
     *
     * @param out
     *            OutputStream to wrap.
     */
    public Base32OutputStream(final OutputStream out) {
        this(out, true);
    }

    /**
     * Creates a Base32OutputStream such that all data written is either Base32-encoded or Base32-decoded to the
     * original provided OutputStream.
     *
     * @param out
     *            OutputStream to wrap.
     * @param doEncode
     *            true if we should encode all data written to us, false if we should decode.
     */
    public Base32OutputStream(final OutputStream out, final boolean doEncode) {
        super(out, new Base32(false), doEncode);
    }

    /**
     * Creates a Base32OutputStream such that all data written is either Base32-encoded or Base32-decoded to the
     * original provided OutputStream.
     *
     * @param out
     *            OutputStream to wrap.
     * @param doEncode
     *            true if we should encode all data written to us, false if we should decode.
     * @param lineLength
     *            If doEncode is true, each line of encoded data will contain lineLength characters (rounded down to
     *            nearest multiple of 4). If lineLength &lt;= 0, the encoded data is not divided into lines. If doEncode
     *            is false, lineLength is ignored.
     * @param lineSeparator
     *            If doEncode is true, each line of encoded data will be terminated with this byte sequence (e.g. \r\n).
     *            If lineLength &lt;= 0, the lineSeparator is not used. If doEncode is false lineSeparator is ignored.
     */
    public Base32OutputStream(final OutputStream out, final boolean doEncode,
                              final int lineLength, final byte[] lineSeparator) {
        super(out, new Base32(lineLength, lineSeparator), doEncode);
    }

}
