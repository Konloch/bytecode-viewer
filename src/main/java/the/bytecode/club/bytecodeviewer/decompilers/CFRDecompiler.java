package the.bytecode.club.bytecodeviewer.decompilers;

import org.apache.commons.io.FileUtils;
import org.benf.cfr.reader.bytecode.analysis.types.JavaTypeInstance;
import org.benf.cfr.reader.entities.ClassFile;
import org.benf.cfr.reader.entities.Method;
import org.benf.cfr.reader.relationship.MemberNameResolver;
import org.benf.cfr.reader.state.ClassFileSourceImpl;
import org.benf.cfr.reader.state.DCCommonState;
import org.benf.cfr.reader.state.TypeUsageCollector;
import org.benf.cfr.reader.util.CannotLoadClassException;
import org.benf.cfr.reader.util.Functional;
import org.benf.cfr.reader.util.ListFactory;
import org.benf.cfr.reader.util.Predicate;
import org.benf.cfr.reader.util.bytestream.BaseByteData;
import org.benf.cfr.reader.util.getopt.GetOptParser;
import org.benf.cfr.reader.util.getopt.Options;
import org.benf.cfr.reader.util.getopt.OptionsImpl;
import org.benf.cfr.reader.util.output.*;
import org.objectweb.asm.tree.ClassNode;
import org.zeroturnaround.zip.ZipUtil;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.JarUtils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
 * CFR Java Wrapper
 *
 * @author Konloch
 */
public class CFRDecompiler extends Decompiler {

    @Override
    public String decompileClassNode(ClassNode cn, byte[] b) {
        try {
            Options options = new GetOptParser().parse(generateMainMethod(), OptionsImpl.getFactory());
            ClassFileSourceImpl classFileSource = new ClassFileSourceImpl(options);
            DCCommonState dcCommonState = new DCCommonState(options, classFileSource);
            return doClass(dcCommonState, b);
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
            Path outputDir = Files.createTempDirectory("cfr_output");
            Path tempJar = Files.createTempFile("cfr_input", ".jar");
            File output = new File(zipName);
            JarUtils.saveAsJar(BytecodeViewer.getLoadedBytes(), tempJar.toAbsolutePath().toString());
            Options options = new GetOptParser().parse(generateMainMethod(), OptionsImpl.getFactory());
            ClassFileSourceImpl classFileSource = new ClassFileSourceImpl(options);
            DCCommonState dcCommonState = new DCCommonState(options, classFileSource);
            doJar(dcCommonState, tempJar.toAbsolutePath(), outputDir.toAbsolutePath());
            ZipUtil.pack(outputDir.toFile(), output);
            FileUtils.deleteDirectory(outputDir.toFile());
            Files.delete(tempJar);
        } catch (Exception e) {
            e.printStackTrace(); //TODO How to handle exceptions again?
        }
    }

    public String[] generateMainMethod() {
        return new String[]{
                "bytecodeviewer",
                "--decodeenumswitch",
                String.valueOf(BytecodeViewer.viewer.decodeenumswitch
                        .isSelected()),
                "--sugarenums",
                String.valueOf(BytecodeViewer.viewer.sugarenums.isSelected()),
                "--decodestringswitch",
                String.valueOf(BytecodeViewer.viewer.decodestringswitch
                        .isSelected()),
                "--arrayiter",
                String.valueOf(BytecodeViewer.viewer.arrayiter.isSelected()),
                "--collectioniter",
                String.valueOf(BytecodeViewer.viewer.collectioniter
                        .isSelected()),
                "--innerclasses",
                String.valueOf(BytecodeViewer.viewer.innerclasses.isSelected()),
                "--removeboilerplate",
                String.valueOf(BytecodeViewer.viewer.removeboilerplate
                        .isSelected()),
                "--removeinnerclasssynthetics",
                String.valueOf(BytecodeViewer.viewer.removeinnerclasssynthetics
                        .isSelected()),
                "--decodelambdas",
                String.valueOf(BytecodeViewer.viewer.decodelambdas.isSelected()),
                "--hidebridgemethods",
                String.valueOf(BytecodeViewer.viewer.hidebridgemethods
                        .isSelected()),
                "--liftconstructorinit",
                String.valueOf(BytecodeViewer.viewer.liftconstructorinit
                        .isSelected()),
                "--removedeadmethods",
                String.valueOf(BytecodeViewer.viewer.removedeadmethods
                        .isSelected()),
                "--removebadgenerics",
                String.valueOf(BytecodeViewer.viewer.removebadgenerics
                        .isSelected()),
                "--sugarasserts",
                String.valueOf(BytecodeViewer.viewer.sugarasserts.isSelected()),
                "--sugarboxing",
                String.valueOf(BytecodeViewer.viewer.sugarboxing.isSelected()),
                "--showversion",
                String.valueOf(BytecodeViewer.viewer.showversion.isSelected()),
                "--decodefinally",
                String.valueOf(BytecodeViewer.viewer.decodefinally.isSelected()),
                "--tidymonitors",
                String.valueOf(BytecodeViewer.viewer.tidymonitors.isSelected()),
                "--lenient",
                String.valueOf(BytecodeViewer.viewer.lenient.isSelected()),
                "--dumpclasspath",
                String.valueOf(BytecodeViewer.viewer.dumpclasspath.isSelected()),
                "--comments",
                String.valueOf(BytecodeViewer.viewer.comments.isSelected()),
                "--forcetopsort",
                String.valueOf(BytecodeViewer.viewer.forcetopsort.isSelected()),
                "--forcetopsortaggress",
                String.valueOf(BytecodeViewer.viewer.forcetopsortaggress
                        .isSelected()),
                "--stringbuffer",
                String.valueOf(BytecodeViewer.viewer.stringbuffer.isSelected()),
                "--stringbuilder",
                String.valueOf(BytecodeViewer.viewer.stringbuilder.isSelected()),
                "--silent",
                String.valueOf(BytecodeViewer.viewer.silent.isSelected()),
                "--recover",
                String.valueOf(BytecodeViewer.viewer.recover.isSelected()),
                "--eclipse",
                String.valueOf(BytecodeViewer.viewer.eclipse.isSelected()),
                "--override",
                String.valueOf(BytecodeViewer.viewer.override.isSelected()),
                "--showinferrable",
                String.valueOf(BytecodeViewer.viewer.showinferrable
                        .isSelected()),
                "--aexagg",
                String.valueOf(BytecodeViewer.viewer.aexagg.isSelected()),
                "--forcecondpropagate",
                String.valueOf(BytecodeViewer.viewer.forcecondpropagate
                        .isSelected()),
                "--hideutf",
                String.valueOf(BytecodeViewer.viewer.hideutf.isSelected()),
                "--hidelongstrings",
                String.valueOf(BytecodeViewer.viewer.hidelongstrings
                        .isSelected()),
                "--commentmonitors",
                String.valueOf(BytecodeViewer.viewer.commentmonitor
                        .isSelected()),
                "--allowcorrecting",
                String.valueOf(BytecodeViewer.viewer.allowcorrecting
                        .isSelected()),
                "--labelledblocks",
                String.valueOf(BytecodeViewer.viewer.labelledblocks
                        .isSelected()),
                "--j14classobj",
                String.valueOf(BytecodeViewer.viewer.j14classobj.isSelected()),
                "--hidelangimports",
                String.valueOf(BytecodeViewer.viewer.hidelangimports
                        .isSelected()),
                "--recovertypeclash",
                String.valueOf(BytecodeViewer.viewer.recoverytypeclash
                        .isSelected()),
                "--recovertypehints",
                String.valueOf(BytecodeViewer.viewer.recoverytypehints
                        .isSelected()),
                "--forcereturningifs",
                String.valueOf(BytecodeViewer.viewer.forceturningifs
                        .isSelected()),
                "--forloopaggcapture",
                String.valueOf(BytecodeViewer.viewer.forloopaggcapture
                        .isSelected()),};
    }

    public static String doClass(DCCommonState dcCommonState, byte[] content1) throws Exception {
        Options options = dcCommonState.getOptions();
        Dumper d = new ToStringDumper();
        BaseByteData data = new BaseByteData(content1);
        ClassFile var24 = new ClassFile(data, "", dcCommonState);
        dcCommonState.configureWith(var24);

        try {
            var24 = dcCommonState.getClassFile(var24.getClassType());
        } catch (CannotLoadClassException var18) {
        }

        if (options.getOption(OptionsImpl.DECOMPILE_INNER_CLASSES)) {
            var24.loadInnerClasses(dcCommonState);
        }

        if (options.getOption(OptionsImpl.RENAME_MEMBERS)) {
            MemberNameResolver.resolveNames(dcCommonState, ListFactory.newList(dcCommonState.getClassCache().getLoadedTypes()));
        }

        var24.analyseTop(dcCommonState);
        TypeUsageCollector var25 = new TypeUsageCollector(var24);
        var24.collectTypeUsages(var25);
        String var26 = options.getOption(OptionsImpl.METHODNAME);
        if (var26 == null) {
            var24.dump(d);
        } else {
            try {
                for (Method method : var24.getMethodByName(var26)) {
                    method.dump(d, true);
                }
            } catch (NoSuchMethodException var19) {
                throw new IllegalArgumentException("No such method \'" + var26 + "\'.");
            }
        }
        d.print("");
        return d.toString();
    }

    public static void doJar(DCCommonState dcCommonState, Path input, Path output) throws Exception {
        SummaryDumper summaryDumper = new NopSummaryDumper();
        Dumper d = new ToStringDumper();
        Options options = dcCommonState.getOptions();
        IllegalIdentifierDump illegalIdentifierDump = IllegalIdentifierDump.Factory.get(options);

        final Predicate e = org.benf.cfr.reader.util.MiscUtils.mkRegexFilter(options.getOption(OptionsImpl.JAR_FILTER), true);

        List<JavaTypeInstance> err1 = dcCommonState.explicitlyLoadJar(input.toAbsolutePath().toString());
        err1 = Functional.filter(err1, new Predicate<JavaTypeInstance>() {
            public boolean test(JavaTypeInstance in) {
                return e.test(in.getRawName());
            }
        });
        if (options.getOption(OptionsImpl.RENAME_MEMBERS)) {
            MemberNameResolver.resolveNames(dcCommonState, err1);
        }

        for (JavaTypeInstance type : err1) {
            try {
                ClassFile e1 = dcCommonState.getClassFile(type);
                if (e1.isInnerClass()) {
                    d = null;
                } else {
                    if (options.getOption(OptionsImpl.DECOMPILE_INNER_CLASSES).booleanValue()) {
                        e1.loadInnerClasses(dcCommonState);
                    }

                    e1.analyseTop(dcCommonState);
                    TypeUsageCollector collectingDumper = new TypeUsageCollector(e1);
                    e1.collectTypeUsages(collectingDumper);
                    d = new FileDumper(output.toAbsolutePath().toString(), e1.getClassType(), summaryDumper, collectingDumper.getTypeUsageInformation(), options, illegalIdentifierDump);
                    e1.dump(d);
                    d.print("\n");
                    d.print("\n");
                }
            } catch (Dumper.CannotCreate var25) {
                throw var25;
            } catch (RuntimeException var26) {
                d.print(var26.toString()).print("\n").print("\n").print("\n");
            } finally {
                if (d != null) {
                    d.close();
                }
            }
        }
    }
}
