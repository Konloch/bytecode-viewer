package the.bytecode.club.bytecodeviewer.resources;

import me.konloch.kontainer.io.DiskWriter;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.gui.components.FileChooser;
import the.bytecode.club.bytecodeviewer.gui.components.MultipleChoiceDialogue;
import the.bytecode.club.bytecodeviewer.util.JarUtils;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Objects;

import static the.bytecode.club.bytecodeviewer.Constants.fs;
import static the.bytecode.club.bytecodeviewer.Constants.tempDirectory;

/**
 * @author Konloch
 * @since 6/21/2021
 */
public class ResourceDecompiling
{
	public static void decompileSaveAll()
	{
		if (BytecodeViewer.getLoadedClasses().isEmpty())
		{
			BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
			return;
		}
		
		Thread t = new Thread(() ->
		{
			if (BytecodeViewer.viewer.compileOnSave.isSelected() && !BytecodeViewer.compile(false))
				return;
			
			JFileChooser fc = new FileChooser(new File(Configuration.lastDirectory),
					"Select Zip Export",
					"Zip Archives",
					"zip");
			
			int returnVal = fc.showSaveDialog(BytecodeViewer.viewer);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				Configuration.lastDirectory = fc.getSelectedFile().getAbsolutePath();
				File file = fc.getSelectedFile();
				
				//auto appened zip
				if (!file.getAbsolutePath().endsWith(".zip"))
					file = new File(file.getAbsolutePath() + ".zip");
				
				if (file.exists())
				{
					MultipleChoiceDialogue dialogue = new MultipleChoiceDialogue("Bytecode Viewer - Overwrite File",
							"Are you sure you wish to overwrite this existing file?",
							new String[]{"Yes", "No"});
					
					if (dialogue.promptChoice() == 0) {
						file.delete();
					} else {
						return;
					}
				}
				
				final File javaSucks = file;
				final String path = MiscUtils.append(file, ".zip");    // cheap hax cause string is final
				
				JOptionPane pane = new JOptionPane("What decompiler will you use?");
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
				
				BytecodeViewer.viewer.updateBusyStatus(true);
				
				File tempZip = new File(tempDirectory + fs + "temp_" + MiscUtils.getRandomizedName() + ".jar");
				if (tempZip.exists())
					tempZip.delete();
				
				JarUtils.saveAsJarClassesOnly(BytecodeViewer.getLoadedClasses(), tempZip.getAbsolutePath());
				
				if (result == 0) {
					Thread t12 = new Thread(() -> {
						try {
							Decompiler.PROCYON.getDecompiler().decompileToZip(tempZip.getAbsolutePath(),
									MiscUtils.append(javaSucks, "-proycon.zip"));
							BytecodeViewer.viewer.updateBusyStatus(false);
						} catch (Exception e) {
							new ExceptionUI(e);
						}
					});
					t12.start();
					Thread t2 = new Thread(() -> {
						try {
							BytecodeViewer.viewer.updateBusyStatus(true);
							Decompiler.CFR.getDecompiler().decompileToZip(tempZip.getAbsolutePath(),
									MiscUtils.append(javaSucks, "-CFR.zip"));
							BytecodeViewer.viewer.updateBusyStatus(false);
						} catch (Exception e) {
							new ExceptionUI(e);
						}
					});
					t2.start();
					Thread t3 = new Thread(() -> {
						try {
							BytecodeViewer.viewer.updateBusyStatus(true);
							Decompiler.FERNFLOWER.getDecompiler().decompileToZip(tempZip.getAbsolutePath(),
									MiscUtils.append(javaSucks, "-fernflower.zip"));
							BytecodeViewer.viewer.updateBusyStatus(false);
						} catch (Exception e) {
							new ExceptionUI(e);
						}
					});
					t3.start();
					Thread t4 = new Thread(() -> {
						try {
							BytecodeViewer.viewer.updateBusyStatus(true);
							Decompiler.KRAKATAU.getDecompiler().decompileToZip(tempZip.getAbsolutePath(),
									MiscUtils.append(javaSucks, "-kraktau.zip"));
							BytecodeViewer.viewer.updateBusyStatus(false);
						} catch (Exception e) {
							new ExceptionUI(e);
						}
					});
					t4.start();
				}
				if (result == 1) {
					Thread t12 = new Thread(() -> {
						try {
							Decompiler.PROCYON.getDecompiler().decompileToZip(tempZip.getAbsolutePath(), path);
							BytecodeViewer.viewer.updateBusyStatus(false);
						} catch (Exception e) {
							new ExceptionUI(e);
						}
					});
					t12.start();
				}
				if (result == 2) {
					Thread t12 = new Thread(() -> {
						try {
							Decompiler.CFR.getDecompiler().decompileToZip(tempZip.getAbsolutePath(), path);
							BytecodeViewer.viewer.updateBusyStatus(false);
						} catch (Exception e) {
							new ExceptionUI(e);
						}
					});
					t12.start();
				}
				if (result == 3) {
					Thread t12 = new Thread(() -> {
						try {
							Decompiler.FERNFLOWER.getDecompiler().decompileToZip(tempZip.getAbsolutePath(), path);
							BytecodeViewer.viewer.updateBusyStatus(false);
						} catch (Exception e) {
							new ExceptionUI(e);
						}
					});
					t12.start();
				}
				
				if (result == 4) {
					Thread t12 = new Thread(() -> {
						try {
							Decompiler.KRAKATAU.getDecompiler().decompileToZip(tempZip.getAbsolutePath(), path);
							BytecodeViewer.viewer.updateBusyStatus(false);
						} catch (Exception e) {
							new ExceptionUI(e);
						}
					});
					t12.start();
				}
				
				if (result == 5) {
					BytecodeViewer.viewer.updateBusyStatus(false);
				}
			}
		});
		t.start();
	}
	
	public static void decompileSaveOpenedOnly()
	{
		if (BytecodeViewer.viewer.workPane.getCurrentViewer() == null) {
			BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
			return;
		}
		
		Thread t = new Thread(() -> {
			if (BytecodeViewer.viewer.compileOnSave.isSelected() && !BytecodeViewer.compile(false))
				return;
			
			final String s = BytecodeViewer.viewer.workPane.getCurrentViewer().cn.name;
			
			if (s == null)
				return;
			
			
			JFileChooser fc = new FileChooser(new File(Configuration.lastDirectory),
					"Select Java Files",
					"Java Source Files",
					"java");
			
			int returnVal = fc.showSaveDialog(BytecodeViewer.viewer);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				Configuration.lastDirectory = fc.getSelectedFile().getAbsolutePath();
				File file = fc.getSelectedFile();
				
				BytecodeViewer.viewer.updateBusyStatus(true);
				final String path = MiscUtils.append(file, ".java");    // cheap hax because string is final
				
				if (new File(path).exists())
				{
					MultipleChoiceDialogue dialogue = new MultipleChoiceDialogue("Bytecode Viewer - Overwrite File",
							"Are you sure you wish to overwrite this existing file?",
							new String[]{"Yes", "No"});
					
					if (dialogue.promptChoice() == 0) {
						file.delete();
					} else {
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
				
				if (result == 0) {
					Thread t1 = new Thread(() -> {
						try {
							ClassNode cn = BytecodeViewer.getClassNode(s);
							final ClassWriter cw = new ClassWriter(0);
							try {
								Objects.requireNonNull(cn).accept(cw);
							} catch (Exception e) {
								e.printStackTrace();
								try {
									Thread.sleep(200);
									Objects.requireNonNull(cn).accept(cw);
								} catch (InterruptedException ignored) {
								}
							}
							
							try {
								DiskWriter.replaceFile(MiscUtils.append(file, "-proycon.java"),
										Decompiler.PROCYON.getDecompiler().decompileClassNode(cn, cw.toByteArray()), false);
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							try {
								DiskWriter.replaceFile(MiscUtils.append(file, "-CFR.java"),
										Decompiler.CFR.getDecompiler().decompileClassNode(cn, cw.toByteArray()), false);
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							try {
								DiskWriter.replaceFile(MiscUtils.append(file, "-fernflower.java"),
										Decompiler.FERNFLOWER.getDecompiler().decompileClassNode(cn, cw.toByteArray()), false);
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							try {
								DiskWriter.replaceFile(MiscUtils.append(file, "-kraktau.java"),
										Decompiler.KRAKATAU.getDecompiler().decompileClassNode(cn, cw.toByteArray()), false);
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							BytecodeViewer.viewer.updateBusyStatus(false);
						} catch (Exception e) {
							BytecodeViewer.viewer.updateBusyStatus(false);
							new ExceptionUI(e);
						}
					});
					t1.start();
				}
				if (result == 1) {
					Thread t1 = new Thread(() -> {
						try {
							ClassNode cn = BytecodeViewer.getClassNode(s);
							final ClassWriter cw = new ClassWriter(0);
							try {
								Objects.requireNonNull(cn).accept(cw);
							} catch (Exception e) {
								e.printStackTrace();
								try {
									Thread.sleep(200);
									Objects.requireNonNull(cn).accept(cw);
								} catch (InterruptedException ignored) {
								}
							}
							String contents = Decompiler.PROCYON.getDecompiler().decompileClassNode(cn, cw.toByteArray());
							DiskWriter.replaceFile(path, contents, false);
							BytecodeViewer.viewer.updateBusyStatus(false);
						} catch (Exception e) {
							BytecodeViewer.viewer.updateBusyStatus(false);
							new ExceptionUI(
									e);
						}
					});
					t1.start();
				}
				if (result == 2) {
					Thread t1 = new Thread(() -> {
						try {
							ClassNode cn = BytecodeViewer.getClassNode(s);
							final ClassWriter cw = new ClassWriter(0);
							try {
								Objects.requireNonNull(cn).accept(cw);
							} catch (Exception e) {
								e.printStackTrace();
								try {
									Thread.sleep(200);
									Objects.requireNonNull(cn).accept(cw);
								} catch (InterruptedException ignored) {
								}
							}
							String contents = Decompiler.CFR.getDecompiler().decompileClassNode(cn, cw.toByteArray());
							DiskWriter.replaceFile(path, contents, false);
							BytecodeViewer.viewer.updateBusyStatus(false);
						} catch (Exception e) {
							BytecodeViewer.viewer.updateBusyStatus(false);
							new ExceptionUI(
									e);
						}
					});
					t1.start();
				}
				if (result == 3) {
					Thread t1 = new Thread(() -> {
						try {
							ClassNode cn = BytecodeViewer.getClassNode(s);
							final ClassWriter cw = new ClassWriter(0);
							try {
								Objects.requireNonNull(cn).accept(cw);
							} catch (Exception e) {
								e.printStackTrace();
								try {
									Thread.sleep(200);
									if (cn != null)
										cn.accept(cw);
								} catch (InterruptedException ignored) {
								}
							}
							String contents = Decompiler.FERNFLOWER.getDecompiler().decompileClassNode(cn,
									cw.toByteArray());
							DiskWriter.replaceFile(path, contents, false);
							BytecodeViewer.viewer.updateBusyStatus(false);
						} catch (Exception e) {
							BytecodeViewer.viewer.updateBusyStatus(false);
							new ExceptionUI(
									e);
						}
					});
					t1.start();
				}
				if (result == 4) {
					Thread t1 = new Thread(() -> {
						try {
							ClassNode cn = BytecodeViewer.getClassNode(s);
							final ClassWriter cw = new ClassWriter(0);
							try {
								Objects.requireNonNull(cn).accept(cw);
							} catch (Exception e) {
								e.printStackTrace();
								try {
									Thread.sleep(200);
									Objects.requireNonNull(cn).accept(cw);
								} catch (InterruptedException ignored) {
								}
							}
							
							String contents = Decompiler.KRAKATAU.getDecompiler().decompileClassNode(cn,
									cw.toByteArray());
							DiskWriter.replaceFile(path, contents, false);
							BytecodeViewer.viewer.updateBusyStatus(false);
						} catch (Exception e) {
							BytecodeViewer.viewer.updateBusyStatus(false);
							new ExceptionUI(
									e);
						}
					});
					t1.start();
				}
				if (result == 5) {
					BytecodeViewer.viewer.updateBusyStatus(false);
				}
			}
		});
		t.start();
	}
}
