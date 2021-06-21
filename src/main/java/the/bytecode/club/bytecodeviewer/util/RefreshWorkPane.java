package the.bytecode.club.bytecodeviewer.util;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Konloch
 * @since 6/21/2021
 */
public class RefreshWorkPane implements ActionListener
{
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (BytecodeViewer.viewer.refreshOnChange.isSelected()) {
			if (BytecodeViewer.viewer.workPane.getCurrentViewer() == null)
				return;
			
			BytecodeViewer.viewer.workPane.refreshClass.doClick();
		}
	}
}
