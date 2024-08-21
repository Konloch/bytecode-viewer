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

package the.bytecode.club.bytecodeviewer.gui.resourcelist;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Comparator;
import java.util.HashMap;

/**
 * @author Konloch
 * @since 6/22/2021
 */
public class ResourceTreeNode extends DefaultMutableTreeNode
{
	
	private static final long serialVersionUID = -8817777566176729571L;

	private static final int CHILD_MAP_BUILD_THRESHOLD = 3;
	private HashMap<Object, ResourceTreeNode> userObjectToChildMap = null;
	
	public ResourceTreeNode(Object o)
	{
		super(o);
	}
	
	@Override
	public void insert(MutableTreeNode newChild, int childIndex)
	{
		super.insert(newChild, childIndex);
		addToMap((ResourceTreeNode) newChild);
	}
	
	public void sort()
	{
		recursiveSort(this);
	}
	
	@SuppressWarnings("unchecked")
	private void recursiveSort(ResourceTreeNode node)
	{
		node.children.sort(nodeComparator);
		for (TreeNode nextNode : (Iterable<TreeNode>) node.children)
		{
			if (nextNode.getChildCount() > 0)
			{
				recursiveSort((ResourceTreeNode) nextNode);
			}
		}
	}

	@Override
	public void add(MutableTreeNode newChild) {
		super.add(newChild);
		addToMap((ResourceTreeNode) newChild);
	}

	private void addToMap(ResourceTreeNode newChild) {
		if (userObjectToChildMap != null)
		{
			userObjectToChildMap.put(newChild.getUserObject(), newChild);
		}
		else if (getChildCount() == CHILD_MAP_BUILD_THRESHOLD)
		{
			buildMap();
		}
	}

	private void buildMap() {
		userObjectToChildMap = new HashMap<>();

		for (int i = 0, childCount = getChildCount(); i < childCount; i++)
		{
			ResourceTreeNode item = (ResourceTreeNode) getChildAt(i);
			userObjectToChildMap.put(item.getUserObject(), item);
		}
	}

	@Override
	public void remove(int childIndex) {
		if (userObjectToChildMap != null)
		{
			TreeNode childAt = getChildAt(childIndex);
			userObjectToChildMap.remove(((ResourceTreeNode) childAt).getUserObject());
		}

		super.remove(childIndex);
	}

	@Override
	public void remove(MutableTreeNode aChild) {
		if (userObjectToChildMap != null && aChild != null)
		{
			userObjectToChildMap.remove(((ResourceTreeNode) aChild).getUserObject());
		}

		super.remove(aChild);
	}

	@Override
	public void removeAllChildren() {
		if (userObjectToChildMap != null)
		{
			userObjectToChildMap.clear();
		}

		super.removeAllChildren();
	}

	public ResourceTreeNode getChildByUserObject(Object userObject) {
		if (userObjectToChildMap != null)
		{
			return userObjectToChildMap.get(userObject);
		}

		for (int i = 0, childCount = getChildCount(); i < childCount; i++)
		{
			ResourceTreeNode child = (ResourceTreeNode) getChildAt(i);
			if (child.getUserObject().equals(userObject))
			{
				return child;
			}
		}

		return null;
	}

	protected Comparator<TreeNode> nodeComparator = new Comparator<TreeNode>()
	{
		@Override
		public int compare(TreeNode o1, TreeNode o2)
		{
			// To make sure nodes with children are always on top
			final int firstOffset = o1.getChildCount() > 0 ? -1000 : 0;
			final int secondOffset = o2.getChildCount() > 0 ? 1000 : 0;
			return o1.toString().compareToIgnoreCase(o2.toString())
					+ firstOffset + secondOffset;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			return false;
		}
		
		@Override
		public int hashCode()
		{
			return 7;
		}
	};
	
}
