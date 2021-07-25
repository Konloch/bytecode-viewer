package the.bytecode.club.bytecodeviewer.decompilers.jdgui;

import java.util.HashMap;
import java.util.Map;

public class CommonPreferences {
    private final Map<String, Object> preferences;
    protected boolean showDefaultConstructor;
    protected boolean realignmentLineNumber;
    protected boolean showPrefixThis;
    protected boolean mergeEmptyLines;
    protected boolean unicodeEscape;
    protected boolean showLineNumbers;

    public CommonPreferences() {
        this.showPrefixThis = true;
        this.mergeEmptyLines = false;
        this.unicodeEscape = false;
        this.showLineNumbers = true;
        this.preferences = new HashMap<>();
    }

    public CommonPreferences(
            boolean showDefaultConstructor, boolean realignmentLineNumber,
            boolean showPrefixThis, boolean mergeEmptyLines,
            boolean unicodeEscape, boolean showLineNumbers) {
        this.showDefaultConstructor = showDefaultConstructor;
        this.realignmentLineNumber = realignmentLineNumber;
        this.showPrefixThis = showPrefixThis;
        this.mergeEmptyLines = mergeEmptyLines;
        this.unicodeEscape = unicodeEscape;
        this.showLineNumbers = showLineNumbers;
        this.preferences = new HashMap<>();
    }

    public boolean isShowDefaultConstructor() {
        return showDefaultConstructor;
    }

    public boolean isRealignmentLineNumber() {
        return realignmentLineNumber;
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

    public Map<String, Object> getPreferences() {
        return preferences;
    }
}
