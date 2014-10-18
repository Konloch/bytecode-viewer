package the.bytecode.club.bytecodeviewer.plugins;

import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.JTextArea;

/**
 * A simple console GUI.
 * 
 * @author Konloch
 *
 */

public class PluginConsole extends JFrame {
	JTextArea textArea = new JTextArea();
	public PluginConsole(String pluginName) {
		setTitle("Bytecode Viewer - Plugin Console - " + pluginName);
		setSize(new Dimension(542, 316));
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		scrollPane.setViewportView(textArea);
		this.setLocationRelativeTo(null);
	}
	
	public void appendText(String t) {
		textArea.setText((textArea.getText().isEmpty() ? "" : textArea.getText()+"\r\n")+t);
		textArea.setCaretPosition(textArea.getLineCount());
	}

	private static final long serialVersionUID = -6556940545421437508L;

}
