package the.bytecode.club.bytecodeviewer.gui.components;

import the.bytecode.club.bytecodeviewer.Resources;

import javax.swing.*;
import java.awt.*;

/**
 * A simple swing JFrame console
 *
 * @author Konloch
 * @since 6/25/2021
 */
public class JFrameConsole extends JFrame
{
	private final SearchableJTextArea textArea = new SearchableJTextArea();
	
	public JFrameConsole(String title)
	{
		this.setIconImages(Resources.iconList);
		setTitle(title);
		setSize(new Dimension(542, 316));
		
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
		textArea.setText(t);
		textArea.setCaretPosition(0);
	}
	
	public SearchableJTextArea getTextArea()
	{
		return textArea;
	}
	
	private static final long serialVersionUID = -5056940543411437508L;
}
