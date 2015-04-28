package the.bytecode.club.bytecodeviewer.gui;

import javax.swing.JFrame;

import java.awt.CardLayout;
import java.awt.Toolkit;

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
	JTextArea txtrBytecodeViewerIs = new JTextArea();
	public AboutWindow() {
		this.setIconImages(Resources.iconList);
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		setType(Type.UTILITY);
		setTitle("Bytecode Viewer - About - https://bytecodeviewer.com | https://the.bytecode.club");
		getContentPane().setLayout(new CardLayout(0, 0));

		txtrBytecodeViewerIs.setDisabledTextColor(Color.BLACK);
		txtrBytecodeViewerIs.setWrapStyleWord(true);
		getContentPane().add(txtrBytecodeViewerIs, "name_140466526081695");txtrBytecodeViewerIs.setEnabled(false);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
	}
	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		txtrBytecodeViewerIs
				.setText("Bytecode Viewer "+BytecodeViewer.version+" is an open source program developed and maintained by Konloch (konloch@gmail.com)\r\n"+
				"100% free and open sourced licensed under GPL v3 CopyLef\r\n\r\n"+
				"Settings:"+BytecodeViewer.nl+
				"BCV Dir: " + BytecodeViewer.getBCVDirectory()+BytecodeViewer.nl+
				"Python: " + BytecodeViewer.python+BytecodeViewer.nl+
				"RT.jar:" + BytecodeViewer.rt+BytecodeViewer.nl+
				"Optional Lib: " + BytecodeViewer.library+BytecodeViewer.nl+
				"BCV Krakatau: v" + BytecodeViewer.krakatauVersion+BytecodeViewer.nl+
				"Krakatau Dir: " + BytecodeViewer.krakatauWorkingDirectory+BytecodeViewer.nl+BytecodeViewer.nl+
				"Keybinds:"+BytecodeViewer.nl+
				"CTRL + O: Open/add new jar/class/apk"+BytecodeViewer.nl+
				"CTLR + N: Reset the workspace"+BytecodeViewer.nl+
				"CTRL + T: Compile"+BytecodeViewer.nl+
				"CTRL + S: Save classes as zip"+BytecodeViewer.nl+
				"CTRL + R: Run (EZ-Inject) - dynamically load the classes and invoke a main class"+
				"\r\n\r\nIt uses code from the following:\r\n    J-RET by WaterWolf\r\n    JHexPane by Sam Koivu\r\n    RSynaxPane by Robert Futrell\r\n    Commons IO by Apache\r\n    ASM by OW2\r\n    FernFlower by Stiver\r\n    Procyon by Mstrobel\r\n    CFR by Lee Benfield\r\n    CFIDE by Bibl\r\n    Smali by JesusFreke\r\n    Dex2Jar by pxb1..?\r\n    Krakatau by Storyyeller\r\n\r\nIf you're interested in Java Reverse Engineering, join The Bytecode Club\r\nhttps://the.bytecode.club");
		
	}

	private static final long serialVersionUID = -8230501978224923296L;

}
