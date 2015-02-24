package the.bytecode.club.bytecodeviewer.compilers;

import java.io.File;
import java.io.IOException;

import me.konloch.kontainer.io.DiskWriter;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.JarUtils;
import the.bytecode.club.bytecodeviewer.MiscUtils;

/**
 * Java Compiler
 * 
 * @author Konloch
 *
 */

public class JavaCompiler extends Compiler {

	@Override
	public byte[] compile(String contents, String name) {
		String fileStart = BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp"+MiscUtils.randomString(12)+BytecodeViewer.fs;
		String fileStart2 = BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp"+MiscUtils.randomString(12)+BytecodeViewer.fs;
		File java = new File(fileStart + BytecodeViewer.fs + name + ".java");
		File clazz = new File(fileStart2 + BytecodeViewer.fs + name + ".class");
		File cp = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + "cpath_"+MiscUtils.randomString(12)+".jar");
		File tempD = new File(fileStart + BytecodeViewer.fs + name.substring(0,name.length() - name.split("/")[name.split("/").length-1].length()));
		tempD.mkdirs();
		
		
		DiskWriter.replaceFile(java.getAbsolutePath(), contents, false);
		JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), cp.getAbsolutePath());

		boolean cont = true;
		try {
			org.codehaus.janino.Compiler.BCV(new String[]{
					"-d", fileStart2,
					"-classpath", cp.getAbsolutePath(),
					java.getAbsolutePath()
			});
		} catch(Exception e) {
			cont = false;
		}

		cp.delete();
		
		if(cont)
			try {
				return org.apache.commons.io.FileUtils.readFileToByteArray(clazz);
			} catch (IOException e) {
				new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
			}
		
		return null;
	}

}
