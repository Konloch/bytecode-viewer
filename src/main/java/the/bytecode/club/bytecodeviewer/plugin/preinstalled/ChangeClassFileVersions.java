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

package the.bytecode.club.bytecodeviewer.plugin.preinstalled;

import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.Plugin;

import java.util.List;

/**
 * As long as there are no new opcodes or API changes you can use this plugin to downgrade compiled code
 * <p>
 * 1) Import a JDK-11 (or higher) Jar resource inside of BCV
 * 2) Run this plugin
 * 3) Export as ZIP, then rename as Jar - Your ClassFiles will now run on JDK-8 (or whatever you selected)
 *
 * @author Konloch
 * @since 07/11/2021
 */
public class ChangeClassFileVersions extends Plugin
{
    @Override
    public void execute(List<ClassNode> classNodeList)
    {
        //prompt dialog for version number
        // TODO: include a little diagram of what JDK is which number
        String input = BytecodeViewer.showInput("Class Version Number: (52 = JDK 8)");

        if(input == null)
            return;

        int newVersion = Integer.parseInt(input);

        //update the ClassFile version
        classNodeList.forEach(classNode -> classNode.version = newVersion);

        //update the the container's resource byte-arrays
        BytecodeViewer.updateAllClassNodeByteArrays();

        //force refresh all tabs (this forces the decompilers to run with the latest resource data)
        BytecodeViewer.refreshAllTabs();

        //alert the changes to the user
        BytecodeViewer.showMessage("Set all of the class versions to " + newVersion);
    }
}
