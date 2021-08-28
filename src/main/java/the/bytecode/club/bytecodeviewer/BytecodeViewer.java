package the.bytecode.club.bytecodeviewer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;
import me.konloch.kontainer.io.DiskReader;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.api.BCV;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.bootloader.Boot;
import the.bytecode.club.bytecodeviewer.bootloader.BootState;
import the.bytecode.club.bytecodeviewer.bootloader.InstallFatJar;
import the.bytecode.club.bytecodeviewer.bootloader.UpdateCheck;
import the.bytecode.club.bytecodeviewer.gui.MainViewerGUI;
import the.bytecode.club.bytecodeviewer.gui.components.ExtendedJOptionPane;
import the.bytecode.club.bytecodeviewer.gui.components.MultipleChoiceDialog;
import the.bytecode.club.bytecodeviewer.gui.resourcelist.ResourceListIconRenderer;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.TabbedPane;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ClassViewer;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ResourceViewer;
import the.bytecode.club.bytecodeviewer.obfuscators.mapping.Refactorer;
import the.bytecode.club.bytecodeviewer.plugin.PluginWriter;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.resources.importing.ImportResource;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.util.BootCheck;
import the.bytecode.club.bytecodeviewer.util.ClassFileUtils;
import the.bytecode.club.bytecodeviewer.util.LazyNameUtil;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;
import the.bytecode.club.bytecodeviewer.util.PingBack;
import the.bytecode.club.bytecodeviewer.util.SecurityMan;

import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static the.bytecode.club.bytecodeviewer.Constants.DEV_MODE;
import static the.bytecode.club.bytecodeviewer.Constants.FAT_JAR;
import static the.bytecode.club.bytecodeviewer.Constants.VERSION;
import static the.bytecode.club.bytecodeviewer.Constants.tempDirectory;

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
 *      + View>Visual Settings>Show Class Methods
 *      + Spam-clicking the refresh button will cause the swing thread to deadlock (Quickly opening resources used to also do this)
 *          This is caused by the ctrlMouseWheelZoom code, a temporary patch is just removing it worst case
 *
 * TODO API BUGS:
 *      + All of the plugins that modify code need to include BytecodeViewer.updateAllClassNodeByteArrays();
 *      + All of the plugins that do any code changes should also include BytecodeViewer.refreshAllTabs();
 *      + Anything using getLoadedClasses() needs to be replaced with the new API
 *      + Anything using blindlySearchForClassNode() should instead search through the resource container search function
 *      + BCV's classLoader should be destroyed each time a resource is added or removed
 *
 * TODO DarkLAF Specific Bugs:
 *      + JMenuBar can only be displayed on a JFrame, a work around is needed for this (Partially solved)
 *
 * TODO IN-PROGRESS:
 *      + Resource Exporter/Save/Decompile As Zip needs to be rewritten
 *      + Finish dragging code
 *      + Finish right-click tab menu detection
 *      + Fix hook inject for EZ-Injection
 *
 * TODO FEATURES:
 *      + On refresh save scroll position
 *      + Option to only compile currently viewed class (true by default)
 *      + CLI Headless needs to be supported
 *      + Add stackmapframes to bytecode decompiler
 *      + Add https://github.com/exbin/bined as the replacement Hex Viewer/Editor
 *      + Make the decompilers launch in a separate process
 *      + Add decompile as zip for krakatau-bytecode, jd-gui and smali for CLI
 *      + Add decompile all as zip for CLI
 *      + Console on the Main Viewer UI
 *      + Font settings
 *
 *  TODO IDEAS:
 *      + App Bundle Support
 *      + Add JEB decompiler optionally, requires them to add jeb library jar
 *      + Add the setting to force all non-class resources to be opened with the Hex Viewer
 *          ^ Optionally a right-click menu open-as would work inside of the resource list
 *      + Allow class files to be opened without needing the .class extension
 *          ^ Easiest way to do this is to read the file header CAFEBABE on resource view
 *      + Add BCEL Support:
 *          ^ https://github.com/ptnkjke/Java-Bytecode-Editor visualizer as a plugin
 *      + Add animated GIF support to image viewer
 *      + Add drag support to images (allow not only to zoom, but also to drag the image)
 *
 * @author Konloch
 * @author The entire BCV community
 */

public class BytecodeViewer
{
    //TODO fix this for tab dragging & better tab controls
    public static boolean EXPERIMENTAL_TAB_CODE = false;
    
    //the launch args called on BCV
    public static String[] launchArgs;
    
    //the GUI reference
    public static MainViewerGUI viewer;
    
    //All of the opened resources (Files/Classes/Etc)
    public static Map<String,ResourceContainer> resourceContainers = new LinkedHashMap<>();
    
    //All of the created processes (Decompilers/etc)
    public static List<Process> createdProcesses = new ArrayList<>();
    
    //Security Manager for dynamic analysis debugging
    public static SecurityMan sm = new SecurityMan();
    
    //Refactorer
    public static Refactorer refactorer = new Refactorer();
    
    //GSON Reference
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    //Threads
    private static final Thread versionChecker = new Thread(new UpdateCheck(), "Version Checker");
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
        if (FAT_JAR)
            System.out.print(" [Fat Jar]");
        
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
            
            //set swing specific system properties
            System.setProperty("swing.aatext", "true");
            
            //setup swing components
            viewer = new MainViewerGUI();
            SwingUtilities.updateComponentTreeUI(viewer);
            
            //load settings and set swing components state
            SettingsSerializer.loadSettings();
            Configuration.bootState = BootState.SETTINGS_LOADED;
            
            //set translation language
            if (!Settings.hasSetLanguageAsSystemLanguage)
                MiscUtils.setLanguage(MiscUtils.guessLanguage());
    
            //handle CLI
            int CLI = CommandLineInput.parseCommandLine(args);
            if (CLI == CommandLineInput.STOP)
                return;
    
            //load with shaded libraries
            if (FAT_JAR)
            {
                installFatJar.start();
            }
            else //load through bootloader
            {
                bootCheck.start();
                Boot.boot(args, CLI != CommandLineInput.GUI);
            }
            
            //CLI arguments say spawn the GUI
            if (CLI == CommandLineInput.GUI)
            {
                BytecodeViewer.boot(false);
                Configuration.bootState = BootState.GUI_SHOWING;
            }
            else //CLI arguments say keep it CLI
            {
                BytecodeViewer.boot(true);
                CommandLineInput.executeCommandLine(args);
            }
        }
        catch (Exception e)
        {
            BytecodeViewer.handleException(e);
        }
    }
    
    /**
     * Boot after all of the libraries have been loaded
     *
     * @param cli is it running CLI mode or not
     */
    public static void boot(boolean cli)
    {
        //delete files in the temp folder
        cleanupAsync();
        
        //shutdown hooks
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            for (Process proc : createdProcesses)
                proc.destroy();
            
            SettingsSerializer.saveSettings();
            cleanup();
        }, "Shutdown Hook"));
    
        //setup the viewer
        viewer.calledAfterLoad();
        
        //setup the recent files
        Settings.resetRecentFilesMenu();
    
        //ping back once on first boot to add to global user count
        if (!Configuration.pingback)
        {
            pingBack.start();
            Configuration.pingback = true;
        }
        
        //version checking
        if (viewer.updateCheck.isSelected() && !DEV_MODE)
            versionChecker.start();
    
        //show the main UI
        if (!cli)
            viewer.setVisible(true);
    
        //print startup time
        System.out.println("Start up took " + ((System.currentTimeMillis() - Configuration.start) / 1000) + " seconds");
        
        //request focus on GUI for hotkeys on start
        if (!cli)
            viewer.requestFocus();
        
        //open files from launch args
        if (!cli)
            if (launchArgs.length >= 1)
                for (String s : launchArgs)
                    openFiles(new File[]{new File(s)}, true);
    }
    
    /**
     * Adds a resource container to BCVs resource container list
     */
    public static void addResourceContainer(ResourceContainer container)
    {
        resourceContainers.put(container.name, container);
        SwingUtilities.invokeLater(() ->
        {
            try {
                viewer.resourcePane.addResourceContainer(container);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Returns true if there is at least one file resource loaded
     */
    public static boolean hasResources()
    {
        return !resourceContainers.isEmpty();
    }
    
    /**
     * Returns true if there is currently a tab open with a resource inside of it
     */
    public static boolean hasActiveResource()
    {
        return getActiveResource() != null;
    }
    
    /**
     * Returns true if there is currently a tab open with a resource inside of it
     */
    public static boolean isActiveResourceClass()
    {
        ResourceViewer resource = getActiveResource();
        return resource instanceof ClassViewer;
    }
    
    /**
     * Returns the currently opened & viewed resource
     */
    public static ResourceViewer getActiveResource()
    {
        return BytecodeViewer.viewer.workPane.getActiveResource();
    }
    
    /**
     * Returns the currently opened ClassNode
     *
     * @return the currently opened ClassNode
     */
    public static ClassNode getCurrentlyOpenedClassNode()
    {
        return getActiveResource().resource.getResourceClassNode();
    }
    
    /**
     * Returns the ClassNode by the specified name
     * <p>
     * TODO anything relying on this should be rewritten to search using the resource container
     *
     * @param name the class name
     * @return the ClassNode instance
     */
    @Deprecated
    public static ClassNode blindlySearchForClassNode(String name)
    {
        for (ResourceContainer container : resourceContainers.values())
        {
            ClassNode node = container.getClassNode(name);
            
            if (node != null)
                return node;
        }
    
        return null;
    }
    
    /**
     * Returns the resource container by the specific name
     */
    public static ResourceContainer getFileContainer(String name)
    {
        for (ResourceContainer container : resourceContainers.values())
            if (container.name.equals(name))
                return container;
    
        return null;
    }
    
    /**
     * Returns all of the loaded resource containers
     */
    public static Collection<ResourceContainer> getResourceContainers()
    {
        return resourceContainers.values();
    }
    
    /**
     * Grabs the file contents of the loaded resources.
     * <p>
     * TODO anything relying on this should be rewritten to use the resource container's getFileContents
     *
     * @param name the file name
     * @return the file contents as a byte[]
     */
    @Deprecated
    public static byte[] getFileContents(String name)
    {
        for (ResourceContainer container : resourceContainers.values())
            if (container.resourceFiles.containsKey(name))
                return container.resourceFiles.get(name);
    
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
     * Gets all of the loaded classes as an array list
     *
     * TODO: remove this and replace it with:
     * BytecodeViewer.getResourceContainers().forEach(container -> {
     *      execute(new ArrayList<>(container.resourceClasses.values()));
     * });
     *
     * @return the loaded classes as an array list
     */
    @Deprecated
    public static List<ClassNode> getLoadedClasses()
    {
        List<ClassNode> a = new ArrayList<>();
    
        for (ResourceContainer container : resourceContainers.values())
            for (ClassNode c : container.resourceClasses.values())
                if (!a.contains(c))
                    a.add(c);
    
        return a;
    }
    
    /**
     * Called any time refresh is called to automatically compile all of the compilable panes that're opened.
     */
    public static boolean autoCompileSuccessful()
    {
        if (!BytecodeViewer.viewer.autoCompileOnRefresh.isSelected())
            return true;
        
        try
        {
            return compile(false, false);
        }
        catch (NullPointerException ignored)
        {
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
                
                if (noErrors && !cv.bytecodeViewPanel1.compile())
                    noErrors = false;
                if (noErrors && !cv.bytecodeViewPanel2.compile())
                    noErrors = false;
                if (noErrors && !cv.bytecodeViewPanel3.compile())
                    noErrors = false;
                
                if (cv.bytecodeViewPanel1.textArea != null && cv.bytecodeViewPanel1.textArea.isEditable())
                    actuallyTried = true;
                if (cv.bytecodeViewPanel2.textArea != null && cv.bytecodeViewPanel2.textArea.isEditable())
                    actuallyTried = true;
                if (cv.bytecodeViewPanel3.textArea != null && cv.bytecodeViewPanel3.textArea.isEditable())
                    actuallyTried = true;
            }
        }
    
        if (message)
        {
            if (actuallyTried)
            {
                if (noErrors && successAlert)
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
        
        try
        {
            PluginWriter writer = new PluginWriter(DiskReader.loadAsString(file.getAbsolutePath()), file.getName());
            writer.setSourceFile(file);
            writer.setVisible(true);
        }
        catch (Exception e)
        {
            BytecodeViewer.handleException(e);
        }
        
        Settings.addRecentPlugin(file);
    }
    
    /**
     * Send a message to alert the user
     *
     * @param message the message you need to send
     */
    public static void showMessage(String message)
    {
        ExtendedJOptionPane.showMessageDialog(viewer, message);
    }
    
    /**
     * Send a message to alert the user
     */
    public static String showInput(String message)
    {
        return ExtendedJOptionPane.showInputDialog(viewer, message);
    }
    
    /**
     * Send a message to alert the user
     */
    public static String showInput(String message, String title, String initialMessage)
    {
        return (String) ExtendedJOptionPane.showInputDialog(viewer, message, title,
                QUESTION_MESSAGE, null, null, initialMessage);
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
     * Returns true if there are no loaded resource classes
     */
    public static boolean promptIfNoLoadedClasses()
    {
        if (BytecodeViewer.getLoadedClasses().isEmpty())
        {
            BytecodeViewer.showMessage(TranslatedStrings.FIRST_OPEN_A_CLASS.toString());
            return true;
        }
        
        return false;
    }
    
    /**
     * Returns true if there are no loaded resource classes
     */
    public static boolean promptIfNoLoadedResources()
    {
        if (BytecodeViewer.resourceContainers.isEmpty())
        {
            BytecodeViewer.showMessage(TranslatedStrings.FIRST_OPEN_A_RESOURCE.toString());
            return true;
        }
        
        return false;
    }
    
    /**
     * Handle the exception by creating a new window for bug reporting
     */
    public static void handleException(Throwable t)
    {
        handleException(t, ExceptionUI.KONLOCH);
    }
    
    /**
     * Handle the exception by creating a new window for bug reporting
     */
    public static void handleException(Throwable t, String author)
    {
        new ExceptionUI(t, author);
    }
    
    /**
     * Refreshes the title on all of the opened tabs
     */
    public static void updateAllClassNodeByteArrays()
    {
        resourceContainers.values().forEach(ResourceContainer::updateClassNodeBytes);
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
     * Refreshes the title on all of the opened tabs
     */
    public static void refreshAllTabs()
    {
        new Thread(()->
        {
            updateBusyStatus(true);
            for (int i = 0; i < BytecodeViewer.viewer.workPane.tabs.getTabCount(); i++)
            {
                ResourceViewer viewer = ((TabbedPane) BytecodeViewer.viewer.workPane.tabs.getTabComponentAt(i)).resource;
                viewer.refresh(null);
            }
            updateBusyStatus(false);
        }, "Refresh All Tabs").start();
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
            MultipleChoiceDialog dialog = new MultipleChoiceDialog(TranslatedStrings.RESET_TITLE.toString(),
                    TranslatedStrings.RESET_CONFIRM.toString(),
                    new String[]{TranslatedStrings.YES.toString(), TranslatedStrings.NO.toString()});
        
            if (dialog.promptChoice() != 0)
                return;
        }
    
        resetWorkspace();
    }
    
    /**
     * Resets the workspace
     */
    public static void resetWorkspace()
    {
        BytecodeViewer.resourceContainers.clear();
        LazyNameUtil.reset();
        BytecodeViewer.viewer.resourcePane.resetWorkspace();
        BytecodeViewer.viewer.workPane.resetWorkspace();
        BytecodeViewer.viewer.searchBoxPane.resetWorkspace();
        BCV.getClassNodeLoader().clear();
        ResourceListIconRenderer.iconCache.clear();
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
     */
    public static void exit(int i) { }
}
