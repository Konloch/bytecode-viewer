package the.bytecode.club.bytecodeviewer.gui.resourcelist.contextmenu;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.gui.resourcelist.ResourceTree;
import the.bytecode.club.bytecodeviewer.gui.resourcelist.contextmenu.impl.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;

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
public class ContextMenu
{
	private static ContextMenu SINGLETON = new ContextMenu();
	private final ArrayList<ContextMenuItem> contextMenuItems = new ArrayList<>();
	
	static
	{
		addContext(new New());
		addContext(new Remove());
		addContext(new Open());
		addContext(new QuickOpen());
		addContext(new Expand());
		addContext(new Collapse());
	}
	
	public static void addContext(ContextMenuItem menuItem)
	{
		SINGLETON.contextMenuItems.add(menuItem);
	}
	
	public static void buildMenu(ResourceTree tree, TreePath selPath, JPopupMenu menu)
	{
		menu.removeAll();
		
		boolean isContainerSelected = selPath.getParentPath() != null && selPath.getParentPath().getParentPath() == null;
		
		//TODO this is hacky - there is probably a better way to do this
		tree.setSelectionPath(selPath);
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		boolean isResourceSelected = !node.children().hasMoreElements();
		
		for(ContextMenuItem item : SINGLETON.contextMenuItems)
		{
			switch(item.getMenuType())
			{
				case CONTAINER:
					if(!isContainerSelected)
						continue;
					break;
				case RESOURCE:
					if(!isResourceSelected || isContainerSelected)
						continue;
					break;
				case DIRECTORY:
					if(isResourceSelected)
						continue;
					break;
			}
			
			item.getBuildContextMenuItem().buildMenu(tree, selPath, menu);
		}
	}
}
