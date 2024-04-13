package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import com.github.weisj.darklaf.components.CloseButton;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.gui.components.listeners.MouseClickedListener;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ResourceViewer;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class TabComponent extends JPanel {

	public TabComponent(final JTabbedPane pane) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		if (pane == null) {
			throw new NullPointerException("TabbedPane is null");
		}

		setOpaque(false);
		JLabel label = new JLabel() {
			public String getText() {
				int i = pane.indexOfTabComponent(TabComponent.this);
				if (i != -1) {
					return pane.getTitleAt(i);
				}

				return null;
			}
		};

		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		add(label);
		JButton button = new CloseButton();
		add(button);

		JPopupMenu rightClickMenu = new JPopupMenu();
		JMenuItem closeAllTabs = new JMenuItem(String.valueOf(TranslatedStrings.CLOSE_ALL_BUT_THIS));
		JMenuItem closeTab = new JMenuItem(String.valueOf(TranslatedStrings.CLOSE_TAB));

		rightClickMenu.add(closeAllTabs);
		rightClickMenu.add(closeTab);
		button.setComponentPopupMenu(rightClickMenu);

		button.addMouseListener(new MouseClickedListener(e ->
		{
			if (e.getButton() != MouseEvent.BUTTON1) // left-click
				return;

			if (pane.indexOfTabComponent(TabComponent.this) != -1) {
				int i = pane.indexOfTabComponent(TabComponent.this);
				removeTab(i);
				pane.remove(pane.indexOfTabComponent(TabComponent.this));
			}
		}));

		closeTab.addActionListener(e ->
		{
			if (pane.indexOfTabComponent(TabComponent.this) != -1) {
				int i = pane.indexOfTabComponent(TabComponent.this);
				removeTab(i);
				pane.remove(pane.indexOfTabComponent(TabComponent.this));
			}
		});

		closeAllTabs.addActionListener(e ->
		{

			while (true) {
				if (pane.getTabCount() <= 1)
					return;

				if (pane.indexOfTabComponent(TabComponent.this) != 0) {
					removeTab(0);
					pane.remove(0);
				} else {
					removeTab(1);
					pane.remove(1);
				}
			}
		});

		setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
	}

	private void removeTab(int index) {
		ResourceViewer resourceViewer = (ResourceViewer) BytecodeViewer.viewer.workPane.tabs.getComponentAt(index);
		BytecodeViewer.viewer.workPane.openedTabs.remove(resourceViewer.resource.workingName);
	}

}
