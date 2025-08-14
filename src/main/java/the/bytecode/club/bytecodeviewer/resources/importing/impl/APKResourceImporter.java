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

import org.apache.commons.io.FileUtils;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.resources.importing.Importer;
import the.bytecode.club.bytecodeviewer.util.*;
import the.bytecode.club.bytecodeviewer.util.apk2Jar.Apk2Jar;

import java.io.File;

import static the.bytecode.club.bytecodeviewer.Constants.FS;
import static the.bytecode.club.bytecodeviewer.Constants.TEMP_DIRECTORY;

/**
 * @author Konloch
 * @since 6/26/2021
 */
public class APKResourceImporter implements Importer
{

    @Override
    public void open(File file) throws Exception
    {
        File tempCopy = new File(TEMP_DIRECTORY + FS + MiscUtils.randomString(32) + ".apk");
        FileUtils.copyFile(file, tempCopy);

        openImpl(tempCopy, file.getName());
    }

    static void openImpl(File apkFile, String importName) throws Exception {
        ResourceContainer container = new ResourceContainer(apkFile, importName);

        // APK Resource Decoding Here
        if (BytecodeViewer.viewer.decodeAPKResources.isSelected())
        {
            APKTool.decodeResources(apkFile, container);
            container.resourceFiles = JarUtils.loadResourcesFromFolder(APKTool.DECODED_RESOURCES, container.APKToolContents);
        }

        container.resourceFiles.putAll(JarUtils.loadResources(apkFile)); // copy and rename
        // to prevent unicode filenames

        // create a new resource importer and copy the contents from it
        container.copy(Apk2Jar.obtainImpl().resourceContainerFromApk(apkFile));

        BytecodeViewer.addResourceContainer(container);
    }
}
