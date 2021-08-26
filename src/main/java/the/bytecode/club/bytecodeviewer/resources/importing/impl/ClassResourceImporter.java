package the.bytecode.club.bytecodeviewer.resources.importing.impl;

import java.io.File;
import java.io.FileInputStream;
import org.apache.commons.io.FilenameUtils;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
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
public class ClassResourceImporter implements Importer
{
	@Override
	public void open(File file) throws Exception
	{
		final String name = file.getName();
		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] bytes = MiscUtils.getBytes(fis);
			ResourceContainer container = new ResourceContainer(file);

			if (MiscUtils.getFileHeaderMagicNumber(bytes).equalsIgnoreCase("cafebabe"))
			{
				final ClassNode cn = JarUtils.getNode(bytes);

				container.resourceClasses.put(FilenameUtils.removeExtension(name), cn);
				container.resourceClassBytes.put(name, bytes);
			}
			else
			{
				BytecodeViewer.showMessage(name + "\nHeader does not start with CAFEBABE\nimporting as resource instead.");

				//TODO double check this
				container.resourceFiles.put(name, bytes);
			}
			BytecodeViewer.addResourceContainer(container);
		}
	}
}
