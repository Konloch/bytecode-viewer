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
import the.bytecode.club.bytecodeviewer.ZipUtils;

/**
 * Krakatau Java Decompiler Wrapper, requires Python 2.7
 * 
 * @author Konloch
 *
 */

public class KrakatauDecompiler extends Decompiler {

	public String quick() {
		if(BytecodeViewer.library.isEmpty())
			return "";
		else
			return ";"+BytecodeViewer.library;
	}
	
	public String decompileClassNode(ClassNode cn, byte[] b) {
		
		if(BytecodeViewer.python.equals("")) {
			BytecodeViewer.showMessage("You need to set your Python (or PyPy for speed) 2.7 executable path.");
			BytecodeViewer.viewer.pythonC();
		}
		if(BytecodeViewer.rt.equals("")) {
			BytecodeViewer.showMessage("You need to set your JRE RT Library.\r\n(C:\\Program Files (x86)\\Java\\jre7\\lib\\rt.jar)");
			BytecodeViewer.viewer.rtC();
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
					BytecodeViewer.krakatauWorkingDirectory + BytecodeViewer.fs + "decompile.py",
					"-skip", //love you storyyeller <3
					"-nauto",
					"-path",
					BytecodeViewer.rt+";"+tempJar.getAbsolutePath()+quick(),
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
			s = DiskReader.loadAsString(tempDirectory.getAbsolutePath() + BytecodeViewer.fs + cn.name + ".java");
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

	public void decompileToZip(String zipName) {
		if(BytecodeViewer.python.equals("")) {
			BytecodeViewer.showMessage("You need to set your Python (or PyPy for speed) 2.7 executable path.");
			BytecodeViewer.viewer.pythonC();
		}
		if(BytecodeViewer.rt.equals("")) {
			BytecodeViewer.showMessage("You need to set your JRE RT Library.\r\n(C:\\Program Files (x86)\\Java\\jre7\\lib\\rt.jar)");
			BytecodeViewer.viewer.rtC();
		}
		
		String ran = MiscUtils.randomString(32);
		final File tempDirectory = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + ran + BytecodeViewer.fs);
		tempDirectory.mkdir();
		final File tempJar = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp.jar");
		JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), tempJar.getAbsolutePath());
		
		BytecodeViewer.sm.blocking = false;
		try {
			ProcessBuilder pb = new ProcessBuilder(
					BytecodeViewer.python,
					"-O", //love you storyyeller <3
					BytecodeViewer.krakatauWorkingDirectory + BytecodeViewer.fs + "decompile.py",
					"-nauto",
					"-path",
					BytecodeViewer.rt+";"+tempJar.getAbsolutePath(),
					"-out",
					tempDirectory.getAbsolutePath(),
					tempJar.getAbsolutePath()
			);

	        Process process = pb.start();
	        
	        //Read out dir output
	        InputStream is = process.getInputStream();
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader br = new BufferedReader(isr);
	        String line;
	        while ((line = br.readLine()) != null) {
	            System.out.println(line);
	        }
	        br.close();
	        
	        is = process.getErrorStream();
	        isr = new InputStreamReader(is);
	        br = new BufferedReader(isr);
	        while ((line = br.readLine()) != null) {
	            System.out.println(line);
	        }
	        br.close();
	        
	        int exitValue = process.waitFor();
	        System.out.println("Exit Value is " + exitValue);
			
	       // ZipUtils.zipDirectory(tempDirectory, new File(zipName));
	        ZipUtils.zipFolder(tempDirectory.getAbsolutePath(), zipName, ran);
	        
			//tempDirectory.delete();
			tempJar.delete();
		} catch(Exception e) {
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
		}
		
		BytecodeViewer.sm.blocking = true;
	}

	public void decompileToClass(String className, String classNameSaved) {
		if(BytecodeViewer.python.equals("")) {
			BytecodeViewer.showMessage("You need to set your Python (or PyPy for speed) 2.7 executable path.");
			BytecodeViewer.viewer.pythonC();
		}
		if(BytecodeViewer.rt.equals("")) {
			BytecodeViewer.showMessage("You need to set your JRE RT Library.\r\n(C:\\Program Files (x86)\\Java\\jre7\\lib\\rt.jar)");
			BytecodeViewer.viewer.rtC();
		}
		
		final File tempDirectory = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + MiscUtils.randomString(32) + BytecodeViewer.fs);
		tempDirectory.mkdir();
		final File tempJar = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp.jar");
		JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), tempJar.getAbsolutePath());
		
		BytecodeViewer.sm.blocking = false;
		try {
			ProcessBuilder pb = new ProcessBuilder(
					BytecodeViewer.python,
					"-O", //love you storyyeller <3
					BytecodeViewer.krakatauWorkingDirectory + BytecodeViewer.fs + "decompile.py",
					"-nauto",
					"-path",
					BytecodeViewer.rt+";"+tempJar.getAbsolutePath(),
					"-out",
					tempDirectory.getAbsolutePath(),
					className+".class"
			);

	        Process process = pb.start();
	        
	        //Read out dir output
	        InputStream is = process.getInputStream();
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader br = new BufferedReader(isr);
	        String line;
	        while ((line = br.readLine()) != null) {
	            System.out.println(line);
	        }
	        br.close();

	        is = process.getErrorStream();
	        isr = new InputStreamReader(is);
	        br = new BufferedReader(isr);
	        while ((line = br.readLine()) != null) {
	            System.out.println(line);
	        }
	        br.close();
	        
	        int exitValue = process.waitFor();
	        System.out.println("Exit Value is " + exitValue);
			
			File f = new File(tempDirectory.getAbsolutePath() + BytecodeViewer.fs + className + ".java");
			f.renameTo(new File(classNameSaved));
			tempDirectory.delete();
			tempJar.delete();
		} catch(Exception e) {
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
		}
	}

}
