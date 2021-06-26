package the.bytecode.club.bytecodeviewer.gui.components;

import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Resources;
import the.bytecode.club.bytecodeviewer.plugin.PluginManager;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.EZInjection;

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

public class RunOptions extends JFrame {
    public RunOptions() {
        this.setIconImages(Resources.iconList);
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

        txtThebytecodeclubexamplemainlstring = new JTextField();

        JButton btnNewButton = new JButton("Execute");
        btnNewButton.setBounds(6, 345, 232, 23);
        getContentPane().add(btnNewButton);

        boolean b = false;
        for (ClassNode classNode : BytecodeViewer.getLoadedClasses()) {
            for (Object o : classNode.methods.toArray()) {
                MethodNode m = (MethodNode) o;

                if (m.name.equals("main") && m.desc.equals("([Ljava/lang/String;)V")) {
                    if (!b) {
                        b = true;
                        txtThebytecodeclubexamplemainlstring
                                .setText(classNode.name + "." + m.name);
                    }
                }
            }
        }

        if (!b)
            txtThebytecodeclubexamplemainlstring.setText("the/bytecode/club/Example.main");

        txtThebytecodeclubexamplemainlstring.setBounds(6, 233, 232, 20);
        getContentPane().add(txtThebytecodeclubexamplemainlstring);
        txtThebytecodeclubexamplemainlstring.setColumns(10);

        JLabel lblNewLabel = new JLabel("Debug Classes (Seperate with , ):");
        lblNewLabel.setBounds(10, 89, 228, 14);
        getContentPane().add(lblNewLabel);

        textField = new JTextField();
        textField.setText("*");
        textField.setBounds(6, 111, 232, 20);
        getContentPane().add(textField);
        textField.setColumns(10);

        textField_1 = new JTextField();
        textField_1.setText("127.0.0.1:9150");
        textField_1.setColumns(10);
        textField_1.setBounds(6, 172, 232, 20);
        getContentPane().add(textField_1);

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

        final JCheckBox chckbxPrintToTerminal = new JCheckBox("Print To Command Line");
        chckbxPrintToTerminal.setSelected(true);
        chckbxPrintToTerminal.setBounds(6, 315, 232, 23);
        getContentPane().add(chckbxPrintToTerminal);
        this.setLocationRelativeTo(null);
        btnNewButton.addActionListener(arg0 -> {
            PluginManager.runPlugin(new EZInjection(accessModifiers
                    .isSelected(), injectHooks.isSelected(),
                    debugMethodCalls.isSelected(), invokeMethod
                    .isSelected(),
                    txtThebytecodeclubexamplemainlstring.getText(), false, false, textField
                    .getText(), textField_1.getText(), forceProxy
                    .isSelected(),
                    launchReflectionKit.isSelected(), console.isSelected(),
                    chckbxPrintToTerminal.isSelected()));
            dispose();
        });
    }

    private static final long serialVersionUID = -2662514582647810868L;
    private final JTextField txtThebytecodeclubexamplemainlstring;
    private final JCheckBox debugMethodCalls;
    private final JTextField textField;
    private final JTextField textField_1;
}
