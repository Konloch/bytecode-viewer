package the.bytecode.club.bytecodeviewer.gui;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.CardLayout;
import javax.swing.JTextArea;
import java.awt.Color;

public class AboutWindow extends JFrame {
	public AboutWindow() {
		setSize(new Dimension(403, 484));
		setType(Type.UTILITY);
		setTitle("Bytecode Viewer - About");
		getContentPane().setLayout(new CardLayout(0, 0));
		
		JTextArea txtrBytecodeViewerIs = new JTextArea();
		txtrBytecodeViewerIs.setDisabledTextColor(Color.BLACK);
		txtrBytecodeViewerIs.setWrapStyleWord(true);
		getContentPane().add(txtrBytecodeViewerIs, "name_140466526081695");
		txtrBytecodeViewerIs.setText("Bytecode Viewer is an open source program\r\ndeveloped by Konloch (konloch@gmail.com)\r\n\r\nIt uses code from the following:\r\n    J-RET by WaterWolf\r\n    JHexPane by Sam Koivu\r\n    JSyntaxPane by Ayman Al\r\n    Commons IO by Apache\r\n    ASM by OW2\r\n    CFIDE  by Bibl\r\n    FernFlower by Stiver\r\n    Procyon by Mstrobel\r\n    CFR by Lee Benfield\r\n\r\nLimitations:\r\n    Syntax highlighting on files that are\r\nbigger than 10K lines can take a while to\r\nload, you may want to disable the syntax\r\nhighlighting for large files.\r\n\r\nIf you're interested in Java Reverse\r\nEngineering, join The Bytecode Club\r\nhttp://the.bytecode.club");
		txtrBytecodeViewerIs.setEnabled(false);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
	}

	private static final long serialVersionUID = -8230501978224923296L;

}
