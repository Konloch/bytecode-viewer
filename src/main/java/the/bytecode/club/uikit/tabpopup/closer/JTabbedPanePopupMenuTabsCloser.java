package the.bytecode.club.uikit.tabpopup.closer;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

import the.bytecode.club.uikit.tabpopup.AbstractJTabbedPanePopupMenuHandler;

/**
 * PopupMenu & Items implementation of Close Tabs
 * @author su
 *
 */
public class JTabbedPanePopupMenuTabsCloser extends AbstractJTabbedPanePopupMenuHandler {
	protected JTabbedPaneCloser tabbedPaneCloser;
	private PopupMenuTabsCloseConfiguration closeConfiguration;

	public JTabbedPanePopupMenuTabsCloser(JTabbedPane tabbedPane) {
		super(tabbedPane);
		this.tabbedPaneCloser = new JTabbedPaneCloser(tabbedPane);
	}
	
	public void configureCloseItems(PopupMenuTabsCloseConfiguration configuration) {
		this.closeConfiguration = configuration;
	}

	public PopupMenuTabsCloseConfiguration getCloseConfiguration() {
		return closeConfiguration;
	}

	@Override
	public JPopupMenu toBuildTabPopupMenu(JTabbedPane tabbedPane, Component popupOnTab) {
		JPopupMenu popUpMenu = new JPopupMenu();
		if (closeConfiguration.isClose()) {
			addItemCloseTab(popUpMenu, popupOnTab);
		}
		if (closeConfiguration.isCloseOthers()) {
			addItemCloseOtherTabs(popUpMenu, popupOnTab);
		}
		if (closeConfiguration.isCloseAll()) {
			addItemCloseAllTabs(popUpMenu);
		}
		if (closeConfiguration.isCloseLefts()) {
			addItemCloseLeftTabs(popUpMenu, popupOnTab);
		}
		if (closeConfiguration.isCloseRights()) {
			addItemCloseRightTabs(popUpMenu, popupOnTab);
		}
		return popUpMenu;
	}

	protected void addItemCloseTab(JPopupMenu popUpMenu, Component popupOnTab) {
		addMenuItem(popUpMenu, "Close", e -> { tabbedPaneCloser.removeComponent(popupOnTab); });
	}
	
	protected void addItemCloseOtherTabs(JPopupMenu popUpMenu, Component popupOnTab) {
		addMenuItem(popUpMenu, "Close Others", e -> { tabbedPaneCloser.removeOtherComponents(popupOnTab); });
	}
	
	protected void addItemCloseAllTabs(JPopupMenu popUpMenu) {
		addMenuItem(popUpMenu, "Close All", e -> { tabbedPaneCloser.removeAllComponents(); });
	}
	
	protected void addItemCloseLeftTabs(JPopupMenu popUpMenu, Component popupOnTab) {
		addMenuItem(popUpMenu, "Close Lefts", e -> { tabbedPaneCloser.removeLeftComponents(popupOnTab); });
	}
	
	protected void addItemCloseRightTabs(JPopupMenu popUpMenu, Component popupOnTab) {
		addMenuItem(popUpMenu, "Close Rights", e -> { tabbedPaneCloser.removeRightComponents(popupOnTab); });
	}
	
	protected void addMenuItem(JPopupMenu popUpMenu, String item, ActionListener listener) {
		JMenuItem menuItem = new JMenuItem(item);
		popUpMenu.add(menuItem);
		menuItem.addActionListener(listener);
	}
}
