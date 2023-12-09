package the.bytecode.club.bytecodeviewer.gui.resourcelist;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

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
 * @since 6/22/2021
 */
public class SearchKeyAdapter extends KeyAdapter
{
	private final ResourceListPane resourceListPane;
	private int iteratePast;
	private String pastQT;
	
	public SearchKeyAdapter(ResourceListPane resourceListPane)
	{
		this.resourceListPane = resourceListPane;
	}
	
	@Override
	public void keyPressed(final KeyEvent ke)
	{
		//only trigger on enter
		if (ke.getKeyCode() != KeyEvent.VK_ENTER)
			return;
		
		String qt = resourceListPane.quickSearch.getText();
		
		if (qt.trim().isEmpty()) //NOPE
			return;
		
		if (pastQT == null || !pastQT.equals(qt))
		{
			iteratePast = 0;
			pastQT = qt;
		}
		
		String[] path;
		
		path = qt.split("[\\./]+"); // split at dot or slash
		qt = qt.replace('/', '.');
		
		ResourceTreeNode curNode = resourceListPane.treeRoot;
		boolean caseSensitive = resourceListPane.caseSensitive.isSelected();
		
		boolean success = false;
		if (resourceListPane.exact.isSelected())
		{
			pathLoop:
			for (int i = 0; i < path.length; i++)
			{
				final String pathName = path[i];
				final boolean isLast = i == path.length - 1;
				
				for (int c = 0; c < curNode.getChildCount(); c++)
				{
					final ResourceTreeNode child = (ResourceTreeNode) curNode.getChildAt(c);
					Object userObject = child.getUserObject();
					if (caseSensitive ? userObject.toString().equals(pathName) : userObject.toString().equalsIgnoreCase(pathName))
					{
						curNode = child;
						if (isLast)
						{
							final TreePath pathn = new TreePath(curNode.getPath());
							resourceListPane.tree.setSelectionPath(pathn);
							resourceListPane.tree.makeVisible(pathn);
							resourceListPane.tree.scrollPathToVisible(pathn);
							resourceListPane.openPath(pathn); //auto open
							success = true;
							break pathLoop;
						}
						continue pathLoop;
					}
				}
				System.out.println("Could not find " + pathName);
				break;
			}
		}
		else
		{
			int iteratations = 0;
			TreePath loopFallBack = null;
			TreePath pathOpen = null;
			
			@SuppressWarnings("unchecked") Enumeration<TreeNode> enums = curNode.depthFirstEnumeration();
			while (enums != null && enums.hasMoreElements())
			{
				ResourceTreeNode node = (ResourceTreeNode) enums.nextElement();
				if (node.isLeaf())
				{
					String userObject = (String) (node.getUserObject());
					String lastElem = path[path.length - 1];
					
					if (caseSensitive ? userObject.contains(lastElem) : userObject.toLowerCase().contains(lastElem.toLowerCase()))
					{
						TreeNode[] pathArray = node.getPath();
						int k = 0;
						StringBuilder fullPath = new StringBuilder();
						while (pathArray != null && k < pathArray.length)
						{
							ResourceTreeNode n = (ResourceTreeNode) pathArray[k];
							String s = (String) (n.getUserObject());
							fullPath.append(s);
							if (k++ != pathArray.length - 1)
								fullPath.append(".");
						}
						
						String fullPathString = fullPath.toString();
						
						if (caseSensitive ? fullPathString.contains(qt) : fullPathString.toLowerCase().contains(qt.toLowerCase()))
						{
							if (loopFallBack == null)
								loopFallBack = new TreePath(node.getPath());
							
							if (iteratations++ < iteratePast)
								continue;
							
							pathOpen = new TreePath(node.getPath());
							break;
						}
					}
				}
			}
			
			if (pathOpen == null && loopFallBack != null)
			{
				iteratePast = 0;
				pathOpen = loopFallBack;
			}
			
			if (pathOpen != null)
			{
				resourceListPane.tree.setSelectionPath(pathOpen.getParentPath());
				resourceListPane.tree.setSelectionPath(pathOpen);
				resourceListPane.tree.makeVisible(pathOpen);
				resourceListPane.tree.scrollPathToVisible(pathOpen);
				
				if(resourceListPane.autoOpen.isSelected())
				{
					resourceListPane.openPath(pathOpen);
					resourceListPane.quickSearch.requestFocusInWindow();
				}
				
				iteratePast++;
				success = true;
			}
		}
		
		if (!success)
			Toolkit.getDefaultToolkit().beep();
	}
}
