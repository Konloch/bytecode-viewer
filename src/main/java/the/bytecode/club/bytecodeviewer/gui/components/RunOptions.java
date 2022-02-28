package the.bytecode.club.bytecodeviewer.gui.components;

import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import the.bytecode.club.bytecodeviewer.api.ASMResourceUtil;
import the.bytecode.club.bytecodeviewer.plugin.PluginManager;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.EZInjection;
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
 * The UI for File>Run aka EZ-Injection plugin.
 *
 * @author Konloch
 */

public class RunOptions extends JFrame
{
    private final JTextField mainMethodFQN;
    private final JCheckBox debugMethodCalls;
    private final JTextField debugClasses;
    private final JTextField socksProxy;
    
    public RunOptions()
    {
        this.setIconImages(IconResources.iconList);
        setSize(new Dimension(250, 402));
        setResizable(false);
        setTitle("Run Options");
        getContentPane().setLayout(null);

        final JCheckBox accessModifiers = new JCheckBox("Set All Access Modifiers Public");
        accessModifiers.setBounds(6, 7, 232, 23);
        getContentPane().add(accessModifiers);

        final JCheckBox invokeMethod = new JCheckBox("Invoke Main Method:");
        invokeMethod.setSelected(true);
        invokeMethod.setBounds(6, 203, 232, 23);
        getContentPane().add(invokeMethod);

        final JCheckBox injectHooks = new JCheckBox("Inject Hooks");
        injectHooks.setBounds(6, 33, 232, 23);
        getContentPane().add(injectHooks);

        debugMethodCalls = new JCheckBox("Debug Method Calls");
        debugMethodCalls.setBounds(6, 59, 232, 23);
        getContentPane().add(debugMethodCalls);

        mainMethodFQN = new JTextField();

        JButton btnNewButton = new JButton("Execute");
        btnNewButton.setBounds(6, 345, 232, 23);
        getContentPane().add(btnNewButton);

        mainMethodFQN.setText(ASMResourceUtil.findMainMethod("the/bytecode/club/Example.main"));

        mainMethodFQN.setBounds(6, 233, 232, 20);
        getContentPane().add(mainMethodFQN);
        mainMethodFQN.setColumns(10);

        JLabel lblNewLabel = new JLabel("Debug Classes (Separated with , ):");
        lblNewLabel.setBounds(10, 89, 228, 14);
        getContentPane().add(lblNewLabel);

        debugClasses = new JTextField();
        debugClasses.setText("*");
        debugClasses.setBounds(6, 111, 232, 20);
        getContentPane().add(debugClasses);
        debugClasses.setColumns(10);

        socksProxy = new JTextField();
        socksProxy.setText("127.0.0.1:9150");
        socksProxy.setColumns(10);
        socksProxy.setBounds(6, 172, 232, 20);
        getContentPane().add(socksProxy);

        final JCheckBox forceProxy = new JCheckBox("Force Proxy (socks5, host:port):");
        forceProxy.setBounds(6, 142, 232, 23);
        getContentPane().add(forceProxy);

        final JCheckBox launchReflectionKit = new JCheckBox("Launch Reflection Kit On Successful Invoke");
        launchReflectionKit.setEnabled(false);
        launchReflectionKit.setBounds(6, 260, 232, 23);
        getContentPane().add(launchReflectionKit);

        final JCheckBox console = new JCheckBox("Launch Console");
        console.setBounds(6, 286, 232, 23);
        console.setSelected(true);
        getContentPane().add(console);

        final JCheckBox printToCommandLine = new JCheckBox("Print To Command Line");
        printToCommandLine.setSelected(true);
        printToCommandLine.setBounds(6, 315, 232, 23);
        getContentPane().add(printToCommandLine);
        this.setLocationRelativeTo(null);
        btnNewButton.addActionListener(arg0 -> {
            PluginManager.runPlugin(new EZInjection(accessModifiers
                    .isSelected(), injectHooks.isSelected(),
                    debugMethodCalls.isSelected(), invokeMethod
                    .isSelected(),
                    mainMethodFQN.getText(), false, false, debugClasses
                    .getText(), this.socksProxy.getText(), forceProxy
                    .isSelected(),
                    launchReflectionKit.isSelected(), console.isSelected(),
                    printToCommandLine.isSelected()));
            dispose();
        });
    }

    private static final long serialVersionUID = -2662514582647810868L;
}
