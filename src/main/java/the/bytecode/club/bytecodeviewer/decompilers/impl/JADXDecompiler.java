package the.bytecode.club.bytecodeviewer.decompilers.impl;

import jadx.api.JadxArgs;
import jadx.api.JadxDecompiler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;
import me.konloch.kontainer.io.DiskReader;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.decompilers.InternalDecompiler;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import static the.bytecode.club.bytecodeviewer.Constants.fs;
import static the.bytecode.club.bytecodeviewer.Constants.nl;
import static the.bytecode.club.bytecodeviewer.Constants.tempDirectory;
import static the.bytecode.club.bytecodeviewer.translation.TranslatedStrings.ERROR;
import static the.bytecode.club.bytecodeviewer.translation.TranslatedStrings.JADX;

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
 * JADX Java Wrapper
 *
 * @author Konloch
 */
public class JADXDecompiler extends InternalDecompiler
{
    private final Random r = new Random();
    
    @Override
    public String decompileClassNode(ClassNode cn, byte[] b) {
        String fileStart = tempDirectory + fs;

        String exception = "";
        final File tempClass = new File(MiscUtils.getUniqueName(fileStart, ".class") + ".class");

        try (FileOutputStream fos = new FileOutputStream(tempClass)) {
            fos.write(b);
        } catch (final IOException e) {
            BytecodeViewer.handleException(e);
        }

        File fuckery = new File(fuckery(fileStart));
        fuckery.mkdirs();
        
        try {
            JadxArgs args = new JadxArgs();
            args.setInputFile(tempClass);
            args.setOutDir(fuckery);
            args.setOutDirSrc(fuckery);
            args.setOutDirRes(fuckery);

            JadxDecompiler jadx = new JadxDecompiler(args);
            jadx.load();
            jadx.saveSources();
        } catch (StackOverflowError | Exception e) {
            StringWriter exceptionWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(exceptionWriter));
            e.printStackTrace();
            exception = exceptionWriter.toString();
        }

        tempClass.delete();

        if (fuckery.exists())
            return findFile(MiscUtils.listFiles(fuckery));
        
        if(exception.isEmpty())
            exception = "Decompiled source file not found!";

        return JADX + " " + ERROR + "! " + ExceptionUI.SEND_STACKTRACE_TO +
                nl + nl + TranslatedStrings.SUGGESTED_FIX_DECOMPILER_ERROR +
                nl + nl + exception;
    }

    //TODO remove
    public String fuckery(String start)
    {
        int failSafe = 0;
        while (failSafe++ <= 42069)
        {
            File f = new File(start + r.nextInt(Integer.MAX_VALUE));
            if (!f.exists())
                return f.toString();
        }
        
        return null;
    }

    public String findFile(File[] fA) {
        for (File f : fA) {
            if (f.isDirectory())
                return findFile(MiscUtils.listFiles(f));
            else {
                String s;
                try {
                    s = DiskReader.loadAsString(f.getAbsolutePath());
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    e.printStackTrace();
                    String exception = ExceptionUI.SEND_STACKTRACE_TO_NL + sw;
                    
                    return JADX + " " + ERROR + "! " + ExceptionUI.SEND_STACKTRACE_TO +
                            nl + nl + TranslatedStrings.SUGGESTED_FIX_DECOMPILER_ERROR +
                            nl + nl + exception;
                }
                return s;
            }
        }
        
        return "JADX error!" +
                nl + nl + TranslatedStrings.SUGGESTED_FIX_DECOMPILER_ERROR;
    }

    @Override
    public void decompileToZip(String sourceJar, String zipName) { }
}
