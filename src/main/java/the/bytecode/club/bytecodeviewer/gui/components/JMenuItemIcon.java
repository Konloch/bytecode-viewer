package the.bytecode.club.bytecodeviewer.gui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

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
 * @since 7/4/2021
 */
public class JMenuItemIcon extends JMenuItem
{
	public JMenuItemIcon(Icon icon)
	{
		super("");
		
		setIcon(icon);
		setAlignmentY(0.65f);
		Dimension size = new Dimension((int) (icon.getIconWidth()*1.4), icon.getIconHeight());
		setSize(size);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
	}
	
	@Override
	public void paint(Graphics g)
	{
		g.setColor(UIManager.getColor("Panel.background"));
		g.fillRect(0, 0, getWidth(), getHeight());
		super.paint(g);
	}
}
