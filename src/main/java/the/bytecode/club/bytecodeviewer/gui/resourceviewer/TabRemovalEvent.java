package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ClassViewer;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.FileViewer;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ResourceViewer;
import the.bytecode.club.bytecodeviewer.resources.Resource;

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
		
		if(!(c instanceof ResourceViewer))
			return;
		
		String workingName = ((ResourceViewer) c).resource.workingName;
		BytecodeViewer.viewer.workPane.openedTabs.remove(workingName);
	}
}
