package the.bytecode.club.bytecodeviewer.gui.components;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import static the.bytecode.club.bytecodeviewer.Configuration.useNewSettingsDialog;

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

public class SettingsDialog extends JScrollPane
{
	public static final List<JComponent> components = new ArrayList<>();
	public static final List<JDialog> dialogs = new ArrayList<>();
	private final List<JMenuItem> options = new ArrayList<>();
	private final JMenu menu;
	private final JPanel display;
	
	public SettingsDialog(JMenu menu, JPanel display)
	{
		super(display);
		
		this.menu = menu;
		this.display = display;
		
		if(!useNewSettingsDialog)
			return;
		
		List<JMenuItem> options = new ArrayList<>();
		for(Component child : menu.getMenuComponents())
		{
			if(!(child instanceof JMenuItem))
				continue;
			
			JMenuItem menuItem = (JMenuItem) child;
			
			options.add(menuItem);
			
			//force unselect after a selection has been made
			//this fixes a graphical bug from forcing menu items on non-menus
			menuItem.addActionListener(e -> unselectAll());
		}
		
		this.options.addAll(options);
		
		buildPanel();
		
		components.add(this);
	}
	
	public void unselectAll()
	{
		options.forEach(jMenuItem -> jMenuItem.setArmed(false));
	}
	
	public void showDialog()
	{
		ExtendedJOptionPane.showJPanelDialog(null, this, 460, dialogs::add);
	}
	
	private void buildPanel()
	{
		display.setLayout(new BoxLayout(display, BoxLayout.Y_AXIS));
		for(JMenuItem menuItem : options)
			display.add(menuItem);
	}
	
	@Override
	public String getName()
	{
		if(menu == null)
			return "ERROR: Dialog missing menu";
		
		return menu.getText();
	}
}
