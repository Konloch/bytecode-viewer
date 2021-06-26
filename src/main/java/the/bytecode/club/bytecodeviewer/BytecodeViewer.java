package the.bytecode.club.bytecodeviewer;

import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.*;

import me.konloch.kontainer.io.HTTPRequest;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bootloader.Boot;
import the.bytecode.club.bytecodeviewer.api.ClassNodeLoader;
import the.bytecode.club.bytecodeviewer.compilers.Compiler;
import the.bytecode.club.bytecodeviewer.gui.components.*;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.ResourcePanelCompileMode;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.TabbedPane;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ClassViewer;
import the.bytecode.club.bytecodeviewer.gui.resourcelist.ResourceListPane;
import the.bytecode.club.bytecodeviewer.gui.MainViewerGUI;
import the.bytecode.club.bytecodeviewer.gui.resourcesearch.SearchBoxPane;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.WorkPaneMainComponent;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ResourceViewer;
import the.bytecode.club.bytecodeviewer.obfuscators.mapping.Refactorer;
import the.bytecode.club.bytecodeviewer.plugin.PluginManager;
import the.bytecode.club.bytecodeviewer.util.*;
import the.bytecode.club.bytecodeviewer.resources.importing.ImportResource;

import static the.bytecode.club.bytecodeviewer.Constants.*;

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
 *      + Synchronized scrolling is broken
 *      + Spam-clicking the refresh button will cause the swing thread to deadlock (Quickly opening resources used to also do this)
 *          This is caused by the ctrlMouseWheelZoom code, a temporary patch is just removing it worst case
 *      + Fix classfile searcher
 *      + Smali Assembly compile - Needs to be fixed
 *      + Krakatau Assembly compile - Needs to be fixed
 *
 * TODO IN-PROGRESS:
 *      + Finish dragging code
 *      + Finish right-click tab menu detection
 *      + Fix hook inject for EZ-Injection
 *
 * TODO FEATURES:
 *      + Add stackmapframes to bytecode decompiler
 *      + Add JEB decompiler optionally, requires them to add jeb library jar
 *      + Add https://github.com/ptnkjke/Java-Bytecode-Editor visualize as a plugin
 *      + Add https://github.com/exbin/bined as the replacement Hed Viewer/Editor
 *      + Make the decompilers launch in a separate process
 *      + Make it use that global last used inside of export as jar
 *      + Make zipfile not include the decode shit
 *      + Make ez-injection plugin console show all sys.out calls
 *      + Add decompile as zip for krakatau-bytecode, jd-gui and smali for CLI
 *      + Add decompile all as zip for CLI
 *
 * @author Konloch
 * @author The entire BCV community
 */

public class BytecodeViewer
{
    public static String[] args;
    public static MainViewerGUI viewer = null;
    public static ClassNodeLoader loader = new ClassNodeLoader(); //might be insecure due to assholes targeting BCV,
    public static SecurityMan sm = new SecurityMan(); //might be insecure due to assholes targeting BCV, however
    public static Refactorer refactorer = new Refactorer();
    public static List<FileContainer> files = new ArrayList<>(); //all of BCV's loaded files/classes/etc
    public static List<Process> createdProcesses = new ArrayList<>();
    public static final DecompilerViewComponent krakatau = new DecompilerViewComponent("Krakatau", true);
    public static final boolean EXPERIMENTAL_TAB_CODE = false;
    
    /**
     * The version checker thread
     */
    private static final Thread versionChecker = new Thread(new VersionChecker());

    /**
     * Pings back to bytecodeviewer.com to be added into the total running statistics
     */
    private static final Thread pingBack = new Thread(() -> {
        try {
            new HTTPRequest(new URL("https://bytecodeviewer.com/add.php")).read();
        } catch (Exception e) {
            Configuration.pingback = false;
        }
    });

    /**
     * Downloads & installs the krakatau & enjarify zips
     */
    private static final Thread installFatJar = new Thread(() -> {
        try {
            if (OFFLINE_MODE) {
                Boot.dropKrakatau();
                Boot.dropEnjarify();
            } else {
                Boot.populateUrlList();
                Boot.populateLibsDirectory();
                Boot.downloadZipsOnly();
                Boot.checkKrakatau();
                Boot.checkEnjarify();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    });

    /**
     * Used to check incase booting failed for some reason, this kicks in as a fail safe
     */
    private static final Thread bootCheck = new Thread(new BootCheck());

    /**
     * Main startup
     *
     * @param args files you want to open or CLI
     */
    public static void main(String[] args) {
        BytecodeViewer.args = args;
        System.out.println("https://the.bytecode.club - Created by @Konloch - Bytecode Viewer " + VERSION + ", " + "Fat-Jar: " + FAT_JAR);
        System.setSecurityManager(sm);
        
        try {
            //precache settings file
            Settings.preloadSettingsFile();
            //setup look and feel
            Configuration.lafTheme.setLAF();
            
            if (PREVIEW_COPY && !CommandLineInput.containsCommand(args))
                showMessage("WARNING: This is a preview/dev copy, you WON'T be alerted when " + VERSION + " is "
                        + "actually out if you use this." + nl +
                        "Make sure to watch the repo: https://github.com/Konloch/bytecode-viewer for " + VERSION + "'s release");

            viewer = new MainViewerGUI();
            Settings.loadSettings();

            int CLI = CommandLineInput.parseCommandLine(args);

            if (CLI == CommandLineInput.STOP)
                return;

            if (!FAT_JAR) {
                bootCheck.start();

                Boot.boot(args, CLI != CommandLineInput.OPEN_FILE);
            } else
                installFatJar.start();

            if (CLI == CommandLineInput.OPEN_FILE)
                BytecodeViewer.boot(false);
            else {
                BytecodeViewer.boot(true);
                CommandLineInput.executeCommandLine(args);
            }
        } catch (Exception e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }
    }

    /**
     * Boot after all of the libraries have been loaded
     *
     * @param cli is it running CLI mode or not
     */
    public static void boot(boolean cli) {
        cleanup();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Process proc : createdProcesses)
                proc.destroy();
            Settings.saveSettings();
            cleanup();
        }));

        viewer.calledAfterLoad();
        Constants.resetRecentFilesMenu();

        if (!Configuration.pingback) {
            pingBack.start();
            Configuration.pingback = true;
        }

        if (viewer.updateCheck.isSelected())
            versionChecker.start();

        if (!cli)
            viewer.setVisible(true);

        System.out.println("Start up took " + ((System.currentTimeMillis() - Configuration.start) / 1000) + " seconds");

        if (!cli)
            if (args.length >= 1)
                for (String s : args)
                    openFiles(new File[]{new File(s)}, true);
    }

    /**
     * because Smali and Baksmali System.exit if it failed
     *
     * @param i
     */
    public static void exit(int i) {

    }

    /**
     * Returns the java command it can use to launch the decompilers
     *
     * @return
     */
    public static synchronized String getJavaCommand() {
        try {
            sm.stopBlocking();
            ProcessBuilder pb = new ProcessBuilder("java", "-version");
            pb.start();
            sm.setBlocking();
            return "java"; //java is set
        } catch (Exception e) { //ignore
            sm.setBlocking();
            boolean empty = Configuration.java.isEmpty();
            while (empty) {
                showMessage("You need to set your Java path, this requires the JRE to be downloaded." + nl +
                        "(C:/Program Files/Java/JDK_xx/bin/java.exe)");
                viewer.selectJava();
                empty = Configuration.java.isEmpty();
            }
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
    public static ClassNode getClassNode(String name) {
        for (FileContainer container : files)
            for (ClassNode c : container.classes)
                if (c.name.equals(name))
                    return c;

        return null;
    }

    public static FileContainer getFileContainer(String name) {
        for (FileContainer container : files)
            if (container.name.equals(name))
                return container;

        return null;
    }

    public static List<FileContainer> getFiles() {
        return files;
    }

    public static ClassNode getClassNode(FileContainer container, String name) {
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
    public static byte[] getFileContents(String name) {
        for (FileContainer container : files)
            if (container.files.containsKey(name))
                return container.files.get(name);

        return null;
    }
    
    /**
     * Grab the byte array from the loaded Class object
     *
     * @param clazz
     * @return
     * @throws IOException
     */
    public static byte[] getClassFile(Class<?> clazz) throws IOException {
        try (InputStream is = clazz.getResourceAsStream(
                "/" + clazz.getName().replace('.', '/') + ".class");
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            int r;
            byte[] buffer = new byte[8192];
            while ((r = Objects.requireNonNull(is).read(buffer)) >= 0) {
                baos.write(buffer, 0, r);
            }
            return baos.toByteArray();
        }
    }

    /**
     * Replaces an old node with a new instance
     *
     * @param oldNode the old instance
     * @param newNode the new instance
     */
    public static void updateNode(ClassNode oldNode, ClassNode newNode) {
        for (FileContainer container : files) {
            if (container.classes.remove(oldNode))
                container.classes.add(newNode);
        }
    }

    /**
     * Gets all of the loaded classes as an array list
     *
     * @return the loaded classes as an array list
     */
    public static ArrayList<ClassNode> getLoadedClasses() {
        ArrayList<ClassNode> a = new ArrayList<>();

        for (FileContainer container : files)
            for (ClassNode c : container.classes)
                if (!a.contains(c))
                    a.add(c);

        return a;
    }

    /**
     * Compile all of the compilable panes that're opened.
     *
     * @param message if it should send a message saying it's compiled sucessfully.
     * @return true if no errors, false if it failed to compile.
     */
    public static boolean compile(boolean message) {
        BytecodeViewer.viewer.updateBusyStatus(true);
        boolean actuallyTried = false;

        for (java.awt.Component c : BytecodeViewer.viewer.workPane.getLoadedViewers())
        {
            if (c instanceof ClassViewer)
            {
                ClassViewer cv = (ClassViewer) c;
                
                //compile smali assembly
                if (cv.resourceViewPanel1.compileMode == ResourcePanelCompileMode.SMALI_ASSEMBLY && cv.resourceViewPanel1.textArea.isEditable() ||
                        cv.resourceViewPanel2.compileMode == ResourcePanelCompileMode.SMALI_ASSEMBLY && cv.resourceViewPanel2.textArea.isEditable() ||
                        cv.resourceViewPanel3.compileMode == ResourcePanelCompileMode.SMALI_ASSEMBLY && cv.resourceViewPanel3.textArea.isEditable())
                {
                    actuallyTried = true;
                    Object[] smali = cv.getSmali();
                    if (smali != null)
                    {
                        ClassNode origNode = (ClassNode) smali[0];
                        String smaliText = (String) smali[1];
                        byte[] smaliCompiled = Compiler.SMALI_ASSEMBLER.getCompiler().compile(smaliText, origNode.name);
                        
                        if (smaliCompiled != null)
                        {
                            try {
                                ClassNode newNode = JarUtils.getNode(smaliCompiled);
                                BytecodeViewer.updateNode(origNode, newNode);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            BytecodeViewer.showMessage("There has been an error with assembling your Smali code, "
                                    + "please check this. Class: " + origNode.name);
                            BytecodeViewer.viewer.updateBusyStatus(false);
                            return false;
                        }
                    }
                }

                //compile krakatau assembly
                if (cv.resourceViewPanel1.compileMode == ResourcePanelCompileMode.KRAKATAU_ASSEMBLY && cv.resourceViewPanel1.textArea.isEditable() ||
                        cv.resourceViewPanel2.compileMode == ResourcePanelCompileMode.KRAKATAU_ASSEMBLY && cv.resourceViewPanel2.textArea.isEditable() ||
                        cv.resourceViewPanel3.compileMode == ResourcePanelCompileMode.KRAKATAU_ASSEMBLY && cv.resourceViewPanel3.textArea.isEditable())
                {
                    actuallyTried = true;
                    Object[] krakatau = cv.getKrakatau();
                    if (krakatau != null)
                    {
                        ClassNode origNode = (ClassNode) krakatau[0];
                        String krakatauText = (String) krakatau[1];
                        byte[] krakatauCompiled = Compiler.KRAKATAU_ASSEMBLER.getCompiler().compile(krakatauText, origNode.name);
                        
                        if (krakatauCompiled != null)
                        {
                            try {
                                ClassNode newNode = JarUtils.getNode(krakatauCompiled);
                                BytecodeViewer.updateNode(origNode, newNode);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            BytecodeViewer.showMessage("There has been an error with assembling your Krakatau "
                                    + "Bytecode, please check this. Class: " + origNode.name);
                            BytecodeViewer.viewer.updateBusyStatus(false);
                            return false;
                        }
                    }
                }

                //default to java compiling
                if (cv.resourceViewPanel1.textArea != null && cv.resourceViewPanel1.textArea.isEditable() ||
                        cv.resourceViewPanel2.textArea != null && cv.resourceViewPanel2.textArea.isEditable() ||
                        cv.resourceViewPanel3.textArea != null && cv.resourceViewPanel3.textArea.isEditable())
                {
                    actuallyTried = true;
                    Object[] java = cv.getJava();
                    if (java != null) {
                        ClassNode origNode = (ClassNode) java[0];
                        String javaText = (String) java[1];

                        SystemErrConsole errConsole = new SystemErrConsole("Java Compile Issues");
                        errConsole.setText("Error compiling class: " + origNode.name + nl + "Keep in mind most "
                                + "decompilers cannot produce compilable classes" + nl + nl);

                        byte[] javaCompiled = Compiler.JAVA_COMPILER.getCompiler().compile(javaText, origNode.name);
                        if (javaCompiled != null)
                        {
                            try {
                                ClassNode newNode = JarUtils.getNode(javaCompiled);
                                BytecodeViewer.updateNode(origNode, newNode);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            errConsole.finished();
                        }
                        else
                        {
                            errConsole.pretty();
                            errConsole.setVisible(true);
                            errConsole.finished();
                            BytecodeViewer.viewer.updateBusyStatus(false);
                            return false;
                        }
                    }
                }
            }
        }

        if (message)
            if (actuallyTried)
                BytecodeViewer.showMessage("Compiled Successfully.");
            else
                BytecodeViewer.showMessage("You have no editable panes opened, make one editable and try again.");

        BytecodeViewer.viewer.updateBusyStatus(false);
        return true;
    }

    /**
     * Opens a file, optional if it should append to the recent files menu
     *
     * @param files       the file(s) you wish to open
     * @param recentFiles if it should append to the recent files menu
     */
    public static void openFiles(final File[] files, boolean recentFiles) {
        if (recentFiles)
            for (File f : files)
                if (f.exists())
                    Constants.addRecentFile(f);

        BytecodeViewer.viewer.updateBusyStatus(true);
        Configuration.needsReDump = true;
        Thread t = new Thread(new ImportResource(files));
        t.start();
    }

    /**
     * Starts the specified plugin
     *
     * @param file the file of the plugin
     */
    public static void startPlugin(File file) {
        if (!file.exists())
            return;

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
    public static void showMessage(String message) {
        JOptionPane.showMessageDialog(viewer, message);
    }

    /**
     * Resets the workspace with optional user input required
     *
     * @param ask if should require user input or not
     */
    public static void resetWorkSpace(boolean ask)
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

        files.clear();
        LazyNameUtil.reset();
        Objects.requireNonNull(MainViewerGUI.getComponent(ResourceListPane.class)).resetWorkspace();
        Objects.requireNonNull(MainViewerGUI.getComponent(WorkPaneMainComponent.class)).resetWorkspace();
        Objects.requireNonNull(MainViewerGUI.getComponent(SearchBoxPane.class)).resetWorkspace();
        the.bytecode.club.bytecodeviewer.api.BytecodeViewer.getClassNodeLoader().clear();
    }

    /**
     * Clears the temp directory
     */
    public static void cleanup() {
        File tempF = new File(tempDirectory);

        try {
            FileUtils.deleteDirectory(tempF);
        } catch (Exception ignored) {
        }

        while (!tempF.exists()) // keep making dirs
            tempF.mkdir();
    }
    
    public static void refreshAllTabTitles()
    {
        for(int i = 0; i < BytecodeViewer.viewer.workPane.tabs.getTabCount(); i++)
        {
            ResourceViewer viewer = ((TabbedPane) BytecodeViewer.viewer.workPane.tabs.getTabComponentAt(i)).resource;
            viewer.refreshTitle();
        }
    }

    /**
     * Checks the hotkeys
     *
     * @param e
     */
    public static void checkHotKey(KeyEvent e) {
        if (System.currentTimeMillis() - Configuration.lastHotKeyExecuted <= (4000))
            return;

        if ((e.getKeyCode() == KeyEvent.VK_O) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            Configuration.lastHotKeyExecuted = System.currentTimeMillis();
            JFileChooser fc = new FileChooser(new File(Configuration.lastDirectory),
                    "Select File or Folder to open in BCV",
                    "APKs, DEX, Class Files or Zip/Jar/War Archives",
                    Constants.SUPPORTED_FILE_EXTENSIONS);
            
            int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                try {
                    Configuration.lastDirectory = fc.getSelectedFile().getAbsolutePath();
                    BytecodeViewer.viewer.updateBusyStatus(true);
                    BytecodeViewer.openFiles(new File[]{fc.getSelectedFile()}, true);
                    BytecodeViewer.viewer.updateBusyStatus(false);
                } catch (Exception e1) {
                    new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
                }
            }
        } else if ((e.getKeyCode() == KeyEvent.VK_N) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            Configuration.lastHotKeyExecuted = System.currentTimeMillis();
            BytecodeViewer.resetWorkSpace(true);
        } else if ((e.getKeyCode() == KeyEvent.VK_T) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            Configuration.lastHotKeyExecuted = System.currentTimeMillis();
            Thread t = new Thread(() -> BytecodeViewer.compile(true));
            t.start();
        } else if ((e.getKeyCode() == KeyEvent.VK_R) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            Configuration.lastHotKeyExecuted = System.currentTimeMillis();
            if (BytecodeViewer.getLoadedClasses().isEmpty()) {
                BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                return;
            }
            new RunOptions().setVisible(true);
        } else if ((e.getKeyCode() == KeyEvent.VK_S) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            Configuration.lastHotKeyExecuted = System.currentTimeMillis();

            if (BytecodeViewer.getLoadedClasses().isEmpty()) {
                BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                return;
            }

            Thread t = new Thread(() -> {
                if (viewer.compileOnSave.isSelected() && !BytecodeViewer.compile(false))
                    return;
                
                JFileChooser fc = new FileChooser(new File(Configuration.lastDirectory),
                        "Select Zip Export",
                        "Zip Archives",
                        "zip");
                
                int returnVal = fc.showSaveDialog(viewer);
                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    Configuration.lastDirectory = fc.getSelectedFile().getAbsolutePath();
                    File file = fc.getSelectedFile();
                    if (!file.getAbsolutePath().endsWith(".zip"))
                        file = new File(file.getAbsolutePath() + ".zip");

                    if (file.exists()) {
                        MultipleChoiceDialogue dialogue = new MultipleChoiceDialogue("Bytecode Viewer - Overwrite File",
                                "Are you sure you wish to overwrite this existing file?",
                                new String[]{"Yes", "No"});

                        if (dialogue.promptChoice() == 0) {
                            file.delete();
                        } else {
                            return;
                        }
                    }

                    final File file2 = file;

                    BytecodeViewer.viewer.updateBusyStatus(true);
                    Thread t1 = new Thread(() -> {
                        JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(),
                                file2.getAbsolutePath());
                        BytecodeViewer.viewer.updateBusyStatus(false);
                    });
                    t1.start();
                }
            });
            t.start();
        } else if ((e.getKeyCode() == KeyEvent.VK_W) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            Configuration.lastHotKeyExecuted = System.currentTimeMillis();
            if (viewer.workPane.getCurrentViewer() != null)
                viewer.workPane.tabs.remove(viewer.workPane.getCurrentViewer());
        }
    }

    public static File[] dumpTempFile(FileContainer container)
    {
        File[] files = new File[2];
        
        //currently won't optimize if you've got two containers with the same name, will need to add this later
        if (!LazyNameUtil.SAME_NAME_JAR_WORKSPACE) {
            if (Configuration.krakatauTempJar != null && !Configuration.krakatauTempJar.exists()) {
                Configuration.needsReDump = true;
            }

            if (Configuration.needsReDump && Configuration.krakatauTempJar != null) {
                Configuration.krakatauTempDir = null;
                Configuration.krakatauTempJar = null;
            }

            boolean passes = false;

            if (BytecodeViewer.viewer.viewPane1.getGroup().isSelected(BytecodeViewer.viewer.viewPane1.getKrakatau().getJava().getModel()))
                passes = true;
            else if (BytecodeViewer.viewer.viewPane1.getGroup().isSelected(BytecodeViewer.viewer.viewPane1.getKrakatau().getBytecode().getModel()))
                passes = true;
            else if (BytecodeViewer.viewer.viewPane2.getGroup().isSelected(BytecodeViewer.viewer.viewPane2.getKrakatau().getJava().getModel()))
                passes = true;
            else if (BytecodeViewer.viewer.viewPane2.getGroup().isSelected(BytecodeViewer.viewer.viewPane2.getKrakatau().getBytecode().getModel()))
                passes = true;
            else if (BytecodeViewer.viewer.viewPane3.getGroup().isSelected(BytecodeViewer.viewer.viewPane3.getKrakatau().getJava().getModel()))
                passes = true;
            else if (BytecodeViewer.viewer.viewPane3.getGroup().isSelected(BytecodeViewer.viewer.viewPane3.getKrakatau().getBytecode().getModel()))
                passes = true;

            if (Configuration.krakatauTempJar != null || !passes) {
                files[0] = Configuration.krakatauTempJar;
                files[1] = Configuration.krakatauTempDir;
                return files;
            }
        }
    
        Configuration.currentlyDumping = true;
        Configuration.needsReDump = false;
        Configuration.krakatauTempDir = new File(tempDirectory + fs + MiscUtils.randomString(32) + fs);
        Configuration.krakatauTempDir.mkdir();
        Configuration.krakatauTempJar = new File(tempDirectory + fs + "temp" + MiscUtils.randomString(32) + ".jar");
        //krakatauTempJar = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp" + MiscUtils
        // .randomString(32) + ".jar."+container.name);
        JarUtils.saveAsJarClassesOnly(container.classes, Configuration.krakatauTempJar.getAbsolutePath());
        Configuration.currentlyDumping = false;

        files[0] = Configuration.krakatauTempJar;
        files[1] = Configuration.krakatauTempDir;
        return files;
    }

    public synchronized static void rtCheck() {
        if (Configuration.rt.isEmpty()) {
            if (RT_JAR.exists()) {
                Configuration.rt = RT_JAR.getAbsolutePath();
            } else if (RT_JAR_DUMPED.exists()) {
                Configuration.rt = RT_JAR_DUMPED.getAbsolutePath();
            } else {
                try {
                    JRTExtractor.extractRT(RT_JAR_DUMPED.getAbsolutePath());
                    Configuration.rt = RT_JAR_DUMPED.getAbsolutePath();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }
}
