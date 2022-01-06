package the.bytecode.club.bytecodeviewer.decompilers.jdgui;

import java.io.Closeable;
import java.io.PrintStream;
import org.jd.core.v1.api.printer.Printer;

public class PlainTextPrinter implements Printer, Closeable {
    protected static final String TAB = "  ";
    protected static final String NEWLINE = "\n";

    protected CommonPreferences preferences;
    protected PrintStream printStream;
    protected int maxLineNumber;
    protected int majorVersion;
    protected int minorVersion;
    protected int digitCount;
    protected String lineNumberBeginPrefix;
    protected String lineNumberEndPrefix;
    protected String unknownLineNumberPrefix;
    protected int indentationCount;
    protected boolean display;

    public PlainTextPrinter(
            CommonPreferences preferences, PrintStream printStream) {
        this.preferences = preferences;
        this.printStream = printStream;
        this.maxLineNumber = 0;
        this.majorVersion = 0;
        this.minorVersion = 0;
        this.indentationCount = 0;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //

    @Override
    public void printKeyword(String s) {
        if (this.display)
            this.printStream.append(s);
    }

    @Override
    public void printDeclaration(int type, String internalTypeName, String name, String descriptor) {
        this.printStream.append(name);
    }

    @Override
    public void printReference(int type, String internalTypeName, String name, String descriptor,
                               String ownerInternalName) {
        this.printStream.append(name);
    }

    @Override
    public void start(int maxLineNumber, int majorVersion, int minorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.indentationCount = 0;
        this.display = true;

        if (this.preferences.isShowLineNumbers()) {
            this.maxLineNumber = maxLineNumber;

            if (maxLineNumber > 0) {
                this.digitCount = 1;
                StringBuilder unknownLineNumberPrefixBuilder = new StringBuilder(" ");
                int maximum = 9;

                while (maximum < maxLineNumber) {
                    this.digitCount++;
                    unknownLineNumberPrefixBuilder.append(' ');
                    maximum = maximum * 10 + 9;
                }

                this.unknownLineNumberPrefix = unknownLineNumberPrefixBuilder.toString();
                this.lineNumberBeginPrefix = "/* ";
                this.lineNumberEndPrefix = " */ ";
            } else {
                this.unknownLineNumberPrefix = "";
                this.lineNumberBeginPrefix = "";
                this.lineNumberEndPrefix = "";
            }
        } else {
            this.maxLineNumber = 0;
            this.unknownLineNumberPrefix = "";
            this.lineNumberBeginPrefix = "";
            this.lineNumberEndPrefix = "";
        }
    }

    @Override
    public void end() {
    }

    @Override
    public void printText(String s) {
        if (this.display)
            printEscape(s);
    }

    @Override
    public void printNumericConstant(String s) {
        this.printStream.append(s);
    }

    @Override
    public void printStringConstant(String s, String s1) {
        this.printStream.append(s);
    }

    @Override
    public void indent() {
        this.indentationCount++;
    }

    @Override
    public void unindent() {
        if (this.indentationCount > 0)
            this.indentationCount--;
    }

    @Override
    public void startLine(int lineNumber) {
        if (this.maxLineNumber > 0) {
            this.printStream.append(this.lineNumberBeginPrefix);

            if (lineNumber == UNKNOWN_LINE_NUMBER) {
                this.printStream.append(this.unknownLineNumberPrefix);
            } else {
                int left = 0;

                left = printDigit(5, lineNumber, 10000, left);
                left = printDigit(4, lineNumber, 1000, left);
                left = printDigit(3, lineNumber, 100, left);
                left = printDigit(2, lineNumber, 10, left);
                this.printStream.append((char) ('0' + (lineNumber - left)));
            }

            this.printStream.append(this.lineNumberEndPrefix);
        }

        for (int i = 0; i < indentationCount; i++)
            this.printStream.append(TAB);
    }

    @Override
    public void endLine() {
        this.printStream.append(NEWLINE);
    }

    @Override
    public void extraLine(int count) {
        if (!this.preferences.isMergeEmptyLines()) {
            while (count-- > 0) {
                if (this.maxLineNumber > 0) {
                    this.printStream.append(this.lineNumberBeginPrefix);
                    this.printStream.append(this.unknownLineNumberPrefix);
                    this.printStream.append(this.lineNumberEndPrefix);
                }

                this.printStream.append(NEWLINE);
            }
        }
    }

    @Override
    public void startMarker(int i) {
    }

    @Override
    public void endMarker(int i) {
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //

    protected void printEscape(String s) {
        if (this.preferences.isUnicodeEscape()) {
            int length = s.length();

            for (int i = 0; i < length; i++) {
                char c = s.charAt(i);

                if (c == '\t') {
                    this.printStream.append('\t');
                } else if (c < 32) {
                    // Write octal format
                    this.printStream.append("\\0");
                    this.printStream.append((char) ('0' + (c >> 3)));
                    this.printStream.append((char) ('0' + (c & 0x7)));
                } else if (c > 127) {
                    // Write octal format
                    this.printStream.append("\\u");

                    int z = (c >> 12);
                    this.printStream.append((char) ((z <= 9) ? ('0' + z) : (('A' - 10) + z)));
                    z = ((c >> 8) & 0xF);
                    this.printStream.append((char) ((z <= 9) ? ('0' + z) : (('A' - 10) + z)));
                    z = ((c >> 4) & 0xF);
                    this.printStream.append((char) ((z <= 9) ? ('0' + z) : (('A' - 10) + z)));
                    z = (c & 0xF);
                    this.printStream.append((char) ((z <= 9) ? ('0' + z) : (('A' - 10) + z)));
                } else {
                    this.printStream.append(c);
                }
            }
        } else {
            this.printStream.append(s);
        }
    }

    protected int printDigit(int dcv, int lineNumber, int divisor, int left) {
        if (this.digitCount >= dcv) {
            if (lineNumber < divisor) {
                this.printStream.append(' ');
            } else {
                int e = (lineNumber - left) / divisor;
                this.printStream.append((char) ('0' + e));
                left += e * divisor;
            }
        }

        return left;
    }

    @Override
    public void close() {
        if (this.printStream != null)
            this.printStream.close();
    }
}
