package the.bytecode.club.bytecodeviewer.obfuscators.rename;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.obfuscators.JavaObfuscator;
import the.bytecode.club.bytecodeviewer.obfuscators.mapping.data.MappingData;
import the.bytecode.club.bytecodeviewer.obfuscators.mapping.data.MethodMappingData;

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

    public static void open()
    {
        if (Configuration.runningObfuscation) {
            BytecodeViewer.showMessage("You're currently running an obfuscation task, wait for this to finish"
                    + ".");
            return;
        }
        new RenameMethods().start();
        BytecodeViewer.viewer.workPane.refreshClass.doClick();
        BytecodeViewer.viewer.resourcePane.tree.updateUI();
    }
    
    @Override
    public void obfuscate() {
        int stringLength = getStringLength();

        System.out.println("Obfuscating method names...");
        for (ClassNode c : BytecodeViewer.getLoadedClasses()) {
            for (Object o : c.methods.toArray()) {
                MethodNode m = (MethodNode) o;

                /* As we dont want to rename native dll methods */
                if ((m.access & Opcodes.ACC_NATIVE) != 0)
                    continue;

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

                        BytecodeViewer.refactorer.getHooks().addMethod(new MethodMappingData(c.name,
                                new MappingData(m.name, newName), m.desc));
						
						/*ASMUtil_OLD.renameMethodNode(c.name, m.name, m.desc,
								null, newName, null);*/
                    }
                }
            }
        }

        System.out.println("Obfuscated method names.");
    }
}
