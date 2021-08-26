package the.bytecode.club.bytecodeviewer.gui.components;

import java.awt.Dimension;
import javax.swing.JLabel;

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
public class MaxWidthJLabel extends JLabel
{
	private final int width;
	private final int height;
	
	public MaxWidthJLabel(String title, int width, int height)
	{
		super(title);
		this.width = width;
		this.height = height;
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		Dimension realDimension = super.getPreferredSize();
		if (realDimension.getWidth() >= width)
			return new Dimension(width, height);
		else
			return realDimension;
	}
	
	private static final long serialVersionUID = -5511025206527893360L;
}
