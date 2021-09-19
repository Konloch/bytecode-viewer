package the.bytecode.club.bytecodeviewer.translation;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import the.bytecode.club.bytecodeviewer.api.BCV;

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
 * Translation keys for constant strings (does not change the component text on language change).
 *
 * You need to add your translation key here if it is not tied to any specific component (Console, Dialogs)
 *
 * @author Konloch
 * @since 7/6/2021
 */

public enum TranslatedStrings
{
	PRODUCT("BCV"),
	PRODUCTNAME("BytecodeViewer"),
	PRODUCT_NAME("Bytecode Viewer"),
	PRODUCT_H_NAME("Bytecode-Viewer"),
	WEBSITE("https://bytecodeviewer.com"),
	TBC("https://the.bytecode.club"),
	
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
	
	
	OPEN_UNSTYLED,
	QUICK_OPEN,
	DELETE,
	NEW,
	EXPAND,
	COLLAPSE,
	RELOAD_RESOURCES_TITLE,
	RELOAD_RESOURCES_CONFIRM,
	SELECT_FILE_TITLE,
	SELECT_FILE_DESCRIPTION,
	SELECT_EXTERNAL_PLUGIN_TITLE,
	SELECT_EXTERNAL_PLUGIN_DESCRIPTION,
	FOREIGN_LIBRARY_WARNING,
	RESET_TITLE,
	RESET_CONFIRM,
	EXIT_TITLE,
	EXIT_CONFIRM,
	ABOUT_TITLE,
	PLUGIN_CONSOLE_TITLE,
	CLOSE_ALL_BUT_THIS,
	CLOSE_TAB,
	PLEASE_SEND_THIS_ERROR_LOG_TO,
	PLEASE_SEND_RESOURCES,
	ONE_PLUGIN_AT_A_TIME,
	ILLEGAL_ACCESS_ERROR,
	
	
	YES,
	NO,
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
	YOU_NEED_TO_SET_YOUR_JAVA_RT_PATH_A,
	YOU_NEED_TO_SET_YOUR_JAVA_RT_PATH_B,
	JAVA_EXECUTABLE,
	JAVAC_EXECUTABLE,
	JAVA_TOOLS_JAR,
	JAVA_RT_JAR,
	OPTIONAL_LIBRARY_FOLDER,
	QUICK_FILE_SEARCH_NO_FILE_EXTENSION,
	SUGGESTED_FIX_DECOMPILER_ERROR,
	SUGGESTED_FIX_COMPILER_ERROR,
	FIRST_OPEN_A_RESOURCE,
	FIRST_OPEN_A_CLASS,
	FIRST_VIEW_A_CLASS,
	SUGGESTED_FIX_NO_DECOMPILER_WARNING,
	DRAG_CLASS_JAR,
	;
	
	public static final Set<String> nameSet = new HashSet<>();
	
	static
	{
		for(TranslatedStrings s : values())
			nameSet.add(s.name());
	}
	
	private final String TEXT_ERROR = "FAILED_TO_LOAD";
	private String text = TEXT_ERROR;
	
	TranslatedStrings(String text)
	{
		this.text = text;
	}
	
	TranslatedStrings()
	{
		//load english translations by default
		try
		{
			setText(Language.ENGLISH.getTranslation().get(name()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setText(String text)
	{
		if(text == null)
		{
			BCV.logE(true, "TranslatedStrings:"+name() + " - Missing Translation");
			text = TEXT_ERROR;
		}
		
		//TODO this should be tokenized against the TranslatedStrings enum
		text = text.replace("{PRODUCTNAME}", PRODUCTNAME.toString())
				.replace("{PRODUCT_NAME}", PRODUCT_NAME.toString())
				.replace("{PRODUCT-NAME}", PRODUCT_H_NAME.toString())
				.replace("{PRODUCT}", PRODUCT.toString())
				.replace("{TBC}", TBC.toString())
				.replace("{WEBSITE}", WEBSITE.toString())
		;
		
		this.text = text;
	}
	
	@Override
	public String toString()
	{
		return text;
	}
}
