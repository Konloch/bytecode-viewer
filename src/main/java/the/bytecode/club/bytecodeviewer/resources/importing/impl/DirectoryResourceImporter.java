package the.bytecode.club.bytecodeviewer.resources.importing.impl;

import org.apache.commons.io.FilenameUtils;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.resources.importing.Import;
import the.bytecode.club.bytecodeviewer.resources.importing.ImportResource;
import the.bytecode.club.bytecodeviewer.resources.importing.Importer;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.util.JarUtils;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author Konloch
 * @since 6/26/2021
 */
public class DirectoryResourceImporter implements Importer
{
	@Override
	public void open(File file) throws Exception
	{
		ResourceContainer container = new ResourceContainer(file);
		LinkedHashMap<String, byte[]> allDirectoryFiles = new LinkedHashMap<>();
		LinkedHashMap<String, ClassNode> allDirectoryClasses = new LinkedHashMap<>();
		
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
					if(ImportResource.importFile(file))
					{
						//let import resource handle it
					}
					else if (fileName.endsWith(".class"))
					{
						byte[] bytes = Files.readAllBytes(Paths.get(child.getAbsolutePath()));
						if (MiscUtils.getFileHeaderMagicNumber(bytes).equalsIgnoreCase("cafebabe"))
						{
							final ClassNode cn = JarUtils.getNode(bytes);
							allDirectoryClasses.put(FilenameUtils.removeExtension(trimmedPath), cn);
						}
					}
					else //pack files into a single container
					{
						allDirectoryFiles.put(trimmedPath, Files.readAllBytes(Paths.get(child.getAbsolutePath())));
					}
				}
				
				finished = true;
			}
		}
		
		container.resourceClasses.putAll(allDirectoryClasses);
		container.resourceFiles = allDirectoryFiles;
		BytecodeViewer.resourceContainers.add(container);
	}
}
