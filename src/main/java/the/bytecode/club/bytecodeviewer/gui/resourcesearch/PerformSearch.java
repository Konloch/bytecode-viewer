package the.bytecode.club.bytecodeviewer.gui.resourcesearch;

import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.gui.MainViewerGUI;
import the.bytecode.club.bytecodeviewer.searching.BackgroundSearchThread;
import the.bytecode.club.bytecodeviewer.searching.RegexInsnFinder;
import the.bytecode.club.bytecodeviewer.searching.RegexSearch;
import the.bytecode.club.bytecodeviewer.searching.SearchResultNotifier;
import the.bytecode.club.bytecodeviewer.util.FileContainer;

import javax.swing.tree.TreePath;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
	public void doSearch()
	{
		try
		{
			Pattern.compile(RegexInsnFinder.processRegex(RegexSearch.searchText.getText()), Pattern.MULTILINE);
		}
		catch (PatternSyntaxException ex)
		{
			BytecodeViewer.showMessage("You have an error in your regex syntax.");
		}
		
		for (FileContainer container : BytecodeViewer.files)
			for (ClassNode c : container.classes)
				searchBoxPane.searchType.details.search(container, c, srn, searchBoxPane.exact.isSelected());
		
		Objects.requireNonNull(MainViewerGUI.getComponent(SearchBoxPane.class)).search.setEnabled(true);
		Objects.requireNonNull(MainViewerGUI.getComponent(SearchBoxPane.class)).search.setText("Search");
		
		searchBoxPane.tree.expandPath(new TreePath(searchBoxPane.tree.getModel().getRoot()));
		searchBoxPane.tree.updateUI();
	}
}
