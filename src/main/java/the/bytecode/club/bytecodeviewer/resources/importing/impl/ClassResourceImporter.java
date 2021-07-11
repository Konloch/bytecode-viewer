package the.bytecode.club.bytecodeviewer.resources.importing.impl;

import org.apache.commons.io.FilenameUtils;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.resources.importing.Importer;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.util.JarUtils;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author Konloch
 * @since 6/26/2021
 */
public class ClassResourceImporter implements Importer
{
	@Override
	public void open(File file) throws Exception
	{
		final String name = file.getName();
		byte[] bytes = MiscUtils.getBytes(new FileInputStream(file));
		ResourceContainer container = new ResourceContainer(file);
		
		if (MiscUtils.getFileHeader(bytes).equalsIgnoreCase("cafebabe"))
		{
			final ClassNode cn = JarUtils.getNode(bytes);
			
			container.resourceClasses.put(FilenameUtils.removeExtension(name), cn);
			container.resourceClassBytes.put(name, bytes);
		}
		else
		{
			BytecodeViewer.showMessage(name + "\nHeader does not start with CAFEBABE\nimporting as resource instead.");
			
			//TODO double check this
			container.resourceFiles.put(name, bytes);
		}
		BytecodeViewer.files.add(container);
	}
}
