package the.bytecode.club.bytecodeviewer.util;

import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;

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
 * This allows functionality to main the same between JTextArea and RSyntaxTextArea text panels
 *
 * @author Konloch
 * @since 6/25/2021
 */
public class JTextAreaUtils
{
	private static final DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 62, 150));
	
	/**
	 * This was really interesting to write.
	 *
	 * @author Konloch
	 */
	public static void search(JTextArea textArea, String search, boolean forwardSearchDirection, boolean caseSensitiveSearch)
	{
		try
		{
			if (search.isEmpty())
			{
				highlight(textArea, "", caseSensitiveSearch);
				return;
			}
			
			int startLine = textArea.getDocument().getDefaultRootElement()
					.getElementIndex(textArea.getCaretPosition()) + 1;
			
			int currentLine = 1;
			boolean canSearch = false;
			String[] test = textArea.getText().split("\r?\n");
			
			int lastGoodLine = -1;
			int firstPos = -1;
			boolean found = false;
			
			if (forwardSearchDirection)
			{
				for (String s : test)
				{
					if (!caseSensitiveSearch)
					{
						s = s.toLowerCase();
						search = search.toLowerCase();
					}
					
					if (currentLine == startLine)
					{
						canSearch = true;
					}
					else if (s.contains(search))
					{
						if (canSearch)
						{
							textArea.setCaretPosition(textArea.getDocument()
									.getDefaultRootElement()
									.getElement(currentLine - 1)
									.getStartOffset());
							
							canSearch = false;
							found = true;
						}
						
						if (firstPos == -1)
							firstPos = currentLine;
					}
					
					currentLine++;
				}
				
				if (!found && firstPos != -1)
				{
					textArea.setCaretPosition(textArea.getDocument()
							.getDefaultRootElement().getElement(firstPos - 1)
							.getStartOffset());
				}
			}
			else
			{
				canSearch = true;
				for (String s : test)
				{
					if (!caseSensitiveSearch)
					{
						s = s.toLowerCase();
						search = search.toLowerCase();
					}
					
					if (s.contains(search))
					{
						if (lastGoodLine != -1 && canSearch)
							textArea.setCaretPosition(textArea.getDocument()
									.getDefaultRootElement()
									.getElement(lastGoodLine - 1)
									.getStartOffset());
						
						lastGoodLine = currentLine;
						
						if (currentLine >= startLine)
							canSearch = false;
					}
					
					currentLine++;
				}
				
				if (lastGoodLine != -1
						&& textArea.getDocument().getDefaultRootElement()
						.getElementIndex(textArea.getCaretPosition()) + 1 == startLine)
				{
					textArea.setCaretPosition(textArea.getDocument()
							.getDefaultRootElement()
							.getElement(lastGoodLine - 1).getStartOffset());
				}
			}
			
			highlight(textArea, search, caseSensitiveSearch);
		}
		catch (Exception e)
		{
			BytecodeViewer.handleException(e);
		}
	}
	
	public static void highlight(JTextArea textArea,String pattern, boolean caseSensitiveSearch)
	{
		if (pattern.isEmpty())
		{
			textArea.getHighlighter().removeAllHighlights();
			return;
		}
		
		try
		{
			Highlighter highlighter = textArea.getHighlighter();
			highlighter.removeAllHighlights();
			Document doc = textArea.getDocument();
			String text = doc.getText(0, doc.getLength());
			int pos = 0;
			
			if (!caseSensitiveSearch)
			{
				pattern = pattern.toLowerCase();
				text = text.toLowerCase();
			}
			
			// Search for pattern
			while ((pos = text.indexOf(pattern, pos)) >= 0)
			{
				// Create highlighter using private painter and apply around
				// pattern
				highlighter.addHighlight(pos, pos + pattern.length(), painter);
				pos += pattern.length();
			}
		}
		catch (Exception e)
		{
			BytecodeViewer.handleException(e);
		}
	}
}
