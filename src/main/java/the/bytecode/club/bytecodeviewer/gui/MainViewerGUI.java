package the.bytecode.club.bytecodeviewer.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.SettingsSerializer;
import the.bytecode.club.bytecodeviewer.gui.components.AboutWindow;
import the.bytecode.club.bytecodeviewer.gui.components.FileChooser;
import the.bytecode.club.bytecodeviewer.gui.components.MultipleChoiceDialog;
import the.bytecode.club.bytecodeviewer.gui.components.RunOptions;
import the.bytecode.club.bytecodeviewer.gui.components.SettingsDialog;
import the.bytecode.club.bytecodeviewer.gui.components.VisibleComponent;
import the.bytecode.club.bytecodeviewer.gui.components.WaitBusyIcon;
import the.bytecode.club.bytecodeviewer.gui.plugins.MaliciousCodeScannerOptions;
import the.bytecode.club.bytecodeviewer.gui.plugins.ReplaceStringsOptions;
import the.bytecode.club.bytecodeviewer.gui.resourcelist.ResourceListPane;
import the.bytecode.club.bytecodeviewer.gui.resourcesearch.SearchBoxPane;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.DecompilerSelectionPane;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.Workspace;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ClassViewer;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ResourceViewer;
import the.bytecode.club.bytecodeviewer.gui.theme.LAFTheme;
import the.bytecode.club.bytecodeviewer.gui.theme.RSTATheme;
import the.bytecode.club.bytecodeviewer.obfuscators.rename.RenameClasses;
import the.bytecode.club.bytecodeviewer.obfuscators.rename.RenameFields;
import the.bytecode.club.bytecodeviewer.obfuscators.rename.RenameMethods;
import the.bytecode.club.bytecodeviewer.plugin.PluginManager;
import the.bytecode.club.bytecodeviewer.plugin.PluginTemplate;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.AllatoriStringDecrypter;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.ChangeClassFileVersions;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.CodeSequenceDiagram;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.ShowAllStrings;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.ShowMainMethods;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.StackFramesRemover;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.ViewAPKAndroidPermissions;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.ViewManifest;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.ZKMStringDecrypter;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.ZStringArrayDecrypter;
import the.bytecode.club.bytecodeviewer.resources.ExternalResources;
import the.bytecode.club.bytecodeviewer.resources.IconResources;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.resources.ResourceDecompiling;
import the.bytecode.club.bytecodeviewer.resources.exporting.Export;
import the.bytecode.club.bytecodeviewer.translation.Language;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponents;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJCheckBoxMenuItem;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJMenu;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJMenuItem;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJRadioButtonMenuItem;
import the.bytecode.club.bytecodeviewer.util.DialogUtils;
import the.bytecode.club.bytecodeviewer.util.KeyEventDispatch;
import the.bytecode.club.bytecodeviewer.util.LazyNameUtil;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;
import the.bytecode.club.bytecodeviewer.util.WindowClosingAdapter;
import the.bytecode.club.bytecodeviewer.util.WindowStateChangeAdapter;

import static the.bytecode.club.bytecodeviewer.Configuration.useNewSettingsDialog;
import static the.bytecode.club.bytecodeviewer.Constants.VERSION;
import static the.bytecode.club.bytecodeviewer.Constants.fs;

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
    public boolean isMaximized;
    public final List<JMenuItem> waitIcons = new ArrayList<>();
    
    //main UI components
    public final List<VisibleComponent> uiComponents = new ArrayList<>();
    public final Workspace workPane = new Workspace();
    public final ResourceListPane resourcePane = new ResourceListPane();
    public final SearchBoxPane searchBoxPane = new SearchBoxPane();
    public JSplitPane splitPane1;
    public JSplitPane splitPane2;
    
    //the root menu bar
    public final JMenuBar rootMenu = new JMenuBar();
    
    //all of the files main menu components
    public final JMenu fileMainMenu = new TranslatedJMenu("File", TranslatedComponents.FILE);
    public final JMenuItem addResource = new TranslatedJMenuItem("Add...", TranslatedComponents.ADD);
    public final JMenuItem newWorkSpace = new TranslatedJMenuItem("New Workspace", TranslatedComponents.NEW_WORKSPACE);
    public final JMenuItem reloadResources = new TranslatedJMenuItem("Reload Resources", TranslatedComponents.RELOAD_RESOURCES);
    public final JMenuItem runButton = new TranslatedJMenuItem("Run", TranslatedComponents.RUN);
    public final JMenuItem compileButton = new TranslatedJMenuItem("Compile", TranslatedComponents.COMPILE);
    public final JMenuItem saveAsRunnableJar = new TranslatedJMenuItem("Save As Runnable Jar..", TranslatedComponents.SAVE_AS_RUNNABLE_JAR);
    public final JMenuItem saveAsDex = new TranslatedJMenuItem("Save As DEX..", TranslatedComponents.SAVE_AS_DEX);
    public final JMenuItem saveAsAPK = new TranslatedJMenuItem("Save As APK..", TranslatedComponents.SAVE_AS_APK);
    public final JMenuItem saveAsZip = new TranslatedJMenuItem("Save As Zip..", TranslatedComponents.SAVE_AS_ZIP);
    public final JMenuItem decompileSaveOpened = new TranslatedJMenuItem("Decompile & Save Opened Class..", TranslatedComponents.DECOMPILE_SAVE_OPENED_CLASSES);
    public final JMenuItem decompileSaveAll = new TranslatedJMenuItem("Decompile & Save All Classes..", TranslatedComponents.DECOMPILE_SAVE_ALL_CLASSES);
    public final JMenu recentFilesSecondaryMenu = new TranslatedJMenu("Recent Files", TranslatedComponents.RECENT_FILES);
    public final JMenuItem about = new TranslatedJMenuItem("About", TranslatedComponents.ABOUT);
    public final JMenuItem exit = new TranslatedJMenuItem("Exit", TranslatedComponents.EXIT);
    
    //all of the view main menu components
    public final JMenu viewMainMenu = new TranslatedJMenu("View", TranslatedComponents.VIEW);
    public final DecompilerSelectionPane viewPane1 = new DecompilerSelectionPane(1);
    public final DecompilerSelectionPane viewPane2 = new DecompilerSelectionPane(2);
    public final DecompilerSelectionPane viewPane3 = new DecompilerSelectionPane(3);
    
    //all of the plugins main menu components
    public final JMenu pluginsMainMenu = new TranslatedJMenu("Plugins", TranslatedComponents.PLUGINS);
    public final JMenuItem openExternalPlugin = new TranslatedJMenuItem("Open Plugin...", TranslatedComponents.OPEN_PLUGIN);
    public final JMenu recentPluginsSecondaryMenu = new TranslatedJMenu("Recent Plugins", TranslatedComponents.RECENT_PLUGINS);
    public final JMenuItem newJavaPlugin = new TranslatedJMenuItem("New Java Plugin...", TranslatedComponents.NEW_JAVA_PLUGIN);
    public final JMenuItem newJavascriptPlugin = new TranslatedJMenuItem("New Javascript Plugin...", TranslatedComponents.NEW_JAVASCRIPT_PLUGIN);
    public final JMenuItem codeSequenceDiagram = new TranslatedJMenuItem("Code Sequence Diagram", TranslatedComponents.CODE_SEQUENCE_DIAGRAM);
    public final JMenuItem maliciousCodeScanner = new TranslatedJMenuItem("Malicious Code Scanner", TranslatedComponents.MALICIOUS_CODE_SCANNER);
    public final JMenuItem showAllStrings = new TranslatedJMenuItem("Show All Strings", TranslatedComponents.SHOW_ALL_STRINGS);
    public final JMenuItem showMainMethods = new TranslatedJMenuItem("Show Main Methods", TranslatedComponents.SHOW_MAIN_METHODS);
    public final JMenuItem replaceStrings = new TranslatedJMenuItem("Replace Strings", TranslatedComponents.REPLACE_STRINGS);
    public final JMenuItem stackFramesRemover = new TranslatedJMenuItem("StackFrames Remover", TranslatedComponents.STACK_FRAMES_REMOVER);
    public final JMenuItem ZKMStringDecrypter = new TranslatedJMenuItem("ZKM String Decrypter", TranslatedComponents.ZKM_STRING_DECRYPTER);
    public final JMenuItem allatoriStringDecrypter = new TranslatedJMenuItem("Allatori String Decrypter", TranslatedComponents.ALLATORI_STRING_DECRYPTER);
    public final JMenuItem zStringArrayDecrypter = new TranslatedJMenuItem("ZStringArray Decrypter", TranslatedComponents.ZSTRINGARRAY_DECRYPTER);
    public final JMenuItem viewAPKAndroidPermissions = new TranslatedJMenuItem("View Android Permissions", TranslatedComponents.VIEW_ANDROID_PERMISSIONS);
    public final JMenuItem viewManifest = new TranslatedJMenuItem("View Manifest", TranslatedComponents.VIEW_MANIFEST);
    public final JMenuItem changeClassFileVersions = new TranslatedJMenuItem("Change ClassFile Versions", TranslatedComponents.CHANGE_CLASSFILE_VERSIONS);
    
    //all of the settings main menu components
    public final JMenu rstaTheme = new TranslatedJMenu("Text Area Theme", TranslatedComponents.TEXT_AREA_THEME);
    public final JMenuItem rstaThemeSettings = new TranslatedJMenuItem("Text Area Theme", TranslatedComponents.TEXT_AREA_THEME);
    public SettingsDialog rstaThemeSettingsDialog;
    public final JMenu lafTheme = new TranslatedJMenu("Window Theme", TranslatedComponents.WINDOW_THEME);
    public final JMenuItem lafThemeSettings = new TranslatedJMenuItem("Window Theme", TranslatedComponents.WINDOW_THEME);
    public SettingsDialog lafThemeSettingsDialog;
    public final JMenu language = new TranslatedJMenu("Language", TranslatedComponents.LANGUAGE);
    public final JMenuItem languageSettings = new TranslatedJMenuItem("Language", TranslatedComponents.LANGUAGE);
    public SettingsDialog languageSettingsDialog;
    public final JMenu fontSize = new TranslatedJMenu("Font Size", TranslatedComponents.FONT_SIZE);
    public final JSpinner fontSpinner = new JSpinner();
    public final Map<RSTATheme, JRadioButtonMenuItem> rstaThemes = new HashMap<>();
    public final Map<LAFTheme, JRadioButtonMenuItem> lafThemes = new HashMap<>();
    public final Map<Language, JRadioButtonMenuItem> languages = new HashMap<>();
    
    //BCV settings
    public final JCheckBoxMenuItem refreshOnChange = new TranslatedJCheckBoxMenuItem("Refresh On View Change", TranslatedComponents.REFRESH_ON_VIEW_CHANGE);
    private final JCheckBoxMenuItem deleteForeignOutdatedLibs = new TranslatedJCheckBoxMenuItem("Delete Foreign/Outdated Libs", TranslatedComponents.DELETE_UNKNOWN_LIBS);
    public final JMenu settingsMainMenu = new TranslatedJMenu("Settings", TranslatedComponents.SETTINGS);
    public final JMenu visualSettings = new TranslatedJMenu("Visual Settings", TranslatedComponents.VISUAL_SETTINGS);
    public final JCheckBoxMenuItem updateCheck = new TranslatedJCheckBoxMenuItem("Update Check", TranslatedComponents.UPDATE_CHECK);
    public final JMenuItem setPython2 = new TranslatedJMenuItem("Set Python 2.7 Executable", TranslatedComponents.SET_PYTHON_27_EXECUTABLE);
    public final JMenuItem setPython3 = new TranslatedJMenuItem("Set Python 3.X Executable", TranslatedComponents.SET_PYTHON_30_EXECUTABLE);
    public final JMenuItem setJRERT = new TranslatedJMenuItem("Set JRE RT Library", TranslatedComponents.SET_JRE_RT_LIBRARY);
    public final JMenuItem setJavac = new TranslatedJMenuItem("Set Javac Executable", TranslatedComponents.SET_JAVAC_EXECUTABLE);
    public final JMenuItem setOptionalLibrary = new TranslatedJMenuItem("Set Optional Library Folder", TranslatedComponents.SET_OPTIONAL_LIBRARY_FOLDER);
    public final JCheckBoxMenuItem compileOnSave = new TranslatedJCheckBoxMenuItem("Compile On Save", TranslatedComponents.COMPILE_ON_SAVE);
    public final JCheckBoxMenuItem showFileInTabTitle = new TranslatedJCheckBoxMenuItem("Show File In Tab Title", TranslatedComponents.SHOW_TAB_FILE_IN_TAB_TITLE);
    public final JCheckBoxMenuItem simplifyNameInTabTitle = new TranslatedJCheckBoxMenuItem("Simplify Name In Tab Title", TranslatedComponents.SIMPLIFY_NAME_IN_TAB_TITLE);
    public final JCheckBoxMenuItem forcePureAsciiAsText = new TranslatedJCheckBoxMenuItem("Force Pure Ascii As Text", TranslatedComponents.FORCE_PURE_ASCII_AS_TEXT);
    public final JCheckBoxMenuItem autoCompileOnRefresh = new TranslatedJCheckBoxMenuItem("Compile On Refresh", TranslatedComponents.COMPILE_ON_REFRESH);
    public final JCheckBoxMenuItem decodeAPKResources = new TranslatedJCheckBoxMenuItem("Decode APK Resources", TranslatedComponents.DECODE_APK_RESOURCES);
    public final JCheckBoxMenuItem synchronizedViewing = new TranslatedJCheckBoxMenuItem("Synchronized Viewing", TranslatedComponents.SYNCHRONIZED_VIEWING);
    public final JCheckBoxMenuItem showClassMethods = new TranslatedJCheckBoxMenuItem("Show Class Methods", TranslatedComponents.SHOW_CLASS_METHODS);
    
    //apk conversion settings
    public final JMenu apkConversionSecondaryMenu = new TranslatedJMenu("APK Conversion/Decoding", TranslatedComponents.APK_CONVERSION_DECODING);
    public final JMenuItem apkConversionSettings = new TranslatedJMenuItem("APK Conversion/Decoding", TranslatedComponents.APK_CONVERSION_DECODING);
    public SettingsDialog apkConversionSettingsDialog;
    public final ButtonGroup apkConversionGroup = new ButtonGroup();
    public final JRadioButtonMenuItem apkConversionDex = new JRadioButtonMenuItem("Dex2Jar");
    public final JRadioButtonMenuItem apkConversionEnjarify = new JRadioButtonMenuItem("Enjarify");
    
    //CFIDE settings
    public final JMenu bytecodeDecompilerSettingsSecondaryMenu = new TranslatedJMenu("Bytecode Decompiler", TranslatedComponents.BYTECODE_DECOMPILER);
    public final JMenuItem bytecodeDecompilerSettings = new TranslatedJMenuItem("Bytecode Decompiler", TranslatedComponents.BYTECODE_DECOMPILER);
    public SettingsDialog bytecodeDecompilerSettingsDialog;
    public final JCheckBoxMenuItem appendBracketsToLabels = new TranslatedJCheckBoxMenuItem("Append Brackets To Labels", TranslatedComponents.APPEND_BRACKETS_TO_LABEL);
    public JCheckBoxMenuItem debugHelpers = new TranslatedJCheckBoxMenuItem("Debug Helpers", TranslatedComponents.DEBUG_HELPERS);
    public final JCheckBoxMenuItem printLineNumbers = new TranslatedJCheckBoxMenuItem("Print Line Numbers", TranslatedComponents.PRINT_LINE_NUMBERS);
    
    //FernFlower settings
    public final JMenu fernFlowerSettingsSecondaryMenu = new TranslatedJMenu("FernFlower Settings", TranslatedComponents.FERNFLOWER_SETTINGS);
    public final JMenuItem fernFlowerSettings = new TranslatedJMenuItem("FernFlower Settings", TranslatedComponents.FERNFLOWER_SETTINGS);
    public SettingsDialog fernFlowerSettingsDialog;
    public TranslatedJCheckBoxMenuItem rbr = new TranslatedJCheckBoxMenuItem("Hide bridge methods", TranslatedComponents.HIDE_BRIDGE_METHODS);
    public TranslatedJCheckBoxMenuItem rsy = new TranslatedJCheckBoxMenuItem("Hide synthetic class members", TranslatedComponents.HIDE_SYNTHETIC_CLASS_MEMBERS);
    public TranslatedJCheckBoxMenuItem din = new TranslatedJCheckBoxMenuItem("Decompile inner classes", TranslatedComponents.DECOMPILE_INNER_CLASSES);
    public TranslatedJCheckBoxMenuItem dc4 = new TranslatedJCheckBoxMenuItem("Collapse 1.4 class references", TranslatedComponents.COLLAPSE_14_CLASS_REFERENCES);
    public TranslatedJCheckBoxMenuItem das = new TranslatedJCheckBoxMenuItem("Decompile assertions", TranslatedComponents.DECOMPILE_ASSERTIONS);
    public TranslatedJCheckBoxMenuItem hes = new TranslatedJCheckBoxMenuItem("Hide empty super invocation", TranslatedComponents.HIDE_EMPTY_SUPER_INVOCATION);
    public TranslatedJCheckBoxMenuItem hdc = new TranslatedJCheckBoxMenuItem("Hide empty default constructor", TranslatedComponents.HIDE_EMPTY_DEFAULT_CONSTRUCTOR);
    public TranslatedJCheckBoxMenuItem dgs = new TranslatedJCheckBoxMenuItem("Decompile generic signatures", TranslatedComponents.DECOMPILE_GENERIC_SIGNATURES);
    public TranslatedJCheckBoxMenuItem ner = new TranslatedJCheckBoxMenuItem("Assume return not throwing exceptions", TranslatedComponents.ASSUME_RETURN_NOT_THROWING_EXCEPTIONS);
    public TranslatedJCheckBoxMenuItem den = new TranslatedJCheckBoxMenuItem("Decompile enumerations", TranslatedComponents.DECOMPILE_ENUMERATIONS);
    public TranslatedJCheckBoxMenuItem rgn = new TranslatedJCheckBoxMenuItem("Remove getClass() invocation", TranslatedComponents.REMOVE_GETCLASS_INVOCATION);
    public TranslatedJCheckBoxMenuItem bto = new TranslatedJCheckBoxMenuItem("Interpret int 1 as boolean true", TranslatedComponents.INTERPRET_INT_1_AS_BOOLEAN_TRUE);
    public TranslatedJCheckBoxMenuItem nns = new TranslatedJCheckBoxMenuItem("Allow for not set synthetic attribute", TranslatedComponents.ALLOW_FOR_NOT_SET_SYNTHETIC_ATTRIBUTE);
    public TranslatedJCheckBoxMenuItem uto = new TranslatedJCheckBoxMenuItem("Consider nameless types as java.lang.Object", TranslatedComponents.CONSIDER_NAMELESS_TYPES_AS_JAVALANGOBJECT);
    public TranslatedJCheckBoxMenuItem udv = new TranslatedJCheckBoxMenuItem("Reconstruct variable names from debug info", TranslatedComponents.RECONSTRUCT_VARIABLE_NAMES_FROM_DEBUG_INFO);
    public TranslatedJCheckBoxMenuItem rer = new TranslatedJCheckBoxMenuItem("Remove empty exception ranges", TranslatedComponents.REMOVE_EMPTY_EXCEPTION_RANGES);
    public TranslatedJCheckBoxMenuItem fdi = new TranslatedJCheckBoxMenuItem("Deinline finally structures", TranslatedComponents.DEINLINE_FINALLY_STRUCTURES);
    public TranslatedJCheckBoxMenuItem asc = new TranslatedJCheckBoxMenuItem("Allow only ASCII characters in strings", TranslatedComponents.ALLOW_ONLY_ASCII_CHARACTERS_IN_STRINGS);
    public TranslatedJCheckBoxMenuItem ren = new TranslatedJCheckBoxMenuItem("Rename ambiguous classes and class elements", TranslatedComponents.RENAME_AMBIGUOUS_CLASSES_AND_CLASS_ELEMENTS);
    
    //Procyon
    public final JMenu procyonSettingsSecondaryMenu = new TranslatedJMenu("Procyon Settings", TranslatedComponents.PROCYON_SETTINGS);
    public final JMenuItem procyonSettings = new TranslatedJMenuItem("Procyon Settings", TranslatedComponents.PROCYON_SETTINGS);
    public SettingsDialog procyonSettingsDialog;
    public final JCheckBoxMenuItem alwaysGenerateExceptionVars = new TranslatedJCheckBoxMenuItem("Always Generate Exception Variable For Catch Blocks", TranslatedComponents.ALWAYS_GENERATE_EXCEPTION_VARIABLE_FOR_CATCH_BLOCKS);
    public final JCheckBoxMenuItem excludeNestedTypes = new TranslatedJCheckBoxMenuItem("Exclude Nested Types", TranslatedComponents.EXCLUDE_NESTED_TYPES);
    public final JCheckBoxMenuItem showDebugLineNumbers = new TranslatedJCheckBoxMenuItem("Show Debug Line Numbers", TranslatedComponents.SHOW_DEBUG_LINE_NUMBERS);
    public final JCheckBoxMenuItem includeLineNumbersInBytecode = new TranslatedJCheckBoxMenuItem("Include Line Numbers In Bytecode", TranslatedComponents.INCLUDE_LINE_NUMBERS_IN_BYTECODE);
    public final JCheckBoxMenuItem includeErrorDiagnostics = new TranslatedJCheckBoxMenuItem("Include Error Diagnostics", TranslatedComponents.INCLUDE_ERROR_DIAGNOSTICS);
    public final JCheckBoxMenuItem showSyntheticMembers = new TranslatedJCheckBoxMenuItem("Show Synthetic Members", TranslatedComponents.SHOW_SYNTHETIC_MEMBERS);
    public final JCheckBoxMenuItem simplifyMemberReferences = new TranslatedJCheckBoxMenuItem("Simplify Member References", TranslatedComponents.SIMPLIFY_MEMBER_REFERENCES);
    public final JCheckBoxMenuItem mergeVariables = new TranslatedJCheckBoxMenuItem("Merge Variables", TranslatedComponents.MERGE_VARIABLES);
    public final JCheckBoxMenuItem forceExplicitTypeArguments = new TranslatedJCheckBoxMenuItem("Force Explicit Type Arguments", TranslatedComponents.FORCE_EXPLICIT_TYPE_ARGUMENTS);
    public final JCheckBoxMenuItem forceExplicitImports = new TranslatedJCheckBoxMenuItem("Force Explicit Imports", TranslatedComponents.FORCE_EXPLICIT_IMPORTS);
    public final JCheckBoxMenuItem flattenSwitchBlocks = new TranslatedJCheckBoxMenuItem("Flatten Switch Blocks", TranslatedComponents.FLATTEN_SWITCH_BLOCKS);
    public final JCheckBoxMenuItem retainPointlessSwitches = new TranslatedJCheckBoxMenuItem("Retain Pointless Switches", TranslatedComponents.RETAIN_POINTLESS_SWITCHES);
    public final JCheckBoxMenuItem retainRedunantCasts = new TranslatedJCheckBoxMenuItem("Retain Redundant Casts", TranslatedComponents.RETAIN_REDUNDANT_CASTS);
    public final JCheckBoxMenuItem unicodeOutputEnabled = new TranslatedJCheckBoxMenuItem("Unicode Output Enabled", TranslatedComponents.UNICODE_OUTPUT_ENABLED);
    
    //CFR
    public final JMenu cfrSettingsSecondaryMenu = new TranslatedJMenu("CFR Settings", TranslatedComponents.CFR_SETTINGS);
    public final JMenuItem cfrSettings = new TranslatedJMenuItem("CFR Settings", TranslatedComponents.CFR_SETTINGS);
    public SettingsDialog cfrSettingsDialog;
    public final JCheckBoxMenuItem decodeEnumSwitch = new TranslatedJCheckBoxMenuItem("Decode Enum Switch", TranslatedComponents.DECODE_ENUM_SWITCH);
    public final JCheckBoxMenuItem sugarEnums = new TranslatedJCheckBoxMenuItem("SugarEnums", TranslatedComponents.SUGARENUMS);
    public final JCheckBoxMenuItem decodeStringSwitch = new TranslatedJCheckBoxMenuItem("Decode String Switch", TranslatedComponents.DECODE_STRING_SWITCH);
    public final JCheckBoxMenuItem arrayiter = new TranslatedJCheckBoxMenuItem("Arrayiter", TranslatedComponents.ARRAYITER);
    public final JCheckBoxMenuItem collectioniter = new TranslatedJCheckBoxMenuItem("Collectioniter", TranslatedComponents.COLLECTIONITER);
    public final JCheckBoxMenuItem innerClasses = new TranslatedJCheckBoxMenuItem("Inner Classes", TranslatedComponents.INNER_CLASSES);
    public final JCheckBoxMenuItem removeBoilerPlate = new TranslatedJCheckBoxMenuItem("Remove Boiler Plate", TranslatedComponents.REMOVE_BOILER_PLATE);
    public final JCheckBoxMenuItem removeInnerClassSynthetics = new TranslatedJCheckBoxMenuItem("Remove Inner Class Synthetics", TranslatedComponents.REMOVE_INNER_CLASS_SYNTHETICS);
    public final JCheckBoxMenuItem decodeLambdas = new TranslatedJCheckBoxMenuItem("Decode Lambdas", TranslatedComponents.DECODE_LAMBDAS);
    public final JCheckBoxMenuItem hideBridgeMethods = new TranslatedJCheckBoxMenuItem("Hide Bridge Methods", TranslatedComponents.HIDE_BRIDGE_METHODS);
    public final JCheckBoxMenuItem liftConstructorInit = new TranslatedJCheckBoxMenuItem("Lift  Constructor Init", TranslatedComponents.LIFT__CONSTRUCTOR_INIT);
    public final JCheckBoxMenuItem removeDeadMethods = new TranslatedJCheckBoxMenuItem("Remove Dead Methods", TranslatedComponents.REMOVE_DEAD_METHODS);
    public final JCheckBoxMenuItem removeBadGenerics = new TranslatedJCheckBoxMenuItem("Remove Bad Generics", TranslatedComponents.REMOVE_BAD_GENERICS);
    public final JCheckBoxMenuItem sugarAsserts = new TranslatedJCheckBoxMenuItem("Sugar Asserts", TranslatedComponents.SUGAR_ASSERTS);
    public final JCheckBoxMenuItem sugarBoxing = new TranslatedJCheckBoxMenuItem("Sugar Boxing", TranslatedComponents.SUGAR_BOXING);
    public final JCheckBoxMenuItem showVersion = new TranslatedJCheckBoxMenuItem("Show Version", TranslatedComponents.SHOW_VERSION);
    public final JCheckBoxMenuItem decodeFinally = new TranslatedJCheckBoxMenuItem("Decode Finally", TranslatedComponents.DECODE_FINALLY);
    public final JCheckBoxMenuItem tidyMonitors = new TranslatedJCheckBoxMenuItem("Tidy Monitors", TranslatedComponents.TIDY_MONITORS);
    public final JCheckBoxMenuItem lenient = new TranslatedJCheckBoxMenuItem("Lenient", TranslatedComponents.LENIENT);
    public final JCheckBoxMenuItem dumpClassPath = new TranslatedJCheckBoxMenuItem("Dump Classpath", TranslatedComponents.DUMP_CLASSPATH);
    public final JCheckBoxMenuItem comments = new TranslatedJCheckBoxMenuItem("Comments", TranslatedComponents.COMMENTS);
    public final JCheckBoxMenuItem forceTopSort = new TranslatedJCheckBoxMenuItem("Force Top Sort", TranslatedComponents.FORCE_TOP_SORT);
    public final JCheckBoxMenuItem forceTopSortAggress = new TranslatedJCheckBoxMenuItem("Force Top Sort Aggress", TranslatedComponents.FORCE_TOP_SORT_AGGRESS);
    public final JCheckBoxMenuItem forceExceptionPrune = new TranslatedJCheckBoxMenuItem("Force Exception Prune", TranslatedComponents.FORCE_EXCEPTION_PRUNE);
    public final JCheckBoxMenuItem stringBuffer = new TranslatedJCheckBoxMenuItem("String Buffer", TranslatedComponents.STRING_BUFFER);
    public final JCheckBoxMenuItem stringBuilder = new TranslatedJCheckBoxMenuItem("String Builder", TranslatedComponents.STRING_BUILDER);
    public final JCheckBoxMenuItem silent = new TranslatedJCheckBoxMenuItem("Silent", TranslatedComponents.SILENT);
    public final JCheckBoxMenuItem recover = new TranslatedJCheckBoxMenuItem("Recover", TranslatedComponents.RECOVER);
    public final JCheckBoxMenuItem eclipse = new TranslatedJCheckBoxMenuItem("Eclipse", TranslatedComponents.ECLIPSE);
    public final JCheckBoxMenuItem override = new TranslatedJCheckBoxMenuItem("Override", TranslatedComponents.OVERRIDE);
    public final JCheckBoxMenuItem showInferrable = new TranslatedJCheckBoxMenuItem("Show Inferrable", TranslatedComponents.SHOW_INFERRABLE);
    public final JCheckBoxMenuItem aexagg = new TranslatedJCheckBoxMenuItem("Aexagg", TranslatedComponents.AEXAGG);
    public final JCheckBoxMenuItem forceCondPropagate = new TranslatedJCheckBoxMenuItem("Force Cond Propagate", TranslatedComponents.FORCE_COND_PROPAGATE);
    public final JCheckBoxMenuItem hideUTF = new TranslatedJCheckBoxMenuItem("Hide UTF", TranslatedComponents.HIDE_UTF);
    public final JCheckBoxMenuItem hideLongStrings = new TranslatedJCheckBoxMenuItem("Hide Long Strings", TranslatedComponents.HIDE_LONG_STRINGS);
    public final JCheckBoxMenuItem commentMonitor = new TranslatedJCheckBoxMenuItem("Comment Monitors", TranslatedComponents.COMMENT_MONITORS);
    public final JCheckBoxMenuItem allowCorrecting = new TranslatedJCheckBoxMenuItem("Allow Correcting", TranslatedComponents.ALLOW_CORRECTING);
    public final JCheckBoxMenuItem labelledBlocks = new TranslatedJCheckBoxMenuItem("Labelled Blocks", TranslatedComponents.LABELLED_BLOCKS);
    public final JCheckBoxMenuItem j14ClassOBJ = new TranslatedJCheckBoxMenuItem("J14ClassOBJ", TranslatedComponents.J14CLASSOBJ);
    public final JCheckBoxMenuItem hideLangImports = new TranslatedJCheckBoxMenuItem("Hide Lang Imports", TranslatedComponents.HIDE_LANG_IMPORTS);
    public final JCheckBoxMenuItem recoveryTypeClash = new TranslatedJCheckBoxMenuItem("Recover Type Clash", TranslatedComponents.RECOVER_TYPE_CLASH);
    public final JCheckBoxMenuItem recoveryTypehInts = new TranslatedJCheckBoxMenuItem("Recover Type  Hints", TranslatedComponents.RECOVER_TYPE__HINTS);
    public final JCheckBoxMenuItem forceTurningIFs = new TranslatedJCheckBoxMenuItem("Force Returning IFs", TranslatedComponents.FORCE_RETURNING_IFS);
    public final JCheckBoxMenuItem forLoopAGGCapture = new TranslatedJCheckBoxMenuItem("For Loop AGG Capture", TranslatedComponents.FOR_LOOP_AGG_CAPTURE);

    //Smali/D2Jar
    public final JMenu minSdkVersionMenu = new TranslatedJMenu("Minimum SDK version", TranslatedComponents.MIN_SDK_VERSION);
    public final JSpinner minSdkVersionSpinner = new JSpinner();

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
        setIconImages(IconResources.iconList);
        setSize(new Dimension(800, 488));
    
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

        viewPane1.setDefault();
        viewPane2.setDefault();
        viewPane3.setDefault();

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
        newWorkSpace.addActionListener(e -> BytecodeViewer.resetWorkspace(true));
        reloadResources.addActionListener(arg0 -> reloadResources());
        runButton.addActionListener(e -> runResources());
        compileButton.addActionListener(arg0 -> compileOnNewThread());
        saveAsRunnableJar.addActionListener(e -> Export.RUNNABLE_JAR.getExporter().promptForExport());
        saveAsAPK.addActionListener(arg0 -> Export.APK.getExporter().promptForExport());
        saveAsDex.addActionListener(arg0 -> Export.DEX.getExporter().promptForExport());
        saveAsZip.addActionListener(arg0 -> Export.ZIP.getExporter().promptForExport());
        decompileSaveAll.addActionListener(arg0 -> ResourceDecompiling.decompileSaveAll());
        decompileSaveOpened.addActionListener(arg0 -> ResourceDecompiling.decompileSaveOpenedResource());
        about.addActionListener(arg0 -> new AboutWindow().setVisible(true));
        exit.addActionListener(arg0 -> askBeforeExiting());
    }
    
    public void buildViewMenu()
    {
        rootMenu.add(viewMainMenu);
        viewMainMenu.add(visualSettings);
        viewMainMenu.add(viewPane1.getMenu());
        viewMainMenu.add(viewPane2.getMenu());
        viewMainMenu.add(viewPane3.getMenu());
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
        settingsMainMenu.add(updateCheck);
        settingsMainMenu.add(forcePureAsciiAsText);
        settingsMainMenu.add(new JSeparator());
        settingsMainMenu.add(setPython2);
        settingsMainMenu.add(setPython3);
        settingsMainMenu.add(setJRERT);
        settingsMainMenu.add(setOptionalLibrary);
        settingsMainMenu.add(setJavac);
        settingsMainMenu.add(new JSeparator());
        
        //TODO the dialog below works but for 3 options,
        // it might be better to leave it as a secondary menu
        settingsMainMenu.add(apkConversionSecondaryMenu);
        //settingsMainMenu.add(useNewSettingsDialog ? apkConversionSettings : apkConversionMenu);
    
        //Smali minSdkVersion
        minSdkVersionSpinner.setPreferredSize(new Dimension(60, 24));
        minSdkVersionSpinner.setMinimumSize(new Dimension(60, 24));
        minSdkVersionSpinner.setModel(new SpinnerNumberModel(26, 1, null, 1));
        minSdkVersionMenu.add(minSdkVersionSpinner);
        settingsMainMenu.add(minSdkVersionMenu);
        
        settingsMainMenu.add(new JSeparator());
        
        fontSpinner.setPreferredSize(new Dimension(60, 24));
        fontSpinner.setMinimumSize(new Dimension(60, 24));
        fontSpinner.setModel(new SpinnerNumberModel(12, 1, null, 1));
        fontSize.add(fontSpinner);
        
        apkConversionSecondaryMenu.add(decodeAPKResources);
        apkConversionSecondaryMenu.add(apkConversionDex);
        apkConversionSecondaryMenu.add(apkConversionEnjarify);
        apkConversionGroup.add(apkConversionDex);
        apkConversionGroup.add(apkConversionEnjarify);
        apkConversionGroup.setSelected(apkConversionDex.getModel(), true);
        //apkConversionSettingsDialog = new SettingsDialogue(apkConversionSecondaryMenu, new JPanel());
        apkConversionSettings.addActionListener((e)-> apkConversionSettingsDialog.showDialog());
        
        ButtonGroup rstaGroup = new ButtonGroup();
        for (RSTATheme t : RSTATheme.values())
        {
            JRadioButtonMenuItem item = new TranslatedJRadioButtonMenuItem(t.getReadableName(), t.getTranslation());
            if (Configuration.rstaTheme.equals(t))
                item.setSelected(true);
            
            rstaGroup.add(item);
            
            item.addActionListener(e ->
            {
                Configuration.rstaTheme = t;
                item.setSelected(true);
                SettingsSerializer.saveSettingsAsync();
                updateTabTheme();
            });
            
            rstaThemes.put(t, item);
            rstaTheme.add(item);
        }
        
        rstaThemeSettingsDialog = new SettingsDialog(rstaTheme, new JPanel());
        rstaThemeSettings.addActionListener((e)-> rstaThemeSettingsDialog.showDialog());
    
        ButtonGroup lafGroup = new ButtonGroup();
        for (LAFTheme theme : LAFTheme.values())
        {
            JRadioButtonMenuItem item = new TranslatedJRadioButtonMenuItem(theme.getReadableName(), theme.getTranslation());
            if (Configuration.lafTheme.equals(theme))
                item.setSelected(true);
            
            lafGroup.add(item);
            
            item.addActionListener(e ->
            {
                Configuration.lafTheme = theme;
                Configuration.rstaTheme = theme.getRSTATheme();
                rstaThemes.get(Configuration.rstaTheme).setSelected(true);
                item.setSelected(true);
                SettingsSerializer.saveSettingsAsync();
                
                try
                {
                    theme.setLAF();
                    updateTabTheme();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            });
    
            lafThemes.put(theme, item);
            lafTheme.add(item);
        }
        
        lafThemeSettingsDialog = new SettingsDialog(lafTheme, new JPanel());
        lafThemeSettings.addActionListener((e)-> lafThemeSettingsDialog.showDialog());
    
        ButtonGroup languageGroup = new ButtonGroup();
        for (Language l : Language.values())
        {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(l.getReadableName());
            if (Configuration.language.equals(l))
                item.setSelected(true);
    
            languageGroup.add(item);
            
            item.addActionListener(e ->
            {
                SettingsSerializer.saveSettingsAsync();
                MiscUtils.setLanguage(l);
            });
    
            languages.put(l, item);
            language.add(item);
        }
        
        languageSettingsDialog = new SettingsDialog(language, new JPanel());
        languageSettings.addActionListener((e)-> languageSettingsDialog.showDialog());
        
        visualSettings.add(useNewSettingsDialog ? lafThemeSettings : lafTheme);
        visualSettings.add(useNewSettingsDialog ? rstaThemeSettings : rstaTheme);
        visualSettings.add(useNewSettingsDialog ? languageSettings : language);
        visualSettings.add(fontSize);
        visualSettings.add(showFileInTabTitle);
        visualSettings.add(simplifyNameInTabTitle);
        visualSettings.add(synchronizedViewing);
        visualSettings.add(showClassMethods);
        
        //PROCYON SETTINGS
        settingsMainMenu.add(useNewSettingsDialog ? procyonSettings : procyonSettingsSecondaryMenu);
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
        procyonSettingsDialog = new SettingsDialog(procyonSettingsSecondaryMenu, new JPanel());
        procyonSettings.addActionListener((e)-> procyonSettingsDialog.showDialog());
        
        //CFR SETTINGS
        settingsMainMenu.add(useNewSettingsDialog ? cfrSettings : cfrSettingsSecondaryMenu);
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
        cfrSettingsDialog = new SettingsDialog(cfrSettingsSecondaryMenu, new JPanel());
        cfrSettings.addActionListener((e)-> cfrSettingsDialog.showDialog());
        
        //FERNFLOWER SETTINGS
        settingsMainMenu.add(useNewSettingsDialog ? fernFlowerSettings : fernFlowerSettingsSecondaryMenu);
        fernFlowerSettingsSecondaryMenu.add(ren);
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
        fernFlowerSettingsDialog = new SettingsDialog(fernFlowerSettingsSecondaryMenu, new JPanel());
        fernFlowerSettings.addActionListener((e)-> fernFlowerSettingsDialog.showDialog());
        
        //CFIDE SETTINGS
        settingsMainMenu.add(useNewSettingsDialog ? bytecodeDecompilerSettings : bytecodeDecompilerSettingsSecondaryMenu);
        bytecodeDecompilerSettingsSecondaryMenu.add(debugHelpers);
        bytecodeDecompilerSettingsSecondaryMenu.add(appendBracketsToLabels);
        bytecodeDecompilerSettingsSecondaryMenu.add(printLineNumbers);
        bytecodeDecompilerSettingsDialog = new SettingsDialog(bytecodeDecompilerSettingsSecondaryMenu, new JPanel());
        bytecodeDecompilerSettings.addActionListener((e)-> bytecodeDecompilerSettingsDialog.showDialog());
        
        deleteForeignOutdatedLibs.addActionListener(arg0 -> showForeignLibraryWarning());
        forcePureAsciiAsText.addActionListener(arg0 -> SettingsSerializer.saveSettingsAsync());
        setPython2.addActionListener(arg0 -> ExternalResources.getSingleton().selectPython2());
        setJRERT.addActionListener(arg0 -> ExternalResources.getSingleton().selectJRERTLibrary());
        setPython3.addActionListener(arg0 -> ExternalResources.getSingleton().selectPython3());
        setOptionalLibrary.addActionListener(arg0 -> ExternalResources.getSingleton().selectOptionalLibraryFolder());
        setJavac.addActionListener(arg0 -> ExternalResources.getSingleton().selectJavac());
        showFileInTabTitle.addActionListener(arg0 -> {
            Configuration.displayParentInTab = BytecodeViewer.viewer.showFileInTabTitle.isSelected();
            SettingsSerializer.saveSettingsAsync();
            BytecodeViewer.refreshAllTabTitles();
        });
        simplifyNameInTabTitle.addActionListener(arg0 -> {
            Configuration.simplifiedTabNames = BytecodeViewer.viewer.simplifyNameInTabTitle.isSelected();
            SettingsSerializer.saveSettingsAsync();
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
        pluginsMainMenu.add(newJavaPlugin);
        pluginsMainMenu.add(newJavascriptPlugin);
        pluginsMainMenu.add(new JSeparator()); //android specific plugins first
        pluginsMainMenu.add(viewAPKAndroidPermissions);
        pluginsMainMenu.add(new JSeparator());
        pluginsMainMenu.add(viewManifest);
        pluginsMainMenu.add(codeSequenceDiagram);
        pluginsMainMenu.add(maliciousCodeScanner);
        pluginsMainMenu.add(showMainMethods);
        pluginsMainMenu.add(showAllStrings);
        pluginsMainMenu.add(replaceStrings);
        pluginsMainMenu.add(stackFramesRemover);
        pluginsMainMenu.add(changeClassFileVersions);
        
        //allatori is disabled since they are just placeholders
        //ZKM and ZStringArray decrypter are disabled until deobfuscation has been extended
        //mnNewMenu_1.add(mntmNewMenuItem_2);
        //mnNewMenu_1.add(mntmStartZkmString);
        //pluginsMainMenu.add(zStringArrayDecrypter);
        
        openExternalPlugin.addActionListener(arg0 -> openExternalPlugin());
        newJavaPlugin.addActionListener(arg0 -> PluginTemplate.JAVA.openEditorExceptionHandled());
        newJavascriptPlugin.addActionListener(arg0 -> PluginTemplate.JAVASCRIPT.openEditorExceptionHandled());
        codeSequenceDiagram.addActionListener(arg0 -> CodeSequenceDiagram.open());
        maliciousCodeScanner.addActionListener(e -> MaliciousCodeScannerOptions.open());
        showMainMethods.addActionListener(e -> PluginManager.runPlugin(new ShowMainMethods()));
        showAllStrings.addActionListener(e -> PluginManager.runPlugin(new ShowAllStrings()));
        replaceStrings.addActionListener(arg0 -> ReplaceStringsOptions.open());
        stackFramesRemover.addActionListener(e -> PluginManager.runPlugin(new StackFramesRemover()));
        allatoriStringDecrypter.addActionListener(e -> PluginManager.runPlugin(new AllatoriStringDecrypter.AllatoriStringDecrypterOptions()));
        ZKMStringDecrypter.addActionListener(e -> PluginManager.runPlugin(new ZKMStringDecrypter()));
        zStringArrayDecrypter.addActionListener(arg0 -> PluginManager.runPlugin(new ZStringArrayDecrypter()));
        viewAPKAndroidPermissions.addActionListener(arg0 -> PluginManager.runPlugin(new ViewAPKAndroidPermissions()));
        viewManifest.addActionListener(arg0 -> PluginManager.runPlugin(new ViewManifest()));
        changeClassFileVersions.addActionListener(arg0 -> PluginManager.runPlugin(new ChangeClassFileVersions()));
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
        autoCompileOnRefresh.setSelected(true);
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
        printLineNumbers.setSelected(false);
    }
    
    public void calledAfterLoad() {
        deleteForeignOutdatedLibs.setSelected(Configuration.deleteForeignLibraries);
    }
    
    public int getFontSize()
    {
        return (int) fontSpinner.getValue();
    }

    public int getMinSdkVersion()
    {
        return (int) minSdkVersionSpinner.getValue();
    }
    
    public synchronized void clearBusyStatus()
    {
        SwingUtilities.invokeLater(()->
        {
            int length = waitIcons.size();
            for (int i = 0; i < length; i++)
                updateBusyStatus(false);
        });
    }
    
    public synchronized void updateBusyStatus(final boolean busy)
    {
        SwingUtilities.invokeLater(() ->
        {
            if (busy)
            {
                JMenuItem waitIcon = new WaitBusyIcon();
                
                rootMenu.add(waitIcon);
                waitIcons.add(waitIcon);
            }
            else
            {
                if(waitIcons.isEmpty())
                    return;
    
                JMenuItem waitIcon = waitIcons.get(0);
                waitIcons.remove(0);
                rootMenu.remove(waitIcon);
                
                //re-enable the Refresh Button incase it gets stuck
                if(waitIcons.isEmpty() && !workPane.refreshClass.isEnabled())
                    workPane.refreshClass.setEnabled(true);
            }
    
            rootMenu.updateUI();
        });
    }
    
    public void compileOnNewThread()
    {
        Thread t = new Thread(() -> BytecodeViewer.compile(true, true), "Compile");
        t.start();
    }
    
    public void runResources()
    {
        if (BytecodeViewer.promptIfNoLoadedClasses())
            return;
        
        new RunOptions().setVisible(true);
    }
    
    public void reloadResources()
    {
        MultipleChoiceDialog dialog = new MultipleChoiceDialog(TranslatedStrings.RELOAD_RESOURCES_TITLE.toString(),
                TranslatedStrings.RELOAD_RESOURCES_CONFIRM.toString(),
                new String[]{TranslatedStrings.YES.toString(), TranslatedStrings.NO.toString()});
    
        if (dialog.promptChoice() == 0)
        {
            LazyNameUtil.reset();
            List<File> reopen = new ArrayList<>();
        
            for (ResourceContainer container : BytecodeViewer.resourceContainers.values())
            {
                File newFile = new File(container.file.getParent() + fs + container.name);
                if (!container.file.getAbsolutePath().equals(newFile.getAbsolutePath()) &&
                        (container.file.getAbsolutePath().endsWith(".apk") || container.file.getAbsolutePath().endsWith(".dex"))) //APKs & dex get renamed
                {
                    container.file.renameTo(newFile);
                    container.file = newFile;
                }
                reopen.add(container.file);
            }
        
            BytecodeViewer.viewer.resourcePane.treeRoot.removeAllChildren();
            BytecodeViewer.resourceContainers.clear();
        
            for (File f : reopen)
            {
                BytecodeViewer.openFiles(new File[]{f}, false);
            }
        
            //refresh panes
        }
    }
    
    public void selectFile()
    {
        final File file = DialogUtils.fileChooser(TranslatedStrings.SELECT_FILE_TITLE.toString(),
                TranslatedStrings.SELECT_FILE_DESCRIPTION.toString(),
                Constants.SUPPORTED_FILE_EXTENSIONS);
    
        if(file == null)
            return;
        
        BytecodeViewer.updateBusyStatus(true);
        BytecodeViewer.openFiles(new File[]{file}, true);
        BytecodeViewer.updateBusyStatus(false);
    }
    
    public void openExternalPlugin()
    {
        final File file = DialogUtils.fileChooser(TranslatedStrings.SELECT_EXTERNAL_PLUGIN_TITLE.toString(),
                TranslatedStrings.SELECT_EXTERNAL_PLUGIN_DESCRIPTION.toString(),
                Configuration.getLastPluginDirectory(),
                PluginManager.fileFilter(),
                Configuration::setLastPluginDirectory,
                FileChooser.EVERYTHING);
    
        if(file == null)
            return;
    
        BytecodeViewer.updateBusyStatus(true);
        BytecodeViewer.startPlugin(file);
        BytecodeViewer.updateBusyStatus(false);
        SettingsSerializer.saveSettingsAsync();
    }
    
    public void askBeforeExiting()
    {
        MultipleChoiceDialog dialog = new MultipleChoiceDialog(TranslatedStrings.EXIT_TITLE.toString(),
                TranslatedStrings.EXIT_CONFIRM.toString(),
                new String[]{TranslatedStrings.YES.toString(), TranslatedStrings.NO.toString()});
    
        if (dialog.promptChoice() == 0)
        {
            Configuration.canExit = true;
            System.exit(0);
        }
    }
    
    public void showForeignLibraryWarning()
    {
        if (!deleteForeignOutdatedLibs.isSelected())
            BytecodeViewer.showMessage(TranslatedStrings.FOREIGN_LIBRARY_WARNING.toString());
        
        Configuration.deleteForeignLibraries = deleteForeignOutdatedLibs.isSelected();
    }
    
    public void updateTabTheme()
    {
        try
        {
            for(Component viewerComponent : BytecodeViewer.viewer.workPane.tabs.getComponents())
            {
                if(!(viewerComponent instanceof ResourceViewer))
                    continue;
            
                ResourceViewer viewerResource = (ResourceViewer) viewerComponent;
                if(!(viewerResource instanceof ClassViewer))
                    continue;
            
                ClassViewer viewerClass = (ClassViewer) viewerResource;
                Configuration.rstaTheme.apply(viewerClass.bytecodeViewPanel1.textArea);
                Configuration.rstaTheme.apply(viewerClass.bytecodeViewPanel2.textArea);
                Configuration.rstaTheme.apply(viewerClass.bytecodeViewPanel3.textArea);
            }
            
            SwingUtilities.updateComponentTreeUI(BytecodeViewer.viewer);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    public static final long serialVersionUID = 1851409230530948543L;
}
