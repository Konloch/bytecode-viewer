package the.bytecode.club.bytecodeviewer.gui.resourcesearch;

import the.bytecode.club.bytecodeviewer.searching.*;

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
