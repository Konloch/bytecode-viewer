package the.bytecode.club.bytecodeviewer.gui.resourcelist.contextmenu.impl;

import org.apache.commons.io.FilenameUtils;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.gui.resourcelist.contextmenu.ContextMenuItem;
import the.bytecode.club.bytecodeviewer.gui.resourcelist.contextmenu.ContextMenuType;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
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
 * @since 7/27/2021
 */
public class New extends ContextMenuItem
{
	public New()
	{
		super(ContextMenuType.ALL, ((tree, selPath, menu) ->
		{
			JMenu quickOpen = new JMenu(TranslatedStrings.NEW.toString());
			quickOpen.add(createMenu("Class", FileType.CLASS, selPath));
			quickOpen.add(createMenu("File", FileType.FILE, selPath));
			//quickOpen.add(createMenu("Directory", FileType.DIRECTORY, selPath));
			menu.add(quickOpen);
		}));
	}
	
	private static JMenuItem createMenu(String name, FileType fileType, TreePath selPath)
	{
		JMenuItem menu = new JMenuItem(name);
		
		String separator = "/"; //fileType == FileType.CLASS ? "." : "/";
		String firstPath = buildPath(0, 2, selPath, separator);
		String path = buildPath(2, selPath.getPathCount(), selPath, separator);
		String containerName = selPath.getPathComponent(1).toString();
		
		menu.addActionListener((e)->{
			String newPath = BytecodeViewer.showInput("Name", "Enter the file name", path);
			
			if(newPath == null || newPath.isEmpty())
				return;
			
			switch(fileType)
			{
				case FILE:
					BytecodeViewer.resourceContainers.get(containerName).resourceFiles.put(newPath, new byte[0]);
					searchAndInsert(firstPath+"/"+newPath, BytecodeViewer.resourceContainers.get(containerName).treeNode);
					break;
					
				case CLASS:
					//TODO needs to be an empty class file, asm can help with this
					break;
			}
		});
		
		return menu;
	}
	
	public static String buildPath(int startsAt, int max, TreePath selPath, String separator)
	{
		StringBuilder tempSpot = new StringBuilder();
		
		for(int counter = startsAt, maxCounter = max;counter < maxCounter; counter++)
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
		
		for(int counter = startsAt, maxCounter = max;counter < maxCounter; counter++)
		{
			if(counter > startsAt)
				tempSpot.append(separator);
			tempSpot.append(treeNode.getPath()[counter]);
		}
		
		return tempSpot.toString();
	}
	
	//TODO this needs to be rewritten to support creating parent nodes that don't exist
	public static boolean searchAndInsert(String path, DefaultMutableTreeNode treeNode)
	{
		Enumeration<DefaultMutableTreeNode> children = treeNode.children();
		
		String findPath = FilenameUtils.getPath(path);
		String currentPath = buildPath(0, treeNode.getPath().length, treeNode, "/");
		String directory = FilenameUtils.getPath(currentPath);
		
		if(currentPath.startsWith(findPath))
		{
			//TODO this can be written without the need for .getParent
			((DefaultMutableTreeNode)treeNode.getParent()).add(new DefaultMutableTreeNode(FilenameUtils.getName(path)));
			return true;
		}
		
		while(children.hasMoreElements())
		{
			DefaultMutableTreeNode child = children.nextElement();
			if(searchAndInsert(path, child))
				return true;
		}
		
		return false;
	}
	
	public enum FileType
	{
		CLASS,
		FILE,
	}
}