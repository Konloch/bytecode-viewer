package the.bytecode.club.bytecodeviewer.searching;

import eu.bibl.banalysis.asm.desc.OpcodeInfo;
import java.awt.GridLayout;
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
 */

public class MethodCallSearch implements SearchTypeDetails {

    JTextField mOwner;
    JTextField mName;
    JTextField mDesc;
    JPanel myPanel = null;

    public MethodCallSearch() {
        mOwner = new JTextField("");
        mOwner.addKeyListener(EnterKeyEvent.SINGLETON);
        mName = new JTextField("");
        mName.addKeyListener(EnterKeyEvent.SINGLETON);
        mDesc = new JTextField("");
        mDesc.addKeyListener(EnterKeyEvent.SINGLETON);
    }

    @Override
    public JPanel getPanel() {
        if (myPanel == null) {
            myPanel = new JPanel(new GridLayout(3, 2));
            myPanel.add(new JLabel("Owner: "));
            myPanel.add(mOwner);
            myPanel.add(new JLabel("Name: "));
            myPanel.add(mName);
            myPanel.add(new JLabel("Desc: "));
            myPanel.add(mDesc);
        }

        return myPanel;
    }

    @Override
    public void search(final FileContainer container, final ClassNode node, final SearchResultNotifier srn,
                       boolean exact) {
        final Iterator<MethodNode> methods = node.methods.iterator();
        String owner = mOwner.getText();
        if (owner.isEmpty()) {
            owner = null;
        }
        String name = mName.getText();
        if (name.isEmpty()) {
            name = null;
        }
        String desc = mDesc.getText();
        if (desc.isEmpty()) {
            desc = null;
        }

        while (methods.hasNext()) {
            final MethodNode method = methods.next();

            final InsnList insnlist = method.instructions;
            for (AbstractInsnNode insnNode : insnlist) {
                if (insnNode instanceof MethodInsnNode) {
                    final MethodInsnNode min = (MethodInsnNode) insnNode;
                    if (name == null && owner == null && desc == null)
                        continue;
                    if (exact) {
                        if (name != null && !name.equals(min.name)) {
                            continue;
                        }
                        if (owner != null && !owner.equals(min.owner)) {
                            continue;
                        }
                        if (desc != null && !desc.equals(min.desc)) {
                            continue;
                        }
                        String desc2 = method.desc;
                        try {
                            desc2 = Type.getType(method.desc).toString();
                            if (desc2.equals("null"))
                                desc2 = method.desc;
                        } catch (ArrayIndexOutOfBoundsException ignored) {

                        }
                        srn.notifyOfResult(container.name + ">" + node.name
                                + "."
                                + method.name
                                + desc2
                                + " > "
                                + OpcodeInfo.OPCODES.get(insnNode.getOpcode())
                                .toLowerCase());
                    } else {
                        if (name != null && !min.name.contains(name)) {
                            continue;
                        }
                        if (owner != null && !min.owner.contains(owner)) {
                            continue;
                        }
                        if (desc != null && !min.desc.contains(desc)) {
                            continue;
                        }
                        String desc2 = method.desc;
                        try {
                            desc2 = Type.getType(method.desc).toString();
                            if (desc2.equals("null"))
                                desc2 = method.desc;
                        } catch (ArrayIndexOutOfBoundsException ignored) {

                        }
                        srn.notifyOfResult(container.name + ">" + node.name
                                + "."
                                + method.name
                                + desc2
                                + " > "
                                + OpcodeInfo.OPCODES.get(insnNode.getOpcode())
                                .toLowerCase());
                    }
                }
            }
        }
    }
}