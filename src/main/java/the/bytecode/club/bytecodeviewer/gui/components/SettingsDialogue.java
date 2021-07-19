package the.bytecode.club.bytecodeviewer.gui.components;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
 * @since 7/19/2021
 */

public class SettingsDialogue extends JScrollPane
{
	public static final List<SettingsDialogue> dialogues = new ArrayList<>();
	private final List<JMenuItem> options = new ArrayList<>();
	private final JMenu menu;
	private final JPanel display;
	
	public SettingsDialogue(JMenu menu, JPanel display)
	{
		super(display);
		List<JMenuItem> options = new ArrayList<>();
		for(Component child : menu.getMenuComponents())
		{
			if(!(child instanceof JMenuItem))
				continue;
			
			options.add((JMenuItem) child);
		}
		
		this.menu = menu;
		this.options.addAll(options);
		this.display = display;
		
		buildPanel();
		
		dialogues.add(this);
	}
	
	private void buildPanel()
	{
		display.setLayout(new BoxLayout(display, BoxLayout.Y_AXIS));
		for(JMenuItem menuItem : options)
			display.add(menuItem);
	}
	
	public void showDialogue()
	{
		BetterJOptionPane.showJPanelDialogue(null, this, 460);
	}
	
	@Override
	public String getName()
	{
		return menu.getText();
	}
}