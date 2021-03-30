package the.bytecode.club.bytecodeviewer.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import me.konloch.kontainer.io.DiskWriter;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.*;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.obfuscators.rename.RenameClasses;
import the.bytecode.club.bytecodeviewer.obfuscators.rename.RenameFields;
import the.bytecode.club.bytecodeviewer.obfuscators.rename.RenameMethods;
import the.bytecode.club.bytecodeviewer.plugin.PluginManager;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.*;
import the.bytecode.club.bytecodeviewer.util.*;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

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
public class MainViewerGUI extends JFrame implements FileChangeNotifier
{

    public static final long serialVersionUID = 1851409230530948543L;
    public JCheckBoxMenuItem debugHelpers = new JCheckBoxMenuItem("Debug Helpers");
    public JSplitPane sp1;
    public JSplitPane sp2;
    static ArrayList<VisibleComponent> rfComps = new ArrayList<VisibleComponent>();
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
    public final JMenuItem mntmNewWorkspace = new JMenuItem("New Workspace");
    public JMenu mnRecentFiles = new JMenu("Recent Files");
    public final JMenuItem mntmNewMenuItem = new JMenuItem("Decompile & Save All Classes..");
    public final JMenuItem mntmAbout = new JMenuItem("About");
    public final JSeparator separator_3 = new JSeparator();
    public final JMenu mnNewMenu_1 = new JMenu("Plugins");
    public final JMenuItem mntmStartExternalPlugin = new JMenuItem("Open Plugin..");
    public final JSeparator separator_4 = new JSeparator();
    public JMenu mnRecentPlugins = new JMenu("Recent Plugins");
    public final JSeparator separator_5 = new JSeparator();
    public final JMenuItem mntmStartZkmString = new JMenuItem("ZKM String Decrypter");
    public final JMenuItem mntmNewMenuItem_1 = new JMenuItem("Malicious Code Scanner");
    public final JMenuItem mntmNewMenuItem_2 = new JMenuItem("Allatori String Decrypter");
    public final JMenuItem mntmShowAllStrings = new JMenuItem("Show All Strings");
    public final JMenuItem mntmShowMainMethods = new JMenuItem("Show Main Methods");
    public final JMenuItem mntmNewMenuItem_3 = new JMenuItem("Save As Runnable Jar..");
    public JMenuBar menuBar = new JMenuBar();
    public final JMenuItem mntmReplaceStrings = new JMenuItem("Replace Strings");
    public final JMenuItem mntmStackFramesRemover = new JMenuItem("StackFrames Remover");
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
    public final JSeparator separator_2 = new JSeparator();
    public final JMenu mnNewMenu_6 = new JMenu("View");
    public final JMenu mnNewMenu_7 = new JMenu("Pane 1");
    public final JRadioButtonMenuItem panel1None = new JRadioButtonMenuItem("None");
    public final JRadioButtonMenuItem panel1Hexcode = new JRadioButtonMenuItem("Hexcode");
    public final JRadioButtonMenuItem panel1Bytecode = new JRadioButtonMenuItem("Bytecode");
    public final JRadioButtonMenuItem panel1Fern = new JRadioButtonMenuItem("Java");
    public final JRadioButtonMenuItem panel1CFR = new JRadioButtonMenuItem("Java");
    public final JRadioButtonMenuItem panel1Proc = new JRadioButtonMenuItem("Java");
    public final JMenuItem mntmNewMenuItem_12 = new JMenuItem("Decompile & Save Opened Class..");
    public WorkPane workPane = new WorkPane(this);
    public final JMenu mnSettings = new JMenu("Settings");
    public final JSeparator separator_6 = new JSeparator();
    public final JCheckBoxMenuItem refreshOnChange = new JCheckBoxMenuItem("Refresh On View Change");
    public AboutWindow aboutWindow = new AboutWindow();

    public FileNavigationPane cn = new FileNavigationPane(this);
    public SearchingPane s;

    public boolean isMaximized = false;

    public void removed(boolean busy)
    {
        if (busy)
        {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            for (Component c : this.getComponents())
                c.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            sp1.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            sp2.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            for (VisibleComponent c : rfComps)
            {
                c.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                if (c instanceof WorkPane)
                {
                    WorkPane w = (WorkPane) c;
                    for (Component c2 : w.tabs.getComponents())
                        c2.setCursor(Cursor
                                .getPredefinedCursor(Cursor.WAIT_CURSOR));
                }
            }
        }
        else
        {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            for (Component c : this.getComponents())
                c.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            sp1.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            sp2.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            for (VisibleComponent c : rfComps)
            {
                c.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                if (c instanceof WorkPane)
                {
                    WorkPane w = (WorkPane) c;
                    for (Component c2 : w.tabs.getComponents())
                        c2.setCursor(Cursor
                                .getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        }
    }

    public class Test implements KeyEventDispatcher
    {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e)
        {
            BytecodeViewer.checkHotKey(e);
            return false;
        }
    }

    public final JMenuItem mntmSaveAsDEX = new JMenuItem("Save As DEX..");
    public final JMenuItem mntmSaveAsAPK = new JMenuItem("Save As APK..");
    public final JMenuItem mntmCodeSequenceDiagram = new JMenuItem("Code Sequence Diagram");
    public final JSeparator separator_7 = new JSeparator();
    public final JSeparator separator_8 = new JSeparator();
    public final JRadioButtonMenuItem panel1Smali = new JRadioButtonMenuItem("Smali/DEX");
    public final JCheckBoxMenuItem compileOnSave = new JCheckBoxMenuItem("Compile On Save");
    public final JCheckBoxMenuItem showFileInTabTitle = new JCheckBoxMenuItem("Show File In Tab Title");
    public final JCheckBoxMenuItem forcePureAsciiAsText = new JCheckBoxMenuItem("Force Pure Ascii As Text");
    public final JMenuItem compileButton = new JMenuItem("Compile");
    public final JCheckBoxMenuItem autoCompileOnRefresh = new JCheckBoxMenuItem("Compile On Refresh");
    public final JMenuItem mntmSetPythonDirectory = new JMenuItem("Set Python 2.7 Executable");
    public final JSeparator separator_13 = new JSeparator();
    public final JRadioButtonMenuItem panel1Krakatau = new JRadioButtonMenuItem("Java");
    public final JRadioButtonMenuItem panel1KrakatauBytecode = new JRadioButtonMenuItem("Bytecode");
    public final JMenuItem mntmSetJreRt = new JMenuItem("Set JRE RT Library");
    public final JMenuItem mntmZstringarrayDecrypter = new JMenuItem("ZStringArray Decrypter");
    public final JSeparator separator_15 = new JSeparator();
    public final JMenuItem mntmRun = new JMenuItem("Run");
    public final JSeparator separator_18 = new JSeparator();
    public final JCheckBoxMenuItem decodeAPKResources = new JCheckBoxMenuItem("Decode APK Resources");
    public final JCheckBoxMenuItem synchronizedViewing = new JCheckBoxMenuItem("Synchronized Viewing");
    public final JCheckBoxMenuItem showClassMethods = new JCheckBoxMenuItem("Show Class Methods");
    public final JMenu mnProcyon = new JMenu("Procyon");
    public final JCheckBoxMenuItem panel1Proc_E = new JCheckBoxMenuItem("Editable");
    public final JSeparator separator_14 = new JSeparator();
    public final JMenu mnCfr = new JMenu("CFR");
    public final JSeparator separator_19 = new JSeparator();
    public final JCheckBoxMenuItem panel1CFR_E = new JCheckBoxMenuItem("Editable");
    public final JMenu mnFernflower = new JMenu("FernFlower");
    public final JSeparator separator_20 = new JSeparator();
    public final JCheckBoxMenuItem panel1Fern_E = new JCheckBoxMenuItem("Editable");
    public final JMenu mnKrakatau = new JMenu("Krakatau");
    public final JSeparator separator_21 = new JSeparator();
    public final JCheckBoxMenuItem panel1Krakatau_E = new JCheckBoxMenuItem("Editable");
    public final JMenu mnSmalidex = new JMenu("Smali/DEX");
    public final JSeparator separator_22 = new JSeparator();
    public final JCheckBoxMenuItem panel1Smali_E = new JCheckBoxMenuItem("Editable");
    public final JMenu mnPane = new JMenu("Pane 2");
    public final JRadioButtonMenuItem panel2None = new JRadioButtonMenuItem("None");
    public final JSeparator separator_9 = new JSeparator();
    public final JMenu menu_1 = new JMenu("Procyon");
    public final JRadioButtonMenuItem panel2Proc = new JRadioButtonMenuItem("Java");
    public final JSeparator separator_10 = new JSeparator();
    public final JCheckBoxMenuItem panel2Proc_E = new JCheckBoxMenuItem("Editable");
    public final JMenu menu_2 = new JMenu("CFR");
    public final JRadioButtonMenuItem panel2CFR = new JRadioButtonMenuItem("Java");
    public final JSeparator separator_11 = new JSeparator();
    public final JCheckBoxMenuItem panel2CFR_E = new JCheckBoxMenuItem("Editable");
    public final JMenu menu_3 = new JMenu("FernFlower");
    public final JRadioButtonMenuItem panel2Fern = new JRadioButtonMenuItem("Java");
    public final JSeparator separator_12 = new JSeparator();
    public final JCheckBoxMenuItem panel2Fern_E = new JCheckBoxMenuItem("Editable");
    public final JMenu menu_4 = new JMenu("Krakatau");
    public final JRadioButtonMenuItem panel2Krakatau = new JRadioButtonMenuItem("Java");
    public final JRadioButtonMenuItem panel2KrakatauBytecode = new JRadioButtonMenuItem("Bytecode");
    public final JSeparator separator_16 = new JSeparator();
    public final JCheckBoxMenuItem panel2Krakatau_E = new JCheckBoxMenuItem("Editable");
    public final JSeparator separator_17 = new JSeparator();
    public final JMenu menu_5 = new JMenu("Smali/DEX");
    public final JRadioButtonMenuItem panel2Smali = new JRadioButtonMenuItem("Smali/DEX");
    public final JSeparator separator_23 = new JSeparator();
    public final JCheckBoxMenuItem panel2Smali_E = new JCheckBoxMenuItem("Editable");
    public final JSeparator separator_24 = new JSeparator();
    public final JRadioButtonMenuItem panel2Bytecode = new JRadioButtonMenuItem("Bytecode");
    public final JRadioButtonMenuItem panel2Hexcode = new JRadioButtonMenuItem("Hexcode");
    public final JMenu mnPane_1 = new JMenu("Pane 3");
    public final JRadioButtonMenuItem panel3None = new JRadioButtonMenuItem("None");
    public final JSeparator separator_25 = new JSeparator();
    public final JMenu menu_7 = new JMenu("Procyon");
    public final JRadioButtonMenuItem panel3Proc = new JRadioButtonMenuItem("Java");
    public final JSeparator separator_26 = new JSeparator();
    public final JCheckBoxMenuItem panel3Proc_E = new JCheckBoxMenuItem("Editable");
    public final JMenu menu_8 = new JMenu("CFR");
    public final JRadioButtonMenuItem panel3CFR = new JRadioButtonMenuItem("Java");
    public final JSeparator separator_27 = new JSeparator();
    public final JCheckBoxMenuItem panel3CFR_E = new JCheckBoxMenuItem("Editable");
    public final JMenu menu_9 = new JMenu("FernFlower");
    public final JRadioButtonMenuItem panel3Fern = new JRadioButtonMenuItem("Java");
    public final JSeparator separator_28 = new JSeparator();
    public final JCheckBoxMenuItem panel3Fern_E = new JCheckBoxMenuItem("Editable");
    public final JMenu menu_10 = new JMenu("Krakatau");
    public final JRadioButtonMenuItem panel3Krakatau = new JRadioButtonMenuItem("Java");
    public final JRadioButtonMenuItem panel3KrakatauBytecode = new JRadioButtonMenuItem("Bytecode");
    public final JSeparator separator_29 = new JSeparator();
    public final JCheckBoxMenuItem panel3Krakatau_E = new JCheckBoxMenuItem("Editable");
    public final JSeparator separator_30 = new JSeparator();
    public final JMenu menu_11 = new JMenu("Smali/DEX");
    public final JRadioButtonMenuItem panel3Smali = new JRadioButtonMenuItem("Smali/DEX");
    public final JSeparator separator_31 = new JSeparator();
    public final JCheckBoxMenuItem panel3Smali_E = new JCheckBoxMenuItem("Editable");
    public final JSeparator separator_32 = new JSeparator();
    public final JRadioButtonMenuItem panel3Bytecode = new JRadioButtonMenuItem("Bytecode");
    public final JRadioButtonMenuItem panel3Hexcode = new JRadioButtonMenuItem("Hexcode");

    public synchronized void setIcon(final boolean busy)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                if (busy)
                {
                    for (int i = 0; i < 10; i++)
                    {
                        if (waitIcons[i].getIcon() == null)
                        {
                            try
                            {
                                waitIcons[i].setIcon(Resources.busyIcon);
                            }
                            catch (NullPointerException e)
                            {
                                waitIcons[i].setIcon(Resources.busyB64Icon);
                            }
                            waitIcons[i].updateUI();
                            break;
                        }
                    }
                }
                else
                {
                    for (int i = 0; i < 10; i++)
                    {
                        if (waitIcons[i].getIcon() != null)
                        {
                            waitIcons[i].setIcon(null);
                            waitIcons[i].updateUI();
                            break;
                        }
                    }
                }
            }
        });
    }

    public final ButtonGroup panelGroup1 = new ButtonGroup();
    public final ButtonGroup panelGroup2 = new ButtonGroup();
    public final ButtonGroup panelGroup3 = new ButtonGroup();
    private final JMenuItem mntmSetOpitonalLibrary = new JMenuItem("Set Optional Library Folder");
    private final JMenu mnJdgui = new JMenu("JD-GUI");
    public final JRadioButtonMenuItem panel3JDGUI = new JRadioButtonMenuItem("Java");
    private final JSeparator separator_33 = new JSeparator();
    public final JCheckBoxMenuItem panel3JDGUI_E = new JCheckBoxMenuItem("Editable");
    private final JMenu menu = new JMenu("JD-GUI");
    public final JRadioButtonMenuItem panel2JDGUI = new JRadioButtonMenuItem("Java");
    private final JSeparator separator_34 = new JSeparator();
    public final JCheckBoxMenuItem panel2JDGUI_E = new JCheckBoxMenuItem("Editable");
    private final JMenu menu_6 = new JMenu("JD-GUI");
    public final JRadioButtonMenuItem panel1JDGUI = new JRadioButtonMenuItem("Java");
    private final JSeparator separator_35 = new JSeparator();
    public final JCheckBoxMenuItem panel1JDGUI_E = new JCheckBoxMenuItem("Editable");

    private final JMenu jadx1 = new JMenu("JADX");
    public final JRadioButtonMenuItem jadxJ1 = new JRadioButtonMenuItem("Java");
    public final JCheckBoxMenuItem jadxE1 = new JCheckBoxMenuItem("Editable");
    private final JMenu jadx2 = new JMenu("JADX");
    public final JRadioButtonMenuItem jadxJ2 = new JRadioButtonMenuItem("Java");
    public final JCheckBoxMenuItem jadxE2 = new JCheckBoxMenuItem("Editable");
    private final JMenu jadx3 = new JMenu("JADX");
    public final JRadioButtonMenuItem jadxJ3 = new JRadioButtonMenuItem("Java");
    public final JCheckBoxMenuItem jadxE3 = new JCheckBoxMenuItem("Editable");

    public final JRadioButtonMenuItem asmText1 = new JRadioButtonMenuItem("ASM Textify");
    public final JRadioButtonMenuItem asmText2 = new JRadioButtonMenuItem("ASM Textify");
    public final JRadioButtonMenuItem asmText3 = new JRadioButtonMenuItem("ASM Textify");

    private final JMenu mnFontSize = new JMenu("Font Size");
    private final JMenu visualSettings = new JMenu("Visual Settings");
    public final JSpinner fontSpinner = new JSpinner();
    private final JSeparator separator_36 = new JSeparator();
    private final JCheckBoxMenuItem chckbxmntmDeleteForeignOutdatedLibs = new JCheckBoxMenuItem("Delete Foreign/Outdated Libs");
    private final JSeparator separator_37 = new JSeparator();
    private final JSeparator separator_38 = new JSeparator();
    private final JMenu mnApkConversion = new JMenu("APK Conversion");
    public final ButtonGroup apkConversionGroup = new ButtonGroup();
    public final JRadioButtonMenuItem apkConversionDex = new JRadioButtonMenuItem("Dex2Jar");
    public final JRadioButtonMenuItem apkConversionEnjarify = new JRadioButtonMenuItem("Enjarify");
    private final JMenuItem mntmSetPythonx = new JMenuItem("Set Python 3.X Executable");
    private final JMenuItem mntmReloadResources = new JMenuItem("Reload Resources");
    private final JSeparator separator_39 = new JSeparator();
    private final JSeparator separator_40 = new JSeparator();
    private final JMenuItem mntmSetJavacExecutable = new JMenuItem("Set Javac Executable");

    public void calledAfterLoad()
    {
        chckbxmntmDeleteForeignOutdatedLibs.setSelected(BytecodeViewer.deleteForeignLibraries);
    }

    public MainViewerGUI()
    {
        mnNewMenu_5.setVisible(false);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new Test());
        this.addWindowStateListener(new WindowAdapter()
        {
            @Override
            public void windowStateChanged(WindowEvent evt)
            {
                int oldState = evt.getOldState();
                int newState = evt.getNewState();

                if ((oldState & Frame.ICONIFIED) == 0 && (newState & Frame.ICONIFIED) != 0)
                {
                    //System.out.println("Frame was iconized");
                }
                else if ((oldState & Frame.ICONIFIED) != 0 && (newState & Frame.ICONIFIED) == 0)
                {
                    //System.out.println("Frame was deiconized");
                }

                if ((oldState & Frame.MAXIMIZED_BOTH) == 0 && (newState & Frame.MAXIMIZED_BOTH) != 0)
                {
                    isMaximized = true;
                }
                else if ((oldState & Frame.MAXIMIZED_BOTH) != 0 && (newState & Frame.MAXIMIZED_BOTH) == 0)
                {
                    isMaximized = false;
                }
            }
        });
        this.setIconImages(Resources.iconList);
        ActionListener listener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                if (refreshOnChange.isSelected())
                {
                    if (workPane.getCurrentViewer() == null)
                        return;

                    workPane.refreshClass.doClick();
                }
            }

        };

        panel1None.addActionListener(listener);
        panel1Hexcode.addActionListener(listener);
        obfuscatorGroup.add(strongObf);
        obfuscatorGroup.add(lightObf);
        obfuscatorGroup.setSelected(strongObf.getModel(), true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                BytecodeViewer.canExit = true;
                System.exit(0);
            }
        });
        // procyon
        /* none */

        setJMenuBar(menuBar);

        JMenu mnNewMenu = new JMenu("File");
        menuBar.add(mnNewMenu);

        mntmNewWorkspace.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                BytecodeViewer.resetWorkSpace(true);
            }
        });

        JMenuItem mntmLoadJar = new JMenuItem("Add..");
        mntmLoadJar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {

                final JFileChooser fc = new JFileChooser();

                try
                {
                    File f = new File(BytecodeViewer.lastDirectory);
                    if (f.exists())
                        fc.setSelectedFile(f);
                }
                catch (Exception e2)
                {

                }

                fc.setDialogTitle("Select File or Folder to open in BCV");
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fc.setAcceptAllFileFilterUsed(true);
                fc.setFileFilter(new FileFilter()
                {
                    @Override
                    public boolean accept(File f)
                    {
                        if (f.isDirectory())
                            return true;

                        String extension = MiscUtils.extension(f.getAbsolutePath());
                        if (extension != null)
                            if (extension.equals("jar") || extension.equals("zip")
                                    || extension.equals("class") || extension.equals("apk")
                                    || extension.equals("dex") || extension.equals("war") || extension.equals("jsp"))
                                return true;

                        return false;
                    }

                    @Override
                    public String getDescription()
                    {
                        return "APKs, DEX, Class Files or Zip/Jar/War Archives";
                    }
                });

                int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);

                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    BytecodeViewer.lastDirectory = fc.getSelectedFile().getAbsolutePath();
                    try
                    {
                        BytecodeViewer.viewer.setIcon(true);
                        BytecodeViewer.openFiles(new File[]{fc.getSelectedFile()}, true);
                        BytecodeViewer.viewer.setIcon(false);
                    }
                    catch (Exception e1)
                    {
                        new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
                    }
                }
            }
        });

        mnNewMenu.add(mntmLoadJar);

        mnNewMenu.add(separator_40);

        mnNewMenu.add(mntmNewWorkspace);

        JMenuItem mntmSave = new JMenuItem("Save As Zip..");
        mntmSave.setActionCommand("");
        mntmSave.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                if (BytecodeViewer.getLoadedClasses().isEmpty())
                {
                    BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                    return;
                }
                Thread t = new Thread()
                {
                    public void run()
                    {
                        if (compileOnSave.isSelected() && !BytecodeViewer.compile(false))
                            return;
                        JFileChooser fc = new JFileChooser();
                        fc.setFileFilter(new FileFilter()
                        {
                            @Override
                            public boolean accept(File f)
                            {
                                return f.isDirectory() || MiscUtils.extension(f.getAbsolutePath()).equals("zip");
                            }

                            @Override
                            public String getDescription()
                            {
                                return "Zip Archives";
                            }
                        });
                        fc.setFileHidingEnabled(false);
                        fc.setAcceptAllFileFilterUsed(false);
                        int returnVal = fc.showSaveDialog(MainViewerGUI.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION)
                        {
                            File file = fc.getSelectedFile();
                            if (!file.getAbsolutePath().endsWith(".zip"))
                                file = new File(file.getAbsolutePath() + ".zip");

                            if (file.exists())
                            {
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

                                if (result == 0)
                                {
                                    file.delete();
                                }
                                else
                                {
                                    return;
                                }
                            }

                            final File file2 = file;

                            BytecodeViewer.viewer.setIcon(true);
                            Thread t = new Thread()
                            {
                                @Override
                                public void run()
                                {
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
            }
        });

        mnNewMenu.add(separator_39);
        mntmReloadResources.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
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

                if (result == 0)
                {
                    LazyNameUtil.reset();
                    ArrayList<File> reopen = new ArrayList<File>();

                    for (FileContainer container : BytecodeViewer.files)
                    {
                        File newFile = new File(container.file.getParent() + BytecodeViewer.fs + container.name);
                        if (!container.file.getAbsolutePath().equals(newFile.getAbsolutePath()) &&
                                (container.file.getAbsolutePath().endsWith(".apk") || container.file.getAbsolutePath().endsWith(".dex"))) //APKs & dex get renamed
                        {
                            container.file.renameTo(newFile);
                            container.file = newFile;
                        }
                        reopen.add(container.file);
                    }

                    BytecodeViewer.files.clear();

                    for (File f : reopen)
                    {
                        BytecodeViewer.openFiles(new File[]{f}, false);
                    }

                    //refresh panes
                }
            }
        });

        mnNewMenu.add(mntmReloadResources);

        mnNewMenu.add(separator_3);
        mntmNewMenuItem_3.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (BytecodeViewer.getLoadedClasses().isEmpty())
                {
                    BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                    return;
                }
                Thread t = new Thread()
                {
                    public void run()
                    {
                        if (compileOnSave.isSelected() && !BytecodeViewer.compile(false))
                            return;
                        JFileChooser fc = new JFileChooser();
                        fc.setFileFilter(new FileFilter()
                        {
                            @Override
                            public boolean accept(File f)
                            {
                                return f.isDirectory() || MiscUtils.extension(f.getAbsolutePath()).equals("zip");
                            }

                            @Override
                            public String getDescription()
                            {
                                return "Zip Archives";
                            }
                        });
                        fc.setFileHidingEnabled(false);
                        fc.setAcceptAllFileFilterUsed(false);
                        int returnVal = fc.showSaveDialog(MainViewerGUI.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION)
                        {
                            File file = fc.getSelectedFile();
                            String path = file.getAbsolutePath();
                            if (!path.endsWith(".jar"))
                                path = path + ".jar";

                            if (new File(path).exists())
                            {
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

                                if (result == 0)
                                {
                                    file.delete();
                                }
                                else
                                {
                                    return;
                                }
                            }

                            new ExportJar(path).setVisible(true);
                        }
                    }
                };
                t.start();
            }
        });
        compileButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                Thread t = new Thread()
                {
                    public void run()
                    {
                        BytecodeViewer.compile(true);
                    }
                };
                t.start();
            }
        });
        mntmRun.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (BytecodeViewer.getLoadedClasses().isEmpty())
                {
                    BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                    return;
                }
                new RunOptions().setVisible(true);
            }
        });

        mnNewMenu.add(mntmRun);

        mnNewMenu.add(compileButton);

        mnNewMenu.add(separator_18);

        mnNewMenu.add(mntmNewMenuItem_3);
        mntmSaveAsDEX.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                if (BytecodeViewer.getLoadedClasses().isEmpty())
                {
                    BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                    return;
                }

                Thread t = new Thread()
                {
                    public void run()
                    {
                        if (compileOnSave.isSelected() && !BytecodeViewer.compile(false))
                            return;
                        JFileChooser fc = new JFileChooser();
                        fc.setFileFilter(new FileFilter()
                        {
                            @Override
                            public boolean accept(File f)
                            {
                                return f.isDirectory() || MiscUtils.extension(f.getAbsolutePath()).equals("dex");
                            }

                            @Override
                            public String getDescription()
                            {
                                return "Android DEX Files";
                            }
                        });
                        fc.setFileHidingEnabled(false);
                        fc.setAcceptAllFileFilterUsed(false);
                        int returnVal = fc.showSaveDialog(MainViewerGUI.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION)
                        {
                            final File file = fc.getSelectedFile();
                            String output = file.getAbsolutePath();
                            if (!output.endsWith(".dex"))
                                output = output + ".dex";

                            final File file2 = new File(output);

                            if (file2.exists())
                            {
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

                                if (result == 0)
                                {
                                    file.delete();
                                }
                                else
                                {
                                    return;
                                }
                            }

                            Thread t = new Thread()
                            {
                                @Override
                                public void run()
                                {
                                    BytecodeViewer.viewer.setIcon(true);
                                    final String input = BytecodeViewer.tempDirectory + BytecodeViewer.fs + BytecodeViewer.getRandomizedName() + ".jar";
                                    JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), input);

                                    Thread t = new Thread()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            Dex2Jar.saveAsDex(new File(input), file2);

                                            BytecodeViewer.viewer.setIcon(false);
                                        }
                                    };
                                    t.start();
                                }
                            };
                            t.start();
                        }
                    }
                };
                t.start();
            }
        });
        mntmSaveAsAPK.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                if (BytecodeViewer.getLoadedClasses().isEmpty())
                {
                    BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                    return;
                }

                //if theres only one file in the container don't bother asking
                List<FileContainer> containers = BytecodeViewer.getFiles();
                List<FileContainer> validContainers = new ArrayList<>();
                List<String> validContainersNames = new ArrayList<>();
                FileContainer container = null;

                for(FileContainer fileContainer : containers)
                {
                    if(fileContainer.APKToolContents != null && fileContainer.APKToolContents.exists())
                    {
                        validContainersNames.add(fileContainer.name);
                        validContainers.add(fileContainer);
                    }
                }

                if(!validContainers.isEmpty())
                {
                    container = validContainers.get(0);

                    if(validContainers.size() >= 2)
                    {
                        JOptionPane pane = new JOptionPane("Which file would you like to export as an APK?");
                        Object[] options = validContainersNames.toArray(new String[0]);

                        pane.setOptions(options);
                        JDialog dialog = pane.createDialog(BytecodeViewer.viewer, "Bytecode Viewer - Select APK");
                        dialog.setVisible(true);
                        Object obj = pane.getValue();
                        int result = -1;
                        for (int k = 0; k < options.length; k++)
                            if (options[k].equals(obj))
                                result = k;

                        container = containers.get(result);
                    }
                }
                else
                {
                    BytecodeViewer.showMessage("You can only export as APK from a valid APK file. Make sure Settings>Decode Resources is ticked on.\n\nTip: Try exporting as DEX, it doesn't rely on decoded APK resources");
                    return;
                }

                final FileContainer finalContainer = container;

                Thread t = new Thread()
                {
                    public void run()
                    {
                        if (compileOnSave.isSelected() && !BytecodeViewer.compile(false))
                            return;
                        JFileChooser fc = new JFileChooser();
                        fc.setFileFilter(new FileFilter()
                        {
                            @Override
                            public boolean accept(File f)
                            {
                                return f.isDirectory() || MiscUtils.extension(f.getAbsolutePath()).equals("apk");
                            }

                            @Override
                            public String getDescription()
                            {
                                return "Android APK";
                            }
                        });
                        fc.setFileHidingEnabled(false);
                        fc.setAcceptAllFileFilterUsed(false);
                        int returnVal = fc.showSaveDialog(MainViewerGUI.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION)
                        {
                            final File file = fc.getSelectedFile();
                            String output = file.getAbsolutePath();
                            if (!output.endsWith(".apk"))
                                output = output + ".apk";

                            final File file2 = new File(output);

                            if (file2.exists())
                            {
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

                                if (result == 0)
                                {
                                    file.delete();
                                }
                                else
                                {
                                    return;
                                }
                            }

                            Thread t = new Thread()
                            {
                                @Override
                                public void run()
                                {
                                    BytecodeViewer.viewer.setIcon(true);
                                    final String input = BytecodeViewer.tempDirectory + BytecodeViewer.fs + BytecodeViewer.getRandomizedName() + ".jar";
                                    JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), input);

                                    Thread t = new Thread()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            APKTool.buildAPK(new File(input), file2, finalContainer);

                                            BytecodeViewer.viewer.setIcon(false);
                                        }
                                    };
                                    t.start();
                                }
                            };
                            t.start();
                        }
                    }
                };
                t.start();
            }
        });

        //mnNewMenu.add(mntmSaveAsAPK);
        mnNewMenu.add(mntmSaveAsDEX);
        mnNewMenu.add(mntmSave);
        mntmNewMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                if (BytecodeViewer.getLoadedClasses().isEmpty())
                {
                    BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                    return;
                }

                Thread t = new Thread()
                {
                    public void run()
                    {
                        if (compileOnSave.isSelected() && !BytecodeViewer.compile(false))
                            return;
                        JFileChooser fc = new JFileChooser();
                        fc.setFileFilter(new FileFilter()
                        {
                            @Override
                            public boolean accept(File f)
                            {
                                return f.isDirectory() || MiscUtils.extension(f.getAbsolutePath()).equals("zip");
                            }

                            @Override
                            public String getDescription()
                            {
                                return "Zip Archives";
                            }
                        });
                        fc.setFileHidingEnabled(false);
                        fc.setAcceptAllFileFilterUsed(false);
                        int returnVal = fc.showSaveDialog(MainViewerGUI.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION)
                        {
                            File file = fc.getSelectedFile();
                            if (!file.getAbsolutePath().endsWith(".zip"))
                                file = new File(file.getAbsolutePath() + ".zip");

                            if (file.exists())
                            {
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

                                if (result == 0)
                                {
                                    file.delete();
                                }
                                else
                                {
                                    return;
                                }
                            }

                            final File javaSucks = file;

                            final String path = MiscUtils.append(file, ".zip");    // cheap hax cause
                            // string is final

                            JOptionPane pane = new JOptionPane(
                                    "What decompiler will you use?");
                            Object[] options = new String[]{"All", "Procyon", "CFR",
                                    "Fernflower", "Krakatau", "Cancel"};
                            pane.setOptions(options);
                            JDialog dialog = pane.createDialog(BytecodeViewer.viewer,
                                    "Bytecode Viewer - Select Decompiler");
                            dialog.setVisible(true);
                            Object obj = pane.getValue();
                            int result = -1;
                            for (int k = 0; k < options.length; k++)
                                if (options[k].equals(obj))
                                    result = k;

                            BytecodeViewer.viewer.setIcon(true);

                            File tempZip = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp_" + BytecodeViewer.getRandomizedName() + ".jar");
                            if (tempZip.exists())
                                tempZip.delete();

                            JarUtils.saveAsJarClassesOnly(BytecodeViewer.getLoadedClasses(), tempZip.getAbsolutePath());

                            if (result == 0)
                            {
                                Thread t = new Thread()
                                {
                                    @Override
                                    public void run()
                                    {
                                        try
                                        {
                                            Decompiler.procyon.decompileToZip(tempZip.getAbsolutePath(), MiscUtils.append(javaSucks, "-proycon.zip"));
                                            BytecodeViewer.viewer.setIcon(false);
                                        }
                                        catch (Exception e)
                                        {
                                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                                        }
                                    }
                                };
                                t.start();
                                Thread t2 = new Thread()
                                {
                                    @Override
                                    public void run()
                                    {
                                        try
                                        {
                                            BytecodeViewer.viewer.setIcon(true);
                                            Decompiler.cfr.decompileToZip(tempZip.getAbsolutePath(), MiscUtils.append(javaSucks, "-CFR.zip"));
                                            BytecodeViewer.viewer.setIcon(false);
                                        }
                                        catch (Exception e)
                                        {
                                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                                        }
                                    }
                                };
                                t2.start();
                                Thread t3 = new Thread()
                                {
                                    @Override
                                    public void run()
                                    {
                                        try
                                        {
                                            BytecodeViewer.viewer.setIcon(true);
                                            Decompiler.fernflower.decompileToZip(tempZip.getAbsolutePath(), MiscUtils.append(javaSucks, "-fernflower.zip"));
                                            BytecodeViewer.viewer.setIcon(false);
                                        }
                                        catch (Exception e)
                                        {
                                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                                        }
                                    }
                                };
                                t3.start();
                                Thread t4 = new Thread()
                                {
                                    @Override
                                    public void run()
                                    {
                                        try
                                        {
                                            BytecodeViewer.viewer.setIcon(true);
                                            Decompiler.krakatau.decompileToZip(tempZip.getAbsolutePath(), MiscUtils.append(javaSucks, "-kraktau.zip"));
                                            BytecodeViewer.viewer.setIcon(false);
                                        }
                                        catch (Exception e)
                                        {
                                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                                        }
                                    }
                                };
                                t4.start();
                            }
                            if (result == 1)
                            {
                                Thread t = new Thread()
                                {
                                    @Override
                                    public void run()
                                    {
                                        try
                                        {
                                            Decompiler.procyon.decompileToZip(tempZip.getAbsolutePath(), path);
                                            BytecodeViewer.viewer.setIcon(false);
                                        }
                                        catch (Exception e)
                                        {
                                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                                        }
                                    }
                                };
                                t.start();
                            }
                            if (result == 2)
                            {
                                Thread t = new Thread()
                                {
                                    @Override
                                    public void run()
                                    {
                                        try
                                        {
                                            Decompiler.cfr.decompileToZip(tempZip.getAbsolutePath(), path);
                                            BytecodeViewer.viewer.setIcon(false);
                                        }
                                        catch (Exception e)
                                        {
                                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                                        }
                                    }
                                };
                                t.start();
                            }
                            if (result == 3)
                            {
                                Thread t = new Thread()
                                {
                                    @Override
                                    public void run()
                                    {
                                        try
                                        {
                                            Decompiler.fernflower.decompileToZip(tempZip.getAbsolutePath(), path);
                                            BytecodeViewer.viewer.setIcon(false);
                                        }
                                        catch (Exception e)
                                        {
                                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                                        }
                                    }
                                };
                                t.start();
                            }

                            if (result == 4)
                            {
                                Thread t = new Thread()
                                {
                                    @Override
                                    public void run()
                                    {
                                        try
                                        {
                                            Decompiler.krakatau.decompileToZip(tempZip.getAbsolutePath(), path);
                                            BytecodeViewer.viewer.setIcon(false);
                                        }
                                        catch (Exception e)
                                        {
                                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                                        }
                                    }
                                };
                                t.start();
                            }

                            if (result == 5)
                            {
                                BytecodeViewer.viewer.setIcon(false);
                            }
                        }
                    }
                };
                t.start();
            }
        });
        mntmNewMenuItem_12.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                if (workPane.getCurrentViewer() == null)
                {
                    BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                    return;
                }

                Thread t = new Thread()
                {
                    public void run()
                    {
                        if (compileOnSave.isSelected() && !BytecodeViewer.compile(false))
                            return;

                        final String s = workPane.getCurrentViewer().cn.name;

                        if (s == null)
                            return;

                        JFileChooser fc = new JFileChooser();
                        fc.setFileFilter(new FileFilter()
                        {
                            @Override
                            public boolean accept(File f)
                            {
                                return f.isDirectory() || MiscUtils.extension(f.getAbsolutePath()).equals("java");
                            }

                            @Override
                            public String getDescription()
                            {
                                return "Java Source Files";
                            }
                        });
                        fc.setFileHidingEnabled(false);
                        fc.setAcceptAllFileFilterUsed(false);
                        int returnVal = fc.showSaveDialog(MainViewerGUI.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION)
                        {
                            File file = fc.getSelectedFile();

                            BytecodeViewer.viewer.setIcon(true);
                            final String path = MiscUtils.append(file, ".java");    // cheap hax cause
                            // string is final

                            if (new File(path).exists())
                            {
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

                                if (result == 0)
                                {
                                    file.delete();
                                }
                                else
                                {
                                    return;
                                }
                            }

                            JOptionPane pane = new JOptionPane(
                                    "What decompiler will you use?");
                            Object[] options = new String[]{"All", "Procyon", "CFR",
                                    "Fernflower", "Krakatau", "Cancel"};
                            pane.setOptions(options);
                            JDialog dialog = pane.createDialog(BytecodeViewer.viewer,
                                    "Bytecode Viewer - Select Decompiler");
                            dialog.setVisible(true);
                            Object obj = pane.getValue();
                            int result = -1;
                            for (int k = 0; k < options.length; k++)
                                if (options[k].equals(obj))
                                    result = k;

                            if (result == 0)
                            {
                                Thread t = new Thread()
                                {
                                    @Override
                                    public void run()
                                    {
                                        try
                                        {
                                            ClassNode cn = BytecodeViewer.getClassNode(s);
                                            final ClassWriter cw = new ClassWriter(0);
                                            try
                                            {
                                                cn.accept(cw);
                                            }
                                            catch (Exception e)
                                            {
                                                e.printStackTrace();
                                                try
                                                {
                                                    Thread.sleep(200);
                                                    cn.accept(cw);
                                                }
                                                catch (InterruptedException e1)
                                                {
                                                }
                                            }

                                            try
                                            {
                                                DiskWriter.replaceFile(MiscUtils.append(file, "-proycon.java"), Decompiler.procyon.decompileClassNode(cn, cw.toByteArray()), false);
                                            }
                                            catch (Exception e)
                                            {
                                                e.printStackTrace();
                                            }

                                            try
                                            {
                                                DiskWriter.replaceFile(MiscUtils.append(file, "-CFR.java"), Decompiler.cfr.decompileClassNode(cn, cw.toByteArray()), false);
                                            }
                                            catch (Exception e)
                                            {
                                                e.printStackTrace();
                                            }

                                            try
                                            {
                                                DiskWriter.replaceFile(MiscUtils.append(file, "-fernflower.java"), Decompiler.fernflower.decompileClassNode(cn, cw.toByteArray()), false);
                                            }
                                            catch (Exception e)
                                            {
                                                e.printStackTrace();
                                            }

                                            try
                                            {
                                                DiskWriter.replaceFile(MiscUtils.append(file, "-kraktau.java"), Decompiler.krakatau.decompileClassNode(cn, cw.toByteArray()), false);
                                            }
                                            catch (Exception e)
                                            {
                                                e.printStackTrace();
                                            }

                                            BytecodeViewer.viewer.setIcon(false);
                                        }
                                        catch (Exception e)
                                        {
                                            BytecodeViewer.viewer.setIcon(false);
                                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                                        }
                                    }
                                };
                                t.start();
                            }
                            if (result == 1)
                            {
                                Thread t = new Thread()
                                {
                                    @Override
                                    public void run()
                                    {
                                        try
                                        {
                                            ClassNode cn = BytecodeViewer.getClassNode(s);
                                            final ClassWriter cw = new ClassWriter(0);
                                            try
                                            {
                                                cn.accept(cw);
                                            }
                                            catch (Exception e)
                                            {
                                                e.printStackTrace();
                                                try
                                                {
                                                    Thread.sleep(200);
                                                    cn.accept(cw);
                                                }
                                                catch (InterruptedException e1)
                                                {
                                                }
                                            }
                                            String contents = Decompiler.procyon.decompileClassNode(cn, cw.toByteArray());
                                            DiskWriter.replaceFile(path, contents, false);
                                            BytecodeViewer.viewer.setIcon(false);
                                        }
                                        catch (Exception e)
                                        {
                                            BytecodeViewer.viewer.setIcon(false);
                                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(
                                                    e);
                                        }
                                    }
                                };
                                t.start();
                            }
                            if (result == 2)
                            {
                                Thread t = new Thread()
                                {
                                    @Override
                                    public void run()
                                    {
                                        try
                                        {
                                            ClassNode cn = BytecodeViewer.getClassNode(s);
                                            final ClassWriter cw = new ClassWriter(0);
                                            try
                                            {
                                                cn.accept(cw);
                                            }
                                            catch (Exception e)
                                            {
                                                e.printStackTrace();
                                                try
                                                {
                                                    Thread.sleep(200);
                                                    cn.accept(cw);
                                                }
                                                catch (InterruptedException e1)
                                                {
                                                }
                                            }
                                            String contents = Decompiler.cfr.decompileClassNode(cn, cw.toByteArray());
                                            DiskWriter.replaceFile(path, contents, false);
                                            BytecodeViewer.viewer.setIcon(false);
                                        }
                                        catch (Exception e)
                                        {
                                            BytecodeViewer.viewer.setIcon(false);
                                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(
                                                    e);
                                        }
                                    }
                                };
                                t.start();
                            }
                            if (result == 3)
                            {
                                Thread t = new Thread()
                                {
                                    @Override
                                    public void run()
                                    {
                                        try
                                        {
                                            ClassNode cn = BytecodeViewer.getClassNode(s);
                                            final ClassWriter cw = new ClassWriter(0);
                                            try
                                            {
                                                cn.accept(cw);
                                            }
                                            catch (Exception e)
                                            {
                                                e.printStackTrace();
                                                try
                                                {
                                                    Thread.sleep(200);
                                                    if (cn != null)
                                                        cn.accept(cw);
                                                }
                                                catch (InterruptedException e1)
                                                {
                                                }
                                            }
                                            String contents = Decompiler.fernflower.decompileClassNode(cn, cw.toByteArray());
                                            DiskWriter.replaceFile(path, contents, false);
                                            BytecodeViewer.viewer.setIcon(false);
                                        }
                                        catch (Exception e)
                                        {
                                            BytecodeViewer.viewer.setIcon(false);
                                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(
                                                    e);
                                        }
                                    }
                                };
                                t.start();
                            }
                            if (result == 4)
                            {
                                Thread t = new Thread()
                                {
                                    @Override
                                    public void run()
                                    {
                                        try
                                        {
                                            ClassNode cn = BytecodeViewer.getClassNode(s);
                                            final ClassWriter cw = new ClassWriter(0);
                                            try
                                            {
                                                cn.accept(cw);
                                            }
                                            catch (Exception e)
                                            {
                                                e.printStackTrace();
                                                try
                                                {
                                                    Thread.sleep(200);
                                                    cn.accept(cw);
                                                }
                                                catch (InterruptedException e1)
                                                {
                                                }
                                            }

                                            if (LazyNameUtil.SAME_NAME_JAR_WORKSPACE)
                                            {

                                            }
                                            String contents = Decompiler.krakatau.decompileClassNode(cn, cw.toByteArray());
                                            DiskWriter.replaceFile(path, contents, false);
                                            BytecodeViewer.viewer.setIcon(false);
                                        }
                                        catch (Exception e)
                                        {
                                            BytecodeViewer.viewer.setIcon(false);
                                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(
                                                    e);
                                        }
                                    }
                                };
                                t.start();
                            }
                            if (result == 5)
                            {
                                BytecodeViewer.viewer.setIcon(false);
                            }
                        }
                    }
                };
                t.start();
            }
        });

        mnNewMenu.add(mntmNewMenuItem_12);

        mnNewMenu.add(mntmNewMenuItem);

        JSeparator separator = new JSeparator();
        mnNewMenu.add(separator);

        mnNewMenu.add(mnRecentFiles);

        JSeparator separator_1 = new JSeparator();
        mnNewMenu.add(separator_1);
        mntmAbout.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                aboutWindow.setVisible(true);
            }
        });

        mnNewMenu.add(mntmAbout);

        JMenuItem mntmExit = new JMenuItem("Exit");
        mntmExit.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
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

                if (result == 0)
                {
                    BytecodeViewer.canExit = true;
                    System.exit(0);
                }
            }
        });

        mnNewMenu.add(mntmExit);

        menuBar.add(mnNewMenu_6);

        mnNewMenu_6.add(mnNewMenu_7);

        mnNewMenu_7.add(panel1None);

        mnNewMenu_7.add(separator_7);

        mnNewMenu_7.add(mnProcyon);
        mnProcyon.add(panel1Proc);

        mnProcyon.add(separator_14);

        mnProcyon.add(panel1Proc_E);
        panel1Proc.addActionListener(listener);

        mnNewMenu_7.add(mnCfr);

        jadx1.add(jadxJ1);
        jadx1.add(new JSeparator());
        jadx1.add(jadxE1);

        jadx2.add(jadxJ2);
        jadx2.add(new JSeparator());
        jadx2.add(jadxE2);

        jadx3.add(jadxJ3);
        jadx3.add(new JSeparator());
        jadx3.add(jadxE3);

        mnNewMenu_7.add(jadx1);


        mnCfr.add(panel1CFR);
        panel1CFR.addActionListener(listener);

        mnCfr.add(separator_19);

        mnCfr.add(panel1CFR_E);

        mnNewMenu_7.add(menu_6);

        menu_6.add(panel1JDGUI);

        menu_6.add(separator_35);

        menu_6.add(panel1JDGUI_E);

        mnNewMenu_7.add(mnFernflower);
        mnFernflower.add(panel1Fern);
        panel1Fern.addActionListener(listener);

        mnFernflower.add(separator_20);

        mnFernflower.add(panel1Fern_E);

        mnNewMenu_7.add(mnKrakatau);
        mnKrakatau.add(panel1Krakatau);
        panel1Krakatau.addActionListener(listener);
        mnKrakatau.add(panel1KrakatauBytecode);
        panel1KrakatauBytecode.addActionListener(listener);

        mnKrakatau.add(separator_21);

        mnKrakatau.add(panel1Krakatau_E);

        mnNewMenu_7.add(separator_8);

        mnNewMenu_7.add(mnSmalidex);
        mnSmalidex.add(panel1Smali);
        panel1Smali.addActionListener(listener);

        mnSmalidex.add(separator_22);

        mnSmalidex.add(panel1Smali_E);
        panel1Bytecode.addActionListener(listener);

        mnNewMenu_7.add(separator_15);

        mnNewMenu_7.add(panel1Bytecode);

        mnNewMenu_7.add(panel1Hexcode);

        mnNewMenu_7.add(asmText1);

        mnNewMenu_6.add(mnPane);

        mnPane.add(panel2None);

        mnPane.add(separator_9);

        mnPane.add(menu_1);

        menu_1.add(panel2Proc);

        menu_1.add(separator_10);

        menu_1.add(panel2Proc_E);

        mnPane.add(menu_2);

        mnPane.add(jadx2);

        menu_2.add(panel2CFR);

        menu_2.add(separator_11);

        menu_2.add(panel2CFR_E);

        mnPane.add(menu);

        menu.add(panel2JDGUI);

        menu.add(separator_34);

        menu.add(panel2JDGUI_E);

        mnPane.add(menu_3);

        menu_3.add(panel2Fern);

        menu_3.add(separator_12);

        menu_3.add(panel2Fern_E);

        mnPane.add(menu_4);

        menu_4.add(panel2Krakatau);

        menu_4.add(panel2KrakatauBytecode);

        menu_4.add(separator_16);

        menu_4.add(panel2Krakatau_E);

        mnPane.add(separator_17);

        mnPane.add(menu_5);

        menu_5.add(panel2Smali);

        menu_5.add(separator_23);

        menu_5.add(panel2Smali_E);

        mnPane.add(separator_24);

        mnPane.add(panel2Bytecode);

        mnPane.add(panel2Hexcode);

        mnPane.add(asmText2);

        mnNewMenu_6.add(mnPane_1);

        mnPane_1.add(panel3None);

        mnPane_1.add(separator_25);

        mnPane_1.add(menu_7);

        menu_7.add(panel3Proc);

        menu_7.add(separator_26);

        menu_7.add(panel3Proc_E);

        mnPane_1.add(menu_8);
        mnPane_1.add(jadx3);

        menu_8.add(panel3CFR);

        menu_8.add(separator_27);

        menu_8.add(panel3CFR_E);

        mnPane_1.add(mnJdgui);

        mnJdgui.add(panel3JDGUI);

        mnJdgui.add(separator_33);

        mnJdgui.add(panel3JDGUI_E);

        mnPane_1.add(menu_9);

        menu_9.add(panel3Fern);

        menu_9.add(separator_28);

        menu_9.add(panel3Fern_E);

        mnPane_1.add(menu_10);

        menu_10.add(panel3Krakatau);

        menu_10.add(panel3KrakatauBytecode);

        menu_10.add(separator_29);

        menu_10.add(panel3Krakatau_E);

        mnPane_1.add(separator_30);

        mnPane_1.add(menu_11);

        menu_11.add(panel3Smali);

        menu_11.add(separator_31);

        menu_11.add(panel3Smali_E);

        mnPane_1.add(separator_32);

        mnPane_1.add(panel3Bytecode);

        mnPane_1.add(panel3Hexcode);

        mnPane_1.add(asmText3);

        compileOnSave.setSelected(false);

        menuBar.add(mnSettings);


        mnSettings.add(visualSettings);
        mnSettings.add(separator_13);
        mnSettings.add(compileOnSave);
        compileOnSave.setSelected(false);


        autoCompileOnRefresh.setSelected(false);

        mnSettings.add(autoCompileOnRefresh);

        mnSettings.add(refreshOnChange);

        mnSettings.add(separator_38);
        decodeAPKResources.setSelected(true);

        mnSettings.add(decodeAPKResources);

        mnSettings.add(mnApkConversion);

        mnApkConversion.add(apkConversionDex);

        mnApkConversion.add(apkConversionEnjarify);

        mnSettings.add(separator_37);

        chckbxmntmNewCheckItem_12.setSelected(true);
        mnSettings.add(chckbxmntmNewCheckItem_12);
        chckbxmntmDeleteForeignOutdatedLibs.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {
                if (!chckbxmntmDeleteForeignOutdatedLibs.isSelected())
                {
                    BytecodeViewer.showMessage("WARNING: With this being toggled off outdated libraries will NOT be removed. It's also a security issue. ONLY TURN IT OFF IF YOU KNOW WHAT YOU'RE DOING.");
                }
                BytecodeViewer.deleteForeignLibraries = chckbxmntmDeleteForeignOutdatedLibs.isSelected();
            }
        });
        mnSettings.add(forcePureAsciiAsText);
        forcePureAsciiAsText.setSelected(true);
        forcePureAsciiAsText.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {
                Settings.saveSettings();
            }
        });
        mnSettings.add(separator_36);

        /*chckbxmntmDeleteForeinoutdatedLibs.setSelected(true);
        mnSettings.add(chckbxmntmDeleteForeinoutdatedLibs);

        mnSettings.add(separator_36);*/

        mntmSetPythonDirectory.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                pythonC();
            }
        });

        mnSettings.add(mntmSetPythonDirectory);
        mntmSetJreRt.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                rtC();
            }
        });
        mntmSetPythonx.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {
                pythonC3();
            }
        });

        mnSettings.add(mntmSetPythonx);

        mnSettings.add(mntmSetJreRt);
        mntmSetOpitonalLibrary.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                library();
            }
        });

        mnSettings.add(mntmSetOpitonalLibrary);
        mntmSetJavacExecutable.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {
                javac();
            }
        });

        mnSettings.add(mntmSetJavacExecutable);

        mnSettings.add(separator_6);
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

        menuBar.add(mnNewMenu_5);
        mntmNewMenuItem_6.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                if (BytecodeViewer.runningObfuscation)
                {
                    BytecodeViewer.showMessage("You're currently running an obfuscation task, wait for this to finish.");
                    return;
                }
                new RenameFields().start();
                workPane.refreshClass.doClick();
                cn.tree.updateUI();
            }
        });

        mnNewMenu_5.add(strongObf);

        mnNewMenu_5.add(lightObf);

        mnNewMenu_5.add(separator_2);
        mntmNewMenuItem_8.setEnabled(false);

        mnNewMenu_5.add(mntmNewMenuItem_8);

        mnNewMenu_5.add(mntmNewMenuItem_6);
        mntmNewMenuItem_7.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                if (BytecodeViewer.runningObfuscation)
                {
                    BytecodeViewer.showMessage("You're currently running an obfuscation task, wait for this to finish.");
                    return;
                }
                new RenameMethods().start();
                workPane.refreshClass.doClick();
                cn.tree.updateUI();
            }
        });

        mnNewMenu_5.add(mntmNewMenuItem_7);
        mntmNewMenuItem_11.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                if (BytecodeViewer.runningObfuscation)
                {
                    BytecodeViewer.showMessage("You're currently running an obfuscation task, wait for this to finish.");
                    return;
                }
                new RenameClasses().start();
                workPane.refreshClass.doClick();
                cn.tree.updateUI();
            }
        });

        mnNewMenu_5.add(mntmNewMenuItem_11);
        mntmNewMenuItem_9.setEnabled(false);

        mnNewMenu_5.add(mntmNewMenuItem_9);
        mntmNewMenuItem_10.setEnabled(false);

        mnNewMenu_5.add(mntmNewMenuItem_10);

        menuBar.add(mnNewMenu_1);
        mnNewMenu_1.add(mntmStartExternalPlugin);
        mnNewMenu_1.add(separator_4);
        mnNewMenu_1.add(mnRecentPlugins);
        mnNewMenu_1.add(separator_5);
        mntmCodeSequenceDiagram.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                if (BytecodeViewer.getLoadedClasses().isEmpty())
                {
                    BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                    return;
                }
                PluginManager.runPlugin(new CodeSequenceDiagram());
            }
        });

        mnNewMenu_1.add(mntmCodeSequenceDiagram);
        mnNewMenu_1.add(mntmNewMenuItem_1);
        mnNewMenu_1.add(mntmShowMainMethods);
        mnNewMenu_1.add(mntmShowAllStrings);
        mntmReplaceStrings.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                if (BytecodeViewer.getLoadedClasses().isEmpty())
                {
                    BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                    return;
                }
                new ReplaceStringsOptions().setVisible(true);
            }
        });

        mnNewMenu_1.add(mntmReplaceStrings);
        mnNewMenu_1.add(mntmNewMenuItem_2);
        mnNewMenu_1.add(mntmStartZkmString);
        mntmZstringarrayDecrypter.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                PluginManager.runPlugin(new ZStringArrayDecrypter());
            }
        });

        mntmStackFramesRemover.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                PluginManager.runPlugin(new StackFramesRemover());
            }
        });

        mnNewMenu_1.add(mntmZstringarrayDecrypter);
        mnNewMenu_1.add(mntmStackFramesRemover);

        waitIcons = new JMenuItem[10];
        for (int i = 0; i < 10; i++)
        {
            waitIcons[i] = new JMenuItem("");
            waitIcons[i].setMaximumSize(new Dimension(20, 50));
            waitIcons[i].setEnabled(false);
            menuBar.add(waitIcons[i]);
        }

        mntmStartExternalPlugin.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                JFileChooser fc = new JFileChooser();
                fc.setFileFilter(PluginManager.fileFilter());
                fc.setFileHidingEnabled(false);
                fc.setAcceptAllFileFilterUsed(false);
                int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);

                if (returnVal == JFileChooser.APPROVE_OPTION)
                    try
                    {
                        BytecodeViewer.viewer.setIcon(true);
                        BytecodeViewer.startPlugin(fc.getSelectedFile());
                        BytecodeViewer.viewer.setIcon(false);
                    }
                    catch (Exception e1)
                    {
                        new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
                    }
            }
        });
        mntmStartZkmString.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                PluginManager.runPlugin(new ZKMStringDecrypter());
            }
        });
        mntmNewMenuItem_2.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                PluginManager.runPlugin(new AllatoriStringDecrypter());
            }
        });
        mntmNewMenuItem_1.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (BytecodeViewer.getLoadedClasses().isEmpty())
                {
                    BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                    return;
                }
                new MaliciousCodeScannerOptions().setVisible(true);
            }
        });
        mntmShowAllStrings.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                PluginManager.runPlugin(new ShowAllStrings());
            }
        });

        mntmShowMainMethods.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                PluginManager.runPlugin(new ShowMainMethods());
            }
        });

        setSize(new Dimension(800, 400));
        if (BytecodeViewer.PREVIEW_COPY)
            setTitle("Bytecode Viewer " + BytecodeViewer.VERSION + " Preview - https://bytecodeviewer.com | https://the.bytecode.club - @Konloch");
        else
            setTitle("Bytecode Viewer " + BytecodeViewer.VERSION + " - https://bytecodeviewer.com | https://the.bytecode.club - @Konloch");

        getContentPane().setLayout(
                new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

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


        panelGroup1.add(panel1None);
        panelGroup1.add(panel1Proc);
        panelGroup1.add(panel1CFR);
        panelGroup1.add(jadxJ1);
        panelGroup1.add(panel1JDGUI);
        panelGroup1.add(panel1Fern);
        panelGroup1.add(panel1Krakatau);
        panelGroup1.add(panel1KrakatauBytecode);
        panelGroup1.add(panel1Smali);
        panelGroup1.add(panel1Bytecode);
        panelGroup1.add(panel1Hexcode);
        panelGroup1.add(asmText1);

        panelGroup2.add(panel2None);
        panelGroup2.add(panel2Proc);
        panelGroup2.add(panel2CFR);
        panelGroup2.add(jadxJ2);
        panelGroup2.add(panel2JDGUI);
        panelGroup2.add(panel2Fern);
        panelGroup2.add(panel2Krakatau);
        panelGroup2.add(panel2KrakatauBytecode);
        panelGroup2.add(panel2Smali);
        panelGroup2.add(panel2Bytecode);
        panelGroup2.add(panel2Hexcode);
        panelGroup2.add(asmText2);

        panelGroup3.add(panel3None);
        panelGroup3.add(panel3Proc);
        panelGroup3.add(panel3CFR);
        panelGroup3.add(jadxJ3);
        panelGroup3.add(panel3JDGUI);
        panelGroup3.add(panel3Fern);
        panelGroup3.add(panel3Krakatau);
        panelGroup3.add(panel3KrakatauBytecode);
        panelGroup3.add(panel3Smali);
        panelGroup3.add(panel3Bytecode);
        panelGroup3.add(panel3Hexcode);
        panelGroup3.add(asmText3);

        fontSpinner.setPreferredSize(new Dimension(42, 20));
        fontSpinner.setSize(new Dimension(42, 20));
        fontSpinner.setModel(new SpinnerNumberModel(new Integer(12), new Integer(1), null, new Integer(1)));

        mnFontSize.add(fontSpinner);

        visualSettings.add(mnFontSize);
        visualSettings.add(showFileInTabTitle);
        showFileInTabTitle.setSelected(false);
        showFileInTabTitle.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {
                BytecodeViewer.displayParentInTab = BytecodeViewer.viewer.showFileInTabTitle.isSelected();
                Settings.saveSettings();
            }
        });

        visualSettings.add(synchronizedViewing);
        showClassMethods.setSelected(false);
        visualSettings.add(showClassMethods);


        panelGroup1.setSelected(panel1Fern.getModel(), true);
        panelGroup2.setSelected(panel2Bytecode.getModel(), true);
        panelGroup3.setSelected(panel3None.getModel(), true);

        this.setLocationRelativeTo(null);
    }


    @Override
    public void openClassFile(final FileContainer container, final String name, final ClassNode cn)
    {
        for (final VisibleComponent vc : rfComps)
        {
            vc.openClassFile(container, name, cn);
        }
    }

    @Override
    public void openFile(final FileContainer container, final String name, byte[] content)
    {
        for (final VisibleComponent vc : rfComps)
        {
            vc.openFile(container, name, content);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getComponent(final Class<T> clazz)
    {
        for (final VisibleComponent vc : rfComps)
        {
            if (vc.getClass() == clazz)
                return (T) vc;
        }
        return null;
    }

    public void pythonC()
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter()
        {
            @Override
            public boolean accept(File f)
            {
                return true;
            }

            @Override
            public String getDescription()
            {
                return "Python (Or PyPy for speed) 2.7 Executable";
            }
        });
        fc.setFileHidingEnabled(false);
        fc.setAcceptAllFileFilterUsed(false);
        int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            try
            {
                BytecodeViewer.python = fc.getSelectedFile().getAbsolutePath();
            }
            catch (Exception e1)
            {
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
            }
    }

    public void javac()
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter()
        {
            @Override
            public boolean accept(File f)
            {
                return true;
            }

            @Override
            public String getDescription()
            {
                return "Javac Executable (Requires JDK  'C:/programfiles/Java/JDK_xx/bin/javac.exe)";
            }
        });
        fc.setFileHidingEnabled(false);
        fc.setAcceptAllFileFilterUsed(false);
        int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            try
            {
                BytecodeViewer.javac = fc.getSelectedFile().getAbsolutePath();
            }
            catch (Exception e1)
            {
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
            }
    }

    public void java()
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter()
        {
            @Override
            public boolean accept(File f)
            {
                return true;
            }

            @Override
            public String getDescription()
            {
                return "Java Executable (Inside Of JRE/JDK 'C:/programfiles/Java/JDK_xx/bin/java.exe')";
            }
        });
        fc.setFileHidingEnabled(false);
        fc.setAcceptAllFileFilterUsed(false);
        int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            try
            {
                BytecodeViewer.java = fc.getSelectedFile().getAbsolutePath();
            }
            catch (Exception e1)
            {
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
            }
    }

    public void pythonC3()
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter()
        {
            @Override
            public boolean accept(File f)
            {
                return true;
            }

            @Override
            public String getDescription()
            {
                return "Python (Or PyPy for speed) 3.x Executable";
            }
        });
        fc.setFileHidingEnabled(false);
        fc.setAcceptAllFileFilterUsed(false);
        int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            try
            {
                BytecodeViewer.python3 = fc.getSelectedFile().getAbsolutePath();
            }
            catch (Exception e1)
            {
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
            }
    }

    public void library()
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter()
        {
            @Override
            public boolean accept(File f)
            {
                return f.isDirectory();
            }

            @Override
            public String getDescription()
            {
                return "Optional Library Folder";
            }
        });
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setFileHidingEnabled(false);
        fc.setAcceptAllFileFilterUsed(false);
        int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            try
            {
                BytecodeViewer.library = fc.getSelectedFile().getAbsolutePath();
            }
            catch (Exception e1)
            {
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
            }
    }


    public void rtC()
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter()
        {
            @Override
            public boolean accept(File f)
            {
                return true;
            }

            @Override
            public String getDescription()
            {
                return "JRE RT Library";
            }
        });
        fc.setFileHidingEnabled(false);
        fc.setAcceptAllFileFilterUsed(false);
        int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            try
            {
                BytecodeViewer.rt = fc.getSelectedFile().getAbsolutePath();
            }
            catch (Exception e1)
            {
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
            }
    }
}
