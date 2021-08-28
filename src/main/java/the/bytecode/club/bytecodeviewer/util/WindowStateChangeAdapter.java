package the.bytecode.club.bytecodeviewer.util;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import the.bytecode.club.bytecodeviewer.gui.MainViewerGUI;

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
 * @since 6/21/2021
 */
public class WindowStateChangeAdapter extends WindowAdapter
{
	private final MainViewerGUI mainViewerGUI;
	
	public WindowStateChangeAdapter(MainViewerGUI mainViewerGUI) {this.mainViewerGUI = mainViewerGUI;}
	
	@Override
	public void windowStateChanged(WindowEvent evt)
	{
		int oldState = evt.getOldState();
		int newState = evt.getNewState();

        /*if ((oldState & Frame.ICONIFIED) == 0 && (newState & Frame.ICONIFIED) != 0) {
            System.out.println("Frame was iconized");
        } else if ((oldState & Frame.ICONIFIED) != 0 && (newState & Frame.ICONIFIED) == 0) {
            System.out.println("Frame was deiconized");
        }*/
		
		if ((oldState & Frame.MAXIMIZED_BOTH) == 0 && (newState & Frame.MAXIMIZED_BOTH) != 0)
		{
			mainViewerGUI.isMaximized = true;
		}
		else if ((oldState & Frame.MAXIMIZED_BOTH) != 0 && (newState & Frame.MAXIMIZED_BOTH) == 0)
		{
			mainViewerGUI.isMaximized = false;
		}
	}
}
