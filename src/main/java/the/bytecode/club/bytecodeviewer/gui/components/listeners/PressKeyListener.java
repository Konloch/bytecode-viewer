package the.bytecode.club.bytecodeviewer.gui.components.listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author Konloch
 * @since 6/25/2021
 */
public class PressKeyListener implements KeyListener
{
	private final KeyPressedEvent keyPressedEvent;
	
	public PressKeyListener(KeyPressedEvent keyPressedEvent) {this.keyPressedEvent = keyPressedEvent;}
	
	@Override
	public void keyTyped(KeyEvent e) { }
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		keyPressedEvent.keyReleased(e);
	}
	
	@Override
	public void keyReleased(KeyEvent e) {}
	
	public interface KeyPressedEvent
	{
		void keyReleased(KeyEvent e);
	}
}
