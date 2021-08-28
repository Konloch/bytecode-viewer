package the.bytecode.club.bytecodeviewer.decompilers.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import me.konloch.kontainer.io.DiskReader;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.decompilers.InternalDecompiler;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import static the.bytecode.club.bytecodeviewer.Constants.LAUNCH_DECOMPILERS_IN_NEW_PROCESS;
import static the.bytecode.club.bytecodeviewer.Constants.fs;
import static the.bytecode.club.bytecodeviewer.Constants.nl;
import static the.bytecode.club.bytecodeviewer.Constants.tempDirectory;
import static the.bytecode.club.bytecodeviewer.translation.TranslatedStrings.ERROR;
import static the.bytecode.club.bytecodeviewer.translation.TranslatedStrings.FERNFLOWER;

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
 * A FernFlower wrapper with all the options (except 2)
 *
 * @author Konloch
 * @author WaterWolf
 * @since 09/26/2011
 */
public class FernFlowerDecompiler extends InternalDecompiler
{
    @Override
    public void decompileToZip(String sourceJar, String zipName)
    {
        File tempZip = new File(sourceJar);

        File f = new File(tempDirectory + fs + "temp" + fs);
        f.mkdir();

        try {
            org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler.main(generateMainMethod(tempZip.getAbsolutePath(), tempDirectory + "./temp/"));
        } catch (StackOverflowError | Exception ignored) { }

        File tempZip2 = new File(tempDirectory + fs + "temp" + fs + tempZip.getName());
        if (tempZip2.exists())
            tempZip2.renameTo(new File(zipName));

        f.delete();
    }

    @Override
    public String decompileClassNode(final ClassNode cn, byte[] b)
    {
        String start = tempDirectory + fs + MiscUtils.getUniqueName("", ".class");

        final File tempClass = new File(start + ".class");
        
        String exception = "";
        try (FileOutputStream fos = new FileOutputStream(tempClass)) {
            fos.write(b);
        } catch (final IOException e) {
            StringWriter exceptionWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(exceptionWriter));
            e.printStackTrace();
            exception = exceptionWriter.toString();
        }


        if (LAUNCH_DECOMPILERS_IN_NEW_PROCESS)
        {
            /*try
            {
                BytecodeViewer.sm.pauseBlocking();
                ProcessBuilder pb = new ProcessBuilder(ArrayUtils.addAll(
                        new String[]{ExternalResources.getSingleton().getJavaCommand(true), "-jar", ExternalResources.getSingleton().findLibrary("fernflower")},
                        generateMainMethod(tempClass.getAbsolutePath(),
                                new File(tempDirectory).getAbsolutePath())
                ));
                Process p = pb.start();
                BytecodeViewer.createdProcesses.add(p);
                p.waitFor();
            } catch (Exception e) {
                BytecodeViewer.handleException(e);
            } finally {
                BytecodeViewer.sm.resumeBlocking();
            }*/
        }
        else
        {
            try {
                org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler.main(
                        generateMainMethod(tempClass.getAbsolutePath(), new File(tempDirectory).getAbsolutePath()));
            } catch (Throwable e) {
                StringWriter exceptionWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(exceptionWriter));
                e.printStackTrace();
                exception =  exceptionWriter.toString();
            }
        }

        tempClass.delete();

        final File outputJava = new File(start + ".java");
        if (outputJava.exists()) {
            String s;
            try {
                s = DiskReader.loadAsString(outputJava.getAbsolutePath());
                
                outputJava.delete();
                
                return s;
            } catch (Exception e) {
                StringWriter exceptionWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(exceptionWriter));
                e.printStackTrace();

                exception += nl + nl + exceptionWriter;
            }
        }
        
        return FERNFLOWER + " " + ERROR + "! " + ExceptionUI.SEND_STACKTRACE_TO +
                nl + nl + TranslatedStrings.SUGGESTED_FIX_DECOMPILER_ERROR +
                nl + nl + exception;
    }

    private String[] generateMainMethod(String className, String folder) {
        return new String[]{
                "-rbr=" + r(BytecodeViewer.viewer.rbr.isSelected()),
                "-rsy=" + r(BytecodeViewer.viewer.rsy.isSelected()),
                "-din=" + r(BytecodeViewer.viewer.din.isSelected()),
                "-dc4=" + r(BytecodeViewer.viewer.dc4.isSelected()),
                "-das=" + r(BytecodeViewer.viewer.das.isSelected()),
                "-hes=" + r(BytecodeViewer.viewer.hes.isSelected()),
                "-hdc=" + r(BytecodeViewer.viewer.hdc.isSelected()),
                "-dgs=" + r(BytecodeViewer.viewer.dgs.isSelected()),
                "-ner=" + r(BytecodeViewer.viewer.ner.isSelected()),
                "-den=" + r(BytecodeViewer.viewer.den.isSelected()),
                "-rgn=" + r(BytecodeViewer.viewer.rgn.isSelected()),
                "-bto=" + r(BytecodeViewer.viewer.bto.isSelected()),
                "-nns=" + r(BytecodeViewer.viewer.nns.isSelected()),
                "-uto=" + r(BytecodeViewer.viewer.uto.isSelected()),
                "-udv=" + r(BytecodeViewer.viewer.udv.isSelected()),
                "-rer=" + r(BytecodeViewer.viewer.rer.isSelected()),
                "-fdi=" + r(BytecodeViewer.viewer.fdi.isSelected()),
                "-asc=" + r(BytecodeViewer.viewer.asc.isSelected()),
                "-ren=" + r(BytecodeViewer.viewer.ren.isSelected()), className,
                folder};
    }

    private String r(boolean b) {
        if (b) {
            return "1";
        } else {
            return "0";
        }
    }
}
