package the.bytecode.club.bytecodeviewer.gui;

import javax.swing.JFrame;

import java.awt.Dimension;
import java.awt.CardLayout;

import javax.swing.JTextArea;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Resources;

import java.awt.Color;

/**
 * The about frame.
 * 
 * @author Konloch
 *
 */

public class AboutWindow extends JFrame {
	public AboutWindow() {
		this.setIconImages(Resources.iconList);
		setSize(new Dimension(446, 434));
		setType(Type.UTILITY);
		setTitle("Bytecode Viewer - About");
		getContentPane().setLayout(new CardLayout(0, 0));

		JTextArea txtrBytecodeViewerIs = new JTextArea();
		txtrBytecodeViewerIs.setDisabledTextColor(Color.BLACK);
		txtrBytecodeViewerIs.setWrapStyleWord(true);
		getContentPane().add(txtrBytecodeViewerIs, "name_140466526081695");
		txtrBytecodeViewerIs
				.setText("Bytecode Viewer "+BytecodeViewer.version+" is an open source program\r\ndeveloped by Konloch (konloch@gmail.com)\r\nDir: C:\\Users\\null\\.Bytecode-Viewer\r\n\r\nIt uses code from the following:\r\n    J-RET by WaterWolf\r\n    JHexPane by Sam Koivu\r\n    RSynaxPane by Robert Futrell\r\n    Commons IO by Apache\r\n    ASM by OW2\r\n    FernFlower by Stiver\r\n    Procyon by Mstrobel\r\n    CFR by Lee Benfield\r\n    CFIDE by Bibl\r\n    Smali by JesusFreke\r\n    Dex2Jar by pxb1..?\r\n    Krakatau by Storyyeller\r\n\r\nIf you're interested in Java Reverse\r\nEngineering, join The Bytecode Club\r\nhttps://the.bytecode.club");
		txtrBytecodeViewerIs.setEnabled(false);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
	}

	private static final long serialVersionUID = -8230501978224923296L;

}
