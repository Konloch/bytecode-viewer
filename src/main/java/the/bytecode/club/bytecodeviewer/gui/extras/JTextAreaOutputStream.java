package the.bytecode.club.bytecodeviewer.gui.extras;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Konloch
 * @since 6/21/2021
 */
public class JTextAreaOutputStream extends OutputStream
{
	private final StringBuilder sb = new StringBuilder();
	private final JTextArea textArea;
	
	public JTextAreaOutputStream(JTextArea textArea)
	{
		this.textArea = textArea;
	}
	
	public void update()
	{
		textArea.append(sb.toString());
	}
	
	@Override
	public void write(int b) throws IOException
	{
		sb.append((char) b);
	}
}
