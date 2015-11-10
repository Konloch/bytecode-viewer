package the.bytecode.club.bytecodeviewer.decompilers;

import jd.cli.preferences.CommonPreferences;
import jd.cli.printer.text.PlainTextPrinter;
import jd.core.loader.Loader;
import jd.core.loader.LoaderException;
import jd.core.process.DecompilerImpl;
import org.apache.commons.io.FileUtils;
import org.benf.cfr.reader.state.ClassFileSourceImpl;
import org.benf.cfr.reader.state.DCCommonState;
import org.benf.cfr.reader.util.getopt.GetOptParser;
import org.benf.cfr.reader.util.getopt.Options;
import org.benf.cfr.reader.util.getopt.OptionsImpl;
import org.objectweb.asm.tree.ClassNode;
import org.zeroturnaround.zip.ZipUtil;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.JarUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
 * JD-Core Decompiler Wrapper
 *
 * @author Konloch
 * @author JD-Core developers
 *
 */

public class JDGUIDecompiler extends Decompiler {

    @Override
    public String decompileClassNode(ClassNode cn, final byte[] b) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            decompile(b, outputStream);
            return outputStream.toString("UTF-8");
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            e.printStackTrace();
            String exception = "Bytecode Viewer Version: " + BytecodeViewer.version + BytecodeViewer.nl + BytecodeViewer.nl + sw.toString();
            return "CFR error! Send the stacktrace to Konloch at http://the.bytecode.club or konloch@gmail.com" + BytecodeViewer.nl + BytecodeViewer.nl + "Suggested Fix: Click refresh class, if it fails again try another decompiler." + BytecodeViewer.nl + BytecodeViewer.nl + exception;
        }
    }

    @Override
    public void decompileToZip(String zipName) {
        try {
            File output = new File(zipName);
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(output));
            for (Map.Entry<String, byte[]> entry : BytecodeViewer.getLoadedBytes().entrySet()) {
                String name = entry.getKey();
                if (name.endsWith(".class")) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    try {
                        decompile(entry.getValue(), out);
                        zipOutputStream.putNextEntry(new ZipEntry(name.substring(0, name.length() - 6) + ".java"));
                        zipOutputStream.write(out.toByteArray());
                    } catch (Exception e) {
                        e.printStackTrace();
                        zipOutputStream.putNextEntry(new ZipEntry(name));
                        zipOutputStream.write(entry.getValue());
                    }
                } else {
                    zipOutputStream.putNextEntry(new ZipEntry(name));
                    zipOutputStream.write(entry.getValue());
                }
                zipOutputStream.closeEntry();
            }
            zipOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace(); //TODO How to handle exceptions again?
        }
    }

    private void decompile(final byte[] data, OutputStream to) throws LoaderException, UnsupportedEncodingException {
        CommonPreferences preferences = new CommonPreferences() {
            @Override
            public boolean isShowLineNumbers() {
                return true;
            }

            @Override
            public boolean isMergeEmptyLines() {
                return true;
            }
        };
        Loader customLoader = new Loader() {
            @Override
            public DataInputStream load(String s) throws LoaderException {
                return new DataInputStream(new ByteArrayInputStream(data));
            }

            @Override
            public boolean canLoad(String s) {
                return true;
            }
        };
        new DecompilerImpl().decompile(preferences, customLoader, new PlainTextPrinter(preferences, new PrintStream(to, false, "UTF-8")), "BytecodeViewer.class");
    }
}