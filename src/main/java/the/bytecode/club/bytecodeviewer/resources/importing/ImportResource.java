package the.bytecode.club.bytecodeviewer.resources.importing;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
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
	private boolean update = true;
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
				
				if (!file.exists())
				{
					update = false;
					BytecodeViewer.showMessage("The file " + file.getAbsolutePath() + " could not be found.");
				}
				else
				{
					if (file.isDirectory())
					{
						ImportType.DIRECTORY.getImporter().open(file);
					}
					else
					{
						if (fn.endsWith(".jar") || fn.endsWith(".zip") || fn.endsWith(".war"))
						{
							if(!ImportType.ZIP.getImporter().open(file))
								update = false;
						}
						else if (fn.endsWith(".class"))
						{
							if(!ImportType.CLASS.getImporter().open(file))
								update = false;
						}
						else if (fn.endsWith(".apk"))
						{
							ImportType.APK.getImporter().open(file);
							return;
						}
						else if (fn.endsWith(".dex"))
						{
							ImportType.DEX.getImporter().open(file);
							return;
						}
						else
						{
							ImportType.FILE.getImporter().open(file);
						}
					}
				}
			}
		}
		catch (final Exception e)
		{
			new ExceptionUI(e);
		}
		finally
		{
			BytecodeViewer.viewer.updateBusyStatus(false);
			
			if (update)
				try {
					Objects.requireNonNull(MainViewerGUI.getComponent(ResourceListPane.class)).updateTree();
				} catch (NullPointerException ignored) { }
		}
	}
}
