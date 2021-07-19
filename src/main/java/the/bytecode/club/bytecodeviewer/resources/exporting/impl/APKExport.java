package the.bytecode.club.bytecodeviewer.resources.exporting.impl;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.gui.components.FileChooser;
import the.bytecode.club.bytecodeviewer.gui.components.MultipleChoiceDialogue;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.resources.exporting.Exporter;
import the.bytecode.club.bytecodeviewer.util.*;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static the.bytecode.club.bytecodeviewer.Constants.fs;
import static the.bytecode.club.bytecodeviewer.Constants.tempDirectory;

/**
 * @author Konloch
 * @since 6/27/2021
 */
public class APKExport implements Exporter
{
	@Override
	public void promptForExport()
	{
		if (BytecodeViewer.promptIfNoLoadedResources())
			return;
		
		List<ResourceContainer> containers = BytecodeViewer.getResourceContainers();
		List<ResourceContainer> validContainers = new ArrayList<>();
		List<String> validContainersNames = new ArrayList<>();
		ResourceContainer container;
		
		for (ResourceContainer resourceContainer : containers)
		{
			if (resourceContainer.APKToolContents != null && resourceContainer.APKToolContents.exists())
			{
				validContainersNames.add(resourceContainer.name);
				validContainers.add(resourceContainer);
			}
		}
		
		if (!validContainers.isEmpty())
		{
			container = validContainers.get(0);
			
			//if theres only one file in the container don't bother asking
			if (validContainers.size() >= 2)
			{
				MultipleChoiceDialogue dialogue = new MultipleChoiceDialogue("Bytecode Viewer - Select APK",
						"Which file would you like to export as an APK?",
						validContainersNames.toArray(new String[0]));
				
				container = containers.get(dialogue.promptChoice());
			}
		} else {
			BytecodeViewer.showMessage("You can only export as APK from a valid APK file. Make sure Settings>Decode Resources is ticked on." +
					"\n\nTip: Try exporting as DEX, it doesn't rely on decoded APK resources");
			return;
		}
		
		final ResourceContainer finalContainer = container;
		
		Thread exportThread = new Thread(() ->
		{
			if (!BytecodeViewer.autoCompileSuccessful())
				return;
			
			JFileChooser fc = new FileChooser(Configuration.getLastSaveDirectory(),
					"Select APK Export",
					"Android APK",
					"apk");
			
			int returnVal = fc.showSaveDialog(BytecodeViewer.viewer);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				Configuration.setLastSaveDirectory(fc.getSelectedFile());
				
				final File file = fc.getSelectedFile();
				String output = file.getAbsolutePath();
				
				//auto appened .apk
				if (!output.endsWith(".apk"))
					output = output + ".apk";
				
				final File file2 = new File(output);
				if (!DialogueUtils.canOverwriteFile(file2))
					return;
				
				Thread saveThread = new Thread(() ->
				{
					BytecodeViewer.updateBusyStatus(true);
					final String input = tempDirectory + fs + MiscUtils.getRandomizedName() + ".jar";
					JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), input);
					
					Thread buildAPKThread = new Thread(() ->
					{
						APKTool.buildAPK(new File(input), file2, finalContainer);
						BytecodeViewer.updateBusyStatus(false);
					}, "Process APK");
					buildAPKThread.start();
				}, "Jar Export");
				saveThread.start();
			}
		}, "Resource Export");
		exportThread.start();
	}
}
