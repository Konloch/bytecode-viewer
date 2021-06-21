package the.bytecode.club.bytecodeviewer;

import java.io.File;

/**
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
}
