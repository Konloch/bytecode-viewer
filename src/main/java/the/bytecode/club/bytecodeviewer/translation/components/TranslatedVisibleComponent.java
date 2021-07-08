package the.bytecode.club.bytecodeviewer.translation.components;

import the.bytecode.club.bytecodeviewer.gui.components.VisibleComponent;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponentReference;
import the.bytecode.club.bytecodeviewer.translation.Translation;

/**
 * @author Konloch
 * @since 7/7/2021
 */
public class TranslatedVisibleComponent extends VisibleComponent
{
	private final TranslatedComponentReference componentReference;
	
	public TranslatedVisibleComponent(String title, Translation translation)
	{
		super(title);
		
		if(translation != null)
		{
			componentReference = translation.getTranslatedComponentReference();
			componentReference.runOnUpdate.add(()-> setTitle(componentReference.value));
			componentReference.translate();
		}
		else
		{
			componentReference = null;
		}
	}
}
