package the.bytecode.club.bytecodeviewer.translation.components;

import the.bytecode.club.bytecodeviewer.translation.TranslatedComponentReference;
import the.bytecode.club.bytecodeviewer.translation.Translation;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Konloch
 * @since 7/7/2021
 */
public class TranslatedDefaultMutableTreeNode extends DefaultMutableTreeNode
{
	private final TranslatedComponentReference componentReference;
	
	public TranslatedDefaultMutableTreeNode(String text, Translation translation)
	{
		super(text);
		
		if(translation != null)
		{
			componentReference = translation.getTranslatedComponentReference();
			componentReference.runOnUpdate.add(()-> setUserObject(componentReference.value));
			componentReference.translate();
		}
		else
		{
			componentReference = null;
		}
	}
}
