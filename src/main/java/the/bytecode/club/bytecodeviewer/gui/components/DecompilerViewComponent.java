package the.bytecode.club.bytecodeviewer.gui.components;

import the.bytecode.club.bytecodeviewer.translation.Translation;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJCheckBoxMenuItem;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJRadioButtonMenuItem;
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
	private final JMenu menu;
	private final DecompilerComponentTypes types;
	private final JRadioButtonMenuItem java = new TranslatedJRadioButtonMenuItem("Java", Translation.JAVA);
	private final JRadioButtonMenuItem bytecode = new TranslatedJRadioButtonMenuItem("Bytecode", Translation.BYTECODE);
	private final JCheckBoxMenuItem editable = new TranslatedJCheckBoxMenuItem("Editable", Translation.EDITABLE);
	
	public DecompilerViewComponent(String name, DecompilerComponentTypes types) {
		this.name = name;
		this.menu = new JMenu(name);
		this.types = types;
		createMenu();
	}
	
	private void createMenu()
	{
		if(types == DecompilerComponentTypes.JAVA || types == DecompilerComponentTypes.JAVA_AND_BYTECODE)
			menu.add(java);
		if(types == DecompilerComponentTypes.BYTECODE || types == DecompilerComponentTypes.JAVA_AND_BYTECODE)
			menu.add(bytecode);
		
		menu.add(new JSeparator());
		menu.add(editable);
		
		java.addActionListener(new RefreshWorkPane());
	}
	
	public void addToGroup(ButtonGroup group)
	{
		if(types == DecompilerComponentTypes.JAVA || types == DecompilerComponentTypes.JAVA_AND_BYTECODE)
			group.add(java);
		if(types == DecompilerComponentTypes.BYTECODE || types == DecompilerComponentTypes.JAVA_AND_BYTECODE)
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
	
	public enum DecompilerComponentTypes
	{
		JAVA,
		BYTECODE,
		JAVA_AND_BYTECODE
	}
}
