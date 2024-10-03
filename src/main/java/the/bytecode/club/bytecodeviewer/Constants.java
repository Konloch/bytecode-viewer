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

package the.bytecode.club.bytecodeviewer;

import org.objectweb.asm.Opcodes;
import the.bytecode.club.bytecodeviewer.resources.ResourceType;

import java.io.File;
import java.io.PrintStream;

/**
 * General program constants, to use this class include everything as a wildcard static import:
 * import static the.bytecode.club.bytecodeviewer.Constants.*;
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
    public static final boolean LAUNCH_DECOMPILERS_IN_NEW_PROCESS = false;  //TODO - work in progress
                                                                            // FernFlower is added

    //could be automatic by checking if it's loaded a class named whatever for a library
    //maybe it could be automatic with some maven plugin?
    public static final boolean FAT_JAR = true;

    //the automatic updater
    //SHADED_LIBRARIES must be false for the boot loader to startup
    //TODO this needs to be changed to support maven
    public static final boolean AUTOMATIC_LIBRARY_UPDATING = false;

    //version is set via maven
    public static final String VERSION = getVersion(BytecodeViewer.class.getPackage().getImplementationVersion());

    //CHECKSTYLE:OFF
    //dev mode is just a check for running via IDE
    public static boolean DEV_MODE;
    //CHECKSTYLE:ON

    //if true the version checker will prompt and ask how you would like to proceed
    public static final boolean FORCE_VERSION_CHECKER_PROMPT = false;

    public static final String FS = System.getProperty("file.separator");
    public static final String NL = System.getProperty("line.separator");
    public static final String[] SUPPORTED_FILE_EXTENSIONS = ResourceType.SUPPORTED_BCV_EXTENSION_MAP.keySet().toArray(new String[0]);
    public static final int ASM_VERSION = Opcodes.ASM9;

    public static final File BCV_DIR = resolveBCVRoot();
    public static final File RT_JAR = new File(System.getProperty("java.home") + FS + "lib" + FS + "rt.jar");
    public static final File JAVA_BINARY = new File(System.getProperty("java.home") + FS + "bin" + FS + "java.exe");
    public static final File JAVA_BINARY_NIX = new File(System.getProperty("java.home") + FS + "bin" + FS + "java");
    public static final File RT_JAR_DUMPED = new File(getBCVDirectory() + FS + "rt.jar");
    public static final String FILES_NAME = getBCVDirectory() + FS + "recentfiles.json";
    public static final String PLUGINS_NAME = getBCVDirectory() + FS + "recentplugins.json";
    public static final String SETTINGS_NAME = getBCVDirectory() + FS + "settings.bcv";
    public static final String TEMP_DIRECTORY = getBCVDirectory() + FS + "bcv_temp" + FS;
    public static final String SYSTEM_TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");
    public static final String LIBS_DIRECTORY = getBCVDirectory() + FS + "libs" + FS;
    public static String krakatauWorkingDirectory = getBCVDirectory() + FS + "krakatau_" + krakatauVersion;
    public static String enjarifyWorkingDirectory = getBCVDirectory() + FS + "enjarify_" + enjarifyVersion;

    //DEV_FLAG_* are used for enabling tooling / systems reserved for development.
    //As a precaution, all variables in here MUST ensure we are working in DEV_MODE only.
    //Nothing here is meant for user level production, only development level production.
    public static final boolean DEV_FLAG_DECOMPILERS_SIMULATED_ERRORS = DEV_MODE && false; //enable true / false to disable

    public static final PrintStream ERR = System.err;
    public static final PrintStream OUT = System.out;

    public static File resolveBCVRoot()
    {
        File defaultLocation = new File(System.getProperty("user.home") + FS + ".Bytecode-Viewer");

        //if BCV was previously installed using the default directory, continue to use that
        if (defaultLocation.exists())
            return defaultLocation;

        //handle XDG Base Directory - https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html
        if (isNix())
        {
            File homeLocal = new File(System.getProperty("user.home") + FS + ".local");
            if (homeLocal.exists())
                return new File(homeLocal, "share" + FS + ".Bytecode-Viewer");

            File homeConfig = new File(System.getProperty("user.home") + FS + ".config");
            if (homeConfig.exists())
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
        while (!BCV_DIR.exists())
            BCV_DIR.mkdirs();

        //hides the BCV directory
        if (isWindows() && !BCV_DIR.isHidden())
        {
            new Thread(() ->
            {
                try
                {
                    // Hide file by running attrib system command (on Windows)
                    Process p = new ProcessBuilder("attrib", "+H", BCV_DIR.getAbsolutePath()).start();
                }
                catch (Exception e)
                {
                    //ignore
                }
            }, "Hide BCV Dir").start();
        }

        return BCV_DIR.getAbsolutePath();
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
        if (FORCE_VERSION_CHECKER_PROMPT)
            return "1.0.0";

        if (mavenVersion == null)
        {
            DEV_MODE = true;
            return "Developer Mode";
        }

        return mavenVersion;
    }
}
