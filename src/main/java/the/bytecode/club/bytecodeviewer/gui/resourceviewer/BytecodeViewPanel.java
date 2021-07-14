package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.compilers.Compiler;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.gui.components.SearchableRSyntaxTextArea;
import the.bytecode.club.bytecodeviewer.gui.components.SystemConsole;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ClassViewer;
import the.bytecode.club.bytecodeviewer.gui.util.BytecodeViewPanelUpdater;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.util.JarUtils;

import javax.swing.*;
import java.awt.*;

import static the.bytecode.club.bytecodeviewer.Constants.nl;

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
 * Represents a Bytecode/ClassFile View Panel
 *
 * @author Konloch
 * @since 6/24/2021
 */
public class BytecodeViewPanel
{
	public final JPanel panel = new JPanel(new BorderLayout());
	
	public final int panelIndex;
	public final ClassViewer viewer;
	public SearchableRSyntaxTextArea textArea;
	public BytecodeViewPanelUpdater updateThread;
	public Decompiler decompiler = Decompiler.NONE;
	public Compiler compiler = Compiler.JAVA_COMPILER;
	
	public BytecodeViewPanel(int panelIndex, ClassViewer viewer)
	{
		this.panelIndex = panelIndex;
		this.viewer = viewer;
	}
	
	public void createPane(ClassViewer viewer)
	{
		panel.removeAll();
		textArea = null;
		
		if(viewer.viewerClassNode == null)
			panel.add(new JLabel("ERROR: Resource Viewer Corrupt ClassNode"));
	}
	
	public void updatePane(ClassViewer cv, byte[] b, JButton button, boolean isPanelEditable)
	{
		updateThread = new BytecodeViewPanelUpdater(this, cv, b, isPanelEditable, button);
	}
	
	public boolean compile()
	{
		if(textArea == null || !textArea.isEditable())
			return true;
		
		SystemConsole errConsole = new SystemConsole("Java Compile Issues");
		errConsole.setText("Error compiling class: " + viewer.viewerClassNode.name +
				nl + "Keep in mind most decompilers cannot produce compilable classes" +
				nl + nl + TranslatedStrings.SUGGESTED_FIX_COMPILER_ERROR +
				nl + nl);
		
		try
		{
			String text = textArea.getText();
			byte[] compiledClass = compiler.getCompiler().compile(text, viewer.viewerClassNode.name);
			
			if (compiledClass != null)
			{
				ClassNode newNode = JarUtils.getNode(compiledClass);
				viewer.container.updateNode(viewer.name, newNode);
				errConsole.finished();
				return true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		errConsole.setVisible(true);
		errConsole.finished();
		return false;
	}
}
