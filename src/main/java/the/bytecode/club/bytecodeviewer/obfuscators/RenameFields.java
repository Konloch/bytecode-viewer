package the.bytecode.club.bytecodeviewer.obfuscators;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
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
 * Rename fields.
 *
 * @author Konloch
 */

public class RenameFields extends JavaObfuscator {

    @Override
    public void obfuscate() {
        int stringLength = getStringLength();

        System.out.println("Obfuscating fields names...");
        for (ClassNode c : BytecodeViewer.getLoadedClasses()) {
            for (Object o : c.fields.toArray()) {
                FieldNode f = (FieldNode) o;
                String newName = generateUniqueName(stringLength);
                ASMResourceUtil.renameFieldNode(c.name, f.name, f.desc, null,
                        newName, null);
                f.name = newName;
            }
        }

        System.out.println("Obfuscated field names.");
    }
}
