package the.bytecode.club.bytecodeviewer.resources.exporting.impl;

import java.io.File;
import javax.swing.JFileChooser;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.gui.components.ExportJar;
import the.bytecode.club.bytecodeviewer.gui.components.FileChooser;
import the.bytecode.club.bytecodeviewer.resources.exporting.Exporter;
import the.bytecode.club.bytecodeviewer.util.DialogUtils;

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
 * @since 6/27/2021
 */
public class RunnableJarExporter implements Exporter
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
					"Select Jar Export",
					"Jar Archives",
					"jar");
			
			int returnVal = fc.showSaveDialog(BytecodeViewer.viewer);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				Configuration.setLastSaveDirectory(fc.getSelectedFile());
				
				File file = fc.getSelectedFile();
				String path = file.getAbsolutePath();
				
				//auto append .jar
				if (!path.endsWith(".jar"))
					path += ".jar";
				
				if (!DialogUtils.canOverwriteFile(path))
					return;
				
				new ExportJar(path).setVisible(true);
			}
		}, "Runnable Jar Export");
		exportThread.start();
	}
}
