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

import java.util.ArrayList;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.decompilers.InternalDecompiler;
import the.bytecode.club.bytecodeviewer.decompilers.bytecode.ClassNodeDecompiler;
import the.bytecode.club.bytecodeviewer.decompilers.bytecode.PrefixedStringBuilder;

/**
 * @author Konloch
 * @since 7/3/2021
 */
public class BytecodeDisassembler extends InternalDecompiler
{
	@Override
	public String decompileClassNode(ClassNode cn, byte[] b) {
		return ClassNodeDecompiler.decompile(new PrefixedStringBuilder(),
				new ArrayList<>(), cn).toString();
	}
	
	@Override
	public void decompileToZip(String sourceJar, String zipName) {
	}
}
