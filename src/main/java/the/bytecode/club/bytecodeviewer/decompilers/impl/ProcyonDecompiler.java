/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Konloch - Konloch.com / BytecodeViewer.com           *
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

package the.bytecode.club.bytecodeviewer.decompilers.impl;

import com.strobel.assembler.InputTypeLoader;
import com.strobel.assembler.metadata.*;
import com.strobel.core.StringUtilities;
import com.strobel.decompiler.DecompilationOptions;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;
import com.strobel.decompiler.languages.java.JavaFormattingOptions;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.decompilers.AbstractDecompiler;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.util.EncodeUtils;
import the.bytecode.club.bytecodeviewer.util.ExceptionUtils;
import the.bytecode.club.bytecodeviewer.util.TempFile;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import static the.bytecode.club.bytecodeviewer.Constants.*;
import static the.bytecode.club.bytecodeviewer.translation.TranslatedStrings.*;

/**
 * Procyon Java Decompiler Wrapper
 *
 * @author Konloch
 * @author DeathMarine
 */

public class ProcyonDecompiler extends AbstractDecompiler
{

    public ProcyonDecompiler()
    {
        super("Procyon Decompiler", "procyon");
    }

    @Override
    public String decompileClassNode(ClassNode cn, byte[] bytes)
    {
        TempFile tempFile = null;
        String exception;

        try
        {
            //create the temporary files
            tempFile = TempFile.createTemporaryFile(false, ".class");
            File tempInputClassFile = tempFile.getFile();

            //write the ClassNode bytes to the temp file
            try (FileOutputStream fos = new FileOutputStream(tempInputClassFile))
            {
                fos.write(bytes);
            }

            //initialize procyon
            DecompilerSettings settings = getDecompilerSettings();
            LuytenTypeLoader typeLoader = new LuytenTypeLoader();
            MetadataSystem metadataSystem = new MetadataSystem(typeLoader);
            DecompilationOptions decompilationOptions = new DecompilationOptions();
            StringWriter writer = new StringWriter();

            //lookup the class-file
            TypeReference type = metadataSystem.lookupType(tempInputClassFile.getCanonicalPath());

            //configure procyon
            decompilationOptions.setSettings(settings);
            decompilationOptions.setFullDecompilation(true);

            //parse class-file
            TypeDefinition resolvedType;

            if (type == null || ((resolvedType = type.resolve()) == null))
                throw new Exception("Unable to resolve class-filetype.");

            //decompile the class-file
            settings.getLanguage().decompileType(resolvedType, new PlainTextOutput(writer), decompilationOptions);

            //handle simulated errors
            if(Constants.DEV_FLAG_DECOMPILERS_SIMULATED_ERRORS)
                throw new RuntimeException(DEV_MODE_SIMULATED_ERROR.toString());

            //return the writer contents
            return EncodeUtils.unicodeToString(writer.toString());
        }
        catch (Throwable e)
        {
            exception = ExceptionUtils.exceptionToString(e);
        }
        finally
        {
            //delete all temporary files
            if(tempFile != null)
                tempFile.cleanup();
        }

        return PROCYON + " " + ERROR + "! " + ExceptionUI.SEND_STACKTRACE_TO + NL + NL
            + TranslatedStrings.SUGGESTED_FIX_DECOMPILER_ERROR + NL + NL + exception;
    }

    @Override
    public void decompileToZip(String sourceJar, String zipName)
    {
        try
        {
            try (JarFile jarFile = new JarFile(sourceJar);
                 FileOutputStream destination = new FileOutputStream(zipName);
                 BufferedOutputStream buffer = new BufferedOutputStream(destination);
                 ZipOutputStream zip = new ZipOutputStream(buffer))
            {
                byte[] data = new byte[1024];

                //initialize procyon
                DecompilerSettings settings = getDecompilerSettings();
                LuytenTypeLoader typeLoader = new LuytenTypeLoader();
                MetadataSystem metadataSystem = new MetadataSystem(typeLoader);
                ITypeLoader jarLoader = new JarTypeLoader(jarFile);

                //lookup the jar-file
                typeLoader.getTypeLoaders().add(jarLoader);

                //configure procyon
                DecompilationOptions decompilationOptions = new DecompilationOptions();
                decompilationOptions.setSettings(settings);
                decompilationOptions.setFullDecompilation(true);

                //setup jar output
                Enumeration<JarEntry> ent = jarFile.entries();
                Set<JarEntry> history = new HashSet<>();

                while (ent.hasMoreElements())
                {
                    JarEntry entry = ent.nextElement();

                    if (entry.getName().endsWith(".class"))
                    {
                        JarEntry etn = new JarEntry(entry.getName().replace(".class", ".java"));

                        if (history.add(etn))
                        {
                            zip.putNextEntry(etn);

                            try
                            {
                                String internalName = StringUtilities.removeRight(entry.getName(), ".class");
                                TypeReference type = metadataSystem.lookupType(internalName);
                                TypeDefinition resolvedType;

                                if ((type == null) || ((resolvedType = type.resolve()) == null))
                                    throw new Exception("Unable to resolve type.");

                                Writer writer = new OutputStreamWriter(zip);
                                settings.getLanguage().decompileType(resolvedType, new PlainTextOutput(writer), decompilationOptions);
                                writer.flush();
                            }
                            finally
                            {
                                zip.closeEntry();
                            }
                        }
                    }
                    else
                    {
                        try
                        {
                            JarEntry etn = new JarEntry(entry.getName());

                            if (history.add(etn))
                                continue;

                            history.add(etn);
                            zip.putNextEntry(etn);

                            try (InputStream in = jarFile.getInputStream(entry))
                            {
                                if (in != null)
                                {
                                    int count;

                                    while ((count = in.read(data, 0, 1024)) != -1)
                                    {
                                        zip.write(data, 0, count);
                                    }
                                }
                            }
                            finally
                            {
                                zip.closeEntry();
                            }
                        }
                        catch (ZipException ze)
                        {
                            // some jars contain duplicate pom.xml entries: ignore it
                            if (!ze.getMessage().contains("duplicate"))
                                throw ze;
                        }
                    }
                }
            }
        }
        catch (StackOverflowError | Exception e)
        {
            BytecodeViewer.handleException(e);
        }
    }

    public DecompilerSettings getDecompilerSettings()
    {
        DecompilerSettings settings = new DecompilerSettings();
        settings.setAlwaysGenerateExceptionVariableForCatchBlocks(BytecodeViewer.viewer.alwaysGenerateExceptionVars.isSelected());
        settings.setExcludeNestedTypes(BytecodeViewer.viewer.excludeNestedTypes.isSelected());
        settings.setShowDebugLineNumbers(BytecodeViewer.viewer.showDebugLineNumbers.isSelected());
        settings.setIncludeLineNumbersInBytecode(BytecodeViewer.viewer.includeLineNumbersInBytecode.isSelected());
        settings.setIncludeErrorDiagnostics(BytecodeViewer.viewer.includeErrorDiagnostics.isSelected());
        settings.setShowSyntheticMembers(BytecodeViewer.viewer.showSyntheticMembers.isSelected());
        settings.setSimplifyMemberReferences(BytecodeViewer.viewer.simplifyMemberReferences.isSelected());
        settings.setMergeVariables(BytecodeViewer.viewer.mergeVariables.isSelected());
        settings.setForceExplicitTypeArguments(BytecodeViewer.viewer.forceExplicitTypeArguments.isSelected());
        settings.setForceExplicitImports(BytecodeViewer.viewer.forceExplicitImports.isSelected());
        settings.setFlattenSwitchBlocks(BytecodeViewer.viewer.flattenSwitchBlocks.isSelected());
        settings.setRetainPointlessSwitches(BytecodeViewer.viewer.retainPointlessSwitches.isSelected());
        settings.setRetainRedundantCasts(BytecodeViewer.viewer.retainRedunantCasts.isSelected());
        settings.setUnicodeOutputEnabled(BytecodeViewer.viewer.unicodeOutputEnabled.isSelected());
        settings.setJavaFormattingOptions(JavaFormattingOptions.createDefault());
        return settings;
    }

    /**
     * @author DeathMarine
     */
    public static final class LuytenTypeLoader implements ITypeLoader
    {

        private final List<ITypeLoader> typeLoaders;

        public LuytenTypeLoader()
        {
            typeLoaders = new ArrayList<>();
            typeLoaders.add(new InputTypeLoader());
        }

        public List<ITypeLoader> getTypeLoaders()
        {
            return typeLoaders;
        }

        @Override
        public boolean tryLoadType(String internalName, Buffer buffer)
        {
            for (ITypeLoader typeLoader : typeLoaders)
            {
                if (typeLoader.tryLoadType(internalName, buffer))
                    return true;

                buffer.reset();
            }

            return false;
        }

    }

}
