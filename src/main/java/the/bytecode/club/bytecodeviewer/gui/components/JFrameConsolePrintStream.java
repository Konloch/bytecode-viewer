package the.bytecode.club.bytecodeviewer.gui.components;

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
	private final PrintStream originalOut;
	private final PrintStream newPrintStream;
	private final JTextAreaOutputStream s;
	
	public JFrameConsolePrintStream(String title, PrintStream originalOut)
	{
		super(title);
		
		this.originalOut = originalOut;
		
		s = new JTextAreaOutputStream(getTextArea());
		newPrintStream = new PrintStream(s);
	}
	
	public void finished()
	{
		if (originalOut != null)
			System.setErr(originalOut);
	}
	
	public PrintStream getNewPrintStream()
	{
		return newPrintStream;
	}
	
	public void pretty()
	{
		s.update();
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
