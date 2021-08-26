package the.bytecode.club.bytecodeviewer.resources;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FilenameUtils;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.api.ASMUtil;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 *                                                                         *
 * This program is free software: you can redistribute it and/or modify    *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

/**
 * @author Konloch
 * @since 7/10/2021
 */
public class ResourceContainerImporter
{
	private final ResourceContainer container;
	
	public ResourceContainerImporter(ResourceContainer container)
	{
		this.container = container;
	}
	
	/**
	 * Return the linked container
	 */
	public ResourceContainer getContainer()
	{
		return container;
	}
	
	/**
	 * Start importing the container file as a file
	 */
	public ResourceContainerImporter importAsFile() throws IOException
	{
		try (FileInputStream fis = new FileInputStream(container.file)) {
			return addUnknownFile(container.file.getName(), fis, false);
		}
	}
	
	/**
	 * Start importing the container file as a zip archive
	 */
	public ResourceContainerImporter importAsZip() throws IOException
	{
		container.resourceClasses.clear();
		container.resourceClassBytes.clear();
		container.resourceFiles.clear();
		
		try
		{
			//attempt to import using Java ZipInputStream
			return importZipInputStream(false);
		}
		catch (Throwable t)
		{
			try {
				//fallback to apache commons ZipFile
				return importApacheZipFile(false);
			} catch (Throwable t1) {
				t1.addSuppressed(t);
				throw t1;
			}
		}
	}
	
	/**
	 * Adds an unknown resource to the container
	 * This will sort the file and start the file-specific adding process
	 */
	public ResourceContainerImporter addUnknownFile(String name, InputStream stream, boolean classesOnly) throws IOException
	{
		//TODO remove this .class check and just look for cafebabe
		if (name.endsWith(".class"))
			return addClassResource(name, stream);
		else if (!classesOnly)
			return addResource(name, stream);

		return this;
	}
	
	/**
	 * Adds a class resource to the container
	 */
	public ResourceContainerImporter addClassResource(String name, InputStream stream) throws IOException
	{
		byte[] bytes = MiscUtils.getBytes(stream);
		if (MiscUtils.getFileHeaderMagicNumber(bytes).equalsIgnoreCase("cafebabe"))
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
					// or solve it automatically by creating a new resource container for each conflict (means no editing)
					
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
		
		return this;
	}
	
	/**
	 * Adds a file resource to the container
	 */
	public ResourceContainerImporter addResource(String name, InputStream stream) throws IOException
	{
		byte[] bytes = MiscUtils.getBytes(stream);
		container.resourceFiles.put(name, bytes);
		return this;
	}
	
	/**
	 * Imports resources from zip archives using ZipInputStream
	 */
	private ResourceContainerImporter importZipInputStream(boolean classesOnly) throws IOException
	{
		try (ZipInputStream jis = new ZipInputStream(new FileInputStream(container.file))) {
			ZipEntry entry;
			while ((entry = jis.getNextEntry()) != null) {
				final String name = entry.getName();

				//skip directories
				if (entry.isDirectory())
					continue;

				addUnknownFile(name, jis, classesOnly);
				jis.closeEntry();
			}

			return this;
		}
	}
	
	/**
	 * Imports resources from zip archives using Apache ZipFile
	 *
	 * TODO if this ever fails: import Sun's jarsigner code from JDK 7, re-sign the jar to rebuild the CRC,
	 *      should also rebuild the archive byte offsets
	 */
	private ResourceContainerImporter importApacheZipFile(boolean classesOnly) throws IOException
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
		
		return this;
	}
}
