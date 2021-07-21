package the.bytecode.club.bytecodeviewer.translation.components;

import the.bytecode.club.bytecodeviewer.gui.components.VisibleComponent;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponentReference;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponents;

/**
 * @author Konloch
 * @since 7/7/2021
 */
public class TranslatedVisibleComponent extends VisibleComponent
{
	public TranslatedVisibleComponent(String title, TranslatedComponents translatedComponents)
	{
		super(title);
		
		if(translatedComponents != null)
		{
			TranslatedComponentReference componentReference = translatedComponents.getTranslatedComponentReference();
			componentReference.runOnUpdate.add(()->
			{
				if(componentReference.value != null && !componentReference.value.isEmpty())
					setTitle(componentReference.value);
			});
			componentReference.translate();
		}
	}
}
