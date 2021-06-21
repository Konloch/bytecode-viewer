package the.bytecode.club.bytecodeviewer.obfuscators.mapping;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;

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
 * @author sc4re
 */
public class Refactorer {

    protected HookMap hooks;

    public Refactorer() {
        hooks = new HookMap();
    }

    public HookMap getHooks() {
        return hooks;
    }

    public void run() {
        if (getHooks() == null)
            return;

        RefactorMapper mapper = new RefactorMapper(getHooks());
        //Map<String, ClassNode> refactored = new HashMap<>();
        for (ClassNode cn : BytecodeViewer.getLoadedClasses()) {
            //String oldName = cn.name;
            ClassReader cr = new ClassReader(getClassNodeBytes(cn));
            ClassWriter cw = new ClassWriter(cr, 0);
            RemappingClassAdapter rca = new RemappingClassAdapter(cw, mapper);
            cr.accept(rca, ClassReader.EXPAND_FRAMES);
            cr = new ClassReader(cw.toByteArray());
            cn = new ClassNode();
            cr.accept(cn, 0);
            //refactored.put(oldName, cn);
        }
        /*for (Map.Entry<String, ClassNode> factor : refactored.entrySet()) {
            BytecodeViewer.relocate(factor.getKey(), factor.getValue());
        }*/
        mapper.printMap();
    }

    private byte[] getClassNodeBytes(ClassNode cn) {
        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
