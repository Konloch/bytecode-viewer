package the.bytecode.club.bytecodeviewer.gui;

import javax.swing.JEditorPane;
import javax.swing.JFrame;

import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JProgressBar;

import java.awt.GridBagConstraints;

import javax.swing.JScrollPane;

import java.awt.Insets;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.text.html.HTMLEditorKit;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Resources;
import the.bytecode.club.bytecodeviewer.Settings;
import me.konloch.kontainer.io.HTTPRequest;

/**
 * First boot, will automatically connect to BytecodeViewer for PingBack
 * It'll Check BCV version
 * then it'll download repos from the library
 * After it's completed and compared MD5 hashes, it simply dynamically loads all jars in /libs/ folder of BCV
 * While all of this is happening, it'll show the HOW-TO guide for BCV
 * 
 * Download Failed? Corrupt Jar? Append -clean to BCV startup
 * 
 * @author Konloch
 *
 */

public class BootScreen extends JFrame {
	
	private static final long serialVersionUID = -1098467609722393444L;

	private static boolean FIRST_BOOT = false;
	
	private JProgressBar progressBar = new JProgressBar();
	
	public BootScreen() throws IOException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setIconImages(Resources.iconList);
		setSize(new Dimension(600, 800));
		setTitle("Bytecode Viewer Boot Screen - Starting Up");
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 24;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		getContentPane().add(scrollPane, gbc_scrollPane);
		
	    JEditorPane editorPane = new JEditorPane();
	    editorPane.setEditorKit(new HTMLEditorKit());
	    
	    editorPane.setText(convertStreamToString(BytecodeViewer.class.getClassLoader().getResourceAsStream("resources/intro.html")));

		scrollPane.setViewportView(editorPane);
		
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_progressBar.gridx = 0;
		gbc_progressBar.gridy = 24;
		getContentPane().add(progressBar, gbc_progressBar);
		this.setLocationRelativeTo(null);
	}
	
	static String convertStreamToString(java.io.InputStream is) throws IOException {
	    @SuppressWarnings("resource")
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    String string =  s.hasNext() ? s.next() : "";
	    is.close();
	    s.close();
	    return string;
	}
	
	public void DO_FIRST_BOOT(String args[]) {
		this.setVisible(true);
		if(FIRST_BOOT)
			return;
		
		FIRST_BOOT = true;
		boolean foundAtleastOne = false;
		

		setTitle("Bytecode Viewer Boot Screen - Checking Libraries...");
		File libsDirectory = new File(BytecodeViewer.libsDirectory);
		
		try {
			int completedCheck = 0;
			List<String> urlList = new ArrayList<String>();
			HTTPRequest req = new HTTPRequest(new URL("https://github.com/Konloch/bytecode-viewer/tree/master/libs"));
			for(String s : req.read())
				if(s.contains("href=\"/Konloch/bytecode-viewer/blob/master/libs/")) {
					urlList.add("https://github.com"+s.split("<a href=")[1].split("\"")[1]);
					foundAtleastOne = true;
				}
			
			if(!foundAtleastOne) {
				new the.bytecode.club.bytecodeviewer.api.ExceptionUI("Bytecode Viewer ran into an issue, for some reason github is not returning what we're expecting. Please try rebooting, if this issue persists please contact @Konloch.");
				return;
			}
			
			if(args.length >= 1)
				if(args[0].equalsIgnoreCase("-clean"))
					libsDirectory.delete();
			
			if(!libsDirectory.exists())
				libsDirectory.mkdir();
			
			List<String> libsList = new ArrayList<String>();
			List<String> libsFileList = new ArrayList<String>();
			for(File f : libsDirectory.listFiles()) {
				libsList.add(f.getName());
				libsFileList.add(f.getAbsolutePath());
			}
						
			progressBar.setMaximum(urlList.size());
			
			for(String s : urlList) {
				String fileName = s.substring("https://github.com/Konloch/bytecode-viewer/blob/master/libs/".length(), s.length());
				if(!libsList.contains(fileName)) {
					setTitle("Bytecode Viewer Boot Screen - Downloading " + fileName);
					boolean passed = false;
					while(!passed) {
						InputStream is = null;
						FileOutputStream fos = null;
						try {
							is = new URL("https://github.com/Konloch/bytecode-viewer/raw/master/libs/" + fileName).openConnection().getInputStream();
							fos = new FileOutputStream(BytecodeViewer.libsDirectory + BytecodeViewer.fs + fileName);
							System.out.println("Downloading from "+s);
						    byte[] buffer = new byte[8192];
						        int len;
						        int downloaded = 0;
						        boolean flag = false;
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
						    libsFileList.add(BytecodeViewer.libsDirectory + BytecodeViewer.fs + fileName);
						} finally {
							try {
						            if (is != null) {
						                is.close();
						            }
							} finally {
						            if (fos != null) {
							            fos.flush();
						            }
						            if (fos != null) {
						                fos.close();
						            }
							}
						}
						System.out.println("Download finished!");
						passed = true;
					}
				}
				completedCheck++;
				progressBar.setValue(completedCheck);
			}

			if(BytecodeViewer.deleteForiegnLibraries) {
				setTitle("Bytecode Viewer Boot Screen - Checking & Deleting Foriegn/Outdated Libraries...");
				for(String s : libsFileList) {
					File f = new File(s);
					boolean delete = true;
					for(String urlS : urlList) {
						String fileName = urlS.substring("https://github.com/Konloch/bytecode-viewer/blob/master/libs/".length(), urlS.length());
						if(fileName.equals(f.getName())) {
							delete = false;
						}
					}
					if(delete) {
						f.delete();
						System.out.println("Detected & Deleted Foriegn/Outdated Jar/File: " + f.getName());
					}
				}
			}
			
			setTitle("Bytecode Viewer Boot Screen - Loading Libraries...");
			
			for(String s : libsFileList ) {
				if(s.endsWith(".jar")) {
					File f = new File(s);
					if(f.exists()) {
						setTitle("Bytecode Viewer Boot Screen - Loading Library " + f.getName());
						System.out.println(f.getName());
						
						try {
							JarFile jarFile = new JarFile(s);
							Enumeration<JarEntry> e = jarFile.entries();
							ClassPathHack.addFile(f);
							while (e.hasMoreElements()) {
								JarEntry je = (JarEntry) e.nextElement();
								if(je.isDirectory() || !je.getName().endsWith(".class")){
									continue;
								}
								try {
							        String className = je.getName().substring(0,je.getName().length()-6);
							        className = className.replace('/', '.');
							        ClassLoader.getSystemClassLoader().loadClass(className);
								} catch(java.lang.VerifyError | java.lang.ExceptionInInitializerError | java.lang.IncompatibleClassChangeError | java.lang.NoClassDefFoundError | Exception e2) {
							        	//ignore
								}
							}
							jarFile.close();
						} catch(java.util.zip.ZipException e) {
							e.printStackTrace();
							f.delete();
							BytecodeViewer.showMessage("Error, Library " + f.getName() + " is corrupt, please restart to redownload it.");
						}
					}
				}
			}
			setTitle("Bytecode Viewer Boot Screen - Booting!");
			
		} catch(Exception e) {
			Settings.saveGUI();
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			e.printStackTrace();
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI("Bytecode Viewer ran into an error while booting, trying to force it anyways."+ BytecodeViewer.nl+ BytecodeViewer.nl+
					"Please ensure you have an active internet connection and restart BCV. If this presists please visit http://github.com/Konloch/Bytecode-Viewer or http://bytecodeviewer.com"+ BytecodeViewer.nl + BytecodeViewer.nl + sw.toString());
		}

		setTitle("Bytecode Viewer Boot Screen - Finished");
		
		BytecodeViewer.BOOT(args);
		
		if(BytecodeViewer.pingback) {
			BytecodeViewer.PingBack.start();
			BytecodeViewer.pingback = true;
		}
		
		if(BytecodeViewer.viewer.chckbxmntmNewCheckItem_12.isSelected())
			BytecodeViewer.versionChecker.start();
		
		this.setVisible(false);
	}

	public static class ClassPathHack {
		  private static final Class<?>[] parameters = new Class[] {URL.class};

		  public static void addFile(File f) throws IOException {
		    // f.toURL is deprecated
		    addURL(f.toURI().toURL());
		  }

		  protected static void addURL(URL u) throws IOException {
		    URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		    Class<?> sysclass = URLClassLoader.class;

		    try {
		      Method method = sysclass.getDeclaredMethod("addURL", parameters);
		      method.setAccessible(true);
		      method.invoke(sysloader, u);
		    } catch (Exception e) {
		      e.printStackTrace();
		    }

		  }
	}
	
}
