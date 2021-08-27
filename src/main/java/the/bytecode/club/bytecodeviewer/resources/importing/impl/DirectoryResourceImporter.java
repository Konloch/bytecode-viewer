package the.bytecode.club.bytecodeviewer.resources.importing.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.resources.importing.ImportResource;
import the.bytecode.club.bytecodeviewer.resources.importing.Importer;
import the.bytecode.club.bytecodeviewer.util.JarUtils;
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
 * @since 6/26/2021
 */
public class DirectoryResourceImporter implements Importer
{
	@Override
	public void open(File file) throws Exception
	{
		ResourceContainer container = new ResourceContainer(file);
		Map<String, byte[]> allDirectoryFiles = new LinkedHashMap<>();
		Map<String, ClassNode> allDirectoryClasses = new LinkedHashMap<>();
		
		boolean finished = false;
		List<File> totalFiles = new ArrayList<>();
		totalFiles.add(file);
		String dir = file.getAbsolutePath();
		
		while (!finished)
		{
			boolean added = false;
			for (int i = 0; i < totalFiles.size(); i++)
			{
				File child = totalFiles.get(i);
				for (File rocket : MiscUtils.listFiles(child))
					if (!totalFiles.contains(rocket))
					{
						totalFiles.add(rocket);
						added = true;
					}
			}
			
			if (!added)
			{
				for (File child : totalFiles)
				{
					if(!child.isFile())
						continue;
					
					final String trimmedPath = child.getAbsolutePath().substring(dir.length() + 1)
							.replaceAll("\\\\", "\\/");
					final String fileName = child.getName();
					
					if (fileName.endsWith(".class"))
					{
						byte[] bytes = Files.readAllBytes(Paths.get(child.getAbsolutePath()));
						if (MiscUtils.getFileHeaderMagicNumber(bytes).equalsIgnoreCase("cafebabe"))
						{
							final ClassNode cn = JarUtils.getNode(bytes);
							allDirectoryClasses.put(FilenameUtils.removeExtension(trimmedPath), cn);
						}
					}
					//attempt to import archives automatically
					else if(ImportResource.importKnownFile(file))
					{
						//let import resource handle it
					}
					else //pack files into a single container
					{
						allDirectoryFiles.put(trimmedPath, Files.readAllBytes(Paths.get(child.getAbsolutePath())));
					}
				}
				
				finished = true;
			}
		}
		
		container.resourceClasses.putAll(allDirectoryClasses);
		container.resourceFiles = allDirectoryFiles;
		BytecodeViewer.addResourceContainer(container);
	}
}
