package the.bytecode.club.bytecodeviewer;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import me.konloch.kontainer.io.DiskReader;
import me.konloch.kontainer.io.DiskWriter;
import me.konloch.kontainer.io.HTTPRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.imgscalr.Scalr;
import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.api.ClassNodeLoader;
import the.bytecode.club.bytecodeviewer.gui.ClassViewer;
import the.bytecode.club.bytecodeviewer.gui.FileNavigationPane;
import the.bytecode.club.bytecodeviewer.gui.MainViewerGUI;
import the.bytecode.club.bytecodeviewer.gui.SearchingPane;
import the.bytecode.club.bytecodeviewer.gui.WorkPane;
import the.bytecode.club.bytecodeviewer.plugins.PluginManager;

/**
 * A lightweight Java Reverse Engineering suite, developed by Konloch -
 * http://konloch.me
 * 
 * Are you a Java Reverse Engineer? Or maybe you want to learn Java Reverse
 * Engineering? Join The Bytecode Club, we're noob friendly, and censorship
 * free.
 * 
 * http://the.bytecode.club
 * 
 * All you have to do is add a jar or class file into the workspace, select the
 * file you want then it will start decompiling the class in the background,
 * when it's done it will show the Source code, Bytecode and Hexcode of the
 * class file you chose.
 * 
 * There is also a plugin system that will allow you to interact with the loaded
 * classfiles, for example you can write a String deobfuscator, a malicious code
 * searcher, or something else you can think of. You can either use one of the
 * pre-written plugins, or write your own. It supports groovy
 * scripting. Once a plugin is activated, it will send a ClassNode ArrayList of
 * every single class loaded in the file system to the execute function, this
 * allows the user to handle it completely using ASM.
 * 
 * TODO:
 * The import jar method eats up a lot of memory, look into some how reducing this.
 * Add obfuscation:
 *    - Add integer boxing and other obfuscation methods contra implemented
 *    - Insert unadded/debug opcodes to try to fuck up decompilers
 *    - ClassAnylyzterAdapter
 * Add progress bars on saving all zips/java decompile jar
 * Add the jump/save mark system Ida Pro has.
 * Add class annotations to bytecode decompiler.
 * Option to make  the bytecode pane automatically scroll to where the source code pane is
 * Replacing all string field calls with the string instance - would require EZ-Injection to run code?
 * Spiffy up the plugin console with red text optional, would require JTextPane, not JTextArea.
 * Add right click on tab > close other tabs > close this tab
 * maybe just do AMS5 then obfuscate the dex2jar shit.
 * 
 * ----Beta 1.0.0-----:
 * 10/4/2014 - Designed a POC GUI, still needs a lot of work.
 * 10/4/2014 - Started importing J-RET's backend.
 * 10/5/2014 - Finished importing J-RET's backend.
 * 10/6/2014 - Started modifying J-RET's UI.
 * 10/6/2014 - Added several FernFlower options.
 * 10/6/2014 - Fixed the class search function so it doesn't require exact class names.
 * 10/6/2014 - Added save as, it'll save all of the loaded classes into one jar file (GUI Jar-Jar now).
 * 10/6/2014 - Centered the select jar text inside of the file navigator.
 * 10/6/2014 - Properly threaded the open jar function, now fernflower/bytecode decompiler runs in the background.
 * 10/6/2014 - Added a hex viewer (Instead of using Re-Java's, I've decided to use a modified version of JHexEditor).
 * 10/6/2014 - Made all of the viewer (Sourcecode, Bytecode & Hexcode toggleable).
 * 10/7/2014 - Fixed the search function.
 * 10/7/2014 - You can now add new files without it creating a new workspace.
 * 10/7/2014 - Added new workspace button underneath File, this will reset the workspace.
 * 10/7/2014 - Renamed File>Open.. to File>Add..
 * 10/7/2014 - Added recent files.
 * 10/7/2014 - Did some bitch work, the project has no warnings now.
 * 10/7/2014 - Added waiting cursors to anything that will require waiting or loading.
 * 10/8/2014 - Searching now runs in a background thread.
 * 10/8/2014 - Added File>About.
 * 10/8/2014 - The main GUI now starts in the middle of your screen, same with the about window.
 * 10/8/2014 - Made the File Navigator Pane, Workspace Pane & Search Pane a little sexier.
 * 10/9/2014 - Started on a Plugin system
 * 10/9/2014 - Added a malicious code scanner plugin, based off of the one from J-RET, this searches for a multitude of classes/packages that can be used for malicious purposes.
 * 10/9/2014 - Added a show all strings plugin, this grabs all the declared strings and displays them in a nice little window.
 * 10/9/2014 - Fixed a bug with Bytecode Decompiler, where it would it display \r and \n as return carriages.
 * 10/9/2014 - Fixed the Bytecode Decompiler>Debug Instructions option.
 * 10/9/2014 - Save Class Files As is now renamed to Save Files As.
 * 10/9/2014 - Save Files As now saves jar resources, not just classfiles.
 * 10/9/2014 - Added an 'Are you sure' pane when you click on File>New Workspace.
 * 10/9/2014 - Save Files As is no longer dependent on the File System, now if you're on windows and you have a file called AA, and one called Aa, you're fine.
 * 10/11/2014 - Modified the FernFlower library, it no longer spits out System.out.println's while processing a method, this has sped it up quite a lot.
 * 10/12/2014 - Fix an issue when resizing.
 * 10/12/2014 - Modified the core slighty to no longer have a modularized decompiling system (since there are only 2 decompilers anyways).
 * 10/12/2014 - Fixed an issue with decompiling multiple files at once.
 * 10/12/2014 - The Plugin Console now shows the plugin's name on the title.
 * 10/12/2014 - Debug Helpers will now debug all jump instructions by showing what instruction is on the line it's suppose to goto, example: 90. goto 120 // line 120 is PUTFIELD Animable_Sub4.anInt1593 : I
 * 10/12/2014 - Now when you select an already opened file, it will automatically go to that opened pane.
 * 10/14/2014 - Added the option 'exact' to the class finder.
 * 10/14/2014 - Added the option 'exact' to the searcher, now it'll search for .contains when unselected.
 * 10/14/2014 - Stopped the use of StringBuffer, replaced all instances with StringBuilder.
 * 10/14/2014 - Added Labels and Try-Catch blocks to the Bytecode Decompiler.
 * 10/14/2014 - For panes that are not selected, the corresponding decompiler will not execute.
 * 10/14/2014 - Added plugin Show Main Methods, this will show every single public static void main(String[]).
 * 10/14/2014 - Plugins can no longer be ran when there is no loaded classes.
 * 10/14/2014 - The Malicious Code Scanner now has gui option pane before you run it.
 * 10/14/2014 - Added a java/io option to the Malicious Code Scanner.
 * 10/14/2014 - Added save Java files as.
 * 10/15/2014 - Added save as Jar file. (Export as Jar)
 * 10/15/2014 - Added the option to ASCII only strings in the Bytecode Decompiler.
 * 10/15/2014 - External plugins are now fully functional, same with recent plugins.
 * 10/16/2014 - Removed all refences of 'ClassContainer'.
 * 10/16/2014 - Rewrote the tempfile system.
 * 10/16/2014 - Moved the file import to BytecodeViewer.class.
 * 10/16/2014 - Fixed a jTree updating issue.
 * 10/16/2014 - Now if you try search with an empty string, it won't search.
 * 10/16/2014 - Added Replace Strings plugin.
 * 10/16/2014 - Added a loading icon that displays whenever a background task is being executed.
 * ----Beta 1.1.0-----:
 * 10/19/2014 - Fixed harcoded \\.
 * ----Beta 1.2.0-----:
 * 10/19/2014 - Started importing Procyon and CFR decompilers.
 * 10/19/2014 - Partially finished importing Procyon and CFR, just need to finish export java files as zip.
 * ----Beta 1.3.0-----:
 * 10/22/2014 - Imported Bibl's Bytecode Decompiler from CFIDE.
 * 10/22/2014 - Did some changes to the Bytecode Decompiler.
 * 10/23/2014 - Added CFR settings.
 * 10/23/2014 - Updated FernFlower to Intellij's Open Sourced version of FernFlower.
 * 10/24/2014 - Fixed FernFlower save Java files as zip.
 * 10/29/2014 - Added version checker.
 * 10/29/2014 - Added Procyon settings.
 * 10/29/2014 - When saving as jars or zips, it'll automatically append the file extension if it's not added.
 * 10/29/2014 - All the built in plugins no longer set the cursor to busy.
 * 10/29/2014 - Tried to fix the issue with JSyntaxPane by making it create the object in a background thread, it still freezes the UI. Changes kept for later implementation of another syntax highlighter.
 * 10/29/2014 - Sped up start up time.
 * ----Beta 1.3.1-----:
 * 10/29/2014 - Replaced JSyntaxPane with RSyntaxArea, this sadly removes the search feature inside of source/bytecode files, I'll implement a search function soon. (This also fixes the JRE 1.8 issue)
 * 10/29/2014 - Added a new decompiler option to append brackets to labels.
 * 10/31/2014 - Fixed an issue with the decompiler still running when the source code pane isn't toggled.
 * ----Beta 1.4.0-----:
 * 11/1/2014 - Fixed FernFlower save Java files on Unix.
 * 11/1/2014 - FernFlower now uses the settings for save Java files.
 * 11/1/2014 - Added Procyon save Java files (It uses the settings).
 * 11/1/2014 - Updated CFR to cfr_0_89.
 * 11/1/2014 - Added CFR save Java files (It uses the settings), however it relies on the file system, because of this if there is heavy name obfuscation, it could mess up for windows.
 * -----Beta 1.5.0-----:
 * 11/1/2014 - Updated and improved the search function, it now prints out more useful information.
 * 11/1/2014 - Fixed a UI issue with the Replace All Strings plugin.
 * 11/2/2014 - Added search function to the Class Viewer.
 * 11/2/2014 - Updated Procyon to procyon-decompiler-0.5.27.
 * -----Beta 1.5.1-----:
 * 11/2/2014 - Fixed a CFR issue with packages.
 * -----Beta 1.5.2-----:
 * 11/3/2014 - Fixed Refresh Class.
 * -----Beta 1.5.3-----:
 * 11/3/2014 - Settings/Temp file are now in a global directory.
 * 11/3/2014 - The GUI setttings now save.
 * 11/3/2014 - Removed the option to disable syntax highlighting (since it's lightweight now).
 * 11/3/2014 - About window now contains the version number and the BCV directory.
 * 11/3/2014 - Added an option to toggle to outdated status.
 * -----2.0.0-----:
 * 11/4/2014 - Officially been 1 month of development.
 * 11/4/2014 - Replaced ""+ with String.valueOf (cheers bibl).
 * 11/4/2014 - Changed how the temp directory was created.
 * 11/4/2014 - Put a file.seperator  to the end of tempDirectory.
 * 11/4/2014 - Made the exit button work.
 * 11/4/2014 - Added a GUI for all Exception Stack Trace's.
 * 11/4/2014 - The plugin system now shows a message instead of just printing to the console when it's not going to run a plugin.
 * 11/4/2014 - Updated the search function, it's now perfect.
 * 11/5/2014 - Made the Show All Strings plugin instant.
 * 11/5/2014 - Kinda added middle mouse button closes tab (only if you click the exit button).
 * 11/5/2014 - Improved the Malicious Code Scanner, also made it instant.
 * 11/5/2014 - Added icons to the program (cheers Fluke).
 * -----2.0.1-----:
 * 11/7/2014 - Fixed the text search function.
 * 11/7/2014 - Removed an unused package containing some unused classes.
 * -----2.1.0-----:
 * 11/5/2014 - Started working on the EZ-Inject plugin.
 * 11/6/2014 - Fixed the ClassNodeDecompiler creating unnessessary objects. (thanks bibl).
 * 11/6/2014 - Finished an alpha version of EZ-Inject.
 * 11/6/2014 - Started working on a basic obfuscator.
 * 11/6/2014 - The Obfuscator now sucessfully renames all field names.
 * 11/6/2014 - Updated CFR to cfr_0_90.
 * 11/8/2014 - Started working on the API for BCV.
 * 11/9/2014 - Decided to make a graphical reflection kit.
 * 11/10/2014 - Made some progress with the obfuscator, almost finished EZ-Injection.
 * 11/14/2014 - Been doing various updates to EZ-Injection, Obfucsation, Reflection Kit and the BCV API.
 * 11/16/2014 - Added the option to launch BCV command line as java -jar bcv.jar C:/test.jar C:/example/whatever.jar
 * 11/17/2014 - Fixed an issue with the out of date checking UI still activating when not selected.
 * 11/19/2014 - Added annotatitons/local variables to the methodnode decompiler (Thanks Bibl).
 * 11/21/2014 - Decided to release it with the obfuscator/reflection kit unfinished, they're currently disabled for future use.
 * -----2.1.1-----:
 * 12/09/2014 - Updated CFR to cfr_0_91.
 * -----2.2.0-----:
 * 12/09/2014 - Added a text search function to the plugin console.
 * 12/09/2014 - When you press enter in the text search bar, it will now search.
 * 12/13/2014 - The Bytecode Decompiler now shows the method's description in a comment.
 * 12/13/2014 - Fixed an issue with the text search function.
 * 12/13/2014 - Search results are now clickable.
 * -----2.2.1-----:
 * 12/13/2014 - Fixed an issue with the Bytecode Decompiler. - Thanks bibl
 * -----2.3.0-----:
 * 12/16/2014 - Started updating the class viewer.
 * 12/18/2014 - Finished a basic concept of the new class viewer.
 * 12/18/2014 - Fixed an error with importing some jars. (Thanks sahitya-pavurala)
 * 12/18/2014 - Fixed the about window.
 * 12/18/2014 - Finished the final concept for the new class viewer.
 * 12/18/2014 - Threaded save Java files as zip, it now runs in a background thread.
 * 12/18/2014 - Save Java files as zip now prompts you to select a decompiler.
 * 12/18/2014 - Removed the cursor waiting for save Java files as zip.
 * 12/18/2014 - Wrapped the save Java files as zip around an exception handler, it will now safely show the exception if any is thrown.
 * 12/18/2014 - Fixed not escaping the Java strings by default for the Bytecode decompiler. - http://i.imgur.com/YrRnZA7.png
 * 12/18/2014 - Used Eclipse's code formatting tool and formatted the code
 * 12/19/2014 - Priav03 fixed the quick class searcher.
 * -----2.4.0-----:
 * 12/19/2014 - Afffsdd made the Bytecode Viewer directory hidden.
 * 12/19/2014 - Added save Java file as, for singular class file decompilation (this is threaded).
 * 12/19/2014 - Removed unused Bytecode Decompiler debug code.
 * 12/20/2014 - Made a new outdated pane - http://i.imgur.com/xMxkwJ9.png
 * 12/20/2014 - Added an expand/collapse the packages in the file navigator.
 * 12/20/2014 - Moved all of the settings to the.bytecode.club.bytecodeviewer.Settings
 * 12/20/2014 - If the class file does not start with CAFEBABE it won't be processed.
 * 12/20/2014 - Properly handled file not found error.
 * 12/21/2014 - Fixed the Refresh Class causing a dupe.
 * -----2.5.0-----:
 * 12/28/2014 - Improved the outdated version pane by including an automatic downloader - http://i.imgur.com/4MXeBGb.png - http://i.imgur.com/v50Pghe.png - http://i.imgur.com/bVZqxZ2.png - http://i.imgur.com/l8nIMzD.png
 * 12/28/2014 - Updated CFR to cfr_0.92.jar
 * 12/31/2014 - Adrianherrera updated the Malicious Code Scanner to detect the security manager being set to null.
 * **HAPPY NEW YEAR**
 * 01/01/2015 - Added refresh class on decompiler/pane view change
 * 01/01/2015 - Moved all of the settings into a settings pane
 * 01/01/2015 - Added some debug code when you first start it up, it also includes how long it took to fully load up.
 * 01/02/2015 - Cached the busy icon.
 * 01/02/2015 - ADDED APK SUPPORT, had to downgrade to ASM 3.3, which means losing some annotation debugging for the Bytecode Decompiler.
 * 01/03/2015 - Wrapped the search pane in a JScrollPane.
 * 01/06/2015 - Added save as DEX and import .dex files.
 * -----2.5.1-----:
 * 01/06/2015 - Silenced the error connecting to update server for offline mode.
 * 01/06/2015 - Fixed a search function with Android APKs.
 * -----2.5.2-----:
 * 01/06/2015 - Fixed another issue with LDC searching for Android APKs.
 * -----2.6.0-----:
 * 01/06/2015 - Now saves if maximized or not.
 * 01/07/2015 - For all save as functions, it will now append the correct extension if not added by the user.
 * 01/07/2015 - You can no longer use use the save functions if no classes are loaded (fixes a crash issue).
 * 01/07/2015 - Moved the Update Check to the Settings menu.
 * 01/08/2015 - Added an extremely basic code sqeuence diagram plugin.
 * 01/08/2015 - Updated CFR to CFR_0.93.jar
 * 01/08/2015 - Threaded the Add files function.
 * 01/08/2015 - Finally implemented Kontainer's HTTPRequest wrapper now that I've open sourced it.
 * 01/08/2015 - Set the panes to be non-editable.
 * 01/08/2015 - Sexified the view pane selection.
 * 01/08/2015 - Started working on Smali Editing support, finished decompiler so far.
 * 01/09/2015 - Fixed a bug with saving.
 * 01/09/2015 - Added add entire directory.
 * 01/09/2015 - Fixed import .DEX files.
 * 01/10/2015 - Finished Smali Editing.
 * 01/10/2015 - Fixed a class opening issue with sychronization.
 * 01/11/2015 - Threaded all of the save functions.
 * 01/11/2015 - Removed all instances of the setCursor to busy.
 * 01/11/2015 - Added are you sure you wish to overwrite this existing file to all the other save functions.
 * 01/11/2015 - All of the decompiling names are now randomly generated instead of a counting number.
 * 01/11/2015 - Updated CFR to CFR_0.94.jar
 * 01/11/2015 - Updated to the latest version of FernFlower.
 * 01/11/2015 - Fixed an extension appending issue with save Java file.
 * -----2.7.0-----:
 * 01/11/2015 - Improved the Refresh Class function to be used as the default compile function.
 * 01/11/2015 - Implemented better error handling for decompiling class files.
 * 01/15/2015 - CTRL + O will open the add file interface.
 * 01/15/2015 - CTRL + N will open the net workspace interface.
 * 01/15/2015 - It will now save the last directory you opened.
 * 01/15/2015 - Some how the URL for the auto updater change log got changed, this has been fixed.
 * 01/15/2015 - Slightly updated the change log display, it'll now show all the changes since your version.
 * 01/16/2015 - Made EZ-Injection UI look a bit nicer.
 * 01/27/2015 - Decided to scrap the  JVM Sandbox POC and use the Security Manager.
 * 01/27/2015 - BCV now blocks exec and won't allow any ports to be bound.
 * 01/27/2015 - Added java.awt.Robot to the malicious code scanner.
 * -----2.7.1-----:
 * 01/27/2015 - Fixed hide file.
 * -----2.8.0-----:
 * 02/01/2015 - Updated CFR and Proycon to latest versions.
 * 02/01/2015 - Started working on implementing Krakatau.
 * 02/01/2015 - Sexifixed the security manager a little bit.
 * 02/03/2015 - Fully added Krakatau Java decompiler, just disassembly/assembly left.
 * 02/03/2015 - Updated the about window.
 * 02/03/2015 - Dropped JRuby and Jython support (BCV is now roughly 16mb, was 45mb).
 * 02/04/2015 - Added Krakatau Disassembly.
 * 02/04/2015 - Added Krakatau Assembly.
 * 
 * @author Konloch
 * 
 */

public class BytecodeViewer {

	/*per version*/
	public static String version = "2.8.0";
	public static String krakatauVersion = "2";
	/*the rest*/
	public static MainViewerGUI viewer = null;
	public static ClassNodeLoader loader = new ClassNodeLoader(); //might be insecure due to assholes targeting BCV, however that's highly unlikely.
	public static String python = "";
	public static String rt = "";
	public static SecurityMan sm = new SecurityMan();
	public static HashMap<String, ClassNode> loadedClasses = new HashMap<String, ClassNode>();
	public static HashMap<String, byte[]> loadedResources = new HashMap<String, byte[]>();
	private static int maxRecentFiles = 25;
	public static String fs = System.getProperty("file.separator");
	public static String nl = System.getProperty("line.separator");
	private static File BCVDir = new File(System.getProperty("user.home") + fs + ".Bytecode-Viewer");
	private static String filesName = getBCVDirectory() + fs + "recentfiles.bcv";
	private static String pluginsName = getBCVDirectory() + fs + "recentplugins.bcv";
	public static String settingsName = getBCVDirectory() + fs + "settings.bcv";
	public static String tempDirectory = getBCVDirectory() + fs + "bcv_temp" + fs;
	public static String krakatauWorkingDirectory = getBCVDirectory() + fs + "krakatau_" + krakatauVersion + fs + "Krakatau-master";
	private static ArrayList<String> recentFiles = DiskReader.loadArrayList(filesName, false);
	private static ArrayList<String> recentPlugins = DiskReader.loadArrayList(pluginsName, false);
	public static boolean runningObfuscation = false;
	private static long start = System.currentTimeMillis();
	public static String lastDirectory = "";
	private static Thread versionChecker = new Thread() {
		@Override
		public void run() {
			try {
				HTTPRequest r = new HTTPRequest(new URL("https://raw.githubusercontent.com/Konloch/bytecode-viewer/master/VERSION"));
				final String version = r.readSingle();
				try {
					int simplemaths = Integer.parseInt(version.replace(".", ""));
					int simplemaths2 = Integer.parseInt(BytecodeViewer.version.replace(".", ""));
					if(simplemaths2 > simplemaths)
						return; //developer version
				} catch(Exception e) {
					
				}
				
				if (!BytecodeViewer.version.equals(version)) {
					r = new HTTPRequest(new URL("https://raw.githubusercontent.com/Konloch/bytecode-viewer/master/README.txt"));
					String[] readme = r.read();
					
					String changelog = "Unable to load change log, please try again later."+nl;
					boolean trigger = false;
					boolean finalTrigger = false;
					for(String st : readme) {
						if(st.equals("--- "+BytecodeViewer.version+" ---:")) {
							changelog = "";
							trigger = true;
						} else if(trigger) {
							if(st.startsWith("--- "))
								finalTrigger = true;
							
							if(finalTrigger)
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
					Object[] options = new String[] { "Open The Download Page", "Download The Updated Jar", "Do Nothing" };
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
						if(Desktop.isDesktopSupported())
						{
						  Desktop.getDesktop().browse(new URI("https://github.com/Konloch/bytecode-viewer/releases"));
						} else {
							showMessage("Cannot open the page, please manually type it.");
						}
					}
					if(result == 1) {
						JFileChooser fc = new JFileChooser();
						try {
							fc.setCurrentDirectory(new File(".").getAbsoluteFile()); //set the current working directory
						} catch(Exception e) {
							new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
						}
						fc.setFileFilter(viewer.new ZipFileFilter());
						fc.setFileHidingEnabled(false);
						fc.setAcceptAllFileFilterUsed(false);
						int returnVal = fc.showSaveDialog(viewer);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							File file = fc.getSelectedFile();
							if(!file.getAbsolutePath().endsWith(".zip"))
								file = new File(file.getAbsolutePath()+".zip");
							
							if(file.exists()) {
								pane = new JOptionPane("The file " + file + " exists, would you like to overwrite it?");
								options = new String[] { "Yes", "No" };
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
										InputStream is = new URL("https://github.com/Konloch/bytecode-viewer/releases/download/v"+version+"/BytecodeViewer."+version+".zip").openConnection().getInputStream();
										FileOutputStream fos = new FileOutputStream(finalFile);
									    try {
									    	System.out.println("Downloading from https://github.com/Konloch/bytecode-viewer/releases/download/v"+version+"/BytecodeViewer."+version+".zip");
									        byte[] buffer = new byte[8192];
									        int len;
									        int downloaded = 0;
									        boolean flag = false;
									    	showMessage("Downloading the jar in the background, when it's finished you will be alerted with another message box."+nl+nl+"Expect this to take several minutes.");
									        while ((len = is.read(buffer)) > 0) {  
									            fos.write(buffer, 0, len);
									            fos.flush();
									            downloaded += 8192;
										        int mbs = downloaded / 1048576;
										        if(mbs % 5 == 0 && mbs != 0) {
										        	if(!flag)
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
									} catch(FileNotFoundException e) {
										showMessage("Unable to download, the zip file has not been uploaded yet, please try again later in an hour.");
									} catch(Exception e) {
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
	 * Grab the byte array from the loaded Class object
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	public static byte[] getClassFile(Class<?> clazz) throws IOException {     
	    InputStream is = clazz.getResourceAsStream( "/" + clazz.getName().replace('.', '/') + ".class");
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    int r = 0;
	    byte[] buffer = new byte[8192];
	    while((r=is.read(buffer))>=0) {
	        baos.write(buffer, 0, r);
	    }   
	    return baos.toByteArray();
	}
	
	public static void main(String[] args) {
		System.setSecurityManager(sm);
		checkKrakatau();
		System.out.println("https://the.bytecode.club - Created by @Konloch - Bytecode Viewer " + version);
		iconList = new ArrayList<BufferedImage>();
		int size = 16;
		for (int i = 0; i < 24; i++) {
			iconList.add(resize(icon, size, size));
			size += 2;
		}
		cleanup();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				Settings.saveGUI();
				cleanup();
			}
		});
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
		}

		viewer = new MainViewerGUI();
		Settings.loadGUI();
		resetRecentFilesMenu();

		if (viewer.chckbxmntmNewCheckItem_12.isSelected()) // start only if selected
			versionChecker.start();

		viewer.setVisible(true);		
		System.out.println("Start up took " + ((System.currentTimeMillis() - start) / 1000) + " seconds");
		
		if (args.length >= 1)
			for (String s : args) {
				openFiles(new File[] { new File(s) }, true);
			}
	}
	
	//because Smali and Baksmali System.exit if it failed
	public static void exit(int i) {
		
	}

	public static ClassNode getCurrentlyOpenedClassNode() {
		return viewer.workPane.getCurrentClass().cn;
	}
	
	public static ClassNode getClassNode(String name) {
		if (loadedClasses.containsKey(name))
			return loadedClasses.get(name);
		return null;
	}

	public static void updateNode(ClassNode oldNode, ClassNode newNode) {
		BytecodeViewer.loadedClasses.remove(oldNode.name);
		BytecodeViewer.loadedClasses.put(oldNode.name, newNode);
	}

	public static ArrayList<ClassNode> getLoadedClasses() {
		ArrayList<ClassNode> a = new ArrayList<ClassNode>();
		if (loadedClasses != null)
			for (Entry<String, ClassNode> entry : loadedClasses.entrySet()) {
				Object value = entry.getValue();
				ClassNode cln = (ClassNode) value;
				a.add(cln);
			}
		return a;
	}
	
	//called whenever a save function is executed
	public static boolean compile(boolean message) {
		if(getLoadedClasses().isEmpty())
			return false;
		
		for(java.awt.Component c : BytecodeViewer.viewer.workPane.getLoadedViewers()) {
			if(c instanceof ClassViewer) {
				ClassViewer cv = (ClassViewer) c;
				Object smali[] = cv.getSmali();
				if(smali != null) {
					ClassNode origNode = (ClassNode) smali[0];
					String smaliText = (String) smali[1];
					byte[] smaliCompiled = the.bytecode.club.bytecodeviewer.compilers.SmaliAssembler.compile(smaliText);
					if(smaliCompiled != null) {
						ClassNode newNode = JarUtils.getNode(smaliCompiled);
						System.out.println(origNode.name+":"+newNode.name);
						BytecodeViewer.updateNode(origNode, newNode);
					} else {
						BytecodeViewer.showMessage("There has been an error with assembling your Smali code, please check this. Class: " + origNode.name);
						return false;
					}
				}
				

				Object krakatau[] = cv.getKrakatau();
				if(krakatau != null) {
					ClassNode origNodeK = (ClassNode) krakatau[0];
					String krakatauText = (String) krakatau[1];
					byte[] krakatauCompiled = the.bytecode.club.bytecodeviewer.compilers.KrakatauAssembler.compile(krakatauText, origNodeK.name);
					if(krakatauCompiled != null) {
						ClassNode newNode = JarUtils.getNode(krakatauCompiled);
						System.out.println(origNodeK.name+":"+newNode.name);
						BytecodeViewer.updateNode(origNodeK, newNode);
					} else {
						BytecodeViewer.showMessage("There has been an error with assembling your Krakatau Bytecode, please check this. Class: " + origNodeK.name);
						return false;
					}
				}
			}
		}
		
		if(message)
			BytecodeViewer.showMessage("Compiled Successfully.");
		
		return true;
	}

	public static void checkKrakatau() {
		File krakatauDirectory = new File(getBCVDirectory() + fs + "krakatau_" + krakatauVersion);
		if(!krakatauDirectory.exists()) {
			try {
				File temp = new File(getBCVDirectory() + fs + "krakatau_" + krakatauVersion + ".zip");
			    while(temp.exists())
			    	temp.delete();
				InputStream is = BytecodeViewer.class.getClassLoader().getResourceAsStream("krakatau.zip");
			    FileOutputStream baos = new FileOutputStream(temp);
			    int r = 0;
			    byte[] buffer = new byte[8192];
			    while((r=is.read(buffer))>=0) {
			        baos.write(buffer, 0, r);
			    }
			    baos.close();
			    ZipUtils.unzipFilesToPath(temp.getAbsolutePath(), krakatauDirectory.getAbsolutePath());
			    temp.delete();
			} catch(Exception e) {
				showMessage("ERROR: There was an issue unzipping Krakatau decompiler, please contact @Konloch with your stacktrace.");
				new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
			}
		}
	}
	
	private static boolean update = true;
	public static void openFiles(final File[] files, boolean recentFiles) {
		if(recentFiles)
			for (File f : files)
				BytecodeViewer.addRecentFile(f);
		
		BytecodeViewer.viewer.setIcon(true);
		update = true;

		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					for (final File f : files) {
						final String fn = f.getName();
						if(!f.exists()) {
							update = false;
							showMessage("The file " + f.getAbsolutePath() + " could not be found.");
						} else {
							if(f.isDirectory()) {
								openFiles(f.listFiles(), false);
							} else {
								if (fn.endsWith(".jar") || fn.endsWith(".zip")) {
									try {
										JarUtils.put(f, BytecodeViewer.loadedClasses);
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
										if(cafebabe.toLowerCase().equals("cafebabe")) {
											final ClassNode cn = JarUtils.getNode(bytes);
											BytecodeViewer.loadedClasses.put(cn.name, cn);
										} else {
											showMessage(fn+": Header does not start with CAFEBABE, ignoring.");
											update = false;
										}
									} catch (final Exception e) {
										new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
										update = false;
									} 
								} else if(fn.endsWith(".apk") || fn.endsWith(".dex")) {
									try {
										String name = getRandomizedName()+".jar";
										File output = new File(tempDirectory + fs + name);
										Dex2Jar.dex2Jar(f, output);
										BytecodeViewer.viewer.setIcon(false);
										openFiles(new File[]{output}, false);
									} catch (final Exception e) {
										new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
									}
									return;
								}
							}
						}
					}
				} catch (final Exception e) {
					new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
				} finally {
					BytecodeViewer.viewer.setIcon(false);
					
					if(update)
						try {
							MainViewerGUI.getComponent(FileNavigationPane.class).updateTree();
						} catch(java.lang.NullPointerException e) {
						}
					}
				}
			};
			t.start();
		}

	public static void startPlugin(File plugin) {
		if (!plugin.exists())
			return;

		try {
			PluginManager.runPlugin(plugin);
		} catch (Exception e) {
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
		}
		addRecentPlugin(plugin);
	}

	public static void showMessage(String message) {
		JOptionPane.showMessageDialog(viewer, message);
	}

	public static void resetWorkSpace(boolean ask) {
		if(!ask) {
			loadedResources.clear();
			loadedClasses.clear();
			MainViewerGUI.getComponent(FileNavigationPane.class)
					.resetWorkspace();
			MainViewerGUI.getComponent(WorkPane.class).resetWorkspace();
			MainViewerGUI.getComponent(SearchingPane.class).resetWorkspace();
			the.bytecode.club.bytecodeviewer.api.BytecodeViewer
					.getClassNodeLoader().clear();
		} else {
			JOptionPane pane = new JOptionPane(
					"Are you sure you want to reset the workspace?\n\rIt will also reset your file navigator and search.");
			Object[] options = new String[] { "Yes", "No" };
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
				loadedResources.clear();
				loadedClasses.clear();
				MainViewerGUI.getComponent(FileNavigationPane.class)
						.resetWorkspace();
				MainViewerGUI.getComponent(WorkPane.class).resetWorkspace();
				MainViewerGUI.getComponent(SearchingPane.class).resetWorkspace();
				the.bytecode.club.bytecodeviewer.api.BytecodeViewer
						.getClassNodeLoader().clear();
			}
		}
	}

	private static ArrayList<String> killList = new ArrayList<String>();

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

	public static void resetRecentFilesMenu() {
		viewer.mnRecentFiles.removeAll();
		for (String s : recentFiles)
			if (!s.isEmpty()) {
				JMenuItem m = new JMenuItem(s);
				m.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JMenuItem m = (JMenuItem) e.getSource();
						openFiles(new File[] { new File(m.getText()) }, true);
					}
				});
				viewer.mnRecentFiles.add(m);
			}
		viewer.mnRecentPlugins.removeAll();
		for (String s : recentPlugins)
			if (!s.isEmpty()) {
				JMenuItem m = new JMenuItem(s);
				m.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JMenuItem m = (JMenuItem) e.getSource();
						startPlugin(new File(m.getText()));
					}
				});
				viewer.mnRecentPlugins.add(m);
			}
	}

	private static File tempF = null;

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
	
	public static String getRandomizedName() {
		boolean generated = false;
		String name = "";
		while(!generated) {
			String randomizedName = MiscUtils.randomString(25);
			if(!createdRandomizedNames.contains(randomizedName)) {
				createdRandomizedNames.add(randomizedName);
				name = randomizedName;
				generated = true;
			}
		}
		return name;
	}

	public static String getBCVDirectory() {
		while (!BCVDir.exists())
			BCVDir.mkdirs();
		
		if (!BCVDir.isHidden() && isWindows())
			hideFile(BCVDir);
		
		return BCVDir.getAbsolutePath();
	}

	private static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

	private static void hideFile(File f) {
		sm.blocking = false;
		try {
			// Hide file by running attrib system command (on Windows)
			Runtime.getRuntime().exec("attrib +H " + f.getAbsolutePath());
		} catch (Exception e) {
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
		}
		sm.blocking = true;
	}

	private static String quickConvert(ArrayList<String> a) {
		String s = "";
		for (String r : a)
			s += r + nl;
		return s;
	}

	public static ArrayList<BufferedImage> iconList;
	public static BufferedImage icon = b642IMG("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");

	public static BufferedImage resize(BufferedImage image, int width,
			int height) {
		return Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, width, height);
	}

	/**
	 * Decodes a Base64 String as a BufferedImage
	 */
	public static BufferedImage b642IMG(String imageString) {
		BufferedImage image = null;
		byte[] imageByte;

		try {
			imageByte = Base64.decodeBase64(imageString);
			ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
			image = ImageIO.read(bis);
			bis.close();
		} catch (Exception e) {
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
		}

		return image;
	}
}
