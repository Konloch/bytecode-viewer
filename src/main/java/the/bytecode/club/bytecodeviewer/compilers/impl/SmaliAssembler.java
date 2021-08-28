package the.bytecode.club.bytecodeviewer.compilers.impl;

import java.io.File;
import java.util.Objects;
import me.konloch.kontainer.io.DiskWriter;
import org.apache.commons.io.FileUtils;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.compilers.InternalCompiler;
import the.bytecode.club.bytecodeviewer.util.Dex2Jar;
import the.bytecode.club.bytecodeviewer.util.Enjarify;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;
import the.bytecode.club.bytecodeviewer.util.ZipUtils;

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
 * Smali Assembler Wrapper for Java
 *
 * @author Konloch
 */

public class SmaliAssembler extends InternalCompiler
{
    @Override
    public byte[] compile(String contents, String fullyQualifiedName)
    {
        String fileStart = tempDirectory + fs + "temp";
        int fileNumber = MiscUtils.getClassNumber(fileStart, ".dex");

        final File tempSmaliFolder = new File(fileStart + fileNumber + "-smalifolder" + fs);
        tempSmaliFolder.mkdir();

        File tempSmali = new File(tempSmaliFolder.getAbsolutePath() + fs + fileNumber + ".smali");
        File tempDex = new File("./out.dex");
        File tempJar = new File(fileStart + fileNumber + ".jar");
        File tempJarFolder = new File(fileStart + fileNumber + "-jar" + fs);

        try {
            DiskWriter.replaceFile(tempSmali.getAbsolutePath(), contents, false);
        } catch (final Exception e) {
            e.printStackTrace();
            //BytecodeViewer.handleException(e);
        }

        try {
            com.googlecode.d2j.smali.SmaliCmd.main(tempSmaliFolder.getAbsolutePath(),
                    "-o", tempDex.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            //BytecodeViewer.handleException(e);
        }


        if (BytecodeViewer.viewer.apkConversionGroup.isSelected(BytecodeViewer.viewer.apkConversionDex.getModel()))
            Dex2Jar.dex2Jar(tempDex, tempJar);
        else if (BytecodeViewer.viewer.apkConversionGroup.isSelected(BytecodeViewer.viewer.apkConversionEnjarify.getModel()))
            Enjarify.apk2Jar(tempDex, tempJar);

        System.out.println("Temporary dex: " + tempDex.getAbsolutePath());

        try {
            System.out.println("Unzipping to " + tempJarFolder.getAbsolutePath());
            ZipUtils.unzipFilesToPath(tempJar.getAbsolutePath(), tempJarFolder.getAbsolutePath());

            File outputClass = null;
            boolean found = false;
            File current = tempJarFolder;
            try {
                while (!found)
                {
                    File f = Objects.requireNonNull(current.listFiles())[0];
                    if (f.isDirectory())
                        current = f;
                    else
                    {
                        outputClass = f;
                        found = true;
                    }
                }

                System.out.println("Saved as: " + outputClass.getAbsolutePath());

                return FileUtils.readFileToByteArray(outputClass);
            } catch (java.lang.NullPointerException ignored) { }
        } catch (Exception e) {
            e.printStackTrace();
            //BytecodeViewer.handleException(e);
        }
        finally
        {
            tempDex.delete();
        }

        return null;
    }
}
