package the.bytecode.club.bytecodeviewer.translation;

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
 * All of the specific translations strings needed for BCV
 *
 * @author Konloch
 * @since 6/28/2021
 */
public enum Translation
{
	FILE,
	ADD,
	NEW_WORKSPACE,
	RELOAD_RESOURCES,
	RUN,
	COMPILE,
	SAVE,
	SAVE_AS,
	SAVE_AS_RUNNABLE_JAR,
	SAVE_AS_ZIP,
	SAVE_AS_DEX,
	SAVE_AS_APK,
	DECOMPILE_SAVE_OPENED_CLASSES,
	DECOMPILE_SAVE_ALL_CLASSES,
	RECENT_FILES,
	ABOUT,
	EXIT,
	
	VIEW,
	VISUAL_SETTINGS,
	LANGUAGE,
	WINDOW_THEME,
	SYSTEM_THEME,
	DARK_THEME,
	LIGHT_THEME,
	
	TEXT_AREA_THEME,
	DEFAULT_RECOMMENDED_LIGHT,
	DARK_RECOMMENDED_DARK,
	DEFAULT_ALT,
	ECLIPSE,
	INTELLIJ,
	VISUAL_STUDIO,
	DRUID_DARK,
	MONOKAI_DARK,
	
	FONT_SIZE,
	SHOW_TAB_FILE_IN_TAB_TITLE,
	SIMPLIFY_NAME_IN_TAB_TITLE,
	SYNCHRONIZED_VIEWING,
	SHOW_CLASS_METHODS,
	
	PANE_1,
	PANE_2,
	PANE_3,
	NONE,
	EDITABLE,
	JAVA,
	BYTECODE,
	HEXCODE,
	ASM_TEXTIFY,
	
	SETTINGS,
	COMPILE_ON_SAVE,
	COMPILE_ON_REFRESH,
	REFRESH_ON_VIEW_CHANGE,
	DECODE_APK_RESOURCES,
	APK_CONVERSION,
	UPDATE_CHECK,
	DELETE_UNKNOWN_LIBS,
	FORCE_PURE_ASCII_AS_TEXT,
	SET_PYTHON_27_EXECUTABLE,
	SET_PYTHON_30_EXECUTABLE,
	SET_JRE_RT_LIBRARY,
	SET_OPTIONAL_LIBRARY_FOLDER,
	SET_JAVAC_EXECUTABLE,
	BYTECODE_DECOMPILER,
	DEBUG_HELPERS,
	APPEND_BRACKETS_TO_LABEL,
	
	PLUGINS,
	OPEN_PLUGIN,
	RECENT_PLUGINS,
	NEW_JAVA_PLUGIN,
	NEW_JAVASCRIPT_PLUGIN,
	CODE_SEQUENCE_DIAGRAM,
	MALICIOUS_CODE_SCANNER,
	SHOW_MAIN_METHODS,
	SHOW_ALL_STRINGS,
	REPLACE_STRINGS,
	STACK_FRAMES_REMOVER,
	ZKM_STRING_DECRYPTER,
	ALLATORI_STRING_DECRYPTER,
	ZSTRINGARRAY_DECRYPTER,
	
	DEX_TO_JAR,
	ENJARIFY,
	PROCYON,
	CFR,
	FERNFLOWER,
	KRAKATAU,
	JDGUI,
	JADX,
	SMALI_DEX,
	SMALI,
	DISASSEMBLER,
	ERROR,
	SUGGESTED_FIX_DECOMPILER_ERROR,
	SUGGESTED_FIX_COMPILER_ERROR,
	PROCYON_DECOMPILER,
	CFR_DECOMPILER,
	FERNFLOWER_DECOMPILER,
	JADX_DECOMPILER,
	JD_DECOMPILER,
	BYTECODE_DISASSEMBLER,
	FILES,
	QUICK_FILE_SEARCH_NO_FILE_EXTENSION,
	WORK_SPACE,
	EXACT,
	SEARCH,
	RESULTS,
	REFRESH,
	
	;
	
	private final TranslatedComponent component;
	
	Translation()
	{
		this.component = new TranslatedComponent();
		this.component.key = name();
	}
	
	public TranslatedComponent getTranslatedComponent()
	{
		return component;
	}
}