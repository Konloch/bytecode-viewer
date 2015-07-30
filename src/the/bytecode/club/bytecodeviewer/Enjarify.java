package the.bytecode.club.bytecodeviewer;

import java.io.File;

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
		
		if(BytecodeViewer.python3.equals("")) {
			BytecodeViewer.showMessage("You need to set Python!");
			return;
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
	        process.waitFor();
	        
		} catch(Exception e) {
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
		} finally {
			BytecodeViewer.sm.setBlocking();
		}
	}
}
