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
 * Replaces all string and string[] instances with whatever.
 *
 * @author Konloch
 */

public class ReplaceStrings extends Plugin
{
    PluginConsole frame;
    String originalLDC;
    String newLDC;
    String className;
    boolean contains;

    public ReplaceStrings(String originalLDC, String newLDC, String className, boolean contains)
    {
        this.originalLDC = originalLDC;
        this.newLDC = newLDC;
        this.className = className;
        this.contains = contains;
    }

    @Override
    public void execute(List<ClassNode> classNodeList)
    {
        frame = new PluginConsole("Replace Strings");
        
        if (!className.equals("*"))
        {
            for (ClassNode classNode : classNodeList)
                if (classNode.name.equals(className))
                    scanClassNode(classNode);
        }
        else
        {
            for (ClassNode classNode : classNodeList)
                scanClassNode(classNode);
        }
        
        frame.setVisible(true);
    }

    public void scanClassNode(ClassNode classNode)
    {
        for (Object o : classNode.fields.toArray())
        {
            FieldNode f = (FieldNode) o;
            Object v = f.value;
            if (v instanceof String)
            {
                String s = (String) v;
                if (contains)
                {
                    if (s.contains(originalLDC))
                        f.value = ((String) f.value).replaceAll(originalLDC, newLDC);
                }
                else
                {
                    if (s.equals(originalLDC))
                        f.value = newLDC;
                }
            }
            
            if (v instanceof String[])
            {
                for (int i = 0; i < ((String[]) v).length; i++)
                {
                    String s = ((String[]) v)[i];
                    if (contains)
                    {
                        if (s.contains(originalLDC))
                        {
                            f.value = ((String[]) f.value)[i].replaceAll(originalLDC, newLDC);
                            String ugh = s.replaceAll("\\n", "\\\\n")
                                    .replaceAll("\\r", "\\\\r");
                            frame.appendText(classNode.name + "." + f.name + ""
                                    + f.desc + " -> \"" + ugh + "\" replaced with \""
                                    + s.replaceAll(originalLDC, newLDC) + "\"");
                        }
                    }
                    else
                    {
                        if (s.equals(originalLDC))
                        {
                            ((String[]) f.value)[i] = newLDC;
                            String ugh = s.replaceAll("\\n", "\\\\n")
                                    .replaceAll("\\r", "\\\\r");
                            frame.appendText(classNode.name + "." + f.name + ""
                                    + f.desc + " -> \"" + ugh + "\" replaced with \"" + newLDC + "\"");
                        }
                    }
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
                        if (contains)
                        {
                            if (s.contains(originalLDC))
                            {
                                ((LdcInsnNode) a).cst = ((String) ((LdcInsnNode) a).cst)
                                        .replaceAll(originalLDC, newLDC);
                                String ugh = s.replaceAll("\\n", "\\\\n")
                                        .replaceAll("\\r", "\\\\r");
                                frame.appendText(classNode.name + "." + m.name + "" + m.desc
                                        + " -> \"" + ugh + "\" replaced with \""
                                        + s.replaceAll(originalLDC, newLDC)
                                        .replaceAll("\\n", "\\\\n")
                                        .replaceAll("\\r", "\\\\r")
                                        + "\"");
                            }
                        }
                        else
                        {
                            if (s.equals(originalLDC))
                            {
                                ((LdcInsnNode) a).cst = newLDC;
                                String ugh = s.replaceAll("\\n", "\\\\n")
                                        .replaceAll("\\r", "\\\\r");
                                frame.appendText(classNode.name + "." + m.name + "" + m.desc
                                        + " -> \"" + ugh + "\" replaced with \""
                                        + newLDC.replaceAll("\\n", "\\\\n")
                                        .replaceAll("\\r", "\\\\r")
                                        + "\"");
                            }
                        }
                    }
                }
            }
        }
    }
}
