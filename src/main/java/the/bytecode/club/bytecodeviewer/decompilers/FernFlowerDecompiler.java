package the.bytecode.club.bytecodeviewer.decompilers;

import org.apache.commons.io.FileUtils;
import org.jetbrains.java.decompiler.main.decompiler.BaseDecompiler;
import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler;
import org.jetbrains.java.decompiler.main.decompiler.PrintStreamLogger;
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;
import org.objectweb.asm.tree.ClassNode;
import org.zeroturnaround.zip.ZipUtil;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.JarUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.Manifest;

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
 * A FernFlower wrapper with all the options (except 2)
 *
 * @author Konloch
 * @author WaterWolf
 */

public class FernFlowerDecompiler extends Decompiler {

    @Override
    public String decompileClassNode(final ClassNode cn, final byte[] b) {
        Map<String, Object> options = main(generateMainMethod());

        final AtomicReference<String> result = new AtomicReference<String>();
        result.set(null);

        BaseDecompiler baseDecompiler = new BaseDecompiler(new IBytecodeProvider() {
            @Override
            public byte[] getBytecode(String s, String s1) throws IOException {
                byte[] clone = new byte[b.length];
                System.arraycopy(b, 0, clone, 0, b.length);
                return clone;
            }
        }, new IResultSaver() {
            @Override
            public void saveFolder(String s) {

            }

            @Override
            public void copyFile(String s, String s1, String s2) {

            }

            @Override
            public void saveClassFile(String s, String s1, String s2, String s3, int[] ints) {
                result.set(s3);
            }

            @Override
            public void createArchive(String s, String s1, Manifest manifest) {

            }

            @Override
            public void saveDirEntry(String s, String s1, String s2) {

            }

            @Override
            public void copyEntry(String s, String s1, String s2, String s3) {

            }

            @Override
            public void saveClassEntry(String s, String s1, String s2, String s3, String s4) {
            }

            @Override
            public void closeArchive(String s, String s1) {

            }
        }, options, new PrintStreamLogger(System.out));

        try {
            baseDecompiler.addSpace(new File("fernflower.class"), true);
            baseDecompiler.decompileContext();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            if (result.get() != null) {
                break;
            }
        }
        return result.get();
    }

    @Override
    public void decompileToZip(String zipName) {
        try {
            Path outputDir = Files.createTempDirectory("fernflower_output");
            Path tempJar = Files.createTempFile("fernflower_input", ".jar");
            File output = new File(zipName);
            JarUtils.saveAsJar(BytecodeViewer.getLoadedBytes(), tempJar.toAbsolutePath().toString());
            ConsoleDecompiler decompiler = new ConsoleDecompiler(outputDir.toFile(), main(generateMainMethod()));
            decompiler.addSpace(tempJar.toFile(), true);
            decompiler.decompileContext();
            Files.move(outputDir.toFile().listFiles()[0].toPath(), output.toPath());
            Files.delete(tempJar);
            FileUtils.deleteDirectory(outputDir.toFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> main(String[] args) {
        HashMap mapOptions = new HashMap();
        boolean isOption = true;

        for (int destination = 0; destination < args.length - 1; ++destination) {
            String logger = args[destination];
            if (isOption && logger.length() > 5 && logger.charAt(0) == 45 && logger.charAt(4) == 61) {
                String decompiler = logger.substring(5);
                if ("true".equalsIgnoreCase(decompiler)) {
                    decompiler = "1";
                } else if ("false".equalsIgnoreCase(decompiler)) {
                    decompiler = "0";
                }

                mapOptions.put(logger.substring(1, 4), decompiler);
            } else {
                isOption = false;
            }
        }

        return mapOptions;
    }

    private String[] generateMainMethod() {
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
                "-asc=" + r(BytecodeViewer.viewer.asc.isSelected())
        };
    }

    private String r(boolean b) {
        return b ? "1" : "0";
    }
}
