package the.bytecode.club.bytecodeviewer.util;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import me.konloch.kontainer.io.DiskWriter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.SecurityMan;

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
 */
public class APKTool {

    public static synchronized void decodeResources(File input, File output, FileContainer container) {
        try {
            File dir = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + MiscUtils.randomString(32)+BytecodeViewer.fs+"Decoded Resources");
            dir.mkdirs();

            File tempAPKPath = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + MiscUtils.randomString(12));
            tempAPKPath.mkdirs();
            brut.apktool.Main.main(new String[]{"r", "--frame-path", tempAPKPath.getAbsolutePath(), "d", input.getAbsolutePath(), "-o", dir.getAbsolutePath(), "-f"});

            File zip = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + MiscUtils.randomString(12) + ".zip");
            ZipUtils.zipFolderAPKTool(dir.getAbsolutePath(), zip.getAbsolutePath());

            if (zip.exists())
                zip.renameTo(output);

            container.APKToolContents = dir;
            tempAPKPath.delete();
        } catch (Exception e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }
    }

    public static synchronized void buildAPK(File input, File output, FileContainer container)
    {
        String name = container.file.getName().toLowerCase();
        String temp = BytecodeViewer.tempDirectory + BytecodeViewer.fs;
        File tempDir = new File(temp+BytecodeViewer.fs+BytecodeViewer.getRandomizedName()+BytecodeViewer.fs);
        tempDir.mkdirs();


        File tempAPKPath = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + MiscUtils.randomString(12));
        tempAPKPath.mkdirs();

        try
        {
            File smaliFolder = new File(container.APKToolContents.getAbsolutePath()+BytecodeViewer.fs+"smali");
            FileUtils.deleteDirectory(smaliFolder);


            //save entire jar as smali files
            System.out.println("Building!");
            BytecodeViewer.sm.stopBlocking();
            brut.apktool.Main.main(new String[]{"b", container.APKToolContents.getAbsolutePath(), "--frame-path", tempAPKPath.getAbsolutePath(), "-o", output.getAbsolutePath()});
            BytecodeViewer.sm.setBlocking();
            tempAPKPath.delete();
        }
        catch (Exception e)
        {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }
    }
}
