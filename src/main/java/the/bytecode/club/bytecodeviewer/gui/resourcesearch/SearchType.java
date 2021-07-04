package the.bytecode.club.bytecodeviewer.gui.resourcesearch;

import the.bytecode.club.bytecodeviewer.searching.*;
import the.bytecode.club.bytecodeviewer.searching.impl.FieldCallSearch;
import the.bytecode.club.bytecodeviewer.searching.impl.LDCSearch;
import the.bytecode.club.bytecodeviewer.searching.impl.MethodCallSearch;
import the.bytecode.club.bytecodeviewer.searching.impl.RegexSearch;

/**
 * @author Konloch
 * @since 6/25/2021
 */
public enum SearchType
{
	Strings(new LDCSearch()),
	Regex(new RegexSearch()),
	MethodCall(new MethodCallSearch()),
	FieldCall(new FieldCallSearch());
	
	public final SearchTypeDetails details;
	
	SearchType(final SearchTypeDetails details)
	{
		this.details = details;
	}
}
