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

import org.apache.commons.io.IOUtils;
import org.benf.cfr.reader.api.CfrDriver;
import org.benf.cfr.reader.api.ClassFileSource;
import org.benf.cfr.reader.api.OutputSinkFactory;
import org.benf.cfr.reader.api.SinkReturns;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.Pair;
import org.benf.cfr.reader.state.ClassFileSourceImpl;
import org.benf.cfr.reader.util.getopt.Options;
import org.benf.cfr.reader.util.getopt.OptionsImpl;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.decompilers.AbstractDecompiler;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.util.ExceptionUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import static the.bytecode.club.bytecodeviewer.Constants.NL;
import static the.bytecode.club.bytecodeviewer.translation.TranslatedStrings.*;

/**
 * CFR Java Wrapper
 *
 * @author GraxCode (Taken mostly out of Threadtear)
 */

public class CFRDecompiler extends AbstractDecompiler
{

    private static final String CLASS_SUFFIX = ".class";

    public CFRDecompiler()
    {
        super("CFR Decompiler", "cfr");
    }

    @Override
    public String decompileClassNode(ClassNode cn, byte[] bytes)
    {
        return decompile(cn, cn.name, bytes);
    }

    private String decompile(ClassNode cn, String name, byte[] content)
    {
        String exception;

        try
        {
            String classPath = name + (name.endsWith(CLASS_SUFFIX) ? "" : CLASS_SUFFIX);
            StringBuilder builder = new StringBuilder();
            Consumer<SinkReturns.Decompiled> dumpDecompiled = d -> builder.append(d.getJava());

            //initialize CFR
            Options options = generateOptions();
            ClassFileSource source = new BCVDataSource(options, cn, classPath, content);
            CfrDriver driver = new CfrDriver.Builder().withClassFileSource(source).withBuiltOptions(options).withOutputSink(new BCVOutputSinkFactory(dumpDecompiled)).build();

            //decompile the class-file
            driver.analyse(Collections.singletonList(name));

            //handle simulated errors
            if(Constants.DEV_FLAG_DECOMPILERS_SIMULATED_ERRORS)
                throw new RuntimeException(DEV_MODE_SIMULATED_ERROR.toString());

            //return the builder contents
            return builder.toString();
        }
        catch (Throwable e)
        {
            exception = ExceptionUtils.exceptionToString(e);
        }

        return CFR + " " + ERROR + "! " + ExceptionUI.SEND_STACKTRACE_TO + NL + NL
            + TranslatedStrings.SUGGESTED_FIX_DECOMPILER_ERROR + NL + NL + exception;
    }

    @Override
    public void decompileToZip(String sourceJar, String outJar)
    {
        try (JarFile jarFile = new JarFile(new File(sourceJar));
             FileOutputStream destination = new FileOutputStream(outJar);
             BufferedOutputStream buffer = new BufferedOutputStream(destination);
             ZipOutputStream zip = new ZipOutputStream(buffer))
        {
            byte[] data = new byte[1024];
            Enumeration<JarEntry> ent = jarFile.entries();
            Set<JarEntry> history = new HashSet<>();

            while (ent.hasMoreElements())
            {
                JarEntry entry = ent.nextElement();

                if (entry.getName().endsWith(CLASS_SUFFIX))
                {
                    JarEntry etn = new JarEntry(entry.getName().replace(CLASS_SUFFIX, ".java"));

                    if (history.add(etn))
                    {
                        zip.putNextEntry(etn);

                        try
                        {
                            IOUtils.write(decompile(null, entry.getName(), IOUtils.toByteArray(jarFile.getInputStream(entry))), zip, StandardCharsets.UTF_8);
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
                        JarEntry jarEntry = new JarEntry(entry.getName());

                        if (history.add(jarEntry))
                            continue;

                        history.add(jarEntry);
                        zip.putNextEntry(jarEntry);

                        try (InputStream input = jarFile.getInputStream(entry))
                        {
                            if (input != null)
                            {
                                int count;

                                while ((count = input.read(data, 0, 1024)) != -1)
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
                    catch (ZipException e)
                    {
                        // some jars contain duplicate pom.xml entries: ignore it
                        if (!e.getMessage().contains("duplicate"))
                            throw e;
                    }
                }
            }
        }
        catch (StackOverflowError | Exception e)
        {
            BytecodeViewer.handleException(e);
        }
    }

    public Options generateOptions()
    {
        Map<String, String> options = new HashMap<>();
        options.put("decodeenumswitch", String.valueOf(BytecodeViewer.viewer.decodeEnumSwitch.isSelected()));
        options.put("sugarenums", String.valueOf(BytecodeViewer.viewer.sugarEnums.isSelected()));
        options.put("decodestringswitch", String.valueOf(BytecodeViewer.viewer.decodeStringSwitch.isSelected()));
        options.put("arrayiter", String.valueOf(BytecodeViewer.viewer.arrayiter.isSelected()));
        options.put("collectioniter", String.valueOf(BytecodeViewer.viewer.collectioniter.isSelected()));
        options.put("innerclasses", String.valueOf(BytecodeViewer.viewer.innerClasses.isSelected()));
        options.put("removeboilerplate", String.valueOf(BytecodeViewer.viewer.removeBoilerPlate.isSelected()));
        options.put("removeinnerclasssynthetics", String.valueOf(BytecodeViewer.viewer.removeInnerClassSynthetics.isSelected()));
        options.put("decodelambdas", String.valueOf(BytecodeViewer.viewer.decodeLambdas.isSelected()));
        options.put("hidebridgemethods", String.valueOf(BytecodeViewer.viewer.hideBridgeMethods.isSelected()));
        options.put("liftconstructorinit", String.valueOf(BytecodeViewer.viewer.liftConstructorInit.isSelected()));
        options.put("removebadgenerics", String.valueOf(BytecodeViewer.viewer.removeBadGenerics.isSelected()));
        options.put("sugarasserts", String.valueOf(BytecodeViewer.viewer.sugarAsserts.isSelected()));
        options.put("sugarboxing", String.valueOf(BytecodeViewer.viewer.sugarBoxing.isSelected()));
        options.put("showversion", String.valueOf(BytecodeViewer.viewer.showVersion.isSelected()));
        options.put("decodefinally", String.valueOf(BytecodeViewer.viewer.decodeFinally.isSelected()));
        options.put("tidymonitors", String.valueOf(BytecodeViewer.viewer.tidyMonitors.isSelected()));
        options.put("lenient", String.valueOf(BytecodeViewer.viewer.lenient.isSelected()));
        options.put("dumpclasspath", String.valueOf(BytecodeViewer.viewer.dumpClassPath.isSelected()));
        options.put("comments", String.valueOf(BytecodeViewer.viewer.comments.isSelected()));
        options.put("forcetopsort", String.valueOf(BytecodeViewer.viewer.forceTopSort.isSelected()));
        options.put("forcetopsortaggress", String.valueOf(BytecodeViewer.viewer.forceTopSortAggress.isSelected()));
        options.put("stringbuffer", String.valueOf(BytecodeViewer.viewer.stringBuffer.isSelected()));
        options.put("stringbuilder", String.valueOf(BytecodeViewer.viewer.stringBuilder.isSelected()));
        options.put("silent", String.valueOf(BytecodeViewer.viewer.silent.isSelected()));
        options.put("recover", String.valueOf(BytecodeViewer.viewer.recover.isSelected()));
        options.put("eclipse", String.valueOf(BytecodeViewer.viewer.eclipse.isSelected()));
        options.put("override", String.valueOf(BytecodeViewer.viewer.override.isSelected()));
        options.put("showinferrable", String.valueOf(BytecodeViewer.viewer.showInferrable.isSelected()));
        options.put("aexagg", String.valueOf(BytecodeViewer.viewer.aexagg.isSelected()));
        options.put("hideutf", String.valueOf(BytecodeViewer.viewer.hideUTF.isSelected()));
        options.put("hidelongstrings", String.valueOf(BytecodeViewer.viewer.hideLongStrings.isSelected()));
        options.put("commentmonitors", String.valueOf(BytecodeViewer.viewer.commentMonitor.isSelected()));
        options.put("allowcorrecting", String.valueOf(BytecodeViewer.viewer.allowCorrecting.isSelected()));
        options.put("labelledblocks", String.valueOf(BytecodeViewer.viewer.labelledBlocks.isSelected()));
        options.put("j14classobj", String.valueOf(BytecodeViewer.viewer.j14ClassOBJ.isSelected()));
        options.put("hidelangimports", String.valueOf(BytecodeViewer.viewer.hideLangImports.isSelected()));
        options.put("recovertypehints", String.valueOf(BytecodeViewer.viewer.recoveryTypehInts.isSelected()));
        options.put("forcereturningifs", String.valueOf(BytecodeViewer.viewer.forceTurningIFs.isSelected()));
        options.put("forloopaggcapture", String.valueOf(BytecodeViewer.viewer.forLoopAGGCapture.isSelected()));

        return new OptionsImpl(options);
    }

    private static class BCVDataSource extends ClassFileSourceImpl
    {

        private final ResourceContainer container;
        private final String classFilePath;
        private final byte[] content;

        private BCVDataSource(Options options, ClassNode cn, String classFilePath, byte[] content)
        {
            super(options);
            this.container = BytecodeViewer.getResourceContainers()
                .stream().filter(rc -> rc.resourceClasses.containsValue(cn)).findFirst().orElse(null);
            this.classFilePath = classFilePath;
            this.content = content;
        }

        @Override
        public Pair<byte[], String> getClassFileContent(String classFilePath) throws IOException
        {
            if (classFilePath.equals(this.classFilePath) && content != null)
                return Pair.make(content, classFilePath);

            if (container == null)
                return super.getClassFileContent(classFilePath);

            byte[] data = container.resourceClassBytes.get(classFilePath);

            if (data == null)
                return super.getClassFileContent(classFilePath);

            return Pair.make(data, classFilePath);
        }
    }

    private static class BCVOutputSinkFactory implements OutputSinkFactory
    {

        private final Consumer<SinkReturns.Decompiled> dumpDecompiled;

        private BCVOutputSinkFactory(Consumer<SinkReturns.Decompiled> dumpDecompiled)
        {
            this.dumpDecompiled = dumpDecompiled;
        }

        @Override
        public List<SinkClass> getSupportedSinks(SinkType sinkType, Collection<SinkClass> available)
        {
            return Collections.singletonList(SinkClass.DECOMPILED);
        }

        @Override
        public <T> Sink<T> getSink(SinkType sinkType, SinkClass sinkClass)
        {
            if (sinkType == SinkType.JAVA && sinkClass == SinkClass.DECOMPILED)
            {
                return x -> dumpDecompiled.accept((SinkReturns.Decompiled) x);
            }

            return ignore -> {};
        }

    }

}
