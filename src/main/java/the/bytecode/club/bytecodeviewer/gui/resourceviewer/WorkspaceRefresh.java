package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import java.awt.event.ActionEvent;
import javax.swing.JButton;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ResourceViewer;

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
 * @since 6/24/2021
 */
public class WorkspaceRefresh implements Runnable
{
	private final ActionEvent event;
	
	public WorkspaceRefresh(ActionEvent event) {
		this.event = event;
	}
	
	@Override
	public void run()
	{
		if (!BytecodeViewer.autoCompileSuccessful())
			return;
		
		JButton src = null;
		if(event != null && event.getSource() instanceof JButton)
			src = (JButton) event.getSource();
		
		final ResourceViewer tabComp = (ResourceViewer) BytecodeViewer.viewer.workPane.tabs.getSelectedComponent();
		
		if(tabComp == null)
			return;
		
		BytecodeViewer.updateBusyStatus(true);
		tabComp.refresh(src);
		BytecodeViewer.updateBusyStatus(false);
	}
}
