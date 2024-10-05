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

import com.konloch.disklib.DiskReader;
import jadx.api.JadxArgs;
import jadx.api.JadxDecompiler;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.Settings;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.decompilers.AbstractDecompiler;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.util.ExceptionUtils;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;
import the.bytecode.club.bytecodeviewer.util.TempFile;

import java.io.*;

import static the.bytecode.club.bytecodeviewer.Constants.*;
import static the.bytecode.club.bytecodeviewer.translation.TranslatedStrings.*;

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
        TempFile tempFile = null;
        String exception;

        try
        {
            //create the temporary files
            tempFile = TempFile.createTemporaryFile(true, ".class");
            File tempDirectory = tempFile.getParent();
            File tempClassFile = tempFile.getFile();

            //write the class-file with bytes
            try (FileOutputStream fos = new FileOutputStream(tempClassFile))
            {
                fos.write(bytes);
            }

            //setup JADX Args
            JadxArgs args = new JadxArgs();
            args.setInputFile(tempClassFile);
            args.setOutDir(tempDirectory);
            args.setOutDirSrc(tempDirectory);
            args.setOutDirRes(tempDirectory);

            //init jadx decompiler
            JadxDecompiler jadx = new JadxDecompiler(args);

            //load jadx
            jadx.load();

            //decompile
            jadx.saveSources();

            //handle simulated errors
            if(Constants.DEV_FLAG_DECOMPILERS_SIMULATED_ERRORS)
                throw new RuntimeException(DEV_MODE_SIMULATED_ERROR.toString());

            return searchForJavaFile(MiscUtils.listFiles(tempDirectory));
        }
        catch (Throwable e)
        {
            exception = ExceptionUtils.exceptionToString(e);
        }
        finally
        {
            //cleanup temp files
            if(tempFile != null)
                tempFile.cleanup();
        }

        return JADX + " " + ERROR + "! " + ExceptionUI.SEND_STACKTRACE_TO + NL + NL
            + TranslatedStrings.SUGGESTED_FIX_DECOMPILER_ERROR + NL + NL + exception;
    }

    public String searchForJavaFile(File[] files) throws Exception
    {
        for (File file : files)
        {
            if (file.isDirectory())
                return searchForJavaFile(MiscUtils.listFiles(file));
            else if(file.getName().toLowerCase().endsWith(".java"))
            {
                String contents = DiskReader.readString(file.getAbsolutePath());

                //cleanup
                if(Settings.DECOMPILERS_AUTOMATICALLY_CLEANUP)
                    file.delete();

                return contents;
            }
        }

        return JADX + " " + ERROR + "! " + ExceptionUI.SEND_STACKTRACE_TO + NL + NL
            + TranslatedStrings.SUGGESTED_FIX_DECOMPILER_ERROR + NL + NL
            + "JADX failed to produce any Java files from the provided source.";
    }

    @Override
    public void decompileToZip(String sourceJar, String zipName)
    {
        //TODO
    }
}
