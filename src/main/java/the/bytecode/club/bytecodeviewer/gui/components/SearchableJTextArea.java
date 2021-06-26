package the.bytecode.club.bytecodeviewer.gui.components;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Resources;
import the.bytecode.club.bytecodeviewer.gui.components.listeners.PressKeyListener;
import the.bytecode.club.bytecodeviewer.gui.components.listeners.ReleaseKeyListener;
import the.bytecode.club.bytecodeviewer.util.JTextAreaUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
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

/**
 * Searching on a JTextArea using swing highlighting
 *
 * @author Konloch
 * @since 6/25/2021
 */
public class SearchableJTextArea extends JTextArea
{
	private final JScrollPane scrollPane = new JScrollPane();
	private final JPanel searchPanel = new JPanel(new BorderLayout());
	private final JTextField searchInput = new JTextField();
	private final JCheckBox caseSensitiveSearch = new JCheckBox("Exact");
	
	public SearchableJTextArea()
	{
		scrollPane.setViewportView(this);
		scrollPane.setColumnHeaderView(searchPanel);
		
		JButton searchNext = new JButton();
		searchNext.setIcon(Resources.nextIcon);
		
		JButton searchPrev = new JButton();
		searchPrev.setIcon(Resources.prevIcon);
		
		JPanel buttonPane = new JPanel(new BorderLayout());
		buttonPane.add(searchNext, BorderLayout.WEST);
		buttonPane.add(searchPrev, BorderLayout.EAST);
		
		searchPanel.add(buttonPane, BorderLayout.WEST);
		searchPanel.add(searchInput, BorderLayout.CENTER);
		searchPanel.add(caseSensitiveSearch, BorderLayout.EAST);
		
		searchNext.addActionListener(arg0 -> search(searchInput.getText(), true, caseSensitiveSearch.isSelected()));
		searchPrev.addActionListener(arg0 -> search(searchInput.getText(), false, caseSensitiveSearch.isSelected()));
		
		searchInput.addKeyListener(new ReleaseKeyListener(keyEvent ->
		{
			if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
				search(searchInput.getText(), true, caseSensitiveSearch.isSelected());
		}));
		
		addKeyListener(new PressKeyListener(keyEvent ->
		{
			if ((keyEvent.getKeyCode() == KeyEvent.VK_F) && ((keyEvent.getModifiers() & KeyEvent.CTRL_MASK) != 0))
				searchInput.requestFocus();
			
			BytecodeViewer.checkHotKey(keyEvent);
		}));
	}
	
	public void search(String search, boolean forwardSearchDirection, boolean caseSensitiveSearch)
	{
		JTextAreaUtils.search(this, search, forwardSearchDirection, caseSensitiveSearch);
	}
	
	public void highlight(String pattern, boolean caseSensitiveSearch)
	{
		JTextAreaUtils.highlight(this, pattern, caseSensitiveSearch);
	}
	
	public JScrollPane getScrollPane()
	{
		return scrollPane;
	}
	
	public JPanel getSearchPanel()
	{
		return searchPanel;
	}
	
	public JTextField getSearchInput()
	{
		return searchInput;
	}
	
	public JCheckBox getCaseSensitiveSearch()
	{
		return caseSensitiveSearch;
	}
}
