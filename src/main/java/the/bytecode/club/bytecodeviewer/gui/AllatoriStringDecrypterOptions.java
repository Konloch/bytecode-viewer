package the.bytecode.club.bytecodeviewer.gui;

import the.bytecode.club.bytecodeviewer.Resources;
import the.bytecode.club.bytecodeviewer.plugin.PluginManager;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.AllatoriStringDecrypter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
 *
 */

public class AllatoriStringDecrypterOptions extends JFrame {
	public AllatoriStringDecrypterOptions() {
		this.setIconImages(Resources.iconList);
		setSize(new Dimension(250, 120));
		setResizable(false);
		setTitle("Allatori decrypter");
		getContentPane().setLayout(null);

		JButton btnNewButton = new JButton("Decrypt");
		btnNewButton.setBounds(6, 56, 232, 23);
		getContentPane().add(btnNewButton);


		JLabel lblNewLabel = new JLabel("Class:");
		lblNewLabel.setBounds(6, 20, 67, 14);
		getContentPane().add(lblNewLabel);

		textField = new JTextField();
		textField.setToolTipText("* will search all classes");
		textField.setText("*");
		textField.setBounds(80, 17, 158, 20);
		getContentPane().add(textField);
		textField.setColumns(10);

		
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				PluginManager.runPlugin(new AllatoriStringDecrypter(textField.getText()));
				dispose();
			}
		});
		this.setLocationRelativeTo(null);
	}

	private static final long serialVersionUID = -2662514582647810868L;
	private JTextField textField;
}
