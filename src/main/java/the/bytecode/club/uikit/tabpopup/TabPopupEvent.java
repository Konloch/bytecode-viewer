package the.bytecode.club.uikit.tabpopup;

import java.awt.Component;
import java.awt.event.MouseEvent;

public class TabPopupEvent extends MouseEvent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2510164400674753411L;
	
	private final Component popupOnTab;

	public TabPopupEvent(MouseEvent e, Component popupOnTab) {
		super(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), 
				e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger(), e.getButton());
		
		this.popupOnTab = popupOnTab;
	}

	public Component getPopupOnTab() {
		return popupOnTab;
	}
	
	
}
