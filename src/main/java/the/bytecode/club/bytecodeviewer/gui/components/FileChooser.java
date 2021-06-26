package the.bytecode.club.bytecodeviewer.gui.components;

import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author Konloch
 * @since 6/25/2021
 */
public class FileChooser extends JFileChooser
{
	private final File filePath;
	private final String title;
	private final String description;
	private final String[] extensions;
	private final HashSet<String> extensionSet = new HashSet<>();
	
	public FileChooser(File filePath, String title, String description, String... extensions)
	{
		this.filePath = filePath;
		this.title = title;
		this.description = description;
		this.extensions = extensions;
		this.extensionSet.addAll(Arrays.asList(extensions));
		
		try {
			if (filePath.exists())
				setSelectedFile(filePath);
		} catch (Exception ignored) { }
		
		setDialogTitle(title);
		setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		setFileHidingEnabled(false);
		setAcceptAllFileFilterUsed(false);
		setFileFilter(new FileFilter()
		{
			@Override
			public boolean accept(File f)
			{
				if (f.isDirectory())
					return true;
				
				if(extensions[0].equals("everything"))
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
