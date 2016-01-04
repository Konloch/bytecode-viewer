package the.bytecode.club.bytecodeviewer.decompilers;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.tree.ClassNode;
import org.zeroturnaround.zip.ZipUtil;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.JarUtils;
import the.bytecode.club.bytecodeviewer.MiscUtils;
import the.bytecode.club.bytecodeviewer.Settings;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 * *
 * This program is free software: you can redistribute it and/or modify    *
 * it under the terms of the GNU General Public License as published by  *
 * the Free Software Foundation, either version 3 of the License, or     *
 * (at your option) any later version.                                   *
 * *
 * This program is distributed in the hope that it will be useful,       *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 * GNU General Public License for more details.                          *
 * *
 * You should have received a copy of the GNU General Public License     *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

/**
 * Krakatau Java Decompiler Wrapper, requires Python 2.7
 *
 * @author Konloch
 */

public class KrakatauDecompiler extends Decompiler {
    @Override
    public String getName() {
        return "Krakatau";
    }

    public String quick() {
        if (Settings.PATH.isEmpty()) return "";
        else return ";" + Settings.PATH.get();
    }

    public String decompileClassNode(ClassNode cn, byte[] b) {
        if (Settings.PYTHON2_LOCATION.isEmpty()) {
            BytecodeViewer.showMessage("You need to set your Python (or PyPy for speed) 2.7 executable path.");
            BytecodeViewer.viewer.pythonC();
        }
        if (Settings.RT_LOCATION.isEmpty()) {
            BytecodeViewer.showMessage("You need to set your JRE RT Library.\r\n(C:\\Program Files (x86)\\Java\\jre7\\lib\\rt.jar)");
            BytecodeViewer.viewer.rtC();
        }

        if (Settings.PYTHON2_LOCATION.isEmpty()) {
            BytecodeViewer.showMessage("You need to set Python!");
            return "Set your paths";
        }

        if (Settings.RT_LOCATION.isEmpty()) {
            BytecodeViewer.showMessage("You need to set RT.jar!");
            return "Set your paths";
        }

        String s = "Bytecode Viewer Version: " + BytecodeViewer.version + BytecodeViewer.nl + BytecodeViewer.nl + "Please send this to konloch@gmail.com. " + BytecodeViewer.nl + BytecodeViewer.nl;


        try {
            final Path outputJar = Files.createTempFile("kdeout", ".zip");
            final Path inputJar = Files.createTempFile("kdein", ".jar");
            JarUtils.saveAsJarClassesOnly(BytecodeViewer.getLoadedBytes(), inputJar.toAbsolutePath().toString());

            BytecodeViewer.sm.stopBlocking();
            ProcessBuilder pb = new ProcessBuilder(Settings.PYTHON2_LOCATION.get(), "-O", //love you storyyeller <3
                    BytecodeViewer.krakatauDirectory.getAbsolutePath() + BytecodeViewer.fs + "decompile.py", "-skip", //love you storyyeller <3
                    "-nauto", "-path", Settings.RT_LOCATION.get() + ";" + inputJar.toAbsolutePath().toString() + quick(), "-out", outputJar.toAbsolutePath().toString(), cn.name + ".class");

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

            ZipFile zipFile=  new ZipFile(outputJar.toFile());
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            byte[] data = null;
            while (entries.hasMoreElements()) {
                ZipEntry next = entries.nextElement();
                if (next.getName().equals(cn.name + ".java")) {
                    data = IOUtils.toByteArray(zipFile.getInputStream(next));
                }
            }
            zipFile.close();
            Files.delete(inputJar);
            Files.delete(outputJar);
            return new String(data, "UTF-8");
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            e.printStackTrace();
            s += BytecodeViewer.nl + "Bytecode Viewer Version: " + BytecodeViewer.version + BytecodeViewer.nl + BytecodeViewer.nl + sw.toString();
        } finally {
            BytecodeViewer.sm.setBlocking();
        }

        return s;
    }

    public void decompileToZip(String zipName) {
        if (Settings.PYTHON2_LOCATION.isEmpty()) {
            BytecodeViewer.showMessage("You need to set your Python (or PyPy for speed) 2.7 executable path.");
            BytecodeViewer.viewer.pythonC();
        }
        if (Settings.RT_LOCATION.isEmpty()) {
            BytecodeViewer.showMessage("You need to set your JRE RT Library.\r\n(C:\\Program Files (x86)\\Java\\jre7\\lib\\rt.jar)");
            BytecodeViewer.viewer.rtC();
        }


        try {
            File tempDir = Files.createTempDirectory("krakatauoutput").toFile();
            File tempJar = new File(tempDir, "temp.jar");
            JarUtils.saveAsJarClassesOnly(BytecodeViewer.getLoadedBytes(), tempJar.getAbsolutePath());

            BytecodeViewer.sm.stopBlocking();
            ProcessBuilder pb = new ProcessBuilder(Settings.PYTHON2_LOCATION.get(), "-O", //love you storyyeller <3
                    BytecodeViewer.krakatauDirectory.getAbsolutePath() + BytecodeViewer.fs + "decompile.py", "-skip", //love you storyyeller <3
                    "-nauto", "-path", Settings.RT_LOCATION.get() + ";" + tempJar.getAbsolutePath(), "-out", tempDir.getAbsolutePath(), tempJar.getAbsolutePath());

            Process process = pb.start();
            BytecodeViewer.createdProcesses.add(process);
            MiscUtils.printProcess(process);
            process.waitFor();

            tempJar.delete();
            ZipUtil.pack(tempDir, new File(zipName));
            FileUtils.deleteDirectory(tempDir);
        } catch (Exception e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        } finally {
            BytecodeViewer.sm.setBlocking();
        }
    }
}
