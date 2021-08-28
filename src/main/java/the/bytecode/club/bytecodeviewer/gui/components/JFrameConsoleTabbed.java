package the.bytecode.club.bytecodeviewer.gui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import the.bytecode.club.bytecodeviewer.resources.IconResources;

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
 * @since 7/14/2021
 */

public class JFrameConsoleTabbed extends JFrame
{
	private final JTabbedPane tabbedPane;
	
	public JFrameConsoleTabbed(String title)
	{
		setIconImages(IconResources.iconList);
		setTitle(title);
		setSize(new Dimension(542, 316));
		
		tabbedPane = new JTabbedPane();
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		this.setLocationRelativeTo(null);
	}
	
	public void addConsole(Component console, String containerName)
	{
		tabbedPane.add(console, containerName);
	}
	
	public JTabbedPane getTabbedPane()
	{
		return tabbedPane;
	}
}
