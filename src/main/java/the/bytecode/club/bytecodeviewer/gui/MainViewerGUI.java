package the.bytecode.club.bytecodeviewer.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.Resources;
import the.bytecode.club.bytecodeviewer.Settings;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.obfuscators.rename.RenameClasses;
import the.bytecode.club.bytecodeviewer.obfuscators.rename.RenameFields;
import the.bytecode.club.bytecodeviewer.obfuscators.rename.RenameMethods;
import the.bytecode.club.bytecodeviewer.plugin.PluginManager;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.AllatoriStringDecrypter;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.CodeSequenceDiagram;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.ShowAllStrings;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.ShowMainMethods;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.StackFramesRemover;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.ZKMStringDecrypter;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.ZStringArrayDecrypter;
import the.bytecode.club.bytecodeviewer.util.*;

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
 * The main file for the GUI
 *
 * @author Konloch
 */
public class MainViewerGUI extends JFrame implements FileChangeNotifier {

    public static final long serialVersionUID = 1851409230530948543L;
    private static ArrayList<VisibleComponent> rfComps = new ArrayList<>();
    
    //the root menu bar
    public final JMenuBar rootMenu = new JMenuBar();
    
    //all of the files main menu components
    public final JMenu fileMainMenu = new JMenu("File");
    public final JMenuItem newWorkSpace = new JMenuItem("New Workspace");
    public final JMenuItem addResource = new JMenuItem("Add...");
    public final JMenuItem reloadResources = new JMenuItem("Reload Resources");
    public final JMenuItem runButton = new JMenuItem("Run");
    public final JMenuItem compileButton = new JMenuItem("Compile");
    public final JMenuItem saveAsRunnableJar = new JMenuItem("Save As Runnable Jar..");
    public final JMenuItem saveAsDex = new JMenuItem("Save As DEX..");
    public final JMenuItem saveAsAPK = new JMenuItem("Save As APK..");
    public final JMenuItem saveAsZip = new JMenuItem("Save As Zip..");
    public final JMenuItem decompileSaveOpened = new JMenuItem("Decompile & Save Opened Class..");
    public final JMenuItem decompileSaveAll = new JMenuItem("Decompile & Save All Classes..");
    public final JMenu recentFilesSecondaryMenu = new JMenu("Recent Files");
    public final JMenuItem aboutButton = new JMenuItem("About");
    public final JMenuItem exitButton = new JMenuItem("Exit");
    
    //all of the view main menu components
    public final JMenu viewMainMenu = new JMenu("View");
    public final ViewPane viewPane1 = new ViewPane(1);
    public final ViewPane viewPane2 = new ViewPane(2);
    public final ViewPane viewPane3 = new ViewPane(3);
    
    //TODO settings main menu components
    
    //all of the plugins main menu components
    public final JMenu pluginsMainMenu = new JMenu("Plugins");
    public final JMenuItem openExternalPlugin = new JMenuItem("Open Plugin...");
    public final JMenu recentPluginsSecondaryMenu = new JMenu("Recent Plugins");
    public final JMenuItem ZKMStringDecrypter = new JMenuItem("ZKM String Decrypter");
    public final JMenuItem allatoriStringDecrypter = new JMenuItem("Allatori String Decrypter");
    public final JMenuItem codeSequenceDiagram = new JMenuItem("Code Sequence Diagram");
    public final JMenuItem maliciousCodeScanner = new JMenuItem("Malicious Code Scanner");
    public final JMenuItem showAllStrings = new JMenuItem("Show All Strings");
    public final JMenuItem showMainMethods = new JMenuItem("Show Main Methods");
    public final JMenuItem replaceStrings = new JMenuItem("Replace Strings");
    public final JMenuItem stackFramesRemover = new JMenuItem("StackFrames Remover");
    public final JMenuItem zStringArrayDecrypter = new JMenuItem("ZStringArray Decrypter");
    
    public JCheckBoxMenuItem debugHelpers = new JCheckBoxMenuItem("Debug Helpers");
    public JSplitPane sp1;
    public JSplitPane sp2;
    public JCheckBoxMenuItem rbr = new JCheckBoxMenuItem("Hide bridge methods");
    public JCheckBoxMenuItem rsy = new JCheckBoxMenuItem("Hide synthetic class members");
    public JCheckBoxMenuItem din = new JCheckBoxMenuItem("Decompile inner classes");
    public JCheckBoxMenuItem dc4 = new JCheckBoxMenuItem("Collapse 1.4 class references");
    public JCheckBoxMenuItem das = new JCheckBoxMenuItem("Decompile assertions");
    public JCheckBoxMenuItem hes = new JCheckBoxMenuItem("Hide empty super invocation");
    public JCheckBoxMenuItem hdc = new JCheckBoxMenuItem("Hide empty default constructor");
    public JCheckBoxMenuItem dgs = new JCheckBoxMenuItem("Decompile generic signatures");
    public JCheckBoxMenuItem ner = new JCheckBoxMenuItem("Assume return not throwing exceptions");
    public JCheckBoxMenuItem den = new JCheckBoxMenuItem("Decompile enumerations");
    public JCheckBoxMenuItem rgn = new JCheckBoxMenuItem("Remove getClass() invocation");
    public JCheckBoxMenuItem bto = new JCheckBoxMenuItem("Interpret int 1 as boolean true");
    public JCheckBoxMenuItem nns = new JCheckBoxMenuItem("Allow for not set synthetic attribute");
    public JCheckBoxMenuItem uto = new JCheckBoxMenuItem("Consider nameless types as java.lang.Object");
    public JCheckBoxMenuItem udv = new JCheckBoxMenuItem("Reconstruct variable names from debug info");
    public JCheckBoxMenuItem rer = new JCheckBoxMenuItem("Remove empty exception ranges");
    public JCheckBoxMenuItem fdi = new JCheckBoxMenuItem("Deinline finally structures");
    public JCheckBoxMenuItem asc = new JCheckBoxMenuItem("Allow only ASCII characters in strings");
    public JCheckBoxMenuItem ren = new JCheckBoxMenuItem("Rename ambiguous classes and class elements");
    
    public final JMenuItem[] waitIcons;
    public final JMenu mnNewMenu_3 = new JMenu("CFR");
    public final JMenu mnNewMenu_4 = new JMenu("Procyon");
    public final JCheckBoxMenuItem decodeenumswitch = new JCheckBoxMenuItem("Decode Enum Switch");
    public final JCheckBoxMenuItem sugarenums = new JCheckBoxMenuItem("SugarEnums");
    public final JCheckBoxMenuItem decodestringswitch = new JCheckBoxMenuItem("Decode String Switch");
    public final JCheckBoxMenuItem arrayiter = new JCheckBoxMenuItem("Arrayiter");
    public final JCheckBoxMenuItem collectioniter = new JCheckBoxMenuItem("Collectioniter");
    public final JCheckBoxMenuItem innerclasses = new JCheckBoxMenuItem("Inner Classes");
    public final JCheckBoxMenuItem removeboilerplate = new JCheckBoxMenuItem("Remove Boiler Plate");
    public final JCheckBoxMenuItem removeinnerclasssynthetics = new JCheckBoxMenuItem("Remove Inner Class Synthetics");
    public final JCheckBoxMenuItem decodelambdas = new JCheckBoxMenuItem("Decode Lambdas");
    public final JCheckBoxMenuItem hidebridgemethods = new JCheckBoxMenuItem("Hide Bridge Methods");
    public final JCheckBoxMenuItem liftconstructorinit = new JCheckBoxMenuItem("Lift  Constructor Init");
    public final JCheckBoxMenuItem removedeadmethods = new JCheckBoxMenuItem("Remove Dead Methods");
    public final JCheckBoxMenuItem removebadgenerics = new JCheckBoxMenuItem("Remove Bad Generics");
    public final JCheckBoxMenuItem sugarasserts = new JCheckBoxMenuItem("Sugar Asserts");
    public final JCheckBoxMenuItem sugarboxing = new JCheckBoxMenuItem("Sugar Boxing");
    public final JCheckBoxMenuItem showversion = new JCheckBoxMenuItem("Show Version");
    public final JCheckBoxMenuItem decodefinally = new JCheckBoxMenuItem("Decode Finally");
    public final JCheckBoxMenuItem tidymonitors = new JCheckBoxMenuItem("Tidy Monitors");
    public final JCheckBoxMenuItem lenient = new JCheckBoxMenuItem("Lenient");
    public final JCheckBoxMenuItem dumpclasspath = new JCheckBoxMenuItem("Dump Classpath");
    public final JCheckBoxMenuItem comments = new JCheckBoxMenuItem("Comments");
    public final JCheckBoxMenuItem forcetopsort = new JCheckBoxMenuItem("Force Top Sort");
    public final JCheckBoxMenuItem forcetopsortaggress = new JCheckBoxMenuItem("Force Top Sort Aggress");
    public final JCheckBoxMenuItem stringbuffer = new JCheckBoxMenuItem("String Buffer");
    public final JCheckBoxMenuItem stringbuilder = new JCheckBoxMenuItem("String Builder");
    public final JCheckBoxMenuItem silent = new JCheckBoxMenuItem("Silent");
    public final JCheckBoxMenuItem recover = new JCheckBoxMenuItem("Recover");
    public final JCheckBoxMenuItem eclipse = new JCheckBoxMenuItem("Eclipse");
    public final JCheckBoxMenuItem override = new JCheckBoxMenuItem("Override");
    public final JCheckBoxMenuItem showinferrable = new JCheckBoxMenuItem("Show Inferrable");
    public final JCheckBoxMenuItem aexagg = new JCheckBoxMenuItem("Aexagg");
    public final JCheckBoxMenuItem forcecondpropagate = new JCheckBoxMenuItem("Force Cond Propagate");
    public final JCheckBoxMenuItem hideutf = new JCheckBoxMenuItem("Hide UTF");
    public final JCheckBoxMenuItem hidelongstrings = new JCheckBoxMenuItem("Hide Long Strings");
    public final JCheckBoxMenuItem commentmonitor = new JCheckBoxMenuItem("Comment Monitors");
    public final JCheckBoxMenuItem allowcorrecting = new JCheckBoxMenuItem("Allow Correcting");
    public final JCheckBoxMenuItem labelledblocks = new JCheckBoxMenuItem("Labelled Blocks");
    public final JCheckBoxMenuItem j14classobj = new JCheckBoxMenuItem("J14ClassOBJ");
    public final JCheckBoxMenuItem hidelangimports = new JCheckBoxMenuItem("Hide Lang Imports");
    public final JCheckBoxMenuItem recoverytypeclash = new JCheckBoxMenuItem("Recover Type Clash");
    public final JCheckBoxMenuItem recoverytypehints = new JCheckBoxMenuItem("Recover Type  Hints");
    public final JCheckBoxMenuItem forceturningifs = new JCheckBoxMenuItem("Force Returning IFs");
    public final JCheckBoxMenuItem forloopaggcapture = new JCheckBoxMenuItem("For Loop AGG Capture");
    public final JCheckBoxMenuItem forceexceptionprune = new JCheckBoxMenuItem("Force Exception Prune");
    public final JCheckBoxMenuItem chckbxmntmShowDebugLine = new JCheckBoxMenuItem("Show Debug Line Numbers");
    public final JCheckBoxMenuItem chckbxmntmSimplifyMemberReferences = new JCheckBoxMenuItem("Simplify Member References");
    public final JCheckBoxMenuItem mnMergeVariables = new JCheckBoxMenuItem("Merge Variables");
    public final JCheckBoxMenuItem chckbxmntmNewCheckItem_1 = new JCheckBoxMenuItem("Unicode Output Enabled");
    public final JCheckBoxMenuItem chckbxmntmNewCheckItem_2 = new JCheckBoxMenuItem("Retain Pointless Switches");
    public final JCheckBoxMenuItem chckbxmntmNewCheckItem_3 = new JCheckBoxMenuItem("Include Line Numbers In Bytecode");
    public final JCheckBoxMenuItem chckbxmntmNewCheckItem_4 = new JCheckBoxMenuItem("Include Error Diagnostics");
    public final JCheckBoxMenuItem chckbxmntmNewCheckItem_5 = new JCheckBoxMenuItem("Retain Redundant Casts");
    public final JCheckBoxMenuItem chckbxmntmNewCheckItem_6 = new JCheckBoxMenuItem("Always Generate Exception Variable For Catch Blocks");
    public final JCheckBoxMenuItem chckbxmntmNewCheckItem_7 = new JCheckBoxMenuItem("Show Synthetic Members");
    public final JCheckBoxMenuItem chckbxmntmNewCheckItem_8 = new JCheckBoxMenuItem("Force Explicit Type Arguments");
    public final JCheckBoxMenuItem chckbxmntmNewCheckItem_9 = new JCheckBoxMenuItem("Force Explicit Imports");
    public final JCheckBoxMenuItem chckbxmntmNewCheckItem_10 = new JCheckBoxMenuItem("Flatten Switch Blocks");
    public final JCheckBoxMenuItem chckbxmntmNewCheckItem_11 = new JCheckBoxMenuItem("Exclude Nested Types");
    public final JCheckBoxMenuItem chckbxmntmAppendBrackets = new JCheckBoxMenuItem("Append Brackets To Labels");
    public final JCheckBoxMenuItem chckbxmntmNewCheckItem_12 = new JCheckBoxMenuItem("Update Check");
    public final JMenu mnNewMenu_5 = new JMenu("Obfuscate");
    public final JMenuItem mntmNewMenuItem_6 = new JMenuItem("Rename Fields");
    public final JMenuItem mntmNewMenuItem_7 = new JMenuItem("Rename Methods");
    public final JMenuItem mntmNewMenuItem_8 = new JMenuItem("Move All Classes Into Root Package");
    public final JMenuItem mntmNewMenuItem_9 = new JMenuItem("Control Flow");
    public final JMenuItem mntmNewMenuItem_10 = new JMenuItem("Junk Code");
    public final ButtonGroup obfuscatorGroup = new ButtonGroup();
    public final JRadioButtonMenuItem strongObf = new JRadioButtonMenuItem("Strong Obfuscation");
    public final JRadioButtonMenuItem lightObf = new JRadioButtonMenuItem("Light Obfuscation");
    public final JMenuItem mntmNewMenuItem_11 = new JMenuItem("Rename Classes");
    public final JMenu mnSettings = new JMenu("Settings");
    public AboutWindow aboutWindow = new AboutWindow();
    
    public final JCheckBoxMenuItem compileOnSave = new JCheckBoxMenuItem("Compile On Save");
    public final JCheckBoxMenuItem showFileInTabTitle = new JCheckBoxMenuItem("Show File In Tab Title");
    public final JCheckBoxMenuItem forcePureAsciiAsText = new JCheckBoxMenuItem("Force Pure Ascii As Text");
    public final JCheckBoxMenuItem autoCompileOnRefresh = new JCheckBoxMenuItem("Compile On Refresh");
    public final JMenuItem mntmSetPythonDirectory = new JMenuItem("Set Python 2.7 Executable");
    public final JMenuItem mntmSetJreRt = new JMenuItem("Set JRE RT Library");
    public final JCheckBoxMenuItem decodeAPKResources = new JCheckBoxMenuItem("Decode APK Resources");
    public final JCheckBoxMenuItem synchronizedViewing = new JCheckBoxMenuItem("Synchronized Viewing");
    public final JCheckBoxMenuItem showClassMethods = new JCheckBoxMenuItem("Show Class Methods");
    

    public FileNavigationPane cn = new FileNavigationPane(this);
    public SearchingPane s;

    public boolean isMaximized = false;

    public void removed(boolean busy) {
        if (busy) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            for (Component c : this.getComponents())
                c.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            sp1.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            sp2.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            for (VisibleComponent c : rfComps) {
                c.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                if (c instanceof WorkPane) {
                    WorkPane w = (WorkPane) c;
                    for (Component c2 : w.tabs.getComponents())
                        c2.setCursor(Cursor
                                .getPredefinedCursor(Cursor.WAIT_CURSOR));
                }
            }
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            for (Component c : this.getComponents())
                c.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            sp1.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            sp2.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            for (VisibleComponent c : rfComps) {
                c.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                if (c instanceof WorkPane) {
                    WorkPane w = (WorkPane) c;
                    for (Component c2 : w.tabs.getComponents())
                        c2.setCursor(Cursor
                                .getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        }
    }

    public static class Test implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            BytecodeViewer.checkHotKey(e);
            return false;
        }
    }
    
    public synchronized void setIcon(final boolean busy) {
        SwingUtilities.invokeLater(() -> {
            if (busy) {
                for (int i = 0; i < 10; i++) {
                    if (waitIcons[i].getIcon() == null) {
                        try {
                            waitIcons[i].setIcon(Resources.busyIcon);
                        } catch (NullPointerException e) {
                            waitIcons[i].setIcon(Resources.busyB64Icon);
                        }
                        waitIcons[i].updateUI();
                        break;
                    }
                }
            } else {
                for (int i = 0; i < 10; i++) {
                    if (waitIcons[i].getIcon() != null) {
                        waitIcons[i].setIcon(null);
                        waitIcons[i].updateUI();
                        break;
                    }
                }
            }
        });
    }

    public final JSpinner fontSpinner = new JSpinner();
    private final JCheckBoxMenuItem chckbxmntmDeleteForeignOutdatedLibs = new JCheckBoxMenuItem("Delete Foreign/Outdated Libs");
    public final ButtonGroup apkConversionGroup = new ButtonGroup();
    public final JRadioButtonMenuItem apkConversionDex = new JRadioButtonMenuItem("Dex2Jar");
    public final JRadioButtonMenuItem apkConversionEnjarify = new JRadioButtonMenuItem("Enjarify");

    public void calledAfterLoad() {
        chckbxmntmDeleteForeignOutdatedLibs.setSelected(Configuration.deleteForeignLibraries);
    }
    
    public final JCheckBoxMenuItem refreshOnChange = new JCheckBoxMenuItem("Refresh On View Change");
    public final WorkPane workPane = new WorkPane(this);

    public MainViewerGUI()
    {
        mnNewMenu_5.setVisible(false);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new Test());
        this.addWindowStateListener(new WindowAdapter() {
            @Override
            public void windowStateChanged(WindowEvent evt) {
                int oldState = evt.getOldState();
                int newState = evt.getNewState();

                /*if ((oldState & Frame.ICONIFIED) == 0 && (newState & Frame.ICONIFIED) != 0) {
                    System.out.println("Frame was iconized");
                } else if ((oldState & Frame.ICONIFIED) != 0 && (newState & Frame.ICONIFIED) == 0) {
                    System.out.println("Frame was deiconized");
                }*/

                if ((oldState & Frame.MAXIMIZED_BOTH) == 0 && (newState & Frame.MAXIMIZED_BOTH) != 0) {
                    isMaximized = true;
                } else if ((oldState & Frame.MAXIMIZED_BOTH) != 0 && (newState & Frame.MAXIMIZED_BOTH) == 0) {
                    isMaximized = false;
                }
            }
        });
        this.setIconImages(Resources.iconList);

        obfuscatorGroup.add(strongObf);
        obfuscatorGroup.add(lightObf);
        obfuscatorGroup.setSelected(strongObf.getModel(), true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Configuration.canExit = true;
                System.exit(0);
            }
        });

        buildMenuBar();
        buildFileMenuBar();
        buildViewMenuBar();
        buildPluginMenuBar();

        compileOnSave.setSelected(false);

        rootMenu.add(mnSettings);


        JMenu visualSettings = new JMenu("Visual Settings");
        mnSettings.add(visualSettings);
        mnSettings.add(new JSeparator());
        mnSettings.add(compileOnSave);
        compileOnSave.setSelected(false);


        autoCompileOnRefresh.setSelected(false);

        mnSettings.add(autoCompileOnRefresh);

        mnSettings.add(refreshOnChange);

        mnSettings.add(new JSeparator());
        decodeAPKResources.setSelected(true);

        mnSettings.add(decodeAPKResources);

        JMenu mnApkConversion = new JMenu("APK Conversion");
        mnSettings.add(mnApkConversion);

        mnApkConversion.add(apkConversionDex);

        mnApkConversion.add(apkConversionEnjarify);

        mnSettings.add(new JSeparator());

        chckbxmntmNewCheckItem_12.setSelected(true);
        mnSettings.add(chckbxmntmNewCheckItem_12);
        chckbxmntmDeleteForeignOutdatedLibs.addActionListener(arg0 -> {
            if (!chckbxmntmDeleteForeignOutdatedLibs.isSelected()) {
                BytecodeViewer.showMessage("WARNING: With this being toggled off outdated libraries will NOT be "
                        + "removed. It's also a security issue. ONLY TURN IT OFF IF YOU KNOW WHAT YOU'RE DOING.");
            }
            Configuration.deleteForeignLibraries = chckbxmntmDeleteForeignOutdatedLibs.isSelected();
        });
        mnSettings.add(forcePureAsciiAsText);
        forcePureAsciiAsText.setSelected(true);
        forcePureAsciiAsText.addActionListener(arg0 -> Settings.saveSettings());
        mnSettings.add(new JSeparator());

        /*chckbxmntmDeleteForeinoutdatedLibs.setSelected(true);
        mnSettings.add(chckbxmntmDeleteForeinoutdatedLibs);
        mnSettings.add(separator_36);*/

        mntmSetPythonDirectory.addActionListener(arg0 -> selectPythonC());

        mnSettings.add(mntmSetPythonDirectory);
        mntmSetJreRt.addActionListener(arg0 -> selectJRERTLibrary());
        JMenuItem mntmSetPythonx = new JMenuItem("Set Python 3.X Executable");
        mntmSetPythonx.addActionListener(arg0 -> selectPythonC3());

        mnSettings.add(mntmSetPythonx);

        mnSettings.add(mntmSetJreRt);
        JMenuItem mntmSetOpitonalLibrary = new JMenuItem("Set Optional Library Folder");
        mntmSetOpitonalLibrary.addActionListener(arg0 -> selectOpenalLibraryFolder());

        mnSettings.add(mntmSetOpitonalLibrary);
        JMenuItem mntmSetJavacExecutable = new JMenuItem("Set Javac Executable");
        mntmSetJavacExecutable.addActionListener(arg0 -> selectJavac());

        mnSettings.add(mntmSetJavacExecutable);

        mnSettings.add(new JSeparator());
        mnSettings.add(mnNewMenu_4);

        mnNewMenu_4.add(chckbxmntmNewCheckItem_6);

        mnNewMenu_4.add(chckbxmntmNewCheckItem_11);

        mnNewMenu_4.add(chckbxmntmShowDebugLine);

        mnNewMenu_4.add(chckbxmntmNewCheckItem_3);

        mnNewMenu_4.add(chckbxmntmNewCheckItem_4);

        chckbxmntmNewCheckItem_7.setSelected(true);
        mnNewMenu_4.add(chckbxmntmNewCheckItem_7);

        mnNewMenu_4.add(chckbxmntmSimplifyMemberReferences);

        mnNewMenu_4.add(mnMergeVariables);

        mnNewMenu_4.add(chckbxmntmNewCheckItem_8);

        mnNewMenu_4.add(chckbxmntmNewCheckItem_9);

        mnNewMenu_4.add(chckbxmntmNewCheckItem_10);

        mnNewMenu_4.add(chckbxmntmNewCheckItem_2);

        mnNewMenu_4.add(chckbxmntmNewCheckItem_5);

        mnNewMenu_4.add(chckbxmntmNewCheckItem_1);
        // cfr
        decodeenumswitch.setSelected(true);
        sugarenums.setSelected(true);
        decodestringswitch.setSelected(true);
        arrayiter.setSelected(true);
        collectioniter.setSelected(true);
        innerclasses.setSelected(true);
        removeboilerplate.setSelected(true);
        removeinnerclasssynthetics.setSelected(true);
        decodelambdas.setSelected(true);
        hidebridgemethods.setSelected(true);
        liftconstructorinit.setSelected(true);
        removedeadmethods.setSelected(true);
        removebadgenerics.setSelected(true);
        sugarasserts.setSelected(true);
        sugarboxing.setSelected(true);
        showversion.setSelected(true);
        decodefinally.setSelected(true);
        tidymonitors.setSelected(true);
        lenient.setSelected(false);
        dumpclasspath.setSelected(false);
        comments.setSelected(true);
        forcetopsort.setSelected(true);
        forcetopsortaggress.setSelected(true);
        forceexceptionprune.setSelected(true);
        stringbuffer.setSelected(false);
        stringbuilder.setSelected(true);
        silent.setSelected(true);
        recover.setSelected(true);
        eclipse.setSelected(true);
        override.setSelected(true);
        showinferrable.setSelected(true);
        aexagg.setSelected(true);
        forcecondpropagate.setSelected(true);
        hideutf.setSelected(true);
        hidelongstrings.setSelected(false);
        commentmonitor.setSelected(false);
        allowcorrecting.setSelected(true);
        labelledblocks.setSelected(true);
        j14classobj.setSelected(false);
        hidelangimports.setSelected(true);
        recoverytypeclash.setSelected(true);
        recoverytypehints.setSelected(true);
        forceturningifs.setSelected(true);
        forloopaggcapture.setSelected(true);
        mnSettings.add(mnNewMenu_3);

        mnNewMenu_3.add(decodeenumswitch);

        mnNewMenu_3.add(sugarenums);

        mnNewMenu_3.add(decodestringswitch);

        mnNewMenu_3.add(arrayiter);

        mnNewMenu_3.add(collectioniter);

        mnNewMenu_3.add(innerclasses);

        mnNewMenu_3.add(removeboilerplate);

        mnNewMenu_3.add(removeinnerclasssynthetics);

        mnNewMenu_3.add(decodelambdas);

        mnNewMenu_3.add(hidebridgemethods);

        mnNewMenu_3.add(liftconstructorinit);

        mnNewMenu_3.add(removedeadmethods);

        mnNewMenu_3.add(removebadgenerics);

        mnNewMenu_3.add(sugarasserts);

        mnNewMenu_3.add(sugarboxing);

        mnNewMenu_3.add(showversion);

        mnNewMenu_3.add(decodefinally);

        mnNewMenu_3.add(tidymonitors);

        mnNewMenu_3.add(lenient);

        mnNewMenu_3.add(dumpclasspath);

        mnNewMenu_3.add(comments);

        mnNewMenu_3.add(forcetopsort);

        mnNewMenu_3.add(forcetopsortaggress);

        mnNewMenu_3.add(forceexceptionprune);

        mnNewMenu_3.add(stringbuffer);

        mnNewMenu_3.add(stringbuilder);

        mnNewMenu_3.add(silent);

        mnNewMenu_3.add(recover);

        mnNewMenu_3.add(eclipse);

        mnNewMenu_3.add(override);

        mnNewMenu_3.add(showinferrable);

        mnNewMenu_3.add(aexagg);

        mnNewMenu_3.add(forcecondpropagate);

        mnNewMenu_3.add(hideutf);

        mnNewMenu_3.add(hidelongstrings);

        mnNewMenu_3.add(commentmonitor);

        mnNewMenu_3.add(allowcorrecting);

        mnNewMenu_3.add(labelledblocks);

        mnNewMenu_3.add(j14classobj);

        mnNewMenu_3.add(hidelangimports);

        mnNewMenu_3.add(recoverytypeclash);

        mnNewMenu_3.add(recoverytypehints);

        mnNewMenu_3.add(forceturningifs);

        mnNewMenu_3.add(forloopaggcapture);
        // fernflower
        rbr.setSelected(true);
        rsy.setSelected(false);
        din.setSelected(true);
        das.setSelected(true);
        dgs.setSelected(false);
        den.setSelected(true);
        uto.setSelected(true);
        udv.setSelected(true);
        fdi.setSelected(true);
        asc.setSelected(false);
        ren.setSelected(false);

        JMenu mnDecompilerSettings = new JMenu("FernFlower");
        mnSettings.add(mnDecompilerSettings);
        dc4.setSelected(true);
        mnDecompilerSettings.add(dc4);
        nns.setSelected(true);
        mnDecompilerSettings.add(nns);
        ner.setSelected(true);
        mnDecompilerSettings.add(ner);
        bto.setSelected(true);
        mnDecompilerSettings.add(bto);
        rgn.setSelected(true);
        mnDecompilerSettings.add(rgn);
        rer.setSelected(true);
        mnDecompilerSettings.add(rer);
        mnDecompilerSettings.add(rbr);
        mnDecompilerSettings.add(rsy);
        hes.setSelected(true);
        mnDecompilerSettings.add(hes);
        hdc.setSelected(true);
        mnDecompilerSettings.add(hdc);
        mnDecompilerSettings.add(din);
        mnDecompilerSettings.add(das);
        mnDecompilerSettings.add(dgs);
        mnDecompilerSettings.add(den);
        mnDecompilerSettings.add(uto);
        mnDecompilerSettings.add(udv);
        mnDecompilerSettings.add(fdi);
        mnDecompilerSettings.add(asc);
        mnDecompilerSettings.add(ren);
        debugHelpers.setSelected(true);
        // other
        chckbxmntmAppendBrackets.setSelected(true);

        JMenu mnBytecodeDecompilerSettings = new JMenu("Bytecode Decompiler");
        mnSettings.add(mnBytecodeDecompilerSettings);

        mnBytecodeDecompilerSettings.add(debugHelpers);

        mnBytecodeDecompilerSettings.add(chckbxmntmAppendBrackets);

        rootMenu.add(mnNewMenu_5);
        mntmNewMenuItem_6.addActionListener(arg0 -> {
            if (Configuration.runningObfuscation) {
                BytecodeViewer.showMessage("You're currently running an obfuscation task, wait for this to finish.");
                return;
            }
            new RenameFields().start();
            workPane.refreshClass.doClick();
            cn.tree.updateUI();
        });

        mnNewMenu_5.add(strongObf);

        mnNewMenu_5.add(lightObf);

        mnNewMenu_5.add(new JSeparator());
        mntmNewMenuItem_8.setEnabled(false);

        mnNewMenu_5.add(mntmNewMenuItem_8);

        mnNewMenu_5.add(mntmNewMenuItem_6);
        mntmNewMenuItem_7.addActionListener(arg0 -> {
            if (Configuration.runningObfuscation) {
                BytecodeViewer.showMessage("You're currently running an obfuscation task, wait for this to finish"
                        + ".");
                return;
            }
            new RenameMethods().start();
            workPane.refreshClass.doClick();
            cn.tree.updateUI();
        });

        mnNewMenu_5.add(mntmNewMenuItem_7);
        mntmNewMenuItem_11.addActionListener(arg0 -> {
            if (Configuration.runningObfuscation) {
                BytecodeViewer.showMessage("You're currently running an obfuscation task, wait for this to finish"
                        + ".");
                return;
            }
            new RenameClasses().start();
            workPane.refreshClass.doClick();
            cn.tree.updateUI();
        });

        mnNewMenu_5.add(mntmNewMenuItem_11);
        mntmNewMenuItem_9.setEnabled(false);
        mnNewMenu_5.add(mntmNewMenuItem_9);
        mntmNewMenuItem_10.setEnabled(false);
        mnNewMenu_5.add(mntmNewMenuItem_10);

    
        codeSequenceDiagram.addActionListener(arg0 -> {
            if (BytecodeViewer.getLoadedClasses().isEmpty()) {
                BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                return;
            }
            PluginManager.runPlugin(new CodeSequenceDiagram());
        });
        replaceStrings.addActionListener(arg0 -> {
            if (BytecodeViewer.getLoadedClasses().isEmpty()) {
                BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                return;
            }
            new ReplaceStringsOptions().setVisible(true);
        });
        
        zStringArrayDecrypter.addActionListener(arg0 -> PluginManager.runPlugin(new ZStringArrayDecrypter()));
        stackFramesRemover.addActionListener(e -> PluginManager.runPlugin(new StackFramesRemover()));

        waitIcons = new JMenuItem[10];
        for (int i = 0; i < 10; i++) {
            waitIcons[i] = new JMenuItem("");
            waitIcons[i].setMaximumSize(new Dimension(20, 50));
            waitIcons[i].setEnabled(false);
            rootMenu.add(waitIcons[i]);
        }

        openExternalPlugin.addActionListener(arg0 -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(PluginManager.fileFilter());
            fc.setFileHidingEnabled(false);
            fc.setAcceptAllFileFilterUsed(false);
            int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);

            if (returnVal == JFileChooser.APPROVE_OPTION)
                try {
                    BytecodeViewer.viewer.setIcon(true);
                    BytecodeViewer.startPlugin(fc.getSelectedFile());
                    BytecodeViewer.viewer.setIcon(false);
                } catch (Exception e1) {
                    new ExceptionUI(e1);
                }
        });
        
        ZKMStringDecrypter.addActionListener(e -> PluginManager.runPlugin(new ZKMStringDecrypter()));
        allatoriStringDecrypter.addActionListener(e -> PluginManager.runPlugin(new AllatoriStringDecrypter()));
        maliciousCodeScanner.addActionListener(e -> MaliciousCodeScannerOptions.showOptionPanel());
        showAllStrings.addActionListener(e -> PluginManager.runPlugin(new ShowAllStrings()));
        showMainMethods.addActionListener(e -> PluginManager.runPlugin(new ShowMainMethods()));

        setSize(new Dimension(800, 400));
        if (PREVIEW_COPY)
            setTitle("Bytecode Viewer " + VERSION + " Preview - https://bytecodeviewer.com | https://the.bytecode.club - @Konloch");
        else
            setTitle("Bytecode Viewer " + VERSION + " - https://bytecodeviewer.com | https://the.bytecode.club - @Konloch");

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

        // scrollPane.setViewportView(tree);
        cn.setMinimumSize(new Dimension(200, 50));
        // panel.add(cn);
        s = new SearchingPane(this);
        s.setPreferredSize(new Dimension(200, 50));
        s.setMinimumSize(new Dimension(200, 50));
        s.setMaximumSize(new Dimension(200, 2147483647));
        // panel.add(s);
        sp1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, cn, s);
        // panel.add(sp1);
        cn.setPreferredSize(new Dimension(200, 50));
        cn.setMaximumSize(new Dimension(200, 2147483647));
        sp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sp1, workPane);
        getContentPane().add(sp2);
        sp2.setResizeWeight(0.05);
        sp1.setResizeWeight(0.5);
        rfComps.add(cn);

        rfComps.add(s);
        rfComps.add(workPane);

        apkConversionGroup.add(apkConversionDex);
        apkConversionGroup.add(apkConversionEnjarify);
        apkConversionGroup.setSelected(apkConversionDex.getModel(), true);



        fontSpinner.setPreferredSize(new Dimension(42, 20));
        fontSpinner.setSize(new Dimension(42, 20));
        fontSpinner.setModel(new SpinnerNumberModel(12, 1, null, 1));

        JMenu mnFontSize = new JMenu("Font Size");
        mnFontSize.add(fontSpinner);

        visualSettings.add(mnFontSize);
        visualSettings.add(showFileInTabTitle);
        showFileInTabTitle.setSelected(false);
        showFileInTabTitle.addActionListener(arg0 -> {
            Configuration.displayParentInTab = BytecodeViewer.viewer.showFileInTabTitle.isSelected();
            Settings.saveSettings();
        });

        visualSettings.add(synchronizedViewing);
        showClassMethods.setSelected(false);
        visualSettings.add(showClassMethods);


        viewPane1.getGroup().setSelected(viewPane1.getFern().getJava().getModel(), true);
        viewPane2.getGroup().setSelected(viewPane1.getBytecode().getModel(), true);
        viewPane3.getGroup().setSelected(viewPane1.getNone().getModel(), true);

        this.setLocationRelativeTo(null);
    }
    
    public void buildMenuBar()
    {
        setJMenuBar(rootMenu);
    
        rootMenu.add(fileMainMenu);
        rootMenu.add(viewMainMenu);
    }
    
    public void buildFileMenuBar()
    {
        fileMainMenu.add(addResource);
        fileMainMenu.add(new JSeparator());
        fileMainMenu.add(newWorkSpace);
        fileMainMenu.add(new JSeparator());
        fileMainMenu.add(reloadResources);
        fileMainMenu.add(new JSeparator());
        fileMainMenu.add(runButton);
        fileMainMenu.add(compileButton);
        fileMainMenu.add(new JSeparator());
        fileMainMenu.add(saveAsRunnableJar);
        //fileMainMenuBar.add(mntmSaveAsAPK);
        fileMainMenu.add(saveAsDex);
        fileMainMenu.add(saveAsZip);
        fileMainMenu.add(decompileSaveOpened);
        fileMainMenu.add(decompileSaveAll);
        fileMainMenu.add(new JSeparator());
        fileMainMenu.add(recentFilesSecondaryMenu);
        fileMainMenu.add(new JSeparator());
        fileMainMenu.add(aboutButton);
        fileMainMenu.add(exitButton);
    
        saveAsZip.setActionCommand("");
    
        addResource.addActionListener(e -> selectFile());
        newWorkSpace.addActionListener(e -> BytecodeViewer.resetWorkSpace(true));
        reloadResources.addActionListener(arg0 -> reloadResources());
        runButton.addActionListener(e -> runResources());
        compileButton.addActionListener(arg0 -> compileOnNewThread());
        saveAsRunnableJar.addActionListener(e -> ResourceExporting.saveAsRunnableJar());
        saveAsAPK.addActionListener(arg0 -> ResourceExporting.saveAsAPK());
        saveAsDex.addActionListener(arg0 -> ResourceExporting.saveAsDex());
        saveAsZip.addActionListener(arg0 -> ResourceExporting.saveAsZip());
        decompileSaveAll.addActionListener(arg0 -> ResourceDecompiling.decompileSaveAll());
        decompileSaveOpened.addActionListener(arg0 -> ResourceDecompiling.decompileSaveOpenedOnly());
        aboutButton.addActionListener(arg0 -> aboutWindow.setVisible(true));
        exitButton.addActionListener(arg0 -> askBeforeExiting());
    }
    
    public void buildViewMenuBar()
    {
        viewMainMenu.add(viewPane1.menu);
        viewMainMenu.add(viewPane2.menu);
        viewMainMenu.add(viewPane3.menu);
    }
    
    public void buildPluginMenuBar()
    {
        rootMenu.add(pluginsMainMenu);
        pluginsMainMenu.add(openExternalPlugin);
        pluginsMainMenu.add(new JSeparator());
        pluginsMainMenu.add(recentPluginsSecondaryMenu);
        pluginsMainMenu.add(new JSeparator());
        pluginsMainMenu.add(codeSequenceDiagram);
        pluginsMainMenu.add(maliciousCodeScanner);
        pluginsMainMenu.add(showMainMethods);
        pluginsMainMenu.add(showAllStrings);
        pluginsMainMenu.add(replaceStrings);
        pluginsMainMenu.add(stackFramesRemover);
        //allatori and ZKM are disabled since they are just placeholders
        //mnNewMenu_1.add(mntmNewMenuItem_2);
        //mnNewMenu_1.add(mntmStartZkmString);
        pluginsMainMenu.add(zStringArrayDecrypter);
    }

    @Override
    public void openClassFile(final FileContainer container, final String name, final ClassNode cn) {
        for (final VisibleComponent vc : rfComps) {
            vc.openClassFile(container, name, cn);
        }
    }

    @Override
    public void openFile(final FileContainer container, final String name, byte[] content) {
        for (final VisibleComponent vc : rfComps) {
            vc.openFile(container, name, content);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getComponent(final Class<T> clazz) {
        for (final VisibleComponent vc : rfComps) {
            if (vc.getClass() == clazz)
                return (T) vc;
        }
        return null;
    }
    
    public void selectFile()
    {
        final JFileChooser fc = new JFileChooser();
    
        try {
            File f = new File(Configuration.lastDirectory);
            if (f.exists())
                fc.setSelectedFile(f);
        } catch (Exception ignored) {
        
        }
    
        fc.setDialogTitle("Select File or Folder to open in BCV");
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setAcceptAllFileFilterUsed(true);
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
            
                String extension = MiscUtils.extension(f.getAbsolutePath());
                return extension.equals("jar") || extension.equals("zip")
                        || extension.equals("class") || extension.equals("apk")
                        || extension.equals("dex") || extension.equals("war") || extension.equals("jsp");
            
            }
        
            @Override
            public String getDescription() {
                return "APKs, DEX, Class Files or Zip/Jar/War Archives";
            }
        });
    
        int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);
    
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            Configuration.lastDirectory = fc.getSelectedFile().getAbsolutePath();
            try {
                BytecodeViewer.viewer.setIcon(true);
                BytecodeViewer.openFiles(new File[]{fc.getSelectedFile()}, true);
                BytecodeViewer.viewer.setIcon(false);
            } catch (Exception e1) {
                new ExceptionUI(e1);
            }
        }
    }
    
    public void reloadResources()
    {
        JOptionPane pane = new JOptionPane("Are you sure you wish to reload the resources?");
        Object[] options = new String[]{"Yes", "No"};
        pane.setOptions(options);
        JDialog dialog = pane.createDialog(BytecodeViewer.viewer, "Bytecode Viewer - Reload Resources");
        dialog.setVisible(true);
        Object obj = pane.getValue();
        int result = -1;
        for (int k = 0; k < options.length; k++)
            if (options[k].equals(obj))
                result = k;
    
        if (result == 0) {
            LazyNameUtil.reset();
            ArrayList<File> reopen = new ArrayList<>();
        
            for (FileContainer container : BytecodeViewer.files) {
                File newFile = new File(container.file.getParent() + fs + container.name);
                if (!container.file.getAbsolutePath().equals(newFile.getAbsolutePath()) &&
                        (container.file.getAbsolutePath().endsWith(".apk") || container.file.getAbsolutePath().endsWith(".dex"))) //APKs & dex get renamed
                {
                    container.file.renameTo(newFile);
                    container.file = newFile;
                }
                reopen.add(container.file);
            }
        
            BytecodeViewer.files.clear();
        
            for (File f : reopen) {
                BytecodeViewer.openFiles(new File[]{f}, false);
            }
        
            //refresh panes
        }
    }
    
    public void compileOnNewThread()
    {
        Thread t = new Thread(() -> BytecodeViewer.compile(true));
        t.start();
    }
    
    public void runResources()
    {
        if (BytecodeViewer.getLoadedClasses().isEmpty()) {
            BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
            return;
        }
        
        new RunOptions().setVisible(true);
    }

    public void selectPythonC() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return true;
            }

            @Override
            public String getDescription() {
                return "Python (Or PyPy for speed) 2.7 Executable";
            }
        });
        fc.setFileHidingEnabled(false);
        fc.setAcceptAllFileFilterUsed(false);
        int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            try {
                Configuration.python = fc.getSelectedFile().getAbsolutePath();
            } catch (Exception e1) {
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
            }
    }

    public void selectJavac() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return true;
            }

            @Override
            public String getDescription() {
                return "Javac Executable (Requires JDK  'C:/programfiles/Java/JDK_xx/bin/javac.exe)";
            }
        });
        fc.setFileHidingEnabled(false);
        fc.setAcceptAllFileFilterUsed(false);
        int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            try {
                Configuration.javac = fc.getSelectedFile().getAbsolutePath();
            } catch (Exception e1) {
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
            }
    }

    public void selectJava() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return true;
            }

            @Override
            public String getDescription() {
                return "Java Executable (Inside Of JRE/JDK 'C:/programfiles/Java/JDK_xx/bin/java.exe')";
            }
        });
        fc.setFileHidingEnabled(false);
        fc.setAcceptAllFileFilterUsed(false);
        int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            try {
                Configuration.java = fc.getSelectedFile().getAbsolutePath();
            } catch (Exception e1) {
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
            }
    }

    public void selectPythonC3() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return true;
            }

            @Override
            public String getDescription() {
                return "Python (Or PyPy for speed) 3.x Executable";
            }
        });
        fc.setFileHidingEnabled(false);
        fc.setAcceptAllFileFilterUsed(false);
        int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            try {
                Configuration.python3 = fc.getSelectedFile().getAbsolutePath();
            } catch (Exception e1) {
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
            }
    }

    public void selectOpenalLibraryFolder() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Optional Library Folder";
            }
        });
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setFileHidingEnabled(false);
        fc.setAcceptAllFileFilterUsed(false);
        int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            try {
                Configuration.library = fc.getSelectedFile().getAbsolutePath();
            } catch (Exception e1) {
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
            }
    }
    
    public void selectJRERTLibrary() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return true;
            }

            @Override
            public String getDescription() {
                return "JRE RT Library";
            }
        });
        fc.setFileHidingEnabled(false);
        fc.setAcceptAllFileFilterUsed(false);
        int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            try {
                Configuration.rt = fc.getSelectedFile().getAbsolutePath();
            } catch (Exception e1) {
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
            }
    }
    
    public void askBeforeExiting()
    {
        JOptionPane pane = new JOptionPane(
                "Are you sure you want to exit?");
        Object[] options = new String[]{"Yes", "No"};
        pane.setOptions(options);
        JDialog dialog = pane.createDialog(BytecodeViewer.viewer,
                "Bytecode Viewer - Exit");
        dialog.setVisible(true);
        Object obj = pane.getValue();
        int result = -1;
        for (int k = 0; k < options.length; k++)
            if (options[k].equals(obj))
                result = k;
    
        if (result == 0) {
            Configuration.canExit = true;
            System.exit(0);
        }
    }
}
