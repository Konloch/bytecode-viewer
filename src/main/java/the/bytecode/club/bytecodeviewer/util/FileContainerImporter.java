package the.bytecode.club.bytecodeviewer.util;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FilenameUtils;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.api.ASMUtil;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Konloch
 * @since 7/10/2021
 */
public class FileContainerImporter
{
	private final FileContainer container;
	
	public FileContainerImporter(FileContainer container)
	{
		this.container = container;
	}
	
	public void importAsFile() throws IOException
	{
		addUnknownFile(container.file.getName(), new FileInputStream(container.file), false);
	}
	
	public void importAsZip() throws IOException
	{
		container.resourceClasses.clear();
		container.resourceClassBytes.clear();
		container.resourceFiles.clear();
		
		try
		{
			//attempt to import using Java ZipInputStream
			importZipInputStream(false);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			
			//fallback to apache commons ZipFile
			importApacheZipFile(false);
		}
	}
	
	//sorts the file type from classes or resources
	public void addUnknownFile(String name, InputStream stream, boolean classesOnly) throws IOException
	{
		//TODO remove this .class check and just look for cafebabe
		if (name.endsWith(".class"))
			addClassResource(name, stream);
		else if(!classesOnly)
			addResource(name, stream);
	}
	
	public void addClassResource(String name, InputStream stream) throws IOException
	{
		byte[] bytes = MiscUtils.getBytes(stream);
		if (MiscUtils.getFileHeader(bytes).equalsIgnoreCase("cafebabe"))
		{
			try
			{
				final ClassNode cn = ASMUtil.bytesToNode(bytes);
				
				//classes are copied into memory twice
				ClassNode existingNode = container.resourceClasses.put(FilenameUtils.removeExtension(name), cn);
				container.resourceClassBytes.put(name, bytes);
				if( existingNode != null)
				{
					//TODO prompt to ask the user if they would like to overwrite the resource conflict
					// or solve it automatically by creating a new file container for each conflict
					
					System.err.println("WARNING: Resource Conflict: " + name);
					System.err.println("Suggested Fix: Contact Konloch to add support for resource conflicts");
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		} else {
			System.err.println(container.file + ">" + name + ": Header does not start with CAFEBABE, ignoring.");
		}
	}
	
	public void addResource(String name, InputStream stream) throws IOException
	{
		byte[] bytes = MiscUtils.getBytes(stream);
		container.resourceFiles.put(name, bytes);
	}
	
	public void importZipInputStream(boolean classesOnly) throws IOException
	{
		ZipInputStream jis = new ZipInputStream(new FileInputStream(container.file));
		ZipEntry entry;
		while ((entry = jis.getNextEntry()) != null)
		{
			final String name = entry.getName();
			
			//skip directories
			if(entry.isDirectory())
				continue;
			
			addUnknownFile(name, jis, classesOnly);
			jis.closeEntry();
		}
		jis.close();
	}
	
	//TODO if this ever fails: import Sun's jarsigner code from JDK 7, re-sign the jar to rebuild the CRC,
	// should also rebuild the archive byte offsets
	public void importApacheZipFile(boolean classesOnly) throws IOException
	{
		try (ZipFile zipFile = new ZipFile(container.file))
		{
			Enumeration<? extends ZipArchiveEntry> entries = zipFile.getEntries();
			while (entries.hasMoreElements())
			{
				ZipArchiveEntry entry = entries.nextElement();
				String name = entry.getName();
				
				if(entry.isDirectory())
					continue;
				
				try (InputStream in = zipFile.getInputStream(entry))
				{
					addUnknownFile(name, in, classesOnly);
				}
			}
		}
	}
}
