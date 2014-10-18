package the.bytecode.club.bytecodeviewer.gui;

import javax.swing.JFrame;

import java.awt.Dimension;

import javax.swing.JCheckBox;
import javax.swing.JButton;

import the.bytecode.club.bytecodeviewer.plugins.MaliciousCodeScanner;
import the.bytecode.club.bytecodeviewer.plugins.PluginManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MaliciousCodeScannerOptions extends JFrame {
	public MaliciousCodeScannerOptions() {
		setSize(new Dimension(250, 277));
		setResizable(false);
		setTitle("Malicious Code Scanner Options");
		getContentPane().setLayout(null);
		
		final JCheckBox chckbxJavalangreflection = new JCheckBox("java/lang/reflection");
		chckbxJavalangreflection.setSelected(true);
		chckbxJavalangreflection.setBounds(6, 7, 232, 23);
		getContentPane().add(chckbxJavalangreflection);
		
		final JCheckBox chckbxJavanet = new JCheckBox("java/net");
		chckbxJavanet.setSelected(true);
		chckbxJavanet.setBounds(6, 59, 232, 23);
		getContentPane().add(chckbxJavanet);
		
		final JCheckBox chckbxJavaio = new JCheckBox("java/io");
		chckbxJavaio.setBounds(6, 85, 232, 23);
		getContentPane().add(chckbxJavaio);
		
		final JCheckBox chckbxJavalangruntime = new JCheckBox("java/lang/Runtime");
		chckbxJavalangruntime.setSelected(true);
		chckbxJavalangruntime.setBounds(6, 33, 232, 23);
		getContentPane().add(chckbxJavalangruntime);
		
		final JCheckBox chckbxLdcContainswww = new JCheckBox("LDC contains 'www.'");
		chckbxLdcContainswww.setSelected(true);
		chckbxLdcContainswww.setBounds(6, 111, 232, 23);
		getContentPane().add(chckbxLdcContainswww);
		
		final JCheckBox chckbxLdcContainshttp = new JCheckBox("LDC contains 'http://'");
		chckbxLdcContainshttp.setSelected(true);
		chckbxLdcContainshttp.setBounds(6, 137, 232, 23);
		getContentPane().add(chckbxLdcContainshttp);
		
		final JCheckBox chckbxLdcContainshttps = new JCheckBox("LDC contains 'https://'");
		chckbxLdcContainshttps.setSelected(true);
		chckbxLdcContainshttps.setBounds(6, 163, 232, 23);
		getContentPane().add(chckbxLdcContainshttps);
		
		final JCheckBox chckbxLdcMatchesIp = new JCheckBox("LDC matches IP regex");
		chckbxLdcMatchesIp.setSelected(true);
		chckbxLdcMatchesIp.setBounds(6, 189, 232, 23);
		getContentPane().add(chckbxLdcMatchesIp);
		
		JButton btnNewButton = new JButton("Start Scanning");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				PluginManager.runPlugin(new MaliciousCodeScanner(chckbxJavalangreflection.isSelected(),
				chckbxJavalangruntime.isSelected(), chckbxJavanet.isSelected(), chckbxJavaio.isSelected(),
				chckbxLdcContainswww.isSelected(), chckbxLdcContainshttp.isSelected(), chckbxLdcContainshttps.isSelected(),
				chckbxLdcMatchesIp.isSelected()));
				dispose();
			}
		});
		btnNewButton.setBounds(6, 219, 232, 23);
		getContentPane().add(btnNewButton);
		this.setLocationRelativeTo(null);
	}
	
	private static final long serialVersionUID = -2662514582647810868L;
}
