package the.bytecode.club.bytecodeviewer.resources.exporting.impl;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.gui.components.ExportJar;
import the.bytecode.club.bytecodeviewer.gui.components.FileChooser;
import the.bytecode.club.bytecodeviewer.gui.components.MultipleChoiceDialogue;
import the.bytecode.club.bytecodeviewer.resources.exporting.Exporter;

import javax.swing.*;
import java.io.File;

/**
 * @author Konloch
 * @since 6/27/2021
 */
public class RunnableJarExporter implements Exporter
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
		}, "Runnable Jar Export");
		exportThread.start();
	}
}
