package the.bytecode.club.bytecodeviewer.decompilers;

import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.decompilers.bytecode.ClassNodeDecompiler;

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
 * Used to represent all of the decompilers/disassemblers BCV contains.
 *
 * @author Konloch
 */

public abstract class Decompiler {

    public final static Decompiler bytecode = new ClassNodeDecompiler();
    public final static Decompiler fernflower = new FernFlowerDecompiler();
    public final static Decompiler procyon = new ProcyonDecompiler();
    public final static Decompiler cfr = new CFRDecompiler();
    public final static KrakatauDecompiler krakatau = new KrakatauDecompiler();
    public final static KrakatauDisassembler krakatauDA = new KrakatauDisassembler();
    public final static SmaliDisassembler smali = new SmaliDisassembler();
    public final static Decompiler jdgui = new JDGUIDecompiler();
    public final static Decompiler jadx = new JADXDecompiler();
    public final static Decompiler textifier = new ASMTextifierDecompiler();

    public abstract String decompileClassNode(ClassNode cn, byte[] b);

    public abstract void decompileToZip(String sourceJar, String zipName);
}
