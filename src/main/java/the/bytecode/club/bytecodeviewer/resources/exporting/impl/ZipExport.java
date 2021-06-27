package the.bytecode.club.bytecodeviewer.resources.exporting.impl;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.gui.components.FileChooser;
import the.bytecode.club.bytecodeviewer.gui.components.MultipleChoiceDialogue;
import the.bytecode.club.bytecodeviewer.resources.exporting.Exporter;
import the.bytecode.club.bytecodeviewer.util.JarUtils;

import javax.swing.*;
import java.io.File;

/**
 * @author Konloch
 * @since 6/27/2021
 */
public class ZipExport implements Exporter
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
}
