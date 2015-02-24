
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

package org.codehaus.janino.samples;

import java.io.FileReader;
import java.io.IOException;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.Java;
import org.codehaus.janino.Parser;
import org.codehaus.janino.Scanner;
import org.codehaus.janino.util.Traverser;

/**
 * An example application for the {@link org.codehaus.janino.util.Traverser}:
 * Reads, scans and parses the files named on the command line and counts
 * several kinds of declarations.
 */
public
class DeclarationCounter extends Traverser {

    public static void // SUPPRESS CHECKSTYLE JavadocMethod
    main(String[] args) throws CompileException, IOException {
        DeclarationCounter dc = new DeclarationCounter();
        for (String fileName : args) {

            // Parse each compilation unit.
            FileReader           r = new FileReader(fileName);
            Java.CompilationUnit cu;
            try {
                cu = new Parser(new Scanner(fileName, r)).parseCompilationUnit();
            } finally {
                r.close();
            }

            // Traverse it and count declarations.
            dc.traverseCompilationUnit(cu);
        }

        System.out.println("Class declarations:     " + dc.classDeclarationCount);
        System.out.println("Interface declarations: " + dc.interfaceDeclarationCount);
        System.out.println("Fields:                 " + dc.fieldCount);
        System.out.println("Local variables:        " + dc.localVariableCount);
    }

    // Count class declarations.
    @Override public void
    traverseClassDeclaration(Java.ClassDeclaration cd) {
        ++this.classDeclarationCount;
        super.traverseClassDeclaration(cd);
    }
    private int classDeclarationCount;

    // Count interface declarations.
    @Override public void
    traverseInterfaceDeclaration(Java.InterfaceDeclaration id) {
        ++this.interfaceDeclarationCount;
        super.traverseInterfaceDeclaration(id);
    }
    private int interfaceDeclarationCount;

    // Count fields.
    @Override public void
    traverseFieldDeclaration(Java.FieldDeclaration fd) {
        this.fieldCount += fd.variableDeclarators.length;
        super.traverseFieldDeclaration(fd);
    }
    private int fieldCount;

    // Count local variables.
    @Override public void
    traverseLocalVariableDeclarationStatement(Java.LocalVariableDeclarationStatement lvds) {
        this.localVariableCount += lvds.variableDeclarators.length;
        super.traverseLocalVariableDeclarationStatement(lvds);
    }
    private int localVariableCount;
}
