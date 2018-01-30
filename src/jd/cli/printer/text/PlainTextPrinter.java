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

    public void print(byte b) {
        this.printStream.append(String.valueOf(b));
    }

    public void print(int i) {
        this.printStream.append(String.valueOf(i));
    }

    public void print(char c) {
        if (this.display)
            this.printStream.append(String.valueOf(c));
    }

    public void print(String s) {
        if (this.display)
            printEscape(s);
    }

    public void printNumeric(String s) {
        this.printStream.append(s);
    }

    public void printString(String s, String scopeInternalName) {
        this.printStream.append(s);
    }

    public void printKeyword(String s) {
        if (this.display)
            this.printStream.append(s);
    }

    public void printJavaWord(String s) {
        this.printStream.append(s);
    }

    public void printType(String internalName, String name, String scopeInternalName) {
        if (this.display)
            printEscape(name);
    }

    public void printTypeDeclaration(String internalName, String name) {
        printEscape(name);
    }

    public void printTypeImport(String internalName, String name) {
        printEscape(name);
    }

    public void printField(
            String internalName, String name,
            String descriptor, String scopeInternalName) {
        printEscape(name);
    }

    public void printFieldDeclaration(
            String internalName, String name, String descriptor) {
        printEscape(name);
    }

    public void printStaticField(
            String internalName, String name,
            String descriptor, String scopeInternalName) {
        printEscape(name);
    }

    public void printStaticFieldDeclaration(
            String internalName, String name, String descriptor) {
        printEscape(name);
    }

    public void printConstructor(
            String internalName, String name,
            String descriptor, String scopeInternalName) {
        printEscape(name);
    }

    public void printConstructorDeclaration(
            String internalName, String name, String descriptor) {
        printEscape(name);
    }

    public void printStaticConstructorDeclaration(
            String internalName, String name) {
        this.printStream.append(name);
    }

    public void printMethod(
            String internalName, String name,
            String descriptor, String scopeInternalName) {
        printEscape(name);
    }

    public void printMethodDeclaration(
            String internalName, String name, String descriptor) {
        printEscape(name);
    }

    public void printStaticMethod(
            String internalName, String name,
            String descriptor, String scopeInternalName) {
        printEscape(name);
    }

    public void printStaticMethodDeclaration(
            String internalName, String name, String descriptor) {
        printEscape(name);
    }

    public void start(int maxLineNumber, int majorVersion, int minorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.indentationCount = 0;
        this.display = true;

        if (this.preferences.isShowLineNumbers()) {
            this.maxLineNumber = maxLineNumber;

            if (maxLineNumber > 0) {
                this.digitCount = 1;
                this.unknownLineNumberPrefix = " ";
                int maximum = 9;

                while (maximum < maxLineNumber) {
                    this.digitCount++;
                    this.unknownLineNumberPrefix += ' ';
                    maximum = maximum * 10 + 9;
                }

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

    public void end() {
    }

    public void indent() {
        this.indentationCount++;
    }

    public void desindent() {
        if (this.indentationCount > 0)
            this.indentationCount--;
    }

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

    public void endOfLine() {
        this.printStream.append(NEWLINE);
    }

    public void extraLine(int count) {
        if (this.preferences.isMergeEmptyLines() == false) {
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

    public void startOfComment() {
    }

    public void endOfComment() {
    }

    public void startOfJavadoc() {
    }

    public void endOfJavadoc() {
    }

    public void startOfXdoclet() {
    }

    public void endOfXdoclet() {
    }

    public void startOfError() {
    }

    public void endOfError() {
    }

    public void startOfImportStatements() {
    }

    public void endOfImportStatements() {
    }

    public void startOfTypeDeclaration(String internalPath) {
    }

    public void endOfTypeDeclaration() {
    }

    public void startOfAnnotationName() {
    }

    public void endOfAnnotationName() {
    }

    public void startOfOptionalPrefix() {
        if (this.preferences.isShowPrefixThis() == false)
            this.display = false;
    }

    public void endOfOptionalPrefix() {
        this.display = true;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //

    public void debugStartOfLayoutBlock() {
    }

    public void debugEndOfLayoutBlock() {
    }

    public void debugStartOfSeparatorLayoutBlock() {
    }

    public void debugEndOfSeparatorLayoutBlock(int min, int value, int max) {
    }

    public void debugStartOfStatementsBlockLayoutBlock() {
    }

    public void debugEndOfStatementsBlockLayoutBlock(int min, int value, int max) {
    }

    public void debugStartOfInstructionBlockLayoutBlock() {
    }

    public void debugEndOfInstructionBlockLayoutBlock() {
    }

    public void debugStartOfCommentDeprecatedLayoutBlock() {
    }

    public void debugEndOfCommentDeprecatedLayoutBlock() {
    }

    public void debugMarker(String marker) {
    }

    public void debugStartOfCaseBlockLayoutBlock() {
    }

    public void debugEndOfCaseBlockLayoutBlock() {
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //

    protected void printEscape(String s) {
        if (this.preferences.isUnicodeEscape()) {
            int length = s.length();

            for (int i = 0; i < length; i++) {
                char c = s.charAt(i);

                if (c == '\t') {
                    this.printStream.append(c);
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
