package the.bytecode.club.bytecodeviewer.gui.components;

import java.io.Closeable;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JTextArea;

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
 * @author Konloch
 * @since 6/21/2021
 */
public class JTextAreaOutputStream extends OutputStream implements Closeable
{
	private StringBuilder sb = new StringBuilder();
	private final JTextArea textArea;
	private final PrintStream og;
	
	public JTextAreaOutputStream(JTextArea textArea, PrintStream og)
	{
		this.textArea = textArea;
		this.og = og;
	}
	
	public boolean noUpdateRequired()
	{
		return sb.length() <= 0;
	}
	
	public void update()
	{
		textArea.append(sb.toString());
		sb = new StringBuilder();
	}
	
	@Override
	public void write(int b)
	{
		sb.append((char) b);
		if(og != null)
			og.write(b);
	}
	
	public StringBuilder getBuffer()
	{
		return sb;
	}

	@Override
	public void close() {
		if (og != null)
			og.close();
	}
}
