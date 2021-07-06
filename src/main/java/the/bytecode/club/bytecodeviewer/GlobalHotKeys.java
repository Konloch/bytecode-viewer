package the.bytecode.club.bytecodeviewer;

import the.bytecode.club.bytecodeviewer.gui.components.FileChooser;
import the.bytecode.club.bytecodeviewer.gui.components.RunOptions;
import the.bytecode.club.bytecodeviewer.util.DialogueUtils;
import the.bytecode.club.bytecodeviewer.util.JarUtils;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * Whenever a key is pressed on the swing UI it should get logged here
 *
 * @author Konloch
 * @since 7/6/2021
 */
public class GlobalHotKeys
{
	/**
	 * Checks the hotkeys
	 */
	public static void keyPressed(KeyEvent e) {
		if (System.currentTimeMillis() - Configuration.lastHotKeyExecuted <= (4000))
			return;
		
		if ((e.getKeyCode() == KeyEvent.VK_O) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0))
		{
			Configuration.lastHotKeyExecuted = System.currentTimeMillis();
			
			final File file = DialogueUtils.fileChooser("Select File or Folder to open in BCV",
					"APKs, DEX, Class Files or Zip/Jar/War Archives",
					Constants.SUPPORTED_FILE_EXTENSIONS);
			
			if(file == null)
				return;
			
			BytecodeViewer.updateBusyStatus(true);
			BytecodeViewer.openFiles(new File[]{file}, true);
			BytecodeViewer.updateBusyStatus(false);
		} else if ((e.getKeyCode() == KeyEvent.VK_N) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
			Configuration.lastHotKeyExecuted = System.currentTimeMillis();
			BytecodeViewer.resetWorkspace(true);
		} else if ((e.getKeyCode() == KeyEvent.VK_T) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
			Configuration.lastHotKeyExecuted = System.currentTimeMillis();
			Thread t = new Thread(() -> BytecodeViewer.compile(true, false), "Compile");
			t.start();
		} else if ((e.getKeyCode() == KeyEvent.VK_R) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
			Configuration.lastHotKeyExecuted = System.currentTimeMillis();
			if (BytecodeViewer.getLoadedClasses().isEmpty()) {
				BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
				return;
			}
			new RunOptions().setVisible(true);
		} else if ((e.getKeyCode() == KeyEvent.VK_S) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
			Configuration.lastHotKeyExecuted = System.currentTimeMillis();
			
			if (BytecodeViewer.getLoadedClasses().isEmpty()) {
				BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
				return;
			}
			
			Thread resourceExport = new Thread(() ->
			{
				if (BytecodeViewer.autoCompileSuccessful())
					return;
				
				JFileChooser fc = new FileChooser(Configuration.getLastDirectory(),
						"Select Zip Export",
						"Zip Archives",
						"zip");
				
				int returnVal = fc.showSaveDialog(BytecodeViewer.viewer);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					Configuration.lastDirectory = fc.getSelectedFile().getAbsolutePath();
					File file = fc.getSelectedFile();
					
					if (!file.getAbsolutePath().endsWith(".zip"))
						file = new File(file.getAbsolutePath() + ".zip");
					
					if (!DialogueUtils.canOverwriteFile(file))
						return;
					
					final File file2 = file;
					
					BytecodeViewer.updateBusyStatus(true);
					Thread jarExport = new Thread(() -> {
						JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(),
								file2.getAbsolutePath());
						BytecodeViewer.updateBusyStatus(false);
					}, "Jar Export");
					jarExport.start();
				}
			}, "Resource Export");
			resourceExport.start();
		}
		else if ((e.getKeyCode() == KeyEvent.VK_W) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0))
		{
			Configuration.lastHotKeyExecuted = System.currentTimeMillis();
			if (BytecodeViewer.viewer.workPane.getCurrentViewer() != null)
				BytecodeViewer.viewer.workPane.tabs.remove(BytecodeViewer.viewer.workPane.getCurrentViewer());
		}
	}
}
