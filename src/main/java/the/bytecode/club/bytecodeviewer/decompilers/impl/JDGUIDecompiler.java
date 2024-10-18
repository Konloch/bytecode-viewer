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
import org.apache.commons.io.FilenameUtils;
import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InnerClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.api.ASMUtil;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.decompilers.AbstractDecompiler;
import the.bytecode.club.bytecodeviewer.decompilers.jdgui.CommonPreferences;
import the.bytecode.club.bytecodeviewer.decompilers.jdgui.DirectoryLoader;
import the.bytecode.club.bytecodeviewer.decompilers.jdgui.JDGUIClassFileUtil;
import the.bytecode.club.bytecodeviewer.decompilers.jdgui.PlainTextPrinter;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.util.ExceptionUtils;
import the.bytecode.club.bytecodeviewer.util.TempFile;

import java.io.*;
import java.util.List;

import static the.bytecode.club.bytecodeviewer.Constants.FS;
import static the.bytecode.club.bytecodeviewer.Constants.NL;
import static the.bytecode.club.bytecodeviewer.translation.TranslatedStrings.*;

/**
 * JD-Core Decompiler Wrapper
 *
 * @author Konloch
 * @author JD-Core developers
 */

public class JDGUIDecompiler extends AbstractDecompiler
{

    public JDGUIDecompiler()
    {
        super("JD-GUI Decompiler", "jdgui");
    }

    @Override
    public String decompileClassNode(ClassNode cn, byte[] bytes)
    {
        TempFile tempFile = null;
        String exception;

        List<InnerClassNode> innerClasses = cn.innerClasses;
        String[] inners = new String[innerClasses.size()];
        for (int i = 0; i < innerClasses.size(); i++)
        {
            if (innerClasses.get(i).name.equals(cn.name))
                break;

            if (innerClasses.get(i).outerName != null && innerClasses.get(i).outerName.equals(cn.name))
            {
                inners[i] = innerClasses.get(i).name;
            }
            else if (innerClasses.get(i).outerName == null)
            {
                String name = innerClasses.get(i).name;
                name = name.substring(name.lastIndexOf('/') + 1);
                if (name.contains(cn.name.substring(cn.name.lastIndexOf('/') + 1)))
                    inners[i] = innerClasses.get(i).name;
            }
        }

        try
        {
            //create the temporary files
            tempFile = TempFile.createTemporaryFile(true, ".class");
            tempFile.setUniqueName(cn.name);
            File tempClassFile = tempFile.createFileFromExtension(false, false, ".class");
            File tempJavaFile = tempFile.createFileFromExtension(false, false, ".java");

            //make any folders for the packages
            makeFolders(tempFile, cn);

            try (FileOutputStream fos = new FileOutputStream(tempClassFile))
            {
                fos.write(bytes);
            }

            // create the inner class temp files
            File innerTempFile;
            for (ResourceContainer container : BytecodeViewer.resourceContainers.values())
            {
                for (String s : container.resourceClasses.keySet())
                {
                    for (String innerClassName : inners)
                    {
                        if (s.equals(innerClassName))
                        {
                            ClassNode cn2 = container.resourceClasses.get(innerClassName);
                            tempFile.setUniqueName(cn2.name);
                            innerTempFile = tempFile.createFileFromExtension(false, false, ".class");
                            try (FileOutputStream fos = new FileOutputStream(innerTempFile))
                            {
                                fos.write(ASMUtil.nodeToBytes(cn2));
                            }
                        }
                    }
                }
            }

            String pathToClass = tempClassFile.getAbsolutePath().replace('/', File.separatorChar).replace('\\', File.separatorChar);
            String directoryPath = JDGUIClassFileUtil.ExtractDirectoryPath(pathToClass);
            String internalPath = FilenameUtils.removeExtension(JDGUIClassFileUtil.ExtractInternalPath(directoryPath,
                pathToClass));


            CommonPreferences preferences = new CommonPreferences()
            {
                @Override
                public boolean isShowLineNumbers()
                {
                    return false;
                }

                @Override
                public boolean isMergeEmptyLines()
                {
                    return true;
                }
            };

            DirectoryLoader loader = new DirectoryLoader(new File(directoryPath));

            org.jd.core.v1.api.Decompiler decompiler = new ClassFileToJavaSourceDecompiler();

            try (PrintStream ps = new PrintStream(tempJavaFile.getAbsolutePath());
                 PlainTextPrinter printer = new PlainTextPrinter(preferences, ps))
            {
                decompiler.decompile(loader, printer, internalPath, preferences.getPreferences());
            }

            //handle simulated errors
            if(Constants.DEV_FLAG_DECOMPILERS_SIMULATED_ERRORS)
                throw new RuntimeException(DEV_MODE_SIMULATED_ERROR.toString());

            //read the java file
            return DiskReader.readString(tempJavaFile.getAbsolutePath());
        }
        catch (Throwable e)
        {
            exception = ExceptionUtils.exceptionToString(e);
        }
        finally
        {
            if(tempFile != null)
                tempFile.cleanup();
        }

        return JDGUI + " " + ERROR + "! " + ExceptionUI.SEND_STACKTRACE_TO + NL + NL
            + TranslatedStrings.SUGGESTED_FIX_DECOMPILER_ERROR + NL + NL + exception;
    }

    @Override
    public void decompileToZip(String sourceJar, String zipName)
    {
        decompileToZipFallBack(sourceJar, zipName);
    }

    private void makeFolders(TempFile tempFile, ClassNode cn)
    {
        if (cn.name.contains("/"))
        {
            String[] raw = cn.name.split("/");
            String path = tempFile.getParent().getAbsolutePath() + FS;

            for (int i = 0; i < raw.length - 1; i++)
            {
                path += raw[i] + FS;
                File f = new File(path);
                f.mkdir();
            }
        }
    }
}
