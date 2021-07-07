package the.bytecode.club.bytecodeviewer.util;

import java.io.File;

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
 * @since 7/4/2021
 */
public class BCVFileUtils
{
	/**
	 * Searches a directory until the extension is found
	 */
	public static File findFile(File basePath, String extension)
	{
		for(File f : basePath.listFiles())
		{
			if(f.isDirectory())
			{
				File child = findFile(f, extension);
				
				if(child != null)
					return child;
				
				continue;
			}
			
			if(f.getName().endsWith(extension))
			{
				return f;
			}
		}
		
		return null;
	}
}
