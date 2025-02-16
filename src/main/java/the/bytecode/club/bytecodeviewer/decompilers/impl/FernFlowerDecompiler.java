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
import org.objectweb.asm.tree.InnerClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.api.ASMUtil;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.decompilers.AbstractDecompiler;
import the.bytecode.club.bytecodeviewer.resources.ExternalResources;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.util.ExceptionUtils;
import the.bytecode.club.bytecodeviewer.util.ProcessUtils;
import the.bytecode.club.bytecodeviewer.util.TempFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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

    private String[] inners;
    private final List<File> innerFiles = new ArrayList<>();

    @Override
    public String decompileClassNode(ClassNode cn, byte[] bytes)
    {
        TempFile tempFile = null;
        String exception;

        List<InnerClassNode> innerClasses = cn.innerClasses;
        List<TempFile> innerTempFiles = new ArrayList<>();
        AtomicReference<TempFile> innerTempFile = new AtomicReference<>();
        if (BytecodeViewer.viewer.din.isSelected())
        {
            inners = new String[innerClasses.size()];
            for (int i = 0; i < innerClasses.size(); i++)
            {
                if (innerClasses.get(i).outerName != null && innerClasses.get(i).outerName.equals(cn.name))
                {
                    inners[i] = innerClasses.get(i).name;
                }
                else if (innerClasses.get(i).outerName == null)
                {
                    String name = innerClasses.get(i).name;
                    name = name.substring(name.lastIndexOf('/') + 1);
                    if (name.contains(cn.name.substring(cn.name.lastIndexOf('/') + 1)))
                    {
                        inners[i] = innerClasses.get(i).name;
                    }
                }
            }

            for (ResourceContainer container : BytecodeViewer.resourceContainers.values())
            {
                container.resourceClasses.forEach((s, classNode) -> {
                    for (String innerClassName : inners)
                    {
                        if (s.equals(innerClassName))
                        {
                            innerTempFile.set(TempFile.createTemporaryFile(true, ".class"));
                            File tempInputClassFile2 = innerTempFile.get().getFile();
                            try (FileOutputStream fos = new FileOutputStream(tempInputClassFile2))
                            {
                                fos.write(ASMUtil.nodeToBytes(classNode));
                            }
                            catch (IOException e)
                            {
                                throw new RuntimeException(e);
                            }
                            finally
                            {
                                innerFiles.add(tempInputClassFile2);
                                innerTempFile.get().markAsCreatedFile(tempInputClassFile2);
                                innerTempFiles.add(innerTempFile.get());
                            }
                        }
                    }
                });
            }
        }

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
                List<String> strings = generate(tempInputClassFile.getAbsolutePath(),
                    new File(TEMP_DIRECTORY).getAbsolutePath());

                String[] args = strings.toArray(new String[0]);

                org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler.main(args);
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
            if (tempFile != null)
                tempFile.cleanup();

            if (innerTempFile.get() != null)
                innerTempFile.get().cleanup();

            for (TempFile file : innerTempFiles)
            {
                file.cleanup();
                File file1 = new File(TEMP_DIRECTORY + file.getUniqueName() + ".java");
                if (file1.exists())
                {
                    file1.delete();
                }
            }

            innerFiles.clear();
        }

        return FERNFLOWER + " " + ERROR + "! " + ExceptionUI.SEND_STACKTRACE_TO + NL + NL
            + TranslatedStrings.SUGGESTED_FIX_DECOMPILER_ERROR + NL + NL + exception;
    }

    @Override
    public void decompileToZip(String sourceJar, String zipName)
    {
        final File destination = new File(zipName);
        File tempInputJarFile = new File(sourceJar);
        File tempOutputJar = new File(TEMP_DIRECTORY + "temp" + FS + tempInputJarFile.getName());
        tempOutputJar.getParentFile().mkdirs();

        try
        {
            ConsoleDecompiler.main(generateMainMethod(tempInputJarFile.getAbsolutePath(), TEMP_DIRECTORY + "temp"));
        }
        catch (StackOverflowError | Exception ignored)
        {
        }

        if (tempOutputJar.exists())
            tempOutputJar.renameTo(destination);
        else //attempt to decompile using fallback
            decompileToZipFallBack(tempInputJarFile.getAbsolutePath(), destination.getAbsolutePath());

    }

    private List<String> generate(String className, String folder)
    {
        List<String> strings = new ArrayList<>();
        strings.add("-rbr=" + ffOnValue(BytecodeViewer.viewer.rbr.isSelected()));
        strings.add("-rsy=" + ffOnValue(BytecodeViewer.viewer.rsy.isSelected()));
        strings.add("-din=" + ffOnValue(BytecodeViewer.viewer.din.isSelected()));
        strings.add("-dc4=" + ffOnValue(BytecodeViewer.viewer.dc4.isSelected()));
        strings.add("-das=" + ffOnValue(BytecodeViewer.viewer.das.isSelected()));
        strings.add("-hes=" + ffOnValue(BytecodeViewer.viewer.hes.isSelected()));
        strings.add("-hdc=" + ffOnValue(BytecodeViewer.viewer.hdc.isSelected()));
        strings.add("-dgs=" + ffOnValue(BytecodeViewer.viewer.dgs.isSelected()));
        strings.add("-ner=" + ffOnValue(BytecodeViewer.viewer.ner.isSelected()));
        strings.add("-den=" + ffOnValue(BytecodeViewer.viewer.den.isSelected()));
        strings.add("-rgn=" + ffOnValue(BytecodeViewer.viewer.rgn.isSelected()));
        strings.add("-bto=" + ffOnValue(BytecodeViewer.viewer.bto.isSelected()));
        strings.add("-nns=" + ffOnValue(BytecodeViewer.viewer.nns.isSelected()));
        strings.add("-uto=" + ffOnValue(BytecodeViewer.viewer.uto.isSelected()));
        strings.add("-udv=" + ffOnValue(BytecodeViewer.viewer.udv.isSelected()));
        strings.add("-rer=" + ffOnValue(BytecodeViewer.viewer.rer.isSelected()));
        strings.add("-fdi=" + ffOnValue(BytecodeViewer.viewer.fdi.isSelected()));
        strings.add("-asc=" + ffOnValue(BytecodeViewer.viewer.asc.isSelected()));
        strings.add("-ren=" + ffOnValue(BytecodeViewer.viewer.ren.isSelected()));
        strings.add(className);
        if (BytecodeViewer.viewer.din.isSelected())
        {
            for (File file : innerFiles)
                strings.add(file.getAbsolutePath());
        }

        strings.add(folder);
        return strings;
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
