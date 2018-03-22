package the.bytecode.club.bytecodeviewer.decompilers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import me.konloch.kontainer.io.DiskReader;

import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Dex2Jar;
import the.bytecode.club.bytecodeviewer.MiscUtils;
import the.bytecode.club.bytecodeviewer.ZipUtils;

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
 * Smali Disassembler Wrapper
 *
 * @author Konloch
 */

public class SmaliDisassembler extends Decompiler {

    public String decompileClassNode(ClassNode cn, byte[] b) {
        String fileStart = BytecodeViewer.tempDirectory + BytecodeViewer.fs
                + "temp";

        String start = MiscUtils.getUniqueName(fileStart, ".class");

        final File tempClass = new File(start + ".class");
        final File tempZip = new File(start + ".jar");
        final File tempDex = new File(start + ".dex");
        final File tempSmali = new File(start + "-smali"); //output directory

        try {
            final FileOutputStream fos = new FileOutputStream(tempClass);

            fos.write(b);

            fos.close();
        } catch (final IOException e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }

        ZipUtils.zipFile(tempClass, tempZip);

        Dex2Jar.saveAsDex(tempZip, tempDex);

        try {
            org.jf.baksmali.main.main(new String[]{"-o", tempSmali.getAbsolutePath(), "-x", tempDex.getAbsolutePath()});
        } catch (Exception e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }

        File outputSmali = null;

        boolean found = false;
        File current = tempSmali;
        while (!found) {
            File f = current.listFiles()[0];
            if (f.isDirectory())
                current = f;
            else {
                outputSmali = f;
                found = true;
            }

        }
        try {
            return DiskReader.loadAsString(outputSmali.getAbsolutePath());
        } catch (Exception e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }

        return null;
    }

    @Override
    public void decompileToZip(String zipName) {

    }
}
