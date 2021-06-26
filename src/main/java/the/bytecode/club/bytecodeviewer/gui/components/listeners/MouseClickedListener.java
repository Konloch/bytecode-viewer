package the.bytecode.club.bytecodeviewer.gui.components.listeners;

import java.awt.event.*;

/**
 * @author Konloch
 * @since 6/25/2021
 */
public class MouseClickedListener implements MouseListener
{
	private final MouseClickedEvent mouseClickedEvent;
	
	public MouseClickedListener(MouseClickedEvent mouseClickedEvent) {this.mouseClickedEvent = mouseClickedEvent;}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		mouseClickedEvent.mouseClicked(e);
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) { }
	
	@Override
	public void mouseExited(MouseEvent arg0) { }
	
	@Override
	public void mousePressed(MouseEvent arg0) { }
	
	@Override
	public void mouseReleased(MouseEvent e) { }
	
	public interface MouseClickedEvent
	{
		void mouseClicked(MouseEvent e);
	}
}
