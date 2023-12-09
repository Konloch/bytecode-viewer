package the.bytecode.club.uikit.tabpopup;

import java.awt.Component;

import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

/**
 * Show PopupMenu on Tabs
 * @author su
 *
 */
public abstract class AbstractJTabbedPanePopupMenuHandler extends JTabbedPanePopupEventHandler implements ITabPopupEventListener {

	public AbstractJTabbedPanePopupMenuHandler(JTabbedPane tabbedPane) {
		super(tabbedPane);
		registerPopupEventListener(this);
	}

	@Override
	public void onTabPopupEvent(JTabbedPane tabbedPane, int index, TabPopupEvent e) {
		JPopupMenu popupMenu = toBuildTabPopupMenu(tabbedPane, e.getPopupOnTab());
		popupTabMenuWithEvent(popupMenu, e);
	}
	
	public abstract JPopupMenu toBuildTabPopupMenu(JTabbedPane tabbedPane, Component popupOnTab);
	

	public static void popupTabMenuWithEvent(JPopupMenu popupMenu, TabPopupEvent e) {
		popupMenu.show(e.getComponent(), e.getX(), e.getY());
	}
	
}
