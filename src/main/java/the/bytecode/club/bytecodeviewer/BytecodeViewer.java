package the.bytecode.club.bytecodeviewer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bootloader.Boot;
import the.bytecode.club.bytecodeviewer.api.ClassNodeLoader;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.gui.components.*;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.TabbedPane;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ClassViewer;
import the.bytecode.club.bytecodeviewer.gui.MainViewerGUI;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ResourceViewer;
import the.bytecode.club.bytecodeviewer.obfuscators.mapping.Refactorer;
import the.bytecode.club.bytecodeviewer.plugin.PluginManager;
import the.bytecode.club.bytecodeviewer.util.*;
import the.bytecode.club.bytecodeviewer.resources.importing.ImportResource;

import static the.bytecode.club.bytecodeviewer.Constants.*;
import static the.bytecode.club.bytecodeviewer.Settings.addRecentPlugin;
import static the.bytecode.club.bytecodeviewer.util.MiscUtils.guessLanguage;

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
 * A lightweight Java Reverse Engineering suite, developed by Konloch - http://konloch.me
 *
 * All you have to do is add a jar or class file into the workspace,
 * select the file you want then it will start decompiling the class in the background.
 * When it's done it will show the Source code, Bytecode and Hexcode of the class file you chose.
 *
 * There is also a plugin system that will allow you to interact with the loaded classfiles.
 * For example you can write a String deobfuscator, a malicious code searcher,
 * or anything else you can think of.
 *
 * You can either use one of the pre-written plugins, or write your own. It supports java scripting.
 * Once a plugin is activated, it will send a ClassNode ArrayList of every single class loaded in the
 * file system to the execute function, this allows the user to handle it completely using ASM.
 *
 * Are you a Java Reverse Engineer? Or maybe you want to learn Java Reverse Engineering?
 * Join The Bytecode Club, we're noob friendly, and censorship free.
 * http://the.bytecode.club
 *
 * TODO BUGS:
 *      + Panes that currently are being opened/decompiled should not be able to be refreshed - Causes a lock
 *      + View>Visual Settings>Show Class Methods
 *      + Spam-clicking the refresh button will cause the swing thread to deadlock (Quickly opening resources used to also do this)
 *          This is caused by the ctrlMouseWheelZoom code, a temporary patch is just removing it worst case
 *      + Versioning and updating need to be fixed
 *      + Fix classfile searcher
 *      + Smali Assembly compile - Needs to be fixed
 *
 * TODO IN-PROGRESS:
 *      + While loading an external plugin it should check if its java or JS, if so it should ask if you'd like to run or edit the plugin using the PluginWriter
 *      + Resource Importer needs to be rewritten to handle resources better
 *      + Resource Exporter/Save/Decompile As Zip needs to be rewrittern
 *      + Finish dragging code
 *      + Finish right-click tab menu detection
 *      + Fix hook inject for EZ-Injection
 *
 * TODO FEATURES:
 *      + CLI Headless needs to be supported
 *      + Add stackmapframes to bytecode decompiler
 *      + Add JEB decompiler optionally, requires them to add jeb library jar
 *      + Add https://github.com/exbin/bined as the replacement Hed Viewer/Editor
 *      + Make the decompilers launch in a separate process
 *      + Make it use that global last used inside of export as jar
 *      + Make zipfile not include the decode shit
 *      + Make ez-injection plugin console show all sys.out calls
 *      + Add decompile as zip for krakatau-bytecode, jd-gui and smali for CLI
 *      + Add decompile all as zip for CLI
 *
 *  TODO IDEAS:
 *      + App Bundle Support
 *      + Add the setting to force all non-class resources to be opened with the Hex Viewer
 *          ^ Optionally a right-click menu open-as would work inside of the resource list
 *      + Allow class files to be opened without needing the .class extension
 *          ^ Easiest way to do this is to read the file header CAFEBABE on resource view
 *      + Look into removing the loaded classes from inside the FileContainer & then generate the ClassNodes on demand
 *          ^ This has the added benefit of only extracting on decompilation/when needed. It would also mean everything
 *            could be treated as byte[] file resources instead of juggling between Classes and File resources.
 *          ^ An added bonus would be you could also support BCEL (along with other bytecode manipulation libraries)
 *            and add support for https://github.com/ptnkjke/Java-Bytecode-Editor visualizer as a plugin
 *
 * @author Konloch
 * @author The entire BCV community
 */

public class BytecodeViewer
{
    public static boolean EXPERIMENTAL_TAB_CODE = false;
    public static boolean DEV_MODE = false; //if true error streams as preserved
    
    public static String[] launchArgs;
    public static MainViewerGUI viewer = null;
    public static ClassNodeLoader loader = new ClassNodeLoader(); //might be insecure due to assholes targeting BCV,
    public static SecurityMan sm = new SecurityMan(); //might be insecure due to assholes targeting BCV,
    public static Refactorer refactorer = new Refactorer();
    public static List<FileContainer> files = new ArrayList<>(); //all of BCV's loaded files/classes/etc
    public static List<Process> createdProcesses = new ArrayList<>();
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    private static final Thread versionChecker = new Thread(new VersionChecker(), "Version Checker");
    private static final Thread pingBack = new Thread(new PingBack(), "Pingback");
    private static final Thread installFatJar = new Thread(new InstallFatJar(), "Install Fat-Jar");
    private static final Thread bootCheck = new Thread(new BootCheck(), "Boot Check");

    /**
     * Main startup
     *
     * @param args files you want to open or CLI
     */
    public static void main(String[] args)
    {
        launchArgs = args;
        
        //welcome message
        System.out.print("Bytecode Viewer " + VERSION);
        if(FAT_JAR)
            System.out.print(" [FatJar]");
        
        System.out.println(" - Created by @Konloch");
        System.out.println("https://bytecodeviewer.com - https://the.bytecode.club");
        
        //set the security manager
        System.setSecurityManager(sm);
        
        try
        {
            //precache settings file
            SettingsSerializer.preloadSettingsFile();
            //setup look and feel
            Configuration.lafTheme.setLAF();
            
            if (PREVIEW_COPY && !CommandLineInput.containsCommand(args))
                showMessage("WARNING: This is a preview/dev copy, you WON'T be alerted when " + VERSION + " is "
                        + "actually out if you use this." + nl +
                        "Make sure to watch the repo: https://github.com/Konloch/bytecode-viewer for " + VERSION + "'s release");
            
            //set swing specific system properties
            System.setProperty("swing.aatext", "true");
            
            //setup swing components
            viewer = new MainViewerGUI();
            SwingUtilities.updateComponentTreeUI(viewer);
            
            //load settings and set swing components state
            SettingsSerializer.loadSettings();
            
            //set translation language
            if(!Settings.hasSetLanguageAsSystemLanguage)
                MiscUtils.setLanguage(guessLanguage());
    
            //handle CLI
            int CLI = CommandLineInput.parseCommandLine(args);

            if (CLI == CommandLineInput.STOP)
                return;

            if (!FAT_JAR)
            {
                bootCheck.start();

                Boot.boot(args, CLI != CommandLineInput.OPEN_FILE);
            }
            else
                installFatJar.start();

            if (CLI == CommandLineInput.OPEN_FILE)
                BytecodeViewer.boot(false);
            else
            {
                BytecodeViewer.boot(true);
                CommandLineInput.executeCommandLine(args);
            }
        }
        catch (Exception e)
        {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }
    }

    /**
     * Boot after all of the libraries have been loaded
     *
     * @param cli is it running CLI mode or not
     */
    public static void boot(boolean cli)
    {
        cleanupAsync();
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            for (Process proc : createdProcesses)
                proc.destroy();
            SettingsSerializer.saveSettings();
            cleanup();
        }, "Shutdown Hook"));

        viewer.calledAfterLoad();
        Settings.resetRecentFilesMenu();

        //ping back once on first boot to add to global user count
        if (!Configuration.pingback)
        {
            pingBack.start();
            Configuration.pingback = true;
        }

        if (viewer.updateCheck.isSelected())
            versionChecker.start();

        if (!cli)
            viewer.setVisible(true);

        System.out.println("Start up took " + ((System.currentTimeMillis() - Configuration.start) / 1000) + " seconds");

        if (!cli)
            if (launchArgs.length >= 1)
                for (String s : launchArgs)
                    openFiles(new File[]{new File(s)}, true);
    }

    /**
     * Returns the java command it can use to launch the decompilers
     *
     * @return
     */
    public static synchronized String getJavaCommand()
    {
        sm.stopBlocking();
        try
        {
            ProcessBuilder pb = new ProcessBuilder("java", "-version");
            pb.start();
            return "java"; //java is set
        }
        catch (Exception e) //ignore
        {
            sm.setBlocking();
            boolean empty = Configuration.java.isEmpty();
            while (empty)
            {
                showMessage("You need to set your Java path, this requires the JRE to be downloaded." +
                        nl + "(C:/Program Files/Java/JDK_xx/bin/java.exe)");
                viewer.selectJava();
                empty = Configuration.java.isEmpty();
            }
        }
        finally
        {
            sm.setBlocking();
        }
        
        return Configuration.java;
    }

    /**
     * Returns the currently opened ClassNode
     *
     * @return the currently opened ClassNode
     */
    public static ClassNode getCurrentlyOpenedClassNode() {
        return viewer.workPane.getCurrentViewer().cn;
    }

    /**
     * Returns the ClassNode by the specified name
     *
     * @param name the class name
     * @return the ClassNode instance
     */
    public static ClassNode getClassNode(String name)
    {
        for (FileContainer container : files)
            for (ClassNode c : container.classes)
                if (c.name.equals(name))
                    return c;

        return null;
    }
    
    /**
     * Returns the File Container by the specific name
     */
    public static FileContainer getFileContainer(String name)
    {
        for (FileContainer container : files)
            if (container.name.equals(name))
                return container;

        return null;
    }
    
    /**
     * Returns all of the loaded File Containers
     */
    public static List<FileContainer> getFiles() {
        return files;
    }
    
    /**
     * Returns a ClassNode by name specific namefrom a specific File Container
     */
    public static ClassNode getClassNode(FileContainer container, String name)
    {
        for (ClassNode c : container.classes)
            if (c.name.equals(name))
                return c;

        return null;
    }

    /**
     * Grabs the file contents of the loaded resources.
     *
     * @param name the file name
     * @return the file contents as a byte[]
     */
    public static byte[] getFileContents(String name)
    {
        for (FileContainer container : files)
            if (container.files.containsKey(name))
                return container.files.get(name);

        return null;
    }
    
    /**
     * Grab the byte array from the loaded Class object by getting the resource from the classloader
     */
    public static byte[] getClassFileBytes(Class<?> clazz) throws IOException
    {
        return ClassFileUtils.getClassFileBytes(clazz);
    }

    /**
     * Replaces an old node with a new instance
     *
     * @param oldNode the old instance
     * @param newNode the new instance
     */
    public static void updateNode(ClassNode oldNode, ClassNode newNode)
    {
        for (FileContainer container : files)
            if (container.classes.remove(oldNode))
                container.classes.add(newNode);
    }

    /**
     * Gets all of the loaded classes as an array list
     *
     * @return the loaded classes as an array list
     */
    public static ArrayList<ClassNode> getLoadedClasses()
    {
        ArrayList<ClassNode> a = new ArrayList<>();

        for (FileContainer container : files)
            for (ClassNode c : container.classes)
                if (!a.contains(c))
                    a.add(c);

        return a;
    }

    /**
     * Called any time refresh is called to automatically compile all of the compilable panes that're opened.
     */
    public static boolean autoCompileSuccessful()
    {
        if(!BytecodeViewer.viewer.autoCompileOnRefresh.isSelected())
            return true;
        
        try {
            return compile(false, false);
        } catch (NullPointerException ignored) {
            return false;
        }
    }

    /**
     * Compile all of the compilable panes that're opened.
     *
     * @param message if it should send a message saying it's compiled sucessfully.
     * @return true if no errors, false if it failed to compile.
     */
    public static boolean compile(boolean message, boolean successAlert)
    {
        BytecodeViewer.updateBusyStatus(true);
        boolean noErrors = true;
        boolean actuallyTried = false;
    
        for (java.awt.Component c : BytecodeViewer.viewer.workPane.getLoadedViewers())
        {
            if (c instanceof ClassViewer)
            {
                ClassViewer cv = (ClassViewer) c;
                
                if(noErrors && !cv.resourceViewPanel1.compile())
                    noErrors = false;
                if(noErrors && !cv.resourceViewPanel2.compile())
                    noErrors = false;
                if(noErrors && !cv.resourceViewPanel3.compile())
                    noErrors = false;
                
                if(cv.resourceViewPanel1.textArea != null && cv.resourceViewPanel1.textArea.isEditable())
                    actuallyTried = true;
                if(cv.resourceViewPanel2.textArea != null && cv.resourceViewPanel2.textArea.isEditable())
                    actuallyTried = true;
                if(cv.resourceViewPanel3.textArea != null && cv.resourceViewPanel3.textArea.isEditable())
                    actuallyTried = true;
            }
        }

        if (message)
        {
            if (actuallyTried)
            {
                if(noErrors && successAlert)
                    BytecodeViewer.showMessage("Compiled Successfully.");
            }
            else
            {
                BytecodeViewer.showMessage("You have no editable panes opened, make one editable and try again.");
            }
        }
        
        BytecodeViewer.updateBusyStatus(false);
        return true;
    }

    /**
     * Opens a file, optional if it should append to the recent files menu
     *
     * @param files       the file(s) you wish to open
     * @param recentFiles if it should append to the recent files menu
     */
    public static void openFiles(final File[] files, boolean recentFiles)
    {
        if (recentFiles)
        {
            for (File f : files)
                if (f.exists())
                    Settings.addRecentFile(f);
    
            SettingsSerializer.saveSettingsAsync();
        }

        BytecodeViewer.updateBusyStatus(true);
        Configuration.needsReDump = true;
        Thread t = new Thread(new ImportResource(files), "Import Resource");
        t.start();
    }

    /**
     * Starts the specified plugin
     *
     * @param file the file of the plugin
     */
    public static void startPlugin(File file)
    {
        if (!file.exists())
        {
            BytecodeViewer.showMessage("The plugin file " + file.getAbsolutePath() + " could not be found.");
            Settings.removeRecentPlugin(file);
            return;
        }

        try {
            PluginManager.runPlugin(file);
        } catch (Throwable e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }
        addRecentPlugin(file);
    }

    /**
     * Send a message to alert the user
     *
     * @param message the message you need to send
     */
    public static void showMessage(String message)
    {
        BetterJOptionPane.showMessageDialog(viewer, message);
    }
    
    /**
     * Alerts the user the program is running something in the background
     */
    public static void updateBusyStatus(boolean busyStatus)
    {
        viewer.updateBusyStatus(busyStatus);
    }
    
    /**
     * Clears all active busy status icons
     */
    public static void clearBusyStatus()
    {
        viewer.clearBusyStatus();
    }
    
    /**
     * Refreshes the title on all of the opened tabs
     */
    public static void refreshAllTabTitles()
    {
        for(int i = 0; i < BytecodeViewer.viewer.workPane.tabs.getTabCount(); i++)
        {
            ResourceViewer viewer = ((TabbedPane) BytecodeViewer.viewer.workPane.tabs.getTabComponentAt(i)).resource;
            viewer.refreshTitle();
        }
    }

    /**
     * Resets the workspace with optional user input required
     *
     * @param ask if should require user input or not
     */
    public static void resetWorkspace(boolean ask)
    {
        if (ask)
        {
            MultipleChoiceDialogue dialogue = new MultipleChoiceDialogue("Bytecode Viewer - Reset Workspace",
                    "Are you sure you want to reset the workspace?" +
                            "\n\rIt will also reset your file navigator and search.",
                    new String[]{"Yes", "No"});
        
            if (dialogue.promptChoice() != 0)
                return;
        }
    
        BCVResourceUtils.resetWorkspace();
    }
    
    /**
     * Clears the temp directory
     */
    public static void cleanupAsync()
    {
        Thread cleanupThread = new Thread(BytecodeViewer::cleanup, "Cleanup");
        cleanupThread.start();
    }

    /**
     * Clears the temp directory
     */
    public static void cleanup()
    {
        File tempF = new File(tempDirectory);

        try {
            FileUtils.deleteDirectory(tempF);
        } catch (Exception ignored) { }

        while (!tempF.exists()) // keep making dirs
            tempF.mkdir();
    }
    
    /**
     * because Smali and Baksmali System.exit if it failed
     *
     * @param i
     */
    public static void exit(int i) { }
}
