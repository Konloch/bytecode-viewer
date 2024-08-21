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

package the.bytecode.club.bytecodeviewer.obfuscators;

import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.ASMResourceUtil;

/**
 * Rename classes.
 *
 * @author Konloch
 */

public class RenameClasses extends JavaObfuscator {

    @Override
    public void obfuscate() {
        int stringLength = getStringLength();

        System.out.println("Obfuscating class names...");
        for (ClassNode c : BytecodeViewer.getLoadedClasses()) {
            String newName = generateUniqueName(stringLength);
            ASMResourceUtil.renameClassNode(c.name, newName);
            c.name = newName;
        }

        System.out.println("Obfuscated class names.");
    }
}
