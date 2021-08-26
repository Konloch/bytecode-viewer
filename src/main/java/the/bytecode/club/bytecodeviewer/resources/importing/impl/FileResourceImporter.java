package the.bytecode.club.bytecodeviewer.resources.importing.impl;

import java.io.File;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainerImporter;
import the.bytecode.club.bytecodeviewer.resources.importing.Importer;

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
public class FileResourceImporter implements Importer
{
	@Override
	public void open(File file) throws Exception
	{
		//create the new resource container
		ResourceContainer container = new ResourceContainer(file);
		//create the new file importer
		ResourceContainerImporter importer = new ResourceContainerImporter(container);
		//import the file into the resource container
		importer.importAsFile();
		//add the resource container to BCV's total loaded files
		BytecodeViewer.addResourceContainer(container);
	}
}
