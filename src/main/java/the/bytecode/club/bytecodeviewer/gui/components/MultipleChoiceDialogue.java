package the.bytecode.club.bytecodeviewer.gui.components;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;

import javax.swing.*;

/**
 * @author Konloch
 * @since 6/26/2021
 */
public class MultipleChoiceDialogue
{
	private final String title;
	private final String description;
	private final String[] options;
	
	public MultipleChoiceDialogue(String title, String description, String[] options)
	{
		this.title = title;
		this.description = description;
		this.options = options;
	}
	
	public int promptChoice()
	{
		JOptionPane pane = new JOptionPane(description);
		pane.setOptions(options);
		JDialog dialog = pane.createDialog(BytecodeViewer.viewer, title);
		dialog.setVisible(true);
		Object obj = pane.getValue();
		int result = -1;
		for (int k = 0; k < options.length; k++)
			if (options[k].equals(obj))
				result = k;
			
		return result;
	}
}
