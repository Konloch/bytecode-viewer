package the.bytecode.club.bytecodeviewer.plugin.preinstalled;

import java.util.List;
import org.objectweb.asm.Opcodes;
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
 * @author Sh1ftchg
 */

public class ShowMainMethods extends Plugin
{
    private static final int PUBLIC_STATIC = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;

    @Override
    public void execute(List<ClassNode> classNodeList)
    {
        PluginConsole frame = new PluginConsole("Show Main Methods");
        StringBuilder sb = new StringBuilder();
        
        for (ClassNode classNode : classNodeList)
        {
            for (Object o : classNode.methods.toArray())
            {
                MethodNode m = (MethodNode) o;

                if ((m.access & (PUBLIC_STATIC)) == PUBLIC_STATIC)
                {
                    if (m.name.equals("main") && m.desc.equals("([Ljava/lang/String;)V"))
                    {
                        sb.append(classNode.name);
                        sb.append(".");
                        sb.append(m.name);
                        sb.append(m.desc);
                        sb.append("\n");
                    }
                }
            }
        }

        if (sb.length() == 0)
            frame.appendText("No main methods found.");
        else
            frame.appendText(sb.toString());

        frame.setVisible(true);
    }
}
