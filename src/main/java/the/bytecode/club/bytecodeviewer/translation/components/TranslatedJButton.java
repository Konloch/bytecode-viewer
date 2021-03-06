package the.bytecode.club.bytecodeviewer.translation.components;

import the.bytecode.club.bytecodeviewer.translation.TranslatedComponentReference;
import the.bytecode.club.bytecodeviewer.translation.Translation;

import javax.swing.*;

/**
 * @author Konloch
 * @since 7/7/2021
 */
public class TranslatedJButton extends JButton
{
	public TranslatedJButton(String text, Translation translation)
	{
		super(text);
		
		if(translation != null)
		{
			TranslatedComponentReference componentReference = translation.getTranslatedComponentReference();
			componentReference.runOnUpdate.add(()->
			{
				if(componentReference.value != null && !componentReference.value.isEmpty())
					setText(componentReference.value);
			});
			componentReference.translate();
		}
	}
}
