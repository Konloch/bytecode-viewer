package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import com.github.weisj.darklaf.components.CloseButton;
import the.bytecode.club.bytecodeviewer.gui.components.listeners.MouseClickedListener;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;

import javax.swing.*;
import java.awt.*;

public class CloseButtonComponent extends JPanel {

	private final JTabbedPane pane;

	public CloseButtonComponent(final JTabbedPane pane) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		if (pane == null) {
			throw new NullPointerException("TabbedPane is null");
		}

		this.pane = pane;
		setOpaque(false);
		JLabel label = new JLabel() {
			public String getText() {
				int i = pane.indexOfTabComponent(CloseButtonComponent.this);
				if (i != -1) {
					return pane.getTitleAt(i);
				}

				return null;
			}
		};

		add(label);
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
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
			if (pane.indexOfTabComponent(CloseButtonComponent.this) != -1)
				pane.remove(pane.indexOfTabComponent(CloseButtonComponent.this));
		}));

		closeTab.addActionListener(e ->
		{
			if (pane.indexOfTabComponent(CloseButtonComponent.this) != -1)
				pane.remove(pane.indexOfTabComponent(CloseButtonComponent.this));
		});
		closeAllTabs.addActionListener(e ->
		{

			while (true) {
				if (pane.getTabCount() <= 1)
					return;

				if (pane.indexOfTabComponent(CloseButtonComponent.this) != 0)
					pane.remove(0);
				else
					pane.remove(1);
			}
		});

		setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
	}

}
