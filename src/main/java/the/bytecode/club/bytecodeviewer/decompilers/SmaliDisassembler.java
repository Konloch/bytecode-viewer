package the.bytecode.club.bytecodeviewer.decompilers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
import me.konloch.kontainer.io.DiskReader;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.util.Dex2Jar;
import the.bytecode.club.bytecodeviewer.util.FileContainer;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import static the.bytecode.club.bytecodeviewer.Constants.*;

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

public class SmaliDisassembler extends InternalDecompiler
{

    public String decompileClassNode(FileContainer container, ClassNode cn, byte[] b) {
        String exception = "";
        String fileStart = tempDirectory + fs + "temp";

        String start = MiscUtils.getUniqueName(fileStart, ".class");

        final File tempClass = new File(start + ".class");
        final File tempDex = new File(start + ".dex");
        final File tempSmali = new File(start + "-smali"); //output directory

        try {
            final FileOutputStream fos = new FileOutputStream(tempClass);

            fos.write(b);

            fos.close();
        } catch (final IOException e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }

        //ZipUtils.zipFile(tempClass, tempZip);

        Dex2Jar.saveAsDex(tempClass, tempDex, true);

        try {
            com.googlecode.d2j.smali.BaksmaliCmd.main(new String[]{tempDex.getAbsolutePath()});
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            e.printStackTrace();

            exception += "Bytecode Viewer Version: " + VERSION + nl + nl + sw;
        }

        File rename = new File(tempDex.getName().replaceFirst("\\.dex", "-out"));

        try {
            FileUtils.moveDirectory(rename, tempSmali);
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            e.printStackTrace();

            exception += "Bytecode Viewer Version: " + VERSION + nl + nl + sw;
        }

        File outputSmali = null;

        boolean found = false;
        File current = tempSmali;
        while (!found) {
            File f = Objects.requireNonNull(current.listFiles())[0];
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
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            e.printStackTrace();

            exception += "Bytecode Viewer Version: " + VERSION + nl + nl + sw;
        }

        return "Smali Disassembler error! Send the stacktrace to Konloch at https://the.bytecode.club or konloch@gmail.com"
                + nl + nl + "Suggested Fix: Click refresh class, if it fails again try another decompiler."
                + nl + nl + exception;
    }

    @Override
    public String decompileClassNode(ClassNode cn, byte[] b) {
        return null;
    }

    @Override
    public void decompileToZip(String sourceJar, String zipName) {

    }
}
