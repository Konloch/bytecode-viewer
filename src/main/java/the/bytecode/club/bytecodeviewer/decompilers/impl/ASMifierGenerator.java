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

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;
import the.bytecode.club.bytecodeviewer.decompilers.AbstractDecompiler;
import the.bytecode.club.bytecodeviewer.util.JavaFormatterUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Objectweb ASMifier output
 *
 * @author Nick Botticelli
 */
public class ASMifierGenerator extends AbstractDecompiler
{
    public ASMifierGenerator()
    {
        super("ASMifier Generator", "asmifier");
    }

    @Override
    public String decompileClassNode(ClassNode cn, byte[] bytes)
    {
        StringWriter writer = new StringWriter();
        cn.accept(new TraceClassVisitor(null, new ASMifier(), new PrintWriter(writer)));
        return JavaFormatterUtils.formatJavaCode(writer.toString());
    }

    @Override
    public void decompileToZip(String sourceJar, String zipName)
    {
    }
}
