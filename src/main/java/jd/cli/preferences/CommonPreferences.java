package jd.cli.preferences;

import jd.core.preferences.Preferences;

public class CommonPreferences extends Preferences {
    protected boolean showPrefixThis;
    protected boolean mergeEmptyLines;
    protected boolean unicodeEscape;
    protected boolean showLineNumbers;

    public CommonPreferences() {
        this.showPrefixThis = true;
        this.mergeEmptyLines = false;
        this.unicodeEscape = false;
        this.showLineNumbers = true;
    }

    public CommonPreferences(
            boolean showDefaultConstructor, boolean realignmentLineNumber,
            boolean showPrefixThis, boolean mergeEmptyLines,
            boolean unicodeEscape, boolean showLineNumbers) {
        super(showDefaultConstructor, realignmentLineNumber);
        this.showPrefixThis = showPrefixThis;
        this.mergeEmptyLines = mergeEmptyLines;
        this.unicodeEscape = unicodeEscape;
        this.showLineNumbers = showLineNumbers;
    }

    public boolean isShowPrefixThis() {
        return showPrefixThis;
    }

    public boolean isMergeEmptyLines() {
        return mergeEmptyLines;
    }

    public boolean isUnicodeEscape() {
        return unicodeEscape;
    }

    public boolean isShowLineNumbers() {
        return showLineNumbers;
    }
}
