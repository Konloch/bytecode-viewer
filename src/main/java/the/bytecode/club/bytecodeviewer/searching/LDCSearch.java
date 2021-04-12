package the.bytecode.club.bytecodeviewer.searching;

import java.awt.GridLayout;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
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
 * LDC Searching
 *
 * @author Konloch
 * @author WaterWolf
 */

public class LDCSearch implements SearchTypeDetails {

    JTextField searchText;
    JPanel myPanel = null;

    public LDCSearch() {
        searchText = new JTextField("");
        searchText.addKeyListener(EnterKeyEvent.SINGLETON);
    }

    @Override
    public JPanel getPanel() {
        if (myPanel == null) {
            myPanel = new JPanel(new GridLayout(1, 2));
            myPanel.add(new JLabel("Search String: "));
            myPanel.add(searchText);
        }

        return myPanel;
    }

    @Override
    public void search(final FileContainer container, final ClassNode node, final SearchResultNotifier srn,
                       boolean exact) {
        final Iterator<MethodNode> methods = node.methods.iterator();
        final String srchText = searchText.getText();
        if (srchText.isEmpty())
            return;
        while (methods.hasNext()) {
            final MethodNode method = methods.next();

            final InsnList insnlist = method.instructions;
            for (AbstractInsnNode insnNode : insnlist) {
                if (insnNode instanceof LdcInsnNode) {
                    final LdcInsnNode ldcObject = ((LdcInsnNode) insnNode);
                    final String ldcString = ldcObject.cst.toString();
                    String desc2 = method.desc;
                    try {
                        desc2 = Type.getType(method.desc).toString();
                        if (desc2.equals("null"))
                            desc2 = method.desc;
                    } catch (ArrayIndexOutOfBoundsException ignored) {

                    }

                    if ((exact && ldcString.equals(srchText)) || (!exact && ldcString.contains(srchText))) {
                        srn.notifyOfResult(container.name + ">" + node.name + "." + method.name
                                + desc2
                                + " -> \"" + ldcString + "\" > "
                                + ldcObject.cst.getClass().getCanonicalName());
                    }
                }
            }
        }
        final Iterator<FieldNode> fields = node.fields.iterator();
        while (methods.hasNext()) {
            final FieldNode field = fields.next();
            String desc2 = field.desc;
            try {
                desc2 = Type.getType(field.desc).toString();
                if (desc2.equals("null"))
                    desc2 = field.desc;
            } catch (java.lang.ArrayIndexOutOfBoundsException ignored) {

            }
            if (field.value instanceof String) {
                srn.notifyOfResult(container.name + ">" + node.name + "." + field.name + desc2
                        + " -> \"" + field.value + "\" > field");
            }
        }
    }
}
