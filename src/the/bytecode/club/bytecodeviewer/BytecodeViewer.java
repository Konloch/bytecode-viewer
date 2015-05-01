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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import me.konloch.kontainer.io.DiskReader;
import me.konloch.kontainer.io.DiskWriter;
import me.konloch.kontainer.io.HTTPRequest;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.api.ClassNodeLoader;
import the.bytecode.club.bytecodeviewer.gui.ClassViewer;
import the.bytecode.club.bytecodeviewer.gui.FileNavigationPane;
import the.bytecode.club.bytecodeviewer.gui.MainViewerGUI;
import the.bytecode.club.bytecodeviewer.gui.RunOptions;
import the.bytecode.club.bytecodeviewer.gui.SearchingPane;
import the.bytecode.club.bytecodeviewer.gui.SystemErrConsole;
import the.bytecode.club.bytecodeviewer.gui.WorkPane;
import the.bytecode.club.bytecodeviewer.obfuscators.mapping.Refactorer;

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
 * 
 * 3.0.0: (RETIREMENT PARTY, WOHOOO)
 * maybe just do AMS5 then obfuscate the dex2jar shit.
 * Add obfuscation:
 *    - Add integer boxing and other obfuscation methods contra implemented
 *    - Insert unadded/debug opcodes to try to fuck up decompilers
 *    - ClassAnylyzterAdapter
 * Add the jump/save mark system Ida Pro has.
 * Add class annotations to bytecode decompiler.
 * EVERYTHING BUG FREE, CHECK 100%
 * bytecode editor that works by editing per method instead of entire class, methods are in a pane like the file navigator
 * Make the tabs menu and middle mouse button click work on the tab itself not just the close button.
 * 
 * before 3.0.0:
 * make it use that global last used inside of export as jar
 * Spiffy up the plugin console with hilighted lines
 * Take https://github.com/ptnkjke/Java-Bytecode-Editor visualize
 * fix the randomly sometimes fucked up names on file navigation bug
 * make zipfile not include the decode shit
 * When you drag a folder, it must add the folder name not just the child into the root jtree path
 * add stackmapframes to bytecode decompiler
 * add stackmapframes remover?
 * In BCV if you open a class and the name is so big, you cannot close because the [X] does not appear."
 * 
 * -----2.9.5-----:
 * 05/01/2015 - Added 'pingback' for statistics (to track how many people globally use BCV)
 * 
 * @author Konloch
 * 
 */

public class BytecodeViewer {

	/*per version*/
	public static String version = "2.9.5";
	public static String krakatauVersion = "2";
	/*the rest*/
	public static MainViewerGUI viewer = null;
	public static ClassNodeLoader loader = new ClassNodeLoader(); //might be insecure due to assholes targeting BCV, however that's highly unlikely.
	public static String python = "";
	public static String rt = "";
	public static String library = "";
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
	public static ArrayList<Process> krakatau = new ArrayList<Process>();
	public static Refactorer refactorer = new Refactorer();
	public static boolean pingback = false;
	
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
	
	/**
	 * Main startup
	 * @param args files you want to open
	 */
	public static void main(String[] args) {
		System.setSecurityManager(sm);
		checkKrakatau();
		System.out.println("https://the.bytecode.club - Created by @Konloch - Bytecode Viewer " + version);
		cleanup();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				for(Process krakatau : krakatau)
					krakatau.destroy();
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
		
		if(!pingback) {
			pingback = true;
			pingback();
		}
	}
	
	public static void pingback() {
		JOptionPane pane = new JOptionPane(
				"Would you like to 'pingback' to https://bytecodeviewer.com to be counted in the global users for BCV?");
		Object[] options = new String[] { "Yes", "No" };
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
				new HTTPRequest(new URL("https://bytecodeviewer.com/add.php")).read();
			} catch (Exception e) {
				new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
			}
		}
	}
	
	/**
	 * because Smali and Baksmali System.exit if it failed
	 * @param i
	 */
	public static void exit(int i) {
		
	}

	/**
	 * Returns the currently opened ClassNode
	 * @return the currently opened ClassNode
	 */
	public static ClassNode getCurrentlyOpenedClassNode() {
		return viewer.workPane.getCurrentViewer().cn;
	}

	/**
	 * Returns the ClassNode by the specified name
	 * @param name the class name
	 * @return the ClassNode instance
	 */
	public static ClassNode getClassNode(String name) {
		if (loadedClasses.containsKey(name))
			return loadedClasses.get(name);
		return null;
	}
	
	/**
	 * Grabs the file contents of the loaded resources.
	 * @param name the file name
	 * @return the file contents as a byte[]
	 */
	public static byte[] getFileContents(String name) {
		if (loadedResources.containsKey(name))
			return loadedResources.get(name);
		return null;
	}

	/**
	 * Replaces an old node with a new instance
	 * @param oldNode the old instance
	 * @param newNode the new instance
	 */
	public static void updateNode(ClassNode oldNode, ClassNode newNode) {
		BytecodeViewer.loadedClasses.remove(oldNode.name);
		BytecodeViewer.loadedClasses.put(oldNode.name, newNode);
	}

	/**
	 * Gets all of the loaded classes as an array list
	 * @return the loaded classes as an array list
	 */
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
	
	/**
	 * Compile all of the compilable panes that're opened.
	 * @param message if it should send a message saying it's compiled sucessfully.
	 * @return true if no errors, false if it failed to compile.
	 */
	public static boolean compile(boolean message) {
		boolean actuallyTried = false;
		
		for(java.awt.Component c : BytecodeViewer.viewer.workPane.getLoadedViewers()) {
			if(c instanceof ClassViewer) {
				ClassViewer cv = (ClassViewer) c;
				if(		cv.smali1 != null && cv.smali1.isEditable() ||
						cv.smali2 != null &&  cv.smali2.isEditable() ||
						cv.smali3 != null && cv.smali3.isEditable())
				{
					actuallyTried = true;
					Object smali[] = cv.getSmali();
					if(smali != null) {
						ClassNode origNode = (ClassNode) smali[0];
						String smaliText = (String) smali[1];
						byte[] smaliCompiled = the.bytecode.club.bytecodeviewer.compilers.Compiler.smali.compile(smaliText, origNode.name);
						if(smaliCompiled != null) {
							ClassNode newNode = JarUtils.getNode(smaliCompiled);
							BytecodeViewer.updateNode(origNode, newNode);
						} else {
							BytecodeViewer.showMessage("There has been an error with assembling your Smali code, please check this. Class: " + origNode.name);
							return false;
						}
					}
				}


				if(cv.krakatau1 != null && cv.krakatau1.isEditable() ||
					cv.krakatau2 != null &&  cv.krakatau2.isEditable() ||
					cv.krakatau3 != null && cv.krakatau3.isEditable())
				{
					actuallyTried = true;
					Object krakatau[] = cv.getKrakatau();
					if(krakatau != null) {
						ClassNode origNode = (ClassNode) krakatau[0];
						String krakatauText = (String) krakatau[1];
						byte[] krakatauCompiled = the.bytecode.club.bytecodeviewer.compilers.Compiler.krakatau.compile(krakatauText, origNode.name);
						if(krakatauCompiled != null) {
							ClassNode newNode = JarUtils.getNode(krakatauCompiled);
							BytecodeViewer.updateNode(origNode, newNode);
						} else {
							BytecodeViewer.showMessage("There has been an error with assembling your Krakatau Bytecode, please check this. Class: " + origNode.name);
							return false;
						}
					}
				}

				if(		cv.java1 != null && cv.java1.isEditable() ||
						cv.java2 != null &&  cv.java2.isEditable() ||
						cv.java3 != null && cv.java3.isEditable())
				{
					actuallyTried = true;
					Object java[] = cv.getJava();
					if(java != null) {
						ClassNode origNode = (ClassNode) java[0];
						String javaText = (String) java[1];
						
						SystemErrConsole errConsole = new SystemErrConsole("Java Compile Issues");
						errConsole.setText("Error compiling class: " + origNode.name + nl + "Keep in mind most decompilers cannot produce compilable classes"+nl+nl);
						
						byte[] javaCompiled = the.bytecode.club.bytecodeviewer.compilers.Compiler.java.compile(javaText, origNode.name);
						if(javaCompiled != null) {
							ClassNode newNode = JarUtils.getNode(javaCompiled);
							BytecodeViewer.updateNode(origNode, newNode);
							errConsole.finished();
						} else {
							errConsole.pretty();
							errConsole.setVisible(true);
							errConsole.finished();
							return false;
						}
					}
				}
			}
		}
		
		if(message)
			if(actuallyTried)
				BytecodeViewer.showMessage("Compiled Successfully.");
			else
				BytecodeViewer.showMessage("You have no editable panes opened, make one editable and try again.");
		
		return true;
	}

	/**
	 * Drops the latest krakatau safely
	 */
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
	
	/**
	 * Opens a file, optional if it should append to the recent files menu
	 * @param files the file(s) you wish to open
	 * @param recentFiles if it should append to the recent files menu
	 */
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
								} else if(fn.endsWith(".apk")) {
									try {
										if(viewer.decodeAPKResources.isSelected()) {
											File decodedResources = new File(tempDirectory + fs + MiscUtils.randomString(32) + ".apk");
											APKTool.decodeResources(f, decodedResources);
											JarUtils.loadResources(decodedResources);
										}
										
										JarUtils.loadResources(f);
										
										String name = getRandomizedName()+".jar";
										File output = new File(tempDirectory + fs + name);
										Dex2Jar.dex2Jar(f, output);
										BytecodeViewer.viewer.setIcon(false);
										openFiles(new File[]{output}, false);
									} catch (final Exception e) {
										new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
									}
									return;
								} else if(fn.endsWith(".dex")) {
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
								} else {
									byte[] bytes = JarUtils.getBytes(new FileInputStream(f));
									BytecodeViewer.loadedResources.put(f.getName(), bytes);
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

	/**
	 * Starts the specified plugin
	 * @param plugin the file of the plugin
	 */
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

	/**
	 * Send a message to alert the user
	 * @param message the message you need to send
	 */
	public static void showMessage(String message) {
		JOptionPane.showMessageDialog(viewer, message);
	}

	/**
	 * Resets the workspace with optional user input required
	 * @param ask if should require user input or not
	 */
	public static void resetWorkSpace(boolean ask) {
		if(!ask) {
			loadedResources.clear();
			loadedClasses.clear();
			MainViewerGUI.getComponent(FileNavigationPane.class).resetWorkspace();
			MainViewerGUI.getComponent(WorkPane.class).resetWorkspace();
			MainViewerGUI.getComponent(SearchingPane.class).resetWorkspace();
			the.bytecode.club.bytecodeviewer.api.BytecodeViewer.getClassNodeLoader().clear();
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
	 * @return the unique randomized name of 25 characters.
	 */
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

	
	/**
	 * Replaces an old node with a new instance
	 * @param oldNode the old instance
	 * @param newNode the new instance
	 */
	public static void relocate(String name, ClassNode node) {
		if (BytecodeViewer.loadedClasses.containsKey(name))
			BytecodeViewer.loadedClasses.remove(name);
		
		BytecodeViewer.loadedClasses.put(node.name, node);
	}
	
	/**
	 * Returns the BCV directory
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
	 * @return true if the os.name property contains 'win'
	 */
	private static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

	/**
	 * Runs the windows command to hide files
	 * @param f file you want hidden
	 */
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

	/**
	 * Converts an array list to a string
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
	 * @param e
	 */
    public static void checkHotKey(KeyEvent e) {
    	if(System.currentTimeMillis() - last <= (4000))
    		return;
    	
        if ((e.getKeyCode() == KeyEvent.VK_O) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
        	last = System.currentTimeMillis();
        	JFileChooser fc = new JFileChooser();
        	try {
        		fc.setSelectedFile(new File(BytecodeViewer.lastDirectory));
        	} catch(Exception e2) {
        		
        	}
			fc.setFileFilter(viewer.new APKDEXJarZipClassFileFilter());
			fc.setFileHidingEnabled(false);
			fc.setAcceptAllFileFilterUsed(false);
			int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				BytecodeViewer.lastDirectory = fc.getSelectedFile().getAbsolutePath();
				try {
					BytecodeViewer.viewer.setIcon(true);
					BytecodeViewer.openFiles(new File[] { fc.getSelectedFile() }, true);
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
        	BytecodeViewer.compile(true);
        } else if ((e.getKeyCode() == KeyEvent.VK_R) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
        	last = System.currentTimeMillis();
			if(BytecodeViewer.getLoadedClasses().isEmpty()) {
				BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
				return;
			}
			new RunOptions().setVisible(true);
        } else if ((e.getKeyCode() == KeyEvent.VK_S) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
        	last = System.currentTimeMillis();

			if(BytecodeViewer.getLoadedClasses().isEmpty()) {
				BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
				return;
			}
			if(viewer.autoCompileSmali.isSelected() && !BytecodeViewer.compile(false))
				return;
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(viewer.new ZipFileFilter());
			fc.setFileHidingEnabled(false);
			fc.setAcceptAllFileFilterUsed(false);
			int returnVal = fc.showSaveDialog(viewer);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				if(!file.getAbsolutePath().endsWith(".zip"))
					file = new File(file.getAbsolutePath()+".zip");
				
				if(file.exists()) {
					JOptionPane pane = new JOptionPane(
							"Are you sure you wish to overwrite this existing file?");
					Object[] options = new String[] { "Yes", "No" };
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
        } else if ((e.getKeyCode() == KeyEvent.VK_W) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
        	last = System.currentTimeMillis();
        	if(viewer.workPane.getCurrentViewer() != null)
        		viewer.workPane.tabs.remove(viewer.workPane.getCurrentViewer());
        }
    }
}
