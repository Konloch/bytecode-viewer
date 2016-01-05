package the.bytecode.club.bytecodeviewer.gui;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.*;
import the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.decompilers.FernFlowerDecompiler;
import the.bytecode.club.bytecodeviewer.decompilers.ProcyonDecompiler;
import the.bytecode.club.bytecodeviewer.decompilers.bytecode.ClassNodeDecompiler;
import the.bytecode.club.bytecodeviewer.obfuscators.rename.RenameClasses;
import the.bytecode.club.bytecodeviewer.obfuscators.rename.RenameFields;
import the.bytecode.club.bytecodeviewer.obfuscators.rename.RenameMethods;
import the.bytecode.club.bytecodeviewer.plugin.PluginManager;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 * *
 * This program is free software: you can redistribute it and/or modify    *
 * it under the terms of the GNU General Public License as published by  *
 * the Free Software Foundation, either version 3 of the License, or     *
 * (at your option) any later version.                                   *
 * *
 * This program is distributed in the hope that it will be useful,       *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 * GNU General Public License for more details.                          *
 * *
 * You should have received a copy of the GNU General Public License     *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

/**
 * The main file for the GUI.n
 *
 * @author Konloch
 */
public class MainViewerGUI extends JFrame implements FileChangeNotifier {

    public void pythonC() {
        new FileChooser(Settings.PYTHON2_LOCATION, "Python (Or PyPy for speed) 2.7 Executable").run();
    }

    public void javac() {
        new FileChooser(Settings.JAVAC_LOCATION, "javac executable (Requires JDK 'C:/Program Files/Java/jdk_xx/bin/javac.exe')").run();
    }

    public void java() {
        new FileChooser(Settings.JAVA_LOCATION, "Java Executable (Requires JRE/JDK 'C:/Program Files/Java/jre_xx/bin/java.exe')").run();
    }

    public void pythonC3() {
        new FileChooser(Settings.PYTHON3_LOCATION, "Python (Or PyPy for speed) 3.x Executable").run();
    }

    public void rtC() {
        new FileChooser(Settings.RT_LOCATION, "Java rt.jar").run();
    }

    public void library() {
        final JTextField text = new JTextField();
        text.setText(Settings.PATH.get());
        final JDialog dialog = new JDialog();
        dialog.setModal(true);
        dialog.add(text);
        dialog.setSize(500, 100);
        dialog.setLocationRelativeTo(BytecodeViewer.viewer);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Settings.PATH.set(text.getText());
            }
        });
        dialog.setVisible(true);
    }

    public static final long serialVersionUID = 1851409230530948543L;

    private final ActionListener listener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (refreshOnChange.isSelected()) {
                if (workPane.getCurrentViewer() == null) return;
                workPane.refreshClass.doClick();
            }
        }
    };

    private JMenu generateDecompilerMenu(Decompiler decompiler, int panelId) {
        ButtonGroup group = allPanes.get(panelId);
        JMenu menu = new JMenu(decompiler.getName());
        JRadioButtonMenuItem java = new JRadioButtonMenuItem(decompiler == Decompiler.SMALI ? "Smali" : "Java");
        java.addActionListener(listener);
        JRadioButtonMenuItem bytecode = new JRadioButtonMenuItem("Bytecode");
        JCheckBoxMenuItem editable = new JCheckBoxMenuItem("Editable");
        JSeparator separator = new JSeparator();
        menu.add(java);
        group.add(java);
        allDecompilers.get(group).put(java, decompiler);
        allDecompilersRev.get(group).put(decompiler, java);
        if (decompiler == Decompiler.KRAKATAU) {
            menu.add(bytecode);
            group.add(bytecode);
            bytecode.addActionListener(listener);
            allDecompilers.get(group).put(bytecode, Decompiler.KRAKATAU_DA);
        }
        menu.add(separator);
        menu.add(editable);
        editButtons.get(group).put(decompiler, editable);
        return menu;
    }

    private JMenu generatePane(int id) {
        JMenu menu = new JMenu("Pane " + (id + 1));
        JRadioButtonMenuItem none = new JRadioButtonMenuItem("None");
        JRadioButtonMenuItem bytecode = new JRadioButtonMenuItem("Bytecode");
        JRadioButtonMenuItem hexcode = new JRadioButtonMenuItem("Hexcode");
        ButtonGroup group = allPanes.get(id);

        group.add(none);
        group.add(bytecode);
        group.add(hexcode);
        allDecompilers.get(group).put(none, null);
        allDecompilersRev.get(group).put(null, none);
        allDecompilers.get(group).put(bytecode, Decompiler.BYTECODE);
        allDecompilersRev.get(group).put(Decompiler.BYTECODE, bytecode);
        allDecompilers.get(group).put(hexcode, Decompiler.HEXCODE);
        allDecompilersRev.get(group).put(Decompiler.HEXCODE, hexcode);

        menu.add(none);
        menu.add(new JSeparator());
        menu.add(generateDecompilerMenu(Decompiler.PROCYON, id));
        menu.add(generateDecompilerMenu(Decompiler.CFR, id));
        menu.add(generateDecompilerMenu(Decompiler.JDGUI, id));
        menu.add(generateDecompilerMenu(Decompiler.FERNFLOWER, id));
        menu.add(generateDecompilerMenu(Decompiler.KRAKATAU, id));
        menu.add(new JSeparator());
        menu.add(generateDecompilerMenu(Decompiler.SMALI, id));
        menu.add(new JSeparator());
        menu.add(bytecode);
        menu.add(hexcode);
        return menu;
    }

    public class Test implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            BytecodeViewer.checkHotKey(e);
            return false;
        }
    }

    public FileNavigationPane cn = new FileNavigationPane(this);
    public boolean isMaximized = false;
    public JSplitPane sp1;
    public JSplitPane sp2;
    static ArrayList<VisibleComponent> rfComps = new ArrayList<VisibleComponent>();
    public final JMenuItem mntmNewWorkspace = new JMenuItem("New Workspace");
    public JMenu mnRecentFiles = new JMenu("Recent Files");
    public final JMenuItem mntmNewMenuItem = new JMenuItem("Decompile & Save All Classes..");
    public final JMenuItem mntmAbout = new JMenuItem("About");
    public final JMenuItem mntmStartExternalPlugin = new JMenuItem("Open Plugin..");
    public JMenu mnRecentPlugins = new JMenu("Recent Plugins");
    public final JMenuItem mntmStartZkmString = new JMenuItem("ZKM String Decrypter");
    public final JMenuItem mntmNewMenuItem_1 = new JMenuItem("Malicious Code Scanner");
    public final JMenuItem mntmNewMenuItem_2 = new JMenuItem("Allatori String Decrypter");
    public final JMenuItem mntmShowAllStrings = new JMenuItem("Show All Strings");
    public final JMenuItem mntmShowMainMethods = new JMenuItem("Show Main Methods");
    public final JMenuItem mntmNewMenuItem_3 = new JMenuItem("Save As Runnable Jar..");
    public final JMenuItem mntmReplaceStrings = new JMenuItem("Replace Strings");
    public final JCheckBoxMenuItem chckbxmntmNewCheckItem_12 = new JCheckBoxMenuItem("Update Check");
    public final JMenuItem mntmNewMenuItem_6 = new JMenuItem("Rename Fields");
    public final JMenuItem mntmNewMenuItem_7 = new JMenuItem("Rename Methods");
    public final JMenuItem mntmNewMenuItem_8 = new JMenuItem("Move All Classes Into Root Package");
    public final JMenuItem mntmNewMenuItem_9 = new JMenuItem("Control Flow");
    public final JMenuItem mntmNewMenuItem_10 = new JMenuItem("Junk Code");
    public final ButtonGroup obfuscatorGroup = new ButtonGroup();
    public final JRadioButtonMenuItem strongObf = new JRadioButtonMenuItem("Strong Obfuscation");
    public final JRadioButtonMenuItem lightObf = new JRadioButtonMenuItem("Light Obfuscation");
    public final JMenuItem mntmNewMenuItem_11 = new JMenuItem("Rename Classes");
    public final JMenuItem mntmNewMenuItem_12 = new JMenuItem("Decompile & Save Opened Class..");
    public WorkPane workPane = new WorkPane(this);
    public final JCheckBoxMenuItem refreshOnChange = new JCheckBoxMenuItem("Refresh On View Change");
    public AboutWindow aboutWindow = new AboutWindow();
    public final JMenuItem mntmSaveAsApk = new JMenuItem("Save As DEX..");
    public final JMenuItem mntmCodeSequenceDiagram = new JMenuItem("Code Sequence Diagram");
    public final JCheckBoxMenuItem autoCompileSmali = new JCheckBoxMenuItem("Compile On Save");
    public final JMenuItem mntmNewMenuItem_13 = new JMenuItem("Compile");
    public final JCheckBoxMenuItem autoCompileOnRefresh = new JCheckBoxMenuItem("Compile On Refresh");
    public final JMenuItem mntmSetPythonDirectory = new JMenuItem("Set Python 2.7 Executable");
    public final JMenuItem mntmSetJreRt = new JMenuItem("Set JRE RT Library");
    public final JMenuItem mntmZstringarrayDecrypter = new JMenuItem("ZStringArray Decrypter");
    public final JMenuItem mntmRun = new JMenuItem("Run");
    public final JCheckBoxMenuItem decodeAPKResources = new JCheckBoxMenuItem("Decode APK Resources");
    public final ButtonGroup panelGroup1 = new ButtonGroup();
    public final ButtonGroup panelGroup2 = new ButtonGroup();
    public final ButtonGroup panelGroup3 = new ButtonGroup();
    public final JCheckBox mnShowContainer = new JCheckBox("Show Containing File's Name");
    private final JMenuItem mntmSetOpitonalLibrary = new JMenuItem("Set Optional Library Folder");
    private final JMenuItem mntmPingback = new JMenuItem("Pingback");
    private final JMenu mnFontSize = new JMenu("Font Size");
    private final JCheckBoxMenuItem chckbxmntmDeleteForiegnoutdatedLibs = new JCheckBoxMenuItem("Delete Foreign/Outdated Libs");
    private final JMenu mnApkConversion = new JMenu("APK Conversion");
    public final ButtonGroup apkConversionGroup = new ButtonGroup();
    public final JRadioButtonMenuItem apkConversionDex = new JRadioButtonMenuItem("Dex2Jar");
    public final JRadioButtonMenuItem apkConversionEnjarify = new JRadioButtonMenuItem("Enjarify");
    private final JMenuItem mntmSetPythonx = new JMenuItem("Set Python 3.X Executable");
    private final JMenuItem mntmReloadResources = new JMenuItem("Reload Resources");
    private final JMenuItem mntmSetJavacExecutable = new JMenuItem("Set Javac Executable");
    public List<ButtonGroup> allPanes = Collections.unmodifiableList(Arrays.asList(panelGroup1, panelGroup2, panelGroup3));
    public Map<ButtonGroup, Map<JRadioButtonMenuItem, Decompiler>> allDecompilers = new HashMap<>();
    public Map<ButtonGroup, Map<Decompiler, JRadioButtonMenuItem>> allDecompilersRev = new HashMap<>();
    public Map<ButtonGroup, Map<Decompiler, JCheckBoxMenuItem>> editButtons = new HashMap<>();

    public MainViewerGUI() {
        Decompiler.ensureInitted();
        allDecompilers.put(panelGroup1, new HashMap<JRadioButtonMenuItem, Decompiler>());
        allDecompilers.put(panelGroup2, new HashMap<JRadioButtonMenuItem, Decompiler>());
        allDecompilers.put(panelGroup3, new HashMap<JRadioButtonMenuItem, Decompiler>());
        allDecompilersRev.put(panelGroup1, new HashMap<Decompiler, JRadioButtonMenuItem>());
        allDecompilersRev.put(panelGroup2, new HashMap<Decompiler, JRadioButtonMenuItem>());
        allDecompilersRev.put(panelGroup3, new HashMap<Decompiler, JRadioButtonMenuItem>());
        editButtons.put(panelGroup1, new HashMap<Decompiler, JCheckBoxMenuItem>());
        editButtons.put(panelGroup2, new HashMap<Decompiler, JCheckBoxMenuItem>());
        editButtons.put(panelGroup3, new HashMap<Decompiler, JCheckBoxMenuItem>());
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new Test());
        this.addWindowStateListener(new WindowAdapter() {
            @Override
            public void windowStateChanged(WindowEvent evt) {
                int oldState = evt.getOldState();
                int newState = evt.getNewState();

                if ((oldState & Frame.ICONIFIED) == 0 && (newState & Frame.ICONIFIED) != 0) {
                    //System.out.println("Frame was iconized");
                } else if ((oldState & Frame.ICONIFIED) != 0 && (newState & Frame.ICONIFIED) == 0) {
                    //System.out.println("Frame was deiconized");
                }

                if ((oldState & Frame.MAXIMIZED_BOTH) == 0 && (newState & Frame.MAXIMIZED_BOTH) != 0) {
                    isMaximized = true;
                } else if ((oldState & Frame.MAXIMIZED_BOTH) != 0 && (newState & Frame.MAXIMIZED_BOTH) == 0) {
                    isMaximized = false;
                }
            }
        });
        this.setIconImages(Resources.iconList);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu viewMenu = new JMenu("View");
        JMenu settingsMenu = new JMenu("Settings");
        JMenu obfuscateMenu = new JMenu("Obfuscate");
        JMenu pluginsMenu = new JMenu("Plugins");
        obfuscateMenu.setVisible(false);
        setJMenuBar(menuBar);

        obfuscatorGroup.add(strongObf);
        obfuscatorGroup.add(lightObf);
        obfuscatorGroup.setSelected(strongObf.getModel(), true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        menuBar.add(fileMenu);

        mntmNewWorkspace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                BytecodeViewer.resetWorkSpace(true);
            }
        });

        JMenuItem mntmLoadJar = new JMenuItem("Add..");
        mntmLoadJar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                try {
                    File f = new File(BytecodeViewer.lastDirectory);
                    if (f.exists()) fc.setSelectedFile(f);
                } catch (Exception e2) {

                }
                fc.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if (f.isDirectory()) return true;

                        String extension = MiscUtils.extension(f.getAbsolutePath());
                        if (extension != null)
                            if (extension.equals("jar") || extension.equals("zip") || extension.equals("class") || extension.equals("apk") || extension.equals("dex"))
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
            }
        });
        fileMenu.add(mntmLoadJar);

        fileMenu.add(new JSeparator());

        fileMenu.add(mntmNewWorkspace);

        JMenuItem mntmSave = new JMenuItem("Save As Zip..");
        mntmSave.setActionCommand("");
        mntmSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (BytecodeViewer.getLoadedBytes().isEmpty()) {
                    BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                    return;
                }
                Thread t = new Thread() {
                    public void run() {
                        if (autoCompileSmali.isSelected() && !BytecodeViewer.compile(false)) return;
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
                        int returnVal = fc.showSaveDialog(MainViewerGUI.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            if (!file.getAbsolutePath().endsWith(".zip"))
                                file = new File(file.getAbsolutePath() + ".zip");

                            if (file.exists()) {
                                JOptionPane pane = new JOptionPane("Are you sure you wish to overwrite this existing file?");
                                Object[] options = new String[]{"Yes", "No"};
                                pane.setOptions(options);
                                JDialog dialog = pane.createDialog(BytecodeViewer.viewer, "Bytecode Viewer - Overwrite File");
                                dialog.setVisible(true);
                                Object obj = pane.getValue();
                                int result = -1;
                                for (int k = 0; k < options.length; k++)
                                    if (options[k].equals(obj)) result = k;

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
                                    JarUtils.saveAsJar(BytecodeViewer.getLoadedBytes(), file2.getAbsolutePath());
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

        fileMenu.add(new JSeparator());
        mntmReloadResources.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                JOptionPane pane = new JOptionPane("Are you sure you wish to reload the resources?");
                Object[] options = new String[]{"Yes", "No"};
                pane.setOptions(options);
                JDialog dialog = pane.createDialog(BytecodeViewer.viewer, "Bytecode Viewer - Reload Resources");
                dialog.setVisible(true);
                Object obj = pane.getValue();
                int result = -1;
                for (int k = 0; k < options.length; k++)
                    if (options[k].equals(obj)) result = k;

                if (result == 0) {
                    ArrayList<File> reopen = new ArrayList<File>();
                    for (FileContainer container : BytecodeViewer.files)
                        reopen.add(container.file);

                    BytecodeViewer.files.clear();
                    BytecodeViewer.openFiles(reopen.toArray(new File[reopen.size()]), false);

                    //refresh panes
                }
            }
        });

        fileMenu.add(mntmReloadResources);

        fileMenu.add(new JSeparator());
        mntmNewMenuItem_3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (BytecodeViewer.getLoadedBytes().isEmpty()) {
                    BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                    return;
                }
                Thread t = new Thread() {
                    public void run() {
                        if (autoCompileSmali.isSelected() && !BytecodeViewer.compile(false)) return;
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
                        int returnVal = fc.showSaveDialog(MainViewerGUI.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            String path = file.getAbsolutePath();
                            if (!path.endsWith(".jar")) path = path + ".jar";

                            if (new File(path).exists()) {
                                JOptionPane pane = new JOptionPane("Are you sure you wish to overwrite this existing file?");
                                Object[] options = new String[]{"Yes", "No"};
                                pane.setOptions(options);
                                JDialog dialog = pane.createDialog(BytecodeViewer.viewer, "Bytecode Viewer - Overwrite File");
                                dialog.setVisible(true);
                                Object obj = pane.getValue();
                                int result = -1;
                                for (int k = 0; k < options.length; k++)
                                    if (options[k].equals(obj)) result = k;

                                if (result == 0) {
                                    file.delete();
                                } else {
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
        mntmNewMenuItem_13.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Thread t = new Thread() {
                    public void run() {
                        BytecodeViewer.compile(true);
                    }
                };
                t.start();
            }
        });
        mntmRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (BytecodeViewer.getLoadedBytes().isEmpty()) {
                    BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                    return;
                }
                new RunOptions().setVisible(true);
            }
        });

        fileMenu.add(mntmRun);

        fileMenu.add(mntmNewMenuItem_13);

        fileMenu.add(new JSeparator());

        fileMenu.add(mntmNewMenuItem_3);
        mntmSaveAsApk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (BytecodeViewer.getLoadedBytes().isEmpty()) {
                    BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                    return;
                }

                Thread t = new Thread() {
                    public void run() {
                        if (autoCompileSmali.isSelected() && !BytecodeViewer.compile(false)) return;
                        JFileChooser fc = new JFileChooser();
                        fc.setFileFilter(new FileFilter() {
                            @Override
                            public boolean accept(File f) {
                                return f.isDirectory() || MiscUtils.extension(f.getAbsolutePath()).equals("dex");
                            }

                            @Override
                            public String getDescription() {
                                return "Android DEX Files";
                            }
                        });
                        fc.setFileHidingEnabled(false);
                        fc.setAcceptAllFileFilterUsed(false);
                        int returnVal = fc.showSaveDialog(MainViewerGUI.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            final File file = fc.getSelectedFile();
                            String output = file.getAbsolutePath();
                            if (!output.endsWith(".dex")) output = output + ".dex";

                            final File file2 = new File(output);

                            if (file2.exists()) {
                                JOptionPane pane = new JOptionPane("Are you sure you wish to overwrite this existing file?");
                                Object[] options = new String[]{"Yes", "No"};
                                pane.setOptions(options);
                                JDialog dialog = pane.createDialog(BytecodeViewer.viewer, "Bytecode Viewer - Overwrite File");
                                dialog.setVisible(true);
                                Object obj = pane.getValue();
                                int result = -1;
                                for (int k = 0; k < options.length; k++)
                                    if (options[k].equals(obj)) result = k;

                                if (result == 0) {
                                    file.delete();
                                } else {
                                    return;
                                }
                            }

                            Thread t = new Thread() {
                                @Override
                                public void run() {
                                    BytecodeViewer.viewer.setIcon(true);
                                    final String input = BytecodeViewer.tempDir.getAbsolutePath() + BytecodeViewer.fs + BytecodeViewer.getRandomizedName() + ".jar";
                                    JarUtils.saveAsJar(BytecodeViewer.getLoadedBytes(), input);

                                    Thread t = new Thread() {
                                        @Override
                                        public void run() {
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

        fileMenu.add(mntmSaveAsApk);
        fileMenu.add(mntmSave);
        mntmNewMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (BytecodeViewer.files.isEmpty()) {
                    BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                    return;
                }

                Thread t = new Thread() {
                    public void run() {
                        if (autoCompileSmali.isSelected() && !BytecodeViewer.compile(false)) return;
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
                        int returnVal = fc.showSaveDialog(MainViewerGUI.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            if (!file.getAbsolutePath().endsWith(".zip"))
                                file = new File(file.getAbsolutePath() + ".zip");

                            if (file.exists()) {
                                JOptionPane pane = new JOptionPane("Are you sure you wish to overwrite this existing file?");
                                Object[] options = new String[]{"Yes", "No"};
                                pane.setOptions(options);
                                JDialog dialog = pane.createDialog(BytecodeViewer.viewer, "Bytecode Viewer - Overwrite File");
                                dialog.setVisible(true);
                                Object obj = pane.getValue();
                                int result = -1;
                                for (int k = 0; k < options.length; k++)
                                    if (options[k].equals(obj)) result = k;

                                if (result == 0) {
                                    file.delete();
                                } else {
                                    return;
                                }
                            }

                            BytecodeViewer.viewer.setIcon(true);
                            final String path = MiscUtils.append(file, ".zip");    // cheap hax cause
                            // string is final

                            JOptionPane pane = new JOptionPane("What decompiler will you use?");
                            Object[] options = new String[]{"Procyon", "CFR", "Fernflower", "Krakatau", "JD-GUI", "Cancel"};
                            pane.setOptions(options);
                            JDialog dialog = pane.createDialog(BytecodeViewer.viewer, "Bytecode Viewer - Select Decompiler");
                            dialog.setVisible(true);
                            Object obj = pane.getValue();
                            int result = -1;
                            for (int k = 0; k < options.length; k++)
                                if (options[k].equals(obj)) result = k;

                            if (result == 0) {
                                Thread t = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            Decompiler.PROCYON.decompileToZip(path);
                                            BytecodeViewer.viewer.setIcon(false);
                                        } catch (Exception e) {
                                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                                        }
                                    }
                                };
                                t.start();
                            }
                            if (result == 1) {
                                Thread t = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            Decompiler.CFR.decompileToZip(path);
                                            BytecodeViewer.viewer.setIcon(false);
                                        } catch (Exception e) {
                                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                                        }
                                    }
                                };
                                t.start();
                            }
                            if (result == 2) {
                                Thread t = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            Decompiler.FERNFLOWER.decompileToZip(path);
                                            BytecodeViewer.viewer.setIcon(false);
                                        } catch (Exception e) {
                                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                                        }
                                    }
                                };
                                t.start();
                            }

                            if (result == 3) {
                                Thread t = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            Decompiler.KRAKATAU.decompileToZip(path);
                                            BytecodeViewer.viewer.setIcon(false);
                                        } catch (Exception e) {
                                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                                        }
                                    }
                                };
                                t.start();
                            } else if (result == 4) {
                                new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            Decompiler.JDGUI.decompileToZip(path);
                                            BytecodeViewer.viewer.setIcon(false);
                                        } catch (Exception e) {
                                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                                        }
                                    }
                                }.start();
                            } else {
                                BytecodeViewer.viewer.setIcon(false);
                            }
                        }
                    }
                };
                t.start();
            }
        });
        mntmNewMenuItem_12.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (workPane.getCurrentViewer() == null) {
                    BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                    return;
                }

                Thread t = new Thread() {
                    public void run() {
                        if (autoCompileSmali.isSelected() && !BytecodeViewer.compile(false)) return;
                        final String s = workPane.getCurrentViewer().name;

                        JFileChooser fc = new JFileChooser();
                        fc.setFileFilter(new FileFilter() {
                            @Override
                            public boolean accept(File f) {
                                return f.isDirectory() || MiscUtils.extension(f.getAbsolutePath()).equals("java");
                            }

                            @Override
                            public String getDescription() {
                                return "Java Source Files";
                            }
                        });
                        fc.setFileHidingEnabled(false);
                        fc.setAcceptAllFileFilterUsed(false);
                        int returnVal = fc.showSaveDialog(MainViewerGUI.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();

                            BytecodeViewer.viewer.setIcon(true);
                            final String path = MiscUtils.append(file, ".java");    // cheap hax cause
                            // string is final

                            if (new File(path).exists()) {
                                JOptionPane pane = new JOptionPane("Are you sure you wish to overwrite this existing file?");
                                Object[] options = new String[]{"Yes", "No"};
                                pane.setOptions(options);
                                JDialog dialog = pane.createDialog(BytecodeViewer.viewer, "Bytecode Viewer - Overwrite File");
                                dialog.setVisible(true);
                                Object obj = pane.getValue();
                                int result = -1;
                                for (int k = 0; k < options.length; k++)
                                    if (options[k].equals(obj)) result = k;

                                if (result == 0) {
                                    file.delete();
                                } else {
                                    return;
                                }
                            }

                            JOptionPane pane = new JOptionPane("What decompiler will you use?");
                            Object[] options = new String[]{"Procyon", "CFR", "Fernflower", "Krakatau", "Cancel"};
                            pane.setOptions(options);
                            JDialog dialog = pane.createDialog(BytecodeViewer.viewer, "Bytecode Viewer - Select Decompiler");
                            dialog.setVisible(true);
                            Object obj = pane.getValue();
                            int result = -1;
                            for (int k = 0; k < options.length; k++)
                                if (options[k].equals(obj)) result = k;
                            final String containerName = BytecodeViewer.files.get(0).name;

                            if (result == 0) {
                                Thread t = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            ClassNode cn = BytecodeViewer.getClassNode(containerName, s);
                                            byte[] bytes = BytecodeViewer.getClassBytes(containerName, s);
                                            String contents = Decompiler.PROCYON.decompileClassNode(cn, bytes);
                                            FileUtils.write(new File(path), contents, "UTF-8", false);
                                            BytecodeViewer.viewer.setIcon(false);
                                        } catch (Exception e) {
                                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                                        }
                                    }
                                };
                                t.start();
                            }
                            if (result == 1) {
                                Thread t = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            ClassNode cn = BytecodeViewer.getClassNode(containerName, s);
                                            byte[] bytes = BytecodeViewer.getClassBytes(containerName, s);
                                            String contents = Decompiler.CFR.decompileClassNode(cn, bytes);
                                            FileUtils.write(new File(path), contents, "UTF-8", false);
                                            BytecodeViewer.viewer.setIcon(false);
                                        } catch (Exception e) {
                                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                                        }
                                    }
                                };
                                t.start();
                            }
                            if (result == 2) {
                                Thread t = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            ClassNode cn = BytecodeViewer.getClassNode(containerName, s);
                                            byte[] bytes = BytecodeViewer.getClassBytes(containerName, s);
                                            String contents = Decompiler.FERNFLOWER.decompileClassNode(cn, bytes);
                                            FileUtils.write(new File(path), contents, "UTF-8", false);
                                            BytecodeViewer.viewer.setIcon(false);
                                        } catch (Exception e) {
                                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                                        }
                                    }
                                };
                                t.start();
                            }
                            if (result == 3) {
                                Thread t = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            ClassNode cn = BytecodeViewer.getClassNode(containerName, s);
                                            byte[] bytes = BytecodeViewer.getClassBytes(containerName, s);
                                            String contents = Decompiler.KRAKATAU.decompileClassNode(cn, bytes);
                                            FileUtils.write(new File(path), contents, "UTF-8", false);
                                            BytecodeViewer.viewer.setIcon(false);
                                        } catch (Exception e) {
                                            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                                        }
                                    }
                                };
                                t.start();
                            }
                            if (result == 4) {
                                BytecodeViewer.viewer.setIcon(false);
                            }
                        }
                    }
                };
                t.start();
            }
        });

        fileMenu.add(mntmNewMenuItem_12);
        fileMenu.add(mntmNewMenuItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(mnRecentFiles);
        fileMenu.add(new JSeparator());
        mntmAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                aboutWindow.setVisible(true);
            }
        });

        fileMenu.add(mntmAbout);

        JMenuItem mntmExit = new JMenuItem("Exit");
        mntmExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                JOptionPane pane = new JOptionPane("Are you sure you want to exit?");
                Object[] options = new String[]{"Yes", "No"};
                pane.setOptions(options);
                JDialog dialog = pane.createDialog(BytecodeViewer.viewer, "Bytecode Viewer - Exit");
                dialog.setVisible(true);
                Object obj = pane.getValue();
                int result = -1;
                for (int k = 0; k < options.length; k++)
                    if (options[k].equals(obj)) result = k;

                if (result == 0) {
                    System.exit(0);
                }
            }
        });
        mntmPingback.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                BytecodeViewer.pingback();
            }
        });

        fileMenu.add(mntmPingback);
        fileMenu.add(mntmExit);

        menuBar.add(viewMenu);
        viewMenu.add(generatePane(0));
        viewMenu.add(generatePane(1));
        viewMenu.add(generatePane(2));


        autoCompileSmali.setSelected(true);

        settingsMenu.add(autoCompileSmali);
        autoCompileOnRefresh.setSelected(true);

        settingsMenu.add(autoCompileOnRefresh);

        settingsMenu.add(refreshOnChange);

        settingsMenu.add(new JSeparator());
        decodeAPKResources.setSelected(true);

        settingsMenu.add(decodeAPKResources);

        settingsMenu.add(mnApkConversion);

        mnApkConversion.add(apkConversionDex);

        mnApkConversion.add(apkConversionEnjarify);

        settingsMenu.add(new JSeparator());
        chckbxmntmNewCheckItem_12.setSelected(true);
        settingsMenu.add(chckbxmntmNewCheckItem_12);
        chckbxmntmDeleteForiegnoutdatedLibs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (!chckbxmntmDeleteForiegnoutdatedLibs.isSelected()) {
                    BytecodeViewer.showMessage("WARNING: With this being toggled off outdated libraries will NOT be removed. It's also a security issue. ONLY TURN IT OFF IF YOU KNOW WHAT YOU'RE DOING.");
                }
                BytecodeViewer.deleteForiegnLibraries = chckbxmntmDeleteForiegnoutdatedLibs.isSelected();
            }
        });
        chckbxmntmDeleteForiegnoutdatedLibs.setSelected(true);

        settingsMenu.add(chckbxmntmDeleteForiegnoutdatedLibs);

        settingsMenu.add(new JSeparator());
        mntmSetPythonDirectory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                pythonC();
            }
        });

        settingsMenu.add(mntmSetPythonDirectory);
        mntmSetJreRt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                rtC();
            }
        });
        mntmSetPythonx.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                pythonC3();
            }
        });

        settingsMenu.add(mntmSetPythonx);

        settingsMenu.add(mntmSetJreRt);
        mntmSetOpitonalLibrary.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                library();
            }
        });

        settingsMenu.add(mntmSetOpitonalLibrary);
        mntmSetJavacExecutable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                javac();
            }
        });

        settingsMenu.add(mntmSetJavacExecutable);

        settingsMenu.add(new JSeparator());

        JMenu cfrSettingsMenu = new JMenu("CFR");
        DecompilerSettings cfrSettings = Decompiler.CFR.getSettings();
        for (CFRDecompiler.Settings setting : CFRDecompiler.Settings.values()) {
            cfrSettingsMenu.add(cfrSettings.getMenuItem(setting));
        }
        settingsMenu.add(cfrSettingsMenu);

        JMenu fernflowerSettingMenu = new JMenu("FernFlower");
        DecompilerSettings fernflowerSettings = Decompiler.FERNFLOWER.getSettings();
        for (FernFlowerDecompiler.Settings setting : FernFlowerDecompiler.Settings.values()) {
            fernflowerSettingMenu.add(fernflowerSettings.getMenuItem(setting));
        }
        settingsMenu.add(fernflowerSettingMenu);

        JMenu procyonSettingsMenu = new JMenu("Procyon");
        DecompilerSettings procyonSettings = Decompiler.PROCYON.getSettings();
        for (ProcyonDecompiler.Settings setting : ProcyonDecompiler.Settings.values()) {
            procyonSettingsMenu.add(procyonSettings.getMenuItem(setting));
        }
        settingsMenu.add(procyonSettingsMenu);

        JMenu bytecodeSettingsMenu = new JMenu("Bytecode Decompiler");
        DecompilerSettings bytecodeSettings = Decompiler.BYTECODE.getSettings();
        for (ClassNodeDecompiler.Settings setting : ClassNodeDecompiler.Settings.values()) {
            bytecodeSettingsMenu.add(bytecodeSettings.getMenuItem(setting));
        }
        settingsMenu.add(bytecodeSettingsMenu);

        menuBar.add(settingsMenu);
        menuBar.add(obfuscateMenu);
        mntmNewMenuItem_6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (BytecodeViewer.runningObfuscation) {
                    BytecodeViewer.showMessage("You're currently running an obfuscation task, wait for this to finish.");
                    return;
                }
                new RenameFields().start();
                workPane.refreshClass.doClick();
                cn.tree.updateUI();
            }
        });

        obfuscateMenu.add(strongObf);

        obfuscateMenu.add(lightObf);

        obfuscateMenu.add(new JSeparator());
        mntmNewMenuItem_8.setEnabled(false);

        obfuscateMenu.add(mntmNewMenuItem_8);

        obfuscateMenu.add(mntmNewMenuItem_6);
        mntmNewMenuItem_7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (BytecodeViewer.runningObfuscation) {
                    BytecodeViewer.showMessage("You're currently running an obfuscation task, wait for this to finish.");
                    return;
                }
                new RenameMethods().start();
                workPane.refreshClass.doClick();
                cn.tree.updateUI();
            }
        });

        obfuscateMenu.add(mntmNewMenuItem_7);
        mntmNewMenuItem_11.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (BytecodeViewer.runningObfuscation) {
                    BytecodeViewer.showMessage("You're currently running an obfuscation task, wait for this to finish.");
                    return;
                }
                new RenameClasses().start();
                workPane.refreshClass.doClick();
                cn.tree.updateUI();
            }
        });

        obfuscateMenu.add(mntmNewMenuItem_11);
        mntmNewMenuItem_9.setEnabled(false);

        obfuscateMenu.add(mntmNewMenuItem_9);
        mntmNewMenuItem_10.setEnabled(false);

        obfuscateMenu.add(mntmNewMenuItem_10);

        menuBar.add(pluginsMenu);
        pluginsMenu.add(mntmStartExternalPlugin);
        pluginsMenu.add(new JSeparator());
        pluginsMenu.add(mnRecentPlugins);
        pluginsMenu.add(new JSeparator());
        mntmCodeSequenceDiagram.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (BytecodeViewer.getLoadedClasses().isEmpty()) {
                    BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                    return;
                }
                PluginManager.runPlugin(new CodeSequenceDiagram());
            }
        });

        pluginsMenu.add(mntmCodeSequenceDiagram);
        pluginsMenu.add(mntmNewMenuItem_1);
        pluginsMenu.add(mntmShowMainMethods);
        pluginsMenu.add(mntmShowAllStrings);
        mntmReplaceStrings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (BytecodeViewer.getLoadedClasses().isEmpty()) {
                    BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                    return;
                }
                new ReplaceStringsOptions().setVisible(true);
            }
        });

        pluginsMenu.add(mntmReplaceStrings);
        pluginsMenu.add(mntmNewMenuItem_2);
        pluginsMenu.add(mntmStartZkmString);
        mntmZstringarrayDecrypter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                PluginManager.runPlugin(new ZStringArrayDecrypter());
            }
        });

        pluginsMenu.add(mntmZstringarrayDecrypter);

        menuBar.add(spinnerMenu);

        mntmStartExternalPlugin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser fc = new JFileChooser();
                fc.setFileFilter(PluginManager.fileFilter());
                fc.setFileHidingEnabled(false);
                fc.setAcceptAllFileFilterUsed(false);
                int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);

                if (returnVal == JFileChooser.APPROVE_OPTION) try {
                    BytecodeViewer.viewer.setIcon(true);
                    BytecodeViewer.startPlugin(fc.getSelectedFile());
                    BytecodeViewer.viewer.setIcon(false);
                } catch (Exception e1) {
                    new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
                }
            }
        });
        mntmStartZkmString.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PluginManager.runPlugin(new ZKMStringDecrypter());
            }
        });
        mntmNewMenuItem_2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (BytecodeViewer.getLoadedClasses().isEmpty()) {
                    BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                    return;
                }
                new AllatoriStringDecrypterOptions().setVisible(true);
            }
        });
        mntmNewMenuItem_1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (BytecodeViewer.getLoadedClasses().isEmpty()) {
                    BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                    return;
                }
                new MaliciousCodeScannerOptions().setVisible(true);
            }
        });
        mntmShowAllStrings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PluginManager.runPlugin(new ShowAllStrings());
            }
        });

        mntmShowMainMethods.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PluginManager.runPlugin(new ShowMainMethods());
            }
        });

        setSize(new Dimension(800, 400));
        if (BytecodeViewer.previewCopy)
            setTitle("Bytecode Viewer " + BytecodeViewer.version + " Preview - https://bytecodeviewer.com | https://the.bytecode.club - @Konloch");
        else
            setTitle("Bytecode Viewer " + BytecodeViewer.version + " - https://bytecodeviewer.com | https://the.bytecode.club - @Konloch");

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

        // scrollPane.setViewportView(tree);
        cn.setMinimumSize(new Dimension(200, 50));
        // panel.add(cn);
        SearchingPane s = new SearchingPane(this);
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
        viewMenu.add(mnFontSize);
        mnFontSize.add(fontSpinner);

        viewMenu.add(mnShowContainer);
        mnShowContainer.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                JTabbedPane tabs = workPane.tabs;
                Component[] components = tabs.getComponents();
                for (int i = 0; i < components.length; i++) {
                    Component c = components[i];
                    if (c instanceof Viewer) {
                        ((Viewer) c).updateName();
                        int idx = tabs.indexOfComponent(c);
                        tabs.setTabComponentAt(idx, new TabbedPane(c.getName(), tabs));
                        workPane.tabs.setTitleAt(idx, c.getName());
                    }
                }
            }
        });
            panelGroup1.setSelected(allDecompilersRev.get(panelGroup1).get(Decompiler.JDGUI).getModel(), true);
        panelGroup2.setSelected(allDecompilersRev.get(panelGroup2).get(Decompiler.BYTECODE).getModel(), true);
        panelGroup3.setSelected(allDecompilersRev.get(panelGroup3).get(null).getModel(), true);
        this.setLocationRelativeTo(null);
    }

    public JSpinner fontSpinner = new JSpinner();
    private JMenuItem spinnerMenu = new JMenuItem("");

    public void setIcon(final boolean busy) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (busy) {
                    try {
                        spinnerMenu.setIcon(Resources.busyIcon);
                    } catch (NullPointerException e) {
                        spinnerMenu.setIcon(Resources.busyB64Icon);
                    }
                } else spinnerMenu.setIcon(null);
                spinnerMenu.updateUI();
            }
        });
    }

    public void calledAfterLoad() {
        chckbxmntmDeleteForiegnoutdatedLibs.setSelected(BytecodeViewer.deleteForiegnLibraries);
    }

    @Override
    public void openClassFile(final String name, String container, final ClassNode cn) {
        for (final VisibleComponent vc : rfComps) {
            vc.openClassFile(name, container, cn);
        }
    }

    @Override
    public void openFile(final String name, String container, byte[] content) {
        for (final VisibleComponent vc : rfComps) {
            vc.openFile(name, container, content);
        }
    }

    public static <T> T getComponent(final Class<T> clazz) {
        for (final VisibleComponent vc : rfComps) {
            if (vc.getClass() == clazz) return clazz.cast(vc);
        }
        return null;
    }
}
