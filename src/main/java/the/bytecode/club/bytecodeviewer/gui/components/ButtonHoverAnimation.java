package the.bytecode.club.bytecodeviewer.gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Konloch
 * @since 6/25/2021
 */
public class ButtonHoverAnimation extends MouseAdapter
{
	@Override
	public void mouseEntered(final MouseEvent e)
	{
		final Component component = e.getComponent();
		if (component instanceof AbstractButton)
		{
			final AbstractButton button = (AbstractButton) component;
			button.setBorderPainted(true);
		}
	}
	
	@Override
	public void mouseExited(final MouseEvent e)
	{
		final Component component = e.getComponent();
		if (component instanceof AbstractButton)
		{
			final AbstractButton button = (AbstractButton) component;
			button.setBorderPainted(false);
		}
	}
}
