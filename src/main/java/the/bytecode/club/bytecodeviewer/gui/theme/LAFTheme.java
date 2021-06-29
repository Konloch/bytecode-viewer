package the.bytecode.club.bytecodeviewer.gui.theme;

import com.bulenkov.darcula.DarculaLaf;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.github.weisj.darklaf.theme.IntelliJTheme;

import javax.swing.*;

/**
 * @author Konloch
 * @author ThexXTURBOXx
 * @since 6/24/2021
 */
public enum LAFTheme
{
	SYSTEM("System Theme (Fast)", RSTATheme.DEFAULT), //System theme
	DARK("Dark Theme (Fast)", RSTATheme.DARK), //Darcula 2017
	BETTER_DARK("Better Dark Theme (Slow)", RSTATheme.DARK), //Darcula 2021
	LIGHT("Light Theme (Slow)", RSTATheme.DEFAULT), //Intellij theme
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
			case SYSTEM:
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				break;
			
			case DARK:
				UIManager.setLookAndFeel(new DarculaLaf());
				break;
			
			case BETTER_DARK:
				LafManager.install(new DarculaTheme());
				break;
			
			case LIGHT:
				LafManager.install(new IntelliJTheme());
				break;
		}
	}
}
