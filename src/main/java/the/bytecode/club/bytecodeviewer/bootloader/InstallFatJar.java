package the.bytecode.club.bytecodeviewer.bootloader;

import static the.bytecode.club.bytecodeviewer.Constants.AUTOMATIC_LIBRARY_UPDATING;

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
 * Downloads & installs the krakatau & enjarify zips
 *
 * Alternatively if OFFLINE_MODE is enabled it will drop the Krakatau and Enjarify versions supplied with BCV
 *
 * @author Konloch
 * @since 7/6/2021
 */
public class InstallFatJar implements Runnable
{
	@Override
	public void run()
	{
		try
		{
			if (AUTOMATIC_LIBRARY_UPDATING)
			{
				Boot.populateUrlList();
				Boot.populateLibsDirectory();
				Boot.downloadZipsOnly();
				Boot.checkKrakatau();
				Boot.checkEnjarify();
			}
			else
			{
				Boot.dropKrakatau();
				Boot.dropEnjarify();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
