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

import jadx.api.JadxArgs;
import jadx.api.JadxDecompiler;
import me.konloch.kontainer.io.DiskReader;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.decompilers.AbstractDecompiler;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import java.io.*;

import static the.bytecode.club.bytecodeviewer.Constants.*;
import static the.bytecode.club.bytecodeviewer.translation.TranslatedStrings.ERROR;
import static the.bytecode.club.bytecodeviewer.translation.TranslatedStrings.JADX;

/**
 * JADX Java Wrapper
 *
 * @author Konloch
 */
public class JADXDecompiler extends AbstractDecompiler
{
    public JADXDecompiler()
    {
        super("JADX Decompiler", "jadx");
    }

    @Override
    public String decompileClassNode(ClassNode cn, byte[] bytes)
    {
        String fileStart = TEMP_DIRECTORY + FS;

        String exception = "";
        final File tempClass = new File(MiscUtils.getUniqueNameBroken(fileStart, ".class") + ".class");

        try (FileOutputStream fos = new FileOutputStream(tempClass))
        {
            fos.write(bytes);
        }
        catch (IOException e)
        {
            BytecodeViewer.handleException(e);
        }

        File freeDirectory = new File(findUnusedFile(fileStart));
        freeDirectory.mkdirs();

        try
        {
            JadxArgs args = new JadxArgs();
            args.setInputFile(tempClass);
            args.setOutDir(freeDirectory);
            args.setOutDirSrc(freeDirectory);
            args.setOutDirRes(freeDirectory);

            JadxDecompiler jadx = new JadxDecompiler(args);
            jadx.load();
            jadx.saveSources();
        }
        catch (StackOverflowError | Exception e)
        {
            StringWriter exceptionWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(exceptionWriter));
            e.printStackTrace();
            exception = exceptionWriter.toString();
        }

        tempClass.delete();

        if (freeDirectory.exists())
            return findFile(MiscUtils.listFiles(freeDirectory));

        if (exception.isEmpty())
            exception = "Decompiled source file not found!";

        return JADX + " " + ERROR + "! " + ExceptionUI.SEND_STACKTRACE_TO + NL + NL + TranslatedStrings.SUGGESTED_FIX_DECOMPILER_ERROR + NL + NL + exception;
    }

    public String findUnusedFile(String start)
    {
        long index = 0;

        while (true)
        {
            File f = new File(start + index);

            if (!f.exists())
                return f.toString();
        }
    }

    public String findFile(File[] fileArray)
    {
        for (File f : fileArray)
        {
            if (f.isDirectory())
                return findFile(MiscUtils.listFiles(f));
            else
            {
                String s;

                try
                {
                    s = DiskReader.loadAsString(f.getAbsolutePath());
                }
                catch (Exception e)
                {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    e.printStackTrace();
                    String exception = ExceptionUI.SEND_STACKTRACE_TO_NL + sw;

                    return JADX + " " + ERROR + "! " + ExceptionUI.SEND_STACKTRACE_TO + NL + NL + TranslatedStrings.SUGGESTED_FIX_DECOMPILER_ERROR + NL + NL + exception;
                }

                return s;
            }
        }

        return "JADX error!" + NL + NL + TranslatedStrings.SUGGESTED_FIX_DECOMPILER_ERROR;
    }

    @Override
    public void decompileToZip(String sourceJar, String zipName)
    {
        //TODO
    }
}
