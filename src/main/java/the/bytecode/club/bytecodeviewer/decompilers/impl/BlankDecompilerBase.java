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
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.decompilers.AbstractDecompiler;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.util.ExceptionUtils;
import the.bytecode.club.bytecodeviewer.util.TempFile;

import java.io.File;
import java.io.FileOutputStream;

import static the.bytecode.club.bytecodeviewer.Constants.*;
import static the.bytecode.club.bytecodeviewer.translation.TranslatedStrings.*;

/**
 * This is an unused class. This is meant to act as a blank decompiler template to aid in developers creating new decompilers / disassemblers.
 *
 * @author Konloch
 * @since 10/02/2024
 */
public class BlankDecompilerBase extends AbstractDecompiler
{
    public BlankDecompilerBase()
    {
        super("[Your] Decompiler", "yourdecompiler");
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
            File tempInputClassFile = tempFile.getFile();
            File tempOutputJavaFile = tempFile.createFileFromExtension(false, true, ".java");

            //write the class-file with bytes
            try (FileOutputStream fos = new FileOutputStream(tempInputClassFile))
            {
                fos.write(bytes);
            }

            //decompile the class-file
            //TODO this is where you would call your decompiler api
            //  such as YourDecompiler.decompile(tempClassFile, tempOutputJavaFile);

            //handle simulated errors
            if(Constants.DEV_FLAG_DECOMPILERS_SIMULATED_ERRORS)
                throw new RuntimeException(DEV_MODE_SIMULATED_ERROR.toString());

            //if the output file is found, read it
            if (tempOutputJavaFile.exists())
                return DiskReader.readString(tempOutputJavaFile.getAbsolutePath());
            else
                exception = getDecompilerName() + " " + ERROR + "! " + tempOutputJavaFile.getAbsolutePath() + " does not exist.";
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

        return getDecompilerName() + " " + ERROR + "! " + ExceptionUI.SEND_STACKTRACE_TO + NL + NL
            + TranslatedStrings.SUGGESTED_FIX_DECOMPILER_ERROR + NL + NL + exception;
    }

    @Override
    public void decompileToZip(String sourceJar, String zipName)
    {
        decompileToZipFallBack(sourceJar, zipName);
    }
}
