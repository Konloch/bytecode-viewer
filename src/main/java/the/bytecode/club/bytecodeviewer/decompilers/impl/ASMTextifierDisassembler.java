package the.bytecode.club.bytecodeviewer.decompilers.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;
import the.bytecode.club.bytecodeviewer.decompilers.InternalDecompiler;

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
 * Objectweb ASM Textifier output
 *
 * @author Thiakil
 */
public class ASMTextifierDisassembler extends InternalDecompiler
{
    @Override
    public String decompileClassNode(ClassNode cn, byte[] b) {
        StringWriter writer = new StringWriter();
        cn.accept(new TraceClassVisitor(null, new Textifier(), new PrintWriter(writer)));
        return writer.toString();
    }

    @Override
    public void decompileToZip(String sourceJar, String zipName) {

    }
}
