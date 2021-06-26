package the.bytecode.club.bytecodeviewer.gui.components;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Resources;

import static the.bytecode.club.bytecodeviewer.Configuration.*;
import static the.bytecode.club.bytecodeviewer.Constants.*;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 *                                                                         *
 * This program is free software: you can redistribute it and/or modify    *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

/**
 * The about frame.
 *
 * @author Konloch
 */

public class AboutWindow extends JFrame
{
    JTextArea textArea = new JTextArea();

    public AboutWindow()
    {
        this.setIconImages(Resources.iconList);
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setType(Type.UTILITY);
        setTitle("Bytecode Viewer - About - https://bytecodeviewer.com | https://the.bytecode.club");
        getContentPane().setLayout(new CardLayout(0, 0));
        JScrollPane scrollPane = new JScrollPane();
        getContentPane().add(scrollPane, "name_845520934713596");
        textArea.setWrapStyleWord(true);
        textArea.setEnabled(false);
        textArea.setDisabledTextColor(Color.BLACK);
        scrollPane.setViewportView(textArea);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
    }

    @Override
    public void setVisible(boolean b)
    {
        super.setVisible(b);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));
        textArea.setText("Bytecode Viewer " + VERSION + " is an open source program developed and " +
                "maintained by Konloch (konloch@gmail.com) 100% free and open sourced licensed under GPL v3 " +
                "CopyLeft" + nl + nl +
                "Settings:" + nl +
                "	Preview Copy: " + PREVIEW_COPY + nl +
                "	Fat Jar: " + FAT_JAR + nl +
                "	Java: " + java + nl +
                "	Javac: " + javac + nl +
                "	BCV Dir: " + getBCVDirectory() + nl +
                "	Python 2.7 (or PyPy): " + python + nl +
                "	Python 3.X (or PyPy): " + python3 + nl +
                "	RT.jar:" + rt + nl +
                "	Optional Lib: " + library + nl +
                "	BCV Krakatau: v" + krakatauVersion + nl +
                "	Krakatau Dir: " + krakatauWorkingDirectory + nl +
                "	BCV Enjarify: v" + enjarifyVersion + nl +
                "	Enjarify Dir: " + enjarifyWorkingDirectory + nl + nl +
                "Command Line Input:" + nl +
                "	-help                         Displays the help menu" + nl +
                "	-list                         Displays the available decompilers" + nl +
                "	-decompiler <decompiler>      Selects the decompiler, procyon by default" + nl +
                "	-i <input file>               Selects the input file (Jar, Class, APK, ZIP, DEX all work " +
                "automatically)" + nl +
                "	-o <output file>              Selects the output file (Java or Java-Bytecode)" + nl +
                "	-t <target classname>         Must either be the fully qualified classname or \"all\" to decompile" +
                " all as zip" + nl +
                "	-nowait                       Doesn't wait for the user to read the CLI messages" + nl + nl +
                "Keybinds:" + nl +
                "	CTRL + O: Open/add new jar/class/apk" + nl +
                "	CTLR + N: Reset the workspace" + nl +
                "	CTRL + W: Closes the currently opened tab" + nl +
                "	CTRL + T: Compile" + nl +
                "	CTRL + S: Save classes as zip" + nl +
                "	CTRL + R: Run (EZ-Inject) - dynamically load the classes and invoke a main class" +
                "\r\n\r\nCode from various projects has been used, including but not limited to:\r\n	J-RET by " +
                "WaterWolf\r\n	JHexPane by Sam Koivu\r\n	RSynaxPane by Robert Futrell\r\n	Commons IO by " +
                "Apache\r\n	ASM by OW2\r\n	FernFlower by Stiver\r\n	Procyon by Mstrobel\r\n	CFR by Lee " +
                "Benfield\r\n	CFIDE by Bibl\r\n	Smali by JesusFreke\r\n	Dex2Jar by pxb1..?\r\n	Krakatau by " +
                "Storyyeller\r\n	JD-GUI + JD-Core by The Java-Decompiler Team\r\n	Enjarify by " +
                "Storyyeller\r\n\r\nIf you're interested in Java Reverse Engineering, join The Bytecode Club - " +
                "https://the.bytecode.club");

    }

    private static final long serialVersionUID = -8230501978224923296L;
}
