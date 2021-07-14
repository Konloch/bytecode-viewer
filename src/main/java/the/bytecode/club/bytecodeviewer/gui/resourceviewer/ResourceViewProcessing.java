package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import org.objectweb.asm.ClassWriter;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.gui.hexviewer.JHexEditor;
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
 * All classfile resources get preprocessed through this class, from there the specified UI panel is used to display the data
 *
 * @author Konloch
 * @since 6/27/2021
 */

public class ResourceViewProcessing extends PaneUpdaterThread
{
	private final ResourceViewPanel resourceViewPanel;
	private final byte[] b;
	private final JButton button;
	public boolean waitingFor;
	
	public ResourceViewProcessing(ResourceViewPanel resourceViewPanel, ClassViewer cv, byte[] b, boolean isPanelEditable, JButton button)
	{
		super(cv, resourceViewPanel);
		this.resourceViewPanel = resourceViewPanel;
		this.b = b;
		this.isPanelEditable = isPanelEditable;
		this.button = button;
		waitingFor = true;
	}
	
	@Override
	public void processDisplay()
	{
		try
		{
			BytecodeViewer.updateBusyStatus(true);
			
			if (resourceViewPanel.decompiler != Decompiler.NONE)
			{
				//hex viewer
				if (resourceViewPanel.decompiler == Decompiler.HEXCODE_VIEWER)
				{
					final ClassWriter cw = new ClassWriter(0);
					viewer.viewerClassNode.accept(cw);
					
					SwingUtilities.invokeLater(() ->
					{
						final JHexEditor hex = new JHexEditor(cw.toByteArray());
						hex.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));
						
						resourceViewPanel.panel.add(hex);
					});
				}
				else
				{
					final Decompiler decompiler = resourceViewPanel.decompiler;
					
					//perform decompiling inside of this thread
					final String decompiledSource = decompiler.getDecompiler().decompileClassNode(viewer.viewerClassNode, b);
					
					//set the swing components on the swing thread
					SwingUtilities.invokeLater(() ->
					{
						buildTextArea(decompiler, decompiledSource);
						waitingFor = false;
					});
					
					//hold this thread until the swing thread has finished attaching the components
					while (waitingFor)
					{
						try {
							Thread.sleep(1);
						} catch (Exception e) {}
					}
				}
			}
		}
		catch (IndexOutOfBoundsException | NullPointerException e)
		{
			//ignore
		}
		catch (Exception e)
		{
			BytecodeViewer.handleException(e);
		}
		finally
		{
			viewer.resetDivider();
			BytecodeViewer.updateBusyStatus(false);
			SwingUtilities.invokeLater(() ->
			{
				if (button != null)
					button.setEnabled(true);
			});
		}
	}
}
