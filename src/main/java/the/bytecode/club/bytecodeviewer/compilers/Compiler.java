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

package the.bytecode.club.bytecodeviewer.compilers;

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
    JAVA_COMPILER(new JavaCompiler());

    private final AbstractCompiler compiler;

    Compiler(AbstractCompiler compiler)
    {
        this.compiler = compiler;
    }

    public AbstractCompiler getCompiler()
    {
        return compiler;
    }
}
