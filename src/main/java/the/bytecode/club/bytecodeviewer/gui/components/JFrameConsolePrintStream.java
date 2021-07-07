package the.bytecode.club.bytecodeviewer.gui.components;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Constants;

import java.io.PrintStream;

import static the.bytecode.club.bytecodeviewer.Constants.nl;

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
 * A swing console that can print out from PrintStreams
 *
 * @author Konloch
 * @since 6/25/2021
 */
public class JFrameConsolePrintStream extends JFrameConsole
{
	private final JTextAreaOutputStream textAreaOutputStreamOut;
	private final JTextAreaOutputStream textAreaOutputStreamErr;
	
	public JFrameConsolePrintStream(String title)
	{
		super(title);
		
		textAreaOutputStreamOut = new JTextAreaOutputStream(getTextArea(), System.out);
		textAreaOutputStreamErr = new JTextAreaOutputStream(getTextArea(), System.err);
		
		System.setOut(new PrintStream(textAreaOutputStreamOut));
		System.setErr(new PrintStream(textAreaOutputStreamErr));
	}
	
	public void finished()
	{
		System.setErr(Constants.ERR);
		System.setOut(Constants.OUT);
	}
	
	public void pretty()
	{
		textAreaOutputStreamOut.update();
		textAreaOutputStreamErr.update();
		String[] test;
		if (getTextArea().getText().split("\n").length >= 2)
			test = getTextArea().getText().split("\n");
		else
			test = getTextArea().getText().split("\r");
		
		StringBuilder replace = new StringBuilder();
		for (String s : test)
		{
			if (s.startsWith("File '"))
			{
				String[] split = s.split("'");
				String start = split[0] + "'" + split[1] + "', ";
				s = s.substring(start.length());
			}
			replace.append(s).append(nl);
		}
		setText(replace.toString());
	}
}
