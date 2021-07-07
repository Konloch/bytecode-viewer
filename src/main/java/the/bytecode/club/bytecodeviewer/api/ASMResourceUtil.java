package the.bytecode.club.bytecodeviewer.api;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
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
 * Used to interact with classnodes loaded inside of BCV as resources
 *
 * @author Konloch
 */

public final class ASMResourceUtil
{
    /**
     * Attempts to a method main inside of the loaded resources and returns the fully qualified name
     */
    public static String findMainMethod(String defaultFQN)
    {
        for (ClassNode cn : BytecodeViewer.getLoadedClasses())
        {
            for (Object o : cn.methods.toArray())
            {
                MethodNode m = (MethodNode) o;
    
                if (m.name.equals("main") && m.desc.equals("([Ljava/lang/String;)V"))
                {
                    return cn.name + "." + m.name;
                }
            }
        }
        
        return defaultFQN;
    }

    public static void renameFieldNode(String originalParentName,
                                       String originalFieldName, String originalFieldDesc,
                                       String newFieldParent, String newFieldName, String newFieldDesc)
    {
        for (ClassNode c : BytecodeViewer.getLoadedClasses())
        {
            for (Object o : c.methods.toArray())
            {
                MethodNode m = (MethodNode) o;
                for (AbstractInsnNode i : m.instructions.toArray())
                {
                    if (i instanceof FieldInsnNode)
                    {
                        FieldInsnNode field = (FieldInsnNode) i;

                        if (field.owner.equals(originalParentName)
                                && field.name.equals(originalFieldName)
                                && field.desc.equals(originalFieldDesc))
                        {
                            if (newFieldParent != null)
                                field.owner = newFieldParent;
                            if (newFieldName != null)
                                field.name = newFieldName;
                            if (newFieldDesc != null)
                                field.desc = newFieldDesc;
                        }
                    }
                }
            }
        }
    }

    public static void renameMethodNode(String originalParentName,
                                        String originalMethodName, String originalMethodDesc,
                                        String newParent, String newName, String newDesc)
    {
        for (ClassNode c : BytecodeViewer.getLoadedClasses())
        {
            for (Object o : c.methods.toArray())
            {
                MethodNode m = (MethodNode) o;
                for (AbstractInsnNode i : m.instructions.toArray())
                {
                    if (i instanceof MethodInsnNode)
                    {
                        MethodInsnNode mi = (MethodInsnNode) i;
                        if (mi.owner.equals(originalParentName)
                                && mi.name.equals(originalMethodName)
                                && mi.desc.equals(originalMethodDesc))
                        {
                            if (newParent != null)
                                mi.owner = newParent;
                            if (newName != null)
                                mi.name = newName;
                            if (newDesc != null)
                                mi.desc = newDesc;
                        }
                    } /*else {
                        System.out.println(i.getOpcode()+":"+c.name+":"+m.name);
                    }*/
                }

                if (m.signature != null)
                {
                    if (newName != null)
                        m.signature = m.signature.replace(originalMethodName,
                                newName);
                    if (newParent != null)
                        m.signature = m.signature.replace(originalParentName,
                                newParent);
                }

                if (m.name.equals(originalMethodName)
                        && m.desc.equals(originalMethodDesc)
                        && c.name.equals(originalParentName))
                {
                    if (newName != null)
                        m.name = newName;
                    if (newDesc != null)
                        m.desc = newDesc;
                }
            }
        }
    }

    public static void renameClassNode(final String oldName,
                                       final String newName)
    {
        for (ClassNode c : BytecodeViewer.getLoadedClasses())
        {
            for (InnerClassNode oo : c.innerClasses)
            {
                if (oo.innerName != null && oo.innerName.equals(oldName))
                    oo.innerName = newName;
                
                if (oo.name.equals(oldName))
                    oo.name = newName;
                
                if (oo.outerName != null && oo.outerName.equals(oldName))
                    oo.outerName = newName;
            }

            if (c.signature != null)
                c.signature = c.signature.replace(oldName, newName);

            if (c.superName.equals(oldName))
                c.superName = newName;
            
            for (Object o : c.fields.toArray())
            {
                FieldNode f = (FieldNode) o;
                f.desc = f.desc.replace(oldName, newName);
            }
            
            for (Object o : c.methods.toArray())
            {
                MethodNode m = (MethodNode) o;

                if (m.localVariables != null)
                    for (LocalVariableNode node : m.localVariables)
                        node.desc = node.desc.replace(oldName, newName);

                if (m.signature != null)
                    m.signature = m.signature.replace(oldName, newName);

                for (int i = 0; i < m.exceptions.size(); i++)
                    if (m.exceptions.get(i).equals(oldName))
                        m.exceptions.set(i, newName);

                for (AbstractInsnNode i : m.instructions.toArray())
                {
                    if (i instanceof TypeInsnNode)
                    {
                        TypeInsnNode t = (TypeInsnNode) i;
                        if (t.desc.equals(oldName))
                            t.desc = newName;
                    }
                    
                    if (i instanceof MethodInsnNode)
                    {
                        MethodInsnNode mi = (MethodInsnNode) i;
                        if (mi.owner.equals(oldName))
                            mi.owner = newName;
                        mi.desc = mi.desc.replace(oldName, newName);
                    }
                    if (i instanceof FieldInsnNode)
                    {
                        FieldInsnNode fi = (FieldInsnNode) i;
                        if (fi.owner.equals(oldName))
                            fi.owner = newName;
                        fi.desc = fi.desc.replace(oldName, newName);
                    }
                }
            }
        }
    }
}