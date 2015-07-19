package the.bytecode.club.bytecodeviewer.gui;

import javax.swing.JFrame;

import java.awt.CardLayout;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JTextArea;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Resources;

import java.awt.Color;
import javax.swing.JScrollPane;

/**
 * The about frame.
 * 
 * @author Konloch
 *
 */

public class AboutWindow extends JFrame {
	JTextArea textArea = new JTextArea();
	public AboutWindow() {
		this.setIconImages(Resources.iconList);
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		setType(Type.UTILITY);
		setTitle("Bytecode Viewer - About - https://bytecodeviewer.com | https://the.bytecode.club");
		getContentPane().setLayout(new CardLayout(0, 0));
JScrollPane scrollPane = new JScrollPane();
getContentPane().add(scrollPane, "name_322439757638784");
textArea.setWrapStyleWord(true);
textArea.setEnabled(false);
textArea.setDisabledTextColor(Color.BLACK);
scrollPane.setViewportView(textArea);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
	}
	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int)BytecodeViewer.viewer.fontSpinner.getValue()));
		textArea.setText("Bytecode Viewer "+BytecodeViewer.version+" is an open source program developed and maintained by Konloch (konloch@gmail.com)\r\n"+
				"100% free and open sourced licensed under GPL v3 CopyLeft\r\n\r\n"+
				"Settings:"+BytecodeViewer.nl+
				"	BCV Dir: " + BytecodeViewer.getBCVDirectory()+BytecodeViewer.nl+
				"	Python 2.7 (or PyPy): " + BytecodeViewer.python+BytecodeViewer.nl+
				"	Python 3.X (or PyPy): " + BytecodeViewer.python3+BytecodeViewer.nl+
				"	RT.jar:" + BytecodeViewer.rt+BytecodeViewer.nl+
				"	Optional Lib: " + BytecodeViewer.library+BytecodeViewer.nl+
				"	BCV Krakatau: v" + BytecodeViewer.krakatauVersion+BytecodeViewer.nl+
				"	Krakatau Dir: " + BytecodeViewer.krakatauWorkingDirectory+BytecodeViewer.nl+
				"	BCV Enjarify: v" + BytecodeViewer.enjarifyVersion+BytecodeViewer.nl+
				"	Enjarify Dir: " + BytecodeViewer.enjarifyWorkingDirectory+BytecodeViewer.nl+BytecodeViewer.nl+
				"Command Line Input:"+BytecodeViewer.nl+
				"	-help                         Displays the help menu"+BytecodeViewer.nl+
				"	-list                         Displays the available decompilers"+BytecodeViewer.nl+
				"	-decompiler <decompiler>      Selects the decompiler, procyon by default"+BytecodeViewer.nl+
				"	-i <input file>               Selects the input file (Jar, Class, APK, ZIP, DEX all work automatically)"+BytecodeViewer.nl+
				"	-o <output file>              Selects the output file (Java or Java-Bytecode)"+BytecodeViewer.nl+
				"	-t <target classname>         Must either be the fully qualified classname or \"all\" to decompile all as zip"+BytecodeViewer.nl+
				"	-nowait                       Doesn't wait for the user to read the CLI messages"+BytecodeViewer.nl+BytecodeViewer.nl+
				"Keybinds:"+BytecodeViewer.nl+
				"	CTRL + O: Open/add new jar/class/apk"+BytecodeViewer.nl+
				"	CTLR + N: Reset the workspace"+BytecodeViewer.nl+
				"	CTRL + W: Closes the currently opened tab"+BytecodeViewer.nl+
				"	CTRL + T: Compile"+BytecodeViewer.nl+
				"	CTRL + S: Save classes as zip"+BytecodeViewer.nl+
				"	CTRL + R: Run (EZ-Inject) - dynamically load the classes and invoke a main class"+
				"\r\n\r\nCode from various projects has been used, including but not limited to:\r\n	J-RET by WaterWolf\r\n	JHexPane by Sam Koivu\r\n	RSynaxPane by Robert Futrell\r\n	Commons IO by Apache\r\n	ASM by OW2\r\n	FernFlower by Stiver\r\n	Procyon by Mstrobel\r\n	CFR by Lee Benfield\r\n	CFIDE by Bibl\r\n	Smali by JesusFreke\r\n	Dex2Jar by pxb1..?\r\n	Krakatau by Storyyeller\r\n	JD-GUI + JD-Core by The Java-Decompiler Team\r\n	Enjarify by Storyyeller\r\n\r\nIf you're interested in Java Reverse Engineering, join The Bytecode Club\r\nhttps://the.bytecode.club");
		
	}

	private static final long serialVersionUID = -8230501978224923296L;

}
