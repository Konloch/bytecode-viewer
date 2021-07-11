package the.bytecode.club.bytecodeviewer.resources.importing.impl;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.resources.importing.Importer;
import the.bytecode.club.bytecodeviewer.util.ResourceContainer;
import the.bytecode.club.bytecodeviewer.util.ResourceContainerImporter;

import java.io.File;

/**
 * @author Konloch
 * @since 6/26/2021
 */
public class ZipResourceImporter implements Importer
{
	@Override
	public void open(File file) throws Exception
	{
		//create the new file container
		ResourceContainer container = new ResourceContainer(file);
		//create the new file importer
		ResourceContainerImporter importer = new ResourceContainerImporter(container);
		//import the file as zip into the file container
		importer.importAsZip();
		//add the file container to BCV's total loaded files
		BytecodeViewer.files.add(container);
	}
}
