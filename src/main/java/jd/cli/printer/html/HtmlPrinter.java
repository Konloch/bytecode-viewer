package jd.cli.printer.html;

import java.io.PrintStream;

import jd.cli.util.VersionUtil;
import jd.core.CoreConstants;
import jd.core.printer.Printer;

/*
 * CSS
 * .javacode{font-size:11px}
 * .javacode .linenumber, .javacode .l, i{color:#3f7f5f}
 * .javacode .keyword, .javacode .k, b{color:#7f0055;font-weight:bold}
 * .javacode .comment, .javacode .t, cite{color:#3f7f5f}
 * .javacode .javadoc, .javacode .d, dfn{color:#3f5fbf}
 * .javacode .error, .javacode .e, span{color:#ff0000}
 * .javacode .annotationname, .javacode .a, del{color:#646464}
 * .javacode .constant, .javacode .c, u{color:#2a00ff}
 * .javacode .field, .javacode .f, var{color:#0000c0}
 * .javacode .staticfield, .javacode .g, em{color:#0000c0;font-style:italic}
 * .javacode .staticmethod, .javacode .n, samp{font-style:italic}
 * .javacode .debuglayoutblock{background-color:#ccffff;border:1px solid #99eeee}
 * .javacode .debugseparatorlayoutblock{background-color:#ccffcc;border:1px solid #99ee99}
 * .javacode .debugstatementblocklayoutblock{background-color:#ffcccc;border:1px solid #ee9999}
 * .javacode .debugenumblocklayoutblock{background-color:#ffffcc;border:1px solid #eeee99}
 * .javacode .debugcommentdeprecatedlayoutblock{background-color:#fefefe;border:1px solid #e9e9e9}
 * .javacode .debugmarker{background-color:#ffd2ff;border:1px solid #cfb2cf}
 * .javacode .extraline, .javacode .x, s
 * .javacode .optionalthisprefix, .javacode .o, kbd
 * .javacode .metadata, .javacode .m, ins
 */
public class HtmlPrinter implements Printer {
    private final static boolean DEBUG = true;

    private PrintStream printStream;
    private StringBuffer sbLineNumber;
    private StringBuffer sbCode;
    private int maxLineNumber;
    private int majorVersion;
    private int minorVersion;
    private int realLineNumber;
    private String realLineNumberFormatPrefix;
    private String lineNumberFormatPrefix;
    private String unknownLineNumberPrefix;
    private int indentationCount;
    private int commentJavadocErrorDepth;

    public HtmlPrinter(PrintStream printStream) {
        this.printStream = printStream;
        this.sbLineNumber = new StringBuffer(10 * 1024);
        this.sbCode = new StringBuffer(30 * 1024);
    }

    public void print(byte b) {
        this.sbCode.append(String.valueOf(b));
    }

    public void print(char c) {
        switch (c) {
            case '<':
                this.sbCode.append("&lt;");
                break;
            case '>':
                this.sbCode.append("&gt;");
                break;
            default:
                this.sbCode.append(String.valueOf(c));
                break;
        }
    }

    public void print(int i) {
        this.sbCode.append(String.valueOf(i));
    }

    public void print(String s) {
        this.sbCode.append(s);
    }

    public void printNumeric(String s) {
        this.sbCode.append("<u>");
        this.sbCode.append(s);
        this.sbCode.append("</u>");
    }

    public void printString(String s, String scopeInternalName) {
        this.sbCode.append("<u>");

        // Replace '<' by '&lt;'
        int length = s.length();

        if (length > 0) {
            for (int i = 0; i < length; i++) {
                char c = s.charAt(i);

                if (c == '<')
                    this.sbCode.append("&lt;");
                else
                    this.sbCode.append(c);
            }
        }

        this.sbCode.append("</u>");
    }

    public void printKeyword(String s) {
        if (this.commentJavadocErrorDepth == 0) {
            this.sbCode.append("<b>");
            this.sbCode.append(s);
            this.sbCode.append("</b>");
        } else {
            this.sbCode.append(s);
        }
    }

    public void printJavaWord(String s) {
        printKeyword(s);
    }

    public void printType(String internalName, String name, String scopeInternalName) {
        this.sbCode.append(name);
    }

    public void printTypeDeclaration(String internalName, String name) {
        this.sbCode.append(name);
    }

    public void printTypeImport(String internalName, String name) {
        this.sbCode.append(name);
    }

    public void printField(
            String internalName, String name,
            String descriptor, String scopeInternalName) {
        printFieldDeclaration(internalName, name, descriptor);
    }

    public void printFieldDeclaration(
            String internalName, String name, String descriptor) {
        this.sbCode.append("<var>");
        this.sbCode.append(name);
        this.sbCode.append("</var>");
    }

    public void printStaticField(
            String internalName, String name,
            String descriptor, String scopeInternalName) {
        printStaticFieldDeclaration(internalName, name, descriptor);
    }

    public void printStaticFieldDeclaration(
            String internalName, String name, String descriptor) {
        this.sbCode.append("<em>");
        this.sbCode.append(name);
        this.sbCode.append("</em>");
    }

    public void printConstructor(
            String internalName, String name,
            String descriptor, String scopeInternalName) {
        this.sbCode.append(name);
    }

    public void printConstructorDeclaration(
            String internalName, String name, String descriptor) {
        this.sbCode.append(name);
    }

    public void printStaticConstructorDeclaration(
            String internalName, String name) {
        this.sbCode.append("<samp>");
        this.sbCode.append(name);
        this.sbCode.append("</samp>");
    }

    public void printMethod(
            String internalName, String name,
            String descriptor, String scopeInternalName) {
        this.sbCode.append(name);
    }

    public void printMethodDeclaration(
            String internalName, String name, String descriptor) {
        this.sbCode.append(name);
    }

    public void printStaticMethod(
            String internalName, String name,
            String descriptor, String scopeInternalName) {
        printStaticMethodDeclaration(internalName, name, descriptor);
    }

    public void printStaticMethodDeclaration(
            String internalName, String name, String descriptor) {
        this.sbCode.append("<samp>");
        this.sbCode.append(name);
        this.sbCode.append("</samp>");
    }

    public void start(int maxLineNumber, int majorVersion, int minorVersion) {
        this.maxLineNumber = maxLineNumber;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.realLineNumber = 0;
        this.indentationCount = 0;
        this.commentJavadocErrorDepth = 0;

        int digitCount = 1;
        int maximum = 9;

        while (maximum < maxLineNumber) {
            digitCount++;
            maximum = maximum * 10 + 9;
        }

        this.realLineNumberFormatPrefix = "%" + (digitCount + 1) + "d:";
        this.lineNumberFormatPrefix = "%" + digitCount + "d<br>";

        StringBuilder sb = new StringBuilder(digitCount + 7);
        sb.append("%" + (digitCount + 1) + "d:");
        for (int i = 0; i < digitCount; i++) sb.append(' ');
        sb.append("<br>");
        this.unknownLineNumberPrefix = sb.toString();

        this.printStream.print(
                "<html><head><style type='text/css'>" +

                        "body,html{font-family:Lucida Grande,Lucida Sans Unicode,Arial,sans-serif;font-size:90%}" +

                        "#demo .out{background-color:#FFFFFF}" +
                        "#demo .out .content{padding:0px;font-size:12px;font-family:courier new,courier;white-space:pre;border-radius:0 0 10px 10px}" +
                        "#demo .out .content .e{color:#FF0000;margin:10px}" +
                        "#linenumber{float:left;margin:0;padding:1px 8px 5px 1px;border-style:solid;border-color:#888888;border-width:0 1px 0 0;color:#888888}" +
                        "#linenumber s{text-decoration:none}" +
                        "#linenumber span{color:#FF0000;font-style:normal}" +
                        "#javacode{padding:0 0 5px 0;margin:1px 5px 1px 5px;color:black}" +
                        "#javacode i{color:#3f7f5f;font-style:normal}" +
                        "#javacode b{color:#7f0055;font-weight:bold;line-height:1}" +
                        "#javacode s{text-decoration:none}" +
                        "#javacode cite{color:#3F7F5F;font-style:normal}" +
                        "#javacode dfn{color:#3f5fbf;font-style:normal}" +
                        "#javacode dfn b{color:#3F5FBF}" +
                        "#javacode span{color:#FF0000;font-style:normal}" +
                        "#javacode del{color:#646464;text-decoration:none}" +
                        "#javacode kbd{font-family:courier new,courier}" +
                        "#javacode u{color:#2a00ff;text-decoration:none}" +
                        "#javacode var{color:#0000c0;font-style:normal}" +
                        "#javacode em{color:#0000c0;font-style:italic;line-height:1}" +
                        "#javacode samp{font-style:italic;line-height:1}" +
                        "#javacode .debuglayoutblock{color:#000000;background-color:#ccffff;border:1px solid #99eeee}" +
                        "#javacode .debugseparatorlayoutblock{color:#000000;background-color:#ccffcc;border:1px solid #99ee99}" +
                        "#javacode .debugstatementblocklayoutblock{color:#000000;background-color:#ffcccc;border:1px solid #ee9999}" +
                        "#javacode .debugenumblocklayoutblock{color:#000000;background-color:#ffffcc;border:1px solid #eeee99}" +
                        "#javacode .debugcommentdeprecatedlayoutblock{color:#000000;background-color:#fefefe;border:1px solid #e9e9e9}" +
                        "#javacode .debugmarker{color:#000000;background-color:#ffd2ff;border:1px solid #cfb2cf}" +
                        "#javacode .debugcaseblocklayoutblock{color:#000000;background-color:#ffde66;border:1px solid #ff9a11}" +
                        "#metadata{padding:5px;color:#444444;background-color:#EEEEEE;border-radius:0 0 10px 10px;font-size:11px}" +

                        "</style>" +
                        "</head><body>" +
                        "<h1>Preview</h1>" +
                        "<div id='demo'><div class='out'><div class='content'>");
    }

    public void end() {
        if (this.maxLineNumber > 0) {
            this.printStream.print("<div id='linenumber'>");
            this.printStream.print(this.sbLineNumber.toString());
            this.printStream.print("</div>");
        }

        this.printStream.print("<div id='javacode'>");
        this.printStream.print(this.sbCode.toString());
        this.printStream.print("</div>");

        this.printStream.print("<div id='metadata'>");
        this.printStream.print("Java Class Version: " + VersionUtil.getJDKVersion(this.majorVersion, this.minorVersion) + "<br>");
        this.printStream.print("JD-CL Version:      " + "0.1.0" + "<br>");
        this.printStream.print("JD-Core Version:    " + CoreConstants.JD_CORE_VERSION);
        this.printStream.print("</div>");

        this.printStream.print("</div></div></div></body></html>");
    }

    public void indent() {
        this.indentationCount++;
    }

    public void desindent() {
        if (this.indentationCount > 0)
            this.indentationCount--;
    }

    public void startOfLine(int lineNumber) {
        this.realLineNumber++;

        if (this.maxLineNumber > 0) {
            if (lineNumber == UNKNOWN_LINE_NUMBER) {
                this.sbLineNumber.append(String.format(
                        this.unknownLineNumberPrefix, this.realLineNumber));
            } else {
                this.sbLineNumber.append(String.format(
                        this.realLineNumberFormatPrefix, this.realLineNumber));

                if (this.realLineNumber == lineNumber) {
                    this.sbLineNumber.append(String.format(
                            this.lineNumberFormatPrefix, lineNumber));
                } else {
                    this.sbLineNumber.append("<span>");
                    this.sbLineNumber.append(String.format(
                            this.lineNumberFormatPrefix, lineNumber));
                    this.sbLineNumber.append("</span>");
                }
            }
        }

        for (int i = 0; i < indentationCount; i++)
            this.sbCode.append("  ");
    }

    public void endOfLine() {
        this.sbCode.append("<br>");
    }

    public void extraLine(int count) {
        if (this.maxLineNumber > 0) {
            this.sbLineNumber.append("<s>");
        }
        this.sbCode.append("<s>");

        while (count-- > 0) {
            this.realLineNumber++;

            if (this.maxLineNumber > 0) {
                this.sbLineNumber.append(String.format(
                        this.unknownLineNumberPrefix, this.realLineNumber));
            }

            this.sbCode.append("<br>");
        }

        if (this.maxLineNumber > 0) {
            this.sbLineNumber.append("</s>");
        }
        this.sbCode.append("</s>");
    }

    public void startOfComment() {
        this.sbCode.append("<cite>");
        this.commentJavadocErrorDepth++;
    }

    public void endOfComment() {
        this.sbCode.append("</cite>");
        this.commentJavadocErrorDepth--;
    }

    public void startOfJavadoc() {
        this.sbCode.append("<dfn>");
        this.commentJavadocErrorDepth++;
    }

    public void endOfJavadoc() {
        this.sbCode.append("</dfn>");
        this.commentJavadocErrorDepth--;
    }

    public void startOfXdoclet() {
        this.sbCode.append("<b>");
    }

    public void endOfXdoclet() {
        this.sbCode.append("</b>");
    }

    public void startOfError() {
        this.sbCode.append("<span>");
        this.commentJavadocErrorDepth++;
    }

    public void endOfError() {
        this.sbCode.append("</span>");
        this.commentJavadocErrorDepth--;
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
        this.sbCode.append("<del>");
    }

    public void endOfAnnotationName() {
        this.sbCode.append("</del>");
    }

    public void startOfOptionalPrefix() {
        this.sbCode.append("<kbd>");
    }

    public void endOfOptionalPrefix() {
        this.sbCode.append("</kbd>");
    }

    public void debugStartOfLayoutBlock() {
        if (DEBUG) {
            this.sbCode.append("<span class='debuglayoutblock' alt='block'>");
        }
    }

    public void debugEndOfLayoutBlock() {
        if (DEBUG) {
            this.sbCode.append("</span>");
        }
    }

    public void debugStartOfSeparatorLayoutBlock() {
        if (DEBUG) {
            this.sbCode.append("<span class='debugseparatorlayoutblock' alr='separator'>");
        }
    }

    public void debugEndOfSeparatorLayoutBlock(int min, int value, int max) {
        if (DEBUG) {
            // DEBUG // this.sb.append(min);
            // DEBUG // this.sb.append("&lt;=");
            // DEBUG // this.sb.append(value);
            // DEBUG // this.sb.append("&lt;=");
            // DEBUG // this.sb.append(max);
            this.sbCode.append("</span>");
        }
    }

    public void debugStartOfStatementsBlockLayoutBlock() {
        if (DEBUG) {
            this.sbCode.append("<span class='debugstatementblocklayoutblock' alt='statement'>");
        }
    }

    public void debugEndOfStatementsBlockLayoutBlock(int min, int value, int max) {
        if (DEBUG) {
            // DEBUG // this.sb.append(min);
            // DEBUG // this.sb.append("&lt;=");
            // DEBUG // this.sb.append(value);
            // DEBUG // this.sb.append("&lt;=");
            // DEBUG // this.sb.append(max);
            this.sbCode.append("</span>");
        }
    }

    public void debugStartOfInstructionBlockLayoutBlock() {
        if (DEBUG) {
            this.sbCode.append("<span class='debugenumblocklayoutblock' alt='numeric block'>");
        }
    }

    public void debugEndOfInstructionBlockLayoutBlock() {
        if (DEBUG) {
            this.sbCode.append("</span>");
        }
    }

    public void debugStartOfCommentDeprecatedLayoutBlock() {
        if (DEBUG) {
            this.sbCode.append("<span class='debugcommentdeprecatedlayoutblock' alt='comment deprecated'>");
        }
    }

    public void debugEndOfCommentDeprecatedLayoutBlock() {
        if (DEBUG) {
            this.sbCode.append("</span>");
        }
    }

    public void debugMarker(String marker) {
        if (DEBUG) {
            // DEBUG // this.sb.append("<span class='debugmarker'>");
            // DEBUG // this.sb.append(marker);
            // DEBUG // this.sb.append("</span>");
        }
    }

    public void debugStartOfCaseBlockLayoutBlock() {
        if (DEBUG) {
            this.sbCode.append("<span class='debugcaseblocklayoutblock' alt='case block'>");
        }
    }

    public void debugEndOfCaseBlockLayoutBlock() {
        if (DEBUG) {
            this.sbCode.append("</span>");
        }
    }
}
