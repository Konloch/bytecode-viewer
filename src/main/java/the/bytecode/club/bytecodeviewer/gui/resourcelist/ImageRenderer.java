package the.bytecode.club.bytecodeviewer.gui.resourcelist;

import the.bytecode.club.bytecodeviewer.Resources;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author http://stackoverflow.com/questions/14968005
 * @author Konloch
 */
public class ImageRenderer extends DefaultTreeCellRenderer
{
	//called every time there is a pane update, I.E. whenever you expand a folder
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
			String name = node.toString().toLowerCase();
			
			if (name.endsWith(".jar") || name.endsWith(".war"))
			{
				setIcon(Resources.jarIcon);
			}
			else if (name.endsWith(".zip"))
			{
				setIcon(Resources.zipIcon);
			}
			else if (name.endsWith(".bat"))
			{
				setIcon(Resources.batIcon);
			}
			else if (name.endsWith(".sh"))
			{
				setIcon(Resources.shIcon);
			}
			else if (name.endsWith(".cs"))
			{
				setIcon(Resources.csharpIcon);
			}
			else if (name.endsWith(".c") || name.endsWith(".cpp") || name.endsWith(".h"))
			{
				setIcon(Resources.cplusplusIcon);
			}
			else if (name.endsWith(".apk") || name.endsWith(".dex"))
			{
				setIcon(Resources.androidIcon);
			}
			else if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")
					|| name.endsWith(".bmp") || name.endsWith(".gif"))
			{
				setIcon(Resources.imageIcon);
			}
			else if (name.endsWith(".class"))
			{
				setIcon(Resources.classIcon);
			}
			else if (name.endsWith(".java"))
			{
				setIcon(Resources.javaIcon);
			}
			else if (name.endsWith(".txt") || name.endsWith(".md"))
			{
				setIcon(Resources.textIcon);
			}
			else if (name.equals("decoded resources"))
			{
				setIcon(Resources.decodedIcon);
			}
			else if (name.endsWith(".properties") || name.endsWith(".xml") || name.endsWith(".jsp")
					|| name.endsWith(".mf") || name.endsWith(".config") || name.endsWith(".cfg"))
			{
				setIcon(Resources.configIcon);
			}
			else if (node.getChildCount() <= 0)
			{ //random file
				setIcon(Resources.fileIcon);
			}
			else
			{ //folder
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
				
				if (isJava)
					setIcon(Resources.packagesIcon);
				else
				{
					setIcon(Resources.folderIcon);
				}
			}
		}
		
		return ret;
	}
}
