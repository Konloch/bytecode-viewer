package the.bytecode.club.bytecodeviewer.gui.contextmenu;

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
 * @since 7/26/2021
 */
public class ContextMenuItem
{
	private final ContextMenuType menuType;
	private final BuildContextMenuItem buildContextMenuItem;
	
	public ContextMenuItem(ContextMenuType menuType, BuildContextMenuItem buildContextMenuItem) {
		this.menuType = menuType;
		this.buildContextMenuItem = buildContextMenuItem;
	}
	
	public ContextMenuType getMenuType()
	{
		return menuType;
	}
	
	public BuildContextMenuItem getBuildContextMenuItem()
	{
		return buildContextMenuItem;
	}
}
