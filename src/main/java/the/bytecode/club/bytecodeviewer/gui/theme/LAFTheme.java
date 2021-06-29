package the.bytecode.club.bytecodeviewer.gui.theme;

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
	SYSTEM("System Theme", RSTATheme.DEFAULT), //System theme
	DARK("Dark Theme", RSTATheme.DARK), //DarkLaf
	LIGHT("Light Theme", RSTATheme.DEFAULT), //Intellij theme
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
				LafManager.install(new DarculaTheme());
				break;
			
			case LIGHT:
				LafManager.install(new IntelliJTheme());
				break;
		}
	}
}
