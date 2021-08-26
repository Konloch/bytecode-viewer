package the.bytecode.club.bytecodeviewer.resources.importing.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import me.konloch.kontainer.io.DiskWriter;
import org.apache.commons.io.IOUtils;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.resources.importing.Import;
import the.bytecode.club.bytecodeviewer.resources.importing.Importer;
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
 * Compressed APKs (XAPK)
 *
 * @author Konloch
 * @since 6/26/2021
 */
public class XAPKResourceImporter implements Importer
{
	@Override
	public void open(File file) throws Exception
	{
		ResourceContainer container = new ResourceContainer(file);
		Map<String, byte[]> allDirectoryFiles = new LinkedHashMap<>();
		
		Configuration.silenceExceptionGUI++; //turn exceptions off
		try (ZipFile zipFile = new ZipFile(file))
		{
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements())
			{
				final ZipEntry entry = entries.nextElement();
				final String fileName = entry.getName();
				
				if(entry.isDirectory())
					continue;
				
				if (fileName.endsWith(".apk"))
				{
					File tempFile = new File(tempDirectory + fs + "temp" + MiscUtils.randomString(32) + fs + entry);
					tempFile.getParentFile().mkdirs();
					
					try (InputStream in = zipFile.getInputStream(entry);
					     OutputStream out = new FileOutputStream(tempFile))
					{
						IOUtils.copy(in, out);
					}
					Import.APK.getImporter().open(tempFile);
				}
				else
				{
					//pack files into a single container
					byte[] bytes;
					try (InputStream in = zipFile.getInputStream(entry))
					{
						bytes = IOUtils.toByteArray(in);
					}
					allDirectoryFiles.put(fileName, bytes);
				}
			}
		}
		
		Configuration.silenceExceptionGUI--; //turn exceptions back on
		BytecodeViewer.viewer.clearBusyStatus(); //clear errant busy signals from failed APK imports
		container.resourceFiles = allDirectoryFiles; //store the file resource
		BytecodeViewer.addResourceContainer(container); //add the resource container to BCV's total loaded files
	}
	
	public File exportTo(File original, String extension, byte[] bytes)
	{
		File file = new File(original.getAbsolutePath() + extension);
		DiskWriter.replaceFileBytes(file.getAbsolutePath(), bytes, false);
		return file;
	}
}
