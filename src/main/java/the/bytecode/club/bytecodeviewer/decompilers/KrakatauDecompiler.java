package the.bytecode.club.bytecodeviewer.decompilers;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.stream.Collectors;
import me.konloch.kontainer.io.DiskReader;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.util.JarUtils;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;
import the.bytecode.club.bytecodeviewer.util.ZipUtils;
import static the.bytecode.club.bytecodeviewer.Constants.*;

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

public class KrakatauDecompiler extends InternalDecompiler
{

    public String quick() {
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

        return ";" + Arrays.stream(files).filter(File::isFile)
                .map(File::getAbsolutePath).collect(Collectors.joining(";"));
    }

    public String decompileClassNode(File krakatauTempJar, File krakatauTempDir, ClassNode cn) {
        if (Configuration.python.isEmpty()) {
            BytecodeViewer.showMessage("You need to set your Python (or PyPy for speed) 2.7 executable path.");
            BytecodeViewer.viewer.selectPythonC();
        }

        BytecodeViewer.rtCheck();
        if (Configuration.rt.isEmpty()) {
            BytecodeViewer.showMessage("You need to set your JRE RT Library.\r\n(C:\\Program Files (x86)"
                    + "\\Java\\jre7\\lib\\rt.jar)");
            BytecodeViewer.viewer.selectJRERTLibrary();
        }

        if (Configuration.python.isEmpty()) {
            BytecodeViewer.showMessage("You need to set Python!");
            return "Set your paths";
        }

        if (Configuration.rt.isEmpty()) {
            BytecodeViewer.showMessage("You need to set RT.jar!");
            return "Set your paths";
        }

        String s = "Bytecode Viewer Version: " + VERSION + nl + nl +
                "Please send this to konloch@gmail.com. " + nl + nl;

        BytecodeViewer.sm.stopBlocking();
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    Configuration.python,
                    "-O", //love you storyyeller <3
                    krakatauWorkingDirectory + fs + "decompile.py",
                    "-skip", //love you storyyeller <3
                    "-nauto",
                    "-path",
                    Configuration.rt + ";" + krakatauTempJar.getAbsolutePath() + quick(),
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
            StringBuilder log = new StringBuilder("Process:" + nl + nl);
            String line;
            while ((line = br.readLine()) != null) {
                log.append(nl).append(line);
            }
            br.close();

            log.append(nl).append(nl).append("Error:").append(nl).append(nl);
            is = process.getErrorStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                log.append(nl).append(line);
            }
            br.close();

            int exitValue = process.waitFor();
            log.append(nl).append(nl).append("Exit Value is ").append(exitValue);
            s = log.toString();

            //if the motherfucker failed this'll fail, aka wont set.
            s = DiskReader.loadAsString(krakatauTempDir.getAbsolutePath() + fs + cn.name + ".java");
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            e.printStackTrace();
            s += nl + "Bytecode Viewer Version: " + VERSION + nl + nl + sw;
        } finally {
            BytecodeViewer.sm.setBlocking();
        }

        return s;
    }

    @Override
    public String decompileClassNode(ClassNode cn, byte[] b) {
        if (Configuration.python.isEmpty()) {
            BytecodeViewer.showMessage("You need to set your Python (or PyPy for speed) 2.7 executable path.");
            BytecodeViewer.viewer.selectPythonC();
        }
        if (Configuration.rt.isEmpty()) {
            BytecodeViewer.showMessage("You need to set your JRE RT Library." +
                    "\r\n(C:\\Program Files (x86)\\Java\\jre7\\lib\\rt.jar)");
            BytecodeViewer.viewer.selectJRERTLibrary();
        }

        if (Configuration.python.isEmpty()) {
            BytecodeViewer.showMessage("You need to set Python!");
            return "Set your paths";
        }

        if (Configuration.rt.isEmpty()) {
            BytecodeViewer.showMessage("You need to set RT.jar!");
            return "Set your paths";
        }

        String s = "Bytecode Viewer Version: " + VERSION + nl + nl +
                "Please send this to konloch@gmail.com. " + nl + nl;

        final File tempDirectory = new File(Constants.tempDirectory + fs + MiscUtils.randomString(32) + fs);
        tempDirectory.mkdir();
        final File tempJar = new File(Constants.tempDirectory + fs + "temp" + MiscUtils.randomString(32) + ".jar");

        JarUtils.saveAsJarClassesOnly(BytecodeViewer.getLoadedClasses(), tempJar.getAbsolutePath());

        BytecodeViewer.sm.stopBlocking();

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    Configuration.python,
                    "-O", //love you storyyeller <3
                    krakatauWorkingDirectory + fs + "decompile.py",
                    "-skip", //love you storyyeller <3
                    "-nauto",
                    "-path",
                    Configuration.rt + ";" + tempJar.getAbsolutePath() + quick(),
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
            StringBuilder log = new StringBuilder("Process:" + nl + nl);
            String line;
            while ((line = br.readLine()) != null) {
                log.append(nl).append(line);
            }
            br.close();

            log.append(nl).append(nl).append("Error:").append(nl)
                    .append(nl);
            is = process.getErrorStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                log.append(nl).append(line);
            }
            br.close();

            int exitValue = process.waitFor();
            log.append(nl).append(nl).append("Exit Value is ").append(exitValue);
            s = log.toString();

            //if the motherfucker failed this'll fail, aka wont set.
            s = DiskReader.loadAsString(tempDirectory.getAbsolutePath() + fs + cn.name + ".java");
            tempDirectory.delete();
            tempJar.delete();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            e.printStackTrace();
            s += nl + "Bytecode Viewer Version: " + VERSION + nl + nl + sw;
        } finally {
            BytecodeViewer.sm.setBlocking();
        }

        return s;
    }

    @Override
    public void decompileToZip(String sourceJar, String zipName) {
        if (Configuration.python.isEmpty()) {
            BytecodeViewer.showMessage("You need to set your Python (or PyPy for speed) 2.7 executable path.");
            BytecodeViewer.viewer.selectPythonC();
        }
        BytecodeViewer.rtCheck();
        if (Configuration.rt.isEmpty()) {
            BytecodeViewer.showMessage("You need to set your JRE RT Library." +
                    "\r\n(C:\\Program Files (x86)\\Java\\jre7\\lib\\rt.jar)");
            BytecodeViewer.viewer.selectJRERTLibrary();
        }

        String ran = MiscUtils.randomString(32);
        final File tempDirectory = new File(Constants.tempDirectory + fs + ran + fs);
        tempDirectory.mkdir();


        final File tempJar = new File(sourceJar);

        BytecodeViewer.sm.stopBlocking();

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    Configuration.python,
                    "-O", //love you storyyeller <3
                    krakatauWorkingDirectory + fs + "decompile.py",
                    "-skip", //love you storyyeller <3
                    "-nauto",
                    "-path",
                    Configuration.rt + ";" + tempJar.getAbsolutePath(),
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
