package the.bytecode.club.bytecodeviewer.decompilers.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import me.konloch.kontainer.io.DiskReader;
import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.decompilers.InternalDecompiler;
import the.bytecode.club.bytecodeviewer.resources.ExternalResources;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.util.JarUtils;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;
import the.bytecode.club.bytecodeviewer.util.ZipUtils;

import static the.bytecode.club.bytecodeviewer.Constants.fs;
import static the.bytecode.club.bytecodeviewer.Constants.krakatauWorkingDirectory;
import static the.bytecode.club.bytecodeviewer.Constants.nl;

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
 * Krakatau Java Disassembler Wrapper, requires Python 2.7
 *
 * @author Konloch
 */

public class KrakatauDisassembler extends InternalDecompiler
{
    @Override
    public String decompileClassNode(ClassNode cn, byte[] b) {
        if(!ExternalResources.getSingleton().hasSetPython2Command())
            return TranslatedStrings.YOU_NEED_TO_SET_YOUR_PYTHON_2_PATH.toString();

        String s = ExceptionUI.SEND_STACKTRACE_TO_NL;

        final File tempDirectory = new File(Constants.tempDirectory + fs + MiscUtils.randomString(32) + fs);
        tempDirectory.mkdir();
        final File tempJar = new File(Constants.tempDirectory + fs + "temp" + MiscUtils.randomString(32) + ".jar");
        JarUtils.saveAsJarClassesOnly(BytecodeViewer.getLoadedClasses(), tempJar.getAbsolutePath());

        try {
            String[] pythonCommands = new String[]{Configuration.python2};
            if(Configuration.python2Extra)
                pythonCommands = ArrayUtils.addAll(pythonCommands, "-2");
            
            ProcessBuilder pb = new ProcessBuilder(ArrayUtils.addAll(
                    pythonCommands,
                    "-O", //love you storyyeller <3
                    krakatauWorkingDirectory + fs + "disassemble.py",
                    "-path",
                    tempJar.getAbsolutePath(),
                    "-out",
                    tempDirectory.getAbsolutePath(),
                    cn.name + ".class"
            ));

            Process process = pb.start();
            BytecodeViewer.createdProcesses.add(process);

            StringBuilder log = new StringBuilder(TranslatedStrings.PROCESS2 + nl + nl);

            //Read out dir output
            try (InputStream is = process.getInputStream();
                 InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader br = new BufferedReader(isr)) {
                String line;
                while ((line = br.readLine()) != null) {
                    log.append(nl).append(line);
                }
            }

            log.append(nl).append(nl).append(TranslatedStrings.ERROR2).append(nl).append(nl);

            try (InputStream is = process.getErrorStream();
                 InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader br = new BufferedReader(isr)) {
                String line;
                while ((line = br.readLine()) != null) {
                    log.append(nl).append(line);
                }
            }

            int exitValue = process.waitFor();
            log.append(nl).append(nl).append(TranslatedStrings.EXIT_VALUE_IS).append(" ").append(exitValue);
            s = log.toString();

            // if the motherfucker failed this'll fail, aka won't set.
            s = DiskReader.loadAsString(tempDirectory.getAbsolutePath() + fs + cn.name + ".j");
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            e.printStackTrace();
            s += nl + ExceptionUI.SEND_STACKTRACE_TO_NL + sw;
        }
        return s;
    }

    @Override
    public void decompileToZip(String sourceJar, String zipName) {
        if(!ExternalResources.getSingleton().hasSetPython2Command())
            return;

        String ran = MiscUtils.randomString(32);
        final File tempDirectory = new File(Constants.tempDirectory + fs + ran + fs);
        tempDirectory.mkdir();

        final File tempJar = new File(sourceJar);

        try {
            String[] pythonCommands = new String[]{Configuration.python2};
            if(Configuration.python2Extra)
                pythonCommands = ArrayUtils.addAll(pythonCommands, "-2");
            
            ProcessBuilder pb = new ProcessBuilder(ArrayUtils.addAll(
                    pythonCommands,
                    "-O", //love you storyyeller <3
                    krakatauWorkingDirectory + fs + "disassemble.py",
                    "-path",
                    Configuration.rt + ";" + tempJar.getAbsolutePath(),
                    "-out",
                    tempDirectory.getAbsolutePath(),
                    tempJar.getAbsolutePath()
            ));

            Process process = pb.start();
            BytecodeViewer.createdProcesses.add(process);
            process.waitFor();

            ZipUtils.zipFolder(tempDirectory.getAbsolutePath(), zipName, ran);
        } catch (Exception e) {
            BytecodeViewer.handleException(e);
        }
    }
}
