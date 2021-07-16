package the.bytecode.club.bytecodeviewer.translation.components;

import com.github.weisj.darklaf.icons.ThemedSVGIcon;
import the.bytecode.club.bytecodeviewer.gui.components.VisibleComponent;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.Workspace;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponentReference;
import the.bytecode.club.bytecodeviewer.translation.Translation;

/**
 * @author Konloch
 * @since 7/7/2021
 */
public class TranslatedVisibleComponent extends VisibleComponent
{
	public TranslatedVisibleComponent(String title, Translation translation)
	{
		super(title);
		
		if(translation != null)
		{
			TranslatedComponentReference componentReference = translation.getTranslatedComponentReference();
			componentReference.runOnUpdate.add(()->
			{
				if(componentReference.value != null && !componentReference.value.isEmpty())
					setTitle(componentReference.value);
			});
			componentReference.translate();
		}
	}
}
