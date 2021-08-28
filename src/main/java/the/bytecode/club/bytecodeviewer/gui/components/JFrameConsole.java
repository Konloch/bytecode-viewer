package the.bytecode.club.bytecodeviewer.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import javax.swing.JFrame;
import me.konloch.kontainer.io.DiskWriter;
import the.bytecode.club.bytecodeviewer.resources.IconResources;

import static the.bytecode.club.bytecodeviewer.Constants.tempDirectory;

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
 * A simple swing JFrame console
 *
 * @author Konloch
 * @since 6/25/2021
 */
public class JFrameConsole extends JFrame
{
	private String containerName;
	private int consoleID;
	private final SearchableJTextArea textArea;
	
	public JFrameConsole()
	{
		this("");
	}
	
	public JFrameConsole(String title)
	{
		setIconImages(IconResources.iconList);
		setTitle(title);
		setSize(new Dimension(542, 316));
		
		textArea = new SearchableJTextArea();
		getContentPane().add(textArea.getScrollPane(), BorderLayout.CENTER);
		
		this.setLocationRelativeTo(null);
	}
	
	/**
	 * Appends \r\n to the end of your string, then it puts it on the top.
	 *
	 * @param t the string you want to append
	 */
	public void appendText(String t)
	{
		setText((textArea.getText().isEmpty()
				? ""
				: textArea.getText() + "\r\n"
		) + t);
	}
	
	/**
	 * Sets the text
	 *
	 * @param t the text you want set
	 */
	public void setText(String t)
	{
		textArea.setText(trimConsoleText(t));
		textArea.setCaretPosition(0);
	}
	
	/**
	 * Returns the SearchableJTextArea pane
	 */
	public SearchableJTextArea getTextArea()
	{
		return textArea;
	}
	
	/**
	 * Returns the console ID
	 */
	public int getConsoleID()
	{
		return consoleID;
	}
	
	/**
	 * Returns the current container name
	 */
	public String getContainerName()
	{
		return containerName;
	}
	
	/**
	 * Set the console ID
	 */
	public void setConsoleID(int consoleID)
	{
		this.consoleID = consoleID;
	}
	
	/**
	 * Set the container name
	 */
	public void setContainerName(String containerName)
	{
		this.containerName = containerName;
	}
	
	/**
	 * Trims the console text to prevent killing the swing thread
	 */
	public String trimConsoleText(final String s)
	{
		int len = s.length();
		
		//TODO this should also be a setting eventually
		int max = 500_000;
		if(len >= max)
		{
			//TODO if two consoles are ran at the same time and exceed the maximum this file will be overwritten
			
			final File tempFile = new File(tempDirectory, "console_" + consoleID + ".log");
			
			//TODO this needs to be rewritten, it doesn't work for a plugin that causes multiple exception UIs
			new Thread(()->
			{
				//save to disk
				DiskWriter.replaceFile(tempFile.getAbsolutePath(), s, false);
			}, "Console Log Saving").start();
			
			//trim
			int skipped = len - max;
			String trimmed = s.substring(0, max);
			
			if(!trimmed.startsWith("WARNING: Skipping"))
				trimmed = ("WARNING: Skipping " + skipped + " chars, allowing " + max + "\n\r")
					+ "Full log saved to: " + tempFile.getAbsolutePath() + "\n\r\n\r"
					+ trimmed;
			
			return trimmed;
		}
		
		return s;
	}
	
	private static final long serialVersionUID = -5056940543411437508L;
}
