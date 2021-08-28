package the.bytecode.club.bytecodeviewer.plugin;

import java.io.IOException;
import org.apache.commons.io.FilenameUtils;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.resources.IconResources;

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
