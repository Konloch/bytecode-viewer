
/*
 * Janino - An embedded Java[TM] compiler
 *
 * Copyright (c) 2001-2010, Arno Unkrig
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *       following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *       following disclaimer in the documentation and/or other materials provided with the distribution.
 *    3. The name of the author may not be used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.codehaus.janino.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.ICompilerFactory;
import org.codehaus.commons.compiler.IExpressionEvaluator;
import org.codehaus.commons.compiler.UncheckedCompileException;
import org.codehaus.janino.Descriptor;
import org.codehaus.janino.ExpressionEvaluator;
import org.codehaus.janino.IClass;
import org.codehaus.janino.IClassLoader;
import org.codehaus.janino.Java;
import org.codehaus.janino.Java.CompilationUnit;
import org.codehaus.janino.Parser;
import org.codehaus.janino.Scanner;
import org.codehaus.janino.UnitCompiler;
import org.codehaus.janino.util.Benchmark;
import org.codehaus.janino.util.ClassFile;
import org.codehaus.janino.util.StringPattern;
import org.codehaus.janino.util.Traverser;
import org.codehaus.janino.util.enumerator.Enumerator;
import org.codehaus.janino.util.iterator.DirectoryIterator;
import org.codehaus.janino.util.resource.PathResourceFinder;


/**
 * Reads a set of compilation units from the file system and searches it for specific
 * Java&trade; constructs, e.g. invocations of a particular method.
 *
 * Usage:
 * <pre>
 * java org.codehaus.janino.JGrep \
 *           [ -dirs <i>directory-name-patterns</i> ] \
 *           [ -files <i>file-name-patterns</i> ] \
 *           { <i>directory-path</i> } \
 *           -method-invocation <i>class.method(arg-types)</i>
 * java org.codehaus.janino.JGrep -help
 * </pre>
 *
 * If "-dirs" is not given, then all <i>directory-path</i>es are scanned for files.
 * The <i>directory-name-patterns</i> work as described in
 * {@link org.codehaus.janino.util.StringPattern#parseCombinedPattern(String)}.
 * <p>
 * If "-files" is not given, then all files ending in ".java" are read. The
 * <i>file-name-patterns</i> work as described in
 * {@link org.codehaus.janino.util.StringPattern#parseCombinedPattern(String)}.
 */
@SuppressWarnings({ "rawtypes", "unchecked" }) public // SUPPRESS CHECKSTYLE HideUtilityClassConstructor
class JGrep {

    private static final boolean DEBUG = false;

    private final List<UnitCompiler> parsedCompilationUnits = new ArrayList();

    /** Command line interface. */
    public static void
    main(String[] args) {
        int idx = 0;

        StringPattern[] directoryNamePatterns     = StringPattern.PATTERNS_ALL;
        StringPattern[] fileNamePatterns          = new StringPattern[] { new StringPattern("*.java") };
        File[]          classPath                 = new File[] { new File(".") };
        File[]          optionalExtDirs           = null;
        File[]          optionalBootClassPath     = null;
        String          optionalCharacterEncoding = null;
        boolean         verbose                   = false;

        for (; idx < args.length; ++idx) {
            String arg = args[idx];
            if (arg.charAt(0) != '-') break;
            if ("-dirs".equals(arg)) {
                directoryNamePatterns = StringPattern.parseCombinedPattern(args[++idx]);
            } else
            if ("-files".equals(arg)) {
                fileNamePatterns = StringPattern.parseCombinedPattern(args[++idx]);
            } else
            if ("-classpath".equals(arg)) {
                classPath = PathResourceFinder.parsePath(args[++idx]);
            } else
            if ("-extdirs".equals(arg)) {
                optionalExtDirs = PathResourceFinder.parsePath(args[++idx]);
            } else
            if ("-bootclasspath".equals(arg)) {
                optionalBootClassPath = PathResourceFinder.parsePath(args[++idx]);
            } else
            if ("-encoding".equals(arg)) {
                optionalCharacterEncoding = args[++idx];
            } else
            if ("-verbose".equals(arg)) {
                verbose = true;
            } else
            if ("-help".equals(arg)) {
                for (String s : JGrep.USAGE) System.out.println(s);
                System.exit(1);
            } else
            {
                System.err.println("Unexpected command-line argument \"" + arg + "\", try \"-help\".");
                System.exit(1);
                return; /* NEVER REACHED */
            }
        }

        // { directory-path }
        File[] rootDirectories;
        {
            int first = idx;
            for (; idx < args.length && args[idx].charAt(0) != '-'; ++idx);
            if (idx == first) {
                System.err.println("No <directory-path>es given, try \"-help\".");
                System.exit(1);
                return; /* NEVER REACHED */
            }
            rootDirectories = new File[idx - first];
            for (int i = first; i < idx; ++i) rootDirectories[i - first] = new File(args[i]);
        }

        // Create the JGrep object.
        final JGrep jGrep = new JGrep(
            classPath,
            optionalExtDirs,
            optionalBootClassPath,
            optionalCharacterEncoding,
            verbose
        );

        List<MethodInvocationTarget> mits = new ArrayList();
        for (; idx < args.length; ++idx) {
            String arg = args[idx];
            if ("-method-invocation".equals(arg)) {
                MethodInvocationTarget mit;
                try {
                    mit = JGrep.parseMethodInvocationPattern(args[++idx]);
                } catch (Exception ex) {
                    System.err.println("Parsing method invocation pattern \"" + args[idx] + "\": " + ex.getMessage());
                    System.exit(1);
                    return; /* NEVER REACHED */
                }
                while (idx < args.length - 1) {
                    arg = args[idx + 1];
                    if (arg.startsWith("predicate:")) {
                        String predicateExpression = arg.substring(10);
                        try {
                            IExpressionEvaluator ee = new ExpressionEvaluator();
                            ee.setClassName(JGrep.class.getName() + "PE");
                            mit.predicates.add((MethodInvocationPredicate) ee.createFastEvaluator(
                                predicateExpression,
                                MethodInvocationPredicate.class,
                                new String[] { "uc", "invocation", "method" }
                            ));
                        } catch (Exception ex) {
                            System.err.println(
                                "Compiling predicate expression \""
                                + predicateExpression
                                + "\": "
                                + ex.getMessage()
                            );
                            System.exit(1);
                            return; /* NEVER REACHED */
                        }
                    } else
                    if (arg.startsWith("action:")) {
                        String action = arg.substring(7);
                        try {
                            mit.actions.add(Action.getMethodInvocationAction(action));
                        } catch (Exception ex) {
                            System.err.println(
                                "Compiling method invocation action \""
                                + action
                                + "\": "
                                + ex.getMessage()
                            );
                            System.exit(1);
                            return; /* NEVER REACHED */
                        }
                    } else
                    {
                        break;
                    }
                    ++idx;
                }
                mits.add(mit);
            } else
            {
                System.err.println("Unexpected command-line argument \"" + arg + "\", try \"-help\".");
                System.exit(1);
                return; /* NEVER REACHED */
            }
        }

        // JGrep the root directories.
        try {
            jGrep.jGrep(
                rootDirectories,
                directoryNamePatterns,
                fileNamePatterns,
                mits                   // methodInvocationTargets
            );
        } catch (Exception e) {
            System.err.println(e.toString());
            System.exit(1);
        }
    }

    private static final
    class Action extends Enumerator {
        private Action(String name) { super(name); }

        static MethodInvocationAction
        getMethodInvocationAction(String action) throws CompileException {
            if ("print-location-and-match".equals(action)) {
                return new MethodInvocationAction() {

                    @Override public void
                    execute(UnitCompiler uc, Java.Invocation invocation, IClass.IMethod method) {
                        System.out.println(invocation.getLocation() + ": " + method);
                    }
                };
            } else
            if ("print-location".equals(action)) {
                return new MethodInvocationAction() {

                    @Override public void
                    execute(UnitCompiler uc, Java.Invocation invocation, IClass.IMethod method) {
                        System.out.println(invocation.getLocation());
                    }
                };
            } else
            {
                ICompilerFactory cf;
                try {
                    cf = CompilerFactoryFactory.getDefaultCompilerFactory();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage()); // SUPPRESS CHECKSTYLE AvoidHidingCause
                }
                return (MethodInvocationAction) cf.newScriptEvaluator().createFastEvaluator(
                    action,                                       // script
                    MethodInvocationAction.class,                 // interfaceToImplement
                    new String[] { "uc", "invocation", "method" } // parameterNames
                );
            }
        }
    }

    private static MethodInvocationTarget
    parseMethodInvocationPattern(String mip) throws CompileException, IOException {
        MethodInvocationTarget mit     = new MethodInvocationTarget();
        Scanner                scanner = new Scanner(null, new StringReader(mip));
        Parser                 parser  = new Parser(scanner);

        for (;;) {
            String s = JGrep.readIdentifierPattern(parser);
            if (parser.peekRead("(")) {
                mit.methodNamePattern = s;
                List<String> l = new ArrayList();
                if (!parser.peekRead(")")) {
                    for (;;) {
                        l.add(JGrep.readIdentifierPattern(parser));
                        if (parser.peek(")")) break;
                        parser.read(",");
                    }
                }
                mit.optionalArgumentTypeNamePatterns = (String[]) l.toArray(new String[l.size()]);
                return mit;
            } else
            if (parser.peekRead(".")) {
                if (mit.optionalClassNamePattern == null) {
                    mit.optionalClassNamePattern = s;
                } else
                {
                    mit.optionalClassNamePattern += '.' + s;
                }
            } else
            if (parser.peekEof()) {
                mit.methodNamePattern = s;
                return mit;
            }
        }
    }

    private static String
    readIdentifierPattern(Parser p) throws CompileException, IOException {
        StringBuilder sb = new StringBuilder();
        if (p.peekRead("*")) {
            sb.append('*');
        } else
        {
            sb.append(p.readIdentifier());
        }
        for (;;) {
            if (p.peekRead("*")) {
                sb.append('*');
            } else
            if (p.peekIdentifier() != null) {
                sb.append(p.readIdentifier());
            } else
            {
                return sb.toString();
            }
        }
    }

    private static
    class MethodInvocationTarget {

        String                          optionalClassNamePattern;
        String                          methodNamePattern;
        String[]                        optionalArgumentTypeNamePatterns;
        List<MethodInvocationPredicate> predicates = new ArrayList();
        List<MethodInvocationAction>    actions    = new ArrayList();

        void
        apply(UnitCompiler uc, Java.Invocation invocation, IClass.IMethod method) throws CompileException {

            // Verify that the class declaring the invoked method matches.
            if (this.optionalClassNamePattern != null) {
                if (!JGrep.typeMatches(
                    this.optionalClassNamePattern,
                    Descriptor.toClassName(method.getDeclaringIClass().getDescriptor())
                )) return;
            }

            // Verify that the name of the invoked method matches.
            if (!new StringPattern(this.methodNamePattern).matches(method.getName())) return;

            // Verify that the parameter count and types of the invoked method match.
            IClass[] fpts = method.getParameterTypes();
            if (this.optionalArgumentTypeNamePatterns != null) {
                String[] atnps = this.optionalArgumentTypeNamePatterns;
                if (atnps.length != fpts.length) return;
                for (int i = 0; i < atnps.length; ++i) {
                    if (!new StringPattern(atnps[i]).matches(Descriptor.toClassName(fpts[i].getDescriptor()))) return;
                }
            }

            // Verify that all predicates (JANINO expressions) return TRUE.
            for (MethodInvocationPredicate mip : this.predicates) {
                try {
                    if (!mip.evaluate(uc, invocation, method)) return;
                } catch (Exception ex) {
                    return; // Treat exception as a "false" predicate.
                }
            }

            // Now that all checks were successful, execute all method invocation actions.
            for (MethodInvocationAction mia : this.actions) {
                try {
                    mia.execute(uc, invocation, method);
                } catch (Exception ex) {
                    ; // Ignore action throwing an exception.
                }
            }
        }
    }

    /** A predicate that examines a method invocation. */
    interface MethodInvocationPredicate {

        /** @return Whether the method incovation met some criterion */
        boolean evaluate(UnitCompiler uc, Java.Invocation invocation, IClass.IMethod method) throws Exception;
    }

    /** An entity that does something with a method invocation, e.g. report where it occurred. */
    interface MethodInvocationAction {

        /** Executes some action for a method invocation. */
        void execute(UnitCompiler uc, Java.Invocation invocation, IClass.IMethod method) throws Exception;
    }

    /**
     * @return Whether the fully qualified {@code typeName} matches the {@code pattern}, or, iff the pattern does not
     *         contain a period, the simple type name of {@code typeName} matches the {@code pattern}
     */
    static boolean
    typeMatches(String pattern, String typeName) {
        return new StringPattern(pattern).matches(
            pattern.indexOf('.') == -1
            ? typeName.substring(typeName.lastIndexOf('.') + 1)
            : typeName
        );
    }

    private static final String[] USAGE = {
        "Usage:",
        "",
        "  java org.codehaus.janino.tools.JGrep [ <option> ... ] <root-dir> ... <pattern> ...",
        "  java org.codehaus.janino.tools.JGrep -help",
        "",
        "Reads a set of compilation units from the files in the <root-dir>s and their",
        "subdirectories and searches them for specific Java[TM] constructs, e.g.",
        "invocations of a particular method.",
        "",
        "Supported <option>s are ('cp' is a 'combined pattern, like '*.java-*Generated*'):",
        "  -dirs <dir-cp>             Ignore subdirectories which don't match",
        "  -files <file-cp>           Include only matching files (default is '*.java')",
        "  -classpath <classpath>",
        "  -extdirs <classpath>",
        "  -bootclasspath <classpath>",
        "  -encoding <encoding>",
        "  -verbose",
        "",
        "Supported <pattern>s are:",
        "  -method-invocation <method-pattern> [ predicate:<predicate-expression> | action:<action-script> ] ...",
        "<method-pattern> is ('<ip>' is an 'identifier pattern' like '*foo*'):",
        "  -method-invocation <method-ip>",
        "  -method-invocation <simple-class-ip>.<method-ip>",
        "  -method-invocation <fully-qualified-class-ip>.<method-ip>",
        "  -method-invocation <method-ip>([<parameter-ip>[,<parameter-ip>]...])",
        "",
        "<predicate-expression> is a Java[TM] expression with the following signature:",
        "  boolean evaluate(UnitCompiler uc, Java.Invocation invocation, IClass.IMethod method)",
        "",
        "<action-script> is either",
        "  print-location-and-match",
        "  print-location",
        ", or a Java[TM] script (method body) with the following signature:",
        "  void execute(UnitCompiler uc, Java.Invocation invocation, IClass.IMethod method)",
    };

    private final IClassLoader iClassLoader;
    private final String       optionalCharacterEncoding;
    private final Benchmark    benchmark;

    public
    JGrep(
        File[]  classPath,
        File[]  optionalExtDirs,
        File[]  optionalBootClassPath,
        String  optionalCharacterEncoding,
        boolean verbose
    ) {
        this(
            org.codehaus.janino.IClassLoader.createJavacLikePathIClassLoader( // iClassLoader
                optionalBootClassPath,
                optionalExtDirs,
                classPath
            ),
            optionalCharacterEncoding,                                        // optionalCharacterEncoding
            verbose                                                           // verbose
        );

        this.benchmark.report("*** JGrep - search Java(TM) source files for specific language constructs");
        this.benchmark.report("*** For more information visit http://janino.codehaus.org");
        this.benchmark.report("Class path",         classPath);
        this.benchmark.report("Ext dirs",           optionalExtDirs);
        this.benchmark.report("Boot class path",    optionalBootClassPath);
        this.benchmark.report("Character encoding", optionalCharacterEncoding);
    }

    public
    JGrep(IClassLoader iClassLoader, final String optionalCharacterEncoding, boolean verbose) {
        this.iClassLoader              = new JGrepIClassLoader(iClassLoader);
        this.optionalCharacterEncoding = optionalCharacterEncoding;
        this.benchmark                 = new Benchmark(verbose);
    }

    private void
    jGrep(
        File[]                       rootDirectories,
        final StringPattern[]        directoryNamePatterns,
        final StringPattern[]        fileNamePatterns,
        List<MethodInvocationTarget> methodInvocationTargets
    ) throws CompileException, IOException {
        this.benchmark.report("Root dirs",               rootDirectories);
        this.benchmark.report("Directory name patterns", directoryNamePatterns);
        this.benchmark.report("File name patterns",      fileNamePatterns);

        this.jGrep(DirectoryIterator.traverseDirectories(
            rootDirectories,              // rootDirectories
            new FilenameFilter() {        // directoryNameFilter
                @Override public boolean
                accept(File dir, String name) { return StringPattern.matches(directoryNamePatterns, name); }
            },
            new FilenameFilter() {        // fileNameFilter
                @Override public boolean
                accept(File dir, String name) { return StringPattern.matches(fileNamePatterns, name); }
            }
        ), methodInvocationTargets);
    }

    private  void
    jGrep(Iterator<File> sourceFilesIterator, final List<MethodInvocationTarget> methodInvocationTargets)
    throws CompileException, IOException {

        // Parse the given source files.
        this.benchmark.beginReporting();
        int sourceFileCount = 0;
        try {

            // Parse all source files.
            while (sourceFilesIterator.hasNext()) {
                File         sourceFile = (File) sourceFilesIterator.next();
                UnitCompiler uc         = new UnitCompiler(this.parseCompilationUnit(
                    sourceFile,                    // sourceFile
                    this.optionalCharacterEncoding // optionalCharacterEncoding
                ), this.iClassLoader);
                this.parsedCompilationUnits.add(uc);
                ++sourceFileCount;
            }
        } finally {
            this.benchmark.endReporting("Parsed " + sourceFileCount + " source file(s)");
        }

        // Traverse the parsed compilation units.
        this.benchmark.beginReporting();
        try {
            for (final UnitCompiler unitCompiler : this.parsedCompilationUnits) {

                CompilationUnit compilationUnit = unitCompiler.getCompilationUnit();
                this.benchmark.beginReporting("Grepping \"" + compilationUnit.optionalFileName + "\"");
                try {
                    new Traverser() {

                        // "method(...)", "x.method(...)"
                        @Override public void
                        traverseMethodInvocation(Java.MethodInvocation mi) {
                            try {
                                this.match(mi, unitCompiler.findIMethod(mi));
                            } catch (CompileException ex) {
                                throw new UncheckedCompileException(ex);
                            }
                            super.traverseMethodInvocation(mi);
                        }

                        // "super.method(...)"
                        @Override public void
                        traverseSuperclassMethodInvocation(Java.SuperclassMethodInvocation scmi) {
                            try {
                                this.match(scmi, unitCompiler.findIMethod(scmi));
                            } catch (CompileException ex) {
                                throw new UncheckedCompileException(ex);
                            }
                            super.traverseSuperclassMethodInvocation(scmi);
                        }

                        // new Xyz(...)
                        @Override public void
                        traverseNewClassInstance(Java.NewClassInstance nci) {
    //                        System.out.println(nci.getLocation() + ": " + nci);
                            super.traverseNewClassInstance(nci);
                        }

                        // new Xyz(...) {}
                        @Override public void
                        traverseNewAnonymousClassInstance(Java.NewAnonymousClassInstance naci) {
    //                        System.out.println(naci.getLocation() + ": " + naci);
                            super.traverseNewAnonymousClassInstance(naci);
                        }

                        // Explicit constructor invocation ("this(...)", "super(...)").
                        @Override public void
                        traverseConstructorInvocation(Java.ConstructorInvocation ci) {
    //                        System.out.println(ci.getLocation() + ": " + ci);
                            super.traverseConstructorInvocation(ci);
                        }

                        private void
                        match(Java.Invocation invocation, IClass.IMethod method) throws CompileException {
                            for (MethodInvocationTarget mit : methodInvocationTargets) {
                                mit.apply(unitCompiler, invocation, method);
                            }
                        }
                    }.traverseCompilationUnit(compilationUnit);
                } catch (UncheckedCompileException uce) {
                    throw uce.compileException; // SUPPRESS CHECKSTYLE AvoidHidingCause
                } finally {
                    this.benchmark.endReporting();
                }
            }
        } finally {
            this.benchmark.endReporting("Traversed " + sourceFileCount + " compilation units");
        }
    }

    /**
     * Read one compilation unit from a file and parse it.
     * <p>
     * The <code>inputStream</code> is closed before the method returns.
     * @return the parsed compilation unit
     */
    private Java.CompilationUnit
    parseCompilationUnit(File sourceFile, String optionalCharacterEncoding) throws CompileException, IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(sourceFile));
        try {
            Parser parser = new Parser(new Scanner(sourceFile.getPath(), is, optionalCharacterEncoding));

            this.benchmark.beginReporting("Parsing \"" + sourceFile + "\"");
            try {
                return parser.parseCompilationUnit();
            } finally {
                this.benchmark.endReporting();
            }
        } finally {
            try { is.close(); } catch (IOException ex) {}
        }
    }

    /**
     * Construct the name of a file that could store the byte code of the class with the given
     * name.
     * <p>
     * If <code>optionalDestinationDirectory</code> is non-null, the returned path is the
     * <code>optionalDestinationDirectory</code> plus the package of the class (with dots replaced
     * with file separators) plus the class name plus ".class". Example:
     * "destdir/pkg1/pkg2/Outer$Inner.class"
     * <p>
     * If <code>optionalDestinationDirectory</code> is null, the returned path is the
     * directory of the <code>sourceFile</code> plus the class name plus ".class". Example:
     * "srcdir/Outer$Inner.class"
     * @param className E.g. "pkg1.pkg2.Outer$Inner"
     * @param sourceFile E.g. "srcdir/Outer.java"
     * @param optionalDestinationDirectory E.g. "destdir"
     */
    public static File
    getClassFile(String className, File sourceFile, File optionalDestinationDirectory) {
        if (optionalDestinationDirectory != null) {
            return new File(optionalDestinationDirectory, ClassFile.getClassFileResourceName(className));
        } else {
            int idx = className.lastIndexOf('.');
            return new File(
                sourceFile.getParentFile(),
                ClassFile.getClassFileResourceName(className.substring(idx + 1))
            );
        }
    }

    /**
     * A specialized {@link IClassLoader} that loads {@link IClass}es from the following
     * sources:
     * <ol>
     *   <li>An already-parsed compilation unit
     *   <li>A class file in the output directory (if existant and younger than source file)
     *   <li>A source file in any of the source path directories
     *   <li>The parent class loader
     * </ol>
     * Notice that the {@link JGrepIClassLoader} is an inner class of {@link JGrep} and
     * heavily uses {@link JGrep}'s members.
     */
    private
    class JGrepIClassLoader extends IClassLoader {

        /**
         * @param optionalParentIClassLoader The {@link IClassLoader} through which {@link IClass}es are to be loaded
         */
        public
        JGrepIClassLoader(IClassLoader optionalParentIClassLoader) {
            super(optionalParentIClassLoader);
            super.postConstruct();
        }

        /** @param type Field descriptor of the {@IClass} to load, e.g. "Lpkg1/pkg2/Outer$Inner;" */
        @Override protected IClass
        findIClass(final String type) {
            if (JGrep.DEBUG) System.out.println("type = " + type);

            // Class type.
            String className = Descriptor.toClassName(type); // E.g. "pkg1.pkg2.Outer$Inner"
            if (JGrep.DEBUG) System.out.println("2 className = \"" + className + "\"");

            // Do not attempt to load classes from package "java".
            if (className.startsWith("java.")) return null;

            // Check the already-parsed compilation units.
            for (int i = 0; i < JGrep.this.parsedCompilationUnits.size(); ++i) {
                UnitCompiler uc  = (UnitCompiler) JGrep.this.parsedCompilationUnits.get(i);
                IClass       res = uc.findClass(className);
                if (res != null) {
                    this.defineIClass(res);
                    return res;
                }
            }
            return null;
        }
    }
}


