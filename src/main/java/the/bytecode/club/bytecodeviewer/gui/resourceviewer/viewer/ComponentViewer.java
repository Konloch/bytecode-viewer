package the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JButton;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.resources.Resource;

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
 * This represents a component opened as a tab
 *
 * @author Konloch
 * @since 7/23/2021
 */

public class ComponentViewer extends ResourceViewer
{
	private final Component component;
	private static final String containerName = "internalComponent.";
	
	public ComponentViewer(String title, Component component)
	{
		super(new Resource(title, containerName + title, null));
		
		this.component = component;
		
		setLayout(new BorderLayout());
		setName(title);
		add(component, BorderLayout.CENTER);
	}
	
	public static ComponentViewer addComponentAsTab(String title, Component c)
	{
		String workingName = containerName + title;
		ComponentViewer componentViewer = new ComponentViewer(title, c);
		BytecodeViewer.viewer.workPane.addResourceToTab(componentViewer,
				workingName, containerName, title);
		
		return componentViewer;
	}
	
	@Override
	public void refresh(JButton button) {
		//TODO add a refresh event so the component can be updated
	}
}
