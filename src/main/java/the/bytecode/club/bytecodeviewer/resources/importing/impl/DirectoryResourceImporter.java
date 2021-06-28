package the.bytecode.club.bytecodeviewer.resources.importing.impl;

import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.resources.importing.Import;
import the.bytecode.club.bytecodeviewer.resources.importing.Importer;
import the.bytecode.club.bytecodeviewer.util.FileContainer;
import the.bytecode.club.bytecodeviewer.util.JarUtils;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

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
		HashMap<String, byte[]> allDirectoryFiles = new HashMap<>();
		HashMap<String, ClassNode> allDirectoryClasses = new HashMap<>();
		
		boolean finished = false;
		ArrayList<File> totalFiles = new ArrayList<>();
		totalFiles.add(file);
		String dir = file.getAbsolutePath();
		
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
						Import.ZIP.getImporter().open(child);
					}
					else if (fileName.endsWith(".apk"))
					{
						Import.APK.getImporter().open(child);
					}
					else if (fileName.endsWith(".dex"))
					{
						Import.DEX.getImporter().open(child);
					}
					else if (fileName.endsWith(".class"))
					{
						byte[] bytes = Files.readAllBytes(Paths.get(child.getAbsolutePath()));
						if (MiscUtils.getFileHeader(bytes).equalsIgnoreCase("cafebabe"))
						{
							final ClassNode cn = JarUtils.getNode(bytes);
							allDirectoryClasses.put(trimmedPath, cn);
						}
					}
					else
					{
						//pack files into a single container
						allDirectoryFiles.put(trimmedPath, Files.readAllBytes(Paths.get(child.getAbsolutePath())));
					}
				}
				
				finished = true;
			}
		}
		
		container.classes.addAll(allDirectoryClasses.values());
		container.files = allDirectoryFiles;
		BytecodeViewer.files.add(container);
		return true;
	}
}
