package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.gui.components.SearchableRSyntaxTextArea;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ClassViewer;
import the.bytecode.club.bytecodeviewer.gui.util.PaneUpdaterThread;

import javax.swing.*;
import java.awt.*;

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
public class ResourceViewPanel
{
	public final JPanel panel = new JPanel(new BorderLayout());
	
	public ClassViewer viewer;
	public Decompiler decompiler = Decompiler.NONE;
	public SearchableRSyntaxTextArea textArea;
	public PaneUpdaterThread updateThread;
	public final int panelIndex;
	
	public ResourcePanelCompileMode compileMode = ResourcePanelCompileMode.JAVA;
	
	public ResourceViewPanel(int panelIndex) {this.panelIndex = panelIndex;}
	
	public void createPane(ClassViewer viewer)
	{
		panel.removeAll();
		textArea = null;
		
		if(viewer.cn == null)
		{
			panel.add(new JLabel("This file has been removed from the reload."));
		}
	}
	
	public void updatePane(ClassViewer cv, byte[] b, JButton button, boolean isPanelEditable)
	{
		updateThread = new ResourceViewProcessing(this, cv, b, isPanelEditable, button);
	}
}
