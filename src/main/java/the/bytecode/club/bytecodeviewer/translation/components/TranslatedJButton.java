package the.bytecode.club.bytecodeviewer.translation.components;

import the.bytecode.club.bytecodeviewer.translation.TranslatedComponentReference;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponents;

import javax.swing.*;

/**
 * @author Konloch
 * @since 7/7/2021
 */
public class TranslatedJButton extends JButton
{
	public TranslatedJButton(String text, TranslatedComponents translatedComponents)
	{
		super(text);
		
		if(translatedComponents != null)
		{
			TranslatedComponentReference componentReference = translatedComponents.getTranslatedComponentReference();
			componentReference.runOnUpdate.add(()->
			{
				if(componentReference.value != null && !componentReference.value.isEmpty())
					setText(componentReference.value);
			});
			componentReference.translate();
		}
	}
}
