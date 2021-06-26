package the.bytecode.club.bytecodeviewer;

import the.bytecode.club.bytecodeviewer.gui.theme.LAFTheme;
import the.bytecode.club.bytecodeviewer.gui.theme.RSTATheme;

import java.io.File;

/**
 * A collection of variables that can be configured through the settings menu or some form of UI/plugin
 *
 * @author Konloch
 * @since 6/21/2021
 */
public class Configuration
{
	public static String python = "";
	public static String python3 = "";
	public static String rt = "";
	public static String library = "";
	public static String javac = "";
	public static String java = "";
	public static File krakatauTempDir;
	public static File krakatauTempJar;
	public static boolean displayParentInTab = false; //also change in the main GUI
	public static boolean simplifiedTabNames = false;
	public static boolean currentlyDumping = false;
	public static boolean needsReDump = true;
	public static boolean warnForEditing = false;
	public static boolean runningObfuscation = false;
	public static final long start = System.currentTimeMillis();
	public static String lastDirectory = ".";
	public static boolean pingback = false;
	public static boolean deleteForeignLibraries = true;
	public static boolean canExit = false;
	
	public static boolean verifyCorruptedStateOnBoot = false; //eventually may be a setting
	
	public static long lastHotKeyExecuted = System.currentTimeMillis();
	
	public static LAFTheme lafTheme = LAFTheme.LIGHT; //lightmode by default since it uses the system theme
	public static RSTATheme rstaTheme = lafTheme.getRSTATheme();
}