package the.bytecode.club.bytecodeviewer.gui.components;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

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
 * @since 6/25/2021
 */
public class FileChooser extends JFileChooser
{
	public static final String EVERYTHING = "everything";
	
	public FileChooser(File file, String title, String description, String... extensions)
	{
		this(false, file, title, description, extensions);
	}
	
	public FileChooser(boolean skipFileFilter, File file, String title, String description, String... extensions)
	{
		Set<String> extensionSet = new HashSet<>(Arrays.asList(extensions));
		
		setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		try {
			setSelectedFile(file);
		} catch (Exception ignored) { }
		
		setDialogTitle(title);
		setFileHidingEnabled(false);
		setAcceptAllFileFilterUsed(false);
		if(!skipFileFilter)
		{
            addChoosableFileFilter(new FileFilter()
			{
				@Override
				public boolean accept(File f)
				{
					if (f.isDirectory())
						return true;
					
					if(extensions[0].equals(EVERYTHING))
						return true;
					
					return extensionSet.contains(MiscUtils.extension(f.getAbsolutePath()));
				}
				
				@Override
				public String getDescription() {
					return description;
				}
			});
		}
	}
}
