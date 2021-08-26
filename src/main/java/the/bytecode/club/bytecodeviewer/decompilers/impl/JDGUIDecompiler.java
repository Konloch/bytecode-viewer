package the.bytecode.club.bytecodeviewer.decompilers.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import me.konloch.kontainer.io.DiskReader;
import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.decompilers.InternalDecompiler;
import the.bytecode.club.bytecodeviewer.decompilers.jdgui.CommonPreferences;
import the.bytecode.club.bytecodeviewer.decompilers.jdgui.DirectoryLoader;
import the.bytecode.club.bytecodeviewer.decompilers.jdgui.JDGUIClassFileUtil;
import the.bytecode.club.bytecodeviewer.decompilers.jdgui.PlainTextPrinter;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import static the.bytecode.club.bytecodeviewer.Constants.fs;
import static the.bytecode.club.bytecodeviewer.Constants.nl;
import static the.bytecode.club.bytecodeviewer.translation.TranslatedStrings.ERROR;
import static the.bytecode.club.bytecodeviewer.translation.TranslatedStrings.JDGUI;

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
 * JD-Core Decompiler Wrapper
 *
 * @author Konloch
 * @author JD-Core developers
 */

public class JDGUIDecompiler extends InternalDecompiler
{

    @Override
    public String decompileClassNode(ClassNode cn, byte[] b) {
        String exception;
        try {
            final File tempDirectory = new File(Constants.tempDirectory + fs + MiscUtils.randomString(32) + fs);
            tempDirectory.mkdir();
            
            final File tempClass = new File(tempDirectory.getAbsolutePath() + fs + cn.name + ".class");
            final File tempJava = new File(tempDirectory.getAbsolutePath() + fs + cn.name + ".java");

            if (cn.name.contains("/")) {
                String[] raw = cn.name.split("/");
                String path = tempDirectory.getAbsolutePath() + fs;
                for (int i = 0; i < raw.length - 1; i++) {
                    path += raw[i] + fs;
                    File f = new File(path);
                    f.mkdir();
                }
            }

            try (FileOutputStream fos = new FileOutputStream(tempClass)) {
                fos.write(b);
            } catch (final IOException e) {
                BytecodeViewer.handleException(e);
            }


            String pathToClass = tempClass.getAbsolutePath().replace('/', File.separatorChar).replace('\\', File.separatorChar);
            String directoryPath = JDGUIClassFileUtil.ExtractDirectoryPath(pathToClass);
            String internalPath = JDGUIClassFileUtil.ExtractInternalPath(directoryPath, pathToClass);

            CommonPreferences preferences = new CommonPreferences() {
                @Override
                public boolean isShowLineNumbers() {
                    return false;
                }

                @Override
                public boolean isMergeEmptyLines() {
                    return true;
                }
            };

            DirectoryLoader loader = new DirectoryLoader(new File(directoryPath));

            //PrintStream ps = new PrintStream("test.html");
            //HtmlPrinter printer = new HtmlPrinter(ps);
            org.jd.core.v1.api.Decompiler decompiler = new ClassFileToJavaSourceDecompiler();

            try (PrintStream ps = new PrintStream(tempJava.getAbsolutePath());
                 PlainTextPrinter printer = new PlainTextPrinter(preferences, ps)) {
                decompiler.decompile(loader, printer, internalPath, preferences.getPreferences());
            }

            return DiskReader.loadAsString(tempJava.getAbsolutePath());
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            e.printStackTrace();

            exception = ExceptionUI.SEND_STACKTRACE_TO_NL + sw;
        }
        
        return JDGUI + " " + ERROR + "! " + ExceptionUI.SEND_STACKTRACE_TO +
                nl + nl + TranslatedStrings.SUGGESTED_FIX_DECOMPILER_ERROR +
                nl + nl + exception;
    }

    @Override
    public void decompileToZip(String sourceJar, String zipName) {
    }
}
