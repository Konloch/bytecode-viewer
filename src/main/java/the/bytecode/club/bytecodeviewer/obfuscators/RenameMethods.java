package the.bytecode.club.bytecodeviewer.obfuscators;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.ASMResourceUtil;

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
 * Rename methods.
 *
 * @author Konloch
 */

public class RenameMethods extends JavaObfuscator {

    @Override
    public void obfuscate() {
        int stringLength = getStringLength();

        System.out.println("Obfuscating method names...");
        for (ClassNode c : BytecodeViewer.getLoadedClasses()) {
            for (Object o : c.methods.toArray()) {
                MethodNode m = (MethodNode) o;
                if (m.access != Opcodes.ACC_ABSTRACT
                        && m.access != Opcodes.ACC_ABSTRACT
                        + Opcodes.ACC_STATIC
                        && m.access != Opcodes.ACC_ABSTRACT
                        + Opcodes.ACC_STATIC + Opcodes.ACC_PUBLIC
                        && m.access != Opcodes.ACC_ABSTRACT
                        + Opcodes.ACC_STATIC + Opcodes.ACC_PRIVATE
                        && m.access != Opcodes.ACC_ABSTRACT
                        + Opcodes.ACC_STATIC + Opcodes.ACC_PROTECTED
                        && m.access != Opcodes.ACC_ABSTRACT
                        + Opcodes.ACC_PUBLIC
                        && m.access != Opcodes.ACC_ABSTRACT
                        + Opcodes.ACC_PRIVATE
                        && m.access != Opcodes.ACC_ABSTRACT
                        + Opcodes.ACC_PROTECTED) {
                    if (!m.name.equals("main") && !m.name.equals("<init>")
                            && !m.name.equals("<clinit>")) {
                        String newName = generateUniqueName(stringLength);
                        ASMResourceUtil.renameMethodNode(c.name, m.name, m.desc,
                                null, newName, null);
                    }
                }
            }
        }

        System.out.println("Obfuscated method names.");
    }
}
