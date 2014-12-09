package the.bytecode.club.bytecodeviewer.gui;

import javax.swing.JFrame;

import java.awt.Dimension;

import javax.swing.JCheckBox;
import javax.swing.JButton;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.plugins.EZInjection;
import the.bytecode.club.bytecodeviewer.plugins.PluginManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTextField;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import javax.swing.JLabel;

public class EZInjectionOptions extends JFrame {
	public EZInjectionOptions() {
    	this.setIconImages(BytecodeViewer.iconList);
		setSize(new Dimension(250, 454));
		setResizable(false);
		setTitle("EZ Injection Options");
		getContentPane().setLayout(null);
		
		final JCheckBox accessModifiers = new JCheckBox("Set All Access Modifiers Public");
		accessModifiers.setSelected(true);
		accessModifiers.setBounds(6, 7, 232, 23);
		getContentPane().add(accessModifiers);
		
		final JCheckBox invokeMethod = new JCheckBox("Invoke Main Method:");
		invokeMethod.setSelected(true);
		invokeMethod.setBounds(6, 251, 232, 23);
		getContentPane().add(invokeMethod);
		
		final JCheckBox injectHooks = new JCheckBox("Inject Hooks");
		injectHooks.setSelected(true);
		injectHooks.setBounds(6, 33, 232, 23);
		getContentPane().add(injectHooks);
		
		debugMethodCalls = new JCheckBox("Debug Method Calls");
		debugMethodCalls.setSelected(true);
		debugMethodCalls.setBounds(6, 59, 232, 23);
		getContentPane().add(debugMethodCalls);
		
		final JCheckBox runtime = new JCheckBox("Sandbox Runtime.exec");
		runtime.setEnabled(false);
		runtime.setBounds(6, 138, 232, 23);
		getContentPane().add(runtime);
		
		final JCheckBox system = new JCheckBox("Sandbox System.exit");
		system.setEnabled(false);
		system.setBounds(6, 164, 232, 23);
		getContentPane().add(system);

		txtThebytecodeclubexamplemainlstring = new JTextField();
		
		JButton btnNewButton = new JButton("Execute");
		btnNewButton.setBounds(6, 393, 232, 23);
		getContentPane().add(btnNewButton);
		
		boolean b = false;
		for(ClassNode classNode : BytecodeViewer.getLoadedClasses()) {
			for(Object o : classNode.methods.toArray()) {
				MethodNode m = (MethodNode) o;
			
				if(m.name.equals("main") && m.desc.equals("([Ljava/lang/String;)V")) {
					if(!b) {
						b = true;
						txtThebytecodeclubexamplemainlstring.setText(classNode.name+"."+m.name);
					}
				}
			}
		}
		
		if(!b)
			txtThebytecodeclubexamplemainlstring.setText("the/bytecode/club/Example.main");
		
		txtThebytecodeclubexamplemainlstring.setBounds(6, 281, 232, 20);
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
		textField_1.setText("127.0.0.1:9050");
		textField_1.setColumns(10);
		textField_1.setBounds(6, 220, 232, 20);
		getContentPane().add(textField_1);
		
		final JCheckBox forceProxy = new JCheckBox("Force Proxy (socks5, host:port):");
		forceProxy.setBounds(6, 190, 232, 23);
		getContentPane().add(forceProxy);
		
		final JCheckBox launchReflectionKit = new JCheckBox("Launch Reflection Kit On Successful Invoke");
		launchReflectionKit.setEnabled(false);
		launchReflectionKit.setBounds(6, 308, 232, 23);
		getContentPane().add(launchReflectionKit);
		
		final JCheckBox console = new JCheckBox("Launch Console");
		console.setBounds(6, 334, 232, 23);
		console.setSelected(true);
		getContentPane().add(console);
		
		final JCheckBox chckbxPrintToTerminal = new JCheckBox("Print To Command Line");
		chckbxPrintToTerminal.setSelected(true);
		chckbxPrintToTerminal.setBounds(6, 363, 232, 23);
		getContentPane().add(chckbxPrintToTerminal);
		this.setLocationRelativeTo(null);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				PluginManager.runPlugin(new EZInjection(accessModifiers.isSelected(), injectHooks.isSelected(), debugMethodCalls.isSelected(), invokeMethod.isSelected(), txtThebytecodeclubexamplemainlstring.getText(), runtime.isSelected(), system.isSelected(), textField.getText(), textField_1.getText(), forceProxy.isSelected(), launchReflectionKit.isSelected(), console.isSelected(), chckbxPrintToTerminal.isSelected()));
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
