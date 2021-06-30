package the.bytecode.club.bytecodeviewer.gui.theme;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.github.weisj.darklaf.theme.IntelliJTheme;
import the.bytecode.club.bytecodeviewer.translation.Translation;

import javax.swing.*;

/**
 * @author Konloch
 * @author ThexXTURBOXx
 * @since 6/24/2021
 */
public enum LAFTheme
{
	SYSTEM("System Theme", RSTATheme.DEFAULT, Translation.SYSTEM_THEME), //System theme
	DARK("Dark Theme", RSTATheme.DARK, Translation.DARK_THEME), //DarkLaf
	LIGHT("Light Theme", RSTATheme.DEFAULT, Translation.LIGHT_THEME), //Intellij theme
	;
	
	private final String readableName;
	private final RSTATheme rstaTheme;
	private final Translation translation;
	
	LAFTheme(String readableName, RSTATheme rstaTheme, Translation translation)
	{
		this.readableName = readableName;
		this.rstaTheme = rstaTheme;
		this.translation = translation;
	}
	
	public String getReadableName()
	{
		return readableName;
	}
	
	public RSTATheme getRSTATheme()
	{
		return rstaTheme;
	}
	
	public Translation getTranslation()
	{
		return translation;
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
