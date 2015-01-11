package the.bytecode.club.bytecodeviewer;

import java.io.File;

/**
 * A simple 'wrapper' for Dex2Jar.
 * 
 * @author Konloch
 *
 */

public class Dex2Jar {

	public static synchronized void dex2Jar(File input, File output) {
		try {
			com.googlecode.dex2jar.tools.Dex2jarCmd.main(new String[]{"--force", input.getAbsolutePath()});
			String realOutput =  input.getName().replaceAll(".dex", "-dex2jar.jar").replaceAll(".apk", "-dex2jar.jar");
			File realOutputF = new File(realOutput);
			realOutputF.renameTo(output);
			File realOutputF2 = new File(realOutput);
			while(realOutputF2.exists())
				realOutputF2.delete();
		} catch(Exception e) {
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
		}
	}
	
	public static synchronized void saveAsDex(File input, File output) {
		try {
			com.googlecode.dex2jar.tools.Jar2Dex.main(new String[]{"--force", input.getAbsolutePath()});
			String realOutput =  input.getName().replaceAll(".jar", "-jar2dex.dex");
			File realOutputF = new File(realOutput);
			realOutputF.renameTo(output);
			File realOutputF2 = new File(realOutput);
			while(realOutputF2.exists())
				realOutputF2.delete();
		} catch(Exception e) {
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
		}
	}
}
