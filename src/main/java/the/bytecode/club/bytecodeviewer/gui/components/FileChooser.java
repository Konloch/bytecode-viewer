package the.bytecode.club.bytecodeviewer.gui.components;

import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author Konloch
 * @since 6/25/2021
 */
public class FileChooser extends JFileChooser
{
	public static final String EVERYTHING = "everything";
	
	public FileChooser(File file, String title, String description, String... extensions)
	{
		this(false, file, title, description, extensions);
	}
	
	public FileChooser(boolean skipFileFilter, File file, String title, String description, String... extensions)
	{
		HashSet<String> extensionSet = new HashSet<>(Arrays.asList(extensions));
		
		try {
			setSelectedFile(file);
		} catch (Exception ignored) { }
		
		setDialogTitle(title);
		setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		setFileHidingEnabled(false);
		setAcceptAllFileFilterUsed(false);
		if(!skipFileFilter)
		{
			setFileFilter(new FileFilter()
			{
				@Override
				public boolean accept(File f)
				{
					if (f.isDirectory())
						return true;
					
					if(extensions[0].equals(EVERYTHING))
						return true;
					
					return extensionSet.contains(MiscUtils.extension(f.getAbsolutePath()));
				}
				
				@Override
				public String getDescription() {
					return description;
				}
			});
		}
	}
}
