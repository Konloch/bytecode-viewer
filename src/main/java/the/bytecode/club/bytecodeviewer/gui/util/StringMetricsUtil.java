package the.bytecode.club.bytecodeviewer.gui.util;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/**
 * @author http://stackoverflow.com/a/18450804
 */
public class StringMetricsUtil
{
	Font font;
	FontRenderContext context;
	
	public StringMetricsUtil(Graphics2D g2)
	{
		font = g2.getFont();
		context = g2.getFontRenderContext();
	}
	
	public Rectangle2D getBounds(String message)
	{
		return font.getStringBounds(message, context);
	}
	
	public double getWidth(String message)
	{
		Rectangle2D bounds = getBounds(message);
		return bounds.getWidth();
	}
	
	public double getHeight(String message)
	{
		Rectangle2D bounds = getBounds(message);
		return bounds.getHeight();
	}
	
}
