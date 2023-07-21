package the.bytecode.club.uikit.tabpopup;

import javax.swing.JTabbedPane;

public interface ITabPopupEventListener {
	/**
	 * 
	 * @param tabbedPane
	 * @param index, index of tab
	 * @param e
	 */
	public void onTabPopupEvent(JTabbedPane tabbedPane, int index, TabPopupEvent e);
}
