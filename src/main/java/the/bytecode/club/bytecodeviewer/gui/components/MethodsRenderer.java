package the.bytecode.club.bytecodeviewer.gui.components;

import the.bytecode.club.bytecodeviewer.gui.util.PaneUpdaterThread;
import the.bytecode.club.bytecodeviewer.util.MethodParser;

import javax.swing.*;
import java.awt.*;

/**
 * @author Konloch
 * @author Waterwolf
 * @since 6/24/2021
 */
public class MethodsRenderer extends JLabel implements ListCellRenderer<Object>
{
	private final PaneUpdaterThread paneUpdaterThread;
	
	public MethodsRenderer(PaneUpdaterThread paneUpdaterThread)
	{
		this.paneUpdaterThread = paneUpdaterThread;
		setOpaque(true);
	}
	
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
	                                              boolean cellHasFocus)
	{
		MethodParser methods = paneUpdaterThread.viewer.methods.get(paneUpdaterThread.decompilerViewIndex);
		MethodParser.Method method = methods.getMethod((Integer) value);
		setText(method.toString());
		return this;
	}
}
