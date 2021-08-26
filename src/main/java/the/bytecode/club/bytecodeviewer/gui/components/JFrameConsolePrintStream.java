package the.bytecode.club.bytecodeviewer.gui.components;

import java.io.PrintStream;
import javax.swing.SwingUtilities;
import the.bytecode.club.bytecodeviewer.Constants;

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
	private Thread updateThread;
	private boolean finished;
	private long lastUpdate = 0;
	
	public JFrameConsolePrintStream(String title)
	{
		this(title, true);
	}
	
	public JFrameConsolePrintStream(String title, boolean preserveOriginalOutput)
	{
		super(title);
		
		textAreaOutputStreamOut = new JTextAreaOutputStream(getTextArea(), preserveOriginalOutput ? System.out : null);
		textAreaOutputStreamErr = new JTextAreaOutputStream(getTextArea(), preserveOriginalOutput ? System.err : null);
		
		System.setOut(new PrintStream(textAreaOutputStreamOut));
		System.setErr(new PrintStream(textAreaOutputStreamErr));
	}
	
	@Override
	public void setVisible(boolean b)
	{
		super.setVisible(b);
		
		if(b && updateThread == null)
		{
			updateThread = new Thread(() ->
			{
				while (isVisible() && !finished)
				{
					update();
					
					try {
						Thread.sleep(10);
					} catch (InterruptedException ignored) { }
				}
				
				lastUpdate = 0;
				update();
			}, "Lazy Console Update");
			
			updateThread.start();
		}
	}
	
	public void finished()
	{
		finished = true;
		System.setErr(Constants.ERR);
		System.setOut(Constants.OUT);
	}
	
	public JTextAreaOutputStream getTextAreaOutputStreamErr()
	{
		return textAreaOutputStreamErr;
	}
	
	public JTextAreaOutputStream getTextAreaOutputStreamOut()
	{
		return textAreaOutputStreamOut;
	}
	
	private void update()
	{
		if(System.currentTimeMillis()-lastUpdate <= 50)
			return;
		
		lastUpdate = System.currentTimeMillis();
		
		//update only if required
		if(textAreaOutputStreamErr.noUpdateRequired() && textAreaOutputStreamOut.noUpdateRequired())
			return;
		
		SwingUtilities.invokeLater(()->
		{
			//print output to the pane
			textAreaOutputStreamOut.update();
			
			//print error to the pane
			textAreaOutputStreamErr.update();
			
			//reformat the pane
			String content = getTextArea().getText();
			if(content.contains("File `"))
			{
				String[] test = content.split("\r?\n");
				
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
		});
	}
}
