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

package the.bytecode.club.bytecodeviewer.decompilers.impl;

import com.googlecode.d2j.smali.BaksmaliCmd;
import com.konloch.disklib.DiskReader;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.decompilers.AbstractDecompiler;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.util.Dex2Jar;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import java.io.*;
import java.util.Objects;

import static the.bytecode.club.bytecodeviewer.Constants.*;
import static the.bytecode.club.bytecodeviewer.translation.TranslatedStrings.*;

/**
 * Smali Disassembler Wrapper
 *
 * @author Konloch
 */

public class SmaliDisassembler extends AbstractDecompiler
{
    public SmaliDisassembler()
    {
        super("Smali Disassembler", "smali");
    }

    @Override
    public String decompileClassNode(ClassNode cn, byte[] bytes)
    {
        final String fileStart = TEMP_DIRECTORY + FS + "temp";
        final String start = MiscUtils.getUniqueNameBroken(fileStart, ".class");
        final File tempClass = new File(start + ".class");
        final File tempDex = new File(start + ".dex");
        final File tempDexOut = new File(start + "-out");
        final File tempSmali = new File(start + "-smali"); //output directory

        String exception = "";

        try (FileOutputStream fos = new FileOutputStream(tempClass))
        {
            fos.write(bytes);
        }
        catch (IOException e)
        {
            BytecodeViewer.handleException(e);
        }

        Dex2Jar.saveAsDex(tempClass, tempDex, true);

        try
        {
            BaksmaliCmd.main(tempDex.getAbsolutePath(), "-o", tempDexOut.getAbsolutePath());
        }
        catch (Exception e)
        {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            e.printStackTrace();

            exception += ExceptionUI.SEND_STACKTRACE_TO_NL + sw;
        }

        try
        {
            FileUtils.moveDirectory(tempDexOut, tempSmali);
        }
        catch (IOException e)
        {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            e.printStackTrace();

            exception += ExceptionUI.SEND_STACKTRACE_TO_NL + sw;
        }

        File outputSmali = null;

        boolean found = false;
        File current = tempSmali;
        while (!found)
        {
            File f = Objects.requireNonNull(current.listFiles())[0];

            if (f.isDirectory())
                current = f;
            else
            {
                outputSmali = f;
                found = true;
            }
        }
        try
        {
            return DiskReader.readString(outputSmali.getAbsolutePath());
        }
        catch (Exception e)
        {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            e.printStackTrace();

            exception += ExceptionUI.SEND_STACKTRACE_TO_NL + sw;
        }

        return SMALI + " " + DISASSEMBLER + " " + ERROR + "! " + ExceptionUI.SEND_STACKTRACE_TO + NL + NL
            + TranslatedStrings.SUGGESTED_FIX_DECOMPILER_ERROR + NL + NL + exception;
    }

    @Override
    public void decompileToZip(String sourceJar, String zipName)
    {

    }
}
