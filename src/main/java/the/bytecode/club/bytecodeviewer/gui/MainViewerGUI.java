package the.bytecode.club.bytecodeviewer.gui;

import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.*;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.gui.components.*;
import the.bytecode.club.bytecodeviewer.gui.plugins.MaliciousCodeScannerOptions;
import the.bytecode.club.bytecodeviewer.gui.plugins.ReplaceStringsOptions;
import the.bytecode.club.bytecodeviewer.gui.resourcelist.ResourceListPane;
import the.bytecode.club.bytecodeviewer.gui.resourcesearch.SearchBoxPane;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.DecompilerSelectionPane;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.WorkPaneMainComponent;
import the.bytecode.club.bytecodeviewer.gui.theme.LAFTheme;
import the.bytecode.club.bytecodeviewer.gui.theme.RSTATheme;
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
import the.bytecode.club.bytecodeviewer.resources.ResourceDecompiling;
import the.bytecode.club.bytecodeviewer.resources.ResourceExporting;

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
public class MainViewerGUI extends JFrame
{
    public AboutWindow aboutWindow = new AboutWindow();
    public boolean isMaximized;
    public final JMenuItem[] waitIcons;
    
    //main UI components
    private static final ArrayList<VisibleComponent> uiComponents = new ArrayList<>();
    public final WorkPaneMainComponent workPane = new WorkPaneMainComponent();
    public final ResourceListPane resourcePane = new ResourceListPane();
    public final SearchBoxPane searchBoxPane = new SearchBoxPane();
    public JSplitPane splitPane1;
    public JSplitPane splitPane2;
    
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
    public final JMenuItem about = new JMenuItem("About");
    public final JMenuItem exit = new JMenuItem("Exit");
    
    //all of the view main menu components
    public final JMenu viewMainMenu = new JMenu("View");
    public final DecompilerSelectionPane viewPane1 = new DecompilerSelectionPane(1);
    public final DecompilerSelectionPane viewPane2 = new DecompilerSelectionPane(2);
    public final DecompilerSelectionPane viewPane3 = new DecompilerSelectionPane(3);
    
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
    
    //all of the settings main menu components
    public final ButtonGroup apkConversionGroup = new ButtonGroup();
    public final JRadioButtonMenuItem apkConversionDex = new JRadioButtonMenuItem("Dex2Jar");
    public final JRadioButtonMenuItem apkConversionEnjarify = new JRadioButtonMenuItem("Enjarify");
    public final JMenu fontSize = new JMenu("Font Size");
    public final JSpinner fontSpinner = new JSpinner();
    public final JMenu rstaTheme = new JMenu("Text Area Theme");
    public final JMenu lafTheme = new JMenu("Window Theme");
    
    //BCV settings
    public final JCheckBoxMenuItem refreshOnChange = new JCheckBoxMenuItem("Refresh On View Change");
    private final JCheckBoxMenuItem deleteForeignOutdatedLibs = new JCheckBoxMenuItem("Delete Foreign/Outdated Libs");
    public final JMenu settingsMainMenu = new JMenu("Settings");
    public final JMenu visualSettings = new JMenu("Visual Settings");
    public final JMenu apkConversion = new JMenu("APK Conversion");
    public final JMenu bytecodeDecompilerSettingsSecondaryMenu = new JMenu("Bytecode Decompiler");
    public final JCheckBoxMenuItem updateCheck = new JCheckBoxMenuItem("Update Check");
    public final JMenuItem setPython2 = new JMenuItem("Set Python 2.7 Executable");
    public final JMenuItem setPython3 = new JMenuItem("Set Python 3.X Executable");
    public final JMenuItem setJRERT = new JMenuItem("Set JRE RT Library");
    public final JMenuItem setJavac = new JMenuItem("Set Javac Executable");
    public final JMenuItem setOptionalLibrary = new JMenuItem("Set Optional Library Folder");
    public final JCheckBoxMenuItem compileOnSave = new JCheckBoxMenuItem("Compile On Save");
    public final JCheckBoxMenuItem showFileInTabTitle = new JCheckBoxMenuItem("Show File In Tab Title");
    public final JCheckBoxMenuItem simplifyNameInTabTitle = new JCheckBoxMenuItem("Simplify Name In Tab Title");
    public final JCheckBoxMenuItem forcePureAsciiAsText = new JCheckBoxMenuItem("Force Pure Ascii As Text");
    public final JCheckBoxMenuItem autoCompileOnRefresh = new JCheckBoxMenuItem("Compile On Refresh");
    public final JCheckBoxMenuItem decodeAPKResources = new JCheckBoxMenuItem("Decode APK Resources");
    public final JCheckBoxMenuItem synchronizedViewing = new JCheckBoxMenuItem("Synchronized Viewing");
    public final JCheckBoxMenuItem showClassMethods = new JCheckBoxMenuItem("Show Class Methods");
    public final Map<RSTATheme, JRadioButtonMenuItem> rstaThemes = new HashMap<>();
    public final Map<LAFTheme, JRadioButtonMenuItem> lafThemes = new HashMap<>();
    
    //CFIDE settings
    public final JCheckBoxMenuItem appendBracketsToLabels = new JCheckBoxMenuItem("Append Brackets To Labels");
    public JCheckBoxMenuItem debugHelpers = new JCheckBoxMenuItem("Debug Helpers");
    
    //FernFlower settings
    public final JMenu fernFlowerSettingsSecondaryMenu = new JMenu("FernFlower");
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
    
    //Proycon
    public final JMenu procyonSettingsSecondaryMenu = new JMenu("Procyon");
    public final JCheckBoxMenuItem alwaysGenerateExceptionVars = new JCheckBoxMenuItem("Always Generate Exception Variable For Catch Blocks");
    public final JCheckBoxMenuItem excludeNestedTypes = new JCheckBoxMenuItem("Exclude Nested Types");
    public final JCheckBoxMenuItem showDebugLineNumbers = new JCheckBoxMenuItem("Show Debug Line Numbers");
    public final JCheckBoxMenuItem includeLineNumbersInBytecode = new JCheckBoxMenuItem("Include Line Numbers In Bytecode");
    public final JCheckBoxMenuItem includeErrorDiagnostics = new JCheckBoxMenuItem("Include Error Diagnostics");
    public final JCheckBoxMenuItem showSyntheticMembers = new JCheckBoxMenuItem("Show Synthetic Members");
    public final JCheckBoxMenuItem simplifyMemberReferences = new JCheckBoxMenuItem("Simplify Member References");
    public final JCheckBoxMenuItem mergeVariables = new JCheckBoxMenuItem("Merge Variables");
    public final JCheckBoxMenuItem forceExplicitTypeArguments = new JCheckBoxMenuItem("Force Explicit Type Arguments");
    public final JCheckBoxMenuItem forceExplicitImports = new JCheckBoxMenuItem("Force Explicit Imports");
    public final JCheckBoxMenuItem flattenSwitchBlocks = new JCheckBoxMenuItem("Flatten Switch Blocks");
    public final JCheckBoxMenuItem retainPointlessSwitches = new JCheckBoxMenuItem("Retain Pointless Switches");
    public final JCheckBoxMenuItem retainRedunantCasts = new JCheckBoxMenuItem("Retain Redundant Casts");
    public final JCheckBoxMenuItem unicodeOutputEnabled = new JCheckBoxMenuItem("Unicode Output Enabled");
    
    //CFR
    public final JMenu cfrSettingsSecondaryMenu = new JMenu("CFR");
    public final JCheckBoxMenuItem decodeEnumSwitch = new JCheckBoxMenuItem("Decode Enum Switch");
    public final JCheckBoxMenuItem sugarEnums = new JCheckBoxMenuItem("SugarEnums");
    public final JCheckBoxMenuItem decodeStringSwitch = new JCheckBoxMenuItem("Decode String Switch");
    public final JCheckBoxMenuItem arrayiter = new JCheckBoxMenuItem("Arrayiter");
    public final JCheckBoxMenuItem collectioniter = new JCheckBoxMenuItem("Collectioniter");
    public final JCheckBoxMenuItem innerClasses = new JCheckBoxMenuItem("Inner Classes");
    public final JCheckBoxMenuItem removeBoilerPlate = new JCheckBoxMenuItem("Remove Boiler Plate");
    public final JCheckBoxMenuItem removeInnerClassSynthetics = new JCheckBoxMenuItem("Remove Inner Class Synthetics");
    public final JCheckBoxMenuItem decodeLambdas = new JCheckBoxMenuItem("Decode Lambdas");
    public final JCheckBoxMenuItem hideBridgeMethods = new JCheckBoxMenuItem("Hide Bridge Methods");
    public final JCheckBoxMenuItem liftConstructorInit = new JCheckBoxMenuItem("Lift  Constructor Init");
    public final JCheckBoxMenuItem removeDeadMethods = new JCheckBoxMenuItem("Remove Dead Methods");
    public final JCheckBoxMenuItem removeBadGenerics = new JCheckBoxMenuItem("Remove Bad Generics");
    public final JCheckBoxMenuItem sugarAsserts = new JCheckBoxMenuItem("Sugar Asserts");
    public final JCheckBoxMenuItem sugarBoxing = new JCheckBoxMenuItem("Sugar Boxing");
    public final JCheckBoxMenuItem showVersion = new JCheckBoxMenuItem("Show Version");
    public final JCheckBoxMenuItem decodeFinally = new JCheckBoxMenuItem("Decode Finally");
    public final JCheckBoxMenuItem tidyMonitors = new JCheckBoxMenuItem("Tidy Monitors");
    public final JCheckBoxMenuItem lenient = new JCheckBoxMenuItem("Lenient");
    public final JCheckBoxMenuItem dumpClassPath = new JCheckBoxMenuItem("Dump Classpath");
    public final JCheckBoxMenuItem comments = new JCheckBoxMenuItem("Comments");
    public final JCheckBoxMenuItem forceTopSort = new JCheckBoxMenuItem("Force Top Sort");
    public final JCheckBoxMenuItem forceTopSortAggress = new JCheckBoxMenuItem("Force Top Sort Aggress");
    public final JCheckBoxMenuItem forceExceptionPrune = new JCheckBoxMenuItem("Force Exception Prune");
    public final JCheckBoxMenuItem stringBuffer = new JCheckBoxMenuItem("String Buffer");
    public final JCheckBoxMenuItem stringBuilder = new JCheckBoxMenuItem("String Builder");
    public final JCheckBoxMenuItem silent = new JCheckBoxMenuItem("Silent");
    public final JCheckBoxMenuItem recover = new JCheckBoxMenuItem("Recover");
    public final JCheckBoxMenuItem eclipse = new JCheckBoxMenuItem("Eclipse");
    public final JCheckBoxMenuItem override = new JCheckBoxMenuItem("Override");
    public final JCheckBoxMenuItem showInferrable = new JCheckBoxMenuItem("Show Inferrable");
    public final JCheckBoxMenuItem aexagg = new JCheckBoxMenuItem("Aexagg");
    public final JCheckBoxMenuItem forceCondPropagate = new JCheckBoxMenuItem("Force Cond Propagate");
    public final JCheckBoxMenuItem hideUTF = new JCheckBoxMenuItem("Hide UTF");
    public final JCheckBoxMenuItem hideLongStrings = new JCheckBoxMenuItem("Hide Long Strings");
    public final JCheckBoxMenuItem commentMonitor = new JCheckBoxMenuItem("Comment Monitors");
    public final JCheckBoxMenuItem allowCorrecting = new JCheckBoxMenuItem("Allow Correcting");
    public final JCheckBoxMenuItem labelledBlocks = new JCheckBoxMenuItem("Labelled Blocks");
    public final JCheckBoxMenuItem j14ClassOBJ = new JCheckBoxMenuItem("J14ClassOBJ");
    public final JCheckBoxMenuItem hideLangImports = new JCheckBoxMenuItem("Hide Lang Imports");
    public final JCheckBoxMenuItem recoveryTypeClash = new JCheckBoxMenuItem("Recover Type Clash");
    public final JCheckBoxMenuItem recoveryTypehInts = new JCheckBoxMenuItem("Recover Type  Hints");
    public final JCheckBoxMenuItem forceTurningIFs = new JCheckBoxMenuItem("Force Returning IFs");
    public final JCheckBoxMenuItem forLoopAGGCapture = new JCheckBoxMenuItem("For Loop AGG Capture");
    
    //obfuscation
    public final JMenu obfuscate = new JMenu("Obfuscate");
    public final JMenuItem renameFields = new JMenuItem("Rename Fields");
    public final JMenuItem renameMethods = new JMenuItem("Rename Methods");
    public final JMenuItem moveAllClassesIntoRoot = new JMenuItem("Move All Classes Into Root Package");
    public final JMenuItem controlFlow = new JMenuItem("Control Flow");
    public final JMenuItem junkCode = new JMenuItem("Junk Code");
    public final ButtonGroup obfuscatorGroup = new ButtonGroup();
    public final JRadioButtonMenuItem strongObf = new JRadioButtonMenuItem("Strong Obfuscation");
    public final JRadioButtonMenuItem lightObf = new JRadioButtonMenuItem("Light Obfuscation");
    public final JMenuItem renameClasses = new JMenuItem("Rename Classes");

    public MainViewerGUI()
    {
        setIconImages(Resources.iconList);
        setSize(new Dimension(800, 400));
    
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatch());
        addWindowStateListener(new WindowStateChangeAdapter(this));
        addWindowListener(new WindowClosingAdapter());

        buildMenuBar();
        buildFileMenu();
        buildViewMenu();
        buildSettingsMenu();
        buildPluginMenu();
        buildObfuscateMenu();
        defaultSettings();
        
        waitIcons = new JMenuItem[10];
        for (int i = 0; i < 10; i++)
        {
            waitIcons[i] = new JMenuItem("");
            waitIcons[i].setMaximumSize(new Dimension(20, 50));
            waitIcons[i].setEnabled(false);
            rootMenu.add(waitIcons[i]);
        }
        
        setTitle("Bytecode Viewer " + VERSION + " - https://bytecodeviewer.com | https://the.bytecode.club - @Konloch");

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

        resourcePane.setMinimumSize(new Dimension(200, 50));
        resourcePane.setPreferredSize(new Dimension(200, 50));
        resourcePane.setMaximumSize(new Dimension(200, 2147483647));
        
        searchBoxPane.setPreferredSize(new Dimension(200, 50));
        searchBoxPane.setMinimumSize(new Dimension(200, 50));
        searchBoxPane.setMaximumSize(new Dimension(200, 2147483647));
        
        splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, resourcePane, searchBoxPane);
        splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane1, workPane);
        getContentPane().add(splitPane2);
        splitPane2.setResizeWeight(0.05);
        splitPane1.setResizeWeight(0.5);
        
        uiComponents.add(resourcePane);
        uiComponents.add(searchBoxPane);
        uiComponents.add(workPane);

        viewPane1.getGroup().setSelected(viewPane1.getFern().getJava().getModel(), true);
        viewPane2.getGroup().setSelected(viewPane1.getBytecode().getModel(), true);
        viewPane3.getGroup().setSelected(viewPane1.getNone().getModel(), true);

        this.setLocationRelativeTo(null);
    }
    
    public void buildMenuBar()
    {
        setJMenuBar(rootMenu);
    }
    
    public void buildFileMenu()
    {
        rootMenu.add(fileMainMenu);
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
        fileMainMenu.add(about);
        fileMainMenu.add(exit);
    
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
        about.addActionListener(arg0 -> aboutWindow.setVisible(true));
        exit.addActionListener(arg0 -> askBeforeExiting());
    }
    
    public void buildViewMenu()
    {
        rootMenu.add(viewMainMenu);
        viewMainMenu.add(visualSettings);
        viewMainMenu.add(viewPane1.menu);
        viewMainMenu.add(viewPane2.menu);
        viewMainMenu.add(viewPane3.menu);
    }
    
    public void buildSettingsMenu()
    {
        rootMenu.add(settingsMainMenu);
        
        //settingsMainMenu.add(visualSettings);
        //settingsMainMenu.add(new JSeparator());
        settingsMainMenu.add(compileOnSave);
        settingsMainMenu.add(autoCompileOnRefresh);
        settingsMainMenu.add(refreshOnChange);
        settingsMainMenu.add(new JSeparator());
        settingsMainMenu.add(decodeAPKResources);
        settingsMainMenu.add(apkConversion);
        apkConversion.add(apkConversionDex);
        apkConversion.add(apkConversionEnjarify);
        apkConversionGroup.add(apkConversionDex);
        apkConversionGroup.add(apkConversionEnjarify);
        apkConversionGroup.setSelected(apkConversionDex.getModel(), true);
        settingsMainMenu.add(new JSeparator());
        settingsMainMenu.add(updateCheck);
        settingsMainMenu.add(forcePureAsciiAsText);
        settingsMainMenu.add(new JSeparator());
        settingsMainMenu.add(setPython2);
        settingsMainMenu.add(setPython3);
        settingsMainMenu.add(setJRERT);
        settingsMainMenu.add(setOptionalLibrary);
        settingsMainMenu.add(setJavac);
        settingsMainMenu.add(new JSeparator());
        fontSpinner.setPreferredSize(new Dimension(42, 20));
        fontSpinner.setSize(new Dimension(42, 20));
        fontSpinner.setModel(new SpinnerNumberModel(12, 1, null, 1));
        fontSize.add(fontSpinner);
        
        ButtonGroup rstaGroup = new ButtonGroup();
        for (RSTATheme t : RSTATheme.values())
        {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(t.getReadableName());
            if (Configuration.rstaTheme.equals(t))
                item.setSelected(true);
            
            rstaGroup.add(item);
            
            item.addActionListener(e ->
            {
                Configuration.rstaTheme = t;
                item.setSelected(true);
                Settings.saveSettings();
            });
            
            rstaThemes.put(t, item);
            rstaTheme.add(item);
        }
    
        ButtonGroup lafGroup = new ButtonGroup();
        for (LAFTheme theme : LAFTheme.values())
        {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(theme.getReadableName());
            if (Configuration.lafTheme.equals(theme))
                item.setSelected(true);
            
            lafGroup.add(item);
            
            item.addActionListener(e ->
            {
                Configuration.lafTheme = theme;
                Configuration.rstaTheme = theme.getRSTATheme();
                rstaThemes.get(Configuration.rstaTheme).setSelected(true);
                item.setSelected(true);
                Settings.saveSettings();
            });
    
            lafThemes.put(theme, item);
            lafTheme.add(item);
        }
        
        visualSettings.add(lafTheme);
        visualSettings.add(rstaTheme);
        visualSettings.add(fontSize);
        visualSettings.add(showFileInTabTitle);
        visualSettings.add(simplifyNameInTabTitle);
        visualSettings.add(synchronizedViewing);
        visualSettings.add(showClassMethods);
        
        //PROCYON SETTINGS
        settingsMainMenu.add(procyonSettingsSecondaryMenu);
        procyonSettingsSecondaryMenu.add(alwaysGenerateExceptionVars);
        procyonSettingsSecondaryMenu.add(excludeNestedTypes);
        procyonSettingsSecondaryMenu.add(showDebugLineNumbers);
        procyonSettingsSecondaryMenu.add(includeLineNumbersInBytecode);
        procyonSettingsSecondaryMenu.add(includeErrorDiagnostics);
        procyonSettingsSecondaryMenu.add(showSyntheticMembers);
        procyonSettingsSecondaryMenu.add(simplifyMemberReferences);
        procyonSettingsSecondaryMenu.add(mergeVariables);
        procyonSettingsSecondaryMenu.add(forceExplicitTypeArguments);
        procyonSettingsSecondaryMenu.add(forceExplicitImports);
        procyonSettingsSecondaryMenu.add(flattenSwitchBlocks);
        procyonSettingsSecondaryMenu.add(retainPointlessSwitches);
        procyonSettingsSecondaryMenu.add(retainRedunantCasts);
        procyonSettingsSecondaryMenu.add(unicodeOutputEnabled);
        
        //CFR SETTINGS
        settingsMainMenu.add(cfrSettingsSecondaryMenu);
        cfrSettingsSecondaryMenu.add(decodeEnumSwitch);
        cfrSettingsSecondaryMenu.add(sugarEnums);
        cfrSettingsSecondaryMenu.add(decodeStringSwitch);
        cfrSettingsSecondaryMenu.add(arrayiter);
        cfrSettingsSecondaryMenu.add(collectioniter);
        cfrSettingsSecondaryMenu.add(innerClasses);
        cfrSettingsSecondaryMenu.add(removeBoilerPlate);
        cfrSettingsSecondaryMenu.add(removeInnerClassSynthetics);
        cfrSettingsSecondaryMenu.add(decodeLambdas);
        cfrSettingsSecondaryMenu.add(hideBridgeMethods);
        cfrSettingsSecondaryMenu.add(liftConstructorInit);
        cfrSettingsSecondaryMenu.add(removeDeadMethods);
        cfrSettingsSecondaryMenu.add(removeBadGenerics);
        cfrSettingsSecondaryMenu.add(sugarAsserts);
        cfrSettingsSecondaryMenu.add(sugarBoxing);
        cfrSettingsSecondaryMenu.add(showVersion);
        cfrSettingsSecondaryMenu.add(decodeFinally);
        cfrSettingsSecondaryMenu.add(tidyMonitors);
        cfrSettingsSecondaryMenu.add(lenient);
        cfrSettingsSecondaryMenu.add(dumpClassPath);
        cfrSettingsSecondaryMenu.add(comments);
        cfrSettingsSecondaryMenu.add(forceTopSort);
        cfrSettingsSecondaryMenu.add(forceTopSortAggress);
        cfrSettingsSecondaryMenu.add(forceExceptionPrune);
        cfrSettingsSecondaryMenu.add(stringBuffer);
        cfrSettingsSecondaryMenu.add(stringBuilder);
        cfrSettingsSecondaryMenu.add(silent);
        cfrSettingsSecondaryMenu.add(recover);
        cfrSettingsSecondaryMenu.add(eclipse);
        cfrSettingsSecondaryMenu.add(override);
        cfrSettingsSecondaryMenu.add(showInferrable);
        cfrSettingsSecondaryMenu.add(aexagg);
        cfrSettingsSecondaryMenu.add(forceCondPropagate);
        cfrSettingsSecondaryMenu.add(hideUTF);
        cfrSettingsSecondaryMenu.add(hideLongStrings);
        cfrSettingsSecondaryMenu.add(commentMonitor);
        cfrSettingsSecondaryMenu.add(allowCorrecting);
        cfrSettingsSecondaryMenu.add(labelledBlocks);
        cfrSettingsSecondaryMenu.add(j14ClassOBJ);
        cfrSettingsSecondaryMenu.add(hideLangImports);
        cfrSettingsSecondaryMenu.add(recoveryTypeClash);
        cfrSettingsSecondaryMenu.add(recoveryTypehInts);
        cfrSettingsSecondaryMenu.add(forceTurningIFs);
        cfrSettingsSecondaryMenu.add(forLoopAGGCapture);
        
        //FERNFLOWER SETTINGS
        settingsMainMenu.add(fernFlowerSettingsSecondaryMenu);
        fernFlowerSettingsSecondaryMenu.add(dc4);
        fernFlowerSettingsSecondaryMenu.add(nns);
        fernFlowerSettingsSecondaryMenu.add(ner);
        fernFlowerSettingsSecondaryMenu.add(bto);
        fernFlowerSettingsSecondaryMenu.add(rgn);
        fernFlowerSettingsSecondaryMenu.add(rer);
        fernFlowerSettingsSecondaryMenu.add(rbr);
        fernFlowerSettingsSecondaryMenu.add(rsy);
        fernFlowerSettingsSecondaryMenu.add(hes);
        fernFlowerSettingsSecondaryMenu.add(hdc);
        fernFlowerSettingsSecondaryMenu.add(din);
        fernFlowerSettingsSecondaryMenu.add(das);
        fernFlowerSettingsSecondaryMenu.add(dgs);
        fernFlowerSettingsSecondaryMenu.add(den);
        fernFlowerSettingsSecondaryMenu.add(uto);
        fernFlowerSettingsSecondaryMenu.add(udv);
        fernFlowerSettingsSecondaryMenu.add(fdi);
        fernFlowerSettingsSecondaryMenu.add(asc);
        fernFlowerSettingsSecondaryMenu.add(ren);
        
        //CFIDE SETTINGS
        settingsMainMenu.add(bytecodeDecompilerSettingsSecondaryMenu);
        bytecodeDecompilerSettingsSecondaryMenu.add(debugHelpers);
        bytecodeDecompilerSettingsSecondaryMenu.add(appendBracketsToLabels);
        
        deleteForeignOutdatedLibs.addActionListener(arg0 -> showForeignLibraryWarning());
        forcePureAsciiAsText.addActionListener(arg0 -> Settings.saveSettings());
        setPython2.addActionListener(arg0 -> selectPythonC());
        setJRERT.addActionListener(arg0 -> selectJRERTLibrary());
        setPython3.addActionListener(arg0 -> selectPythonC3());
        setOptionalLibrary.addActionListener(arg0 -> selectOpenalLibraryFolder());
        setJavac.addActionListener(arg0 -> selectJavac());
        showFileInTabTitle.addActionListener(arg0 -> {
            Configuration.displayParentInTab = BytecodeViewer.viewer.showFileInTabTitle.isSelected();
            Settings.saveSettings();
            BytecodeViewer.refreshAllTabTitles();
        });
        simplifyNameInTabTitle.addActionListener(arg0 -> {
            Configuration.simplifiedTabNames = BytecodeViewer.viewer.simplifyNameInTabTitle.isSelected();
            Settings.saveSettings();
            BytecodeViewer.refreshAllTabTitles();
        });
    }
    
    public void buildPluginMenu()
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
        
        openExternalPlugin.addActionListener(arg0 -> openExternalPlugin());
        codeSequenceDiagram.addActionListener(arg0 -> CodeSequenceDiagram.open());
        maliciousCodeScanner.addActionListener(e -> MaliciousCodeScannerOptions.open());
        showMainMethods.addActionListener(e -> PluginManager.runPlugin(new ShowMainMethods()));
        showAllStrings.addActionListener(e -> PluginManager.runPlugin(new ShowAllStrings()));
        replaceStrings.addActionListener(arg0 -> ReplaceStringsOptions.open());
        stackFramesRemover.addActionListener(e -> PluginManager.runPlugin(new StackFramesRemover()));
        allatoriStringDecrypter.addActionListener(e -> PluginManager.runPlugin(new AllatoriStringDecrypter()));
        ZKMStringDecrypter.addActionListener(e -> PluginManager.runPlugin(new ZKMStringDecrypter()));
        zStringArrayDecrypter.addActionListener(arg0 -> PluginManager.runPlugin(new ZStringArrayDecrypter()));
    }
    
    public void buildObfuscateMenu()
    {
        //hide obfuscation menu since it's currently not being used
        obfuscate.setVisible(false);
        
        rootMenu.add(obfuscate);
        obfuscate.add(strongObf);
        obfuscate.add(lightObf);
        obfuscate.add(new JSeparator());
        obfuscate.add(moveAllClassesIntoRoot);
        obfuscate.add(renameFields);
        obfuscate.add(renameMethods);
        obfuscate.add(renameClasses);
        obfuscate.add(controlFlow);
        obfuscate.add(junkCode);
        
        obfuscatorGroup.add(strongObf);
        obfuscatorGroup.add(lightObf);
        obfuscatorGroup.setSelected(strongObf.getModel(), true);
    
        renameFields.addActionListener(arg0 -> RenameFields.open());
        renameClasses.addActionListener(arg0 -> RenameClasses.open());
        renameMethods.addActionListener(arg0 -> RenameMethods.open());
    }
    
    public void defaultSettings()
    {
        compileOnSave.setSelected(false);
        autoCompileOnRefresh.setSelected(false);
        decodeAPKResources.setSelected(true);
        updateCheck.setSelected(true);
        forcePureAsciiAsText.setSelected(true);
        showSyntheticMembers.setSelected(true);
        
        showFileInTabTitle.setSelected(false);
        showClassMethods.setSelected(false);
    
        simplifyNameInTabTitle.setEnabled(true);
    
        moveAllClassesIntoRoot.setEnabled(false);
        controlFlow.setEnabled(false);
        junkCode.setEnabled(false);
        
        // cfr
        decodeEnumSwitch.setSelected(true);
        sugarEnums.setSelected(true);
        decodeStringSwitch.setSelected(true);
        arrayiter.setSelected(true);
        collectioniter.setSelected(true);
        innerClasses.setSelected(true);
        removeBoilerPlate.setSelected(true);
        removeInnerClassSynthetics.setSelected(true);
        decodeLambdas.setSelected(true);
        hideBridgeMethods.setSelected(true);
        liftConstructorInit.setSelected(true);
        removeDeadMethods.setSelected(true);
        removeBadGenerics.setSelected(true);
        sugarAsserts.setSelected(true);
        sugarBoxing.setSelected(true);
        showVersion.setSelected(true);
        decodeFinally.setSelected(true);
        tidyMonitors.setSelected(true);
        lenient.setSelected(false);
        dumpClassPath.setSelected(false);
        comments.setSelected(true);
        forceTopSort.setSelected(true);
        forceTopSortAggress.setSelected(true);
        forceExceptionPrune.setSelected(true);
        stringBuffer.setSelected(false);
        stringBuilder.setSelected(true);
        silent.setSelected(true);
        recover.setSelected(true);
        eclipse.setSelected(true);
        override.setSelected(true);
        showInferrable.setSelected(true);
        aexagg.setSelected(true);
        forceCondPropagate.setSelected(true);
        hideUTF.setSelected(true);
        hideLongStrings.setSelected(false);
        commentMonitor.setSelected(false);
        allowCorrecting.setSelected(true);
        labelledBlocks.setSelected(true);
        j14ClassOBJ.setSelected(false);
        hideLangImports.setSelected(true);
        recoveryTypeClash.setSelected(true);
        recoveryTypehInts.setSelected(true);
        forceTurningIFs.setSelected(true);
        forLoopAGGCapture.setSelected(true);
        
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
        dc4.setSelected(true);
        nns.setSelected(true);
        ner.setSelected(true);
        bto.setSelected(true);
        rgn.setSelected(true);
        rer.setSelected(true);
        hes.setSelected(true);
        hdc.setSelected(true);
        
        //CFIDE
        debugHelpers.setSelected(true);
        appendBracketsToLabels.setSelected(true);
    }
    
    public void calledAfterLoad() {
        deleteForeignOutdatedLibs.setSelected(Configuration.deleteForeignLibraries);
    }
    
    public synchronized void updateBusyStatus(final boolean busy) {
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

    public void openClassFile(final FileContainer container, final String name, final ClassNode cn) {
        workPane.addClassResource(container, name, cn);
    }

    public void openFile(final FileContainer container, final String name, byte[] content) {
        workPane.addFileResource(container, name, content);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getComponent(final Class<T> clazz) {
        for (final VisibleComponent vc : uiComponents) {
            if (vc.getClass() == clazz)
                return (T) vc;
        }
        return null;
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
    
    public void reloadResources()
    {
        MultipleChoiceDialogue dialogue = new MultipleChoiceDialogue("Bytecode Viewer - Reload Resources",
                "Are you sure you wish to reload the resources?",
                new String[]{"Yes", "No"});
    
        if (dialogue.promptChoice() == 0)
        {
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
    
    public void selectFile()
    {
        final JFileChooser fc = new FileChooser(new File(Configuration.lastDirectory),
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
                new ExceptionUI(e1);
            }
        }
    }

    public void selectPythonC() {
        final JFileChooser fc = new FileChooser(new File(Configuration.lastDirectory),
                "Select Python 2.7 Executable",
                "Python (Or PyPy for speed) 2.7 Executable",
                "everything");
        
        int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);
        if (returnVal == JFileChooser.APPROVE_OPTION)
            try {
                Configuration.lastDirectory = fc.getSelectedFile().getAbsolutePath();
                Configuration.python = fc.getSelectedFile().getAbsolutePath();
            } catch (Exception e1) {
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
            }
    }

    public void selectJavac() {
        final JFileChooser fc = new FileChooser(new File(Configuration.lastDirectory),
                "Select Javac Executable",
                "Javac Executable (Requires JDK  'C:/programfiles/Java/JDK_xx/bin/javac.exe)",
                "everything");
        
        int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);
        if (returnVal == JFileChooser.APPROVE_OPTION)
            try {
                Configuration.lastDirectory = fc.getSelectedFile().getAbsolutePath();
                Configuration.javac = fc.getSelectedFile().getAbsolutePath();
            } catch (Exception e1) {
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
            }
    }

    public void selectJava() {
        final JFileChooser fc = new FileChooser(new File(Configuration.lastDirectory),
                "Select Java Executable",
                "Java Executable (Inside Of JRE/JDK 'C:/programfiles/Java/JDK_xx/bin/java.exe')",
                "everything");
        
        int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);
        if (returnVal == JFileChooser.APPROVE_OPTION)
            try {
                Configuration.lastDirectory = fc.getSelectedFile().getAbsolutePath();
                Configuration.java = fc.getSelectedFile().getAbsolutePath();
            } catch (Exception e1) {
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
            }
    }

    public void selectPythonC3() {
        final JFileChooser fc = new FileChooser(new File(Configuration.lastDirectory),
                "Select Python 3.x Executable",
                "Python (Or PyPy for speed) 3.x Executable",
                "everything");
        
        int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);
        if (returnVal == JFileChooser.APPROVE_OPTION)
            try {
                Configuration.lastDirectory = fc.getSelectedFile().getAbsolutePath();
                Configuration.python3 = fc.getSelectedFile().getAbsolutePath();
            } catch (Exception e1) {
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
            }
    }

    public void selectOpenalLibraryFolder() {
        final JFileChooser fc = new FileChooser(new File(Configuration.lastDirectory),
                "Select Library Folder",
                "Optional Library Folder",
                "everything");
        
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setFileHidingEnabled(false);
        fc.setAcceptAllFileFilterUsed(false);
        
        int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);
        if (returnVal == JFileChooser.APPROVE_OPTION)
            try {
                Configuration.lastDirectory = fc.getSelectedFile().getAbsolutePath();
                Configuration.library = fc.getSelectedFile().getAbsolutePath();
            } catch (Exception e1) {
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
            }
    }
    
    public void selectJRERTLibrary() {
        final JFileChooser fc = new FileChooser(new File(Configuration.lastDirectory),
                "Select JRE RT Jar",
                "JRE RT Library",
                "everything");
        
        int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);
        if (returnVal == JFileChooser.APPROVE_OPTION)
            try {
                Configuration.lastDirectory = fc.getSelectedFile().getAbsolutePath();
                Configuration.rt = fc.getSelectedFile().getAbsolutePath();
            } catch (Exception e1) {
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
            }
    }
    
    public void openExternalPlugin()
    {
        JFileChooser fc = new FileChooser(new File(Configuration.lastDirectory),
                "Select External Plugin",
                "External Plugin",
                "everything");
        fc.setFileFilter(PluginManager.fileFilter());
        
        int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);
        if (returnVal == JFileChooser.APPROVE_OPTION)
            try {
                Configuration.lastDirectory = fc.getSelectedFile().getAbsolutePath();
                BytecodeViewer.viewer.updateBusyStatus(true);
                BytecodeViewer.startPlugin(fc.getSelectedFile());
                BytecodeViewer.viewer.updateBusyStatus(false);
            } catch (Exception e1) {
                new ExceptionUI(e1);
            }
    }
    
    public void askBeforeExiting()
    {
        MultipleChoiceDialogue dialogue = new MultipleChoiceDialogue("Bytecode Viewer - Exit",
                "Are you sure you want to exit?",
                new String[]{"Yes", "No"});
    
        if (dialogue.promptChoice() == 0)
        {
            Configuration.canExit = true;
            System.exit(0);
        }
    }
    
    public void showForeignLibraryWarning()
    {
        if (!deleteForeignOutdatedLibs.isSelected())
        {
            BytecodeViewer.showMessage("WARNING: With this being toggled off outdated libraries will NOT be "
                    + "removed. It's also a security issue. ONLY TURN IT OFF IF YOU KNOW WHAT YOU'RE DOING.");
        }
        
        Configuration.deleteForeignLibraries = deleteForeignOutdatedLibs.isSelected();
    }
    
    public static final long serialVersionUID = 1851409230530948543L;
}