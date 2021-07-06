package the.bytecode.club.bytecodeviewer.util;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.gui.components.MultipleChoiceDialogue;

import java.io.File;

import static the.bytecode.club.bytecodeviewer.Constants.*;
import static the.bytecode.club.bytecodeviewer.Constants.RT_JAR_DUMPED;

/**
 * @author Konloch
 * @since 7/6/2021
 */
public class BCVResourceUtils
{
	public static void resetWorkspace()
	{
		BytecodeViewer.files.clear();
		LazyNameUtil.reset();
		BytecodeViewer.viewer.resourcePane.resetWorkspace();
		BytecodeViewer.viewer.workPane.resetWorkspace();
		BytecodeViewer.viewer.searchBoxPane.resetWorkspace();
		the.bytecode.club.bytecodeviewer.api.BytecodeViewer.getClassNodeLoader().clear();
	}
	
	/**
	 * Dumps the loaded classes as a library to be used for Krakatau
	 */
	public static File[] dumpTempFile(FileContainer container)
	{
		File[] files = new File[2];
		
		//currently won't optimize if you've got two containers with the same name, will need to add this later
		if (!LazyNameUtil.SAME_NAME_JAR_WORKSPACE)
		{
			if (Configuration.krakatauTempJar != null && !Configuration.krakatauTempJar.exists())
				Configuration.needsReDump = true;
			
			if (Configuration.needsReDump && Configuration.krakatauTempJar != null)
			{
				Configuration.krakatauTempDir = null;
				Configuration.krakatauTempJar = null;
			}
			
			boolean passes = false;
			
			if (BytecodeViewer.viewer.viewPane1.getGroup().isSelected(BytecodeViewer.viewer.viewPane1.getKrakatau().getJava().getModel()))
				passes = true;
			else if (BytecodeViewer.viewer.viewPane1.getGroup().isSelected(BytecodeViewer.viewer.viewPane1.getKrakatau().getBytecode().getModel()))
				passes = true;
			
			else if (BytecodeViewer.viewer.viewPane2.getGroup().isSelected(BytecodeViewer.viewer.viewPane2.getKrakatau().getJava().getModel()))
				passes = true;
			else if (BytecodeViewer.viewer.viewPane2.getGroup().isSelected(BytecodeViewer.viewer.viewPane2.getKrakatau().getBytecode().getModel()))
				passes = true;
			
			else if (BytecodeViewer.viewer.viewPane3.getGroup().isSelected(BytecodeViewer.viewer.viewPane3.getKrakatau().getJava().getModel()))
				passes = true;
			else if (BytecodeViewer.viewer.viewPane3.getGroup().isSelected(BytecodeViewer.viewer.viewPane3.getKrakatau().getBytecode().getModel()))
				passes = true;
			
			if (Configuration.krakatauTempJar != null || !passes)
			{
				files[0] = Configuration.krakatauTempJar;
				files[1] = Configuration.krakatauTempDir;
				return files;
			}
		}
		
		Configuration.currentlyDumping = true;
		Configuration.needsReDump = false;
		Configuration.krakatauTempDir = new File(tempDirectory + fs + MiscUtils.randomString(32) + fs);
		Configuration.krakatauTempDir.mkdir();
		Configuration.krakatauTempJar = new File(tempDirectory + fs + "temp" + MiscUtils.randomString(32) + ".jar");
		//krakatauTempJar = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp" + MiscUtils
		// .randomString(32) + ".jar."+container.name);
		JarUtils.saveAsJarClassesOnly(container.classes, Configuration.krakatauTempJar.getAbsolutePath());
		Configuration.currentlyDumping = false;
		
		files[0] = Configuration.krakatauTempJar;
		files[1] = Configuration.krakatauTempDir;
		return files;
	}
	
	//rt.jar check
	public synchronized static void rtCheck()
	{
		if (Configuration.rt.isEmpty())
		{
			if (RT_JAR.exists())
				Configuration.rt = RT_JAR.getAbsolutePath();
			else if (RT_JAR_DUMPED.exists())
				Configuration.rt = RT_JAR_DUMPED.getAbsolutePath();
			else try {
					JRTExtractor.extractRT(RT_JAR_DUMPED.getAbsolutePath());
					Configuration.rt = RT_JAR_DUMPED.getAbsolutePath();
				} catch (Throwable t) {
					t.printStackTrace();
				}
		}
	}
}
