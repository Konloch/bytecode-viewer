package the.bytecode.club.bytecodeviewer.gui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import the.bytecode.club.bytecodeviewer.Resources;
import the.bytecode.club.bytecodeviewer.plugin.PluginManager;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.ReplaceStrings;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JCheckBox;

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

public class ReplaceStringsOptions extends JFrame {
    public ReplaceStringsOptions() {
        this.setIconImages(Resources.iconList);
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

        textField = new JTextField();
        textField.setBounds(80, 37, 158, 20);
        getContentPane().add(textField);
        textField.setColumns(10);

        JLabel lblNewLabel_1 = new JLabel("New LDC:");
        lblNewLabel_1.setBounds(6, 65, 77, 14);
        getContentPane().add(lblNewLabel_1);

        textField_1 = new JTextField();
        textField_1.setColumns(10);
        textField_1.setBounds(80, 62, 158, 20);
        getContentPane().add(textField_1);

        JLabel lblNewLabel_2 = new JLabel("Class:");
        lblNewLabel_2.setBounds(6, 90, 46, 14);
        getContentPane().add(lblNewLabel_2);

        textField_2 = new JTextField();
        textField_2.setToolTipText("* will search all classes");
        textField_2.setText("*");
        textField_2.setBounds(80, 87, 158, 20);
        getContentPane().add(textField_2);
        textField_2.setColumns(10);

        final JCheckBox chckbxNewCheckBox = new JCheckBox(
                "Replace All Contains");
        chckbxNewCheckBox
                .setToolTipText("If it's unticked, it will check if the string equals, if its ticked it will check if it contains, then replace the original LDC part of the string.");
        chckbxNewCheckBox.setBounds(6, 7, 232, 23);
        getContentPane().add(chckbxNewCheckBox);
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                PluginManager.runPlugin(new ReplaceStrings(textField.getText(),
                        textField_1.getText(), textField_2.getText(),
                        chckbxNewCheckBox.isSelected()));
                dispose();
            }
        });
        this.setLocationRelativeTo(null);
    }

    private static final long serialVersionUID = -2662514582647810868L;
    private JTextField textField;
    private JTextField textField_1;
    private JTextField textField_2;
}
