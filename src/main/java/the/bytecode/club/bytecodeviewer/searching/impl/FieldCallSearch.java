package the.bytecode.club.bytecodeviewer.searching.impl;

import java.util.Iterator;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;

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
 * Field call searching
 *
 * @author Konloch
 * @author Water Wolf
 */

public class FieldCallSearch extends MethodCallSearch
{
    @Override
    public void search(ResourceContainer container, String resourceWorkingName, ClassNode node, boolean exact)
    {
        final Iterator<MethodNode> methods = node.methods.iterator();
        
        String searchOwner = mOwner.getText();
        if (searchOwner.isEmpty())
            searchOwner = null;
        
        String searchName = mName.getText();
        if (searchName.isEmpty())
            searchName = null;
        
        String searchDesc = mDesc.getText();
        if (searchDesc.isEmpty())
            searchDesc = null;
        
        while (methods.hasNext())
        {
            final MethodNode method = methods.next();

            final InsnList insnlist = method.instructions;
            for (AbstractInsnNode insnNode : insnlist)
            {
                if (insnNode instanceof FieldInsnNode)
                {
                    final FieldInsnNode min = (FieldInsnNode) insnNode;
                    
                    if (searchName == null && searchOwner == null && searchDesc == null)
                        continue;
                    
                    if (exact)
                    {
                        if (searchName != null && !searchName.equals(min.name))
                            continue;
                        if (searchOwner != null && !searchOwner.equals(min.owner))
                            continue;
                        if (searchDesc != null && !searchDesc.equals(min.desc))
                            continue;
                    }
                    else
                    {
                        if (searchName != null && !min.name.contains(searchName))
                            continue;
                        if (searchOwner != null && !min.owner.contains(searchOwner))
                            continue;
                        if (searchDesc != null && !min.desc.contains(searchDesc))
                            continue;
                    }
    
                    found(container, resourceWorkingName, node, method, insnNode);
                }
            }
        }
    }
}
