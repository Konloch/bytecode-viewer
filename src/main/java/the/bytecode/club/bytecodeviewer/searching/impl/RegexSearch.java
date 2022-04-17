package the.bytecode.club.bytecodeviewer.searching.impl;

import java.awt.*;
import java.util.Iterator;
import java.util.regex.Pattern;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.searching.EnterKeyEvent;
import the.bytecode.club.bytecodeviewer.searching.LDCSearchTreeNodeResult;
import the.bytecode.club.bytecodeviewer.searching.RegexInsnFinder;
import the.bytecode.club.bytecodeviewer.searching.SearchPanel;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponents;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJLabel;

import static the.bytecode.club.bytecodeviewer.searching.RegexInsnFinder.processRegex;

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
 * Regex Searching
 *
 * @author Konloch
 * @author WaterWolf
 * @since 09/29/2011
 */

public class RegexSearch implements SearchPanel
{
    public static JTextField searchText;
    JPanel myPanel = null;

    public RegexSearch()
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
            myPanel.add(new TranslatedJLabel("Search Regex: ", TranslatedComponents.SEARCH_REGEX), BorderLayout.WEST);
            myPanel.add(searchText, BorderLayout.CENTER);
        }

        return myPanel;
    }

    @Override
    public void search(final ResourceContainer container, final String resourceWorkingName, final ClassNode node, boolean exact)
    {
        final Iterator<MethodNode> methods = node.methods.iterator();
        final String srchText = searchText.getText();

        if (srchText.isEmpty())
            return;
        
        Pattern pattern = Pattern.compile(processRegex(srchText), Pattern.MULTILINE);
        while (methods.hasNext())
        {
            final MethodNode method = methods.next();

            if (RegexInsnFinder.staticScan(node, method, pattern))
            {
                String desc2 = method.desc;
                try
                {
                    desc2 = Type.getType(method.desc).toString();

                    if (desc2.equals("null"))
                        desc2 = method.desc;
                } catch (java.lang.ArrayIndexOutOfBoundsException ignored) {}
    
                BytecodeViewer.viewer.searchBoxPane.treeRoot.add(new LDCSearchTreeNodeResult(
                        container,
                        resourceWorkingName,
                        node,
                        method,
                        null,
                        node.name + "." + method.name + desc2,
                        ""
                ));
            }
        }
    }
}
