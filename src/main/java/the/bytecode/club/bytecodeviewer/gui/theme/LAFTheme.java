package the.bytecode.club.bytecodeviewer.gui.theme;

import com.bulenkov.darcula.DarculaLaf;

import javax.swing.*;

/**
 * @author Konloch
 * @author ThexXTURBOXx
 * @since 6/24/2021
 */
public enum LAFTheme
{
	LIGHT("Light Theme (Requires restart)", RSTATheme.DEFAULT), //System theme
	DARK("Dark Theme (Requires restart)", RSTATheme.DARK), //Darcula
	;
	
	private final String readableName;
	private final RSTATheme rstaTheme;
	
	LAFTheme(String readableName, RSTATheme rstaTheme)
	{
		this.readableName = readableName;
		this.rstaTheme = rstaTheme;
	}
	
	public String getReadableName()
	{
		return readableName;
	}
	
	public RSTATheme getRSTATheme()
	{
		return rstaTheme;
	}
	
	public void setLAF() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException
	{
		switch(this)
		{
			default:
			case LIGHT:
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				break;
			
			case DARK:
				UIManager.setLookAndFeel(new DarculaLaf());
				break;
		}
	}
}
