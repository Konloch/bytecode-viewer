package the.bytecode.club.bytecodeviewer.util;

import the.bytecode.club.bytecodeviewer.Configuration;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Konloch
 * @since 6/21/2021
 */
public class WindowClosingAdapter extends WindowAdapter
{
	@Override
	public void windowClosing(WindowEvent e)
	{
		Configuration.canExit = true;
		System.exit(0);
	}
}
