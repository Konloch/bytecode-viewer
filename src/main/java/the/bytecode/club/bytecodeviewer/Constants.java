package the.bytecode.club.bytecodeviewer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.konloch.kontainer.io.DiskReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Konloch
 * @since 6/21/2021
 */
public class Constants
{
	/*per version*/
	public static final String VERSION = "2.10.12"; //could be loaded from the pom
	public static String krakatauVersion = "12";
	public static String enjarifyVersion = "4";
	public static final boolean BLOCK_TAB_MENU = true;
	public static final boolean PREVIEW_COPY = false;
	public static final boolean FAT_JAR = true; //could be automatic by checking if it's loaded a class named whatever for a library
	public static final boolean OFFLINE_MODE = true; //disables the automatic updater
	
	public static final int maxRecentFiles = 25;
	public static final String fs = System.getProperty("file.separator");
	public static final String nl = System.getProperty("line.separator");
	public static final File BCVDir = new File(System.getProperty("user.home") + fs + ".Bytecode-Viewer");
	public static final File RT_JAR = new File(System.getProperty("java.home") + fs + "lib" + fs + "rt.jar");
	public static final File RT_JAR_DUMPED = new File(getBCVDirectory() + fs + "rt.jar");
	public static final String filesName = getBCVDirectory() + fs + "recentfiles.json";
	public static final String pluginsName = getBCVDirectory() + fs + "recentplugins.json";
	public static final String settingsName = getBCVDirectory() + fs + "settings.bcv";
	public static final String themeSettingsName = getBCVDirectory() + fs + "theme.bcv";
	public static final String tempDirectory = getBCVDirectory() + fs + "bcv_temp" + fs;
	public static final String libsDirectory = getBCVDirectory() + fs + "libs" + fs;
	public static String krakatauWorkingDirectory = getBCVDirectory() + fs + "krakatau_" + krakatauVersion;
	public static String enjarifyWorkingDirectory = getBCVDirectory() + fs + "enjarify_" + enjarifyVersion;
	
	public static List<String> recentPlugins;
	public static List<String> recentFiles;
	public static Gson gson;
	
	static {
		try {
			gson = new GsonBuilder().setPrettyPrinting().create();
			if (new File(filesName).exists())
				recentFiles = gson.fromJson(DiskReader.loadAsString(filesName), new TypeToken<ArrayList<String>>() {}.getType());
			else
				recentFiles = DiskReader.loadArrayList(getBCVDirectory() + fs + "recentfiles.bcv", false);
			
			if (new File(pluginsName).exists())
				recentPlugins = gson.fromJson(DiskReader.loadAsString(pluginsName), new TypeToken<ArrayList<String>>() {}.getType());
			else
				recentPlugins = DiskReader.loadArrayList(getBCVDirectory() + fs + "recentplugins.bcv", false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the BCV directory
	 *
	 * @return the static BCV directory
	 */
	public static String getBCVDirectory() {
		while (!BCVDir.exists())
			BCVDir.mkdirs();
		
		if (!BCVDir.isHidden() && isWindows())
			hideFile(BCVDir);
		
		return BCVDir.getAbsolutePath();
	}
	
	/**
	 * Checks if the OS contains 'win'
	 *
	 * @return true if the os.name property contains 'win'
	 */
	private static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}
	
	/**
	 * Runs the windows command to hide files
	 *
	 * @param f file you want hidden
	 */
	private static void hideFile(File f) {
		BytecodeViewer.sm.stopBlocking();
		try {
			// Hide file by running attrib system command (on Windows)
			Runtime.getRuntime().exec("attrib +H " + f.getAbsolutePath());
		} catch (Exception e) {
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
		}
		BytecodeViewer.sm.setBlocking();
	}
}
