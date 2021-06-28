package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.objectweb.asm.ClassWriter;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.gui.components.SearchableRSyntaxTextArea;
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
	private final ClassViewer cv;
	private final byte[] b;
	private final boolean isPanelEditable;
	private final JButton button;
	public boolean waitingFor;
	
	public ResourceViewProcessing(ResourceViewPanel resourceViewPanel, ClassViewer cv, byte[] b, boolean isPanelEditable, JButton button)
	{
		super(resourceViewPanel.panelIndex, resourceViewPanel.decompilerViewIndex);
		this.resourceViewPanel = resourceViewPanel;
		this.cv = cv;
		this.b = b;
		this.isPanelEditable = isPanelEditable;
		this.button = button;
		waitingFor = true;
	}
	
	@Override
	public void doShit()
	{
		try
		{
			BytecodeViewer.viewer.updateBusyStatus(true);
			
			if (resourceViewPanel.decompilerViewIndex > 0)
			{
				//hex viewer
				if (resourceViewPanel.decompilerViewIndex == 5)
				{
					final ClassWriter cw = new ClassWriter(0);
					cv.cn.accept(cw);
					
					final JHexEditor hex = new JHexEditor(cw.toByteArray());
					hex.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));
					
					SwingUtilities.invokeLater(() -> resourceViewPanel.panel.add(hex));
				}
				else
				{
					viewer = cv;
					updateUpdaterTextArea = (SearchableRSyntaxTextArea) Configuration.rstaTheme.apply(new SearchableRSyntaxTextArea());
					
					final Decompiler decompiler = Decompiler.decompilersByIndex.get(resourceViewPanel.decompilerViewIndex);
					
					//perform decompiling inside of this thread
					final String decompiledSource = decompiler.getDecompiler().decompileClassNode(cv.cn, b);
					
					resourceViewPanel.textArea = updateUpdaterTextArea;
					
					//set the swing components on the swing thread
					SwingUtilities.invokeLater(() ->
					{
						resourceViewPanel.panel.add(updateUpdaterTextArea.getScrollPane());
						resourceViewPanel.panel.add(updateUpdaterTextArea.getTitleHeader(), BorderLayout.NORTH);
						
						resourceViewPanel.textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
						resourceViewPanel.textArea.setCodeFoldingEnabled(true);
						resourceViewPanel.textArea.setAntiAliasingEnabled(true);
						resourceViewPanel.textArea.setText(decompiledSource);
						resourceViewPanel.textArea.setCaretPosition(0);
						resourceViewPanel.textArea.setEditable(isPanelEditable);
						
						resourceViewPanel.textArea.getTitleHeader().setText(decompiler.getDecompilerName() + " - Editable: " + resourceViewPanel.textArea.isEditable());
						resourceViewPanel.textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN,
								(int) BytecodeViewer.viewer.fontSpinner.getValue()));
						
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
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
		}
		finally
		{
			cv.resetDivider();
			BytecodeViewer.viewer.updateBusyStatus(false);
			if (button != null)
				button.setEnabled(true);
		}
	}
}
