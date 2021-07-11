package the.bytecode.club.bytecodeviewer.gui.components;

import the.bytecode.club.bytecodeviewer.resources.IconResources;

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
		if(IconResources.busyIcon != null)
			return IconResources.busyIcon;
			
		return IconResources.busyB64Icon;
	}
}
