package the.bytecode.club.bytecodeviewer.compilers.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import me.konloch.kontainer.io.DiskWriter;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.compilers.InternalCompiler;
import the.bytecode.club.bytecodeviewer.resources.ExternalResources;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.util.JarUtils;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import static the.bytecode.club.bytecodeviewer.Constants.fs;
import static the.bytecode.club.bytecodeviewer.Constants.nl;
import static the.bytecode.club.bytecodeviewer.Constants.tempDirectory;

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
 * Java Compiler
 *
 * @author Konloch
 */

public class JavaCompiler extends InternalCompiler
{
    @Override
    public byte[] compile(String contents, String fullyQualifiedName)
    {
        String fileStart = tempDirectory + fs + "temp" + MiscUtils.randomString(12) + fs;
        String fileStart2 = tempDirectory + fs + "temp" + MiscUtils.randomString(12) + fs;
        File java = new File(fileStart + fs + fullyQualifiedName + ".java");
        File clazz = new File(fileStart2 + fs + fullyQualifiedName + ".class");
        File cp = new File(tempDirectory + fs + "cpath_" + MiscUtils.randomString(12) + ".jar");
        File tempD = new File(fileStart + fs + fullyQualifiedName.substring(0, fullyQualifiedName.length() - fullyQualifiedName.split("/")[fullyQualifiedName.split("/").length - 1].length()));
        
        tempD.mkdirs();
        new File(fileStart2).mkdirs();

        if (Configuration.javac.isEmpty() || !new File(Configuration.javac).exists()) {
            BytecodeViewer.showMessage("You need to set your Javac path, this requires the JDK to be downloaded." + nl + "(C:/Program Files/Java/JDK_xx/bin/javac.exe)");
            ExternalResources.getSingleton().selectJavac();
        }

        if (Configuration.javac.isEmpty() || !new File(Configuration.javac).exists()) {
            BytecodeViewer.showMessage("You need to set Javac!");
            return null;
        }

        DiskWriter.replaceFile(java.getAbsolutePath(), contents, false);
        JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), cp.getAbsolutePath());

        boolean cont = true;
        try {
            StringBuilder log = new StringBuilder();
            ProcessBuilder pb;

            if (Configuration.library.isEmpty()) {
                pb = new ProcessBuilder(
                        Configuration.javac,
                        "-d", fileStart2,
                        "-classpath", cp.getAbsolutePath(),
                        java.getAbsolutePath()
                );
            } else {
                pb = new ProcessBuilder(
                        Configuration.javac,
                        "-d", fileStart2,
                        "-classpath",
                        cp.getAbsolutePath() + System.getProperty("path.separator") + Configuration.library,
                        java.getAbsolutePath()
                );
            }

            Process process = pb.start();
            BytecodeViewer.createdProcesses.add(process);

            Thread failSafe = new Thread(() -> {
                long started = System.currentTimeMillis();
                while (System.currentTimeMillis() - started <= 10_000) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (process.isAlive())
                {
                    System.out.println("Force killing javac process, assuming it's gotten stuck");
                    process.destroyForcibly().destroy();
                }
            }, "Javac Fail-Safe");
            failSafe.start();

            int exitValue = process.waitFor();

            //Read out dir output
            try (InputStream is = process.getInputStream();
                 InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader br = new BufferedReader(isr)) {
                String line;
                while ((line = br.readLine()) != null)
                    log.append(nl).append(line);
            }

            log.append(nl).append(nl).append(TranslatedStrings.ERROR2).append(nl).append(nl);
            try (InputStream is = process.getErrorStream();
                 InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader br = new BufferedReader(isr)) {
                String line;
                while ((line = br.readLine()) != null)
                    log.append(nl).append(line);
            }

            log.append(nl).append(nl).append(TranslatedStrings.EXIT_VALUE_IS).append(" ").append(exitValue);
            System.out.println(log);

            if (!clazz.exists())
                throw new Exception(log.toString());
        } catch (Exception e) {
            cont = false;
            e.printStackTrace();
        }

        cp.delete();

        if (cont)
            try {
                return org.apache.commons.io.FileUtils.readFileToByteArray(clazz);
            } catch (IOException e) {
                e.printStackTrace();
                //BytecodeViewer.handleException(e);
            }

        return null;
    }
}
