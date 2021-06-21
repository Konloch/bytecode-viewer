package the.bytecode.club.bytecodeviewer.obfuscators.rename;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.obfuscators.JavaObfuscator;
import the.bytecode.club.bytecodeviewer.obfuscators.mapping.data.MappingData;

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
 * Rename classes.
 *
 * @author Konloch
 */

public class RenameClasses extends JavaObfuscator {

    public static void open()
    {
        if (Configuration.runningObfuscation) {
            BytecodeViewer.showMessage("You're currently running an obfuscation task, wait for this to finish"
                    + ".");
            return;
        }
        new RenameClasses().start();
        BytecodeViewer.viewer.workPane.refreshClass.doClick();
        BytecodeViewer.viewer.resourcePane.tree.updateUI();
    }
    
    @Override
    public void obfuscate() {
        int stringLength = 5;//getStringLength();

        System.out.println("Obfuscating class names...");
        classLoop:
        for (ClassNode c : BytecodeViewer.getLoadedClasses()) {

            /* As we dont want to rename classes that contain native dll methods */
            for (MethodNode o : c.methods) {
                /* As we dont want to rename any  main-classes */
                if (o.name.equals("main") && o.desc.equals("([Ljava/lang/String;)V")
                        || o.name.equals("init") && c.superName.equals("java/applet/Applet"))
                    continue classLoop;

                /* As we dont want to rename native dll methods */
                if ((o.access & Opcodes.ACC_NATIVE) != 0)
                    continue classLoop;
            }

            String newName = generateUniqueName(stringLength);

            BytecodeViewer.refactorer.getHooks().addClass(new MappingData(c.name, newName));
			
			/*ASMUtil_OLD.renameClassNode(c.name, newName);
			c.name = newName;*/
        }

        System.out.println("Obfuscated class names.");
    }
}
