package the.bytecode.club.bytecodeviewer.resources.importing.impl;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.resources.importing.ImportType;
import the.bytecode.club.bytecodeviewer.resources.importing.Importer;
import the.bytecode.club.bytecodeviewer.util.FileContainer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author Konloch
 * @since 6/26/2021
 */
public class DirectoryResourceImporter implements Importer
{
	@Override
	public boolean open(File file) throws Exception
	{
		FileContainer container = new FileContainer(file);
		HashMap<String, byte[]> files1 = new HashMap<>();
		boolean finished = false;
		ArrayList<File> totalFiles = new ArrayList<>();
		totalFiles.add(file);
		String dir = file.getAbsolutePath();//f.getAbsolutePath().substring(0, f.getAbsolutePath
		// ().length()-f.getName().length());
		
		while (!finished)
		{
			boolean added = false;
			for (int i = 0; i < totalFiles.size(); i++)
			{
				File child = totalFiles.get(i);
				if (child.listFiles() != null)
					for (File rocket : Objects.requireNonNull(child.listFiles()))
						if (!totalFiles.contains(rocket))
						{
							totalFiles.add(rocket);
							added = true;
						}
			}
			
			if (!added)
			{
				for (File child : totalFiles)
				{
					if(!child.isFile())
						continue;
					
					final String trimmedPath = child.getAbsolutePath().substring(dir.length() + 1)
							.replaceAll("\\\\", "\\/");
					final String fileName = child.getName();
					
					//attempt to import archives automatically
					if (fileName.endsWith(".jar") || fileName.endsWith(".zip") || fileName.endsWith(".war"))
					{
						ImportType.ZIP.getImporter().open(child);
					}
					else if (fileName.endsWith(".apk"))
					{
						ImportType.APK.getImporter().open(child);
					}
					else if (fileName.endsWith(".dex"))
					{
						ImportType.DEX.getImporter().open(child);
					}
					else
					{
						//pack files into a single container
						files1.put(trimmedPath, Files.readAllBytes(Paths.get(child.getAbsolutePath())));
					}
				}
				
				finished = true;
			}
		}
		
		container.files = files1;
		BytecodeViewer.files.add(container);
		return true;
	}
}
