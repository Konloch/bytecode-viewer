package the.bytecode.club.bytecodeviewer.compilers;

import java.io.File;

import me.konloch.kontainer.io.DiskWriter;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Dex2Jar;
import the.bytecode.club.bytecodeviewer.MiscUtils;
import the.bytecode.club.bytecodeviewer.ZipUtils;

/**
 * Smali Assembler Wrapper for Java
 * 
 * @author Konloch
 *
 */

public class SmaliAssembler extends Compiler {

	@Override
	public byte[] compile(String contents, String name) {
		String fileStart = BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp";
		int fileNumber = MiscUtils.getClassNumber(fileStart, ".dex");

		final File tempSmaliFolder = new File(fileStart + fileNumber + "-smalifolder"+BytecodeViewer.fs);
		tempSmaliFolder.mkdir();
		
		File tempSmali = new File(tempSmaliFolder.getAbsolutePath() +BytecodeViewer.fs + fileNumber + ".smali");
		File tempDex = new File(fileStart + fileNumber + ".dex");
		File tempJar = new File(fileStart + fileNumber + ".jar");
		File tempJarFolder = new File(fileStart + fileNumber + "-jar"+BytecodeViewer.fs);
		
		try {
			DiskWriter.replaceFile(tempSmali.getAbsolutePath(), contents, false);
		} catch (final Exception e) {
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
		}
		
		try {
			org.jf.smali.main.main(new String[]{tempSmaliFolder.getAbsolutePath(), "-o", tempDex.getAbsolutePath()});
		} catch (Exception e) {
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
		}
		

		Dex2Jar.dex2Jar(tempDex, tempJar);
		
		try {
			ZipUtils.unzipFilesToPath(tempJar.getAbsolutePath(), tempJarFolder.getAbsolutePath());
			
			File outputClass = null;
			boolean found = false;
			File current = tempJarFolder;
			try {
				while(!found) {
					File f = current.listFiles()[0];
					if(f.isDirectory())
						current = f;
					else {
						outputClass = f;
						found = true;
					}
						
				}
				
				return org.apache.commons.io.FileUtils.readFileToByteArray(outputClass);
			} catch (java.lang.NullPointerException e) {
				
			}
		} catch (Exception e) {
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
		}
		
		return null;
	}
	
}
