/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Konloch - Konloch.com / BytecodeViewer.com           *
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

package the.bytecode.club.bytecodeviewer.resources.importing.impl;

import java.io.File;
import org.apache.commons.io.FileUtils;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainerImporter;
import the.bytecode.club.bytecodeviewer.resources.importing.Importer;
import the.bytecode.club.bytecodeviewer.util.APKTool;
import the.bytecode.club.bytecodeviewer.util.Dex2Jar;
import the.bytecode.club.bytecodeviewer.util.Enjarify;
import the.bytecode.club.bytecodeviewer.util.JarUtils;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import static the.bytecode.club.bytecodeviewer.Constants.fs;
import static the.bytecode.club.bytecodeviewer.Constants.tempDirectory;

/**
 * @author Konloch
 * @since 6/26/2021
 */
public class APKResourceImporter implements Importer
{

	@Override
	public void open(File file) throws Exception
	{
		File tempCopy = new File(tempDirectory + fs + MiscUtils.randomString(32) + ".apk");
		FileUtils.copyFile(file, tempCopy);
		
		ResourceContainer container = new ResourceContainer(tempCopy, file.getName());
		
		// APK Resource Decoding Here
		if (BytecodeViewer.viewer.decodeAPKResources.isSelected())
		{
			File decodedResources = new File(tempDirectory + fs + MiscUtils.randomString(32) + ".apk");
			APKTool.decodeResources(tempCopy, decodedResources, container);
			container.resourceFiles = JarUtils.loadResources(decodedResources);
		}
		
		container.resourceFiles.putAll(JarUtils.loadResources(tempCopy)); // copy and rename
		// to prevent unicode filenames
		
		String name = MiscUtils.getRandomizedName() + ".jar";
		File output = new File(tempDirectory + fs + name);
		
		if (BytecodeViewer.viewer.apkConversionGroup.isSelected(BytecodeViewer.viewer.apkConversionDex.getModel()))
			Dex2Jar.dex2Jar(tempCopy, output);
		else if (BytecodeViewer.viewer.apkConversionGroup.isSelected(BytecodeViewer.viewer.apkConversionEnjarify.getModel()))
			Enjarify.apk2Jar(tempCopy, output);
		
		// create a new resource importer and copy the contents from it
		container.copy(new ResourceContainerImporter(
				new ResourceContainer(output)).importAsZip().getContainer());
		
		BytecodeViewer.addResourceContainer(container);
	}

}
