/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Konloch - Konloch.com / BytecodeViewer.com           *
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

package the.bytecode.club.bytecodeviewer.decompilers.jdgui;

import java.util.HashMap;
import java.util.Map;

public class CommonPreferences
{
    private final Map<String, Object> preferences;
    protected boolean showDefaultConstructor;
    protected boolean realignmentLineNumber;
    protected boolean showPrefixThis;
    protected boolean mergeEmptyLines;
    protected boolean unicodeEscape;
    protected boolean showLineNumbers;

    public CommonPreferences()
    {
        this.showPrefixThis = true;
        this.mergeEmptyLines = false;
        this.unicodeEscape = false;
        this.showLineNumbers = true;
        this.preferences = new HashMap<>();
    }

    public CommonPreferences(boolean showDefaultConstructor, boolean realignmentLineNumber, boolean showPrefixThis, boolean mergeEmptyLines, boolean unicodeEscape, boolean showLineNumbers)
    {
        this.showDefaultConstructor = showDefaultConstructor;
        this.realignmentLineNumber = realignmentLineNumber;
        this.showPrefixThis = showPrefixThis;
        this.mergeEmptyLines = mergeEmptyLines;
        this.unicodeEscape = unicodeEscape;
        this.showLineNumbers = showLineNumbers;
        this.preferences = new HashMap<>();
    }

    public boolean isShowDefaultConstructor()
    {
        return showDefaultConstructor;
    }

    public boolean isRealignmentLineNumber()
    {
        return realignmentLineNumber;
    }

    public boolean isShowPrefixThis()
    {
        return showPrefixThis;
    }

    public boolean isMergeEmptyLines()
    {
        return mergeEmptyLines;
    }

    public boolean isUnicodeEscape()
    {
        return unicodeEscape;
    }

    public boolean isShowLineNumbers()
    {
        return showLineNumbers;
    }

    public Map<String, Object> getPreferences()
    {
        return preferences;
    }
}
