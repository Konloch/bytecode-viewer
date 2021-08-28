package the.bytecode.club.bytecodeviewer.util;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import the.bytecode.club.bytecodeviewer.GlobalHotKeys;
import the.bytecode.club.bytecodeviewer.gui.components.SearchableRSyntaxTextArea;

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
public class KeyEventDispatch implements KeyEventDispatcher
{
	@Override
	public boolean dispatchKeyEvent(KeyEvent e)
	{
		//hardcoded check for searchable syntax panes, this allows specific panels to ctrl + s save externally
		if(e.getSource() instanceof SearchableRSyntaxTextArea)
		{
			SearchableRSyntaxTextArea rSyntaxTextArea = (SearchableRSyntaxTextArea) e.getSource();
			if(rSyntaxTextArea.getOnCtrlS() != null)
				return false;
		}
		
		GlobalHotKeys.keyPressed(e);
		return false;
	}
}
