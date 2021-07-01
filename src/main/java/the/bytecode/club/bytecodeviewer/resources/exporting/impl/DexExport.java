package the.bytecode.club.bytecodeviewer.resources.exporting.impl;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.gui.components.FileChooser;
import the.bytecode.club.bytecodeviewer.gui.components.MultipleChoiceDialogue;
import the.bytecode.club.bytecodeviewer.resources.exporting.Exporter;
import the.bytecode.club.bytecodeviewer.util.Dex2Jar;
import the.bytecode.club.bytecodeviewer.util.JarUtils;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import javax.swing.*;
import java.io.File;

import static the.bytecode.club.bytecodeviewer.Constants.fs;
import static the.bytecode.club.bytecodeviewer.Constants.tempDirectory;

/**
 * @author Konloch
 * @since 6/27/2021
 */
public class DexExport implements Exporter
{
	
	@Override
	public void promptForExport()
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
				
				File outputPath = new File(output);
				if (outputPath.exists())
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
				
				Thread saveAsJar = new Thread(() ->
				{
					BytecodeViewer.viewer.updateBusyStatus(true);
					final String input = tempDirectory + fs + MiscUtils.getRandomizedName() + ".jar";
					JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), input);
					
					Thread saveAsDex = new Thread(() ->
					{
						Dex2Jar.saveAsDex(new File(input), outputPath);
						
						BytecodeViewer.viewer.updateBusyStatus(false);
					}, "Process DEX");
					saveAsDex.start();
				}, "Jar Export");
				saveAsJar.start();
			}
		}, "Resource Export");
		exportThread.start();
	}
}
