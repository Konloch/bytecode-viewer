package the.bytecode.club.bytecodeviewer.decompilers;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import me.konloch.kontainer.io.DiskReader;

import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.JarUtils;
import the.bytecode.club.bytecodeviewer.MiscUtils;

/**
 * Krakatau Java Disassembler Wrapper, requires Python 2.7
 * 
 * @author Konloch
 *
 */

public class KrakatauDisassembler extends Decompiler {

	public String decompileClassNode(ClassNode cn, byte[] b) {
		if(BytecodeViewer.python.equals("")) {
			BytecodeViewer.showMessage("You need to set your Python (or PyPy for speed) 2.7 executable path.");
			BytecodeViewer.viewer.pythonC();
		}
		String s = "Bytecode Viewer Version: " + BytecodeViewer.version + BytecodeViewer.nl + BytecodeViewer.nl + "Please send this to konloch@gmail.com. " + BytecodeViewer.nl + BytecodeViewer.nl;
		
		final File tempDirectory = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + MiscUtils.randomString(32) + BytecodeViewer.fs);
		tempDirectory.mkdir();
		final File tempJar = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp"+MiscUtils.randomString(32)+".jar");
		JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), tempJar.getAbsolutePath());
		
		BytecodeViewer.sm.blocking = false;
		try {
			ProcessBuilder pb = new ProcessBuilder(
					BytecodeViewer.python,
					"-O", //love you storyyeller <3
					BytecodeViewer.krakatauWorkingDirectory + BytecodeViewer.fs + "disassemble.py",
					"-path",
					tempJar.getAbsolutePath(),
					"-out",
					tempDirectory.getAbsolutePath(),
					cn.name+".class"
			);

	        Process process = pb.start();
	        BytecodeViewer.krakatau.add(process);
	        
	        //Read out dir output
	        InputStream is = process.getInputStream();
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader br = new BufferedReader(isr);
	        String log = "Process:"+BytecodeViewer.nl+BytecodeViewer.nl;
	        String line;
	        while ((line = br.readLine()) != null) {
	            log += BytecodeViewer.nl + line;
	        }
	        br.close();

	        log += BytecodeViewer.nl+BytecodeViewer.nl+"Error:"+BytecodeViewer.nl+BytecodeViewer.nl;
	        is = process.getErrorStream();
	        isr = new InputStreamReader(is);
	        br = new BufferedReader(isr);
	        while ((line = br.readLine()) != null) {
	            log += BytecodeViewer.nl + line;
	        }
	        br.close();
	        
	        int exitValue = process.waitFor();
	        log += BytecodeViewer.nl+BytecodeViewer.nl+"Exit Value is " + exitValue;
			s = log;
	        
			//if the motherfucker failed this'll fail, aka wont set.
			s = DiskReader.loadAsString(tempDirectory.getAbsolutePath() + BytecodeViewer.fs + cn.name + ".j");
			tempDirectory.delete();
			tempJar.delete();
		} catch(Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			e.printStackTrace();
			s += BytecodeViewer.nl+"Bytecode Viewer Version: " + BytecodeViewer.version + BytecodeViewer.nl + BytecodeViewer.nl + sw.toString();
		}
		
		BytecodeViewer.sm.blocking = true;
		
		return s;
	}

	@Override public void decompileToZip(String zipName) { }
	@Override public void decompileToClass(String className, String classNameSaved) { }
}
