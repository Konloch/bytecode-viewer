package the.bytecode.club.bytecodeviewer.resources.importing.impl;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.resources.importing.Importer;
import the.bytecode.club.bytecodeviewer.util.FileContainer;
import the.bytecode.club.bytecodeviewer.util.JarUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

/**
 * @author Konloch
 * @since 6/26/2021
 */
public class FileResourceImporter implements Importer
{
	@Override
	public boolean open(File file) throws Exception
	{
		HashMap<String, byte[]> files1 = new HashMap<>();
		byte[] bytes = JarUtils.getBytes(new FileInputStream(file));
		files1.put(file.getName(), bytes);
		
		
		FileContainer container = new FileContainer(file);
		container.files = files1;
		BytecodeViewer.files.add(container);
		return true;
	}
}
