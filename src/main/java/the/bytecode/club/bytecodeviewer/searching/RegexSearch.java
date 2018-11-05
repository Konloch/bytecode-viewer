package the.bytecode.club.bytecodeviewer.searching;

import java.awt.GridLayout;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

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
 */

public class RegexSearch implements SearchTypeDetails {

    public static JTextField searchText = new JTextField("");
    JPanel myPanel = null;

    private static RegexInsnFinder regexFinder;

    @Override
    public JPanel getPanel() {
        if (myPanel == null) {
            myPanel = new JPanel(new GridLayout(1, 2));
            myPanel.add(new JLabel("Search Regex: "));
            myPanel.add(searchText);
        }

        return myPanel;
    }

    @Override
    public void search(final ClassNode node, final SearchResultNotifier srn,
                       boolean exact) {
        final Iterator<MethodNode> methods = node.methods.iterator();
        final String srchText = searchText.getText();
        if (srchText.isEmpty())
            return;
        while (methods.hasNext()) {
            final MethodNode method = methods.next();

            if (regexFinder == null) {
                regexFinder = new RegexInsnFinder(node, method);
            } else {
                regexFinder.setMethod(node, method);
            }

            if (regexFinder.find(srchText).length > 0) {
                String desc2 = method.desc;
                try {
                    desc2 = Type.getType(method.desc).toString();
                    if (desc2 == null || desc2.equals("null"))
                        desc2 = method.desc;
                } catch (java.lang.ArrayIndexOutOfBoundsException e) {

                }
                srn.notifyOfResult(node.name + "." + method.name + desc2);
            }
        }
    }
}