package the.bytecode.club.bytecodeviewer.searching.impl;

import eu.bibl.banalysis.asm.desc.OpcodeInfo;

import java.awt.*;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import the.bytecode.club.bytecodeviewer.searching.EnterKeyEvent;
import the.bytecode.club.bytecodeviewer.searching.SearchResultNotifier;
import the.bytecode.club.bytecodeviewer.searching.SearchTypeDetails;
import the.bytecode.club.bytecodeviewer.translation.Translation;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJLabel;
import the.bytecode.club.bytecodeviewer.util.FileContainer;

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

public class MethodCallSearch implements SearchTypeDetails
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

    @Override
    public JPanel getPanel()
    {
        if (myPanel == null)
        {
            myPanel = new JPanel(new GridLayout(3, 2));
            myPanel.add(new TranslatedJLabel("Owner: ", Translation.OWNER));
            myPanel.add(mOwner);
            myPanel.add(new TranslatedJLabel("Name: ", Translation.NAME));
            myPanel.add(mName);
            myPanel.add(new TranslatedJLabel("Desc: ", Translation.DESC));
            myPanel.add(mDesc);
        }

        return myPanel;
    }

    @Override
    public void search(final FileContainer container, final ClassNode node, final SearchResultNotifier srn,
                       boolean exact)
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
                    
                    found(container, node, method, insnNode, srn);
                }
            }
        }
    }
    
    public void found(final FileContainer container, final ClassNode node, final MethodNode method, final AbstractInsnNode insnNode, final SearchResultNotifier srn)
    {
        String desc = method.desc;
        try
        {
            desc = Type.getType(method.desc).toString();
        
            if (desc.equals("null"))
                desc = method.desc;
        } catch (ArrayIndexOutOfBoundsException ignored) { }
        
        srn.notifyOfResult(container.name + ">" + node.name
                + "."
                + method.name
                + desc
                + " > "
                + OpcodeInfo.OPCODES.get(insnNode.getOpcode())
                .toLowerCase());
    }
}