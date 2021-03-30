package the.bytecode.club.bytecodeviewer.compilers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import me.konloch.kontainer.io.DiskWriter;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.util.JarUtils;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

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

public class JavaCompiler extends Compiler {

    @Override
    public byte[] compile(String contents, String name) {
        String fileStart = BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp" + MiscUtils.randomString(12) + BytecodeViewer.fs;
        String fileStart2 = BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp" + MiscUtils.randomString(12) + BytecodeViewer.fs;
        File java = new File(fileStart + BytecodeViewer.fs + name + ".java");
        File clazz = new File(fileStart2 + BytecodeViewer.fs + name + ".class");
        File cp = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + "cpath_" + MiscUtils.randomString(12) + ".jar");
        File tempD = new File(fileStart + BytecodeViewer.fs + name.substring(0, name.length() - name.split("/")[name.split("/").length - 1].length()));
        tempD.mkdirs();
        new File(fileStart2).mkdirs();

        if (BytecodeViewer.javac.equals("") || !new File(BytecodeViewer.javac).exists()) {
            BytecodeViewer.showMessage("You need to set your Javac path, this requires the JDK to be downloaded." + BytecodeViewer.nl + "(C:/programfiles/Java/JDK_xx/bin/javac.exe)");
            BytecodeViewer.viewer.javac();
        }

        if (BytecodeViewer.javac.equals("") || !new File(BytecodeViewer.javac).exists()) {
            BytecodeViewer.showMessage("You need to set Javac!");
            return null;
        }

        DiskWriter.replaceFile(java.getAbsolutePath(), contents, false);
        JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), cp.getAbsolutePath());

        boolean cont = true;
        BytecodeViewer.sm.stopBlocking();
        try {
            String log = "";
            ProcessBuilder pb;

            if (BytecodeViewer.library.isEmpty()) {
                pb = new ProcessBuilder(
                        BytecodeViewer.javac,
                        "-d", fileStart2,
                        "-classpath", cp.getAbsolutePath(),
                        java.getAbsolutePath()
                );
            } else {
                pb = new ProcessBuilder(
                        BytecodeViewer.javac,
                        "-d", fileStart2,
                        "-classpath", cp.getAbsolutePath() + System.getProperty("path.separator") + BytecodeViewer.library,
                        java.getAbsolutePath()
                );
            }

            Process process = pb.start();
            BytecodeViewer.createdProcesses.add(process);

            Thread failSafe = new Thread()
            {
                @Override
                public void run()
                {
                    long started = System.currentTimeMillis();
                    while(System.currentTimeMillis()-started <= 10_000)
                    {
                        try
                        {
                            Thread.sleep(100);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    if(process.isAlive())
                    {
                        System.out.println("Force killing javac process, assuming it's gotten stuck");
                        process.destroyForcibly().destroy();
                    }
                }
            };
            failSafe.start();

            int exitValue = process.waitFor();

            //Read out dir output
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null)
            {
                log += BytecodeViewer.nl + line;
            }
            br.close();

            log += BytecodeViewer.nl + BytecodeViewer.nl + "Error:" + BytecodeViewer.nl + BytecodeViewer.nl;
            is = process.getErrorStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                log += BytecodeViewer.nl + line;
            }
            br.close();

            log += BytecodeViewer.nl + BytecodeViewer.nl + "Exit Value is " + exitValue;
            System.out.println(log);

            if (!clazz.exists())
                throw new Exception(log);

        } catch (Exception e) {
            cont = false;
            e.printStackTrace();
        }
        BytecodeViewer.sm.setBlocking();

        cp.delete();

        if (cont)
            try {
                return org.apache.commons.io.FileUtils.readFileToByteArray(clazz);
            } catch (IOException e) {
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
            }

        return null;
    }
}
