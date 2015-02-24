
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

package org.codehaus.janino;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.commons.compiler.ErrorHandler;
import org.codehaus.commons.compiler.Location;
import org.codehaus.commons.compiler.WarningHandler;
import org.codehaus.janino.Java.CompilationUnit;
import org.codehaus.janino.util.ClassFile;
import org.codehaus.janino.util.resource.Resource;
import org.codehaus.janino.util.resource.ResourceFinder;


/**
 * This {@link org.codehaus.janino.IClassLoader} finds, scans and parses compilation units.
 * <p>
 * Notice that it does not compile them!
 */
public
class JavaSourceIClassLoader extends IClassLoader {
    private static final boolean DEBUG = false;

    private ResourceFinder          sourceFinder;
    private String                  optionalCharacterEncoding;
    /** Collection of parsed compilation units. */
    private final Set<UnitCompiler> unitCompilers = new HashSet<UnitCompiler>();

    private ErrorHandler            optionalCompileErrorHandler;
    private WarningHandler          optionalWarningHandler;

    public
    JavaSourceIClassLoader(
        ResourceFinder    sourceFinder,
        String            optionalCharacterEncoding,
        IClassLoader      optionalParentIClassLoader
    ) {
        super(optionalParentIClassLoader);

        this.sourceFinder              = sourceFinder;
        this.optionalCharacterEncoding = optionalCharacterEncoding;
        super.postConstruct();
    }

    /**
     * Returns the set of {@link UnitCompiler}s that were created so far.
     */
    public Set<UnitCompiler>
    getUnitCompilers() { return this.unitCompilers; }

    /** @param pathResourceFinder The source path */
    public void
    setSourceFinder(ResourceFinder pathResourceFinder) {
        this.sourceFinder = pathResourceFinder;
    }

    /**
     * @param optionalCharacterEncoding The name of the charset that is used to read source files, or {@code null} to
     *                                  use the platform's 'default charset'
     */
    public void
    setCharacterEncoding(String optionalCharacterEncoding) {
        this.optionalCharacterEncoding = optionalCharacterEncoding;
    }

    /** @see UnitCompiler#setCompileErrorHandler(ErrorHandler) */
    public void
    setCompileErrorHandler(ErrorHandler optionalCompileErrorHandler) {
        this.optionalCompileErrorHandler = optionalCompileErrorHandler;
    }

    /**
     * @see Parser#setWarningHandler(WarningHandler)
     * @see UnitCompiler#setCompileErrorHandler(ErrorHandler)
     */
    public void
    setWarningHandler(WarningHandler optionalWarningHandler) {
        this.optionalWarningHandler = optionalWarningHandler;
    }

    /**
     * @param fieldDescriptor         Field descriptor of the {@link IClass} to load, e.g. "Lpkg1/pkg2/Outer$Inner;"
     * @throws ClassNotFoundException An exception was raised while loading the {@link IClass}
     */
    @Override public IClass
    findIClass(final String fieldDescriptor) throws ClassNotFoundException {
        if (JavaSourceIClassLoader.DEBUG) System.out.println("type = " + fieldDescriptor);

        // Class type.
        String className = Descriptor.toClassName(fieldDescriptor); // E.g. "pkg1.pkg2.Outer$Inner"
        if (JavaSourceIClassLoader.DEBUG) System.out.println("2 className = \"" + className + "\"");

        // Do not attempt to load classes from package "java".
        if (className.startsWith("java.")) return null;

        // Determine the name of the top-level class.
        String topLevelClassName;
        {
            int idx = className.indexOf('$');
            topLevelClassName = idx == -1 ? className : className.substring(0, idx);
        }

        // Check the already-parsed compilation units.
        for (UnitCompiler uc : this.unitCompilers) {
            IClass res = uc.findClass(topLevelClassName);
            if (res != null) {
                if (!className.equals(topLevelClassName)) {
                    res = uc.findClass(className);
                    if (res == null) return null;
                }
                this.defineIClass(res);
                return res;
            }
        }

        try {
            Java.CompilationUnit cu = this.findCompilationUnit(className);
            if (cu == null) return null;

            UnitCompiler uc = new UnitCompiler(cu, this);
            uc.setCompileErrorHandler(this.optionalCompileErrorHandler);
            uc.setWarningHandler(this.optionalWarningHandler);

            // Remember compilation unit for later compilation.
            this.unitCompilers.add(uc);

            // Find the class/interface declaration in the compiled unit.
            IClass res = uc.findClass(className);
            if (res == null) {
                if (className.equals(topLevelClassName)) {
                    throw new CompileException(
                        "Compilation unit '" + className + "' does not declare a class with the same name",
                        (Location) null
                    );
                }
                return null;
            }
            this.defineIClass(res);
            return res;
        } catch (IOException e) {
            throw new ClassNotFoundException("Parsing compilation unit '" + className + "'", e);
        } catch (CompileException e) {
            throw new ClassNotFoundException("Parsing compilation unit '" + className + "'", e);
        }
    }

    /**
     * Finds the Java&trade; source file for the named class through the configured 'source resource finder' and
     * parses it.
     *
     * @return {@code null} iff the source file could not be found
     */
    protected CompilationUnit
    findCompilationUnit(String className) throws IOException, CompileException {

        // Find source file.
        Resource sourceResource = this.sourceFinder.findResource(ClassFile.getSourceResourceName(className));
        if (sourceResource == null) return null;
        if (JavaSourceIClassLoader.DEBUG) System.out.println("sourceResource=" + sourceResource);

        // Scan and parse the source file.
        InputStream inputStream = sourceResource.open();
        try {
            Scanner scanner = new Scanner(
                sourceResource.getFileName(),
                inputStream,
                this.optionalCharacterEncoding
            );
            scanner.setWarningHandler(this.optionalWarningHandler);

            Parser parser = new Parser(scanner);
            parser.setWarningHandler(this.optionalWarningHandler);

            return parser.parseCompilationUnit();
        } finally {
            try { inputStream.close(); } catch (IOException ex) {}
        }
    }
}
