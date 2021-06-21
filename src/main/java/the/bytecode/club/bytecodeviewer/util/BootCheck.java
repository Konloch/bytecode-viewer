package the.bytecode.club.bytecodeviewer.util;

import the.bytecode.club.bootloader.Boot;
import the.bytecode.club.bootloader.ILoader;
import the.bytecode.club.bootloader.resource.EmptyExternalResource;
import the.bytecode.club.bootloader.resource.ExternalResource;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.CommandLineInput;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static the.bytecode.club.bytecodeviewer.Constants.nl;

/**
 * @author Konloch
 * @since 6/21/2021
 */
public class BootCheck implements Runnable
{
	boolean finished = false;
	
	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void run() {
		long start = System.currentTimeMillis();
		
		while (!finished) {
			if (System.currentTimeMillis() - start >= 7000) { //7 second failsafe
				if (!Boot.completedboot && !Boot.downloading) {
					File libsDir = Boot.libsDir();
					File[] listFiles = libsDir.listFiles();
					if (listFiles == null || listFiles.length <= 0) {
						BytecodeViewer.showMessage(
								"Github is loading extremely slow, BCV needs to download libraries from github in"
										+ " order" + nl +
										"to work, please try adjusting your network settings or manually "
										+ "downloading these libraries" + nl +
										"if this error persists.");
						finished = true;
						return;
					}
					
					Boot.setState("Bytecode Viewer Boot Screen (OFFLINE MODE) - Unable to connect to github, "
							+ "force booting...");
					System.out.println("Unable to connect to github, force booting...");
					List<String> libsFileList = new ArrayList<>();
					for (File f : listFiles) {
						libsFileList.add(f.getAbsolutePath());
					}
					
					ILoader<?> loader = Boot.findLoader();
					
					for (String s : libsFileList) {
						if (s.endsWith(".jar")) {
							File f = new File(s);
							if (f.exists()) {
								Boot.setState("Bytecode Viewer Boot Screen (OFFLINE MODE) - Force Loading Library"
										+ " " + f.getName());
								System.out.println("Force loading library " + f.getName());
								
								try {
									ExternalResource res = new EmptyExternalResource<>(f.toURI().toURL());
									loader.bind(res);
									System.out.println("Successfully loaded " + f.getName());
								} catch (Exception e) {
									e.printStackTrace();
									f.delete();
									JOptionPane.showMessageDialog(null, "Error, Library " + f.getName() + " is "
													+ "corrupt, please restart to redownload it.",
											"Error", JOptionPane.ERROR_MESSAGE);
								}
							}
						}
					}
					
					Boot.checkEnjarify();
					Boot.checkKrakatau();
					
					Boot.globalstop = false;
					Boot.hide();
					
					if (CommandLineInput.parseCommandLine(BytecodeViewer.args) == CommandLineInput.OPEN_FILE)
						BytecodeViewer.boot(false);
					else {
						BytecodeViewer.boot(true);
						CommandLineInput.executeCommandLine(BytecodeViewer.args);
					}
				}
				finished = true;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException ignored) {
			}
		}
	}
}
