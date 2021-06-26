package the.bytecode.club.bytecodeviewer.util;

import me.konloch.kontainer.io.HTTPRequest;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.gui.components.FileChooser;
import the.bytecode.club.bytecodeviewer.gui.components.MultipleChoiceDialogue;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import static the.bytecode.club.bytecodeviewer.Constants.*;

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
public class VersionChecker implements Runnable
{
	@Override
	public void run()
	{
		try {
			HTTPRequest r = new HTTPRequest(new URL("https://raw.githubusercontent.com/Konloch/bytecode-viewer/master/VERSION"));
			final String version = r.readSingle();
			final String localVersion = VERSION + 0;
			try {
				int simplemaths = Integer.parseInt(version.replace(".", ""));
				int simplemaths2 = Integer.parseInt(localVersion.replace(".", ""));
				if (simplemaths2 > simplemaths)
					return; //developer version
			} catch (Exception ignored) {
			
			}
			
			if (!VERSION.equals(version))
			{
				MultipleChoiceDialogue outdatedDialogue = new MultipleChoiceDialogue("Bytecode Viewer - Outdated Version",
						"Your version: " + VERSION + ", latest version: "
						+ version + nl + "What would you like to do?",
						new String[]{"Open The Download Page", "Download The Updated Jar", "Do Nothing"});
				
				int result = outdatedDialogue.promptChoice();
				
				if (result == 0)
				{
					if (Desktop.isDesktopSupported())
						Desktop.getDesktop().browse(new URI("https://github.com/Konloch/bytecode-viewer/releases"));
					else
						BytecodeViewer.showMessage("Cannot open the page, please manually type it." + nl + "https://github.com/Konloch/bytecode-viewer/releases");
				}
				else if (result == 1)
				{
					JFileChooser fc = new FileChooser(new File(Configuration.lastDirectory),
							"Select Save File",
							"Zip Archives",
							"zip");
					
					try {
						fc.setCurrentDirectory(new File(".").getAbsoluteFile()); //set the current working directory
					} catch (Exception e) {
						new ExceptionUI(e);
					}
					
					int returnVal = fc.showSaveDialog(BytecodeViewer.viewer);
					if (returnVal == JFileChooser.APPROVE_OPTION)
					{
						Configuration.lastDirectory = fc.getSelectedFile().getAbsolutePath();
						File file = fc.getSelectedFile();
						if (!file.getAbsolutePath().endsWith(".zip"))
							file = new File(file.getAbsolutePath() + ".zip");
						
						if (file.exists())
						{
							MultipleChoiceDialogue overwriteDialogue = new MultipleChoiceDialogue("Bytecode Viewer - Overwrite File",
									"The file " + file + " exists, would you like to overwrite it?",
									new String[]{"Yes", "No"});
							
							if (overwriteDialogue.promptChoice() != 0)
								return;
							
							file.delete();
						}
						
						final File finalFile = file;
						Thread downloadThread = new Thread(() -> {
							try {
								InputStream is = new URL("https://github.com/Konloch/bytecode-viewer/releases/download/v" + version + "/BytecodeViewer." + version + ".zip").openConnection().getInputStream();
								FileOutputStream fos = new FileOutputStream(finalFile);
								try {
									System.out.println("Downloading from https://github.com/Konloch/bytecode-viewer/releases/download/v" + version + "/BytecodeViewer." + version + ".zip");
									byte[] buffer = new byte[8192];
									int len;
									int downloaded = 0;
									boolean flag = false;
									BytecodeViewer.showMessage("Downloading the jar in the background, when it's finished "
											+ "you will be alerted with another message box." + nl + nl +
											"Expect this to take several minutes.");
									
									while ((len = is.read(buffer)) > 0)
									{
										fos.write(buffer, 0, len);
										fos.flush();
										downloaded += 8192;
										int mbs = downloaded / 1048576;
										if (mbs % 5 == 0 && mbs != 0)
										{
											if (!flag)
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
										fos.flush();
										fos.close();
									}
								}
								System.out.println("Download finished!");
								BytecodeViewer.showMessage("Download successful! You can find the updated program at " + finalFile.getAbsolutePath());
							} catch (FileNotFoundException e) {
								try {
									InputStream is = new URL("https://github.com/Konloch/bytecode-viewer/releases/download/v" + version + "/BytecodeViewer." + version + ".jar"
									).openConnection().getInputStream();
									FileOutputStream fos = new FileOutputStream(finalFile);
									try {
										System.out.println("Downloading from https://github.com/Konloch/bytecode-viewer/releases/download/v" + version + "/BytecodeViewer." + version + ".jar");
										byte[] buffer = new byte[8192];
										int len;
										int downloaded = 0;
										boolean flag = false;
										BytecodeViewer.showMessage("Downloading the jar in the background, when it's "
												+ "finished you will be alerted with another message box." + nl + nl + "Expect this to take several minutes.");
										while ((len = is.read(buffer)) > 0) {
											fos.write(buffer, 0, len);
											fos.flush();
											downloaded += 8192;
											int mbs = downloaded / 1048576;
											if (mbs % 5 == 0 && mbs != 0) {
												if (!flag)
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
											fos.flush();
											fos.close();
										}
									}
									System.out.println("Download finished!");
									BytecodeViewer.showMessage("Download successful! You can find the updated program at " + finalFile.getAbsolutePath());
								} catch (FileNotFoundException ex) {
									BytecodeViewer.showMessage("Unable to download, the zip file has not been uploaded yet, "
											+ "please try again in about 10 minutes.");
								} catch (Exception ex) {
									new ExceptionUI(ex);
								}
								
							} catch (Exception e) {
								new ExceptionUI(e);
							}
							
						});
						downloadThread.start();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
