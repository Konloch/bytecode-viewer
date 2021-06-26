package the.bytecode.club.bytecodeviewer.decompilers;

import jadx.api.JadxArgs;
import jadx.api.JadxDecompiler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
import java.util.Random;
import me.konloch.kontainer.io.DiskReader;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

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
 * JADX Java Wrapper
 *
 * @author Konloch
 */
public class JADXDecompiler extends InternalDecompiler
{
    @Override
    public String decompileClassNode(ClassNode cn, byte[] b) {
        String fileStart = tempDirectory + fs;

        String exception = "";
        final File tempClass = new File(MiscUtils.getUniqueName(fileStart, ".class") + ".class");

        try {
            final FileOutputStream fos = new FileOutputStream(tempClass);
            fos.write(b);
            fos.close();
        } catch (final IOException e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }

        File fuckery = new File(fuckery(fileStart));
        
        try {
            JadxArgs args = new JadxArgs();
            args.getInputFiles().add(tempClass);
            args.setOutDir(fuckery);
            args.setOutDirSrc(fuckery);
            args.setOutDirRes(fuckery);

            JadxDecompiler jadx = new JadxDecompiler(args);
            jadx.load();
            jadx.saveSources();
            //jadx.close();
        } catch (StackOverflowError | Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            e.printStackTrace();
            exception = "Bytecode Viewer Version: " + VERSION + nl + nl + sw;
        }

        tempClass.delete();

        if (fuckery.exists())
            return findFile(Objects.requireNonNull(fuckery.listFiles()));
        
        if(exception.isEmpty())
            exception = "Decompiled source file not found!";

        return "JADX error! Send the stacktrace to Konloch at https://the.bytecode.club or konloch@gmail.com"
                + nl + nl + "Suggested Fix: Click refresh class, if it fails again try another decompiler."
                + nl + nl + exception;
    }

    Random r = new Random();
    File f;

    public String fuckery(String start) {
        while (true) {
            f = new File(start + r.nextInt(Integer.MAX_VALUE));
            if (!f.exists())
                return f.toString();
        }
    }

    public String findFile(File[] fA) {
        for (File f : fA) {
            if (f.isDirectory())
                return findFile(Objects.requireNonNull(f.listFiles()));
            else {
                String s;
                try {
                    s = DiskReader.loadAsString(f.getAbsolutePath());
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    e.printStackTrace();
                    String exception = "Bytecode Viewer Version: " + VERSION + nl + nl + sw;
                    
                    return "JADX error! Send the stacktrace to Konloch at https://the.bytecode.club or konloch@gmail"
                            + ".com" + nl + nl + "Suggested Fix: Click refresh class, "
                            + "if it fails again try another decompiler." + nl + nl + exception;
                }
                return s;
            }
        }
        return "JADX error!" + nl + nl + "Suggested Fix: Click refresh class, if it "
                + "fails again try another decompiler.";
    }

    @Override
    public void decompileToZip(String sourceJar, String zipName) {
    }

}
