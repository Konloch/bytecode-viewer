package the.bytecode.club.bytecodeviewer.gui.resourcelist;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;

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
	
	public SearchKeyAdapter(ResourceListPane resourceListPane) {this.resourceListPane = resourceListPane;}
	
	@Override
	public void keyPressed(final KeyEvent ke)
	{
		//only trigger on enter
		if (ke.getKeyCode() != KeyEvent.VK_ENTER)
			return;
		
		final String qt = resourceListPane.quickSearch.getText();
		resourceListPane.quickSearch.setText("");
		
		if (qt.isEmpty()) //NOPE
			return;
		
		String[] path;
		int found = 0;
		
		if (qt.contains("."))
		{
			path = qt.split("\\.");
		}
		else
		{
			path = new String[]{qt};
		}
		
		ResourceTreeNode curNode = resourceListPane.treeRoot;
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
					System.out.println(pathName + ":" + child.getUserObject());
					
					if (child.getUserObject().equals(pathName))
					{
						curNode = child;
						if (isLast)
						{
							System.out.println("Found! " + curNode);
							found++;
							final TreePath pathn = new TreePath(curNode.getPath());
							resourceListPane.tree.setSelectionPath(pathn);
							resourceListPane.tree.makeVisible(pathn);
							resourceListPane.tree.scrollPathToVisible(pathn);
							resourceListPane.openPath(pathn); //auto open
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
			@SuppressWarnings("unchecked")
			Enumeration<TreeNode> enums = curNode.depthFirstEnumeration();
			while (enums != null && enums.hasMoreElements())
			{
				ResourceTreeNode node = (ResourceTreeNode) enums.nextElement();
				if (node.isLeaf())
				{
					if (((String) (node.getUserObject())).toLowerCase().contains(path[path.length - 1].toLowerCase()))
					{
						TreeNode[] pathArray = node.getPath();
						int k = 0;
						StringBuilder fullPath = new StringBuilder();
						while (pathArray != null
								&& k < pathArray.length)
						{
							ResourceTreeNode n = (ResourceTreeNode) pathArray[k];
							String s = (String) (n.getUserObject());
							fullPath.append(s);
							if (k++ != pathArray.length - 1)
							{
								fullPath.append(".");
							}
						}
						String fullPathString = fullPath.toString();
						if (fullPathString.toLowerCase().contains(qt.toLowerCase()))
						{
							System.out.println("Found! " + node);
							found++;
							if (found >= 30)
							{ //TODO probably make this a setting, no real reason it's 30
								BytecodeViewer.showMessage("Uh oh, there could be more results but you've"
										+ " triggered the 30 classes at once limit. Try refining your search.");
								return;
							}
							final TreePath pathn = new TreePath(node.getPath());
							resourceListPane.tree.setSelectionPath(pathn.getParentPath());
							resourceListPane.tree.setSelectionPath(pathn);
							resourceListPane.tree.makeVisible(pathn);
							resourceListPane.tree.scrollPathToVisible(pathn);
						}
					}
				}
			}
		}
	}
}
