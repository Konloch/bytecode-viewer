package the.bytecode.club.bytecodeviewer.gui;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JCheckBoxMenuItem;

import org.apache.commons.codec.binary.Base64;
import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.FileChangeNotifier;
import the.bytecode.club.bytecodeviewer.JarUtils;
import the.bytecode.club.bytecodeviewer.decompilers.java.CFRDecompiler;
import the.bytecode.club.bytecodeviewer.decompilers.java.FernFlowerDecompiler;
import the.bytecode.club.bytecodeviewer.decompilers.java.ProcyonDecompiler;
import the.bytecode.club.bytecodeviewer.plugins.AllatoriStringDecrypter;
import the.bytecode.club.bytecodeviewer.plugins.PluginManager;
import the.bytecode.club.bytecodeviewer.plugins.ShowAllStrings;
import the.bytecode.club.bytecodeviewer.plugins.ShowMainMethods;
import the.bytecode.club.bytecodeviewer.plugins.ZKMStringDecrypter;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JRadioButtonMenuItem;


public class MainViewerGUI extends JFrame implements FileChangeNotifier {

    final FernFlowerDecompiler ff_dc = new FernFlowerDecompiler();
    final ProcyonDecompiler proc_dc = new ProcyonDecompiler();
    final CFRDecompiler cfr_dc = new CFRDecompiler();
    
	private static final long serialVersionUID = 1851409230530948543L;
	public JCheckBoxMenuItem debugHelpers = new JCheckBoxMenuItem("Debug Helpers");
	private JSplitPane sp1;
	private JSplitPane sp2;
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
    private final JSeparator separator_2 = new JSeparator();
    public JCheckBoxMenuItem srcSyntax = new JCheckBoxMenuItem("Source Code Syntax");
    public JCheckBoxMenuItem bycSyntax = new JCheckBoxMenuItem("Bytecode Syntax");
    JCheckBoxMenuItem sourcePane = new JCheckBoxMenuItem("Source Pane");
    JCheckBoxMenuItem bytecodePane = new JCheckBoxMenuItem("Bytecode Pane");
    JCheckBoxMenuItem hexPane = new JCheckBoxMenuItem("Hex Pane");
    private final JMenuItem mntmNewWorkspace = new JMenuItem("New Workspace");
    public JMenu mnRecentFiles = new JMenu("Recent Files");
    private final JMenuItem mntmNewMenuItem = new JMenuItem("Save Java Files As..");
    private final JMenuItem mntmAbout = new JMenuItem("About");
    private AboutWindow aboutWindow = new AboutWindow();
    private final JSeparator separator_3 = new JSeparator();
    private final JMenu mnNewMenu_1 = new JMenu("Plugins");
    private final JMenuItem mntmStartExternalPlugin = new JMenuItem("Open Plugin..");
    private final JSeparator separator_4 = new JSeparator();
    public JMenu mnRecentPlugins = new JMenu("Recent Plugins");
    private final JSeparator separator_5 = new JSeparator();
    private final JMenuItem mntmStartZkmString = new JMenuItem("ZKM String Decrypter");
    private final JMenuItem mntmNewMenuItem_1 = new JMenuItem("Malicious Code Scanner");
    private final JMenuItem mntmNewMenuItem_2 = new JMenuItem("Allatori String Decrypter");
    private final JMenuItem mntmShowAllStrings = new JMenuItem("Show All Strings");
    private final JMenuItem mntmShowMainMethods = new JMenuItem("Show Main Methods");
    private final JMenuItem mntmNewMenuItem_3 = new JMenuItem("Save As Jar..");
    private JMenuBar menuBar = new JMenuBar();
    public JCheckBoxMenuItem chckbxmntmNewCheckItem = new JCheckBoxMenuItem("Allow Only ASCII Characters In Strings");
    private final JMenuItem mntmReplaceStrings = new JMenuItem("Replace Strings");
    private final JMenuItem mntmNewMenuItem_4 = new JMenuItem("");
    private final JMenu mnNewMenu_2 = new JMenu("Java Decompiler");
    public final JRadioButtonMenuItem fernflowerDec = new JRadioButtonMenuItem("FernFlower");
    public final JRadioButtonMenuItem procyonDec = new JRadioButtonMenuItem("Procyon");
    public final JRadioButtonMenuItem cfrDec = new JRadioButtonMenuItem("CFR");
	public final ButtonGroup decompilerGroup = new ButtonGroup();
    private final JMenu mnNewMenu_3 = new JMenu("CFR");
    private final JMenu mnNewMenu_4 = new JMenu("Procyon");
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

	
    public void setC(boolean busy) {
    	if(busy) {
    		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    		for(Component c : this.getComponents())
    			c.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    		
    		sp1.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    		sp2.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    		
    		for(VisibleComponent c : rfComps) {
    			c.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    			if(c instanceof WorkPane) {
    				WorkPane w = (WorkPane)c;
    				for(Component c2 : w.tabs.getComponents())
    					c2.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    			}
    		}
    	} else {
    		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    		for(Component c : this.getComponents())
    			c.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    		
    		sp1.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    		sp2.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    		
    		for(VisibleComponent c : rfComps) {
    			c.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    			if(c instanceof WorkPane) {
    				WorkPane w = (WorkPane)c;
    				for(Component c2 : w.tabs.getComponents())
    					c2.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    			}
    		}
    	}
    }
    
    public void setIcon(final boolean busy) {
    	  SwingUtilities.invokeLater(new Runnable() {
    		    public void run() {
			    	if(busy) {
			    		try {
				    		mntmNewMenuItem_4.setIcon(new ImageIcon(getClass().getResource("/resources/1.gif")));
			    		} catch(NullPointerException e) {
			    			mntmNewMenuItem_4.setIcon(new ImageIcon(b642IMG("R0lGODlhEAALAPQAAP///wAAANra2tDQ0Orq6gcHBwAAAC8vL4KCgmFhYbq6uiMjI0tLS4qKimVlZb6+vicnJwUFBU9PT+bm5tjY2PT09Dk5Odzc3PLy8ra2tqCgoMrKyu7u7gAAAAAAAAAAACH5BAkLAAAAIf4aQ3JlYXRlZCB3aXRoIGFqYXhsb2FkLmluZm8AIf8LTkVUU0NBUEUyLjADAQAAACwAAAAAEAALAAAFLSAgjmRpnqSgCuLKAq5AEIM4zDVw03ve27ifDgfkEYe04kDIDC5zrtYKRa2WQgAh+QQJCwAAACwAAAAAEAALAAAFJGBhGAVgnqhpHIeRvsDawqns0qeN5+y967tYLyicBYE7EYkYAgAh+QQJCwAAACwAAAAAEAALAAAFNiAgjothLOOIJAkiGgxjpGKiKMkbz7SN6zIawJcDwIK9W/HISxGBzdHTuBNOmcJVCyoUlk7CEAAh+QQJCwAAACwAAAAAEAALAAAFNSAgjqQIRRFUAo3jNGIkSdHqPI8Tz3V55zuaDacDyIQ+YrBH+hWPzJFzOQQaeavWi7oqnVIhACH5BAkLAAAALAAAAAAQAAsAAAUyICCOZGme1rJY5kRRk7hI0mJSVUXJtF3iOl7tltsBZsNfUegjAY3I5sgFY55KqdX1GgIAIfkECQsAAAAsAAAAABAACwAABTcgII5kaZ4kcV2EqLJipmnZhWGXaOOitm2aXQ4g7P2Ct2ER4AMul00kj5g0Al8tADY2y6C+4FIIACH5BAkLAAAALAAAAAAQAAsAAAUvICCOZGme5ERRk6iy7qpyHCVStA3gNa/7txxwlwv2isSacYUc+l4tADQGQ1mvpBAAIfkECQsAAAAsAAAAABAACwAABS8gII5kaZ7kRFGTqLLuqnIcJVK0DeA1r/u3HHCXC/aKxJpxhRz6Xi0ANAZDWa+kEAA7"), ""));
			    		}
			    	} else
			    		mntmNewMenuItem_4.setIcon(null);
			    	mntmNewMenuItem_4.updateUI();
    		    }
    	  });
    }
    
	/**
	 * Decodes a Base64 String as a BufferedImage
	 */
    public BufferedImage b642IMG(String imageString) {
        BufferedImage image = null;
        byte[] imageByte;
        
        try {
            imageByte = Base64.decodeBase64(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return image;
    }

    public MainViewerGUI() {
		decompilerGroup.add(fernflowerDec);
		decompilerGroup.add(procyonDec);
		decompilerGroup.add(cfrDec);
		decompilerGroup.setSelected(procyonDec.getModel(), true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//fernflower
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
		srcSyntax.setSelected(true);
		bycSyntax.setSelected(true);
		debugHelpers.setSelected(true);
		sourcePane.setSelected(true);
		bytecodePane.setSelected(true);
		//cfr
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
		//procyon
		/*none*/
		//other
		chckbxmntmAppendBrackets.setSelected(true);
		
        setJMenuBar(menuBar);
        
        JMenu mnNewMenu = new JMenu("File");
        menuBar.add(mnNewMenu);
        
        final JFrame This = this;
        mntmNewWorkspace.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		BytecodeViewer.resetWorkSpace();
        	}
        });
        
        JMenuItem mntmLoadJar = new JMenuItem("Add..");
        mntmLoadJar.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new JarZipClassFileFilter());
				fc.setFileHidingEnabled(false);
				fc.setAcceptAllFileFilterUsed(false);
				int returnVal = fc.showOpenDialog(This);
				
		        if (returnVal == JFileChooser.APPROVE_OPTION)
					try {
						BytecodeViewer.viewer.setC(true);
						BytecodeViewer.openFiles(new File[]{fc.getSelectedFile()});
						BytecodeViewer.viewer.setC(false);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
              }
        });
         mnNewMenu.add(mntmLoadJar);
        
        mnNewMenu.add(mntmNewWorkspace);
        
        JMenuItem mntmSave = new JMenuItem("Save Files As..");
        mntmSave.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
                JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new ZipFileFilter());
				fc.setFileHidingEnabled(false);
				fc.setAcceptAllFileFilterUsed(false);
                int returnVal = fc.showSaveDialog(MainViewerGUI.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
					BytecodeViewer.viewer.setC(true);
	        		JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), file.getAbsolutePath());
					BytecodeViewer.viewer.setC(false);
                }
        	}
        });
        
        mnNewMenu.add(separator_3);
        mntmNewMenuItem_3.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new JarFileFilter());
				fc.setFileHidingEnabled(false);
				fc.setAcceptAllFileFilterUsed(false);
                int returnVal = fc.showSaveDialog(MainViewerGUI.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    String path = file.getAbsolutePath();
                    if(!path.endsWith(".jar"))
                    	path = path + ".jar";
	        		new ExportJar(path).setVisible(true);
                }
        	}
        });
        
        mnNewMenu.add(mntmNewMenuItem_3);
        mnNewMenu.add(mntmSave);
        mntmNewMenuItem.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
                JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new ZipFileFilter());
				fc.setFileHidingEnabled(false);
				fc.setAcceptAllFileFilterUsed(false);
                int returnVal = fc.showSaveDialog(MainViewerGUI.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
					BytecodeViewer.viewer.setC(true);
                    String path = file.getAbsolutePath();
                    if(!path.endsWith(".zip"))
                    	path = path + ".zip";
			        if(BytecodeViewer.viewer.decompilerGroup.isSelected(BytecodeViewer.viewer.fernflowerDec.getModel()))
			        	ff_dc.decompileToZip(path);
			        else if(BytecodeViewer.viewer.decompilerGroup.isSelected(BytecodeViewer.viewer.procyonDec.getModel()))
			        	proc_dc.decompileToZip(path);
			        else if(BytecodeViewer.viewer.decompilerGroup.isSelected(BytecodeViewer.viewer.cfrDec.getModel()))
			        	cfr_dc.decompileToZip(path);
					BytecodeViewer.viewer.setC(false);
                }
        	}
        });
        
        mnNewMenu.add(mntmNewMenuItem);
        
        JSeparator separator = new JSeparator();
        mnNewMenu.add(separator);
        
        mnNewMenu.add(mnRecentFiles);
        
        JSeparator separator_1 = new JSeparator();
        mnNewMenu.add(separator_1);
        mntmAbout.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		aboutWindow.setVisible(true);
        	}
        });
        
        mnNewMenu.add(mntmAbout);
        
        JMenuItem mntmExit = new JMenuItem("Exit");
        mnNewMenu.add(mntmExit);
        
        JMenu mnView = new JMenu("View");
        menuBar.add(mnView);
        
        mnView.add(sourcePane);
        mnView.add(bytecodePane);
        mnView.add(hexPane);
        
        mnView.add(separator_2);
        
        mnView.add(srcSyntax);
        
        mnView.add(bycSyntax);
        
        menuBar.add(mnNewMenu_2);
        
        mnNewMenu_2.add(procyonDec);
        
        mnNewMenu_2.add(cfrDec);
        
        mnNewMenu_2.add(fernflowerDec);
        
        menuBar.add(mnNewMenu_4);
        
        mnNewMenu_4.add(chckbxmntmNewCheckItem_6);
        
        mnNewMenu_4.add(chckbxmntmNewCheckItem_11);
        
        mnNewMenu_4.add(chckbxmntmShowDebugLine);
        
        mnNewMenu_4.add(chckbxmntmNewCheckItem_3);
        
        mnNewMenu_4.add(chckbxmntmNewCheckItem_4);
        
        mnNewMenu_4.add(chckbxmntmNewCheckItem_7);
        
        mnNewMenu_4.add(chckbxmntmSimplifyMemberReferences);
        
        mnNewMenu_4.add(mnMergeVariables);
        
        mnNewMenu_4.add(chckbxmntmNewCheckItem_8);
        
        mnNewMenu_4.add(chckbxmntmNewCheckItem_9);
        
        mnNewMenu_4.add(chckbxmntmNewCheckItem_10);
        
        mnNewMenu_4.add(chckbxmntmNewCheckItem_2);
        
        mnNewMenu_4.add(chckbxmntmNewCheckItem_5);
        
        mnNewMenu_4.add(chckbxmntmNewCheckItem_1);
        
        menuBar.add(mnNewMenu_3);
        
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
        
        JMenu mnDecompilerSettings = new JMenu("FernFlower");
        menuBar.add(mnDecompilerSettings);
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
        
        JMenu mnBytecodeDecompilerSettings = new JMenu("Bytecode Decompiler");
        menuBar.add(mnBytecodeDecompilerSettings);
        
        mnBytecodeDecompilerSettings.add(debugHelpers);
        
        mnBytecodeDecompilerSettings.add(chckbxmntmAppendBrackets);
        
        mnBytecodeDecompilerSettings.add(chckbxmntmNewCheckItem);
        
        menuBar.add(mnNewMenu_1);
        mnNewMenu_1.add(mntmStartExternalPlugin);
        mnNewMenu_1.add(separator_4);
        mnNewMenu_1.add(mnRecentPlugins);
        mnNewMenu_1.add(separator_5);
        mnNewMenu_1.add(mntmNewMenuItem_1);
        mnNewMenu_1.add(mntmShowMainMethods);
        mnNewMenu_1.add(mntmShowAllStrings);
        mntmReplaceStrings.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		if(!BytecodeViewer.loadedClasses.isEmpty())
            		new ReplaceStringsOptions().setVisible(true);
        		else
        			System.out.println("Plugin not ran, put some classes in first.");
        	}
        });
        
        mnNewMenu_1.add(mntmReplaceStrings);
        mnNewMenu_1.add(mntmNewMenuItem_2);
        mnNewMenu_1.add(mntmStartZkmString);
        
        menuBar.add(mntmNewMenuItem_4);
        

        mntmStartExternalPlugin.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new GroovyPythonRubyFileFilter());
				fc.setFileHidingEnabled(false);
				fc.setAcceptAllFileFilterUsed(false);
				int returnVal = fc.showOpenDialog(This);
				
		        if (returnVal == JFileChooser.APPROVE_OPTION)
					try {
						BytecodeViewer.viewer.setC(true);
						BytecodeViewer.startPlugin(fc.getSelectedFile());
						BytecodeViewer.viewer.setC(false);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
        	}
        });
        mntmStartZkmString.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		PluginManager.runPlugin(new ZKMStringDecrypter());
        	}
        });
        mntmNewMenuItem_2.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		PluginManager.runPlugin(new AllatoriStringDecrypter());
        	}
        });
        mntmNewMenuItem_1.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(!BytecodeViewer.loadedClasses.isEmpty())
        			new MaliciousCodeScannerOptions().setVisible(true);
        		else
        			System.out.println("Plugin not ran, put some classes in first.");
        	}
        });
        mntmShowAllStrings.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		PluginManager.runPlugin(new ShowAllStrings());
        	}
        });
        
        mntmShowMainMethods.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		PluginManager.runPlugin(new ShowMainMethods());
        	}
        });
        
		setSize(new Dimension(800, 400));
		setTitle("Bytecode Viewer " + BytecodeViewer.version + " - http://the.bytecode.club - @Konloch");
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setMaximumSize(new Dimension(12000, 32767));
		//scrollPane.setViewportView(tree);
		FileNavigationPane cn = new FileNavigationPane(this);
		cn.setMinimumSize(new Dimension(200, 50));
		//panel.add(cn);
		SearchingPane s = new SearchingPane(this);
		s.setPreferredSize(new Dimension(200, 50));
		s.setMinimumSize(new Dimension(200, 50));
		s.setMaximumSize(new Dimension(200, 2147483647));
		//panel.add(s);
		sp1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, cn, s);
		//panel.add(sp1);
		cn.setPreferredSize(new Dimension(200, 50));
		cn.setMaximumSize(new Dimension(200, 2147483647));
		WorkPane cv = new WorkPane(this);
		sp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sp1, cv);
		getContentPane().add(sp2);
		sp2.setResizeWeight(0.05);
		sp1.setResizeWeight(0.5);
		rfComps.add(cn);
		
		rfComps.add(s);
		rfComps.add(cv);
		this.setLocationRelativeTo(null);
	}

    @Override
    public void openClassFile(final String name, final ClassNode cn) {
        for (final VisibleComponent vc : rfComps) {
            vc.openClassFile(name, cn);
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
	
	public class GroovyPythonRubyFileFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
		    if (f.isDirectory())
		        return true;

		    String extension = getExtension(f);
		    if (extension != null)
		        return (extension.equals("gy") || extension.equals("groovy") ||
		        		extension.equals("py") || extension.equals("python") ||
		        		extension.equals("rb") || extension.equals("ruby"));
		    
		    return false;
		}

		@Override
		public String getDescription() {
			return "Groovy, Python or Ruby plugins.";
		}
	    
	    public String getExtension(File f) {
	        String ext = null;
	        String s = f.getName();
	        int i = s.lastIndexOf('.');

	        if (i > 0 &&  i < s.length() - 1)
	            ext = s.substring(i+1).toLowerCase();
	        
	        return ext;
	    }
	}
	
	public class JarZipClassFileFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
		    if (f.isDirectory())
		        return true;

		    String extension = getExtension(f);
		    if (extension != null)
		        return (extension.equals("jar") || extension.equals("zip") || extension.equals("class"));
		    
		    return false;
		}

		@Override
		public String getDescription() {
			return "Class Files or Zip/Jar Archives";
		}
	    
	    public String getExtension(File f) {
	        String ext = null;
	        String s = f.getName();
	        int i = s.lastIndexOf('.');

	        if (i > 0 &&  i < s.length() - 1)
	            ext = s.substring(i+1).toLowerCase();
	        
	        return ext;
	    }
	}
	
	public class ZipFileFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
		    if (f.isDirectory())
		        return true;

		    String extension = getExtension(f);
		    if (extension != null)
		        return (extension.equals("zip"));
		    
		    return false;
		}

		@Override
		public String getDescription() {
			return "Zip Archives";
		}
	    
	    public String getExtension(File f) {
	        String ext = null;
	        String s = f.getName();
	        int i = s.lastIndexOf('.');

	        if (i > 0 &&  i < s.length() - 1)
	            ext = s.substring(i+1).toLowerCase();
	        
	        return ext;
	    }
	}
	
	public class JarFileFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
		    if (f.isDirectory())
		        return true;

		    String extension = getExtension(f);
		    if (extension != null)
		        return (extension.equals("jar"));
		    
		    return false;
		}

		@Override
		public String getDescription() {
			return "Jar Archives";
		}
	    
	    public String getExtension(File f) {
	        String ext = null;
	        String s = f.getName();
	        int i = s.lastIndexOf('.');

	        if (i > 0 &&  i < s.length() - 1)
	            ext = s.substring(i+1).toLowerCase();
	        
	        return ext;
	    }
	}
}