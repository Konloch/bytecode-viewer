package the.bytecode.club.bytecodeviewer.gui.resourcelist;

import java.util.Comparator;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

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
public class ResourceTreeNode extends DefaultMutableTreeNode
{
	
	private static final long serialVersionUID = -8817777566176729571L;
	
	public ResourceTreeNode(final Object o)
	{
		super(o);
	}
	
	@Override
	public void insert(final MutableTreeNode newChild, final int childIndex)
	{
		super.insert(newChild, childIndex);
	}
	
	public void sort()
	{
		recursiveSort(this);
	}
	
	@SuppressWarnings("unchecked")
	private void recursiveSort(final ResourceTreeNode node)
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
	
	protected Comparator<TreeNode> nodeComparator = new Comparator<TreeNode>()
	{
		@Override
		public int compare(final TreeNode o1, final TreeNode o2)
		{
			// To make sure nodes with children are always on top
			final int firstOffset = o1.getChildCount() > 0 ? -1000 : 0;
			final int secondOffset = o2.getChildCount() > 0 ? 1000 : 0;
			return o1.toString().compareToIgnoreCase(o2.toString())
					+ firstOffset + secondOffset;
		}
		
		@Override
		public boolean equals(final Object obj)
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
