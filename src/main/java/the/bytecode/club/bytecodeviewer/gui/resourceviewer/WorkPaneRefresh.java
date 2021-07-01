package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ClassViewer;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.FileViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Konloch
 * @since 6/24/2021
 */
public class WorkPaneRefresh implements Runnable
{
	private final ActionEvent event;
	
	public WorkPaneRefresh(ActionEvent event) {
		this.event = event;
	}
	
	@Override
	public void run()
	{
		if (BytecodeViewer.viewer.autoCompileOnRefresh.isSelected())
			try {
				if (!BytecodeViewer.compile(false))
					return;
			} catch (NullPointerException ignored) { }
		
		JButton src = null;
		if(event != null && event.getSource() instanceof JButton)
			src = (JButton) event.getSource();
		
		final Component tabComp = BytecodeViewer.viewer.workPane.tabs.getSelectedComponent();
		
		if(tabComp == null)
			return;
		
		if(src != null)
		{
			JButton finalSrc = src;
			SwingUtilities.invokeLater(()-> finalSrc.setEnabled(false));
		}
		
		BytecodeViewer.viewer.updateBusyStatus(true);
		
		if (tabComp instanceof ClassViewer)
			((ClassViewer) tabComp).startPaneUpdater(src);
		else if (tabComp instanceof FileViewer)
			((FileViewer) tabComp).refresh(src);
		
		BytecodeViewer.viewer.updateBusyStatus(false);
	}
}
