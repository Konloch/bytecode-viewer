package the.bytecode.club.bytecodeviewer.plugin.preinstalled;

import org.objectweb.asm.tree.*;
import the.bytecode.club.bytecodeviewer.Resources;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.plugin.PluginManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

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
 * An Allatori String Decrypter, targets an unknown version.
 *
 * @author Konloch
 * @author Szperak
 * @since 08/15/2015
 */

public class AllatoriStringDecrypterOptions extends Plugin
{
    @Override
    public void execute(ArrayList<ClassNode> classNodeList)
    {
        new AllatoriStringDecrypterOptionsFrame().setVisible(true);
    }
    
    public static class AllatoriStringDecrypterOptionsFrame extends JFrame
    {
        private JTextField textField;
        
        public AllatoriStringDecrypterOptionsFrame()
        {
            this.setIconImages(Resources.iconList);
            setSize(new Dimension(250, 120));
            setResizable(false);
            setTitle("Allatori String Decrypter");
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
            
            btnNewButton.addActionListener(arg0 ->
            {
                PluginManager.runPlugin(new the.bytecode.club.bytecodeviewer.plugin.preinstalled.AllatoriStringDecrypter(textField.getText()));
                dispose();
            });
            
            this.setLocationRelativeTo(null);
        }
        
        private static final long serialVersionUID = -2662514582647810868L;
    }
}
