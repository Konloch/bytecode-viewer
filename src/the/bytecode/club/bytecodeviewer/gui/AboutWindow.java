package the.bytecode.club.bytecodeviewer.gui;

import javax.swing.JFrame;

import java.awt.Dimension;
import java.awt.CardLayout;

import javax.swing.JTextArea;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;

import java.awt.Color;

public class AboutWindow extends JFrame {
	public AboutWindow() {
		this.setIconImages(BytecodeViewer.iconList);
		setSize(new Dimension(446, 374));
		setType(Type.UTILITY);
		setTitle("Bytecode Viewer - About");
		getContentPane().setLayout(new CardLayout(0, 0));

		JTextArea txtrBytecodeViewerIs = new JTextArea();
		txtrBytecodeViewerIs.setDisabledTextColor(Color.BLACK);
		txtrBytecodeViewerIs.setWrapStyleWord(true);
		getContentPane().add(txtrBytecodeViewerIs, "name_140466526081695");
		txtrBytecodeViewerIs
				.setText("Bytecode Viewer 2.3.0 is an open source program\r\ndeveloped by Konloch (konloch@gmail.com)\r\nDir: "
						+ BytecodeViewer.getBCVDirectory()
						+ "\r\n\r\nIt uses code from the following:\r\n    J-RET by WaterWolf\r\n    JHexPane by Sam Koivu\r\n    RSyntaxTextArea by Bobbylight\r\n    Commons IO by Apache\r\n    ASM by OW2\r\n    CFIDE  by Bibl\r\n    FernFlower by Stiver\r\n    Procyon by Mstrobel\r\n    CFR by Lee Benfield\r\n\r\nIf you're interested in Java Reverse\r\nEngineering, join The Bytecode Club\r\nhttps://the.bytecode.club");
		txtrBytecodeViewerIs.setEnabled(false);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
	}

	private static final long serialVersionUID = -8230501978224923296L;

}
