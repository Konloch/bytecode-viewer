package the.bytecode.club.bytecodeviewer.util.resources;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.gui.resourcelist.ResourceListPane;
import the.bytecode.club.bytecodeviewer.gui.MainViewerGUI;
import the.bytecode.club.bytecodeviewer.util.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static the.bytecode.club.bytecodeviewer.Constants.tempDirectory;
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
 * @author Konloch
 */
public class ImportResource implements Runnable
{
	private boolean update = true;
	private final File[] files;
	
	public ImportResource(File[] files) {this.files = files;}
	
	@Override
	public void run()
	{
		try
		{
			for (final File f : files)
			{
				final String fn = f.getName();
				if (!f.exists()) {
					update = false;
					BytecodeViewer.showMessage("The file " + f.getAbsolutePath() + " could not be found.");
				} else {
					if (f.isDirectory()) {
						FileContainer container = new FileContainer(f);
						HashMap<String, byte[]> files1 = new HashMap<>();
						boolean finished = false;
						ArrayList<File> totalFiles = new ArrayList<>();
						totalFiles.add(f);
						String dir = f.getAbsolutePath();//f.getAbsolutePath().substring(0, f.getAbsolutePath
						// ().length()-f.getName().length());
						
						while (!finished) {
							boolean added = false;
							for (int i = 0; i < totalFiles.size(); i++) {
								File child = totalFiles.get(i);
								if (child.listFiles() != null)
									for (File rocket : Objects.requireNonNull(child.listFiles()))
										if (!totalFiles.contains(rocket)) {
											totalFiles.add(rocket);
											added = true;
										}
							}
							
							if (!added) {
								for (File child : totalFiles)
									if (child.isFile()) {
										String fileName = child.getAbsolutePath().substring(dir.length() + 1
										).replaceAll("\\\\", "\\/");
										
										
										files1.put(fileName,
												Files.readAllBytes(Paths.get(child.getAbsolutePath())));
									}
								finished = true;
							}
						}
						container.files = files1;
						BytecodeViewer.files.add(container);
					} else {
						if (fn.endsWith(".jar") || fn.endsWith(".zip") || fn.endsWith(".war")) {
							try {
								JarUtils.put(f);
							} catch (IOException z) {
								try {
									JarUtils.put2(f);
								} catch (final Exception e) {
									new ExceptionUI(e);
									update = false;
								}
							} catch (final Exception e) {
								new ExceptionUI(e);
								update = false;
							}
							
						} else if (fn.endsWith(".class")) {
							try {
								byte[] bytes = JarUtils.getBytes(new FileInputStream(f));
								String cafebabe = String.format("%02X", bytes[0]) + String.format("%02X",
										bytes[1]) + String.format("%02X", bytes[2]) + String.format("%02X",
										bytes[3]);
								if (cafebabe.equalsIgnoreCase("cafebabe")) {
									final ClassNode cn = JarUtils.getNode(bytes);
									
									FileContainer container = new FileContainer(f);
									container.classes.add(cn);
									BytecodeViewer.files.add(container);
								} else {
									BytecodeViewer.showMessage(fn + ": Header does not start with CAFEBABE, ignoring.");
									update = false;
								}
							} catch (final Exception e) {
								new ExceptionUI(e);
								update = false;
							}
						} else if (fn.endsWith(".apk")) {
							try {
								BytecodeViewer.viewer.updateBusyStatus(true);
								
								File tempCopy = new File(tempDirectory + fs + MiscUtils.randomString(32) + ".apk");
								
								FileUtils.copyFile(f, tempCopy);
								
								FileContainer container = new FileContainer(tempCopy, f.getName());
								
								if (BytecodeViewer.viewer.decodeAPKResources.isSelected()) {
									File decodedResources =
											new File(tempDirectory + fs + MiscUtils.randomString(32) + ".apk");
									APKTool.decodeResources(tempCopy, decodedResources, container);
									container.files = JarUtils.loadResources(decodedResources);
								}
								
								Objects.requireNonNull(container.files).putAll(JarUtils.loadResources(tempCopy)); //copy and rename
								// to prevent unicode filenames
								
								String name = MiscUtils.getRandomizedName() + ".jar";
								File output = new File(tempDirectory + fs + name);
								
								if (BytecodeViewer.viewer.apkConversionGroup.isSelected(BytecodeViewer.viewer.apkConversionDex.getModel()))
									Dex2Jar.dex2Jar(tempCopy, output);
								else if (BytecodeViewer.viewer.apkConversionGroup.isSelected(BytecodeViewer.viewer.apkConversionEnjarify.getModel()))
									Enjarify.apk2Jar(tempCopy, output);
								
								container.classes = JarUtils.loadClasses(output);
								
								BytecodeViewer.viewer.updateBusyStatus(false);
								BytecodeViewer.files.add(container);
							} catch (final Exception e) {
								new ExceptionUI(e);
							}
							return;
						} else if (fn.endsWith(".dex")) {
							try {
								BytecodeViewer.viewer.updateBusyStatus(true);
								
								File tempCopy = new File(tempDirectory + fs + MiscUtils.randomString(32) +
										".dex");
								
								FileUtils.copyFile(f, tempCopy); //copy and rename to prevent unicode filenames
								
								FileContainer container = new FileContainer(tempCopy, f.getName());
								
								String name = MiscUtils.getRandomizedName() + ".jar";
								File output = new File(tempDirectory + fs + name);
								
								if (BytecodeViewer.viewer.apkConversionGroup.isSelected(BytecodeViewer.viewer.apkConversionDex.getModel()))
									Dex2Jar.dex2Jar(tempCopy, output);
								else if (BytecodeViewer.viewer.apkConversionGroup.isSelected(BytecodeViewer.viewer.apkConversionEnjarify.getModel()))
									Enjarify.apk2Jar(tempCopy, output);
								
								container.classes = JarUtils.loadClasses(output);
								
								BytecodeViewer.viewer.updateBusyStatus(false);
								BytecodeViewer.files.add(container);
							} catch (final Exception e) {
								new ExceptionUI(e);
							}
							return;
						} else {
							HashMap<String, byte[]> files1 = new HashMap<>();
							byte[] bytes = JarUtils.getBytes(new FileInputStream(f));
							files1.put(f.getName(), bytes);
							
							
							FileContainer container = new FileContainer(f);
							container.files = files1;
							BytecodeViewer.files.add(container);
						}
					}
				}
			}
		} catch (final Exception e) {
			new ExceptionUI(e);
		} finally {
			BytecodeViewer.viewer.updateBusyStatus(false);
			
			if (update)
				try {
					Objects.requireNonNull(MainViewerGUI.getComponent(ResourceListPane.class)).updateTree();
				} catch (NullPointerException ignored) {
				}
		}
	}
}
