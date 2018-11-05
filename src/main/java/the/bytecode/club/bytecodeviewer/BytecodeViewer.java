package the.bytecode.club.bytecodeviewer;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import me.konloch.kontainer.io.DiskReader;
import me.konloch.kontainer.io.DiskWriter;
import me.konloch.kontainer.io.HTTPRequest;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bootloader.Boot;
import the.bytecode.club.bootloader.ILoader;
import the.bytecode.club.bootloader.resource.EmptyExternalResource;
import the.bytecode.club.bootloader.resource.ExternalResource;
import the.bytecode.club.bytecodeviewer.api.ClassNodeLoader;
import the.bytecode.club.bytecodeviewer.gui.ClassViewer;
import the.bytecode.club.bytecodeviewer.gui.FileNavigationPane;
import the.bytecode.club.bytecodeviewer.gui.MainViewerGUI;
import the.bytecode.club.bytecodeviewer.gui.RunOptions;
import the.bytecode.club.bytecodeviewer.gui.SearchingPane;
import the.bytecode.club.bytecodeviewer.gui.SystemErrConsole;
import the.bytecode.club.bytecodeviewer.gui.WorkPane;
import the.bytecode.club.bytecodeviewer.obfuscators.mapping.Refactorer;
import the.bytecode.club.bytecodeviewer.plugin.PluginManager;

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
 * <p>
 * Are you a Java Reverse Engineer? Or maybe you want to learn Java Reverse Engineering?
 * Join The Bytecode Club, we're noob friendly, and censorship free.
 * <p>
 * http://the.bytecode.club
 * <p>
 * All you have to do is add a jar or class file into the workspace, select the
 * file you want then it will start decompiling the class in the background,
 * when it's done it will show the Source code, Bytecode and Hexcode of the
 * class file you chose.
 * <p>
 * There is also a plugin system that will allow you to interact with the loaded
 * classfiles, for example you can write a String deobfuscator, a malicious code
 * searcher, or something else you can think of. You can either use one of the
 * pre-written plugins, or write your own. It supports groovy
 * scripting. Once a plugin is activated, it will send a ClassNode ArrayList of
 * every single class loaded in the file system to the execute function, this
 * allows the user to handle it completely using ASM.
 * <p>
 * TODO:
 * <p>
 * 3.0.0: (RETIREMENT PARTY, WOHOOO)
 * Add obfuscation:
 * - Add integer boxing and other obfuscation methods contra implemented
 * - Insert unadded/debug opcodes to try to fuck up decompilers
 * - ClassAnylyzterAdapter
 * Add the jump/save mark system Ida Pro has.
 * Add class annotations to bytecode decompiler.
 * EVERYTHING BUG FREE, CHECK 100%
 * bytecode editor that works by editing per method instead of entire class, methods are in a pane like the file navigator
 * Make the tabs menu and middle mouse button click work on the tab itself not just the close button.
 * <p>
 * before 3.0.0:
 * EVERYTHING ON THE FUCKING GITHUB ISSUES LOL
 * make it use that global last used inside of export as jar
 * Spiffy up the plugin console with hilighted lines
 * Take https://github.com/ptnkjke/Java-Bytecode-Editor visualize
 * make zipfile not include the decode shit
 * add stackmapframes to bytecode decompiler
 * add stackmapframes remover?
 * make ez-injection plugin console show all sys.out calls
 * add JEB decompiler optionally, requires them to add jeb library jar externally and disable update check ?
 * add decompile as zip for krakatau-bytecode, jd-gui and smali for CLI
 * fix hook inject for EZ-Injection
 * fix classfile searcher
 * make the decompilers launch in a separate process?
 * <p>
 * -----2.9.9-----:
 * 08/01/2015 - Fixed a pingback concurrency exception issue.
 * 08/03/2015 - Fixed a typo for FernFlower decompiler.
 * 08/03/2015 - Fixed an issue with Krakatau Decompiler as zip.
 * 08/07/2015 - "Fixed" an issue with Enjarify and latest PyPy3 bin.
 * 08/07/2015 - FernFlower & CFR Decompiler now launch in their own process with the 'slimjar' version.
 * 08/07/2015 - Switched the ClassViewer up slightly so it utilizes the event dispatch thread.
 * 08/07/2015 - Fixed? CFIDE's Bytecode Decompiler on TableSwitchs
 *
 * @author Konloch
 */

public class BytecodeViewer {

    /*per version*/
    public static String version = "2.9.11";
    public static boolean previewCopy = false;
    public static boolean fatJar = true; //could be automatic by checking if it's loaded a class named whatever for a library
    /*the rest*/
    public static boolean verify = false; //eventually may be a setting
    public static String[] args;
    public static MainViewerGUI viewer = null;
    public static ClassNodeLoader loader = new ClassNodeLoader(); //might be insecure due to assholes targeting BCV, however that's highly unlikely.
    public static SecurityMan sm = new SecurityMan(); //might be insecure due to assholes targeting BCV, however that's highly unlikely.
    public static String python = "";
    public static String python3 = "";
    public static String rt = "";
    public static String library = "";
    public static String javac = "";
    public static String java = "";
    public static ArrayList<FileContainer> files = new ArrayList<FileContainer>(); //all of BCV's loaded files/classes/etc
    private static int maxRecentFiles = 25;
    public static String fs = System.getProperty("file.separator");
    public static String nl = System.getProperty("line.separator");
    private static File BCVDir = new File(System.getProperty("user.home") + fs + ".Bytecode-Viewer");
    private static String filesName = getBCVDirectory() + fs + "recentfiles.bcv";
    private static String pluginsName = getBCVDirectory() + fs + "recentplugins.bcv";
    public static String settingsName = getBCVDirectory() + fs + "settings.bcv";
    public static String tempDirectory = getBCVDirectory() + fs + "bcv_temp" + fs;
    public static String libsDirectory = getBCVDirectory() + fs + "libs" + fs;
    public static String krakatauWorkingDirectory = "";
    public static String krakatauVersion = "";
    public static String enjarifyWorkingDirectory = "";
    public static String enjarifyVersion = "";
    private static ArrayList<String> recentFiles = DiskReader.loadArrayList(filesName, false);
    private static ArrayList<String> recentPlugins = DiskReader.loadArrayList(pluginsName, false);
    public static boolean runningObfuscation = false;
    private static long start = System.currentTimeMillis();
    public static String lastDirectory = "";
    public static ArrayList<Process> createdProcesses = new ArrayList<Process>();
    public static Refactorer refactorer = new Refactorer();
    public static boolean pingback = false;
    public static boolean deleteForiegnLibraries = true;

    /**
     * The version checker thread
     */
    private static Thread versionChecker = new Thread() {
        @Override
        public void run() {
            try {
                HTTPRequest r = new HTTPRequest(new URL("https://raw.githubusercontent.com/Konloch/bytecode-viewer/master/VERSION"));
                final String version = r.readSingle();
                try {
                    int simplemaths = Integer.parseInt(version.replace(".", ""));
                    int simplemaths2 = Integer.parseInt(BytecodeViewer.version.replace(".", ""));
                    if (simplemaths2 > simplemaths)
                        return; //developer version
                } catch (Exception e) {

                }

                if (!BytecodeViewer.version.equals(version)) {
                    r = new HTTPRequest(new URL("https://raw.githubusercontent.com/Konloch/bytecode-viewer/master/CHANGELOG.md"));
                    String[] readme = r.read();

                    String changelog = "Unable to load change log, please try again later." + nl;
                    boolean trigger = false;
                    boolean finalTrigger = false;
                    for (String st : readme) {
                    	if(st.equals("```")) {
                    		continue;
                    	} else if (st.equals("--- " + BytecodeViewer.version + " ---:")) {
                            changelog = "";
                            trigger = true;
                        } else if (trigger) {
                            if (st.startsWith("--- "))
                                finalTrigger = true;

                            if (finalTrigger)
                                changelog += st + nl;
                        }
                    }

                    JOptionPane pane = new JOptionPane("Your version: "
                            + BytecodeViewer.version
                            + ", latest version: "
                            + version
                            + nl
                            + nl
                            + "Changes since your version:"
                            + nl
                            + changelog
                            + nl
                            + "What would you like to do?");
                    Object[] options = new String[]{"Open The Download Page", "Download The Updated Jar", "Do Nothing"};
                    pane.setOptions(options);
                    JDialog dialog = pane.createDialog(BytecodeViewer.viewer,
                            "Bytecode Viewer - Outdated Version");
                    dialog.setVisible(true);
                    Object obj = pane.getValue();
                    int result = -1;
                    for (int k = 0; k < options.length; k++)
                        if (options[k].equals(obj))
                            result = k;

                    if (result == 0) {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().browse(new URI("https://github.com/Konloch/bytecode-viewer/releases"));
                        } else {
                            showMessage("Cannot open the page, please manually type it." + nl + "https://github.com/Konloch/bytecode-viewer/releases");
                        }
                    }
                    if (result == 1) {
                        JFileChooser fc = new JFileChooser();
                        try {
                            fc.setCurrentDirectory(new File(".").getAbsoluteFile()); //set the current working directory
                        } catch (Exception e) {
                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                        }
                        fc.setFileFilter(new FileFilter() {
                            @Override
                            public boolean accept(File f) {
                                return f.isDirectory() || MiscUtils.extension(f.getAbsolutePath()).equals("zip");
                            }

                            @Override
                            public String getDescription() {
                                return "Zip Archives";
                            }
                        });
                        fc.setFileHidingEnabled(false);
                        fc.setAcceptAllFileFilterUsed(false);
                        int returnVal = fc.showSaveDialog(viewer);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            if (!file.getAbsolutePath().endsWith(".zip"))
                                file = new File(file.getAbsolutePath() + ".zip");

                            if (file.exists()) {
                                pane = new JOptionPane("The file " + file + " exists, would you like to overwrite it?");
                                options = new String[]{"Yes", "No"};
                                pane.setOptions(options);
                                dialog = pane.createDialog(BytecodeViewer.viewer,
                                        "Bytecode Viewer - Overwrite File");
                                dialog.setVisible(true);
                                obj = pane.getValue();
                                result = -1;
                                for (int k = 0; k < options.length; k++)
                                    if (options[k].equals(obj))
                                        result = k;

                                if (result != 0)
                                    return;

                                file.delete();
                            }

                            final File finalFile = file;
                            Thread downloadThread = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        InputStream is = new URL("https://github.com/Konloch/bytecode-viewer/releases/download/v" + version + "/BytecodeViewer." + version + ".zip").openConnection().getInputStream();
                                        FileOutputStream fos = new FileOutputStream(finalFile);
                                        try {
                                            System.out.println("Downloading from https://github.com/Konloch/bytecode-viewer/releases/download/v" + version + "/BytecodeViewer." + version + ".zip");
                                            byte[] buffer = new byte[8192];
                                            int len;
                                            int downloaded = 0;
                                            boolean flag = false;
                                            showMessage("Downloading the jar in the background, when it's finished you will be alerted with another message box." + nl + nl + "Expect this to take several minutes.");
                                            while ((len = is.read(buffer)) > 0) {
                                                fos.write(buffer, 0, len);
                                                fos.flush();
                                                downloaded += 8192;
                                                int mbs = downloaded / 1048576;
                                                if (mbs % 5 == 0 && mbs != 0) {
                                                    if (!flag)
                                                        System.out.println("Downloaded " + mbs + "MBs so far");
                                                    flag = true;
                                                } else
                                                    flag = false;
                                            }
                                        } finally {
                                            try {
                                                if (is != null) {
                                                    is.close();
                                                }
                                            } finally {
                                                if (fos != null) {
                                                    fos.flush();
                                                    fos.close();
                                                }
                                            }
                                        }
                                        System.out.println("Download finished!");
                                        showMessage("Download successful! You can find the updated program at " + finalFile.getAbsolutePath());
                                    } catch (FileNotFoundException e) {
                                    	try
                                    	{
                                            InputStream is = new URL("https://github.com/Konloch/bytecode-viewer/releases/download/v" + version + "/BytecodeViewer." + version + ".jar").openConnection().getInputStream();
                                            FileOutputStream fos = new FileOutputStream(finalFile);
                                            try {
                                                System.out.println("Downloading from https://github.com/Konloch/bytecode-viewer/releases/download/v" + version + "/BytecodeViewer." + version + ".jar");
                                                byte[] buffer = new byte[8192];
                                                int len;
                                                int downloaded = 0;
                                                boolean flag = false;
                                                showMessage("Downloading the jar in the background, when it's finished you will be alerted with another message box." + nl + nl + "Expect this to take several minutes.");
                                                while ((len = is.read(buffer)) > 0) {
                                                    fos.write(buffer, 0, len);
                                                    fos.flush();
                                                    downloaded += 8192;
                                                    int mbs = downloaded / 1048576;
                                                    if (mbs % 5 == 0 && mbs != 0) {
                                                        if (!flag)
                                                            System.out.println("Downloaded " + mbs + "MBs so far");
                                                        flag = true;
                                                    } else
                                                        flag = false;
                                                }
                                            } finally {
                                                try {
                                                    if (is != null) {
                                                        is.close();
                                                    }
                                                } finally {
                                                    if (fos != null) {
                                                        fos.flush();
                                                        fos.close();
                                                    }
                                                }
                                            }
                                            System.out.println("Download finished!");
                                            showMessage("Download successful! You can find the updated program at " + finalFile.getAbsolutePath());
                                    	} catch (FileNotFoundException ex) {
                                    	     showMessage("Unable to download, the zip file has not been uploaded yet, please try again in about 10 minutes.");
                                    	} catch (Exception ex) {
                                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(ex);
                                        }
                                    	
                                    } catch (Exception e) {
                                        new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                                    }

                                }
                            };
                            downloadThread.start();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * Pings back to bytecodeviewer.com to be added into the total running statistics
     */
    private static Thread PingBack = new Thread() {
        @Override
        public void run() {
            try {
                new HTTPRequest(new URL("https://bytecodeviewer.com/add.php")).read();
            } catch (Exception e) {
                pingback = false;
            }
        }
    };

    /**
     * Downloads & installs the krakatau & enjarify zips
     */
    private static Thread InstallFatJar = new Thread() {
        @Override
        public void run() {
            try {
                Boot.populateUrlList();
                Boot.populateLibsDirectory();
                Boot.downloadZipsOnly();
                Boot.checkKrakatau();
                Boot.checkEnjarify();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * Used to check incase booting failed for some reason, this kicks in as a fail safe
     */
    private static Thread bootCheck = new Thread() {
        boolean finished = false;

        @SuppressWarnings({"rawtypes", "unchecked"})
        public void run() {
            long start = System.currentTimeMillis();

            while (!finished) {
                if (System.currentTimeMillis() - start >= 7000) { //7 second failsafe
                    if (!Boot.completedboot && !Boot.downloading) {
                        if (Boot.libsDir() == null || Boot.libsDir().listFiles() == null || Boot.libsDir().listFiles().length <= 0) {
                            BytecodeViewer.showMessage(
                                    "Github is loading extremely slow, BCV needs to download libraries from github in order" + nl +
                                            "to work, please try ajusting your network settings or manually downloading these libraries" + nl +
                                            "if this error persists.");
                            finished = true;
                            return;
                        }

                        Boot.setState("Bytecode Viewer Boot Screen (OFFLINE MODE) - Unable to connect to github, force booting...");
                        System.out.println("Unable to connect to github, force booting...");
                        List<String> libsList = new ArrayList<String>();
                        List<String> libsFileList = new ArrayList<String>();
                        if (Boot.libsDir() != null)
                            for (File f : Boot.libsDir().listFiles()) {
                                libsList.add(f.getName());
                                libsFileList.add(f.getAbsolutePath());
                            }

                        ILoader<?> loader = Boot.findLoader();

                        for (String s : libsFileList) {
                            if (s.endsWith(".jar")) {
                                File f = new File(s);
                                if (f.exists()) {
                                    Boot.setState("Bytecode Viewer Boot Screen (OFFLINE MODE) - Force Loading Library " + f.getName());
                                    System.out.println("Force loading library " + f.getName());

                                    try {
                                        ExternalResource res = new EmptyExternalResource<Object>(f.toURI().toURL());
                                        loader.bind(res);
                                        System.out.println("Succesfully loaded " + f.getName());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        f.delete();
                                        JOptionPane.showMessageDialog(null, "Error, Library " + f.getName() + " is corrupt, please restart to redownload it.",
                                                "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            }
                        }

                        Boot.checkEnjarify();
                        Boot.checkKrakatau();

                        Boot.globalstop = false;
                        Boot.hide();

                        if (CommandLineInput.parseCommandLine(args) == CommandLineInput.OPEN_FILE)
                            BytecodeViewer.BOOT(false);
                        else {
                            BytecodeViewer.BOOT(true);
                            CommandLineInput.executeCommandLine(args);
                        }
                    }
                    finished = true;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
        }
    };

    public static void pingback() {
        JOptionPane pane = new JOptionPane(
                "Would you like to 'pingback' to https://bytecodeviewer.com to be counted in the global users for BCV?");
        Object[] options = new String[]{"Yes", "No"};
        pane.setOptions(options);
        JDialog dialog = pane.createDialog(BytecodeViewer.viewer,
                "Bytecode Viewer - Optional Pingback");
        dialog.setVisible(true);
        Object obj = pane.getValue();
        int result = -1;
        for (int k = 0; k < options.length; k++)
            if (options[k].equals(obj))
                result = k;

        if (result == 0) {
            try {
                if (!PingBack.isAlive())
                    PingBack.start();
            } catch (Exception e) {
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
            }
        }
    }

    /**
     * Grab the byte array from the loaded Class object
     *
     * @param clazz
     * @return
     * @throws IOException
     */
    public static byte[] getClassFile(Class<?> clazz) throws IOException {
        InputStream is = clazz.getResourceAsStream("/" + clazz.getName().replace('.', '/') + ".class");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int r = 0;
        byte[] buffer = new byte[8192];
        while ((r = is.read(buffer)) >= 0) {
            baos.write(buffer, 0, r);
        }
        return baos.toByteArray();
    }

    /**
     * Main startup
     *
     * @param args files you want to open or CLI
     */
    public static void main(String[] args) {
        BytecodeViewer.args = args;
        System.out.println("https://the.bytecode.club - Created by @Konloch - Bytecode Viewer " + version+", FatJar: " + fatJar);
        System.setSecurityManager(sm);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            if (previewCopy && !CommandLineInput.containsCommand(args))
                showMessage("WARNING: This is a preview/dev copy, you WON'T be alerted when " + version + " is actually out if you use this." + nl +
                        "Make sure to watch the repo: https://github.com/Konloch/bytecode-viewer for " + version + "'s release");

            viewer = new MainViewerGUI();
            Settings.loadGUI();

            int CLI = CommandLineInput.parseCommandLine(args);

            if (CLI == CommandLineInput.STOP)
                return;

            if (!fatJar) {
                bootCheck.start();

                if (CLI == CommandLineInput.OPEN_FILE)
                    Boot.boot(args, false);
                else
                    Boot.boot(args, true);
            } else
                InstallFatJar.start();

            if (CLI == CommandLineInput.OPEN_FILE)
                BytecodeViewer.BOOT(false);
            else {
                BytecodeViewer.BOOT(true);
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
    public static void BOOT(boolean cli) {
        cleanup();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                for (Process proc : createdProcesses)
                    proc.destroy();
                Settings.saveGUI();
                cleanup();
            }
        });

        viewer.calledAfterLoad();
        resetRecentFilesMenu();

        if (!pingback) {
            PingBack.start();
            pingback = true;
        }

        if (viewer.chckbxmntmNewCheckItem_12.isSelected())
            versionChecker.start();

        if (!cli)
            viewer.setVisible(true);

        System.out.println("Start up took " + ((System.currentTimeMillis() - start) / 1000) + " seconds");

        if (!cli)
            if (args.length >= 1)
                for (String s : args) {
                    openFiles(new File[]{new File(s)}, true);
                }
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
            Process p = pb.start();
            sm.setBlocking();
            if (p != null)
                return "java"; //java is set
        } catch (Exception e) { //ignore
            sm.setBlocking();
            boolean empty = java.isEmpty();
            while (empty) {
                showMessage("You need to set your Java path, this requires the JRE to be downloaded." + BytecodeViewer.nl +
                        "(C:/programfiles/Java/JRE_xx/bin/java.exe)");
                viewer.java();
                empty = java.isEmpty();
            }
        }
        return java;
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

    /**
     * Grabs the file contents of the loaded resources.
     *
     * @param name the file name
     * @return the file contents as a byte[]
     */
    public static byte[] getFileContents(String name) {
        for (FileContainer container : files) {
            HashMap<String, byte[]> files = container.files;
            if (files.containsKey(name))
                return files.get(name);
        }

        return null;
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
        ArrayList<ClassNode> a = new ArrayList<ClassNode>();

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
        BytecodeViewer.viewer.setIcon(true);
        boolean actuallyTried = false;

        for (java.awt.Component c : BytecodeViewer.viewer.workPane.getLoadedViewers()) {
            if (c instanceof ClassViewer) {
                ClassViewer cv = (ClassViewer) c;
                if (cv.smali1 != null && cv.smali1.isEditable() ||
                        cv.smali2 != null && cv.smali2.isEditable() ||
                        cv.smali3 != null && cv.smali3.isEditable()) {
                    actuallyTried = true;
                    Object smali[] = cv.getSmali();
                    if (smali != null) {
                        ClassNode origNode = (ClassNode) smali[0];
                        String smaliText = (String) smali[1];
                        byte[] smaliCompiled = the.bytecode.club.bytecodeviewer.compilers.Compiler.smali.compile(smaliText, origNode.name);
                        if (smaliCompiled != null) {
                            ClassNode newNode = JarUtils.getNode(smaliCompiled);
                            BytecodeViewer.updateNode(origNode, newNode);
                        } else {
                            BytecodeViewer.showMessage("There has been an error with assembling your Smali code, please check this. Class: " + origNode.name);
                            BytecodeViewer.viewer.setIcon(false);
                            return false;
                        }
                    }
                }


                if (cv.krakatau1 != null && cv.krakatau1.isEditable() ||
                        cv.krakatau2 != null && cv.krakatau2.isEditable() ||
                        cv.krakatau3 != null && cv.krakatau3.isEditable()) {
                    actuallyTried = true;
                    Object krakatau[] = cv.getKrakatau();
                    if (krakatau != null) {
                        ClassNode origNode = (ClassNode) krakatau[0];
                        String krakatauText = (String) krakatau[1];
                        byte[] krakatauCompiled = the.bytecode.club.bytecodeviewer.compilers.Compiler.krakatau.compile(krakatauText, origNode.name);
                        if (krakatauCompiled != null) {
                            ClassNode newNode = JarUtils.getNode(krakatauCompiled);
                            BytecodeViewer.updateNode(origNode, newNode);
                        } else {
                            BytecodeViewer.showMessage("There has been an error with assembling your Krakatau Bytecode, please check this. Class: " + origNode.name);
                            BytecodeViewer.viewer.setIcon(false);
                            return false;
                        }
                    }
                }

                if (cv.java1 != null && cv.java1.isEditable() ||
                        cv.java2 != null && cv.java2.isEditable() ||
                        cv.java3 != null && cv.java3.isEditable()) {
                    actuallyTried = true;
                    Object java[] = cv.getJava();
                    if (java != null) {
                        ClassNode origNode = (ClassNode) java[0];
                        String javaText = (String) java[1];

                        SystemErrConsole errConsole = new SystemErrConsole("Java Compile Issues");
                        errConsole.setText("Error compiling class: " + origNode.name + nl + "Keep in mind most decompilers cannot produce compilable classes" + nl + nl);

                        byte[] javaCompiled = the.bytecode.club.bytecodeviewer.compilers.Compiler.java.compile(javaText, origNode.name);
                        if (javaCompiled != null) {
                            ClassNode newNode = JarUtils.getNode(javaCompiled);
                            BytecodeViewer.updateNode(origNode, newNode);
                            errConsole.finished();
                        } else {
                            errConsole.pretty();
                            errConsole.setVisible(true);
                            errConsole.finished();
                            BytecodeViewer.viewer.setIcon(false);
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

        BytecodeViewer.viewer.setIcon(false);
        return true;
    }

    private static boolean update = true;

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
                    BytecodeViewer.addRecentFile(f);

        BytecodeViewer.viewer.setIcon(true);
        update = true;

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    for (final File f : files) {
                        final String fn = f.getName();
                        if (!f.exists()) {
                            update = false;
                            showMessage("The file " + f.getAbsolutePath() + " could not be found.");
                        } else {
                            if (f.isDirectory()) {
                                FileContainer container = new FileContainer(f);
                                HashMap<String, byte[]> files = new HashMap<String, byte[]>();
                                boolean finished = false;
                                ArrayList<File> totalFiles = new ArrayList<File>();
                                totalFiles.add(f);
                                String dir = f.getAbsolutePath();//f.getAbsolutePath().substring(0, f.getAbsolutePath().length()-f.getName().length());

                                while (!finished) {
                                    boolean added = false;
                                    for (int i = 0; i < totalFiles.size(); i++) {
                                        File child = totalFiles.get(i);
                                        if (child.listFiles() != null)
                                            for (File rocket : child.listFiles())
                                                if (!totalFiles.contains(rocket)) {
                                                    totalFiles.add(rocket);
                                                    added = true;
                                                }
                                    }

                                    if (!added) {
                                        for (File child : totalFiles)
                                            if (child.isFile()) {
                                                String fileName = child.getAbsolutePath().substring(dir.length() + 1, child.getAbsolutePath().length()).replaceAll("\\\\", "\\/");


                                                files.put(fileName, Files.readAllBytes(Paths.get(child.getAbsolutePath())));
                                            }
                                        finished = true;
                                    }
                                }
                                container.files = files;
                                BytecodeViewer.files.add(container);
                            } else {
                                if (fn.endsWith(".jar") || fn.endsWith(".zip")) {
                                    try {
                                        JarUtils.put(f);
                                    } catch (final Exception e) {
                                        new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                                        update = false;
                                    }

                                } else if (fn.endsWith(".class")) {
                                    try {
                                        byte[] bytes = JarUtils.getBytes(new FileInputStream(f));
                                        String cafebabe = String.format("%02X", bytes[0])
                                                + String.format("%02X", bytes[1])
                                                + String.format("%02X", bytes[2])
                                                + String.format("%02X", bytes[3]);
                                        if (cafebabe.toLowerCase().equals("cafebabe")) {
                                            final ClassNode cn = JarUtils.getNode(bytes);

                                            FileContainer container = new FileContainer(f);
                                            container.classes.add(cn);
                                            BytecodeViewer.files.add(container);
                                        } else {
                                            showMessage(fn + ": Header does not start with CAFEBABE, ignoring.");
                                            update = false;
                                        }
                                    } catch (final Exception e) {
                                        new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                                        update = false;
                                    }
                                } else if (fn.endsWith(".apk")) {
                                    try {
                                        BytecodeViewer.viewer.setIcon(true);
                                        FileContainer container = new FileContainer(f);

                                        if (viewer.decodeAPKResources.isSelected()) {
                                            File decodedResources = new File(tempDirectory + fs + MiscUtils.randomString(32) + ".apk");
                                            APKTool.decodeResources(f, decodedResources);
                                            container.files = JarUtils.loadResources(decodedResources);
                                        }

                                        container.files.putAll(JarUtils.loadResources(f));

                                        String name = getRandomizedName() + ".jar";
                                        File output = new File(tempDirectory + fs + name);

                                        if (BytecodeViewer.viewer.apkConversionGroup.isSelected(BytecodeViewer.viewer.apkConversionDex.getModel()))
                                            Dex2Jar.dex2Jar(f, output);
                                        else if (BytecodeViewer.viewer.apkConversionGroup.isSelected(BytecodeViewer.viewer.apkConversionEnjarify.getModel()))
                                            Enjarify.apk2Jar(f, output);

                                        container.classes = JarUtils.loadClasses(output);

                                        BytecodeViewer.viewer.setIcon(false);
                                        BytecodeViewer.files.add(container);
                                    } catch (final Exception e) {
                                        new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                                    }
                                    return;
                                } else if (fn.endsWith(".dex")) {
                                    try {
                                        BytecodeViewer.viewer.setIcon(true);
                                        FileContainer container = new FileContainer(f);

                                        String name = getRandomizedName() + ".jar";
                                        File output = new File(tempDirectory + fs + name);

                                        if (BytecodeViewer.viewer.apkConversionGroup.isSelected(BytecodeViewer.viewer.apkConversionDex.getModel()))
                                            Dex2Jar.dex2Jar(f, output);
                                        else if (BytecodeViewer.viewer.apkConversionGroup.isSelected(BytecodeViewer.viewer.apkConversionEnjarify.getModel()))
                                            Enjarify.apk2Jar(f, output);

                                        container.classes = JarUtils.loadClasses(output);

                                        BytecodeViewer.viewer.setIcon(false);
                                        BytecodeViewer.files.add(container);
                                    } catch (final Exception e) {
                                        new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                                    }
                                    return;
                                } else {
                                    HashMap<String, byte[]> files = new HashMap<String, byte[]>();
                                    byte[] bytes = JarUtils.getBytes(new FileInputStream(f));
                                    files.put(f.getName(), bytes);


                                    FileContainer container = new FileContainer(f);
                                    container.files = files;
                                    BytecodeViewer.files.add(container);
                                }
                            }
                        }
                    }
                } catch (final Exception e) {
                    new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                } finally {
                    BytecodeViewer.viewer.setIcon(false);

                    if (update)
                        try {
                            MainViewerGUI.getComponent(FileNavigationPane.class).updateTree();
                        } catch (java.lang.NullPointerException e) {
                        }
                }
            }
        };
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
    public static void resetWorkSpace(boolean ask) {
        if (!ask) {
            files.clear();
            MainViewerGUI.getComponent(FileNavigationPane.class).resetWorkspace();
            MainViewerGUI.getComponent(WorkPane.class).resetWorkspace();
            MainViewerGUI.getComponent(SearchingPane.class).resetWorkspace();
            the.bytecode.club.bytecodeviewer.api.BytecodeViewer.getClassNodeLoader().clear();
        } else {
            JOptionPane pane = new JOptionPane(
                    "Are you sure you want to reset the workspace?\n\rIt will also reset your file navigator and search.");
            Object[] options = new String[]{"Yes", "No"};
            pane.setOptions(options);
            JDialog dialog = pane.createDialog(viewer,
                    "Bytecode Viewer - Reset Workspace");
            dialog.setVisible(true);
            Object obj = pane.getValue();
            int result = -1;
            for (int k = 0; k < options.length; k++)
                if (options[k].equals(obj))
                    result = k;

            if (result == 0) {
                files.clear();
                MainViewerGUI.getComponent(FileNavigationPane.class).resetWorkspace();
                MainViewerGUI.getComponent(WorkPane.class).resetWorkspace();
                MainViewerGUI.getComponent(SearchingPane.class).resetWorkspace();
                the.bytecode.club.bytecodeviewer.api.BytecodeViewer.getClassNodeLoader().clear();
            }
        }
    }

    private static ArrayList<String> killList = new ArrayList<String>();

    /**
     * Add the recent file
     *
     * @param f the recent file
     */
    public static void addRecentFile(File f) {
        for (int i = 0; i < recentFiles.size(); i++) { // remove dead strings
            String s = recentFiles.get(i);
            if (s.isEmpty() || i > maxRecentFiles)
                killList.add(s);
        }
        if (!killList.isEmpty()) {
            for (String s : killList)
                recentFiles.remove(s);
            killList.clear();
        }

        if (recentFiles.contains(f.getAbsolutePath())) // already added on the list
            recentFiles.remove(f.getAbsolutePath());
        if (recentFiles.size() >= maxRecentFiles)
            recentFiles.remove(maxRecentFiles - 1); // zero indexing

        recentFiles.add(0, f.getAbsolutePath());
        DiskWriter.replaceFile(filesName, quickConvert(recentFiles), false);
        resetRecentFilesMenu();
    }

    private static ArrayList<String> killList2 = new ArrayList<String>();

    /**
     * Add to the recent plugin list
     *
     * @param f the plugin file
     */
    public static void addRecentPlugin(File f) {
        for (int i = 0; i < recentPlugins.size(); i++) { // remove dead strings
            String s = recentPlugins.get(i);
            if (s.isEmpty() || i > maxRecentFiles)
                killList2.add(s);
        }
        if (!killList2.isEmpty()) {
            for (String s : killList2)
                recentPlugins.remove(s);
            killList2.clear();
        }

        if (recentPlugins.contains(f.getAbsolutePath())) // already added on the list
            recentPlugins.remove(f.getAbsolutePath());
        if (recentPlugins.size() >= maxRecentFiles)
            recentPlugins.remove(maxRecentFiles - 1); // zero indexing

        recentPlugins.add(0, f.getAbsolutePath());
        DiskWriter.replaceFile(pluginsName, quickConvert(recentPlugins), false);
        resetRecentFilesMenu();
    }

    /**
     * resets the recent files menu
     */
    public static void resetRecentFilesMenu() {
        viewer.mnRecentFiles.removeAll();
        for (String s : recentFiles)
            if (!s.isEmpty()) {
                JMenuItem m = new JMenuItem(s);
                m.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JMenuItem m = (JMenuItem) e.getSource();
                        openFiles(new File[]{new File(m.getText())}, true);
                    }
                });
                viewer.mnRecentFiles.add(m);
            }
        viewer.mnRecentPlugins.removeAll();
        for (String s : recentPlugins)
            if (!s.isEmpty()) {
                JMenuItem m = new JMenuItem(s);
                m.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JMenuItem m = (JMenuItem) e.getSource();
                        startPlugin(new File(m.getText()));
                    }
                });
                viewer.mnRecentPlugins.add(m);
            }
    }

    private static File tempF = null;

    /**
     * Clears the temp directory
     */
    public static void cleanup() {
        tempF = new File(tempDirectory);

        try {
            FileUtils.deleteDirectory(tempF);
        } catch (Exception e) {
        }

        while (!tempF.exists()) // keep making dirs
            tempF.mkdir();
    }

    public static ArrayList<String> createdRandomizedNames = new ArrayList<String>();

    /**
     * Ensures it will only return a uniquely generated names, contains a dupe checker to be sure
     *
     * @return the unique randomized name of 25 characters.
     */
    public static String getRandomizedName() {
        boolean generated = false;
        String name = "";
        while (!generated) {
            String randomizedName = MiscUtils.randomString(25);
            if (!createdRandomizedNames.contains(randomizedName)) {
                createdRandomizedNames.add(randomizedName);
                name = randomizedName;
                generated = true;
            }
        }
        return name;
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
        sm.stopBlocking();
        try {
            // Hide file by running attrib system command (on Windows)
            Runtime.getRuntime().exec("attrib +H " + f.getAbsolutePath());
        } catch (Exception e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }
        sm.setBlocking();
    }

    /**
     * Converts an array list to a string
     *
     * @param a array
     * @return string with newline per array object
     */
    private static String quickConvert(ArrayList<String> a) {
        String s = "";
        for (String r : a)
            s += r + nl;
        return s;
    }

    private static long last = System.currentTimeMillis();

    /**
     * Checks the hotkeys
     *
     * @param e
     */
    public static void checkHotKey(KeyEvent e) {
        if (System.currentTimeMillis() - last <= (4000))
            return;

        if ((e.getKeyCode() == KeyEvent.VK_O) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            last = System.currentTimeMillis();
            JFileChooser fc = new JFileChooser();
            try {
                fc.setSelectedFile(new File(BytecodeViewer.lastDirectory));
            } catch (Exception e2) {

            }
            fc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory())
                        return true;

                    String extension = MiscUtils.extension(f.getAbsolutePath());
                    if (extension != null)
                        if (extension.equals("jar") || extension.equals("zip")
                                || extension.equals("class") || extension.equals("apk")
                                || extension.equals("dex"))
                            return true;

                    return false;
                }

                @Override
                public String getDescription() {
                    return "APKs, DEX, Class Files or Zip/Jar Archives";
                }
            });
            fc.setFileHidingEnabled(false);
            fc.setAcceptAllFileFilterUsed(false);
            int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                BytecodeViewer.lastDirectory = fc.getSelectedFile().getAbsolutePath();
                try {
                    BytecodeViewer.viewer.setIcon(true);
                    BytecodeViewer.openFiles(new File[]{fc.getSelectedFile()}, true);
                    BytecodeViewer.viewer.setIcon(false);
                } catch (Exception e1) {
                    new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
                }
            }
        } else if ((e.getKeyCode() == KeyEvent.VK_N) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            last = System.currentTimeMillis();
            BytecodeViewer.resetWorkSpace(true);
        } else if ((e.getKeyCode() == KeyEvent.VK_T) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            last = System.currentTimeMillis();
            Thread t = new Thread() {
                public void run() {
                    BytecodeViewer.compile(true);
                }
            };
            t.start();
        } else if ((e.getKeyCode() == KeyEvent.VK_R) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            last = System.currentTimeMillis();
            if (BytecodeViewer.getLoadedClasses().isEmpty()) {
                BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                return;
            }
            new RunOptions().setVisible(true);
        } else if ((e.getKeyCode() == KeyEvent.VK_S) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            last = System.currentTimeMillis();

            if (BytecodeViewer.getLoadedClasses().isEmpty()) {
                BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                return;
            }

            Thread t = new Thread() {
                public void run() {
                    if (viewer.autoCompileSmali.isSelected() && !BytecodeViewer.compile(false))
                        return;
                    JFileChooser fc = new JFileChooser();
                    fc.setFileFilter(new FileFilter() {
                        @Override
                        public boolean accept(File f) {
                            return f.isDirectory() || MiscUtils.extension(f.getAbsolutePath()).equals("zip");
                        }

                        @Override
                        public String getDescription() {
                            return "Zip Archives";
                        }
                    });
                    fc.setFileHidingEnabled(false);
                    fc.setAcceptAllFileFilterUsed(false);
                    int returnVal = fc.showSaveDialog(viewer);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        if (!file.getAbsolutePath().endsWith(".zip"))
                            file = new File(file.getAbsolutePath() + ".zip");

                        if (file.exists()) {
                            JOptionPane pane = new JOptionPane(
                                    "Are you sure you wish to overwrite this existing file?");
                            Object[] options = new String[]{"Yes", "No"};
                            pane.setOptions(options);
                            JDialog dialog = pane.createDialog(BytecodeViewer.viewer,
                                    "Bytecode Viewer - Overwrite File");
                            dialog.setVisible(true);
                            Object obj = pane.getValue();
                            int result = -1;
                            for (int k = 0; k < options.length; k++)
                                if (options[k].equals(obj))
                                    result = k;

                            if (result == 0) {
                                file.delete();
                            } else {
                                return;
                            }
                        }

                        final File file2 = file;

                        BytecodeViewer.viewer.setIcon(true);
                        Thread t = new Thread() {
                            @Override
                            public void run() {
                                JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(),
                                        file2.getAbsolutePath());
                                BytecodeViewer.viewer.setIcon(false);
                            }
                        };
                        t.start();
                    }
                }
            };
            t.start();
        } else if ((e.getKeyCode() == KeyEvent.VK_W) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            last = System.currentTimeMillis();
            if (viewer.workPane.getCurrentViewer() != null)
                viewer.workPane.tabs.remove(viewer.workPane.getCurrentViewer());
        }
    }
}
