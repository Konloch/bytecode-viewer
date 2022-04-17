package the.bytecode.club.bytecodeviewer.searching.impl;

import java.awt.*;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
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
 * LDC Searching
 *
 * @author Konloch
 * @author WaterWolf
 * @since 09/29/2011
 */

public class LDCSearch implements SearchPanel
{
    JTextField searchText;
    JPanel myPanel = null;

    public LDCSearch()
    {
        searchText = new JTextField("");
        searchText.addKeyListener(EnterKeyEvent.SINGLETON);
    }

    @Override
    public JPanel getPanel()
    {
        if (myPanel == null)
        {
            myPanel = new JPanel(new BorderLayout(16, 16));
            myPanel.add(new TranslatedJLabel("Search String: ", TranslatedComponents.SEARCH_STRING), BorderLayout.WEST);
            myPanel.add(searchText, BorderLayout.CENTER);
        }

        return myPanel;
    }

    public void search(final ResourceContainer container, final String resourceWorkingName, final ClassNode node,
                       boolean caseSensitive)
    {
        final Iterator<MethodNode> methods = node.methods.iterator();
        final String srchText = searchText.getText();
        final String srchTextLowerCase = searchText.getText().toLowerCase();
        
        if (srchText.isEmpty())
            return;
        
        while (methods.hasNext())
        {
            final MethodNode method = methods.next();

            final InsnList insnlist = method.instructions;
            for (AbstractInsnNode insnNode : insnlist)
            {
                if (insnNode instanceof LdcInsnNode)
                {
                    final LdcInsnNode ldcObject = ((LdcInsnNode) insnNode);
                    final String ldcString = ldcObject.cst.toString();

                    //TODO re-add this at some point when the search pane is redone
                    boolean exact = false;
                    final boolean exactMatch = exact && ldcString.equals(srchText);
                    final boolean caseInsensitiveMatch = !exact && caseSensitive && ldcString.contains(srchText);
                    final boolean caseSensitiveMatch = !exact && !caseSensitive && ldcString.toLowerCase().contains(srchTextLowerCase);
                    final boolean anyMatch = exactMatch || caseInsensitiveMatch || caseSensitiveMatch;
                    
                    if (anyMatch)
                    {
                        BytecodeViewer.viewer.searchBoxPane.treeRoot.add(new LDCSearchTreeNodeResult(
                                container,
                                resourceWorkingName,
                                node,
                                method,
                                null,
                                ldcString,
                                ldcObject.cst.getClass().getCanonicalName()
                                ));
                    }
                }
            }
        }
        
        final Iterator<FieldNode> fields = node.fields.iterator();
        while (methods.hasNext())
        {
            final FieldNode field = fields.next();
            
            if (field.value instanceof String)
            {
                BytecodeViewer.viewer.resourcePane.treeRoot.add(new LDCSearchTreeNodeResult(container,
                        resourceWorkingName,
                        node,
                        null,
                        field,
                        String.valueOf(field.value),
                        field.value.getClass().getCanonicalName()
                ));
            }
        }
    }
}
