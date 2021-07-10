package the.bytecode.club.bytecodeviewer.resources.importing.impl;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.resources.importing.Importer;
import the.bytecode.club.bytecodeviewer.util.FileContainer;
import the.bytecode.club.bytecodeviewer.util.FileContainerImporter;
import the.bytecode.club.bytecodeviewer.util.JarUtils;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

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
	public void open(File file) throws Exception
	{
		//create the new file container
		FileContainer container = new FileContainer(file);
		//create the new file importer
		FileContainerImporter importer = new FileContainerImporter(container);
		//import the file into the file container
		importer.importAsFile();
		//add the file container to BCV's total loaded files
		BytecodeViewer.files.add(container);
	}
}
