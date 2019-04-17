package the.bytecode.club.bytecodeviewer.decompilers;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import me.konloch.kontainer.io.DiskReader;

import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.util.JarUtils;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;
import the.bytecode.club.bytecodeviewer.util.ZipUtils;

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
 * Krakatau Java Decompiler Wrapper, requires Python 2.7
 *
 * @author Konloch
 */

public class KrakatauDecompiler extends Decompiler {

    public String quick() {
        if (BytecodeViewer.library.isEmpty())
            return "";
        else
            return ";" + BytecodeViewer.library;
    }

    public String decompileClassNode(File krakatauTempJar, File krakatauTempDir, ClassNode cn, byte[] b)
    {
        if (BytecodeViewer.python.equals("")) {
            BytecodeViewer.showMessage("You need to set your Python (or PyPy for speed) 2.7 executable path.");
            BytecodeViewer.viewer.pythonC();
        }

        BytecodeViewer.rtCheck();
        if (BytecodeViewer.rt.equals("")) {
            BytecodeViewer.showMessage("You need to set your JRE RT Library.\r\n(C:\\Program Files (x86)\\Java\\jre7\\lib\\rt.jar)");
            BytecodeViewer.viewer.rtC();
        }

        if (BytecodeViewer.python.equals("")) {
            BytecodeViewer.showMessage("You need to set Python!");
            return "Set your paths";
        }

        if (BytecodeViewer.rt.equals("")) {
            BytecodeViewer.showMessage("You need to set RT.jar!");
            return "Set your paths";
        }

        String s = "Bytecode Viewer Version: " + BytecodeViewer.VERSION + BytecodeViewer.nl + BytecodeViewer.nl + "Please send this to konloch@gmail.com. " + BytecodeViewer.nl + BytecodeViewer.nl;

        BytecodeViewer.sm.stopBlocking();
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    BytecodeViewer.python,
                    "-O", //love you storyyeller <3
                    BytecodeViewer.krakatauWorkingDirectory + BytecodeViewer.fs + "decompile.py",
                    "-skip", //love you storyyeller <3
                    "-nauto",
                    "-path",
                    BytecodeViewer.rt + ";" + krakatauTempJar.getAbsolutePath() + quick(),
                    "-out",
                    krakatauTempDir.getAbsolutePath(),
                    cn.name + ".class"
            );

            Process process = pb.start();
            BytecodeViewer.createdProcesses.add(process);

            //Read out dir output
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String log = "Process:" + BytecodeViewer.nl + BytecodeViewer.nl;
            String line;
            while ((line = br.readLine()) != null) {
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

            int exitValue = process.waitFor();
            log += BytecodeViewer.nl + BytecodeViewer.nl + "Exit Value is " + exitValue;
            s = log;

            //if the motherfucker failed this'll fail, aka wont set.
            s = DiskReader.loadAsString(krakatauTempDir.getAbsolutePath() + BytecodeViewer.fs + cn.name + ".java");
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            e.printStackTrace();
            s += BytecodeViewer.nl + "Bytecode Viewer Version: " + BytecodeViewer.VERSION + BytecodeViewer.nl + BytecodeViewer.nl + sw.toString();
        } finally {
            BytecodeViewer.sm.setBlocking();
        }

        return s;
    }

    @Override
    public String decompileClassNode(ClassNode cn, byte[] b)
    {
        if (BytecodeViewer.python.equals("")) {
            BytecodeViewer.showMessage("You need to set your Python (or PyPy for speed) 2.7 executable path.");
            BytecodeViewer.viewer.pythonC();
        }
        if (BytecodeViewer.rt.equals("")) {
            BytecodeViewer.showMessage("You need to set your JRE RT Library.\r\n(C:\\Program Files (x86)\\Java\\jre7\\lib\\rt.jar)");
            BytecodeViewer.viewer.rtC();
        }

        if (BytecodeViewer.python.equals("")) {
            BytecodeViewer.showMessage("You need to set Python!");
            return "Set your paths";
        }

        if (BytecodeViewer.rt.equals("")) {
            BytecodeViewer.showMessage("You need to set RT.jar!");
            return "Set your paths";
        }

        String s = "Bytecode Viewer Version: " + BytecodeViewer.VERSION + BytecodeViewer.nl + BytecodeViewer.nl + "Please send this to konloch@gmail.com. " + BytecodeViewer.nl + BytecodeViewer.nl;

        final File tempDirectory = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + MiscUtils.randomString(32) + BytecodeViewer.fs);
        tempDirectory.mkdir();
        final File tempJar = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp" + MiscUtils.randomString(32) + ".jar");
        JarUtils.saveAsJarClassesOnly(BytecodeViewer.getLoadedClasses(), tempJar.getAbsolutePath());

        BytecodeViewer.sm.stopBlocking();

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    BytecodeViewer.python,
                    "-O", //love you storyyeller <3
                    BytecodeViewer.krakatauWorkingDirectory + BytecodeViewer.fs + "decompile.py",
                    "-skip", //love you storyyeller <3
                    "-nauto",
                    "-path",
                    BytecodeViewer.rt + ";" + tempJar.getAbsolutePath() + quick(),
                    "-out",
                    tempDirectory.getAbsolutePath(),
                    cn.name + ".class"
            );

            Process process = pb.start();
            BytecodeViewer.createdProcesses.add(process);

            //Read out dir output
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String log = "Process:" + BytecodeViewer.nl + BytecodeViewer.nl;
            String line;
            while ((line = br.readLine()) != null) {
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

            int exitValue = process.waitFor();
            log += BytecodeViewer.nl + BytecodeViewer.nl + "Exit Value is " + exitValue;
            s = log;

            //if the motherfucker failed this'll fail, aka wont set.
            s = DiskReader.loadAsString(tempDirectory.getAbsolutePath() + BytecodeViewer.fs + cn.name + ".java");
            tempDirectory.delete();
            tempJar.delete();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            e.printStackTrace();
            s += BytecodeViewer.nl + "Bytecode Viewer Version: " + BytecodeViewer.VERSION + BytecodeViewer.nl + BytecodeViewer.nl + sw.toString();
        } finally {
            BytecodeViewer.sm.setBlocking();
        }

        return s;
    }

    public void decompileToZip(String sourceJar, String zipName) {
        if (BytecodeViewer.python.equals("")) {
            BytecodeViewer.showMessage("You need to set your Python (or PyPy for speed) 2.7 executable path.");
            BytecodeViewer.viewer.pythonC();
        }
        BytecodeViewer.rtCheck();
        if (BytecodeViewer.rt.equals("")) {
            BytecodeViewer.showMessage("You need to set your JRE RT Library.\r\n(C:\\Program Files (x86)\\Java\\jre7\\lib\\rt.jar)");
            BytecodeViewer.viewer.rtC();
        }

        String ran = MiscUtils.randomString(32);
        final File tempDirectory = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + ran + BytecodeViewer.fs);
        tempDirectory.mkdir();


        final File tempJar = new File(sourceJar);

        BytecodeViewer.sm.stopBlocking();

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    BytecodeViewer.python,
                    "-O", //love you storyyeller <3
                    BytecodeViewer.krakatauWorkingDirectory + BytecodeViewer.fs + "decompile.py",
                    "-skip", //love you storyyeller <3
                    "-nauto",
                    "-path",
                    BytecodeViewer.rt + ";" + tempJar.getAbsolutePath(),
                    "-out",
                    tempDirectory.getAbsolutePath(),
                    tempJar.getAbsolutePath()
            );

            Process process = pb.start();
            BytecodeViewer.createdProcesses.add(process);
            process.waitFor();
            MiscUtils.printProcess(process);

            ZipUtils.zipFolder(tempDirectory.getAbsolutePath(), zipName, ran);
        } catch (Exception e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        } finally {
            BytecodeViewer.sm.setBlocking();
        }
    }
}
