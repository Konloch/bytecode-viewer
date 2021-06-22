package the.bytecode.club.bytecodeviewer.gui.resourcelist;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.Comparator;
import javax.swing.tree.TreeNode;

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
