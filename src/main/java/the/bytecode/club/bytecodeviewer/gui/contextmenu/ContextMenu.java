package the.bytecode.club.bytecodeviewer.gui.contextmenu;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import the.bytecode.club.bytecodeviewer.gui.contextmenu.resourcelist.Collapse;
import the.bytecode.club.bytecodeviewer.gui.contextmenu.resourcelist.Expand;
import the.bytecode.club.bytecodeviewer.gui.contextmenu.resourcelist.New;
import the.bytecode.club.bytecodeviewer.gui.contextmenu.resourcelist.Open;
import the.bytecode.club.bytecodeviewer.gui.contextmenu.resourcelist.QuickEdit;
import the.bytecode.club.bytecodeviewer.gui.contextmenu.resourcelist.QuickOpen;
import the.bytecode.club.bytecodeviewer.gui.contextmenu.resourcelist.Delete;
import the.bytecode.club.bytecodeviewer.gui.resourcelist.ResourceTree;
import the.bytecode.club.bytecodeviewer.searching.LDCSearchTreeNodeResult;

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
	private static final ContextMenu SINGLETON = new ContextMenu();
	private final List<ContextMenuItem> contextMenuItems = new ArrayList<>();
	
	static
	{
		//resource list
		addContext(new Delete()); //TODO add support for resources & whole parent nodes (directories)
		addContext(new New());
		addContext(new Open());
		addContext(new QuickOpen());
		addContext(new QuickEdit());
		addContext(new Expand());
		addContext(new Collapse());
		
		//search box
		addContext(new the.bytecode.club.bytecodeviewer.gui.contextmenu.searchbox.Open());
		addContext(new the.bytecode.club.bytecodeviewer.gui.contextmenu.searchbox.QuickOpen());
		addContext(new the.bytecode.club.bytecodeviewer.gui.contextmenu.searchbox.QuickEdit());
	}
	
	public static void addContext(ContextMenuItem menuItem)
	{
		SINGLETON.contextMenuItems.add(menuItem);
	}
	
	public static void buildMenu(ResourceTree tree, TreePath selPath, LDCSearchTreeNodeResult selectedNode, JPopupMenu menu)
	{
		menu.removeAll();
		
		boolean searchBoxPane = selectedNode != null;
		boolean isContainerSelected = !searchBoxPane && selPath.getParentPath() != null && selPath.getParentPath().getParentPath() == null;
		boolean isResourceSelected = false;
		
		//TODO this is hacky - there is probably a better way to do this
		if(!searchBoxPane)
		{
			tree.setSelectionPath(selPath);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			isResourceSelected = !node.children().hasMoreElements();
		}
		
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
					if(isResourceSelected || searchBoxPane)
						continue;
					break;
				case RESOURCE_LIST:
					if(searchBoxPane)
						continue;
					break;
				case SEARCH_BOX_RESULT:
					if(!searchBoxPane)
						continue;
					break;
			}
			
			item.getBuildContextMenuItem().buildMenu(tree, selPath, selectedNode, menu);
		}
	}
}
