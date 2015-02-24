
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.commons.compiler.ICookable;
import org.codehaus.commons.compiler.Location;
import org.codehaus.commons.compiler.WarningHandler;
import org.codehaus.janino.util.TeeReader;

/**
 * Splits up a character stream into tokens and returns them as
 * {@link java.lang.String String} objects.
 * <p>
 * The <code>optionalFileName</code> parameter passed to many
 * constructors should point
 */
@SuppressWarnings({ "rawtypes", "unchecked" }) public
class Scanner {

    // Public Scanners that read from a file.

    /**
     * Set up a scanner that reads tokens from the given file in the default charset.
     * <p>
     * <b>This method is deprecated because it leaves the input file open.</b>
     *
     * @deprecated // SUPPRESS CHECKSTYLE MissingDeprecated
     */
    @Deprecated public
    Scanner(String fileName) throws CompileException, IOException {
        this(
            fileName,                     // optionalFileName
            new FileInputStream(fileName) // is
        );
    }

    /**
     * Set up a scanner that reads tokens from the given file in the given encoding.
     * <p>
     * <b>This method is deprecated because it leaves the input file open.</b>
     *
     * @deprecated // SUPPRESS CHECKSTYLE MissingDeprecated
     */
    @Deprecated public
    Scanner(String fileName, String encoding) throws CompileException, IOException {
        this(
            fileName,                      // optionalFileName
            new FileInputStream(fileName), // is
            encoding                       // optionalEncoding
        );
    }

    /**
     * Set up a scanner that reads tokens from the given file in the platform
     * default encoding.
     * <p>
     * <b>This method is deprecated because it leaves the input file open.</b>
     *
     * @deprecated // SUPPRESS CHECKSTYLE MissingDeprecated
     */
    @Deprecated public
    Scanner(File file) throws CompileException, IOException {
        this(
            file.getAbsolutePath(),    // optionalFileName
            new FileInputStream(file), // is
            null                       // optionalEncoding
        );
    }

    /**
     * Set up a scanner that reads tokens from the given file in the given encoding.
     * <p>
     * <b>This method is deprecated because it leaves the input file open.</b>
     *
     * @deprecated // SUPPRESS CHECKSTYLE MissingDeprecated
     */
    @Deprecated public
    Scanner(File file, String optionalEncoding) throws CompileException, IOException {
        this(
            file.getAbsolutePath(),    // optionalFileName
            new FileInputStream(file), // is
            optionalEncoding           // optionalEncoding
        );
    }

    // Public Scanners that read from an InputStream

    /**
     * Set up a scanner that reads tokens from the given
     * {@link InputStream} in the platform default encoding.
     * <p>
     * The <code>fileName</code> is solely used for reporting in thrown
     * exceptions.
     */
    public
    Scanner(String optionalFileName, InputStream is) throws CompileException, IOException {
        this(
            optionalFileName,
            new InputStreamReader(is), // in
            (short) 1,                 // initialLineNumber
            (short) 0                  // initialColumnNumber
        );
    }

    /**
     * Set up a scanner that reads tokens from the given
     * {@link InputStream} with the given <code>optionalEncoding</code>
     * (<code>null</code> means platform default encoding).
     * <p>
     * The <code>optionalFileName</code> is used for reporting errors during
     * compilation and for source level debugging, and should name an existing
     * file. If <code>null</code> is passed, and the system property
     * <code>org.codehaus.janino.source_debugging.enable</code> is set to "true", then
     * a temporary file in <code>org.codehaus.janino.source_debugging.dir</code> or the
     * system's default temp dir is created in order to make the source code
     * available to a debugger.
     */
    public
    Scanner(String optionalFileName, InputStream is, String optionalEncoding) throws CompileException, IOException {
        this(
            optionalFileName,                  // optionalFileName
            (                                  // in
                optionalEncoding == null
                ? new InputStreamReader(is)
                : new InputStreamReader(is, optionalEncoding)
            ),
            (short) 1,                         // initialLineNumber
            (short) 0                          // initialColumnNumber
        );
    }

    // Public Scanners that read from a Reader.

    /**
     * Set up a scanner that reads tokens from the given
     * {@link Reader}.
     * <p>
     * The <code>optionalFileName</code> is used for reporting errors during
     * compilation and for source level debugging, and should name an existing
     * file. If <code>null</code> is passed, and the system property
     * <code>org.codehaus.janino.source_debugging.enable</code> is set to "true", then
     * a temporary file in <code>org.codehaus.janino.source_debugging.dir</code> or the
     * system's default temp dir is created in order to make the source code
     * available to a debugger.
     */
    public
    Scanner(String optionalFileName, Reader in) throws CompileException, IOException {
        this(
            optionalFileName, // optionalFileName
            in,               // in
            (short) 1,        // initialLineNumber
            (short) 0         // initialColumnNumber
        );
    }

    /** Creates a {@link Scanner} that counts lines and columns from non-default initial values. */
    public
    Scanner(
        String optionalFileName,
        Reader in,
        short  initialLineNumber,        // "1" is a good idea
        short  initialColumnNumber       // "0" is a good idea
    ) throws CompileException, IOException {

        // Debugging on source code level is only possible if the code comes from
        // a "real" Java source file which the debugger can read. If this is not the
        // case, and we absolutely want source code level debugging, then we write
        // a verbatim copy of the source code into a temporary file in the system
        // temp directory.
        // This behavior is controlled by the two system properties
        //     org.codehaus.janino.source_debugging.enable
        //     org.codehaus.janino.source_debugging.dir
        // JANINO is designed to compile in memory to save the overhead of disk
        // I/O, so writing this file is only recommended for source code level
        // debugging purposes.
        if (optionalFileName == null && Boolean.getBoolean(ICookable.SYSTEM_PROPERTY_SOURCE_DEBUGGING_ENABLE)) {
            String dirName       = System.getProperty(ICookable.SYSTEM_PROPERTY_SOURCE_DEBUGGING_DIR);
            File   dir           = dirName == null ? null : new File(dirName);
            File   temporaryFile = File.createTempFile("janino", ".java", dir);
            temporaryFile.deleteOnExit();
            in = new TeeReader(
                in,                            // in
                new FileWriter(temporaryFile), // out
                true                           // closeWriterOnEOF
            );
            optionalFileName = temporaryFile.getAbsolutePath();
        }

        this.optionalFileName     = optionalFileName;
        this.in                   = new UnicodeUnescapeReader(in);
        this.nextCharLineNumber   = initialLineNumber;
        this.nextCharColumnNumber = initialColumnNumber;

        this.readNextChar();
    }

    /** @return The file name optionally passed to the constructor */
    public String
    getFileName() { return this.optionalFileName; }

    /**
     * Closes the character source (file, {@link InputStream}, {@link Reader}) associated with this object. The results
     * of future calls to {@link #produce()} are undefined.
     *
     * @deprecated This method is deprecated, because the concept described above is confusing. An application should
     *             close the underlying {@link InputStream} or {@link Reader} itself.</b>
     */
    @Deprecated public void
    close() throws IOException { this.in.close(); }

    /**
     * Get the text of the doc comment (a.k.a. "JAVADOC comment") preceeding
     * the next token.
     * @return <code>null</code> if the next token is not preceeded by a doc comment
     */
    public String
    doc() {
        String s = this.docComment;
        this.docComment = null;
        return s;
    }

    /** @return The {@link Location} of the next character */
    public Location
    location() {
        return new Location(this.optionalFileName, this.nextCharLineNumber, this.nextCharColumnNumber);
    }

    /** Representation of a Java&trade; token. */
    public final
    class Token {
        private final String optionalFileName;
        private final short  lineNumber;
        private final short  columnNumber;
        private Location     location;

        /** The type of this token; legal values are the various public constant declared in this class. */
        public final int type;

        /** Indication of the 'end-of-input' condition. */
        public static final int EOF = 0;

        /** The token represents an identifier. */
        public static final int IDENTIFIER = 1;

        /** The token represents a keyword. */
        public static final int KEYWORD = 2;

        /**
         * The token represents an integer literal; its {@link #value} is the text of the integer literal exactly as it
         * appears in the source code.
         */
        public static final int INTEGER_LITERAL = 3;

        /**
         * The token represents a floating-point literal; its {@link #value} is the text of the floating-point literal
         * exactly as it appears in the source code.
         */
        public static final int FLOATING_POINT_LITERAL = 4;

        /** The token represents a boolean literal; its {@link #value} is either 'true' or 'false'. */
        public static final int BOOLEAN_LITERAL = 5;

        /**
         * The token represents a character literal; its {@link #value} is the text of the character literal exactly as
         * it appears in the source code (including the single quotes around it).
         */
        public static final int CHARACTER_LITERAL = 6;

        /**
         * The token represents a string literal; its {@link #value} is the text of the string literal exactly as it
         * appears in the source code (including the double quotes around it).
         */
        public static final int STRING_LITERAL = 7;

        /** The token represents the {@code null} literal; its {@link #value} is 'null'. */
        public static final int NULL_LITERAL = 8;

        /** The token represents an operator; its {@link #value} is exactly the particular operator (e.g. "<<<="). */
        public static final int OPERATOR = 9;

        /** The text of the token exactly as it appears in the source code. */
        public final String value;

        private
        Token(int type, String value) {
            this.optionalFileName = Scanner.this.optionalFileName;
            this.lineNumber       = Scanner.this.tokenLineNumber;
            this.columnNumber     = Scanner.this.tokenColumnNumber;
            this.type             = type;
            this.value            = value;
        }

        /** @return The location of the first character of this token */
        public Location
        getLocation() {
            if (this.location == null) {
                this.location = new Location(this.optionalFileName, this.lineNumber, this.columnNumber);
            }
            return this.location;
        }

        @Override public String
        toString() { return this.value; }
    }

    /**
     * Preduces and returns the next token. Notice that end-of-input is <i>not</i> signalized with a {@code null}
     * product, but by the special {@link Token#EOF} token.
     */
    public Token
    produce() throws CompileException, IOException {
        if (this.docComment != null) {
            this.warning("MDC", "Misplaced doc comment", this.location());
            this.docComment = null;
        }

        // Skip whitespace and process comments.
        int           state = 0;
        StringBuilder dcsb  = null; // For doc comment

        PROCESS_COMMENTS:
        for (;;) {
            switch (state) {

            case 0: // Outside any comment
                if (this.nextChar == -1) {
                    return new Token(Token.EOF, "EOF");
                } else
                if (Character.isWhitespace((char) this.nextChar)) {
                    ;
                } else
                if (this.nextChar == '/') {
                    state = 1;
                } else
                {
                    break PROCESS_COMMENTS;
                }
                break;

            case 1:  // After "/"
                if (this.nextChar == -1) {
                    return new Token(Token.OPERATOR, "/");
                } else
                if (this.nextChar == '=') {
                    this.readNextChar();
                    return new Token(Token.OPERATOR, "/=");
                } else
                if (this.nextChar == '/') {
                    state = 2;
                } else
                if (this.nextChar == '*') {
                    state = 3;
                } else
                {
                    return new Token(Token.OPERATOR, "/");
                }
                break;

            case 2: // After "//..."
                if (this.nextChar == -1) {
                    return new Token(Token.EOF, "EOF");
                } else
                if (this.nextChar == '\r' || this.nextChar == '\n') {
                    state = 0;
                } else
                {
                    ;
                }
                break;

            case 3: // After "/*"
                if (this.nextChar == -1) {
                    throw new CompileException("EOF in traditional comment", this.location());
                } else
                if (this.nextChar == '*') {
                    state = 4;
                } else
                {
                    state = 9;
                }
                break;

            case 4: // After "/**"
                if (this.nextChar == -1) {
                    throw new CompileException("EOF in doc comment", this.location());
                } else
                if (this.nextChar == '/') {
                    state = 0;
                } else
                {
                    if (this.docComment != null) {
                        this.warning(
                            "MDC",
                            "Multiple doc comments",
                            new Location(this.optionalFileName, this.nextCharLineNumber, this.nextCharColumnNumber)
                        );
                    }
                    dcsb  = new StringBuilder().append((char) this.nextChar);
                    state = (
                        (this.nextChar == '\r' || this.nextChar == '\n') ? 6
                        : this.nextChar == '*' ? 8
                        : 5
                    );
                }
                break;

            case 5: // After "/**..."
                if (this.nextChar == -1) {
                    throw new CompileException("EOF in doc comment", this.location());
                } else
                if (this.nextChar == '*') {
                    state = 8;
                } else
                if (this.nextChar == '\r' || this.nextChar == '\n') {
                    dcsb.append((char) this.nextChar);
                    state = 6;
                } else
                {
                    dcsb.append((char) this.nextChar);
                }
                break;

            case 6: // After "/**...\n"
                if (this.nextChar == -1) {
                    throw new CompileException("EOF in doc comment", this.location());
                } else
                if (this.nextChar == '*') {
                    state = 7;
                } else
                if (this.nextChar == '\r' || this.nextChar == '\n') {
                    dcsb.append((char) this.nextChar);
                } else
                if (this.nextChar == ' ' || this.nextChar == '\t') {
                    ;
                } else
                {
                    dcsb.append((char) this.nextChar);
                    state = 5;
                }
                break;

            case 7: // After "/**...\n *"
                if (this.nextChar == -1) {
                    throw new CompileException("EOF in doc comment", this.location());
                } else
                if (this.nextChar == '*') {
                    ;
                } else
                if (this.nextChar == '/') {
                    this.docComment = dcsb.toString();
                    state           = 0;
                } else
                {
                    dcsb.append((char) this.nextChar);
                    state = 5;
                }
                break;

            case 8: // After "/**...*"
                if (this.nextChar == -1) {
                    throw new CompileException("EOF in doc comment", this.location());
                } else
                if (this.nextChar == '/') {
                    this.docComment = dcsb.toString();
                    state           = 0;
                } else
                if (this.nextChar == '*') {
                    dcsb.append('*');
                } else
                {
                    dcsb.append('*');
                    dcsb.append((char) this.nextChar);
                    state = 5;
                }
                break;

            case 9: // After "/*..."
                if (this.nextChar == -1) {
                    throw new CompileException("EOF in traditional comment", this.location());
                } else
                if (this.nextChar == '*') {
                    state = 10;
                } else
                {
                    ;
                }
                break;

            case 10: // After "/*...*"
                if (this.nextChar == -1) {
                    throw new CompileException("EOF in traditional comment", this.location());
                } else
                if (this.nextChar == '/') {
                    state = 0;
                } else
                if (this.nextChar == '*') {
                    ;
                } else
                {
                    state = 9;
                }
                break;

            default:
                throw new JaninoRuntimeException(Integer.toString(state));
            }
            this.readNextChar();
        }

        /*
         * Whitespace and comments are now skipped; "nextChar" is definitely
         * the first character of the token.
         */
        this.tokenLineNumber   = this.nextCharLineNumber;
        this.tokenColumnNumber = this.nextCharColumnNumber;

        // Scan identifier.
        if (Character.isJavaIdentifierStart((char) this.nextChar)) {
            StringBuilder sb = new StringBuilder();
            sb.append((char) this.nextChar);
            for (;;) {
                this.readNextChar();
                if (this.nextChar == -1 || !Character.isJavaIdentifierPart((char) this.nextChar)) break;
                sb.append((char) this.nextChar);
            }
            String s = sb.toString();
            if ("true".equals(s))  return new Token(Token.BOOLEAN_LITERAL, "true");
            if ("false".equals(s)) return new Token(Token.BOOLEAN_LITERAL, "false");
            if ("null".equals(s))  return new Token(Token.NULL_LITERAL,    "null");
            {
                String v = (String) Scanner.JAVA_KEYWORDS.get(s);
                if (v != null) return new Token(Token.KEYWORD, v);
            }
            return new Token(Token.IDENTIFIER, s);
        }

        // Scan numeric literal.
        if (Character.isDigit((char) this.nextChar)) {
            return this.scanNumericLiteral(false);
        }

        // A "." is special: Could either be a floating-point constant like ".001", or the "."
        // operator.
        if (this.nextChar == '.') {
            this.readNextChar();
            if (Character.isDigit((char) this.nextChar)) {
                return this.scanNumericLiteral(true);
            } else {
                return new Token(Token.OPERATOR, ".");
            }
        }

        // Scan string literal.
        if (this.nextChar == '"') {
            StringBuilder sb = new StringBuilder("\"");
            this.readNextChar();
            while (this.nextChar != '"') {
                this.scanLiteralCharacter(sb);
            }
            this.readNextChar();
            return new Token(Token.STRING_LITERAL, sb.append('"').toString());
        }

        // Scan character literal.
        if (this.nextChar == '\'') {
            this.readNextChar();
            if (this.nextChar == '\'') {
                throw new CompileException(
                    "Single quote must be backslash-escaped in character literal",
                    this.location()
                );
            }

            StringBuilder sb = new StringBuilder("'");
            this.scanLiteralCharacter(sb);
            if (this.nextChar != '\'') throw new CompileException("Closing single quote missing", this.location());
            this.readNextChar();

            return new Token(Token.CHARACTER_LITERAL, sb.append('\'').toString());
        }

        // Scan separator / operator.
        {
            String v = (String) Scanner.JAVA_OPERATORS.get(new String(new char[] { (char) this.nextChar }));
            if (v != null) {
                for (;;) {
                    this.readNextChar();
                    String v2 = (String) (
                        this.expectGreater
                        ? Scanner.JAVA_OPERATORS_EXPECT_GREATER
                        : Scanner.JAVA_OPERATORS
                    ).get(v + (char) this.nextChar);
                    if (v2 == null) return new Token(Token.OPERATOR, v);
                    v = v2;
                }
            }
        }

        throw new CompileException(
            "Invalid character input \"" + (char) this.nextChar + "\" (character code " + this.nextChar + ")",
            this.location()
        );
    }

    /** @return Whether the scanner is currently in 'expect greater' mode */
    public boolean
    getExpectGreater() { return this.expectGreater; }

    /**
     * Sets or resets the 'expect greater' mode.
     *
     * @return Whether the 'expect greater' mode was previously active
     */
    public boolean
    setExpectGreater(boolean value) {
        boolean tmp = this.expectGreater;
        this.expectGreater = value;
        return tmp;
    }

    private Token
    scanNumericLiteral(boolean hadDecimalPoint) throws CompileException, IOException {
        StringBuilder sb    = hadDecimalPoint ? new StringBuilder(".") : new StringBuilder();
        int           state = hadDecimalPoint ? 2 : 0;
        for (;;) {
            switch (state) {

            case 0: // First character.
                if (this.nextChar == '0') {
                    sb.append('0');
                    state = 6;
                } else
                if (Character.isDigit((char) this.nextChar)) {
                    sb.append((char) this.nextChar);
                    state = 1;
                } else
                {
                    throw new CompileException(
                        "Numeric literal begins with invalid character '" + (char) this.nextChar + "'",
                        this.location()
                    );
                }
                break;

            case 1: // Decimal digits.
                if (Character.isDigit((char) this.nextChar)) {
                    sb.append((char) this.nextChar);
                } else
                if (this.nextChar == 'l' || this.nextChar == 'L') {
                    sb.append((char) this.nextChar);
                    this.readNextChar();
                    return new Token(Token.INTEGER_LITERAL, sb.toString());
                } else
                if (this.nextChar == 'f' || this.nextChar == 'F' || this.nextChar == 'd' || this.nextChar == 'D') {
                    sb.append((char) this.nextChar);
                    this.readNextChar();
                    return new Token(Token.FLOATING_POINT_LITERAL, sb.toString());
                } else
                if (this.nextChar == '.') {
                    sb.append('.');
                    state = 2;
                } else
                if (this.nextChar == 'E' || this.nextChar == 'e') {
                    sb.append('E');
                    state = 3;
                } else
                {
                    return new Token(Token.INTEGER_LITERAL, sb.toString());
                }
                break;

            case 2: // After decimal point.
                if (Character.isDigit((char) this.nextChar)) {
                    sb.append((char) this.nextChar);
                } else
                if (this.nextChar == 'e' || this.nextChar == 'E') {
                    sb.append('E');
                    state = 3;
                } else
                if (this.nextChar == 'f' || this.nextChar == 'F' || this.nextChar == 'd' || this.nextChar == 'D') {
                    sb.append((char) this.nextChar);
                    this.readNextChar();
                    return new Token(Token.FLOATING_POINT_LITERAL, sb.toString());
                } else
                {
                    return new Token(Token.FLOATING_POINT_LITERAL, sb.toString());
                }
                break;

            case 3: // Read exponent.
                if (Character.isDigit((char) this.nextChar)) {
                    sb.append((char) this.nextChar);
                    state = 5;
                } else
                if (this.nextChar == '-' || this.nextChar == '+') {
                    sb.append((char) this.nextChar);
                    state = 4;
                } else
                {
                    throw new CompileException("Exponent missing after \"E\"", this.location());
                }
                break;

            case 4: // After exponent sign.
                if (Character.isDigit((char) this.nextChar)) {
                    sb.append((char) this.nextChar);
                    state = 5;
                } else
                {
                    throw new CompileException("Exponent missing after 'E' and sign", this.location());
                }
                break;

            case 5: // After first exponent digit.
                if (Character.isDigit((char) this.nextChar)) {
                    sb.append((char) this.nextChar);
                } else
                if (this.nextChar == 'f' || this.nextChar == 'F' || this.nextChar == 'd' || this.nextChar == 'D') {
                    sb.append((char) this.nextChar);
                    this.readNextChar();
                    return new Token(Token.FLOATING_POINT_LITERAL, sb.toString());
                } else
                {
                    return new Token(Token.FLOATING_POINT_LITERAL, sb.toString());
                }
                break;

            case 6: // After leading zero
                if ("01234567".indexOf(this.nextChar) != -1) {
                    sb.append((char) this.nextChar);
                    state = 7;
                } else
                if (this.nextChar == 'l' || this.nextChar == 'L') {
                    sb.append((char) this.nextChar);
                    this.readNextChar();
                    return new Token(Token.INTEGER_LITERAL, sb.toString());
                } else
                if (this.nextChar == 'f' || this.nextChar == 'F' || this.nextChar == 'd' || this.nextChar == 'D') {
                    sb.append((char) this.nextChar);
                    this.readNextChar();
                    return new Token(Token.FLOATING_POINT_LITERAL, sb.toString());
                } else
                if (this.nextChar == '.') {
                    sb.append('.');
                    state = 2;
                } else
                if (this.nextChar == 'E' || this.nextChar == 'e') {
                    sb.append((char) this.nextChar);
                    state = 3;
                } else
                if (this.nextChar == 'x' || this.nextChar == 'X') {
                    sb.append((char) this.nextChar);
                    state = 8;
                } else
                {
                    return new Token(Token.INTEGER_LITERAL, "0");
                }
                break;

            case 7: // In octal literal.
                if ("01234567".indexOf(this.nextChar) != -1) {
                    sb.append((char) this.nextChar);
                } else
                if (this.nextChar == '8' || this.nextChar == '9') {
                    throw new CompileException(
                        "Digit '" + (char) this.nextChar + "' not allowed in octal literal",
                        this.location()
                    );
                } else
                if (this.nextChar == 'l' || this.nextChar == 'L') {
                    // Octal long literal.
                    sb.append((char) this.nextChar);
                    this.readNextChar();
                    return new Token(Token.INTEGER_LITERAL, sb.toString());
                } else
                {
                    // Octal int literal
                    return new Token(Token.INTEGER_LITERAL, sb.toString());
                }
                break;

            case 8: // After '0x'.
                if (Character.digit((char) this.nextChar, 16) != -1) {
                    sb.append((char) this.nextChar);
                    state = 9;
                } else
                {
                    throw new CompileException("Hex digit expected after \"0x\"", this.location());
                }
                break;

            case 9: // After first hex digit.
                if (Character.digit((char) this.nextChar, 16) != -1) {
                    sb.append((char) this.nextChar);
                } else
                if (this.nextChar == 'l' || this.nextChar == 'L') {
                    // Hex long literal
                    sb.append((char) this.nextChar);
                    this.readNextChar();
                    return new Token(Token.INTEGER_LITERAL, sb.toString());
                } else
                {
                    // Hex int literal
                    return new Token(Token.INTEGER_LITERAL, sb.toString());
                }
                break;

            default:
                throw new JaninoRuntimeException(Integer.toString(state));
            }
            this.readNextChar();
        }
    }

    /** Scans the next literal character into a {@link StringBuilder}. */
    private void
    scanLiteralCharacter(StringBuilder sb) throws CompileException, IOException {
        if (this.nextChar == -1) throw new CompileException("EOF in literal", this.location());

        if (this.nextChar == '\r' || this.nextChar == '\n') {
            throw new CompileException("Line break in literal not allowed", this.location());
        }

        if (this.nextChar != '\\') {

            // Not an escape sequence.
            sb.append((char) this.nextChar);
            this.readNextChar();
            return;
        }

        // JLS7 3.10.6: Escape sequences for character and string literals.
        sb.append('\\');
        this.readNextChar();

        {
            int idx = "btnfr\"'\\".indexOf(this.nextChar);
            if (idx != -1) {

                // "\t" and friends.
                sb.append((char) this.nextChar);
                this.readNextChar();
                return;
            }
        }

        {
            int idx = "01234567".indexOf(this.nextChar);
            if (idx != -1) {

                // Octal escapes: "\0" through "\3ff".
                char firstChar = (char) this.nextChar;
                sb.append(firstChar);
                this.readNextChar();

                idx = "01234567".indexOf(this.nextChar);
                if (idx == -1) return;
                sb.append((char) this.nextChar);
                this.readNextChar();

                idx = "01234567".indexOf(this.nextChar);
                if (idx == -1) return;
                if ("0123".indexOf(firstChar) == -1) {
                    throw new CompileException("Invalid octal escape", this.location());
                }
                sb.append((char) this.nextChar);
                this.readNextChar();
                return;
            }
        }

        throw new CompileException("Invalid escape sequence", this.location());
    }

    // Read one character and store in "nextChar".
    private void
    readNextChar() throws IOException, CompileException {
        try {
            this.nextChar = this.in.read();
        } catch (UnicodeUnescapeException ex) {
            throw new CompileException(ex.getMessage(), this.location(), ex);
        }
        if (this.nextChar == '\r') {
            ++this.nextCharLineNumber;
            this.nextCharColumnNumber = 0;
            this.crLfPending          = true;
        } else
        if (this.nextChar == '\n') {
            if (this.crLfPending) {
                this.crLfPending = false;
            } else {
                ++this.nextCharLineNumber;
                this.nextCharColumnNumber = 0;
            }
        } else
        {
            ++this.nextCharColumnNumber;
        }
//System.out.println("'" + (char) nextChar + "' = " + (int) nextChar);
    }

    private final String optionalFileName;
    private final Reader in;
    private int          nextChar = -1; // Always valid (one character read-ahead).
    private boolean      crLfPending;
    private short        nextCharLineNumber;
    private short        nextCharColumnNumber;

    /** Line number of the previously produced token (typically starting at one). */
    private short tokenLineNumber;

    /**
     * Column number of the first character of the previously produced token (1 if token is immediately preceded by a
     * line break).
     */
    private short tokenColumnNumber;

    /** The optional JAVADOC comment preceding the {@link #nextToken}. */
    private String docComment;

    /**
     * Whether the scanner is in 'expect greater' mode: If so, it parses character sequences like ">>>=" as
     * ">", ">", ">", "=".
     */
    private boolean expectGreater;

    private static final Map<String, String> JAVA_KEYWORDS = new HashMap();
    static {
        String[] ks = {
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue",
            "default", "do", "double", "else", "extends", "final", "finally", "float", "for", "goto", "if",
            "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private",
            "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this",
            "throw", "throws", "transient", "try", "void", "volatile", "while",
        };
        for (int i = 0; i < ks.length; ++i) Scanner.JAVA_KEYWORDS.put(ks[i], ks[i]);
    }
    private static final Map<String, String> JAVA_OPERATORS                = new HashMap();
    private static final Map<String, String> JAVA_OPERATORS_EXPECT_GREATER = new HashMap();
    static {
        String[] ops = {
            // Separators:
            "(", ")", "{", "}", "[", "]", ";", ",", ".", "@",
            // Operators:
            "=",  ">",  "<",  "!",  "~",  "?",  ":",
            "==", "<=", ">=", "!=", "&&", "||", "++", "--",
            "+",  "-",  "*",  "/",  "&",  "|",  "^",  "%",  "<<",  ">>",  ">>>",
            "+=", "-=", "*=", "/=", "&=", "|=", "^=", "%=", "<<=", ">>=", ">>>=",
        };
        for (String op : ops) {
            Scanner.JAVA_OPERATORS.put(op, op);
            if (!op.startsWith(">>")) Scanner.JAVA_OPERATORS_EXPECT_GREATER.put(op, op);
        }
    }

    /**
     * By default, warnings are discarded, but an application my install a
     * {@link WarningHandler}.
     * <p>
     * Notice that there is no <code>Scanner.setErrorHandler()</code> method, but scan errors
     * always throw a {@link CompileException}. The reason being is that there is no reasonable
     * way to recover from scan errors and continue scanning, so there is no need to install
     * a custom scan error handler.
     *
     * @param optionalWarningHandler <code>null</code> to indicate that no warnings be issued
     */
    public void
    setWarningHandler(WarningHandler optionalWarningHandler) {
        this.optionalWarningHandler = optionalWarningHandler;
    }

    // Used for elaborate warning handling.
    private WarningHandler optionalWarningHandler;

    /**
     * Issues a warning with the given message and location and returns. This is done through
     * a {@link WarningHandler} that was installed through
     * {@link #setWarningHandler(WarningHandler)}.
     * <p>
     * The <code>handle</code> argument qulifies the warning and is typically used by
     * the {@link WarningHandler} to suppress individual warnings.
     * @throws CompileException
     */
    private void
    warning(String handle, String message, Location optionalLocation) throws CompileException {
        if (this.optionalWarningHandler != null) {
            this.optionalWarningHandler.handleWarning(handle, message, optionalLocation);
        }
    }
}
