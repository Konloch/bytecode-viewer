package the.bytecode.club.bytecodeviewer.plugin;

import org.apache.commons.io.FilenameUtils;
import the.bytecode.club.bytecodeviewer.Resources;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;

import java.io.IOException;

/**
 * @author Konloch
 * @since 7/1/2021
 */
public enum PluginTemplate
{
	JAVA("/templates/Template.java"),
	JAVASCRIPT("/templates/Template.js"),
	;
	
	private final String resourcePath;
	private final String extension;
	private String contents;
	
	PluginTemplate(String resourcePath)
	{
		this.resourcePath = resourcePath;
		this.extension = FilenameUtils.getExtension(resourcePath);
	}
	
	public String getContents() throws IOException
	{
		if(contents == null)
			contents = Resources.loadResourceAsString(resourcePath);
		
		return contents;
	}
	
	public String getExtension()
	{
		return extension;
	}
	
	public PluginWriter openEditorExceptionHandled()
	{
		try
		{
			return openEditor();
		}
		catch (IOException e)
		{
			new ExceptionUI(e);
		}
		
		return null;
	}
	
	public PluginWriter openEditor() throws IOException
	{
		PluginWriter writer = new PluginWriter(this);
		writer.setVisible(true);
		return writer;
	}
}
