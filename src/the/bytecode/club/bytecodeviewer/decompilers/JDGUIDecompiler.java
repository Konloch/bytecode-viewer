package the.bytecode.club.bytecodeviewer.decompilers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import jd.cli.loader.DirectoryLoader;
import jd.cli.preferences.CommonPreferences;
import jd.cli.util.ClassFileUtil;
import jd.core.process.DecompilerImpl;
import me.konloch.kontainer.io.DiskReader;

import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.MiscUtils;
import jd.cli.printer.text.PlainTextPrinter;

/**
 * JDCore Decompiler Wrapper
 * 
 * @author Konloch
 * @author JD-Core developers
 * 
 */

public class JDGUIDecompiler extends Decompiler {
	
	@Override
	public String decompileClassNode(ClassNode cn, byte[] b) {
		String exception = "";
		try {
			final File tempDirectory = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + MiscUtils.randomString(32) + BytecodeViewer.fs);
			tempDirectory.mkdir();
			final File tempClass = new File(tempDirectory.getAbsolutePath() + BytecodeViewer.fs + cn.name + ".class");
			final File tempJava = new File(tempDirectory.getAbsolutePath() + BytecodeViewer.fs + cn.name + ".java");

			if(cn.name.contains("/")) {
				String[] raw = cn.name.split("/");
				String path = tempDirectory.getAbsolutePath() + BytecodeViewer.fs;
				for(int i = 0; i < raw.length-1; i++) {
					path += raw[i] + BytecodeViewer.fs;
					File f = new File(path);
					f.mkdir();
				}
			}
			
			try {
				final FileOutputStream fos = new FileOutputStream(tempClass);

				fos.write(b);

				fos.close();
			} catch (final IOException e) {
				new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
			}
			
			
			String pathToClass = tempClass.getAbsolutePath().replace('/', File.separatorChar).replace('\\', File.separatorChar);			
			String directoryPath = ClassFileUtil.ExtractDirectoryPath(pathToClass);
			
			String internalPath = ClassFileUtil.ExtractInternalPath(directoryPath, pathToClass);
			
			CommonPreferences preferences = new CommonPreferences() {
				@Override
				public boolean isShowLineNumbers() {
					return false;
				}
				@Override
				public boolean isMergeEmptyLines() {
					return true;
				}
			};
			
			DirectoryLoader loader = new DirectoryLoader(new File(directoryPath));
			
			//PrintStream ps = new PrintStream("test.html");
			//HtmlPrinter printer = new HtmlPrinter(ps);
			PrintStream ps = new PrintStream(tempJava.getAbsolutePath());
			PlainTextPrinter printer = new PlainTextPrinter(preferences, ps);
		
			jd.core.Decompiler decompiler = new DecompilerImpl();		
			decompiler.decompile(preferences, loader, printer, internalPath);
			
            String decompiledSource = "Error with decompilation.";
			decompiledSource = DiskReader.loadAsString(tempJava.getAbsolutePath());

			return decompiledSource;
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			e.printStackTrace();

			exception = "Bytecode Viewer Version: " + BytecodeViewer.version + BytecodeViewer.nl + BytecodeViewer.nl + sw.toString();
		}
		return "JD-GUI error! Send the stacktrace to Konloch at http://the.bytecode.club or konloch@gmail.com"+BytecodeViewer.nl+BytecodeViewer.nl+"Suggested Fix: Click refresh class, if it fails again try another decompiler."+BytecodeViewer.nl+BytecodeViewer.nl+exception;
	}

	@Override
	public void decompileToZip(String zipName) {
	}
}