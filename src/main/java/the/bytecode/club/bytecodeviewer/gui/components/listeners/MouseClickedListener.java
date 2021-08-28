package the.bytecode.club.bytecodeviewer.gui.components.listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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
public class MouseClickedListener implements MouseListener
{
	private final MouseClickedEvent mouseClickedEvent;
	
	public MouseClickedListener(MouseClickedEvent mouseClickedEvent) {this.mouseClickedEvent = mouseClickedEvent;}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		mouseClickedEvent.mouseClicked(e);
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) { }
	
	@Override
	public void mouseExited(MouseEvent arg0) { }
	
	@Override
	public void mousePressed(MouseEvent arg0) { }
	
	@Override
	public void mouseReleased(MouseEvent e) { }
	
	public interface MouseClickedEvent
	{
		void mouseClicked(MouseEvent e);
	}
}
