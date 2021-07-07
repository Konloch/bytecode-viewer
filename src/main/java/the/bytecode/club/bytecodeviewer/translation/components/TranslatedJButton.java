package the.bytecode.club.bytecodeviewer.translation.components;

import the.bytecode.club.bytecodeviewer.translation.TranslatedComponent;
import the.bytecode.club.bytecodeviewer.translation.Translation;

import javax.swing.*;

/**
 * @author Konloch
 * @since 7/7/2021
 */
public class TranslatedJButton extends JButton
{
	private final TranslatedComponent component;
	
	public TranslatedJButton(String text, Translation translation)
	{
		super(text);
		
		if(translation != null)
		{
			this.component = translation.getTranslatedComponent();
			this.component.runOnUpdate.add(this::updateText);
		}
		else
		{
			this.component = null;
		}
	}
	
	public void updateText()
	{
		if(component != null)
			setText(component.value);
	}
}
