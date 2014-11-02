package the.bytecode.club.bytecodeviewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import me.konloch.kontainer.io.DiskReader;
import me.konloch.kontainer.io.DiskWriter;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.gui.FileNavigationPane;
import the.bytecode.club.bytecodeviewer.gui.MainViewerGUI;
import the.bytecode.club.bytecodeviewer.gui.SearchingPane;
import the.bytecode.club.bytecodeviewer.gui.WorkPane;
import the.bytecode.club.bytecodeviewer.plugins.PluginManager;

/**
 * A lightweight Java Bytecode Viewer/GUI Decompiler, developed by Konloch - http://konloch.me
 * 
 * Are you a Java Reverse Engineer? Or maybe you want to learn Java Reverse Engineering?
 * Join The Bytecode Club - http://the.bytecode.club
 * We're noob friendly, and censorship free.
 * 
 * All you have to do is add a jar or class file into the workspace, select the file you want
 * then it will start decompiling the class in the background, when it's done it will show
 * the Source code, Bytecode and Hexcode of the class file you chose.
 * 
 * There is also a plugin system that will allow you to interact with the loaded classfiles, for example
 * you can write a String deobfuscator, a malicious code searcher, or something else you can think of.
 * You can either use one of the pre-written plugins, or write your own. It supports groovy, python and
 * ruby scripting. Once a plugin is activated, it will send a ClassNode ArrayList of every single
 * class loaded in the file system to the execute function, this allows the user to handle it
 * completely using ASM.
 * 
 * File Navigation Pane, Search Pane and Work Pane based off of J-RET by WaterWolf - https://github.com/Waterwolf/Java-ReverseEngineeringTool
 * HexViewer pane based off of Re-Java's by Sami Koivu - http://rejava.sourceforge.net/
 * Java Decompiler is a modified version of FernFlower, Procyon and CFR.
 * Bytecode Decompiler base & ByteAnalysis lib by Bibl. - 
 * 
 * TODO:
 * Fix the fucking import jar method cause it's a bitch on memory (at the.bytecode.club.bytecodeviewer.JarUtils.getNode(JarUtils.java:83))
 * JSyntaxPane can be horribly slow for really big classfiles, might need to find a work around to this (create the syntaxpane object in the thread, then pass it to the GUI)s
 * Make the search results clickable
 * Add a tool to build a flowchart of all the classes, and what methods execute what classes, and those method, read chatlog
 * Middle mouse click should close tabs
 * http://i.imgur.com/yHaai9D.png
 * 
 *  
 * ----Beta 1.0-----:
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
 * ----Beta 1.1-----:
 * 10/19/2014 - Fixed harcoded \\.
 * ----Beta 1.2-----:
 * 10/19/2014 - Started importing Procyon and CFR decompilers.
 * 10/19/2014 - Partially finished importing Procyon and CFR, just need to finish export java files as zip.
 * ----Beta 1.3-----:
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
 * ----Beta 1.4-----:
 * 11/1/2014 - Fixed FernFlower save Java files on Unix.
 * 11/1/2014 - FernFlower now uses the settings for save Java files.
 * 11/1/2014 - Added Procyon save Java files (It uses the settings).
 * 11/1/2014 - Updated CFR to cfr_0_89.
 * 11/1/2014 - Added CFR save Java files (It uses the settings), however it relies on the file system, because of this if there is heavy name obfuscation, it could mess up for windows.
 * 
 * @author Konloch
 *
 */

public class BytecodeViewer {

	public static MainViewerGUI viewer = null;
    public static HashMap<String, ClassNode> loadedClasses = new HashMap<String, ClassNode>();
    public static HashMap<String, byte[]> loadedResources = new HashMap<String, byte[]>();
    private static String filesName = "recentfiles.bcv";
    private static String pluginsName = "recentplugins.bcv";
    private static ArrayList<String> recentFiles = DiskReader.loadArrayList(filesName, false);
    private static ArrayList<String> recentPlugins = DiskReader.loadArrayList(pluginsName, false);
	private static int maxRecentFiles = 25;
	public static  String fs = System.getProperty("file.separator");
	public static  String nl = System.getProperty("line.separator");
	public static String tempDirectory = "bcv_temp";
	public static String version = "Beta 1.4";
	
	public static void main(String[] args) {
		cleanup();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				cleanup();
			}
		});
		Thread versionChecker = new Thread() {
			@Override
			public void run() {
				try {
					HttpURLConnection connection = (HttpURLConnection) new URL("https://raw.githubusercontent.com/Konloch/bytecode-viewer/master/VERSION").openConnection();
					connection.setUseCaches(false);
					connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0");
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String version = reader.readLine();
					reader.close();
					if(!BytecodeViewer.version.equals(version))
						showMessage("You're running an outdated version of Bytecode Viewer, current version: " + BytecodeViewer.version + ", latest version: " + version+nl+nl+"https://github.com/Konloch/bytecode-viewer");
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		};
		versionChecker.start();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		viewer = new MainViewerGUI();
		resetRecentFilesMenu();
		viewer.setVisible(true);
	}
	
	public static ClassNode getClassNode(String name) {
		if(loadedClasses.containsKey(name))
			return loadedClasses.get(name);
		return null;
	}
	
	public static ArrayList<ClassNode> getLoadedClasses() {
		ArrayList<ClassNode> a = new ArrayList<ClassNode>();
		if(loadedClasses != null)
			for (Entry<String, ClassNode> entry : loadedClasses.entrySet()) {
				Object value = entry.getValue();
				ClassNode cln = (ClassNode)value;
				a.add(cln);
			}
		return a;
	}
	
	public static void openFiles(File[] files) {
        BytecodeViewer.viewer.setC(true);
        BytecodeViewer.viewer.setIcon(true);
        
        for (final File f : files) {
	            final String fn = f.getName();
	            if (fn.endsWith(".jar")) {
	                try {
	                    JarUtils.put(f, BytecodeViewer.loadedClasses);
	                } catch (final Exception e) {
	                    e.printStackTrace();
	                }
	                
	            }
	            else if (fn.endsWith(".class")) {
	                try {
	                    final ClassNode cn = JarUtils.getNode(JarUtils.getBytes(new FileInputStream(f)));
	                    BytecodeViewer.loadedClasses.put(cn.name, cn);
	                } catch (final Exception e) {
	                    e.printStackTrace();
	                }
	            }
        }
        
        for(File f : files)
        	BytecodeViewer.addRecentFile(f);
        
        BytecodeViewer.viewer.setC(false);
        BytecodeViewer.viewer.setIcon(false);
        
		MainViewerGUI.getComponent(FileNavigationPane.class).updateTree();
	}
	
	public static void startPlugin(File plugin) {
		if(!plugin.exists())
			return;
		
		try {
			PluginManager.runPlugin(plugin);
		} catch (Exception e) {
			e.printStackTrace();
		}
		addRecentPlugin(plugin);
	}
	
	public static void showMessage(String message) {
   		JOptionPane.showMessageDialog(viewer, message);
	}
	
	@SuppressWarnings("deprecation")
	public static void resetWorkSpace() {
	    JOptionPane pane = new JOptionPane("Are you sure you want to reset the workspace?\n\rIt will also reset your file navigator and search.");
	    Object[] options = new String[] { "Yes", "No" };
	    pane.setOptions(options);
	    JDialog dialog = pane.createDialog(viewer, "Bytecode Viewer - Reset Workspace");
	    dialog.show();
	    Object obj = pane.getValue(); 
	    int result = -1;
	    for (int k = 0; k < options.length; k++)
	    	if (options[k].equals(obj))
	    		result = k;
	        
	     
		if(result == 0) {
			loadedResources.clear();
    		loadedClasses.clear();
			MainViewerGUI.getComponent(FileNavigationPane.class).resetWorkspace();
			MainViewerGUI.getComponent(WorkPane.class).resetWorkspace();
			MainViewerGUI.getComponent(SearchingPane.class).resetWorkspace();
		}
	}

	private static ArrayList<String> killList = new ArrayList<String>();
	public static void addRecentFile(File f) {
		for(int i = 0; i < recentFiles.size(); i++) { //remove dead strings
			String s = recentFiles.get(i);
			if(s.isEmpty() || i > maxRecentFiles)
				killList.add(s);
		}
		if(!killList.isEmpty()) {
			for(String s : killList)
				recentFiles.remove(s);
			killList.clear();
		}
		
		if(recentFiles.contains(f.getAbsolutePath())) //already added on the list
			recentFiles.remove(f.getAbsolutePath());
		if(recentFiles.size() >= maxRecentFiles)
			recentFiles.remove(maxRecentFiles-1); //zero indexing
		
		recentFiles.add(0, f.getAbsolutePath());
		DiskWriter.replaceFile(filesName, quickConvert(recentFiles), false);
		resetRecentFilesMenu();
	}

	private static ArrayList<String> killList2 = new ArrayList<String>();
	public static void addRecentPlugin(File f) {
		for(int i = 0; i < recentPlugins.size(); i++) { //remove dead strings
			String s = recentPlugins.get(i);
			if(s.isEmpty() || i > maxRecentFiles)
				killList2.add(s);
		}
		if(!killList2.isEmpty()) {
			for(String s : killList2)
				recentPlugins.remove(s);
			killList2.clear();
		}
		
		if(recentPlugins.contains(f.getAbsolutePath())) //already added on the list
			recentPlugins.remove(f.getAbsolutePath());
		if(recentPlugins.size() >= maxRecentFiles)
			recentPlugins.remove(maxRecentFiles-1); //zero indexing
		
		recentPlugins.add(0, f.getAbsolutePath());
		DiskWriter.replaceFile(pluginsName, quickConvert(recentPlugins), false);
		resetRecentFilesMenu();
	}
	
	public static void resetRecentFilesMenu() {
		viewer.mnRecentFiles.removeAll();
		for(String s : recentFiles)
			if(!s.isEmpty()) {
				JMenuItem m = new JMenuItem(s);
				m.addActionListener(new ActionListener() {
		        	public void actionPerformed(ActionEvent e) {
		        		JMenuItem m = (JMenuItem)e.getSource();
		        		openFiles(new File[]{new File(m.getText())});
		        	}
				});
				viewer.mnRecentFiles.add(m);
			}
		viewer.mnRecentPlugins.removeAll();
		for(String s : recentPlugins)
			if(!s.isEmpty()) {
				JMenuItem m = new JMenuItem(s);
				m.addActionListener(new ActionListener() {
		        	public void actionPerformed(ActionEvent e) {
		        		JMenuItem m = (JMenuItem)e.getSource();
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
		
		while(!tempF.exists()) { //keep making dirs
			try {
				tempF.mkdir();
				Thread.sleep(1);
			} catch (Exception e) {
			}
		}
	}
	
	private static String quickConvert(ArrayList<String> a) {
		String s = "";
		for(String r : a)
			s += r+"\r";
		return s;
	}
	
}
