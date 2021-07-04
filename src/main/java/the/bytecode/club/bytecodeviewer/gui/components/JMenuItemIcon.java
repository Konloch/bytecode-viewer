package the.bytecode.club.bytecodeviewer.gui.components;

import javax.swing.*;
import java.awt.*;

/**
 * @author Konloch
 * @since 7/4/2021
 */
public class JMenuItemIcon extends JMenuItem
{
	public JMenuItemIcon(Icon icon)
	{
		super("");
		
		setIcon(icon);
		setAlignmentY(0.65f);
		Dimension size = new Dimension((int) (icon.getIconWidth()*1.4), icon.getIconHeight());
		setSize(size);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
	}
	
	@Override
	public void paint(Graphics g)
	{
		g.setColor(UIManager.getColor("Panel.background"));
		g.fillRect(0, 0, getWidth(), getHeight());
		super.paint(g);
	}
}
