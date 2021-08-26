package the.bytecode.club.bytecodeviewer.gui.contextmenu.searchbox;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.gui.contextmenu.ContextMenuItem;
import the.bytecode.club.bytecodeviewer.gui.contextmenu.ContextMenuType;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;

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
 * @since 7/27/2021
 */
public class QuickEdit extends ContextMenuItem
{
	public QuickEdit()
	{
		super(ContextMenuType.SEARCH_BOX_RESULT, ((tree, selPath, result, menu) ->
		{
			JMenu quickOpen = new JMenu("Quick Edit");
			quickOpen.add(createMenu(TranslatedStrings.KRAKATAU.toString(), ()->
					BytecodeViewer.viewer.searchBoxPane.quickDecompile(Decompiler.KRAKATAU_DISASSEMBLER, result, true)));
			menu.add(quickOpen);
		}));
	}
	
	private static JMenuItem createMenu(String name, Runnable onClick)
	{
		JMenuItem menu = new JMenuItem(name);
		menu.addActionListener((e)->onClick.run());
		return menu;
	}
}
