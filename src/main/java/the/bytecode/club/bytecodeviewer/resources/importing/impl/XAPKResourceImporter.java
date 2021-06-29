package the.bytecode.club.bytecodeviewer.resources.importing.impl;

import me.konloch.kontainer.io.DiskWriter;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.resources.importing.Import;
import the.bytecode.club.bytecodeviewer.resources.importing.Importer;
import the.bytecode.club.bytecodeviewer.util.FileContainer;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;

import static the.bytecode.club.bytecodeviewer.Constants.fs;
import static the.bytecode.club.bytecodeviewer.Constants.tempDirectory;

/**
 * Compressed APKs (XAPK)
 *
 * @author Konloch
 * @since 6/26/2021
 */
public class XAPKResourceImporter implements Importer
{
	@Override
	public boolean open(File file) throws Exception
	{
		FileContainer container = new FileContainer(file);
		HashMap<String, byte[]> allDirectoryFiles = new HashMap<>();
		HashMap<String, ClassNode> allDirectoryClasses = new HashMap<>();
		
		Configuration.silenceExceptionGUI++; //turn exceptions off
		try (java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(file))
		{
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements())
			{
				final ZipEntry entry = entries.nextElement();
				final String fileName = entry.getName();
				
				if(entry.isDirectory())
					continue;
				
				if (fileName.endsWith(".apk"))
				{
					File tempFile = new File(tempDirectory + fs + "temp" + MiscUtils.randomString(32) + fs + entry);
					tempFile.getParentFile().mkdirs();
					
					try (InputStream in = zipFile.getInputStream(entry);
					     OutputStream out = new FileOutputStream(tempFile))
					{
						IOUtils.copy(in, out);
					}
					Import.APK.getImporter().open(tempFile);
				}
				else
				{
					//pack files into a single container
					byte[] bytes;
					try (InputStream in = zipFile.getInputStream(entry))
					{
						bytes = IOUtils.toByteArray(in);
					}
					allDirectoryFiles.put(fileName, bytes);
				}
			}
		}
		Configuration.silenceExceptionGUI--; //turn exceptions back on
		
		container.classes.addAll(allDirectoryClasses.values());
		container.files = allDirectoryFiles;
		BytecodeViewer.files.add(container);
		return true;
	}
	
	public File exportTo(File original, String extension, byte[] bytes)
	{
		File file = new File(original.getAbsolutePath() + extension);
		DiskWriter.replaceFile(file.getAbsolutePath(), bytes, false);
		return file;
	}
}
