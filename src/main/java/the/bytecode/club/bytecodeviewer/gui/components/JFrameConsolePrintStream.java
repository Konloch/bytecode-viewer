package the.bytecode.club.bytecodeviewer.gui.components;

import java.io.PrintStream;

import static the.bytecode.club.bytecodeviewer.Constants.nl;

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
