package the.bytecode.club.bytecodeviewer.resources.importing.impl;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.resources.importing.Importer;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainerImporter;

import java.io.File;

/**
 * @author Konloch
 * @since 6/26/2021
 */
public class FileResourceImporter implements Importer
{
	@Override
	public void open(File file) throws Exception
	{
		//create the new resource container
		ResourceContainer container = new ResourceContainer(file);
		//create the new file importer
		ResourceContainerImporter importer = new ResourceContainerImporter(container);
		//import the file into the resource container
		importer.importAsFile();
		//add the resource container to BCV's total loaded files
		BytecodeViewer.resourceContainers.add(container);
	}
}
