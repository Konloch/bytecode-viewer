package the.bytecode.club.bytecodeviewer.plugin.preinstalled;

import java.util.List;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.api.PluginConsole;

import static the.bytecode.club.bytecodeviewer.Constants.nl;

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
 * Simply shows all the non-empty strings in every single class
 *
 * @author Konloch
 */

public class ShowAllStrings extends Plugin
{
    @Override
    public void execute(List<ClassNode> classNodeList)
    {
        PluginConsole frame = new PluginConsole("Show All Strings");
        StringBuilder sb = new StringBuilder();
        
        for (ClassNode classNode : classNodeList)
        {
            for (Object o : classNode.fields.toArray())
            {
                FieldNode f = (FieldNode) o;
                Object v = f.value;
                
                if (v instanceof String)
                {
                    String s = (String) v;
                    if (!s.isEmpty())
                        sb.append(classNode.name).append(".").append(f.name).append(f.desc).append(" -> \"")
                                .append(s.replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r"))
                                .append("\"").append(nl);
                }
                
                if (v instanceof String[])
                {
                    for (int i = 0; i < ((String[]) v).length; i++)
                    {
                        String s = ((String[]) v)[i];
                        if (!s.isEmpty())
                            sb.append(classNode.name).append(".").append(f.name).append(f.desc).append("[").append(i)
                                    .append("] -> \"").append(s.replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r"))
                                    .append("\"").append(nl);
                    }
                }
            }

            for (Object o : classNode.methods.toArray())
            {
                MethodNode m = (MethodNode) o;
                InsnList iList = m.instructions;
                
                for (AbstractInsnNode a : iList.toArray())
                {
                    if (a instanceof LdcInsnNode)
                    {
                        if (((LdcInsnNode) a).cst instanceof String)
                        {
                            final String s = (String) ((LdcInsnNode) a).cst;
                            if (!s.isEmpty())
                                sb.append(classNode.name).append(".").append(m.name).append(m.desc).append(" -> \"")
                                        .append(s.replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r"))
                                        .append("\"").append(nl);
                        }
                    }
                }
            }
        }
    
        frame.setText(sb.toString());
        frame.setVisible(true);
    }
}
