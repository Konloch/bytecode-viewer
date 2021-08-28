package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicButtonUI;

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
 * @author Konloch
 * @since 6/25/2021
 */
public class TabExitButton extends JButton implements ActionListener
{
	private final TabbedPane tabbedPane;
	private final int tabIndex;
	private final String tabWorkingName;
	
	public TabExitButton(TabbedPane tabbedPane, int tabIndex, String tabWorkingName)
	{
		this.tabbedPane = tabbedPane;
		this.tabIndex = tabIndex;
		this.tabWorkingName = tabWorkingName;
		final int size = 17;
		setPreferredSize(new Dimension(size, size));
		setToolTipText("Close this tab");
		// Make the button looks the same for all Laf's
		setUI(new BasicButtonUI());
		// Make it transparent
		setContentAreaFilled(false);
		// No need to be focusable
		setFocusable(false);
		setBorder(BorderFactory.createEtchedBorder());
		setBorderPainted(false);
		// Making nice rollover effect
		// we use the same listener for all buttons
		addMouseListener(TabbedPane.buttonHoverAnimation);
		setRolloverEnabled(true);
		// Close the proper tab by clicking the button
		addActionListener(this);
	}
	
	public int getTabIndex()
	{
		return tabIndex;
	}
	
	@Override
	public void actionPerformed(final ActionEvent e)
	{
		final int i = tabbedPane.tabs.indexOfTabComponent(tabbedPane);
		if (i != -1)
		{
			tabbedPane.tabs.remove(i);
		}
	}
	
	// we don't want to update UI for this button
	@Override
	public void updateUI() { }
	
	// paint the cross
	@Override
	protected void paintComponent(final Graphics g)
	{
		super.paintComponent(g);
		final Graphics2D g2 = (Graphics2D) g.create();
		// shift the image for pressed buttons
		if (getModel().isPressed())
			g2.translate(1, 1);
		
		g2.setStroke(new BasicStroke(2));
		g2.setColor(Color.BLACK);
		
		if (getModel().isRollover())
			g2.setColor(Color.MAGENTA);
		
		final int delta = 6;
		g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
		g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
		g2.dispose();
	}
	
	public TabbedPane getTabbedPane()
	{
		return tabbedPane;
	}
	
	public String getTabWorkingName()
	{
		return tabWorkingName;
	}
	
	public static long getSerialVersionUID()
	{
		return serialVersionUID;
	}
	
	private static final long serialVersionUID = -4492967978286454159L;
}
