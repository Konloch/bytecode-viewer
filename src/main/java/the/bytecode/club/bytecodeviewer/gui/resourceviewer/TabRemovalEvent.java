package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ClassViewer;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.FileViewer;

import java.awt.*;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

/**
 * @author Konloch
 * @since 6/24/2021
 */
public class TabRemovalEvent implements ContainerListener
{
	@Override
	public void componentAdded(ContainerEvent e) { }
	
	@Override
	public void componentRemoved(ContainerEvent e)
	{
		final Component c = e.getChild();
		
		if (c instanceof ClassViewer)
		{
			String workingName = ((ClassViewer) c).workingName;
			BytecodeViewer.viewer.workPane.openedTabs.remove(workingName);
		}
		else if (c instanceof FileViewer)
		{
			String workingName = ((FileViewer) c).workingName;
			BytecodeViewer.viewer.workPane.openedTabs.remove(workingName);
		}
	}
}
