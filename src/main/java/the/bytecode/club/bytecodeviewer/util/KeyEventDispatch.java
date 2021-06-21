package the.bytecode.club.bytecodeviewer.util;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author Konloch
 * @since 6/21/2021
 */
public class KeyEventDispatch implements KeyEventDispatcher
{
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		BytecodeViewer.checkHotKey(e);
		return false;
	}
}
