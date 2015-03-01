package the.bytecode.club.bytecodeviewer.decompilers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import me.konloch.kontainer.io.DiskReader;
import me.konloch.kontainer.io.DiskWriter;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.JarUtils;
import the.bytecode.club.bytecodeviewer.MiscUtils;

/**
 * A FernFlower wrapper with all the options (except 2)
 * 
 * @author Konloch
 * @author WaterWolf
 * 
 */

public class FernFlowerDecompiler extends Decompiler {

	@Override
	public void decompileToClass(String className, String classNameSaved) {
		ClassNode cn = BytecodeViewer.getClassNode(className);
		final ClassWriter cw = new ClassWriter(0);
		try {
			cn.accept(cw);
		} catch(Exception e) {
			e.printStackTrace();
			try {
				Thread.sleep(200);
				cn.accept(cw);
			} catch (InterruptedException e1) { }
		}
		String contents = decompileClassNode(cn, cw.toByteArray());
		DiskWriter.replaceFile(classNameSaved, contents, false);
	}

	@Override
	public void decompileToZip(String zipName) {
		File tempZip = new File(BytecodeViewer.tempDirectory + "temp.zip");
		if (tempZip.exists())
			tempZip.delete();

		File f = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs
				+ "temp" + BytecodeViewer.fs);
		f.mkdir();

		JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(),
				tempZip.getAbsolutePath());

		org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler
				.main(generateMainMethod(tempZip.getAbsolutePath(),
						BytecodeViewer.tempDirectory + "./temp/"));
		File tempZip2 = new File(BytecodeViewer.tempDirectory
				+ BytecodeViewer.fs + "temp" + BytecodeViewer.fs
				+ tempZip.getName());
		if (tempZip2.exists())
			tempZip2.renameTo(new File(zipName));

		tempZip.delete();
		new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp")
				.delete();
	}

	@Override
	public String decompileClassNode(final ClassNode cn, byte[] b) {
		String start = MiscUtils.getUniqueName("", ".class");
		
		final File tempClass = new File(start + ".class");

		String exception = "";
		try {
			final FileOutputStream fos = new FileOutputStream(tempClass);

			fos.write(b);

			fos.close();
		} catch (final IOException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			e.printStackTrace();

			exception = "Bytecode Viewer Version: " + BytecodeViewer.version + BytecodeViewer.nl + BytecodeViewer.nl + sw.toString();
		}

		org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler
				.main(generateMainMethod(tempClass.getAbsolutePath(), "."));

		tempClass.delete();

		final File outputJava = new File(start + ".java");
		if (outputJava.exists()) {
			String s;
			try {
				s = DiskReader.loadAsString(outputJava.getAbsolutePath());

				outputJava.delete();

				return s;
			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				e.printStackTrace();

				exception += BytecodeViewer.nl + BytecodeViewer.nl + sw.toString();
			}
		}
		return "FernFlower error! Send the stacktrace to Konloch at http://the.bytecode.club or konloch@gmail.com"+BytecodeViewer.nl+BytecodeViewer.nl+"Suggested Fix: Click refresh class, if it fails again try another decompiler."+BytecodeViewer.nl+BytecodeViewer.nl+exception;
	}

	private String[] generateMainMethod(String className, String folder) {
		return new String[] {
				"-rbr=" + r(BytecodeViewer.viewer.rbr.isSelected()),
				"-rsy=" + r(BytecodeViewer.viewer.rsy.isSelected()),
				"-din=" + r(BytecodeViewer.viewer.din.isSelected()),
				"-dc4=" + r(BytecodeViewer.viewer.dc4.isSelected()),
				"-das=" + r(BytecodeViewer.viewer.das.isSelected()),
				"-hes=" + r(BytecodeViewer.viewer.hes.isSelected()),
				"-hdc=" + r(BytecodeViewer.viewer.hdc.isSelected()),
				"-dgs=" + r(BytecodeViewer.viewer.dgs.isSelected()),
				"-ner=" + r(BytecodeViewer.viewer.ner.isSelected()),
				"-den=" + r(BytecodeViewer.viewer.den.isSelected()),
				"-rgn=" + r(BytecodeViewer.viewer.rgn.isSelected()),
				"-bto=" + r(BytecodeViewer.viewer.bto.isSelected()),
				"-nns=" + r(BytecodeViewer.viewer.nns.isSelected()),
				"-uto=" + r(BytecodeViewer.viewer.uto.isSelected()),
				"-udv=" + r(BytecodeViewer.viewer.udv.isSelected()),
				"-rer=" + r(BytecodeViewer.viewer.rer.isSelected()),
				"-fdi=" + r(BytecodeViewer.viewer.fdi.isSelected()),
				"-asc=" + r(BytecodeViewer.viewer.asc.isSelected()), className,
				folder };
	}

	private String r(boolean b) {
		if (b) {
			return "1";
		} else {
			return "0";
		}
	}

}
