package the.bytecode.club.bytecodeviewer.util;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import java.awt.*;

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
			String[] test = (textArea.getText().split("\n").length >= 2
					? textArea.getText().split("\n")
					: textArea.getText().split("\r"));
			
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
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
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
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
		}
	}
}
