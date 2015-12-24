package the.bytecode.club.bytecodeviewer.gui;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Resources;
import the.bytecode.club.bytecodeviewer.Settings;

import javax.swing.*;
import java.awt.*;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 * *
 * This program is free software: you can redistribute it and/or modify    *
 * it under the terms of the GNU General Public License as published by  *
 * the Free Software Foundation, either version 3 of the License, or     *
 * (at your option) any later version.                                   *
 * *
 * This program is distributed in the hope that it will be useful,       *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 * GNU General Public License for more details.                          *
 * *
 * You should have received a copy of the GNU General Public License     *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

/**
 * The about frame.
 *
 * @author Konloch
 */
public class AboutWindow extends JFrame {
    private static final long serialVersionUID = -8230501978224923296L;
    private JTextArea textArea = new JTextArea();

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
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));
        textArea.setText("Bytecode Viewer " + BytecodeViewer.version + " is an open source program developed and maintained by Konloch (konloch@gmail.com) and samczsun 100% free and open sourced licensed under GPL v3 CopyLeft" + BytecodeViewer.nl +
                BytecodeViewer.nl +
                "Settings:" + BytecodeViewer.nl +
                "	Preview Copy: " + BytecodeViewer.previewCopy + BytecodeViewer.nl +
                "	Java: " + Settings.JAVA_LOCATION.get() + BytecodeViewer.nl +
                "	Javac: " + Settings.JAVAC_LOCATION.get() + BytecodeViewer.nl +
                "	BCV Dir: " + BytecodeViewer.getBCVDirectory() + BytecodeViewer.nl +
                "	Python 2.7 (or PyPy): " + Settings.PYTHON2_LOCATION.get() + BytecodeViewer.nl +
                "	Python 3.X (or PyPy): " + Settings.PYTHON3_LOCATION.get() + BytecodeViewer.nl +
                "	RT.jar:" + Settings.RT_LOCATION.get() + BytecodeViewer.nl +
                "	Optional Lib: " + Settings.PATH.get() + BytecodeViewer.nl +
                "	BCV Krakatau: v" + BytecodeViewer.krakatauVersion + BytecodeViewer.nl +
                "	Krakatau Dir: " + BytecodeViewer.krakatauDirectory.getAbsolutePath() + BytecodeViewer.nl +
                "	BCV Enjarify: v" + BytecodeViewer.enjarifyVersion + BytecodeViewer.nl +
                "	Enjarify Dir: " + BytecodeViewer.enjarifyDirectory.getAbsolutePath()+ BytecodeViewer.nl + BytecodeViewer.nl +
                "Command Line Input:" + BytecodeViewer.nl +
                "	-help                         Displays the help menu" + BytecodeViewer.nl +
                "	-list                         Displays the available decompilers" + BytecodeViewer.nl +
                "	-decompiler <decompiler>      Selects the decompiler, procyon by default" + BytecodeViewer.nl +
                "	-i <input file>               Selects the input file (Jar, Class, APK, ZIP, DEX all work automatically)" + BytecodeViewer.nl +
                "	-o <output file>              Selects the output file (Java or Java-Bytecode)" + BytecodeViewer.nl +
                "	-t <target classname>         Must either be the fully qualified classname or \"all\" to decompile all as zip" + BytecodeViewer.nl +
                "	-nowait                       Doesn't wait for the user to read the CLI messages" + BytecodeViewer.nl + BytecodeViewer.nl +
                "Keybinds:" + BytecodeViewer.nl +
                "	CTRL + O: Open/add new jar/class/apk" + BytecodeViewer.nl +
                "	CTLR + N: Reset the workspace" + BytecodeViewer.nl +
                "	CTRL + W: Closes the currently opened tab" + BytecodeViewer.nl +
                "	CTRL + T: Compile" + BytecodeViewer.nl +
                "	CTRL + S: Save classes as zip" + BytecodeViewer.nl +
                "	CTRL + R: Run (EZ-Inject) - dynamically load the classes and invoke a main class" + BytecodeViewer.nl +
                BytecodeViewer.nl +
                "Code from various projects has been used, including but not limited to:" + BytecodeViewer.nl +
                "	J-RET by WaterWolf" + BytecodeViewer.nl +
                "	JHexPane by Sam Koivu" + BytecodeViewer.nl +
                "	RSynaxPane by Robert Futrell" + BytecodeViewer.nl +
                "	Commons IO by Apache" + BytecodeViewer.nl +
                "	ASM by OW2" + BytecodeViewer.nl +
                "	FernFlower by Stiver" + BytecodeViewer.nl +
                "	Procyon by Mstrobel" + BytecodeViewer.nl +
                "	CFR by Lee Benfield" + BytecodeViewer.nl +
                "	CFIDE by Bibl" + BytecodeViewer.nl +
                "	Smali by JesusFreke" + BytecodeViewer.nl +
                "	Dex2Jar by pxb1..?" + BytecodeViewer.nl +
                "	Krakatau by Storyyeller" + BytecodeViewer.nl +
                "	JD-GUI + JD-Core by The Java-Decompiler Team" + BytecodeViewer.nl +
                "	Enjarify by Storyyeller" + BytecodeViewer.nl +
                BytecodeViewer.nl +
                "If you're interested in Java Reverse Engineering, join The Bytecode Club - https://the.bytecode.club");
    }
}
