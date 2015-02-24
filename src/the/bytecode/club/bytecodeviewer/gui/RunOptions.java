package the.bytecode.club.bytecodeviewer.gui;

import javax.swing.JFrame;

import java.awt.Dimension;

import javax.swing.JCheckBox;
import javax.swing.JButton;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.PluginManager;
import the.bytecode.club.bytecodeviewer.Resources;
import the.bytecode.club.bytecodeviewer.plugins.EZInjection;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTextField;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import javax.swing.JLabel;

/**
 * The UI for File>Run aka EZ-Injection plugin.
 * 
 * @author Konloch
 *
 */

public class RunOptions extends JFrame {
	public RunOptions() {
		this.setIconImages(Resources.iconList);
		setSize(new Dimension(250, 402));
		setResizable(false);
		setTitle("Run Options");
		getContentPane().setLayout(null);

		final JCheckBox accessModifiers = new JCheckBox(
				"Set All Access Modifiers Public");
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

				if (m.name.equals("main")
						&& m.desc.equals("([Ljava/lang/String;)V")) {
					if (!b) {
						b = true;
						txtThebytecodeclubexamplemainlstring
								.setText(classNode.name + "." + m.name);
					}
				}
			}
		}

		if (!b)
			txtThebytecodeclubexamplemainlstring
					.setText("the/bytecode/club/Example.main");

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

		final JCheckBox forceProxy = new JCheckBox(
				"Force Proxy (socks5, host:port):");
		forceProxy.setBounds(6, 142, 232, 23);
		getContentPane().add(forceProxy);

		final JCheckBox launchReflectionKit = new JCheckBox(
				"Launch Reflection Kit On Successful Invoke");
		launchReflectionKit.setEnabled(false);
		launchReflectionKit.setBounds(6, 260, 232, 23);
		getContentPane().add(launchReflectionKit);

		final JCheckBox console = new JCheckBox("Launch Console");
		console.setBounds(6, 286, 232, 23);
		console.setSelected(true);
		getContentPane().add(console);

		final JCheckBox chckbxPrintToTerminal = new JCheckBox(
				"Print To Command Line");
		chckbxPrintToTerminal.setSelected(true);
		chckbxPrintToTerminal.setBounds(6, 315, 232, 23);
		getContentPane().add(chckbxPrintToTerminal);
		this.setLocationRelativeTo(null);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
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
			}
		});
	}

	private static final long serialVersionUID = -2662514582647810868L;
	private JTextField txtThebytecodeclubexamplemainlstring;
	private JCheckBox debugMethodCalls;
	private JTextField textField;
	private JTextField textField_1;
}
