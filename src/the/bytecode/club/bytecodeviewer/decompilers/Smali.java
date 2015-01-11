package the.bytecode.club.bytecodeviewer.decompilers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import me.konloch.kontainer.io.DiskReader;
import me.konloch.kontainer.io.DiskWriter;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Dex2Jar;
import the.bytecode.club.bytecodeviewer.ZipUtils;

public class Smali {
	
	public static String decompileClassNode(ClassNode cn) {
		final ClassWriter cw = new ClassWriter(0);
		cn.accept(cw);

		String fileStart = BytecodeViewer.tempDirectory + BytecodeViewer.fs
				+ "temp";
		
		String start = JavaDecompiler.getUniqueName(fileStart, ".class");

		final File tempClass = new File(start + ".class");
		final File tempZip = new File(start + ".jar");
		final File tempDex = new File(start + ".dex");
		final File tempSmali = new File(start + "-smali"); //output directory
		
		try {
			final FileOutputStream fos = new FileOutputStream(tempClass);

			fos.write(cw.toByteArray());

			fos.close();
		} catch (final IOException e) {
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
		}
		
		ZipUtils.zipFile(tempClass, tempZip);
		Dex2Jar.saveAsDex(tempZip, tempDex);
		try {
			org.jf.baksmali.main.main(new String[]{"-o", tempSmali.getAbsolutePath(), "-x", tempDex.getAbsolutePath()});
		} catch (Exception e) {
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
		}
		
		File outputSmali = null;
		
		boolean found = false;
		File current = tempSmali;
		while(!found) {
			File f = current.listFiles()[0];
			if(f.isDirectory())
				current = f;
			else {
				outputSmali = f;
				found = true;
			}
				
		}
		try {
			return DiskReader.loadAsString(outputSmali.getAbsolutePath());
		} catch (Exception e) {
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
		}
		
		return null;
	}
	
	public static byte[] compile(String contents) {
		String fileStart = BytecodeViewer.tempDirectory + BytecodeViewer.fs
				+ "temp";
		int fileNumber = JavaDecompiler.getClassNumber(fileStart, ".dex");

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
