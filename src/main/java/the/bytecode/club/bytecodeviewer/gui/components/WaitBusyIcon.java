package the.bytecode.club.bytecodeviewer.gui.components;

import the.bytecode.club.bytecodeviewer.Resources;

import javax.swing.*;

/**
 * @author Konloch
 * @since 7/4/2021
 */
public class WaitBusyIcon extends JMenuItemIcon
{
	public WaitBusyIcon()
	{
		super(loadIcon());
		setAlignmentY(0.65f);
	}
	
	public static Icon loadIcon()
	{
		if(Resources.busyIcon != null)
			return Resources.busyIcon;
			
		return Resources.busyB64Icon;
	}
}
