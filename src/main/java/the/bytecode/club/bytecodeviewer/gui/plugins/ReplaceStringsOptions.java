package the.bytecode.club.bytecodeviewer.gui.plugins;

import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.plugin.PluginManager;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.ReplaceStrings;
import the.bytecode.club.bytecodeviewer.resources.IconResources;

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
 * The UI for replace strings plugin.
 *
 * @author Konloch
 */

public class ReplaceStringsOptions extends JFrame
{
    public static void open()
    {
        if (BytecodeViewer.promptIfNoLoadedClasses())
            return;
        
        new ReplaceStringsOptions().setVisible(true);
    }
    
    public ReplaceStringsOptions()
    {
        this.setIconImages(IconResources.iconList);
        setSize(new Dimension(250, 176));
        setResizable(false);
        setTitle("Replace Strings");
        getContentPane().setLayout(null);

        JButton btnNewButton = new JButton("Start Replacing");
        btnNewButton.setBounds(6, 115, 232, 23);
        getContentPane().add(btnNewButton);

        JLabel lblNewLabel = new JLabel("Original LDC:");
        lblNewLabel.setBounds(6, 40, 67, 14);
        getContentPane().add(lblNewLabel);

        originalLDC = new JTextField();
        originalLDC.setBounds(80, 37, 158, 20);
        getContentPane().add(originalLDC);
        originalLDC.setColumns(10);

        JLabel lblNewLabel_1 = new JLabel("New LDC:");
        lblNewLabel_1.setBounds(6, 65, 77, 14);
        getContentPane().add(lblNewLabel_1);

        newLDC = new JTextField();
        newLDC.setColumns(10);
        newLDC.setBounds(80, 62, 158, 20);
        getContentPane().add(newLDC);

        JLabel lblNewLabel_2 = new JLabel("Class:");
        lblNewLabel_2.setBounds(6, 90, 46, 14);
        getContentPane().add(lblNewLabel_2);

        classToReplaceIn = new JTextField();
        classToReplaceIn.setToolTipText("* will search all classes");
        classToReplaceIn.setText("*");
        classToReplaceIn.setBounds(80, 87, 158, 20);
        getContentPane().add(classToReplaceIn);
        classToReplaceIn.setColumns(10);

        final JCheckBox chckbxNewCheckBox = new JCheckBox("Replace All Contains");
        chckbxNewCheckBox.setToolTipText("If it's unticked, it will check if the string equals, if its ticked it will check if"
                        + " it contains, then replace the original LDC part of the string.");
        chckbxNewCheckBox.setBounds(6, 7, 232, 23);
        getContentPane().add(chckbxNewCheckBox);
        btnNewButton.addActionListener(arg0 -> {
            PluginManager.runPlugin(new ReplaceStrings(originalLDC.getText(),
                    newLDC.getText(), classToReplaceIn.getText(),
                    chckbxNewCheckBox.isSelected()));
            dispose();
        });
        
        this.setLocationRelativeTo(null);
    }

    private static final long serialVersionUID = -2662514582647810868L;
    private final JTextField originalLDC;
    private final JTextField newLDC;
    private final JTextField classToReplaceIn;
}
