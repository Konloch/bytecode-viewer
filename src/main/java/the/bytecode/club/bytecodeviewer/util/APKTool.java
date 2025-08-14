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

package the.bytecode.club.bytecodeviewer.util;

import org.apache.commons.io.FileUtils;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;

import java.io.File;

import static the.bytecode.club.bytecodeviewer.Constants.FS;
import static the.bytecode.club.bytecodeviewer.Constants.TEMP_DIRECTORY;

/**
 * @author Konloch
 */
public class APKTool
{
    public static final String DECODED_RESOURCES = "Decoded Resources";


    public static synchronized void decodeResources(File input, ResourceContainer container)
    {
        try
        {
            File dir = new File(TEMP_DIRECTORY + FS + MiscUtils.randomString(32) + FS + DECODED_RESOURCES);
            dir.mkdirs();

            File tempAPKPath = new File(TEMP_DIRECTORY + FS + MiscUtils.randomString(12));
            tempAPKPath.mkdirs();

            brut.apktool.Main.main(new String[] {
                "r",
                "--frame-path", tempAPKPath.getAbsolutePath(),
                "d", input.getAbsolutePath(),
                "-o", dir.getAbsolutePath(),
                "-f",
                "-jobs",
                String.valueOf(Runtime.getRuntime().availableProcessors())
            });

            container.APKToolContents = dir;
            tempAPKPath.delete();
        }
        catch (Exception e)
        {
            BytecodeViewer.handleException(e);
        }
    }

    public static synchronized void buildAPK(File output, ResourceContainer container)
    {
        String temp = TEMP_DIRECTORY + FS;
        File tempDir = new File(temp + FS + MiscUtils.getRandomizedName() + FS);
        tempDir.mkdirs();

        File tempAPKPath = new File(TEMP_DIRECTORY + FS + MiscUtils.randomString(12));
        tempAPKPath.mkdirs();

        try
        {
            File smaliFolder = new File(container.APKToolContents.getAbsolutePath() + FS + "smali");
            FileUtils.deleteDirectory(smaliFolder);

            //save entire jar as smali files
            System.out.println("Building!");
            brut.apktool.Main.main(new String[]{"b", container.APKToolContents.getAbsolutePath(),
                "--frame-path", tempAPKPath.getAbsolutePath(),
                "-o", output.getAbsolutePath()});

            //cleanup
            tempAPKPath.delete();
        }
        catch (Exception e)
        {
            BytecodeViewer.handleException(e);
        }
    }
}
