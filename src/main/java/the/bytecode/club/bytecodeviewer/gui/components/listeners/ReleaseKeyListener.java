package the.bytecode.club.bytecodeviewer.gui.components.listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author Konloch
 * @since 6/25/2021
 */
public class ReleaseKeyListener implements KeyListener
{
	private final KeyReleasedEvent keyReleasedEvent;
	
	public ReleaseKeyListener(KeyReleasedEvent keyReleasedEvent) {this.keyReleasedEvent = keyReleasedEvent;}
	
	@Override
	public void keyTyped(KeyEvent e) { }
	
	@Override
	public void keyPressed(KeyEvent e) { }
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		keyReleasedEvent.keyReleased(e);
	}
	
	public interface KeyReleasedEvent
	{
		void keyReleased(KeyEvent e);
	}
}
