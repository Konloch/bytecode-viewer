package the.bytecode.club.bytecodeviewer.compilers;

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

import the.bytecode.club.bytecodeviewer.compilers.impl.JavaCompiler;
import the.bytecode.club.bytecodeviewer.compilers.impl.KrakatauAssembler;
import the.bytecode.club.bytecodeviewer.compilers.impl.SmaliAssembler;

/**
 * A collection of all of the supported compilers/assemblers inside of BCV
 *
 * @author Konloch
 */
public enum Compiler
{
    KRAKATAU_ASSEMBLER(new KrakatauAssembler()),
    SMALI_ASSEMBLER(new SmaliAssembler()),
    JAVA_COMPILER(new JavaCompiler()),
    ;
    
    private final InternalCompiler compiler;
    
    Compiler(InternalCompiler compiler) {this.compiler = compiler;}
    
    public InternalCompiler getCompiler()
    {
        return compiler;
    }
}
