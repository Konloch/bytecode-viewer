/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Konloch - Konloch.com / BytecodeViewer.com           *
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

package the.bytecode.club.bytecodeviewer.gui.contextmenu.searchbox;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.gui.contextmenu.ContextMenuItem;
import the.bytecode.club.bytecodeviewer.gui.contextmenu.ContextMenuType;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;

/**
 * @author Konloch
 * @since 7/26/2021
 */
public class QuickOpen extends ContextMenuItem
{
	public QuickOpen()
	{
		super(ContextMenuType.SEARCH_BOX_RESULT, ((tree, selPath, result, menu) ->
		{
			JMenu quickOpen = new JMenu(TranslatedStrings.QUICK_OPEN.toString());
			quickOpen.add(createMenu(TranslatedStrings.PROCYON.toString(), ()->BytecodeViewer.viewer.searchBoxPane.quickDecompile(Decompiler.PROCYON_DECOMPILER, result, false)));
			quickOpen.add(createMenu(TranslatedStrings.CFR.toString(), ()->BytecodeViewer.viewer.searchBoxPane.quickDecompile(Decompiler.CFR_DECOMPILER, result, false)));
			quickOpen.add(createMenu(TranslatedStrings.FERNFLOWER.toString(), ()->BytecodeViewer.viewer.searchBoxPane.quickDecompile(Decompiler.FERNFLOWER_DECOMPILER, result, false)));
			quickOpen.add(createMenu(TranslatedStrings.KRAKATAU.toString(), ()->BytecodeViewer.viewer.searchBoxPane.quickDecompile(Decompiler.KRAKATAU_DECOMPILER, result, false)));
			quickOpen.add(createMenu(TranslatedStrings.BYTECODE.toString(), ()->BytecodeViewer.viewer.searchBoxPane.quickDecompile(Decompiler.BYTECODE_DISASSEMBLER, result, false)));
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
