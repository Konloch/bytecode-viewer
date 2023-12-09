package the.bytecode.club.uikit.tabpopup;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTabbedPane;

/**
 * Register PopupEvent Handler on TabbedPane
 * @author su
 *
 */
public class JTabbedPanePopupEventHandler {
	protected final JTabbedPane tabbedPane;
	private ITabPopupEventListener tabPopupEventListener;

	public JTabbedPanePopupEventHandler(JTabbedPane tabbedPane) {
		super();
		this.tabbedPane = tabbedPane;
		this.registerMouseEventListener();
	}
	
	private void registerMouseEventListener() {
		this.tabbedPane.addMouseListener(new MouseAdapter() {
            @Override 
            public void mousePressed(MouseEvent e) { 
            	tryTriggerTabPopupEvent(e); 
            }
            
            @Override 
            public void mouseReleased(MouseEvent e) { 
            	tryTriggerTabPopupEvent(e); 
            }
        });
	}
	
	public void registerPopupEventListener(ITabPopupEventListener tabPopupEventListener) {
		this.tabPopupEventListener = tabPopupEventListener;
	}
	
	protected void tryTriggerTabPopupEvent(MouseEvent e) {
        if (e.isPopupTrigger()) {
            int index = tabbedPane.indexAtLocation(e.getX(), e.getY());
            if (index != -1) {
            	Component popupOnTab = tabbedPane.getComponentAt(index);
            	if (this.tabPopupEventListener != null) {
            		this.tabPopupEventListener.onTabPopupEvent(tabbedPane, index, new TabPopupEvent(e, popupOnTab));
            	}
            }
        }
    }

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}
	
}
