/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Konloch - Konloch.com / BytecodeViewer.com           *
 *                                                                         *
 * This program is free software: you can redistribute it and/or modify    *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import com.github.weisj.darklaf.components.CloseButton;
import the.bytecode.club.bytecodeviewer.gui.components.listeners.MouseClickedListener;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class CloseButtonComponent extends JPanel {

	private final JTabbedPane pane;

	public CloseButtonComponent(JTabbedPane pane) {
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
			if (e.getButton() != MouseEvent.BUTTON1) // left-click
				return;

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
