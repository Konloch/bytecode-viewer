package jd.cli.printer.html;

import java.io.PrintStream;
import jd.cli.util.VersionUtil;
import org.jd.core.v1.api.printer.Printer;

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

    private final PrintStream printStream;
    private final StringBuffer sbLineNumber;
    private final StringBuffer sbCode;
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

    @Override
    public void printKeyword(String s) {
        if (this.commentJavadocErrorDepth == 0) {
            this.sbCode.append("<b>");
            this.sbCode.append(s);
            this.sbCode.append("</b>");
        } else {
            this.sbCode.append(s);
        }
    }

    @Override
    public void printDeclaration(int i, String s, String s1, String s2) {
    }

    @Override
    public void printReference(int i, String s, String s1, String s2, String s3) {
    }

    @Override
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
        sb.append("%").append(digitCount + 1).append("d:");
        for (int i = 0; i < digitCount; i++) sb.append(' ');
        sb.append("<br>");
        this.unknownLineNumberPrefix = sb.toString();

        this.printStream.print(
                "<html><head><style type='text/css'>" +

                        "body,html{font-family:Lucida Grande,Lucida Sans Unicode,Arial,sans-serif;font-size:90%}" +

                        "#demo .out{background-color:#FFFFFF}" +
                        "#demo .out .content{padding:0px;font-size:12px;font-family:courier new,courier;"
                        + "white-space:pre;border-radius:0 0 10px 10px}" +
                        "#demo .out .content .e{color:#FF0000;margin:10px}" +
                        "#linenumber{float:left;margin:0;padding:1px 8px 5px 1px;border-style:solid;"
                        + "border-color:#888888;border-width:0 1px 0 0;color:#888888}" +
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
                        "#javacode .debugseparatorlayoutblock{color:#000000;background-color:#ccffcc;border:1px solid"
                        + " #99ee99}" +
                        "#javacode .debugstatementblocklayoutblock{color:#000000;background-color:#ffcccc;border:1px "
                        + "solid #ee9999}" +
                        "#javacode .debugenumblocklayoutblock{color:#000000;background-color:#ffffcc;border:1px solid"
                        + " #eeee99}" +
                        "#javacode .debugcommentdeprecatedlayoutblock{color:#000000;background-color:#fefefe;"
                        + "border:1px solid #e9e9e9}" +
                        "#javacode .debugmarker{color:#000000;background-color:#ffd2ff;border:1px solid #cfb2cf}" +
                        "#javacode .debugcaseblocklayoutblock{color:#000000;background-color:#ffde66;border:1px solid"
                        + " #ff9a11}" +
                        "#metadata{padding:5px;color:#444444;background-color:#EEEEEE;border-radius:0 0 10px 10px;"
                        + "font-size:11px}" +

                        "</style>" +
                        "</head><body>" +
                        "<h1>Preview</h1>" +
                        "<div id='demo'><div class='out'><div class='content'>");
    }

    @Override
    public void end() {
        if (this.maxLineNumber > 0) {
            this.printStream.print("<div id='linenumber'>");
            this.printStream.print(this.sbLineNumber);
            this.printStream.print("</div>");
        }

        this.printStream.print("<div id='javacode'>");
        this.printStream.print(this.sbCode);
        this.printStream.print("</div>");

        this.printStream.print("<div id='metadata'>");
        this.printStream.print("Java Class Version: " + VersionUtil.getJDKVersion(this.majorVersion,
                this.minorVersion) + "<br>");
        this.printStream.print("JD-CL Version:      " + "0.1.0" + "<br>");
        // TODO: Where can I find this dynamically?
        this.printStream.print("JD-Core Version:    " + "1.6.6");
        this.printStream.print("</div>");

        this.printStream.print("</div></div></div></body></html>");
    }

    @Override
    public void printText(String s) {
        this.sbCode.append(s);
    }

    @Override
    public void printNumericConstant(String s) {
        this.sbCode.append("<u>");
        this.sbCode.append(s);
        this.sbCode.append("</u>");
    }

    @Override
    public void printStringConstant(String s, String s1) {
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

    @Override
    public void endLine() {
        this.sbCode.append("<br>");
    }

    @Override
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

    @Override
    public void startMarker(int i) {
    }

    @Override
    public void endMarker(int i) {
    }

}
