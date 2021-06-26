package the.bytecode.club.bytecodeviewer.decompilers;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import me.konloch.kontainer.io.DiskReader;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
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
 * CFR Java Wrapper
 *
 * @author Konloch
 */

public class CFRDecompiler extends InternalDecompiler
{

    private static final String[] WINDOWS_IS_GREAT = new String[]
            {
                    "CON",
                    "PRN",
                    "AUX",
                    "NUL",
                    "COM1",
                    "COM2",
                    "COM3",
                    "COM4",
                    "COM5",
                    "COM6",
                    "COM7",
                    "COM8",
                    "COM9",
                    "LPT1",
                    "LPT2",
                    "LPT3",
                    "LPT4",
                    "LPT5",
                    "LPT6",
                    "LPT7",
                    "LPT8",
                    "LPT9"
            };

    public static String windowsFun(String base) {
        for (String s : WINDOWS_IS_GREAT) {
            if (base.contains(s.toLowerCase())) {
                base = base.replace(s.toLowerCase(), "BCV");
            }
        }

        return base;
    }

    @Override
    public String decompileClassNode(ClassNode cn, byte[] b) {
        String fileStart = tempDirectory + fs.toLowerCase();

        String exception = "";
        //final File tempClass = new File(windowsFun(MiscUtils.getUniqueName(fileStart, ".class") + ".class"));
        final File tempClass = new File(MiscUtils.getUniqueName(fileStart, ".class") + ".class");

        try {
            final FileOutputStream fos = new FileOutputStream(tempClass);

            fos.write(b);

            fos.close();
        } catch (final IOException e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }

        String fuckery = fuckery(fileStart);

        /*if (!BytecodeViewer.fatJar) {
            try {
                ProcessBuilder pb = new ProcessBuilder(ArrayUtils.addAll(
                        new String[]{BytecodeViewer.getJavaCommand(), "-jar", Resources.findLibrary("cfr")},
                        generateMainMethod(tempClass.getAbsolutePath(), fuckery)
                ));
                BytecodeViewer.sm.stopBlocking();
                Process p = pb.start();
                BytecodeViewer.createdProcesses.add(p);
                p.waitFor();
            } catch (Exception e) {
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
            } finally {
                BytecodeViewer.sm.setBlocking();
            }
        } else {
            org.benf.cfr.reader.Main.main(generateMainMethod(tempClass.getAbsolutePath(), fuckery));
        }*/

        try {
            org.benf.cfr.reader.Main.main(generateMainMethod(tempClass.getAbsolutePath(), fuckery));
        } catch (StackOverflowError | Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            e.printStackTrace();

            exception =
                    "Bytecode Viewer Version: " + VERSION + nl + nl + sw;
        }

        tempClass.delete();
        File file = new File(fuckery);

        if (file.exists())
            return findFile(Objects.requireNonNull(file.listFiles()));

        return "CFR error! Send the stacktrace to Konloch at https://the.bytecode.club or konloch@gmail.com" + nl + nl + "Suggested Fix: Click refresh class, if it fails again try another decompiler." + nl + nl + exception;
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

                    String exception =
                            "Bytecode Viewer Version: " + VERSION + nl + nl + sw;
                    return "CFR error! Send the stacktrace to Konloch at https://the.bytecode.club or konloch@gmail"
                            + ".com" + nl + nl + "Suggested Fix: Click refresh class, "
                            + "if it fails again try another decompiler." + nl + nl + exception;
                }
                return s;
            }
        }
        return "CFR error!" + nl + nl + "Suggested Fix: Click refresh class, if it "
                + "fails again try another decompiler.";
    }

    public String[] generateMainMethod(String filePath, String outputPath) {
        return new String[]{
                filePath,
                "--outputdir",
                outputPath,
                "--decodeenumswitch",
                String.valueOf(BytecodeViewer.viewer.decodeEnumSwitch
                        .isSelected()),
                "--sugarenums",
                String.valueOf(BytecodeViewer.viewer.sugarEnums.isSelected()),
                "--decodestringswitch",
                String.valueOf(BytecodeViewer.viewer.decodeStringSwitch
                        .isSelected()),
                "--arrayiter",
                String.valueOf(BytecodeViewer.viewer.arrayiter.isSelected()),
                "--collectioniter",
                String.valueOf(BytecodeViewer.viewer.collectioniter
                        .isSelected()),
                "--innerclasses",
                String.valueOf(BytecodeViewer.viewer.innerClasses.isSelected()),
                "--removeboilerplate",
                String.valueOf(BytecodeViewer.viewer.removeBoilerPlate
                        .isSelected()),
                "--removeinnerclasssynthetics",
                String.valueOf(BytecodeViewer.viewer.removeInnerClassSynthetics
                        .isSelected()),
                "--decodelambdas",
                String.valueOf(BytecodeViewer.viewer.decodeLambdas.isSelected()),
                "--hidebridgemethods",
                String.valueOf(BytecodeViewer.viewer.hideBridgeMethods
                        .isSelected()),
                "--liftconstructorinit",
                String.valueOf(BytecodeViewer.viewer.liftConstructorInit
                        .isSelected()),
                "--removedeadmethods",
                String.valueOf(BytecodeViewer.viewer.removeDeadMethods
                        .isSelected()),
                "--removebadgenerics",
                String.valueOf(BytecodeViewer.viewer.removeBadGenerics
                        .isSelected()),
                "--sugarasserts",
                String.valueOf(BytecodeViewer.viewer.sugarAsserts.isSelected()),
                "--sugarboxing",
                String.valueOf(BytecodeViewer.viewer.sugarBoxing.isSelected()),
                "--showversion",
                String.valueOf(BytecodeViewer.viewer.showVersion.isSelected()),
                "--decodefinally",
                String.valueOf(BytecodeViewer.viewer.decodeFinally.isSelected()),
                "--tidymonitors",
                String.valueOf(BytecodeViewer.viewer.tidyMonitors.isSelected()),
                "--lenient",
                String.valueOf(BytecodeViewer.viewer.lenient.isSelected()),
                "--dumpclasspath",
                String.valueOf(BytecodeViewer.viewer.dumpClassPath.isSelected()),
                "--comments",
                String.valueOf(BytecodeViewer.viewer.comments.isSelected()),
                "--forcetopsort",
                String.valueOf(BytecodeViewer.viewer.forceTopSort.isSelected()),
                "--forcetopsortaggress",
                String.valueOf(BytecodeViewer.viewer.forceTopSortAggress
                        .isSelected()),
                "--stringbuffer",
                String.valueOf(BytecodeViewer.viewer.stringBuffer.isSelected()),
                "--stringbuilder",
                String.valueOf(BytecodeViewer.viewer.stringBuilder.isSelected()),
                "--silent",
                String.valueOf(BytecodeViewer.viewer.silent.isSelected()),
                "--recover",
                String.valueOf(BytecodeViewer.viewer.recover.isSelected()),
                "--eclipse",
                String.valueOf(BytecodeViewer.viewer.eclipse.isSelected()),
                "--override",
                String.valueOf(BytecodeViewer.viewer.override.isSelected()),
                "--showinferrable",
                String.valueOf(BytecodeViewer.viewer.showInferrable
                        .isSelected()),
                "--aexagg",
                String.valueOf(BytecodeViewer.viewer.aexagg.isSelected()),
                "--forcecondpropagate",
                String.valueOf(BytecodeViewer.viewer.forceCondPropagate
                        .isSelected()),
                "--hideutf",
                String.valueOf(BytecodeViewer.viewer.hideUTF.isSelected()),
                "--hidelongstrings",
                String.valueOf(BytecodeViewer.viewer.hideLongStrings
                        .isSelected()),
                "--commentmonitors",
                String.valueOf(BytecodeViewer.viewer.commentMonitor
                        .isSelected()),
                "--allowcorrecting",
                String.valueOf(BytecodeViewer.viewer.allowCorrecting
                        .isSelected()),
                "--labelledblocks",
                String.valueOf(BytecodeViewer.viewer.labelledBlocks
                        .isSelected()),
                "--j14classobj",
                String.valueOf(BytecodeViewer.viewer.j14ClassOBJ.isSelected()),
                "--hidelangimports",
                String.valueOf(BytecodeViewer.viewer.hideLangImports
                        .isSelected()),
                "--recovertypeclash",
                String.valueOf(BytecodeViewer.viewer.recoveryTypeClash
                        .isSelected()),
                "--recovertypehints",
                String.valueOf(BytecodeViewer.viewer.recoveryTypehInts
                        .isSelected()),
                "--forcereturningifs",
                String.valueOf(BytecodeViewer.viewer.forceTurningIFs
                        .isSelected()),
                "--forloopaggcapture",
                String.valueOf(BytecodeViewer.viewer.forLoopAGGCapture
                        .isSelected()),};
    }

    @Override
    public void decompileToZip(String sourceJar, String zipName) {
        File tempZip = new File(sourceJar);

        String fileStart = tempDirectory + fs;
        String fuckery = fuckery(fileStart);

        org.benf.cfr.reader.Main.main(generateMainMethod(tempZip.getAbsolutePath(), fuckery));

        File fuck = new File(fuckery);

        try {
            zip(fuck, new File(zipName));
        } catch (IOException e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }

        fuck.delete();
    }

    @SuppressWarnings("resource")
    public void zip(File directory, File zipfile) throws IOException {
        java.net.URI base = directory.toURI();
        Deque<File> queue = new LinkedList<>();
        queue.push(directory);
        OutputStream out = new FileOutputStream(zipfile);
        Closeable res = out;
        try {
            ZipOutputStream zout = new ZipOutputStream(out);
            res = zout;
            while (!queue.isEmpty()) {
                directory = queue.pop();
                for (File kid : Objects.requireNonNull(directory.listFiles())) {
                    String name = base.relativize(kid.toURI()).getPath();
                    if (kid.isDirectory()) {
                        queue.push(kid);
                        name = name.endsWith("/") ? name : name + "/";
                        zout.putNextEntry(new ZipEntry(name));
                    } else {
                        zout.putNextEntry(new ZipEntry(name));
                        copy(kid, zout);
                        zout.closeEntry();
                    }
                }
            }
        } finally {
            res.close();
            out.close();
        }
    }

    private static void copy(InputStream in, OutputStream out)
            throws IOException {
        byte[] buffer = new byte[1024];
        while (true) {
            int readCount = in.read(buffer);
            if (readCount < 0) {
                break;
            }
            out.write(buffer, 0, readCount);
        }
    }

    private static void copy(File file, OutputStream out) throws IOException {
        try (InputStream in = new FileInputStream(file)) {
            copy(in, out);
        }
    }
}
