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
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.decompilers.AbstractDecompiler;
import the.bytecode.club.bytecodeviewer.resources.ExternalResources;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.util.ExceptionUtils;
import the.bytecode.club.bytecodeviewer.util.ProcessUtils;
import the.bytecode.club.bytecodeviewer.util.TempFile;

import java.io.*;

import static the.bytecode.club.bytecodeviewer.Constants.*;
import static the.bytecode.club.bytecodeviewer.translation.TranslatedStrings.*;

/**
 * A FernFlower wrapper with all the options (except 2)
 *
 * @author Konloch
 * @author WaterWolf
 * @since 09/26/2011
 */
public class FernFlowerDecompiler extends AbstractDecompiler
{
    public FernFlowerDecompiler()
    {
        super("FernFlower Decompiler", "fernflower");
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

            //load java source from temp directory
            tempFile.setParent(new File(TEMP_DIRECTORY));
            File tempOutputJavaFile = tempFile.createFileFromExtension(false, true, ".java");

            //write the class-file with bytes
            try (FileOutputStream fos = new FileOutputStream(tempInputClassFile))
            {
                fos.write(bytes);
            }

            //decompile the class-file
            if (LAUNCH_DECOMPILERS_IN_NEW_PROCESS)
            {
                ProcessUtils.runDecompilerExternal(ArrayUtils.addAll(new String[]
                    {
                        ExternalResources.getSingleton().getJavaCommand(true),
                        "-jar", ExternalResources.getSingleton().findLibrary("fernflower")
                    }, generateMainMethod(tempInputClassFile.getAbsolutePath(), tempFile.getParent().getAbsolutePath())
                ), false);
            }
            else
            {
                org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler.main(generateMainMethod(tempInputClassFile.getAbsolutePath(), new File(TEMP_DIRECTORY).getAbsolutePath()));
            }

            //if rename is enabled the file name will be the actual class name
            if (BytecodeViewer.viewer.ren.isSelected())
            {
                int indexOfLastPackage = cn.name.lastIndexOf('/');
                String classNameNoPackages = indexOfLastPackage < 0 ? cn.name : cn.name.substring(indexOfLastPackage);
                tempOutputJavaFile = new File(tempFile.getParent(), classNameNoPackages + ".java");
                tempFile.markAsCreatedFile(tempOutputJavaFile);
            }

            //if the output file is found, read it
            if (tempOutputJavaFile.exists() && !Constants.DEV_FLAG_DECOMPILERS_SIMULATED_ERRORS)
                return DiskReader.readString(tempOutputJavaFile.getAbsolutePath());
            else
                exception = FERNFLOWER + " " + ERROR + "! " + tempOutputJavaFile.getAbsolutePath() + " does not exist.";
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

        return FERNFLOWER + " " + ERROR + "! " + ExceptionUI.SEND_STACKTRACE_TO + NL + NL
            + TranslatedStrings.SUGGESTED_FIX_DECOMPILER_ERROR + NL + NL + exception;
    }

    @Override
    public void decompileToZip(String sourceJar, String zipName)
    {
        final File destination = new File(zipName);
        File tempInputJarFile = new File(sourceJar);
        File tempOutputJar = new File(TEMP_DIRECTORY + FS + "temp" + FS + tempInputJarFile.getName());

        try
        {
            ConsoleDecompiler.main(generateMainMethod(tempInputJarFile.getAbsolutePath(), TEMP_DIRECTORY + "./temp/"));
        }
        catch (StackOverflowError | Exception ignored)
        {
        }

        if (tempOutputJar.exists())
            tempOutputJar.renameTo(destination);
        else //attempt to decompile using fallback
            decompileToZipFallBack(tempInputJarFile.getAbsolutePath(), destination.getAbsolutePath());

    }

    private String[] generateMainMethod(String className, String folder)
    {
        return new String[]
        {
            "-rbr=" + ffOnValue(BytecodeViewer.viewer.rbr.isSelected()),
            "-rsy=" + ffOnValue(BytecodeViewer.viewer.rsy.isSelected()),
            "-din=" + ffOnValue(BytecodeViewer.viewer.din.isSelected()),
            "-dc4=" + ffOnValue(BytecodeViewer.viewer.dc4.isSelected()),
            "-das=" + ffOnValue(BytecodeViewer.viewer.das.isSelected()),
            "-hes=" + ffOnValue(BytecodeViewer.viewer.hes.isSelected()),
            "-hdc=" + ffOnValue(BytecodeViewer.viewer.hdc.isSelected()),
            "-dgs=" + ffOnValue(BytecodeViewer.viewer.dgs.isSelected()),
            "-ner=" + ffOnValue(BytecodeViewer.viewer.ner.isSelected()),
            "-den=" + ffOnValue(BytecodeViewer.viewer.den.isSelected()),
            "-rgn=" + ffOnValue(BytecodeViewer.viewer.rgn.isSelected()),
            "-bto=" + ffOnValue(BytecodeViewer.viewer.bto.isSelected()),
            "-nns=" + ffOnValue(BytecodeViewer.viewer.nns.isSelected()),
            "-uto=" + ffOnValue(BytecodeViewer.viewer.uto.isSelected()),
            "-udv=" + ffOnValue(BytecodeViewer.viewer.udv.isSelected()),
            "-rer=" + ffOnValue(BytecodeViewer.viewer.rer.isSelected()),
            "-fdi=" + ffOnValue(BytecodeViewer.viewer.fdi.isSelected()),
            "-asc=" + ffOnValue(BytecodeViewer.viewer.asc.isSelected()),
            "-ren=" + ffOnValue(BytecodeViewer.viewer.ren.isSelected()),
            className, folder
        };
    }

    private String ffOnValue(boolean b)
    {
        if (b)
            return "1";
        else
            return "0";
    }
}
