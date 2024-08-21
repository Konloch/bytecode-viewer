/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Konloch - Konloch.com / BytecodeViewer.com           *
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

package the.bytecode.club.bytecodeviewer.gui.resourcesearch;

import the.bytecode.club.bytecodeviewer.searching.SearchPanel;
import the.bytecode.club.bytecodeviewer.searching.impl.*;

/**
 * @author Konloch
 * @since 6/25/2021
 */
public enum SearchType
{
	Strings(new LDCSearch()),
	Regex(new RegexSearch()),
	MethodCall(new MethodCallSearch()),
	FieldCall(new FieldCallSearch()),
	MemberWithAnnotation(new MemberWithAnnotationSearch())
	;
	
	public final SearchPanel panel;
	
	SearchType(SearchPanel panel)
	{
		this.panel = panel;
	}
}
