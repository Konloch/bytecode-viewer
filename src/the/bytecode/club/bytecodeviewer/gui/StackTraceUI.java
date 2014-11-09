package the.bytecode.club.bytecodeviewer.gui;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import java.awt.Dimension;
import java.awt.CardLayout;

import javax.swing.JTextArea;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;

import java.awt.Color;
import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceUI extends JFrame {
	
	public StackTraceUI(Exception e) {
    	this.setIconImages(BytecodeViewer.iconList);
		setSize(new Dimension(600, 400));
		setTitle("Bytecode Viewer "+BytecodeViewer.version+" - Stack Trace - Send this to @Konloch.");
		getContentPane().setLayout(new CardLayout(0, 0));
		
		JTextArea txtrBytecodeViewerIs = new JTextArea();
		txtrBytecodeViewerIs.setDisabledTextColor(Color.BLACK);
		txtrBytecodeViewerIs.setWrapStyleWord(true);
		getContentPane().add(new JScrollPane(txtrBytecodeViewerIs), "name_140466576080695");
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		e.printStackTrace();
		
		txtrBytecodeViewerIs.setText(sw.toString());
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private static final long serialVersionUID = -5230501978224926296L;

}
