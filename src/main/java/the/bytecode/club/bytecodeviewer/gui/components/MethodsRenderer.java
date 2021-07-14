package the.bytecode.club.bytecodeviewer.gui.components;

import the.bytecode.club.bytecodeviewer.gui.util.BytecodeViewPanelUpdater;
import the.bytecode.club.bytecodeviewer.util.MethodParser;

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
 * @author Waterwolf
 * @since 09/29/2011
 */
public class MethodsRenderer extends JLabel implements ListCellRenderer<Object>
{
	private final BytecodeViewPanelUpdater bytecodeViewPanelUpdater;
	
	public MethodsRenderer(BytecodeViewPanelUpdater bytecodeViewPanelUpdater)
	{
		this.bytecodeViewPanelUpdater = bytecodeViewPanelUpdater;
		setOpaque(true);
	}
	
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
	                                              boolean cellHasFocus)
	{
		MethodParser methods = bytecodeViewPanelUpdater.viewer.methods.get(bytecodeViewPanelUpdater.bytecodeViewPanel.decompiler.ordinal());
		MethodParser.Method method = methods.getMethod((Integer) value);
		setText(method.toString());
		return this;
	}
}
