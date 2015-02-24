package the.bytecode.club.bytecodeviewer.api;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import java.awt.Dimension;
import java.awt.CardLayout;

import javax.swing.JTextArea;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Resources;

import java.awt.Color;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A simple class designed to show exceptions in the UI.
 * 
 * @author Konloch
 * 
 */

public class ExceptionUI extends JFrame {

	private static final long serialVersionUID = -5230501978224926296L;

	/**
	 * @param e
	 *            The exception to be shown
	 */
	public ExceptionUI(Exception e) {
		setup(e, "@Konloch - konloch@gmail.com");
	}
	
	/**
	 * @param e
	 *            The exception to be shown
	 */
	public ExceptionUI(String e) {
		setup(e, "@Konloch - konloch@gmail.com");
	}

	/**
	 * @param e
	 *            The exception to be shown
	 * @param author
	 *            the author of the plugin throwing this exception.
	 */
	public ExceptionUI(Exception e, String author) {
		setup(e, author);
	}

	/**
	 * @param e
	 *            The exception to be shown
	 * @param author
	 *            the author of the plugin throwing this exception.
	 */
	public ExceptionUI(String e, String author) {
		setup(e, author);
	}

	private void setup(Exception e, String author) {

		this.setIconImages(Resources.iconList);
		setSize(new Dimension(600, 400));
		setTitle("Bytecode Viewer " + BytecodeViewer.version
				+ " - Stack Trace - Send this to " + author);
		getContentPane().setLayout(new CardLayout(0, 0));

		JTextArea txtrBytecodeViewerIs = new JTextArea();
		txtrBytecodeViewerIs.setDisabledTextColor(Color.BLACK);
		txtrBytecodeViewerIs.setWrapStyleWord(true);
		getContentPane().add(new JScrollPane(txtrBytecodeViewerIs),
				"name_140466576080695");
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		e.printStackTrace();

		txtrBytecodeViewerIs.setText("Bytecode Viewer Version: " + BytecodeViewer.version + BytecodeViewer.nl + BytecodeViewer.nl + sw.toString());
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	private void setup(String e, String author) {
		this.setIconImages(Resources.iconList);
		setSize(new Dimension(600, 400));
		setTitle("Bytecode Viewer " + BytecodeViewer.version
				+ " - Stack Trace - Send this to " + author);
		getContentPane().setLayout(new CardLayout(0, 0));

		JTextArea txtrBytecodeViewerIs = new JTextArea();
		txtrBytecodeViewerIs.setDisabledTextColor(Color.BLACK);
		txtrBytecodeViewerIs.setWrapStyleWord(true);
		getContentPane().add(new JScrollPane(txtrBytecodeViewerIs),
				"name_140466576080695");
		txtrBytecodeViewerIs.setText(e);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

}
