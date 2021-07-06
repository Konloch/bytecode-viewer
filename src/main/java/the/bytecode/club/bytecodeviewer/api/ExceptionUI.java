package the.bytecode.club.bytecodeviewer.api;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.Resources;

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
 * A simple class designed to show exceptions in the UI.
 *
 * @author Konloch
 */

public class ExceptionUI extends JFrame
{
    /**
     * @param e The exception to be shown
     */
    public ExceptionUI(Throwable e) {
        setupException(e, "@Konloch - konloch@gmail.com");
    }

    /**
     * @param e The exception to be shown
     */
    public ExceptionUI(String e) {
        setupFrame(e, "@Konloch - konloch@gmail.com");
    }

    /**
     * @param e      The exception to be shown
     * @param author the author of the plugin throwing this exception.
     */
    public ExceptionUI(Throwable e, String author) {
        setupException(e, author);
    }

    /**
     * @param e      The exception to be shown
     * @param author the author of the plugin throwing this exception.
     */
    public ExceptionUI(String e, String author) {
        setupFrame(e, author);
    }

    private void setupException(Throwable error, String author)
    {
        //exceptions are completely hidden
        if(Configuration.silenceExceptionGUI > 0)
            return;
    
        //exception GUI is disabled but printstack is still enabled
        if(Configuration.pauseExceptionGUI > 0)
        {
            error.printStackTrace();
            return;
        }
        
        StringWriter sw = new StringWriter();
        error.printStackTrace(new PrintWriter(sw));
        error.printStackTrace();
        
        setupFrame(sw.toString(), author);
    }

    private void setupFrame(String error, String author)
    {
        this.setIconImages(Resources.iconList);
        setSize(new Dimension(600, 400));
        setTitle("Bytecode Viewer " + VERSION + " - Stack Trace - Send this to " + author);
        getContentPane().setLayout(new CardLayout(0, 0));

        JTextArea textArea = new JTextArea();
        textArea.setDisabledTextColor(Color.BLACK);
        textArea.setWrapStyleWord(true);
        getContentPane().add(new JScrollPane(textArea));

        textArea.setText(
                "Please send this error log to " + author +
                "\nIf you " +
                "\nIif you hold appropriate legal rights the relevant class file." +
                "Bytecode Viewer Version: " + VERSION +
                ", Preview Copy: " + PREVIEW_COPY +
                ", Fat Jar: " + FAT_JAR +
                ", OS: " + System.getProperty("os.name") +
                ", Java: " + System.getProperty("java.version") +
                nl + nl + error);
        
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
    private static final long serialVersionUID = -5230501978224926296L;
}