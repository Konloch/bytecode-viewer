package the.bytecode.club.bytecodeviewer.util;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.gui.components.FileChooser;
import the.bytecode.club.bytecodeviewer.gui.components.MultipleChoiceDialogue;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * @author Konloch
 * @since 7/1/2021
 */
public class DialogueUtils
{
	/**
	 * Asks if the user would like to overwrite the file
	 */
	public static boolean canOverwriteFile(String filePath)
	{
		return canOverwriteFile(new File(filePath));
	}
	
	/**
	 * Asks if the user would like to overwrite the file
	 */
	public static boolean canOverwriteFile(File file) {
		if (file.exists())
		{
			MultipleChoiceDialogue dialogue = new MultipleChoiceDialogue("Bytecode Viewer - Overwrite File",
					"Are you sure you wish to overwrite this existing file?",
					new String[]{"Yes", "No"});
			
			if (dialogue.promptChoice() == 0) {
				file.delete();
				
				return true;
			} else {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Prompts a File Chooser dilogue
	 */
	public static File fileChooser(String title, String description, String... extensions)
	{
		return fileChooser(title, description, null, extensions);
	}
	
	/**
	 * Prompts a File Chooser dilogue
	 */
	public static File fileChooser(String title, String description, FileFilter filter, String... extensions)
	{
		final JFileChooser fc = new FileChooser(Configuration.getLastDirectory(),
				title,
				description,
				extensions);
		
		if(filter != null)
			fc.setFileFilter(filter);
		
		int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);
		if (returnVal == JFileChooser.APPROVE_OPTION)
			try {
				File file = fc.getSelectedFile();
				Configuration.lastDirectory = file.getAbsolutePath();
				return file;
			} catch (Exception e1) {
				BytecodeViewer.handleException(e1);
			}
		
		return null;
	}
}
