package the.bytecode.club.bytecodeviewer.translation;

import java.io.IOException;
import java.util.HashSet;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
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

/**
 * Constant-like strings not associated with any specific JComponent
 *
 * @author Konloch
 * @since 7/6/2021
 */

public enum TranslatedStrings
{
	EDITABLE,
	JAVA,
	PROCYON,
	CFR,
	FERNFLOWER,
	KRAKATAU,
	JDGUI,
	JADX,
	SMALI,
	SMALI_DEX,
	HEXCODE,
	BYTECODE,
	ASM_TEXTIFY,
	ERROR,
	DISASSEMBLER,
	RESULTS,
	SEARCH,
	ERROR2,
	PROCESS2,
	EXIT_VALUE_IS,
	ERROR_COMPILING_CLASS,
	COMPILER_TIP,
	JAVA_COMPILE_FAILED,
	SELECT_LIBRARY_FOLDER,
	SELECT_JAVA_RT,
	SELECT_JAVA,
	SELECT_JAVAC,
	SELECT_JAVA_TOOLS,
	SELECT_PYTHON_2,
	SELECT_PYTHON_3,
	PYTHON_2_EXECUTABLE,
	PYTHON_3_EXECUTABLE,
	YOU_NEED_TO_SET_YOUR_PYTHON_2_PATH,
	YOU_NEED_TO_SET_YOUR_PYTHON_3_PATH,
	JAVA_EXECUTABLE,
	JAVAC_EXECUTABLE,
	JAVA_TOOLS_JAR,
	JAVA_RT_JAR,
	OPTIONAL_LIBRARY_FOLDER,
	QUICK_FILE_SEARCH_NO_FILE_EXTENSION,
	SUGGESTED_FIX_DECOMPILER_ERROR,
	SUGGESTED_FIX_COMPILER_ERROR,
	DRAG_CLASS_JAR,
	;
	
	public static final HashSet<String> nameSet = new HashSet<>();
	
	static
	{
		for(TranslatedStrings s : values())
			nameSet.add(s.name());
	}
	
	private String text;
	
	TranslatedStrings()
	{
		//load english translations by default
		try
		{
			this.text = Language.ENGLISH.getTranslation().get(name());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	@Override
	public String toString()
	{
		return text;
	}
}