package the.bytecode.club.bytecodeviewer.plugins;

import javax.swing.JFrame;

import java.awt.Dimension;

import javax.swing.JScrollPane;

import java.awt.BorderLayout;

import javax.swing.JTextArea;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;

/**
 * A simple console GUI.
 * 
 * @author Konloch
 *
 */

public class PluginConsole extends JFrame {
	JTextArea textArea = new JTextArea();
	public PluginConsole(String pluginName) {
    	this.setIconImages(BytecodeViewer.iconList);
		setTitle("Bytecode Viewer - Plugin Console - " + pluginName);
		setSize(new Dimension(542, 316));
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		scrollPane.setViewportView(textArea);
		this.setLocationRelativeTo(null);
	}
	
	public void appendText(String t) {
		textArea.setText((textArea.getText().isEmpty() ? "" : textArea.getText()+"\r\n")+t);
		textArea.setCaretPosition(0);
	}

	private static final long serialVersionUID = -6556940545421437508L;

}
