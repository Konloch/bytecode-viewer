package the.bytecode.club.bytecodeviewer.translation.components;

import the.bytecode.club.bytecodeviewer.translation.TranslatedComponentReference;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponents;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * @author Konloch
 * @since 7/7/2021
 */
public class TranslatedDefaultMutableTreeNode extends DefaultMutableTreeNode
{
	private DefaultTreeModel tree;
	
	public TranslatedDefaultMutableTreeNode(String text, TranslatedComponents translatedComponents)
	{
		super(text);
		
		if(translatedComponents != null)
		{
			TranslatedComponentReference componentReference = translatedComponents.getTranslatedComponentReference();
			componentReference.runOnUpdate.add(()->
			{
				if(componentReference.value != null && !componentReference.value.isEmpty())
				{
					setUserObject(componentReference.value);
					if(tree != null)
						tree.nodeChanged(this);
				}
			});
			componentReference.translate();
		}
	}
	
	public void setTree(DefaultTreeModel tree)
	{
		this.tree = tree;
	}
}
