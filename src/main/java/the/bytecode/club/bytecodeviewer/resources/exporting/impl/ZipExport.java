package the.bytecode.club.bytecodeviewer.resources.exporting.impl;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.gui.components.FileChooser;
import the.bytecode.club.bytecodeviewer.resources.exporting.Exporter;
import the.bytecode.club.bytecodeviewer.util.DialogueUtils;
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
		if (BytecodeViewer.promptIfNoLoadedResources())
			return;
		
		Thread exportThread = new Thread(() ->
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
				Configuration.lastSaveDirectory = fc.getSelectedFile().getAbsolutePath();
				File file = fc.getSelectedFile();
				
				//auto append .zip
				if (!file.getAbsolutePath().endsWith(".zip"))
					file = new File(file.getAbsolutePath() + ".zip");
				
				if (!DialogueUtils.canOverwriteFile(file))
					return;
				
				final File file2 = file;
				
				BytecodeViewer.updateBusyStatus(true);
				Thread saveThread = new Thread(() ->
				{
					JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), file2.getAbsolutePath());
					BytecodeViewer.updateBusyStatus(false);
				}, "Jar Export");
				saveThread.start();
			}
		}, "Resource Export");
		exportThread.start();
	}
}
