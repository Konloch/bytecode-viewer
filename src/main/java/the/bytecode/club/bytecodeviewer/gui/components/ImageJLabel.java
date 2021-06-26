package the.bytecode.club.bytecodeviewer.gui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Display an image on a JLabel element
 *
 * @author Konloch
 * @since 6/25/2021
 */
public class ImageJLabel extends JLabel
{
	public ImageJLabel(Image image)
	{
		super("", new ImageIcon(image), JLabel.CENTER);
	}
}
