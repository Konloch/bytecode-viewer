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
 * @author Konloch
 * @since 6/24/2021
 */
public class ResourceViewPanel
{
	public final JPanel panel = new JPanel(new BorderLayout());
	
	public ClassViewer viewer;
	public int decompilerViewIndex = -1;
	public SearchableRSyntaxTextArea textArea;
	public PaneUpdaterThread updateThread;
	public final int panelIndex;
	
	//TODO change the compile mode for Krakatau and Smali assembly
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
		updateThread = new PaneUpdaterThread(panelIndex, decompilerViewIndex)
		{
			@Override
			public void doShit()
			{
				try
				{
					BytecodeViewer.viewer.updateBusyStatus(true);
					
					if(ResourceViewPanel.this.decompilerViewIndex > 0)
					{
						//hex viewer
						if (ResourceViewPanel.this.decompilerViewIndex == 5)
						{
							final ClassWriter cw = new ClassWriter(0);
							cv.cn.accept(cw);
							
							final JHexEditor hex = new JHexEditor(cw.toByteArray());
							hex.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));
							
							SwingUtilities.invokeLater(() -> panel.add(hex));
						}
						else
						{
							viewer = cv;
							updateUpdaterTextArea = (SearchableRSyntaxTextArea) Configuration.rstaTheme.apply(new SearchableRSyntaxTextArea());
							
							final Decompiler decompiler = Decompiler.decompilersByIndex.get(ResourceViewPanel.this.decompilerViewIndex);
							
							//perform decompiling inside of this thread
							final String decompiledSource = decompiler.getDecompiler().decompileClassNode(cv.cn, b);
							
							//set the swing components on the swing thread
							SwingUtilities.invokeLater(() ->
							{
								panel.add(updateUpdaterTextArea.getScrollPane());
								panel.add(updateUpdaterTextArea.getTitleHeader(), BorderLayout.NORTH);
								
								textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
								textArea.setCodeFoldingEnabled(true);
								textArea.setAntiAliasingEnabled(true);
								textArea.setText(decompiledSource);
								textArea.setCaretPosition(0);
								textArea.setEditable(isPanelEditable);
								
								textArea.getTitleHeader().setText(decompiler.getDecompilerName() + " - Editable: " + textArea.isEditable());
								textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN,
										(int) BytecodeViewer.viewer.fontSpinner.getValue()));
							});
							textArea = updateUpdaterTextArea;
						}
					}
				} catch (java.lang.IndexOutOfBoundsException | java.lang.NullPointerException e) {
					//ignore
				} catch (Exception e) {
					new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
				} finally {
					cv.resetDivider();
					BytecodeViewer.viewer.updateBusyStatus(false);
					if (button != null)
						button.setEnabled(true);
				}
			}
		};
	}
}
