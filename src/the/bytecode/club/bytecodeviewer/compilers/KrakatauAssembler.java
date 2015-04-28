package the.bytecode.club.bytecodeviewer.compilers;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import me.konloch.kontainer.io.DiskWriter;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.JarUtils;
import the.bytecode.club.bytecodeviewer.MiscUtils;

/**
 * Krakatau Java assembler, requires Python 2.7
 * 
 * @author Konloch
 *
 */
public class KrakatauAssembler extends Compiler {

	@Override
	public byte[] compile(String contents, String name) {
		if(BytecodeViewer.python.equals("")) {
			BytecodeViewer.showMessage("You need to set your Python (or PyPy for speed) 2.7 executable path.");
			BytecodeViewer.viewer.pythonC();
		}

		String origName = name;
		name = MiscUtils.randomString(20);

		System.out.println("run");
		File tempD = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + MiscUtils.randomString(32) + BytecodeViewer.fs);
		tempD.mkdir();
		
		File tempJ = new File(tempD.getAbsolutePath() + BytecodeViewer.fs+name+".j");
		DiskWriter.replaceFile(tempJ.getAbsolutePath(), contents, true);
		System.out.println("ran");
		
		final File tempDirectory = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + MiscUtils.randomString(32) + BytecodeViewer.fs);
		tempDirectory.mkdir();
		final File tempJar = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp"+MiscUtils.randomString(32)+".jar");
		JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), tempJar.getAbsolutePath());
		
		BytecodeViewer.sm.blocking = false;
        String log = "";
		try {
			ProcessBuilder pb = new ProcessBuilder(
					BytecodeViewer.python,
					"-O", //love you storyyeller <3
					BytecodeViewer.krakatauWorkingDirectory + BytecodeViewer.fs + "assemble.py",
					"-out",
					tempDirectory.getAbsolutePath(),
					tempJ.getAbsolutePath()
			);

	        Process process = pb.start();
	        BytecodeViewer.krakatau.add(process);
	        
	        //Read out dir output
	        InputStream is = process.getInputStream();
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader br = new BufferedReader(isr);
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
	        System.out.println(log);

			byte[] b = org.apache.commons.io.FileUtils.readFileToByteArray(new File(tempDirectory.getAbsolutePath() + BytecodeViewer.fs + origName + ".class"));
			tempDirectory.delete();
			tempJar.delete();
			return b;
		} catch(Exception e) {
			e.printStackTrace();
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(log);
		}
		
		BytecodeViewer.sm.blocking = true;
		
		return null;
	}
	
}
