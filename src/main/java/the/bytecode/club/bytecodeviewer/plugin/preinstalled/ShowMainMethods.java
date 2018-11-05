package the.bytecode.club.bytecodeviewer.plugin.preinstalled;

import java.util.ArrayList;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.api.PluginConsole;

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
 * Simply shows all classes that have a public static void main(String[])
 *
 * @author Konloch
 */

public class ShowMainMethods extends Plugin {

    @Override
    public void execute(ArrayList<ClassNode> classNodeList) {
        PluginConsole frame = new PluginConsole("Show Main Methods");
        for (ClassNode classNode : classNodeList) {
            for (Object o : classNode.methods.toArray()) {
                MethodNode m = (MethodNode) o;

                if (m.name.equals("main")
                        && m.desc.equals("([Ljava/lang/String;)V"))
                    frame.appendText(classNode.name + "." + m.name + ""
                            + m.desc);
            }
        }
        frame.setVisible(true);
    }
}
