package the.bytecode.club.bytecodeviewer.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.CommandLineInput;
import the.bytecode.club.bytecodeviewer.bootloader.Boot;
import the.bytecode.club.bytecodeviewer.bootloader.loader.ILoader;
import the.bytecode.club.bytecodeviewer.bootloader.resource.external.EmptyExternalResource;
import the.bytecode.club.bytecodeviewer.bootloader.resource.external.ExternalResource;

import static the.bytecode.club.bytecodeviewer.Constants.nl;

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
 * Loads the libraries on boot. If booting failed for some reason, this kicks in as a fail safe.
 *
 * This broke with maven so now only FatJar builds will work.
 *
 * To get this system working again for smaller binaries/automatic updating libraries maven support will need to be added.
 *
 * @author Konloch
 * @author Bibl (don't ban me pls)
 * @since 6/21/2021
 */
public class BootCheck implements Runnable
{
	@Override
	public void run()
	{
		long start = System.currentTimeMillis();
		
		//7 second failsafe
		while (System.currentTimeMillis() - start < 7000)
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException ignored) { }
		}
		
		//if it's failed to boot and it's not downloading attempt to load the libraries
		failSafeLoadLibraries();
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void failSafeLoadLibraries()
	{
		if (!Boot.completedboot && !Boot.downloading)
		{
			File libsDir = Boot.libsDir();
			File[] listFiles = libsDir.listFiles();
			
			//first boot failed to download libraries
			if (listFiles == null || listFiles.length <= 0)
			{
				BytecodeViewer.showMessage(
						"Github is loading extremely slow, BCV needs to download libraries from github in order"
								+ nl + "to work, please try adjusting your network settings or manually downloading these libraries"
								+ nl + "if this error persists.");
				return;
			}
			
			Boot.setState("Bytecode Viewer Boot Screen (OFFLINE MODE) - Unable to connect to github, force booting...");
			System.out.println("Unable to connect to github, force booting...");
			
			List<String> libsFileList = new ArrayList<>();
			for (File f : listFiles)
				libsFileList.add(f.getAbsolutePath());
			
			ILoader<?> loader = Boot.findLoader();
			for (String s : libsFileList)
			{
				if (s.endsWith(".jar"))
				{
					File f = new File(s);
					if (f.exists())
					{
						Boot.setState("Bytecode Viewer Boot Screen (OFFLINE MODE) - Force Loading Library " + f.getName());
						System.out.println("Force loading library " + f.getName());
						
						try
						{
							ExternalResource res = new EmptyExternalResource<>(f.toURI().toURL());
							loader.bind(res);
							System.out.println("Successfully loaded " + f.getName());
						}
						catch (Exception e)
						{
							e.printStackTrace();
							f.delete();
							JOptionPane.showMessageDialog(null,
									"Error, Library " + f.getName() + " is corrupt, please restart to re-download it.",
									"Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
			
			Boot.checkEnjarify();
			Boot.checkKrakatau();
			
			Boot.globalstop = false;
			Boot.hide();
			
			int CLI = CommandLineInput.parseCommandLine(BytecodeViewer.launchArgs);
			
			
			if (CLI == CommandLineInput.GUI)
				BytecodeViewer.boot(false);
			else {
				BytecodeViewer.boot(true);
				CommandLineInput.executeCommandLine(BytecodeViewer.launchArgs);
			}
		}
	}
}
