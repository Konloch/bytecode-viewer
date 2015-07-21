package the.bytecode.club.bytecodeviewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A simple wrapper for Enjarify.
 * 
 * @author Konloch
 *
 */

public class Enjarify {

	/**
	 * Converts a .apk or .dex to .jar
	 * @param input the input .apk or .dex file
	 * @param output the output .jar file
	 */
	public static synchronized void apk2Jar(File input, File output) {
		if(BytecodeViewer.python3.equals("")) {
			BytecodeViewer.showMessage("You need to set your Python (or PyPy for speed) 3.x executable path.");
			BytecodeViewer.viewer.pythonC3();
		}
		
		BytecodeViewer.sm.stopBlocking();
		try {
			ProcessBuilder pb = new ProcessBuilder(
					BytecodeViewer.python3,
					"-O",
					"-m",
					"enjarify.main",
					input.getAbsolutePath(),
					"-o",
					output.getAbsolutePath()
			);

			pb.directory(new File(BytecodeViewer.enjarifyWorkingDirectory));
			
	        Process process = pb.start();
	        BytecodeViewer.createdProcesses.add(process);
	        
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
			
		} catch(Exception e) {
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
		}
		
		BytecodeViewer.sm.setBlocking();
	}
}
