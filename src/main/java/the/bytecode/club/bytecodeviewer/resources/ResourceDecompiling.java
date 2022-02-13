package the.bytecode.club.bytecodeviewer.resources;

import java.io.File;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import me.konloch.kontainer.io.DiskWriter;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.api.BCV;
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
	private static final int DECOMPILE_SAVE_ALL = 10;
	private static final int DECOMPILE_SAVE_ALL_PROCYON = 11;
	private static final int DECOMPILE_SAVE_ALL_CFR = 12;
	private static final int DECOMPILE_SAVE_ALL_FERNFLOWER = 13;
	private static final int DECOMPILE_SAVE_ALL_KRAKATAU = 14;
	//TODO JDGUI,JADX
	
	private static final int DECOMPILE_OPENED_ONLY_ALL = 20;
	private static final int DECOMPILE_OPENED_ONLY_PROCYON = 21;
	private static final int DECOMPILE_OPENED_ONLY_CFR = 22;
	private static final int DECOMPILE_OPENED_ONLY_FERNFLOWER = 23;
	private static final int DECOMPILE_OPENED_ONLY_KRAKATAU = 24;
	//TODO JDGUI,JADX
	
	public static void decompileSaveAll()
	{
		//alert the user if no classes have been imported into BCV
		if (BytecodeViewer.promptIfNoLoadedClasses())
			return;
		
		MiscUtils.createNewThread("Decompile Save-All Thread", () ->
		{
			//signal to the user that BCV is performing an action in the background
			BytecodeViewer.updateBusyStatus(true);
			
			//auto compile before decompilation
			if (!BytecodeViewer.autoCompileSuccessful())
				return;
			
			final JFileChooser fc = new FileChooser(Configuration.getLastSaveDirectory(), "Select Zip Export",
					"Zip Archives", "zip");
			
			//if the user doesn't select a file then we should stop while we're ahead
			if (fc.showSaveDialog(BytecodeViewer.viewer) != JFileChooser.APPROVE_OPTION)
				return;
			
			//set the last touched save directory for BCV
			Configuration.setLastSaveDirectory(fc.getSelectedFile());
			
			//get the save file and auto append zip extension
			final File outputZip = MiscUtils.autoAppendFileExtension(".zip", fc.getSelectedFile());
			
			//prompt the user for a dialogue override-this-file option if the file already exists
			if (!DialogUtils.canOverwriteFile(outputZip))
				return;
			
			//this temporary jar file will be used to store the classes while BCV performs decompilation
			File temporaryTargetJar = MiscUtils.deleteExistingFile(new File(tempDirectory + fs + "temp_" + MiscUtils.getRandomizedName() + ".jar"));

			//extract all the loaded classes imported into BCV to the temporary target jar
			JarUtils.saveAsJarClassesOnly(BytecodeViewer.getLoadedClasses(), temporaryTargetJar.getAbsolutePath());
			
			//signal to the user that BCV is finished performing that action
			BytecodeViewer.updateBusyStatus(false);
			
			try
			{
				//handle the result of the user selection
				switch (promptDecompilerUserSelect() + DECOMPILE_SAVE_ALL)
				{
					case DECOMPILE_SAVE_ALL:
						//decompile using procyon
						decompileSaveAll(Decompiler.PROCYON_DECOMPILER, temporaryTargetJar, outputZip, true);
						
						//decompile using CFR
						decompileSaveAll(Decompiler.CFR_DECOMPILER, temporaryTargetJar, outputZip, true);
						
						//decompile using fern
						decompileSaveAll(Decompiler.FERNFLOWER_DECOMPILER, temporaryTargetJar, outputZip, true);
						
						//decompile using krakatau
						decompileSaveAll(Decompiler.KRAKATAU_DECOMPILER, temporaryTargetJar, outputZip, true);
						break;
					
					case DECOMPILE_SAVE_ALL_PROCYON:
						//decompile using procyon
						decompileSaveAll(Decompiler.PROCYON_DECOMPILER, temporaryTargetJar, outputZip, false);
						break;
					
					case DECOMPILE_SAVE_ALL_CFR:
						//decompile using CFR
						decompileSaveAll(Decompiler.CFR_DECOMPILER, temporaryTargetJar, outputZip, false);
						break;
					
					case DECOMPILE_SAVE_ALL_FERNFLOWER:
						//decompile using fern
						decompileSaveAll(Decompiler.FERNFLOWER_DECOMPILER, temporaryTargetJar, outputZip, false);
						break;
					
					case DECOMPILE_SAVE_ALL_KRAKATAU:
						//decompile using krakatau
						decompileSaveAll(Decompiler.KRAKATAU_DECOMPILER, temporaryTargetJar, outputZip, false);
						break;
				}
			}
			catch (Exception e)
			{
				BytecodeViewer.handleException(e);
			}
		});
	}
	
	public static void decompileSaveOpenedResource()
	{
		//alert the user if no classes have been imported into BCV
		if (BytecodeViewer.promptIfNoLoadedClasses())
			return;
		
		//verify the active resource is a valid class file
		if (!BytecodeViewer.isActiveResourceClass())
		{
			BytecodeViewer.showMessage(TranslatedStrings.FIRST_VIEW_A_CLASS.toString());
			return;
		}
		
		MiscUtils.createNewThread("Decompile Save Opened Resource", () ->
		{
			//signal to the user that BCV is performing an action in the background
			BytecodeViewer.updateBusyStatus(true);
			
			//auto compile before decompilation
			if (!BytecodeViewer.autoCompileSuccessful())
				return;
			
			JFileChooser fc = new FileChooser(Configuration.getLastSaveDirectory(), "Select Java Files",
					"Java Source Files", "java");
			
			//if the user doesn't select a file then we should stop while we're ahead
			if(fc.showSaveDialog(BytecodeViewer.viewer) != JFileChooser.APPROVE_OPTION)
				return;
			
			//set the last touched save directory for BCV
			Configuration.setLastSaveDirectory(fc.getSelectedFile());
			
			//get the save file and auto append java extension
			File file = MiscUtils.autoAppendFileExtension(".java", fc.getSelectedFile());
			
			//prompt the user for a dialogue override-this-file option if the file already exists
			if (!DialogUtils.canOverwriteFile(file))
				return;
			
			//signal to the user that BCV is finished performing that action
			BytecodeViewer.updateBusyStatus(false);
			
			try
			{
				//handle the result of the user selection
				switch(promptDecompilerUserSelect() + DECOMPILE_OPENED_ONLY_ALL)
				{
					case DECOMPILE_OPENED_ONLY_ALL:
						//decompile using procyon
						decompileCurrentlyOpenedResource(Decompiler.PROCYON_DECOMPILER, file, true);
						
						//decompile using cfr
						decompileCurrentlyOpenedResource(Decompiler.CFR_DECOMPILER, file, true);
						
						//decompile using fernflower
						decompileCurrentlyOpenedResource(Decompiler.FERNFLOWER_DECOMPILER, file, true);
						
						//decompile using krakatau
						decompileCurrentlyOpenedResource(Decompiler.KRAKATAU_DECOMPILER, file, true);
						break;
						
					case DECOMPILE_OPENED_ONLY_PROCYON:
						//decompile using procyon
						decompileCurrentlyOpenedResource(Decompiler.PROCYON_DECOMPILER, file, false);
						break;
						
					case DECOMPILE_OPENED_ONLY_CFR:
						//decompile using cfr
						decompileCurrentlyOpenedResource(Decompiler.CFR_DECOMPILER, file, false);
						break;
						
					case DECOMPILE_OPENED_ONLY_FERNFLOWER:
						//decompile using fernflower
						decompileCurrentlyOpenedResource(Decompiler.FERNFLOWER_DECOMPILER, file, false);
						break;
						
					case DECOMPILE_OPENED_ONLY_KRAKATAU:
						//decompile using krakatau
						decompileCurrentlyOpenedResource(Decompiler.KRAKATAU_DECOMPILER, file, false);
						break;
				}
			}
			catch (Exception e)
			{
				BytecodeViewer.handleException(e);
			}
		});
	}
	
	public static int promptDecompilerUserSelect()
	{
		final JOptionPane pane = new JOptionPane("Which decompiler would you like to use?");
		final Object[] options = new String[]{ "All", "Procyon", "CFR",
				"Fernflower", "Krakatau", "Cancel"}; //TODO JDGUI,JADX
		
		pane.setOptions(options);
		final JDialog dialog = pane.createDialog(BytecodeViewer.viewer, "Bytecode Viewer - Select Decompiler");
		dialog.setVisible(true);
		final Object obj = pane.getValue();
		
		int result = -1;
		for (int k = 0; k < options.length; k++)
			if (options[k].equals(obj))
				result = k;
		
		return result;
	}
	
	public static void decompileSaveAll(Decompiler decompiler, File targetJar, File outputZip, boolean saveAll)
	{
		//signal to the user that BCV is performing an action in the background
		BytecodeViewer.updateBusyStatus(true);
		
		//decompile all opened classes to zip
		decompiler.getDecompiler().decompileToZip(targetJar.getAbsolutePath(), saveAll
				? MiscUtils.append(outputZip, "-" + decompiler.getDecompilerNameProgrammic() + ".zip")
				: outputZip.getAbsolutePath());
		
		//signal to the user that BCV is finished performing that action
		BytecodeViewer.updateBusyStatus(false);
	}
	
	public static void decompileCurrentlyOpenedResource(Decompiler decompiler, File outputFile, boolean saveAll)
	{
		//signal to the user that BCV is performing an action in the background
		BytecodeViewer.updateBusyStatus(true);
		
		//decompile the currently opened resource and save it to the specified file
		DiskWriter.replaceFile(saveAll
						? MiscUtils.append(outputFile, "-" + decompiler.getDecompilerNameProgrammic() + ".java")
						: outputFile.getAbsolutePath(),
				BCV.decompileCurrentlyOpenedClassNode(decompiler), false);
		
		//signal to the user that BCV is finished performing that action
		BytecodeViewer.updateBusyStatus(false);
	}
}