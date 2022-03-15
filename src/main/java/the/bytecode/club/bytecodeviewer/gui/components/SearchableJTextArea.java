package the.bytecode.club.bytecodeviewer.gui.components;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.GlobalHotKeys;
import the.bytecode.club.bytecodeviewer.gui.components.listeners.PressKeyListener;
import the.bytecode.club.bytecodeviewer.gui.components.listeners.ReleaseKeyListener;
import the.bytecode.club.bytecodeviewer.resources.IconResources;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponents;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJCheckBox;
import the.bytecode.club.bytecodeviewer.util.JTextAreaUtils;

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
	private final JCheckBox caseSensitiveSearch = new TranslatedJCheckBox("Match case", TranslatedComponents.MATCH_CASE);
	
	public SearchableJTextArea()
	{
		scrollPane.setViewportView(this);
		scrollPane.setColumnHeaderView(searchPanel);
		
		JButton searchNext = new JButton();
		searchNext.setIcon(IconResources.nextIcon);
		
		JButton searchPrev = new JButton();
		searchPrev.setIcon(IconResources.prevIcon);
		
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
			if ((keyEvent.getKeyCode() == KeyEvent.VK_F) && ((keyEvent.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0))
				searchInput.requestFocus();
			
			GlobalHotKeys.keyPressed(keyEvent);
		}));
		
		final Font newFont = getFont().deriveFont((float) BytecodeViewer.viewer.getFontSize());
		
		//set number-bar font
		setFont(newFont);
		
		SwingUtilities.invokeLater(()-> {
			//attach CTRL + Mouse Wheel Zoom
			attachCtrlMouseWheelZoom();
			
			//set text font
			setFont(newFont);
		});
	}
	
	public void search(String search, boolean forwardSearchDirection, boolean caseSensitiveSearch)
	{
		JTextAreaUtils.search(this, search, forwardSearchDirection, caseSensitiveSearch);
	}
	
	public void highlight(String pattern, boolean caseSensitiveSearch)
	{
		JTextAreaUtils.highlight(this, pattern, caseSensitiveSearch);
	}
	
	public void attachCtrlMouseWheelZoom()
	{
		//get the existing scroll event
		MouseWheelListener ogListener = scrollPane.getMouseWheelListeners().length > 0 ?
				scrollPane.getMouseWheelListeners()[0] : null;
		
		//remove the existing event
		if(ogListener != null)
			scrollPane.removeMouseWheelListener(ogListener);
		
		//add a new event
		scrollPane.addMouseWheelListener(e ->
		{
			if (getText().isEmpty())
				return;
			
			if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0)
			{
				Font font = getFont();
				int size = font.getSize();
				
				if (e.getWheelRotation() > 0) //Up
					setFont(new Font(font.getName(), font.getStyle(), --size >= 2 ? --size : 2));
				else //Down
					setFont(new Font(font.getName(), font.getStyle(), ++size));
				
				e.consume();
			}
			else if(ogListener != null)
			{
				ogListener.mouseWheelMoved(e);
			}
		});
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
