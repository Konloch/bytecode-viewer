package the.bytecode.club.bytecodeviewer.util;

import the.bytecode.club.bytecodeviewer.gui.components.MultipleChoiceDialogue;

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
}
