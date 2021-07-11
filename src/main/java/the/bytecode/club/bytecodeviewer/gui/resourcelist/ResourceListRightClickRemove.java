package the.bytecode.club.bytecodeviewer.gui.resourcelist;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Objects;

/**
 * @author Konloch
 * @since 6/22/2021
 */
public class ResourceListRightClickRemove extends AbstractAction
{
	private final ResourceListPane resourceListPane;
	private final int x;
	private final int y;
	private final ResourceTree tree;
	
	public ResourceListRightClickRemove(ResourceListPane resourceListPane, int x, int y, ResourceTree tree)
	{
		super("Remove");
		this.resourceListPane = resourceListPane;
		this.x = x;
		this.y = y;
		this.tree = tree;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		TreePath selPath = resourceListPane.tree.getPathForLocation(x, y);
		DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) Objects.requireNonNull(selPath).getLastPathComponent();
		Enumeration<?> enumeration = resourceListPane.treeRoot.children();
		while (enumeration.hasMoreElements())
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
			if (node.isNodeAncestor(selectNode))
			{
				DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
				root.remove(node);
				
				for (ResourceContainer resourceContainer : BytecodeViewer.files)
				{
					if (resourceContainer.name.equals(selectNode.toString()))
					{
						resourceListPane.removeFile(resourceContainer);
						break;
					}
				}
				
				resourceListPane.updateTree();
				return;
			}
		}
	}
}
