package the.bytecode.club.bytecodeviewer.gui.resourcelist;

import org.apache.commons.io.FilenameUtils;
import the.bytecode.club.bytecodeviewer.resources.IconResources;
import the.bytecode.club.bytecodeviewer.resources.ResourceType;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author http://stackoverflow.com/questions/14968005
 * @author Konloch
 */

public class ResourceListIconRenderer extends DefaultTreeCellRenderer
{
	//called every time there is a pane update
	@Override
	public Component getTreeCellRendererComponent(
			JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus)
	{
		Component ret = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		
		if (value instanceof ResourceTreeNode)
		{
			ResourceTreeNode node = (ResourceTreeNode) value;
			String nameOG = node.toString();
			String name = nameOG.toLowerCase();
			String onlyName = FilenameUtils.getName(name);
			boolean iconSet = false;
			
			//guess file type based on extension
			ResourceType knownResourceType = onlyName.contains(":") ? null
					: ResourceType.extensionMap.get(FilenameUtils.getExtension(onlyName).toLowerCase());
			
			//set the icon to a known file type
			if (knownResourceType != null
					//check if is parent/root node, or not a directory
					&& (node.getParent() == node.getRoot()
					|| node.getChildCount() == 0))
			{
				setIcon(knownResourceType.getIcon());
				iconSet = true;
			}
			//hardcoded resource icons go here
			else if (nameOG.equals("Decoded Resources") && node.getChildCount() > 0)
			{
				setIcon(IconResources.decodedIcon);
				iconSet = true;
			}
			else if (node.getChildCount() == 0
					&& (nameOG.equals("README")
					|| nameOG.equals("LICENSE")
					|| nameOG.equals("NOTICE")))
			{
				setIcon(IconResources.textIcon);
				iconSet = true;
			}
			
			//folders
			if (node.getChildCount() > 0)
			{
				ArrayList<TreeNode> nodes = new ArrayList<>();
				ArrayList<TreeNode> totalNodes = new ArrayList<>();
				
				nodes.add(node);
				totalNodes.add(node);
				
				boolean isJava = false;
				boolean finished = false;
				
				while (!finished)
				{ //may cause a clusterfuck with huge files
					if (nodes.isEmpty())
						finished = true;
					else
					{
						TreeNode treeNode = nodes.get(0);
						nodes.remove(treeNode);
						int children = treeNode.getChildCount();
						if (children >= 1)
							for (int i = 0; i < children; i++)
							{
								TreeNode child = treeNode.getChildAt(i);
								
								if (!totalNodes.contains(child))
								{
									nodes.add(child);
									totalNodes.add(child);
								}
								
								if (child.toString().endsWith(".class"))
									isJava = true;
							}
						
						if (isJava)
							nodes.clear();
					}
				}
				
				if(!iconSet)
				{
					//java packages
					if (isJava)
						setIcon(IconResources.packagesIcon);
					else //regular folders
						setIcon(IconResources.folderIcon);
				}
			}
			
			//unknown files
			else if (knownResourceType == null && !iconSet)
				setIcon(IconResources.unknownFileIcon);
		}
		
		return ret;
	}
}
