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
import the.bytecode.club.bytecodeviewer.DecompilerSettings;
import the.bytecode.club.bytecodeviewer.JarUtils;

import java.io.File;
import java.io.IOException;
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

    public CFRDecompiler() {
        for (Settings setting : Settings.values()) {
            settings.registerSetting(setting);
        }
    }

    @Override
    public DecompilerSettings getSettings() {
        return settings;
    }

    @Override
    public String getName() {
        return "CFR";
    }

    @Override
    public String decompileClassNode(ClassNode cn, byte[] b) {
        try {
            Options options = new GetOptParser().parse(generateMainMethod(), OptionsImpl.getFactory());
            ClassFileSourceImpl classFileSource = new ClassFileSourceImpl(options);
            DCCommonState dcCommonState = new DCCommonState(options, classFileSource);
            return doClass(dcCommonState, b);
        } catch (Exception e) {
            return parseException(e);
        }
    }

    @Override
    public void decompileToZip(String zipName) {
        try {
            Path outputDir = Files.createTempDirectory("cfr_output");
            Path tempJar = Files.createTempFile("cfr_input", ".jar");
            File output = new File(zipName);
            try {
                JarUtils.saveAsJar(BytecodeViewer.getLoadedBytes(), tempJar.toAbsolutePath().toString());
                Options options = new GetOptParser().parse(generateMainMethod(), OptionsImpl.getFactory());
                ClassFileSourceImpl classFileSource = new ClassFileSourceImpl(options);
                DCCommonState dcCommonState = new DCCommonState(options, classFileSource);
                doJar(dcCommonState, tempJar.toAbsolutePath(), outputDir.toAbsolutePath());
                ZipUtil.pack(outputDir.toFile(), output);
            } catch (Exception e) {
                handleException(e);
            } finally {
                try {
                    FileUtils.deleteDirectory(outputDir.toFile());
                } catch (IOException e) {
                    handleException(e);
                }
                try {
                    Files.delete(tempJar);
                } catch (IOException e) {
                    handleException(e);
                }
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public String[] generateMainMethod() {
        String[] result = new String[getSettings().size() * 2 + 1];
        result[0] = "bytecodeviewer";
        int index = 1;
        for (Settings setting : Settings.values()) {
            result[index++] = "--" + setting.getParam();
            result[index++] = String.valueOf(getSettings().isSelected(setting));
        }
        return result;
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

    public enum Settings implements DecompilerSettings.Setting {
        DECODE_ENUM_SWITCH("decodeenumswitch", "Decode Enum Switch", true),
        SUGAR_ENUMS("sugarenums", "SugarEnums", true),
        DECODE_STRING_SWITCH("decodestringswitch", "Decode String Switch", true),
        ARRAYITER("arrayiter", "Arrayiter", true),
        COLLECTIONITER("collectioniter", "Collectioniter", true),
        INNER_CLASSES("innerclasses", "Inner Classes", true),
        REMOVE_BOILER_PLATE("removeboilerplate", "Remove Boiler Plate", true),
        REMOVE_INNER_CLASS_SYNTHETICS("removeinnerclasssynthetics", "Remove Inner Class Synthetics", true),
        DECODE_LAMBDAS("decodelambdas", "Decode Lambdas", true),
        HIDE_BRIDGE_METHODS("hidebridgemethods", "Hide Bridge Methods", true),
        LIFT_CONSTRUCTOR_INIT("liftconstructorinit", "Lift Constructor Init", true),
        REMOVE_DEAD_METHODS("removedeadmethods", "Remove Dead Methods", true),
        REMOVE_BAD_GENERICS("removebadgenerics", "Remove Bad Generics", true),
        SUGAR_ASSERTS("sugarasserts", "Sugar Asserts", true),
        SUGAR_BOXING("sugarboxing", "Sugar Boxing", true),
        SHOW_VERSION("showversion", "Show Version", true),
        DECODE_FINALLY("decodefinally", "Decode Finally", true),
        TIDY_MONITORS("tidymonitors", "Tidy Monitors", true),
        LENIENT("lenient", "Lenient"),
        DUMP_CLASS_PATH("dumpclasspath", "Dump Classpath"),
        COMMENTS("comments", "Comments", true),
        FORCE_TOP_SORT("forcetopsort", "Force Top Sort", true),
        FORCE_TOP_SORT_AGGRESSIVE("forcetopsortaggress", "Force Top Sort Aggressive", true),
        STRINGBUFFER("stringbuffer", "StringBuffer"),
        STRINGBUILDER("stringbuilder", "StringBuilder", true),
        SILENT("silent", "Silent", true),
        RECOVER("recover", "Recover", true),
        ECLIPSE("eclipse", "Eclipse", true),
        OVERRIDE("override", "Override", true),
        SHOW_INFERRABLE("showinferrable", "Show Inferrable", true),
        FORCE_AGGRESSIVE_EXCEPTION_AGG("aexagg", "Force Aggressive Exception Aggregation", true),
        FORCE_COND_PROPAGATE("forcecondpropagate", "Force Conditional Propogation", true),
        HIDE_UTF("hideutf", "Hide UTF", true),
        HIDE_LONG_STRINGS("hidelongstrings", "Hide Long Strings"),
        COMMENT_MONITORS("commentmonitors", "Comment Monitors"),
        ALLOW_CORRECTING("allowcorrecting", "Allow Correcting", true),
        LABELLED_BLOCKS("labelledblocks", "Labelled Blocks", true),
        J14_CLASS_OBJ("j14classobj", "Java 1.4 Class Objects"),
        HIDE_LANG_IMPORTS("hidelangimports", "Hide Lang Imports", true),
        RECOVER_TYPE_CLASH("recovertypeclash", "Recover Type Clash", true),
        RECOVER_TYPE_HINTS("recovertypehints", "Recover Type Hints", true),
        FORCE_RETURNING_IFS("forcereturningifs", "Force Returning Ifs", true),
        FOR_LOOP_AGG_CAPTURE("forloopaggcapture", "For Loop Aggressive Capture", true);

        private String name;
        private String param;
        private boolean on;

        Settings(String param, String name) {
            this(param, name, false);
        }

        Settings(String param, String name, boolean on) {
            this.name = name;
            this.param = param;
            this.on = on;
        }

        public String getText() {
            return name;
        }

        public boolean isDefaultOn() {
            return on;
        }

        public String getParam() {
            return param;
        }
    }
}
