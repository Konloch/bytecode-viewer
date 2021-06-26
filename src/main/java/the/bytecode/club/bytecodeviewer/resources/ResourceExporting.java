package the.bytecode.club.bytecodeviewer.resources;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.gui.components.ExportJar;
import the.bytecode.club.bytecodeviewer.gui.components.FileChooser;
import the.bytecode.club.bytecodeviewer.gui.components.MultipleChoiceDialogue;
import the.bytecode.club.bytecodeviewer.util.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static the.bytecode.club.bytecodeviewer.Constants.fs;
import static the.bytecode.club.bytecodeviewer.Constants.tempDirectory;

/**
 * @author Konloch
 * @since 6/21/2021
 */
public class ResourceExporting
{
	public static void saveAsRunnableJar()
	{
		if (BytecodeViewer.getLoadedClasses().isEmpty())
		{
			BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
			return;
		}
		
		Thread exportThread = new Thread(() ->
		{
			if (BytecodeViewer.viewer.compileOnSave.isSelected() && !BytecodeViewer.compile(false))
				return;
			
			JFileChooser fc = new FileChooser(new File(Configuration.lastDirectory),
					"Select Jar Export",
					"Jar Archives",
					"jar");
			
			int returnVal = fc.showSaveDialog(BytecodeViewer.viewer);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				Configuration.lastDirectory = fc.getSelectedFile().getAbsolutePath();
				File file = fc.getSelectedFile();
				String path = file.getAbsolutePath();
				
				//auto append .jar
				if (!path.endsWith(".jar"))
					path = path + ".jar";
				
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
				
				new ExportJar(path).setVisible(true);
			}
		});
		exportThread.start();
	}
	
	public static void saveAsZip()
	{
		if (BytecodeViewer.getLoadedClasses().isEmpty())
		{
			BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
			return;
		}
		
		Thread exportThread = new Thread(() ->
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
				
				//auto append .zip
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
				
				final File file2 = file;
				
				BytecodeViewer.viewer.updateBusyStatus(true);
				Thread saveThread = new Thread(() ->
				{
					JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), file2.getAbsolutePath());
					BytecodeViewer.viewer.updateBusyStatus(false);
				});
				saveThread.start();
			}
		});
		exportThread.start();
	}
	
	public static void saveAsDex()
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
					"Select DEX Export",
					"Android DEX Files",
					"dex");
			
			int returnVal = fc.showSaveDialog(BytecodeViewer.viewer);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				Configuration.lastDirectory = fc.getSelectedFile().getAbsolutePath();
				final File file = fc.getSelectedFile();
				String output = file.getAbsolutePath();
				
				//auto append .dex
				if (!output.endsWith(".dex"))
					output = output + ".dex";
				
				final File file2 = new File(output);
				if (file2.exists())
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
				
				Thread t16 = new Thread(() -> {
					BytecodeViewer.viewer.updateBusyStatus(true);
					final String input = tempDirectory + fs + MiscUtils.getRandomizedName() + ".jar";
					JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), input);
					
					Thread t15 = new Thread(() -> {
						Dex2Jar.saveAsDex(new File(input), file2);
						
						BytecodeViewer.viewer.updateBusyStatus(false);
					});
					t15.start();
				});
				t16.start();
			}
		});
		t.start();
	}
	
	public static void saveAsAPK()
	{
		if (BytecodeViewer.getLoadedClasses().isEmpty())
		{
			BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
			return;
		}
		
		//if theres only one file in the container don't bother asking
		List<FileContainer> containers = BytecodeViewer.getFiles();
		List<FileContainer> validContainers = new ArrayList<>();
		List<String> validContainersNames = new ArrayList<>();
		FileContainer container;
		
		for (FileContainer fileContainer : containers)
		{
			if (fileContainer.APKToolContents != null && fileContainer.APKToolContents.exists())
			{
				validContainersNames.add(fileContainer.name);
				validContainers.add(fileContainer);
			}
		}
		
		if (!validContainers.isEmpty())
		{
			container = validContainers.get(0);
			
			if (validContainers.size() >= 2)
			{
				JOptionPane pane = new JOptionPane("Which file would you like to export as an APK?");
				Object[] options = validContainersNames.toArray(new String[0]);
				
				pane.setOptions(options);
				JDialog dialog = pane.createDialog(BytecodeViewer.viewer, "Bytecode Viewer - Select APK");
				dialog.setVisible(true);
				Object obj = pane.getValue();
				int result = -1;
				for (int k = 0; k < options.length; k++)
					if (options[k].equals(obj))
						result = k;
				
				container = containers.get(result);
			}
		} else {
			BytecodeViewer.showMessage("You can only export as APK from a valid APK file. Make sure "
					+ "Settings>Decode Resources is ticked on.\n\nTip: Try exporting as DEX, it doesn't rely on "
					+ "decoded APK resources");
			return;
		}
		
		final FileContainer finalContainer = container;
		
		Thread exportThread = new Thread(() ->
		{
			if (BytecodeViewer.viewer.compileOnSave.isSelected() && !BytecodeViewer.compile(false))
				return;
			
			JFileChooser fc = new FileChooser(new File(Configuration.lastDirectory),
					"Select APK Export",
					"Android APK",
					"apk");
			
			int returnVal = fc.showSaveDialog(BytecodeViewer.viewer);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				Configuration.lastDirectory = fc.getSelectedFile().getAbsolutePath();
				final File file = fc.getSelectedFile();
				String output = file.getAbsolutePath();
				
				//auto appened .apk
				if (!output.endsWith(".apk"))
					output = output + ".apk";
				
				final File file2 = new File(output);
				if (file2.exists())
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
				
				Thread saveThread = new Thread(() ->
				{
					BytecodeViewer.viewer.updateBusyStatus(true);
					final String input = tempDirectory + fs + MiscUtils.getRandomizedName() + ".jar";
					JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), input);
					
					Thread buildAPKThread = new Thread(() ->
					{
						APKTool.buildAPK(new File(input), file2, finalContainer);
						BytecodeViewer.viewer.updateBusyStatus(false);
					});
					buildAPKThread.start();
				});
				saveThread.start();
			}
		});
		exportThread.start();
	}
}
