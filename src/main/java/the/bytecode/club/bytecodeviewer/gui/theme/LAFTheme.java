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
				failSafe();
				break;
			
			case LIGHT:
				LafManager.install(new IntelliJTheme());
				failSafe();
				break;
		}
	}
	
	/**
	 * Attempts to failsafe by forcing an error before the mainviewer is called.
	 * It then defaults to the system theme
	 */
	private static void failSafe() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException
	{
		try
		{
			JInternalFrame test = new JInternalFrame("Test LAF");
			test.dispose();
		}
		catch(Error e)
		{
			e.printStackTrace();
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
	}
}
