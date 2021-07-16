package the.bytecode.club.bytecodeviewer.resources.exporting.impl;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.gui.components.FileChooser;
import the.bytecode.club.bytecodeviewer.resources.exporting.Exporter;
import the.bytecode.club.bytecodeviewer.util.Dex2Jar;
import the.bytecode.club.bytecodeviewer.util.DialogueUtils;
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
		if (BytecodeViewer.promptIfNoLoadedResources())
			return;
		
		Thread exportThread = new Thread(() ->
		{
			if (!BytecodeViewer.autoCompileSuccessful())
				return;
			
			JFileChooser fc = new FileChooser(Configuration.getLastDirectory(),
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
				if (!DialogueUtils.canOverwriteFile(outputPath))
					return;
				
				Thread saveAsJar = new Thread(() ->
				{
					BytecodeViewer.updateBusyStatus(true);
					final String input = tempDirectory + fs + MiscUtils.getRandomizedName() + ".jar";
					JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), input);
					
					Thread saveAsDex = new Thread(() ->
					{
						Dex2Jar.saveAsDex(new File(input), outputPath);
						
						BytecodeViewer.updateBusyStatus(false);
					}, "Process DEX");
					saveAsDex.start();
				}, "Jar Export");
				saveAsJar.start();
			}
		}, "Resource Export");
		exportThread.start();
	}
}
