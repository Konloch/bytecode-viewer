package the.bytecode.club.bytecodeviewer.gui.components;

import the.bytecode.club.bytecodeviewer.util.RefreshWorkPane;

import javax.swing.*;

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
 * @since 6/21/2021
 */
public class DecompilerViewComponent
{
	private final String name;
	private final boolean hasBytecodeOption;
	private final JMenu menu;
	private final JRadioButtonMenuItem java = new JRadioButtonMenuItem("Java");
	private final JRadioButtonMenuItem bytecode = new JRadioButtonMenuItem("Bytecode");
	private final JCheckBoxMenuItem editable = new JCheckBoxMenuItem("Editable");
	
	public DecompilerViewComponent(String name) {
		this(name, false);
	}
	
	public DecompilerViewComponent(String name, boolean hasBytecodeOption) {
		this.name = name;
		this.menu = new JMenu(name);
		this.hasBytecodeOption = hasBytecodeOption;
		createMenu();
	}
	
	private void createMenu()
	{
		menu.add(java);
		if(hasBytecodeOption)
			menu.add(bytecode);
		
		menu.add(new JSeparator());
		menu.add(editable);
		
		java.addActionListener(new RefreshWorkPane());
	}
	
	public void addToGroup(ButtonGroup group)
	{
		group.add(java);
		if(hasBytecodeOption)
			group.add(bytecode);
	}
	
	public JMenu getMenu()
	{
		return menu;
	}
	
	public JRadioButtonMenuItem getJava()
	{
		return java;
	}
	
	public JRadioButtonMenuItem getBytecode()
	{
		return bytecode;
	}
	
	public JCheckBoxMenuItem getEditable()
	{
		return editable;
	}
}
