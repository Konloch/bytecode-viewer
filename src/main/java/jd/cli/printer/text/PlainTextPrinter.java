package jd.cli.printer.text;

import java.io.PrintStream;
import jd.cli.preferences.CommonPreferences;
import jd.core.model.instruction.bytecode.instruction.Instruction;
import jd.core.printer.Printer;

public class PlainTextPrinter implements Printer {
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
    public void print(byte b) {
        this.printStream.append(String.valueOf(b));
    }

    @Override
    public void print(int i) {
        this.printStream.append(String.valueOf(i));
    }

    @Override
    public void print(char c) {
        if (this.display)
            this.printStream.append(String.valueOf(c));
    }

    @Override
    public void print(String s) {
        if (this.display)
            printEscape(s);
    }

    @Override
    public void printNumeric(String s) {
        this.printStream.append(s);
    }

    @Override
    public void printString(String s, String scopeInternalName) {
        this.printStream.append(s);
    }

    @Override
    public void printKeyword(String s) {
        if (this.display)
            this.printStream.append(s);
    }

    @Override
    public void printJavaWord(String s) {
        this.printStream.append(s);
    }

    @Override
    public void printType(String internalName, String name, String scopeInternalName) {
        if (this.display)
            printEscape(name);
    }

    @Override
    public void printTypeDeclaration(String internalName, String name) {
        printEscape(name);
    }

    @Override
    public void printTypeImport(String internalName, String name) {
        printEscape(name);
    }

    @Override
    public void printField(
            String internalName, String name,
            String descriptor, String scopeInternalName) {
        printEscape(name);
    }

    @Override
    public void printFieldDeclaration(
            String internalName, String name, String descriptor) {
        printEscape(name);
    }

    @Override
    public void printStaticField(
            String internalName, String name,
            String descriptor, String scopeInternalName) {
        printEscape(name);
    }

    @Override
    public void printStaticFieldDeclaration(
            String internalName, String name, String descriptor) {
        printEscape(name);
    }

    @Override
    public void printConstructor(
            String internalName, String name,
            String descriptor, String scopeInternalName) {
        printEscape(name);
    }

    @Override
    public void printConstructorDeclaration(
            String internalName, String name, String descriptor) {
        printEscape(name);
    }

    @Override
    public void printStaticConstructorDeclaration(
            String internalName, String name) {
        this.printStream.append(name);
    }

    @Override
    public void printMethod(
            String internalName, String name,
            String descriptor, String scopeInternalName) {
        printEscape(name);
    }

    @Override
    public void printMethodDeclaration(
            String internalName, String name, String descriptor) {
        printEscape(name);
    }

    @Override
    public void printStaticMethod(
            String internalName, String name,
            String descriptor, String scopeInternalName) {
        printEscape(name);
    }

    @Override
    public void printStaticMethodDeclaration(
            String internalName, String name, String descriptor) {
        printEscape(name);
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
    public void indent() {
        this.indentationCount++;
    }

    @Override
    public void desindent() {
        if (this.indentationCount > 0)
            this.indentationCount--;
    }

    @Override
    public void startOfLine(int lineNumber) {
        if (this.maxLineNumber > 0) {
            this.printStream.append(this.lineNumberBeginPrefix);

            if (lineNumber == Instruction.UNKNOWN_LINE_NUMBER) {
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
    public void endOfLine() {
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
    public void startOfComment() {
    }

    @Override
    public void endOfComment() {
    }

    @Override
    public void startOfJavadoc() {
    }

    @Override
    public void endOfJavadoc() {
    }

    @Override
    public void startOfXdoclet() {
    }

    @Override
    public void endOfXdoclet() {
    }

    @Override
    public void startOfError() {
    }

    @Override
    public void endOfError() {
    }

    @Override
    public void startOfImportStatements() {
    }

    @Override
    public void endOfImportStatements() {
    }

    @Override
    public void startOfTypeDeclaration(String internalPath) {
    }

    @Override
    public void endOfTypeDeclaration() {
    }

    @Override
    public void startOfAnnotationName() {
    }

    @Override
    public void endOfAnnotationName() {
    }

    @Override
    public void startOfOptionalPrefix() {
        if (!this.preferences.isShowPrefixThis())
            this.display = false;
    }

    @Override
    public void endOfOptionalPrefix() {
        this.display = true;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //

    @Override
    public void debugStartOfLayoutBlock() {
    }

    @Override
    public void debugEndOfLayoutBlock() {
    }

    @Override
    public void debugStartOfSeparatorLayoutBlock() {
    }

    @Override
    public void debugEndOfSeparatorLayoutBlock(int min, int value, int max) {
    }

    @Override
    public void debugStartOfStatementsBlockLayoutBlock() {
    }

    @Override
    public void debugEndOfStatementsBlockLayoutBlock(int min, int value, int max) {
    }

    @Override
    public void debugStartOfInstructionBlockLayoutBlock() {
    }

    @Override
    public void debugEndOfInstructionBlockLayoutBlock() {
    }

    @Override
    public void debugStartOfCommentDeprecatedLayoutBlock() {
    }

    @Override
    public void debugEndOfCommentDeprecatedLayoutBlock() {
    }

    @Override
    public void debugMarker(String marker) {
    }

    @Override
    public void debugStartOfCaseBlockLayoutBlock() {
    }

    @Override
    public void debugEndOfCaseBlockLayoutBlock() {
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
}
