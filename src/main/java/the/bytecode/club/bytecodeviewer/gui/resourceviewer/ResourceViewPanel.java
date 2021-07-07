package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.compilers.Compiler;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.gui.components.SearchableRSyntaxTextArea;
import the.bytecode.club.bytecodeviewer.gui.components.SystemConsole;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ClassViewer;
import the.bytecode.club.bytecodeviewer.gui.util.PaneUpdaterThread;
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
 * @author Konloch
 * @since 6/24/2021
 */
public class ResourceViewPanel
{
	public final JPanel panel = new JPanel(new BorderLayout());
	
	public final int panelIndex;
	public final ClassViewer viewer;
	public Decompiler decompiler = Decompiler.NONE;
	public SearchableRSyntaxTextArea textArea;
	public PaneUpdaterThread updateThread;
	
	public Compiler compileMode = Compiler.JAVA_COMPILER;
	
	public ResourceViewPanel(int panelIndex, ClassViewer viewer) {this.panelIndex = panelIndex;
		this.viewer = viewer;
	}
	
	public void createPane(ClassViewer viewer)
	{
		panel.removeAll();
		textArea = null;
		
		if(viewer.cn == null)
		{
			panel.add(new JLabel("This resource has been removed."));
		}
	}
	
	public void updatePane(ClassViewer cv, byte[] b, JButton button, boolean isPanelEditable)
	{
		updateThread = new ResourceViewProcessing(this, cv, b, isPanelEditable, button);
	}
	
	public boolean compile()
	{
		if(textArea == null || !textArea.isEditable())
			return true;
		
		SystemConsole errConsole = new SystemConsole("Java Compile Issues");
		errConsole.setText("Error compiling class: " + viewer.cn.name +
				nl + "Keep in mind most decompilers cannot produce compilable classes" +
				nl + nl + TranslatedStrings.SUGGESTED_FIX_COMPILER_ERROR +
				nl + nl);
		
		try
		{
			String text = textArea.getText();
			byte[] compiledClass = compileMode.getCompiler().compile(text, viewer.cn.name);
			
			if (compiledClass != null)
			{
				ClassNode newNode = JarUtils.getNode(compiledClass);
				BytecodeViewer.updateNode(viewer.cn, newNode);
				errConsole.finished();
				return true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		errConsole.pretty();
		errConsole.setVisible(true);
		errConsole.finished();
		return false;
	}
}
