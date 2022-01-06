package the.bytecode.club.bytecodeviewer.resources.importing;

import java.io.File;
import org.apache.commons.io.FilenameUtils;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Settings;

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
				System.out.println("Opening..." + file.getAbsolutePath());
				
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
				}
				//everything else import as a resource
				else if(!importKnownFile(file))
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
		}
	}
	
	/**
	 * Imports a file using File-Specific importers/decoders
	 */
	public static boolean importKnownFile(File file) throws Exception
	{
		final String fn = FilenameUtils.getName(file.getName()).toLowerCase();
		final String extension = fn.contains(":") ? null : FilenameUtils.getExtension(fn);
		
		Import imp = Import.extensionMap.get(extension);
		
		if(imp == null)
			return false;
		
		//import/decode the file using the file specific importer
		imp.getImporter().open(file);
		
		return true;
	}
}
