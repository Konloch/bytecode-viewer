package the.bytecode.club.bytecodeviewer;

import the.bytecode.club.bytecodeviewer.gui.theme.LAFTheme;
import the.bytecode.club.bytecodeviewer.gui.theme.RSTATheme;
import the.bytecode.club.bytecodeviewer.translation.Language;

import java.io.File;

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
	public static boolean useNewSettingsDialogue = true; //TODO add to GUI
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
	public static Language language = Language.ENGLISH;
	public static LAFTheme lafTheme = LAFTheme.SYSTEM; //lightmode by default since it uses the system theme
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
		
		return new File(".");
	}
	
	public static File getLastPluginDirectory()
	{
		File lastDir = new File(lastPluginDirectory);
		
		if(lastDir.getParentFile() != null && lastDir.getParentFile().exists())
			return lastDir;
		
		return new File(".");
	}
	
	public enum BootState
	{
		START_UP,
		SETTINGS_LOADED,
		GUI_SHOWING,
	}
}