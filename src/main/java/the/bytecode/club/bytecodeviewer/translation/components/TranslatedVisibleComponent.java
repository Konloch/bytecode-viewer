package the.bytecode.club.bytecodeviewer.translation.components;

import the.bytecode.club.bytecodeviewer.gui.components.VisibleComponent;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponent;
import the.bytecode.club.bytecodeviewer.translation.Translation;

/**
 * @author Konloch
 * @since 7/7/2021
 */
public class TranslatedVisibleComponent extends VisibleComponent
{
	private final TranslatedComponent component;
	
	public TranslatedVisibleComponent(String title, Translation translation)
	{
		super(title);
		
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
			setTitle(component.value);
	}
}
