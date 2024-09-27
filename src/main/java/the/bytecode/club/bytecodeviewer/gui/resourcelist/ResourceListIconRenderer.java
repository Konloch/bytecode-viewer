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

import org.apache.commons.io.FilenameUtils;
import the.bytecode.club.bytecodeviewer.resources.IconResources;
import the.bytecode.club.bytecodeviewer.resources.ResourceType;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author http://stackoverflow.com/questions/14968005
 * @author Konloch
 */

public class ResourceListIconRenderer extends DefaultTreeCellRenderer
{
    //TODO the icon cache needs to be cleared on treenode removal
    public static Map<ResourceTreeNode, Icon> iconCache = new HashMap<>();

    //called every time there is a pane update
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        Component ret = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        if (value instanceof ResourceTreeNode)
        {
            if (iconCache.containsKey(value))
            {
                setIcon(iconCache.get(value));
                return ret;
            }

            ResourceTreeNode node = (ResourceTreeNode) value;

            String nameOG = node.toString();
            String name = nameOG.toLowerCase();
            String onlyName = FilenameUtils.getName(name);
            boolean iconSet = false;

            //guess file type based on extension
            ResourceType knownResourceType = onlyName.contains(":") ? null : ResourceType.EXTENSION_MAP.get(FilenameUtils.getExtension(onlyName).toLowerCase());

            //set the icon to a known file type
            if (knownResourceType != null
                //check if is parent/root node, or not a directory
                && (node.getParent() == node.getRoot() || node.getChildCount() == 0))
            {
                cacheNodeIcon(node, knownResourceType.getIcon());
                iconSet = true;
            }
            //hardcoded resource icons go here
            else if (nameOG.equals("Decoded Resources") && node.getChildCount() > 0)
            {
                cacheNodeIcon(node, IconResources.decodedIcon);
                iconSet = true;
            }
            else if (node.getChildCount() == 0 && (nameOG.equals("README") || nameOG.equals("LICENSE") || nameOG.equals("NOTICE")))
            {
                cacheNodeIcon(node, IconResources.textIcon);
                iconSet = true;
            }

            //folders
            if (node.getChildCount() > 0)
            {
                List<TreeNode> nodes = new ArrayList<>();
                List<TreeNode> totalNodes = new ArrayList<>();

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

                if (!iconSet)
                {
                    //java packages
                    if (isJava)
                        cacheNodeIcon(node, IconResources.packagesIcon);
                    else //regular folders
                        cacheNodeIcon(node, IconResources.folderIcon);
                }
            }

            //unknown files
            else if (knownResourceType == null && !iconSet)
                cacheNodeIcon(node, IconResources.unknownFileIcon);
        }

        return ret;
    }

    public void cacheNodeIcon(ResourceTreeNode node, Icon icon)
    {
        iconCache.put(node, icon);
        setIcon(icon);
    }
}
