package the.bytecode.club.bytecodeviewer.translation.components;

import the.bytecode.club.bytecodeviewer.translation.TranslatedComponentReference;
import the.bytecode.club.bytecodeviewer.translation.Translation;

import javax.swing.*;

/**
 * @author Konloch
 * @since 7/8/2021
 */
public class TranslatedJTextField extends JTextField
{
	private final TranslatedComponentReference componentReference;
	
	public TranslatedJTextField(String text, Translation translation)
	{
		super(text);
		
		if(translation != null)
		{
			componentReference = translation.getTranslatedComponentReference();
			componentReference.runOnUpdate.add(()->
			{
				if(componentReference.value != null && !componentReference.value.isEmpty())
					setText(componentReference.value);
			});
			componentReference.translate();
		}
		else
		{
			componentReference = null;
		}
	}
}
