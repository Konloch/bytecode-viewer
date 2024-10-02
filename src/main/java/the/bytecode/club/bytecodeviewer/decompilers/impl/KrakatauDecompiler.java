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

import me.konloch.kontainer.io.DiskReader;
import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.decompilers.AbstractDecompiler;
import the.bytecode.club.bytecodeviewer.resources.ExternalResources;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.util.JarUtils;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;
import the.bytecode.club.bytecodeviewer.util.ZipUtils;

import java.io.*;
import java.util.Arrays;
import java.util.stream.Collectors;

import static the.bytecode.club.bytecodeviewer.Constants.*;

/**
 * Krakatau Java Decompiler Wrapper, requires Python 2.7
 *
 * @author Konloch
 */

public class KrakatauDecompiler extends AbstractDecompiler
{
    public String buildCLIArguments()
    {
        if (Configuration.library.isEmpty())
            return "";

        File dir = new File(Configuration.library);
        if (!dir.exists())
            return "";
        if (!dir.isDirectory())
            return ";" + Configuration.library;

        File[] files = dir.listFiles();
        if (files == null || files.length == 0)
            return "";

        return ";" + Arrays.stream(files).filter(File::isFile).map(File::getAbsolutePath).collect(Collectors.joining(";"));
    }

    @Override
    public String decompileClassNode(ClassNode cn, byte[] bytes)
    {
        if (!ExternalResources.getSingleton().hasSetPython2Command())
            return TranslatedStrings.YOU_NEED_TO_SET_YOUR_PYTHON_2_PATH.toString();

        ExternalResources.getSingleton().rtCheck();

        if (Configuration.rt.isEmpty())
        {
            BytecodeViewer.showMessage(TranslatedStrings.YOU_NEED_TO_SET_YOUR_JAVA_RT_PATH_A + "\r\n" + TranslatedStrings.YOU_NEED_TO_SET_YOUR_JAVA_RT_PATH_B);
            ExternalResources.getSingleton().selectJRERTLibrary();
        }

        if (Configuration.rt.isEmpty())
        {
            BytecodeViewer.showMessage(TranslatedStrings.YOU_NEED_TO_SET_YOUR_JAVA_RT_PATH_A + "\r\n" + TranslatedStrings.YOU_NEED_TO_SET_YOUR_JAVA_RT_PATH_B);
            return TranslatedStrings.YOU_NEED_TO_SET_YOUR_JAVA_RT_PATH_A + " " + TranslatedStrings.YOU_NEED_TO_SET_YOUR_JAVA_RT_PATH_B;
        }

        final File tempDirectory = new File(Constants.TEMP_DIRECTORY + FS + MiscUtils.randomString(32) + FS);
        final File tempJar = new File(Constants.TEMP_DIRECTORY + FS + "temp" + MiscUtils.randomString(32) + ".jar");

        tempDirectory.mkdir();

        JarUtils.saveAsJarClassesOnly(BytecodeViewer.getLoadedClasses(), tempJar.getAbsolutePath());

        return decompileClassNode(tempJar, tempDirectory, cn);
    }

    public String decompileClassNode(File tempJar, File tempDir, ClassNode cn)
    {
        if (!ExternalResources.getSingleton().hasSetPython2Command())
            return TranslatedStrings.YOU_NEED_TO_SET_YOUR_PYTHON_2_PATH.toString();

        ExternalResources.getSingleton().rtCheck();

        if (Configuration.rt.isEmpty())
        {
            BytecodeViewer.showMessage(TranslatedStrings.YOU_NEED_TO_SET_YOUR_JAVA_RT_PATH_A + "\r\n" + TranslatedStrings.YOU_NEED_TO_SET_YOUR_JAVA_RT_PATH_B);
            ExternalResources.getSingleton().selectJRERTLibrary();
        }

        if (Configuration.rt.isEmpty())
        {
            BytecodeViewer.showMessage(TranslatedStrings.YOU_NEED_TO_SET_YOUR_JAVA_RT_PATH_A + "\r\n" + TranslatedStrings.YOU_NEED_TO_SET_YOUR_JAVA_RT_PATH_B);
            return TranslatedStrings.YOU_NEED_TO_SET_YOUR_JAVA_RT_PATH_A + " " + TranslatedStrings.YOU_NEED_TO_SET_YOUR_JAVA_RT_PATH_B;
        }

        String returnString = ExceptionUI.SEND_STACKTRACE_TO_NL;

        try
        {
            String[] pythonCommands = new String[]{Configuration.python2};
            if (Configuration.python2Extra)
                pythonCommands = ArrayUtils.addAll(pythonCommands, "-2");

            ProcessBuilder pb = new ProcessBuilder(ArrayUtils.addAll(pythonCommands, "-O", //love you storyyeller <3
                krakatauWorkingDirectory + FS + "decompile.py", "-skip", //love you storyyeller <3
                "-nauto", "-path", Configuration.rt + ";" + tempJar.getAbsolutePath() + buildCLIArguments(),
                "-out", tempDir.getAbsolutePath(), cn.name + ".class"));

            Process process = pb.start();
            BytecodeViewer.createdProcesses.add(process);

            StringBuilder log = new StringBuilder(TranslatedStrings.PROCESS2 + NL + NL);

            //Read out dir output
            try (InputStream is = process.getInputStream();
                 InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader br = new BufferedReader(isr))
            {
                String line;
                while ((line = br.readLine()) != null)
                {
                    log.append(NL).append(line);
                }
            }

            log.append(NL).append(NL).append(TranslatedStrings.ERROR2).append(NL).append(NL);

            try (InputStream is = process.getErrorStream();
                 InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader br = new BufferedReader(isr))
            {
                String line;
                while ((line = br.readLine()) != null)
                {
                    log.append(NL).append(line);
                }
            }

            int exitValue = process.waitFor();
            log.append(NL).append(NL).append(TranslatedStrings.EXIT_VALUE_IS).append(" ").append(exitValue);
            returnString = log.toString();

            // update the string on a successful disassemble
            returnString = DiskReader.loadAsString(tempDir.getAbsolutePath() + FS + cn.name + ".java");
        }
        catch (Exception e)
        {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            e.printStackTrace();
            returnString += NL + ExceptionUI.SEND_STACKTRACE_TO_NL + sw;
        }

        return returnString;
    }

    @Override
    public void decompileToZip(String sourceJar, String zipName)
    {
        if (!ExternalResources.getSingleton().hasSetPython2Command())
            return;

        ExternalResources.getSingleton().rtCheck();

        if (Configuration.rt.isEmpty())
        {
            BytecodeViewer.showMessage(TranslatedStrings.YOU_NEED_TO_SET_YOUR_JAVA_RT_PATH_A + "\r\n" + TranslatedStrings.YOU_NEED_TO_SET_YOUR_JAVA_RT_PATH_B);
            ExternalResources.getSingleton().selectJRERTLibrary();
        }

        final String ran = MiscUtils.randomString(32);
        final File tempDirectory = new File(Constants.TEMP_DIRECTORY + FS + ran + FS);
        final File tempJar = new File(sourceJar);

        tempDirectory.mkdir();

        try
        {
            String[] pythonCommands = new String[]{Configuration.python2};
            if (Configuration.python2Extra)
                pythonCommands = ArrayUtils.addAll(pythonCommands, "-2");

            ProcessBuilder pb = new ProcessBuilder(ArrayUtils.addAll(pythonCommands, "-O", //love you storyyeller <3
                krakatauWorkingDirectory + FS + "decompile.py", "-skip", //love you storyyeller <3
                "-nauto", "-path", Configuration.rt + ";" + tempJar.getAbsolutePath(), "-out", tempDirectory.getAbsolutePath(), tempJar.getAbsolutePath()));

            Process process = pb.start();
            BytecodeViewer.createdProcesses.add(process);
            process.waitFor();
            MiscUtils.printProcess(process);

            ZipUtils.zipFolder(tempDirectory.getAbsolutePath(), zipName, ran);
        }
        catch (Exception e)
        {
            BytecodeViewer.handleException(e);
        }
    }
}
