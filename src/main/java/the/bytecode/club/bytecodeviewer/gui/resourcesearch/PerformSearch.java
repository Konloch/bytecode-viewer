package the.bytecode.club.bytecodeviewer.gui.resourcesearch;

import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.searching.BackgroundSearchThread;
import the.bytecode.club.bytecodeviewer.searching.RegexInsnFinder;
import the.bytecode.club.bytecodeviewer.searching.impl.RegexSearch;
import the.bytecode.club.bytecodeviewer.searching.SearchResultNotifier;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;

import javax.swing.tree.TreePath;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
 * @since 6/25/2021
 */
class PerformSearch extends BackgroundSearchThread
{
	private final SearchBoxPane searchBoxPane;
	private final SearchResultNotifier srn;
	
	public PerformSearch(SearchBoxPane searchBoxPane, SearchResultNotifier srn)
	{
		this.searchBoxPane = searchBoxPane;
		this.srn = srn;
	}
	
	@Override
	public void search()
	{
		try
		{
			Pattern.compile(RegexInsnFinder.processRegex(RegexSearch.searchText.getText()), Pattern.MULTILINE);
		}
		catch (PatternSyntaxException ex)
		{
			BytecodeViewer.showMessage("You have an error in your regex syntax.");
		}
		
		for (ResourceContainer container : BytecodeViewer.resourceContainers.values())
			for (ClassNode c : container.resourceClasses.values())
				searchBoxPane.searchType.details.search(container, c, srn, searchBoxPane.exact.isSelected());
		
		BytecodeViewer.viewer.searchBoxPane.search.setEnabled(true);
		BytecodeViewer.viewer.searchBoxPane.search.setText(TranslatedStrings.SEARCH.toString());
		
		searchBoxPane.tree.expandPath(new TreePath(searchBoxPane.tree.getModel().getRoot()));
		searchBoxPane.tree.updateUI();
	}
}
