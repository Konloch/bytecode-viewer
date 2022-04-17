package the.bytecode.club.bytecodeviewer.searching.impl;

import eu.bibl.banalysis.asm.desc.OpcodeInfo;

import java.awt.*;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.searching.EnterKeyEvent;
import the.bytecode.club.bytecodeviewer.searching.LDCSearchTreeNodeResult;
import the.bytecode.club.bytecodeviewer.searching.SearchPanel;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponents;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJLabel;

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
 * Method call searching
 *
 * @author Konloch
 * @author WaterWolf
 * @since 09/29/2011
 */

public class MethodCallSearch implements SearchPanel
{
    JTextField mOwner;
    JTextField mName;
    JTextField mDesc;
    JPanel myPanel = null;

    public MethodCallSearch()
    {
        mOwner = new JTextField("");
        mOwner.addKeyListener(EnterKeyEvent.SINGLETON);
        mName = new JTextField("");
        mName.addKeyListener(EnterKeyEvent.SINGLETON);
        mDesc = new JTextField("");
        mDesc.addKeyListener(EnterKeyEvent.SINGLETON);
    }

    public JPanel getPanel()
    {
        if (myPanel == null)
        {
            myPanel = new JPanel(new BorderLayout(16, 16));

            JPanel left = new JPanel(new GridLayout(3,1));
            JPanel right = new JPanel(new GridLayout(3,1));

            left.add(new TranslatedJLabel("Owner: ", TranslatedComponents.OWNER));
            right.add(mOwner);
            left.add(new TranslatedJLabel("Name: ", TranslatedComponents.NAME));
            right.add(mName);
            left.add(new TranslatedJLabel("Desc: ", TranslatedComponents.DESC));
            right.add(mDesc);
            myPanel.add(left, BorderLayout.WEST);
            myPanel.add(right, BorderLayout.CENTER);
        }

        return myPanel;
    }
    
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
                if (insnNode instanceof MethodInsnNode)
                {
                    final MethodInsnNode min = (MethodInsnNode) insnNode;
                    
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
    
    public void found(final ResourceContainer container, final String resourceWorkingName, final ClassNode node, final MethodNode method, final AbstractInsnNode insnNode)
    {
        BytecodeViewer.viewer.searchBoxPane.treeRoot.add(new LDCSearchTreeNodeResult(
                container,
                resourceWorkingName,
                node,
                method,
                null,
                OpcodeInfo.OPCODES.get(insnNode.getOpcode()).toLowerCase(),
                ""
        ));
    }
}
