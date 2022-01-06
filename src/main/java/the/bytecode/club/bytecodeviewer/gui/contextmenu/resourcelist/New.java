package the.bytecode.club.bytecodeviewer.gui.contextmenu.resourcelist;

import java.util.Enumeration;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.apache.commons.io.FilenameUtils;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.api.ASMUtil;
import the.bytecode.club.bytecodeviewer.gui.contextmenu.ContextMenuItem;
import the.bytecode.club.bytecodeviewer.gui.contextmenu.ContextMenuType;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;

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
 * @since 7/27/2021
 */
public class New extends ContextMenuItem
{
	public New()
	{
		super(ContextMenuType.RESOURCE_LIST, ((tree, selPath, result, menu) ->
		{
			JMenu quickOpen = new JMenu(TranslatedStrings.NEW.toString());
			quickOpen.add(createMenu("Class", FileType.CLASS, selPath));
			
			//TODO
			// + directory isn't finished
			// + file has no purpose until the plugin writer code is added for newly created resources
			//   ^ this will allow users to edit the files they have created
			if(Constants.DEV_MODE)
			{
				quickOpen.add(createMenu("File", FileType.FILE, selPath));
				quickOpen.add(createMenu("Directory", FileType.DIRECTORY, selPath));
			}
			menu.add(quickOpen);
		}));
	}
	
	private static JMenuItem createMenu(String name, FileType fileType, TreePath selPath)
	{
		JMenuItem menu = new JMenuItem(name);
		
		String separator = fileType == FileType.CLASS ? "." : "/";
		String firstPath = buildPath(0, 2, selPath, "/");
		String path = buildPath(2, selPath.getPathCount(), selPath, separator);
		String containerName = selPath.getPathComponent(1).toString();
		
		menu.addActionListener((e)->{
			String newPath = BytecodeViewer.showInput("Name",
					fileType == FileType.CLASS ? "Enter the class name" : "Enter the file name",
					FilenameUtils.removeExtension(path));
			
			if(newPath == null || newPath.isEmpty())
				return;
			
			byte[] contents = new byte[0];

			switch(fileType)
			{
				case CLASS:
					ClassNode cn = new ClassNode();
					
					//TODO this should be a dialog
					cn.version = 52;
					
					//TODO santize newPath and remove extension if added
					cn.name = newPath;
					String oldResourcePath = newPath.replace(".", "/");
					String newResourcePath = oldResourcePath + ".class";
					
					contents = ASMUtil.nodeToBytes(cn);
					
					BytecodeViewer.resourceContainers.get(containerName).resourceClasses.put(oldResourcePath, cn);
					BytecodeViewer.resourceContainers.get(containerName).resourceClassBytes.put(newResourcePath, contents);
					searchAndInsert(firstPath + "/" + newResourcePath, BytecodeViewer.resourceContainers.get(containerName).treeNode, "/");
					
					break;
				case FILE:
					BytecodeViewer.resourceContainers.get(containerName).resourceFiles.put(newPath, contents);
					searchAndInsert(firstPath + separator +newPath, BytecodeViewer.resourceContainers.get(containerName).treeNode, separator);
					break;
			}
			
			BytecodeViewer.viewer.resourcePane.tree.updateUI();
		});
		
		return menu;
	}
	
	public static String buildPath(int startsAt, int max, TreePath selPath, String separator)
	{
		StringBuilder tempSpot = new StringBuilder();
		
		for(int counter = startsAt; counter < max; counter++)
		{
			if(counter > startsAt)
				tempSpot.append(separator);
			tempSpot.append(selPath.getPathComponent(counter));
		}
		
		return tempSpot.toString();
	}
	
	public static String buildPath(int startsAt, int max, DefaultMutableTreeNode treeNode, String separator)
	{
		StringBuilder tempSpot = new StringBuilder();
		
		for(int counter = startsAt; counter < max; counter++)
		{
			if(counter > startsAt)
				tempSpot.append(separator);
			tempSpot.append(treeNode.getPath()[counter]);
		}
		
		return tempSpot.toString();
	}
	
	//TODO this needs to be rewritten to support creating parent nodes that don't exist
	@SuppressWarnings("unchecked")
	public static boolean searchAndInsert(String path, DefaultMutableTreeNode treeNode, String separator)
	{
		Enumeration<TreeNode> children = treeNode.children();
		
		String findPath = FilenameUtils.getPath(path);
		String currentPath = buildPath(0, treeNode.getPath().length, treeNode, separator);
		String directory = FilenameUtils.getPath(currentPath);
		
		if(currentPath.startsWith(findPath))
		{
			//TODO this can be written without the need for .getParent
			((DefaultMutableTreeNode)treeNode.getParent()).add(new DefaultMutableTreeNode(FilenameUtils.getName(path)));
			return true;
		}
		
		while(children.hasMoreElements())
		{
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
			if(searchAndInsert(path, child, separator))
				return true;
		}
		
		return false;
	}
	
	public enum FileType
	{
		CLASS,
		FILE,
		DIRECTORY,
	}
}
