package the.bytecode.club.bytecodeviewer.resources.importing.impl;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.resources.importing.Importer;
import the.bytecode.club.bytecodeviewer.util.FileContainer;
import the.bytecode.club.bytecodeviewer.util.FileContainerImporter;
import the.bytecode.club.bytecodeviewer.util.JarUtils;

import java.io.File;
import java.io.IOException;

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
		FileContainer container = new FileContainer(file);
		//create the new file importer
		FileContainerImporter importer = new FileContainerImporter(container);
		//import the file as zip into the file container
		importer.importAsZip();
		//add the file container to BCV's total loaded files
		BytecodeViewer.files.add(container);
	}
}
