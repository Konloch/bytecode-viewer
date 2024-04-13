package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import com.github.weisj.darklaf.ui.tabbedpane.DarkTabbedPaneUI;

import javax.swing.*;

public class DraggableTabbedPane extends JTabbedPane {

	private static final long serialVersionUID = 1L;

	public DraggableTabbedPane() {
		super(SwingConstants.TOP, SCROLL_TAB_LAYOUT);
		this.putClientProperty(DarkTabbedPaneUI.KEY_DND, true);
	}
}
