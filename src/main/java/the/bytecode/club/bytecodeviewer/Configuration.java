package the.bytecode.club.bytecodeviewer;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import the.bytecode.club.bytecodeviewer.bootloader.BootState;
import the.bytecode.club.bytecodeviewer.gui.theme.LAFTheme;
import the.bytecode.club.bytecodeviewer.gui.theme.RSTATheme;
import the.bytecode.club.bytecodeviewer.translation.Language;

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
 * A collection of variables that can be configured through the settings menu or some form of UI/plugin
 *
 * @author Konloch
 * @since 6/21/2021
 */

public class Configuration
{
	public static String python2 = "";
	public static boolean python2Extra = false;
	public static String python3 = "";
	public static boolean python3Extra = false;
	public static String rt = "";
	public static String library = "";
	public static String java = Constants.JAVA_BINARY.exists() ? Constants.JAVA_BINARY.getAbsolutePath() :
			Constants.JAVA_BINARY_NIX.exists() ? Constants.JAVA_BINARY_NIX.getAbsolutePath() : "";
	public static String javac = "";
	public static String javaTools = "";
	public static File krakatauTempDir;
	public static File krakatauTempJar;
	
	public static boolean displayParentInTab = false; //also change in the main GUI
	public static boolean simplifiedTabNames = false;
	
	//if true it will show a settings dialog on click instead of more menu items
	public static boolean useNewSettingsDialog = true; //TODO add to GUI
	
	//if true it will put force error UIs and console UIs to be added as a tab
	public static boolean pluginConsoleAsNewTab = true; //TODO add to GUI
	//if true it will put force error UIs and console UIs to be added as a tab
	public static boolean errorLogsAsNewTab = true; //TODO add to GUI
	//if true the plugin writer will open inside of a tab
	public static boolean pluginWriterAsNewTab = true; //TODO add to GUI
	
	//if true jadx will be above smali in an android grouping
	public static boolean jadxGroupedWithSmali = true; //TODO add to GUI
	
	public static boolean forceResourceUpdateFromClassNode = false; //TODO add to GUI
	public static boolean showDarkLAFComponentIcons = false;
	public static boolean currentlyDumping = false;
	public static boolean needsReDump = true;
	public static boolean warnForEditing = false;
	public static boolean runningObfuscation = false;
	public static final long start = System.currentTimeMillis();
	public static String lastOpenDirectory = ".";
	public static String lastSaveDirectory = ".";
	public static String lastPluginDirectory = ".";
	public static boolean pingback = false;
	public static boolean deleteForeignLibraries = true;
	public static boolean canExit = false;
	public static int silenceExceptionGUI = 0;
	public static int pauseExceptionGUI = 0;
	
	public static final int maxRecentFiles = 25; //eventually may be a setting
	public static boolean verifyCorruptedStateOnBoot = false; //eventually may be a setting
	
	public static BootState bootState = BootState.START_UP;
	public static Language language = guessBestLanguage();
	public static LAFTheme lafTheme = LAFTheme.DARK;
	public static RSTATheme rstaTheme = lafTheme.getRSTATheme();
	public static long lastHotKeyExecuted = 0;
	
	public static void setLastOpenDirectory(File file)
	{
		lastOpenDirectory = file.getAbsolutePath();
	}
	
	public static void setLastSaveDirectory(File file)
	{
		lastSaveDirectory = file.getAbsolutePath();
	}
	
	public static void setLastPluginDirectory(File file)
	{
		lastPluginDirectory = file.getAbsolutePath();
	}
	
	public static File getLastOpenDirectory()
	{
		File lastDir = new File(lastOpenDirectory);
		if(lastDir.getParentFile() != null && lastDir.getParentFile().exists())
			return lastDir;
		
		return new File(".");
	}
	
	public static File getLastSaveDirectory()
	{
		File lastDir = new File(lastSaveDirectory);
		if(lastDir.getParentFile() != null && lastDir.getParentFile().exists())
			return lastDir;
		
		try
		{
			return new File(".").getCanonicalFile();
		}
		catch (IOException e)
		{
			return new File(".");
		}
	}
	
	public static File getLastPluginDirectory()
	{
		File lastDir = new File(lastPluginDirectory);
		
		if(lastDir.getParentFile() != null && lastDir.getParentFile().exists())
			return lastDir;
		
		return new File(".");
	}
	
	public static Language guessBestLanguage()
	{
		Language language = Language.getLanguageCodeLookup().get(Locale.getDefault().getLanguage());
		
		if(language != null)
			return language;
		
		//fallback to english
		return Language.ENGLISH;
	}
}
