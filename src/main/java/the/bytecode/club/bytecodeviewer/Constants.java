package the.bytecode.club.bytecodeviewer;

import java.io.File;
import java.io.PrintStream;

import org.objectweb.asm.Opcodes;
import the.bytecode.club.bytecodeviewer.resources.ResourceType;

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
 * General program constants, to use this class include everything as a wildcard static import:
 *      import static the.bytecode.club.bytecodeviewer.Constants.*;
 *
 * @author Konloch
 * @since 6/21/2021
 */
public class Constants
{
	/*per version*/
	public static String krakatauVersion = "12";
	public static String enjarifyVersion = "4";
	
	//if true this disables testing code for tabs
	//until dragging and full right-click menu support is added this is
	//a starting point
	public static final boolean BLOCK_TAB_MENU = true;
	
	//if true this will attempt to launch the decompilers in a new JVM process
	//the pro's to this are:
	//      + You can control the java arguments (more memory & stack)
	//the cons to this are:
	//      + If you could keep it in memory, now you need to write to disk (windows limitations)
	public static final boolean LAUNCH_DECOMPILERS_IN_NEW_PROCESS = false; //TODO
	
	//could be automatic by checking if it's loaded a class named whatever for a library
	//maybe it could be automatic with some maven plugin?
	public static final boolean FAT_JAR = true;
	
	//the automatic updater
	//SHADED_LIBRARIES must be false for the boot loader to startup
	//TODO this needs to be changed to support maven
	public static final boolean AUTOMATIC_LIBRARY_UPDATING = false;
	
	//version is set via maven
	public static final String VERSION = getVersion(BytecodeViewer.class.getPackage().getImplementationVersion());
	//dev mode is just a check for running via IDE
	public static boolean DEV_MODE;
	
	//if true the version checker will prompt and ask how you would like to proceed
	public static final boolean FORCE_VERSION_CHECKER_PROMPT = false;
	
	public static final String fs = System.getProperty("file.separator");
	public static final String nl = System.getProperty("line.separator");
	
	public static final File BCVDir = resolveBCVRoot();
	public static final File RT_JAR = new File(System.getProperty("java.home") + fs + "lib" + fs + "rt.jar");
	public static final File JAVA_BINARY = new File(System.getProperty("java.home") + fs + "bin" + fs + "java.exe");
	public static final File JAVA_BINARY_NIX = new File(System.getProperty("java.home") + fs + "bin" + fs + "java");
	public static final File RT_JAR_DUMPED = new File(getBCVDirectory() + fs + "rt.jar");
	public static final String filesName = getBCVDirectory() + fs + "recentfiles.json";
	public static final String pluginsName = getBCVDirectory() + fs + "recentplugins.json";
	public static final String settingsName = getBCVDirectory() + fs + "settings.bcv";
	public static final String tempDirectory = getBCVDirectory() + fs + "bcv_temp" + fs;
	public static final String systemTempDirectory = System.getProperty("java.io.tmpdir");
	public static final String libsDirectory = getBCVDirectory() + fs + "libs" + fs;
	public static String krakatauWorkingDirectory = getBCVDirectory() + fs + "krakatau_" + krakatauVersion;
	public static String enjarifyWorkingDirectory = getBCVDirectory() + fs + "enjarify_" + enjarifyVersion;
	public static final String[] SUPPORTED_FILE_EXTENSIONS = ResourceType.supportedBCVExtensionMap.keySet().toArray(new String[0]);
	public static final int ASM_VERSION = Opcodes.ASM9;
	
	public static final PrintStream ERR = System.err;
	public static final PrintStream OUT = System.out;
	
	public static File resolveBCVRoot()
	{
		File defaultLocation =  new File(System.getProperty("user.home") + fs + ".Bytecode-Viewer");
		
		//if BCV was previously installed using the default directory, continue to use that
		if(defaultLocation.exists())
			return defaultLocation;
		
		//handle XDG Base Directory - https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html
		if(isNix())
		{
			File homeLocal = new File(System.getProperty("user.home") + fs + ".local");
			if(homeLocal.exists())
				return new File(homeLocal, "share" + fs + ".Bytecode-Viewer");
			
			File homeConfig = new File(System.getProperty("user.home") + fs + ".config");
			if(homeConfig.exists())
				return new File(homeConfig, ".Bytecode-Viewer");
		}
		
		//return BCV default location
		return defaultLocation;
	}
	
	/**
	 * Returns the BCV directory
	 *
	 * @return the static BCV directory
	 */
	public static String getBCVDirectory()
	{
		while (!BCVDir.exists())
			BCVDir.mkdirs();
		
		//hides the BCV directory
		if (isWindows() && !BCVDir.isHidden())
		{
			new Thread(()->{
				try {
					// Hide file by running attrib system command (on Windows)
					Process p = new ProcessBuilder("attrib",
							"+H",
							BCVDir.getAbsolutePath()).start();
				} catch (Exception e) {
					//ignore
				}
			}, "Hide BCV Dir").start();
		}
		
		return BCVDir.getAbsolutePath();
	}
	
	/**
	 * Checks if the OS contains 'win'
	 *
	 * @return true if the os.name property contains 'win'
	 */
	private static boolean isWindows()
	{
		return System.getProperty("os.name").toLowerCase().contains("win");
	}
	
	/**
	 * Checks if the OS contains 'nix', 'nux', or 'bsd'
	 *
	 * @return true if the os.name property contains 'nix', 'nux', or 'bsd'
	 */
	private static boolean isNix()
	{
		String os = System.getProperty("os.name").toLowerCase();
		return os.contains("nix") || os.contains("nux") || os.contains("bsd");
	}
	
	/**
	 * Detects developer mode or returns the current version
	 */
	public static String getVersion(String mavenVersion)
	{
		if(FORCE_VERSION_CHECKER_PROMPT)
			return "1.0.0";
		
		if(mavenVersion == null)
		{
			DEV_MODE = true;
			return "Developer Mode";
		}
		
		return mavenVersion;
	}
}
