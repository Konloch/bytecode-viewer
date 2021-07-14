package the.bytecode.club.bytecodeviewer.resources.importing;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Settings;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.gui.resourcelist.ResourceListPane;
import the.bytecode.club.bytecodeviewer.gui.MainViewerGUI;

import java.io.File;
import java.util.Objects;

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
	private final File[] files;
	
	public ImportResource(File[] files) {this.files = files;}
	
	@Override
	public void run()
	{
		try
		{
			for (final File file : files)
			{
				final String fn = file.getName();
				
				//check if file exists
				if (!file.exists())
				{
					BytecodeViewer.showMessage("The file " + file.getAbsolutePath() + " could not be found.");
					Settings.removeRecentFile(file);
					continue;
				}
				
				//check if file is directory
				if (file.isDirectory())
				{
					Import.DIRECTORY.getImporter().open(file);
					continue;
				}
				
				//check for classes
				if (fn.endsWith(".class"))
					Import.CLASS.getImporter().open(file);
				
				//everything else import as a resource
				else if(!importFile(file))
					Import.FILE.getImporter().open(file);
			}
		}
		catch (final Exception e)
		{
			BytecodeViewer.handleException(e);
		}
		finally
		{
			BytecodeViewer.updateBusyStatus(false);
			try {
				BytecodeViewer.viewer.resourcePane.updateTree();
			} catch (NullPointerException ignored) { }
		}
	}
	
	public static boolean importFile(File file) throws Exception
	{
		final String fn = file.getName();
		
		//check for zip archives
		if (fn.endsWith(".jar") || fn.endsWith(".zip") || fn.endsWith(".war") || fn.endsWith(".ear"))
			Import.ZIP.getImporter().open(file);
			
		//check for XAPKs
		else if (fn.endsWith(".xapk"))
			Import.XAPK.getImporter().open(file);
			
		//check for APKs
		else if (fn.endsWith(".apk"))
			Import.APK.getImporter().open(file);
			
		//check for DEX
		else if (fn.endsWith(".dex"))
			Import.DEX.getImporter().open(file);
		
		//return false
		else
			return false;
		
		return true;
	}
}
