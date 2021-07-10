package the.bytecode.club.bytecodeviewer.resources.importing.impl;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.resources.importing.Importer;
import the.bytecode.club.bytecodeviewer.util.*;

import java.io.File;
import java.util.ArrayList;

import static the.bytecode.club.bytecodeviewer.Constants.fs;
import static the.bytecode.club.bytecodeviewer.Constants.tempDirectory;

/**
 * @author Konloch
 * @since 6/26/2021
 */
public class DEXResourceImporter implements Importer
{
	@Override
	public void open(File file) throws Exception
	{
		BytecodeViewer.updateBusyStatus(true);
		
		File tempCopy = new File(tempDirectory + fs + MiscUtils.randomString(32) + ".dex");
		
		FileUtils.copyFile(file, tempCopy); //copy and rename to prevent unicode filenames
		
		FileContainer container = new FileContainer(tempCopy, file.getName());
		
		String name = MiscUtils.getRandomizedName() + ".jar";
		File output = new File(tempDirectory + fs + name);
		
		if (BytecodeViewer.viewer.apkConversionGroup.isSelected(BytecodeViewer.viewer.apkConversionDex.getModel()))
			Dex2Jar.dex2Jar(tempCopy, output);
		else if (BytecodeViewer.viewer.apkConversionGroup.isSelected(BytecodeViewer.viewer.apkConversionEnjarify.getModel()))
			Enjarify.apk2Jar(tempCopy, output);
		
		//TODO update to FileContainerImporter
		ArrayList<ClassNode> nodeList = JarUtils.loadClasses(output);
		for(ClassNode cn : nodeList)
			container.resourceClasses.put(cn.name, cn);
		
		BytecodeViewer.updateBusyStatus(false);
		BytecodeViewer.files.add(container);
	}
}
