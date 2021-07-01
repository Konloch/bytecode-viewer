package the.bytecode.club.bytecodeviewer.util;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.gui.components.SearchableRSyntaxTextArea;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author Konloch
 * @since 6/21/2021
 */
public class KeyEventDispatch implements KeyEventDispatcher
{
	@Override
	public boolean dispatchKeyEvent(KeyEvent e)
	{
		//hardcoded check for searchable syntax panes, this allows specific panels to ctrl + s save externally
		if(e.getSource() instanceof SearchableRSyntaxTextArea)
		{
			SearchableRSyntaxTextArea rSyntaxTextArea = (SearchableRSyntaxTextArea) e.getSource();
			if(rSyntaxTextArea.getOnCtrlS() != null)
				return false;
		}
		
		BytecodeViewer.checkHotKey(e);
		return false;
	}
}
