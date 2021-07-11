package the.bytecode.club.bytecodeviewer.plugin;

import org.apache.commons.io.FilenameUtils;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.resources.IconResources;

import java.io.IOException;

/**
 * @author Konloch
 * @since 7/1/2021
 */
public enum PluginTemplate
{
	JAVA("/templates/Template_Plugin.java"),
	JAVASCRIPT("/templates/Template_Plugin.js"),
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
			contents = IconResources.loadResourceAsString(resourcePath);
		
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
			BytecodeViewer.handleException(e);
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
