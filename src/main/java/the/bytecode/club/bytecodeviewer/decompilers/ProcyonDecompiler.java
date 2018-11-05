package the.bytecode.club.bytecodeviewer.decompilers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import org.objectweb.asm.tree.ClassNode;

import com.strobel.core.StringUtilities;
import com.strobel.decompiler.DecompilationOptions;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;
import com.strobel.decompiler.languages.java.JavaFormattingOptions;
import com.strobel.assembler.InputTypeLoader;
import com.strobel.assembler.metadata.Buffer;
import com.strobel.assembler.metadata.ITypeLoader;
import com.strobel.assembler.metadata.JarTypeLoader;
import com.strobel.assembler.metadata.MetadataSystem;
import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.assembler.metadata.TypeReference;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.JarUtils;
import the.bytecode.club.bytecodeviewer.MiscUtils;

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
 * Procyon Java Decompiler Wrapper
 *
 * @author Konloch
 * @author DeathMarine
 */

public class ProcyonDecompiler extends Decompiler {

    public DecompilerSettings getDecompilerSettings() {
        DecompilerSettings settings = new DecompilerSettings();
        settings.setAlwaysGenerateExceptionVariableForCatchBlocks(BytecodeViewer.viewer.chckbxmntmNewCheckItem_6
                .isSelected());
        settings.setExcludeNestedTypes(BytecodeViewer.viewer.chckbxmntmNewCheckItem_11
                .isSelected());
        settings.setShowDebugLineNumbers(BytecodeViewer.viewer.chckbxmntmShowDebugLine
                .isSelected());
        settings.setIncludeLineNumbersInBytecode(BytecodeViewer.viewer.chckbxmntmNewCheckItem_3
                .isSelected());
        settings.setIncludeErrorDiagnostics(BytecodeViewer.viewer.chckbxmntmNewCheckItem_4
                .isSelected());
        settings.setShowSyntheticMembers(BytecodeViewer.viewer.chckbxmntmNewCheckItem_7
                .isSelected());
        settings.setSimplifyMemberReferences(BytecodeViewer.viewer.chckbxmntmSimplifyMemberReferences
                .isSelected());
        settings.setMergeVariables(BytecodeViewer.viewer.mnMergeVariables
                .isSelected());
        settings.setForceExplicitTypeArguments(BytecodeViewer.viewer.chckbxmntmNewCheckItem_8
                .isSelected());
        settings.setForceExplicitImports(BytecodeViewer.viewer.chckbxmntmNewCheckItem_9
                .isSelected());
        settings.setFlattenSwitchBlocks(BytecodeViewer.viewer.chckbxmntmNewCheckItem_10
                .isSelected());
        settings.setRetainPointlessSwitches(BytecodeViewer.viewer.chckbxmntmNewCheckItem_2
                .isSelected());
        settings.setRetainRedundantCasts(BytecodeViewer.viewer.chckbxmntmNewCheckItem_5
                .isSelected());
        settings.setUnicodeOutputEnabled(BytecodeViewer.viewer.chckbxmntmNewCheckItem_1
                .isSelected());
        settings.setFormattingOptions(JavaFormattingOptions.createDefault());
        return settings;
    }

    @Override
    public String decompileClassNode(ClassNode cn, byte[] b) {
        String exception = "";
        try {
            String fileStart = BytecodeViewer.tempDirectory + BytecodeViewer.fs
                    + "temp";

            final File tempClass = new File(MiscUtils.getUniqueName(fileStart, ".class") + ".class");

            try {
                final FileOutputStream fos = new FileOutputStream(tempClass);

                fos.write(b);

                fos.close();
            } catch (final IOException e) {
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
            }

            DecompilerSettings settings = getDecompilerSettings();

            LuytenTypeLoader typeLoader = new LuytenTypeLoader();
            MetadataSystem metadataSystem = new MetadataSystem(typeLoader);
            TypeReference type = metadataSystem.lookupType(tempClass
                    .getCanonicalPath());

            DecompilationOptions decompilationOptions = new DecompilationOptions();
            decompilationOptions.setSettings(DecompilerSettings.javaDefaults());
            decompilationOptions.setFullDecompilation(true);

            TypeDefinition resolvedType = null;
            if (type == null || ((resolvedType = type.resolve()) == null)) {
                throw new Exception("Unable to resolve type.");
            }
            StringWriter stringwriter = new StringWriter();
            settings.getLanguage().decompileType(resolvedType,
                    new PlainTextOutput(stringwriter), decompilationOptions);
            String decompiledSource = stringwriter.toString();

            return decompiledSource;
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            e.printStackTrace();

            exception = "Bytecode Viewer Version: " + BytecodeViewer.version + BytecodeViewer.nl + BytecodeViewer.nl + sw.toString();
        }
        return "Procyon error! Send the stacktrace to Konloch at http://the.bytecode.club or konloch@gmail.com" + BytecodeViewer.nl + BytecodeViewer.nl + "Suggested Fix: Click refresh class, if it fails again try another decompiler." + BytecodeViewer.nl + BytecodeViewer.nl + exception;
    }

    @Override
    public void decompileToZip(String zipName) {
        File tempZip = new File(BytecodeViewer.tempDirectory
                + BytecodeViewer.fs + "temp.jar");
        if (tempZip.exists())
            tempZip.delete();

        JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(),
                tempZip.getAbsolutePath());

        try {
            doSaveJarDecompiled(tempZip, new File(zipName));
        } catch (Exception e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }
    }

    /**
     * @author DeathMarine
     */
    private void doSaveJarDecompiled(File inFile, File outFile)
            throws Exception {
        try (JarFile jfile = new JarFile(inFile);
             FileOutputStream dest = new FileOutputStream(outFile);
             BufferedOutputStream buffDest = new BufferedOutputStream(dest);
             ZipOutputStream out = new ZipOutputStream(buffDest);) {
            byte data[] = new byte[1024];
            DecompilerSettings settings = getDecompilerSettings();
            LuytenTypeLoader typeLoader = new LuytenTypeLoader();
            MetadataSystem metadataSystem = new MetadataSystem(typeLoader);
            ITypeLoader jarLoader = new JarTypeLoader(jfile);
            typeLoader.getTypeLoaders().add(jarLoader);

            DecompilationOptions decompilationOptions = new DecompilationOptions();
            decompilationOptions.setSettings(settings);
            decompilationOptions.setFullDecompilation(true);

            Enumeration<JarEntry> ent = jfile.entries();
            Set<JarEntry> history = new HashSet<JarEntry>();
            while (ent.hasMoreElements()) {
                JarEntry entry = ent.nextElement();
                if (entry.getName().endsWith(".class")) {
                    JarEntry etn = new JarEntry(entry.getName().replace(
                            ".class", ".java"));
                    if (history.add(etn)) {
                        out.putNextEntry(etn);
                        try {
                            String internalName = StringUtilities.removeRight(
                                    entry.getName(), ".class");
                            TypeReference type = metadataSystem
                                    .lookupType(internalName);
                            TypeDefinition resolvedType = null;
                            if ((type == null)
                                    || ((resolvedType = type.resolve()) == null)) {
                                throw new Exception("Unable to resolve type.");
                            }
                            Writer writer = new OutputStreamWriter(out);
                            settings.getLanguage().decompileType(resolvedType,
                                    new PlainTextOutput(writer),
                                    decompilationOptions);
                            writer.flush();
                        } finally {
                            out.closeEntry();
                        }
                    }
                } else {
                    try {
                        JarEntry etn = new JarEntry(entry.getName());
                        if (history.add(etn))
                            continue;
                        history.add(etn);
                        out.putNextEntry(etn);
                        try {
                            InputStream in = jfile.getInputStream(entry);
                            if (in != null) {
                                try {
                                    int count;
                                    while ((count = in.read(data, 0, 1024)) != -1) {
                                        out.write(data, 0, count);
                                    }
                                } finally {
                                    in.close();
                                }
                            }
                        } finally {
                            out.closeEntry();
                        }
                    } catch (ZipException ze) {
                        // some jar-s contain duplicate pom.xml entries: ignore
                        // it
                        if (!ze.getMessage().contains("duplicate")) {
                            throw ze;
                        }
                    }
                }
            }
        }
    }

    /**
     * @author DeathMarine
     */
    public final class LuytenTypeLoader implements ITypeLoader {
        private final List<ITypeLoader> _typeLoaders;

        public LuytenTypeLoader() {
            _typeLoaders = new ArrayList<ITypeLoader>();
            _typeLoaders.add(new InputTypeLoader());
        }

        public final List<ITypeLoader> getTypeLoaders() {
            return _typeLoaders;
        }

        @Override
        public boolean tryLoadType(final String internalName,
                                   final Buffer buffer) {
            for (final ITypeLoader typeLoader : _typeLoaders) {
                if (typeLoader.tryLoadType(internalName, buffer)) {
                    return true;
                }

                buffer.reset();
            }

            return false;
        }
    }
}
