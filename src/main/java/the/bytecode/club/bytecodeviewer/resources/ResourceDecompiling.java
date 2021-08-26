package the.bytecode.club.bytecodeviewer.resources;

import java.io.File;
import java.util.Objects;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import me.konloch.kontainer.io.DiskWriter;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.gui.components.FileChooser;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.util.DialogUtils;
import the.bytecode.club.bytecodeviewer.util.JarUtils;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import static the.bytecode.club.bytecodeviewer.Constants.fs;
import static the.bytecode.club.bytecodeviewer.Constants.tempDirectory;

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
 * @author Konloch
 * @since 6/21/2021
 */
public class ResourceDecompiling
{
	public static void decompileSaveAll()
	{
		if (BytecodeViewer.promptIfNoLoadedClasses())
			return;
		
		Thread decompileThread = new Thread(() ->
		{
			if (!BytecodeViewer.autoCompileSuccessful())
				return;
			
			JFileChooser fc = new FileChooser(Configuration.getLastSaveDirectory(),
					"Select Zip Export",
					"Zip Archives",
					"zip");
			
			int returnVal = fc.showSaveDialog(BytecodeViewer.viewer);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				Configuration.setLastSaveDirectory(fc.getSelectedFile());
				
				File file = fc.getSelectedFile();
				
				//auto appened zip
				if (!file.getAbsolutePath().endsWith(".zip"))
					file = new File(file.getAbsolutePath() + ".zip");
				
				if (!DialogUtils.canOverwriteFile(file))
					return;
				
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
				
				BytecodeViewer.updateBusyStatus(true);
				
				File tempZip = new File(tempDirectory + fs + "temp_" + MiscUtils.getRandomizedName() + ".jar");
				if (tempZip.exists())
					tempZip.delete();
				
				JarUtils.saveAsJarClassesOnly(BytecodeViewer.getLoadedClasses(), tempZip.getAbsolutePath());
				
				if (result == 0) {
					Thread t12 = new Thread(() -> {
						try {
							Decompiler.PROCYON_DECOMPILER.getDecompiler().decompileToZip(tempZip.getAbsolutePath(),
									MiscUtils.append(javaSucks, "-procyon.zip"));
							BytecodeViewer.updateBusyStatus(false);
						} catch (Exception e) {
							BytecodeViewer.handleException(e);
						}
					});
					t12.start();
					Thread t2 = new Thread(() -> {
						try {
							BytecodeViewer.updateBusyStatus(true);
							Decompiler.CFR_DECOMPILER.getDecompiler().decompileToZip(tempZip.getAbsolutePath(),
									MiscUtils.append(javaSucks, "-CFR.zip"));
							BytecodeViewer.updateBusyStatus(false);
						} catch (Exception e) {
							BytecodeViewer.handleException(e);
						}
					});
					t2.start();
					Thread t3 = new Thread(() -> {
						try {
							BytecodeViewer.updateBusyStatus(true);
							Decompiler.FERNFLOWER_DECOMPILER.getDecompiler().decompileToZip(tempZip.getAbsolutePath(),
									MiscUtils.append(javaSucks, "-fernflower.zip"));
							BytecodeViewer.updateBusyStatus(false);
						} catch (Exception e) {
							BytecodeViewer.handleException(e);
						}
					});
					t3.start();
					Thread t4 = new Thread(() -> {
						try {
							BytecodeViewer.updateBusyStatus(true);
							Decompiler.KRAKATAU_DECOMPILER.getDecompiler().decompileToZip(tempZip.getAbsolutePath(),
									MiscUtils.append(javaSucks, "-kraktau.zip"));
							BytecodeViewer.updateBusyStatus(false);
						} catch (Exception e) {
							BytecodeViewer.handleException(e);
						}
					});
					t4.start();
				}
				if (result == 1) {
					Thread t12 = new Thread(() -> {
						try {
							Decompiler.PROCYON_DECOMPILER.getDecompiler().decompileToZip(tempZip.getAbsolutePath(), path);
							BytecodeViewer.updateBusyStatus(false);
						} catch (Exception e) {
							BytecodeViewer.handleException(e);
						}
					});
					t12.start();
				}
				if (result == 2) {
					Thread t12 = new Thread(() -> {
						try {
							Decompiler.CFR_DECOMPILER.getDecompiler().decompileToZip(tempZip.getAbsolutePath(), path);
							BytecodeViewer.updateBusyStatus(false);
						} catch (Exception e) {
							BytecodeViewer.handleException(e);
						}
					});
					t12.start();
				}
				if (result == 3) {
					Thread t12 = new Thread(() -> {
						try {
							Decompiler.FERNFLOWER_DECOMPILER.getDecompiler().decompileToZip(tempZip.getAbsolutePath(), path);
							BytecodeViewer.updateBusyStatus(false);
						} catch (Exception e) {
							BytecodeViewer.handleException(e);
						}
					});
					t12.start();
				}
				
				if (result == 4) {
					Thread t12 = new Thread(() -> {
						try {
							Decompiler.KRAKATAU_DECOMPILER.getDecompiler().decompileToZip(tempZip.getAbsolutePath(), path);
							BytecodeViewer.updateBusyStatus(false);
						} catch (Exception e) {
							BytecodeViewer.handleException(e);
						}
					});
					t12.start();
				}
				
				if (result == 5) {
					BytecodeViewer.updateBusyStatus(false);
				}
			}
		}, "Decompile Thread");
		decompileThread.start();
	}
	
	public static void decompileSaveOpenedOnly()
	{
		if (BytecodeViewer.promptIfNoLoadedClasses())
			return;
		
		if (!BytecodeViewer.isActiveResourceClass())
		{
			BytecodeViewer.showMessage(TranslatedStrings.FIRST_VIEW_A_CLASS.toString());
			return;
		}
		
		Thread decompileThread = new Thread(() ->
		{
			if (!BytecodeViewer.autoCompileSuccessful())
				return;
			
			final ClassNode cn = BytecodeViewer.getCurrentlyOpenedClassNode();
			
			JFileChooser fc = new FileChooser(Configuration.getLastSaveDirectory(),
					"Select Java Files",
					"Java Source Files",
					"java");
			
			int returnVal = fc.showSaveDialog(BytecodeViewer.viewer);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				Configuration.setLastSaveDirectory(fc.getSelectedFile());
				
				File file = fc.getSelectedFile();
				
				BytecodeViewer.updateBusyStatus(true);
				final String path = MiscUtils.append(file, ".java");
				
				if (!DialogUtils.canOverwriteFile(path))
					return;
				
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
								DiskWriter.replaceFile(MiscUtils.append(file, "-procyon.java"),
										Decompiler.PROCYON_DECOMPILER.getDecompiler().decompileClassNode(cn, cw.toByteArray()), false);
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							try {
								DiskWriter.replaceFile(MiscUtils.append(file, "-CFR.java"),
										Decompiler.CFR_DECOMPILER.getDecompiler().decompileClassNode(cn, cw.toByteArray()), false);
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							try {
								DiskWriter.replaceFile(MiscUtils.append(file, "-fernflower.java"),
										Decompiler.FERNFLOWER_DECOMPILER.getDecompiler().decompileClassNode(cn, cw.toByteArray()), false);
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							try {
								DiskWriter.replaceFile(MiscUtils.append(file, "-kraktau.java"),
										Decompiler.KRAKATAU_DECOMPILER.getDecompiler().decompileClassNode(cn, cw.toByteArray()), false);
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							BytecodeViewer.updateBusyStatus(false);
						} catch (Exception e) {
							BytecodeViewer.updateBusyStatus(false);
							BytecodeViewer.handleException(e);
						}
					});
					t1.start();
				}
				if (result == 1) {
					Thread t1 = new Thread(() -> {
						try {
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
							String contents = Decompiler.PROCYON_DECOMPILER.getDecompiler().decompileClassNode(cn, cw.toByteArray());
							DiskWriter.replaceFile(path, contents, false);
							BytecodeViewer.updateBusyStatus(false);
						} catch (Exception e) {
							BytecodeViewer.updateBusyStatus(false);
							BytecodeViewer.handleException(
									e);
						}
					});
					t1.start();
				}
				if (result == 2) {
					Thread t1 = new Thread(() -> {
						try {
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
							String contents = Decompiler.CFR_DECOMPILER.getDecompiler().decompileClassNode(cn, cw.toByteArray());
							DiskWriter.replaceFile(path, contents, false);
							BytecodeViewer.updateBusyStatus(false);
						} catch (Exception e) {
							BytecodeViewer.updateBusyStatus(false);
							BytecodeViewer.handleException(
									e);
						}
					});
					t1.start();
				}
				if (result == 3) {
					Thread t1 = new Thread(() -> {
						try {
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
							String contents = Decompiler.FERNFLOWER_DECOMPILER.getDecompiler().decompileClassNode(cn,
									cw.toByteArray());
							DiskWriter.replaceFile(path, contents, false);
							BytecodeViewer.updateBusyStatus(false);
						} catch (Exception e) {
							BytecodeViewer.updateBusyStatus(false);
							BytecodeViewer.handleException(
									e);
						}
					});
					t1.start();
				}
				if (result == 4) {
					Thread t1 = new Thread(() -> {
						try {
							final ClassWriter cw = new ClassWriter(0);
							try {
								Objects.requireNonNull(cn).accept(cw);
							} catch (Exception e) {
								e.printStackTrace();
								try {
									Thread.sleep(200);
									Objects.requireNonNull(cn).accept(cw);
								} catch (InterruptedException ignored) { }
							}
							
							String contents = Decompiler.KRAKATAU_DECOMPILER.getDecompiler().
									decompileClassNode(cn, cw.toByteArray());
							DiskWriter.replaceFile(path, contents, false);
							BytecodeViewer.updateBusyStatus(false);
						} catch (Exception e) {
							BytecodeViewer.updateBusyStatus(false);
							BytecodeViewer.handleException(e);
						}
					});
					t1.start();
				}
				if (result == 5) {
					BytecodeViewer.updateBusyStatus(false);
				}
			}
		}, "Decompile Thread");
		decompileThread.start();
	}
}
