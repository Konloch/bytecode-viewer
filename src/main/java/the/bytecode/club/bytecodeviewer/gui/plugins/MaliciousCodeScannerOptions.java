package the.bytecode.club.bytecodeviewer.gui.plugins;

import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Resources;
import the.bytecode.club.bytecodeviewer.plugin.PluginManager;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.MaliciousCodeScanner;

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
 * A simple GUI to select the Malicious Code Scanner options.
 *
 * @author Konloch
 * @author Adrianherrera
 */

public class MaliciousCodeScannerOptions extends JFrame
{
    public static void open()
    {
        if (BytecodeViewer.getLoadedClasses().isEmpty()) {
            BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
            return;
        }
        new MaliciousCodeScannerOptions().setVisible(true);
    }
    
    public MaliciousCodeScannerOptions()
    {
        this.setIconImages(Resources.iconList);
        setSize(new Dimension(250, 323));
        setResizable(false);
        setTitle("Malicious Code Scanner Options");
        getContentPane().setLayout(null);

        final JCheckBox chckbxJavalangreflection = new JCheckBox("java/lang/reflection");
        chckbxJavalangreflection.setSelected(true);
        chckbxJavalangreflection.setBounds(6, 7, 232, 23);
        getContentPane().add(chckbxJavalangreflection);

        final JCheckBox chckbxJavanet = new JCheckBox("java/net");
        chckbxJavanet.setSelected(true);
        chckbxJavanet.setBounds(6, 81, 232, 23);
        getContentPane().add(chckbxJavanet);

        final JCheckBox chckbxJavaio = new JCheckBox("java/io");
        chckbxJavaio.setBounds(6, 104, 232, 23);
        getContentPane().add(chckbxJavaio);

        final JCheckBox chckbxJavalangruntime = new JCheckBox("java/lang/Runtime");
        chckbxJavalangruntime.setSelected(true);
        chckbxJavalangruntime.setBounds(6, 33, 232, 23);
        getContentPane().add(chckbxJavalangruntime);

        final JCheckBox chckbxLdcContainswww = new JCheckBox("LDC contains 'www.'");
        chckbxLdcContainswww.setSelected(true);
        chckbxLdcContainswww.setBounds(6, 130, 232, 23);
        getContentPane().add(chckbxLdcContainswww);

        final JCheckBox chckbxLdcContainshttp = new JCheckBox("LDC contains 'http://'");
        chckbxLdcContainshttp.setSelected(true);
        chckbxLdcContainshttp.setBounds(6, 156, 232, 23);
        getContentPane().add(chckbxLdcContainshttp);

        final JCheckBox chckbxLdcContainshttps = new JCheckBox("LDC contains 'https://'");
        chckbxLdcContainshttps.setSelected(true);
        chckbxLdcContainshttps.setBounds(6, 182, 232, 23);
        getContentPane().add(chckbxLdcContainshttps);

        final JCheckBox chckbxLdcMatchesIp = new JCheckBox("LDC matches IP regex");
        chckbxLdcMatchesIp.setSelected(true);
        chckbxLdcMatchesIp.setBounds(6, 208, 232, 23);
        getContentPane().add(chckbxLdcMatchesIp);

        final JCheckBox chckbxNullSecMan = new JCheckBox("SecurityManager set to null");
        chckbxNullSecMan.setSelected(true);
        chckbxNullSecMan.setBounds(6, 234, 232, 23);
        getContentPane().add(chckbxNullSecMan);

        final JCheckBox chckbxJavaawtrobot = new JCheckBox("java/awt/Robot");
        chckbxJavaawtrobot.setSelected(true);
        chckbxJavaawtrobot.setBounds(6, 59, 232, 23);
        getContentPane().add(chckbxJavaawtrobot);

        JButton btnNewButton = new JButton("Start Scanning");
        btnNewButton.addActionListener(arg0 -> {
            PluginManager.runPlugin(new MaliciousCodeScanner(
                    chckbxJavalangreflection.isSelected(),
                    chckbxJavalangruntime.isSelected(),
                    chckbxJavanet.isSelected(),
                    chckbxJavaio.isSelected(),
                    chckbxLdcContainswww.isSelected(),
                    chckbxLdcContainshttp.isSelected(),
                    chckbxLdcContainshttps.isSelected(),
                    chckbxLdcMatchesIp.isSelected(),
                    chckbxNullSecMan.isSelected(),
                    chckbxJavaawtrobot.isSelected()));
            dispose();
        });

        btnNewButton.setBounds(6, 264, 232, 23);
        getContentPane().add(btnNewButton);
        this.setLocationRelativeTo(null);
    }

    private static final long serialVersionUID = -2662514582647810868L;
}