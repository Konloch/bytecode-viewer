package the.bytecode.club.bytecodeviewer.resources.importing.impl;

import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.resources.importing.Importer;
import the.bytecode.club.bytecodeviewer.util.FileContainer;
import the.bytecode.club.bytecodeviewer.util.JarUtils;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author Konloch
 * @since 6/26/2021
 */
public class ClassResourceImporter implements Importer
{
	@Override
	public boolean open(File file) throws Exception
	{
		final String fn = file.getName();
		try
		{
			byte[] bytes = JarUtils.getBytes(new FileInputStream(file));
			String cafebabe = String.format("%02X", bytes[0])
					+ String.format("%02X", bytes[1])
					+ String.format("%02X", bytes[2])
					+ String.format("%02X", bytes[3]);
			
			if (cafebabe.equalsIgnoreCase("cafebabe"))
			{
				final ClassNode cn = JarUtils.getNode(bytes);
				
				FileContainer container = new FileContainer(file);
				container.classes.add(cn);
				BytecodeViewer.files.add(container);
			}
			else
			{
				BytecodeViewer.showMessage(fn + ": Header does not start with CAFEBABE, ignoring.");
				return false;
			}
		}
		catch (final Exception e)
		{
			new ExceptionUI(e);
			return false;
		}
		
		return true;
	}
}
