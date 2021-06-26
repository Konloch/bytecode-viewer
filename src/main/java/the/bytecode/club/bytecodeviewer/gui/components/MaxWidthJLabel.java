package the.bytecode.club.bytecodeviewer.gui.components;

import javax.swing.*;
import java.awt.*;

/**
 * @author Konloch
 * @since 6/25/2021
 */
public class MaxWidthJLabel extends JLabel
{
	private final int width;
	private final int height;
	
	public MaxWidthJLabel(String title, int width, int height)
	{
		super(title);
		this.width = width;
		this.height = height;
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		Dimension realDimension = super.getPreferredSize();
		if (realDimension.getWidth() >= width)
			return new Dimension(width, height);
		else
			return realDimension;
	}
	
	private static final long serialVersionUID = -5511025206527893360L;
}
