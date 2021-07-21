package the.bytecode.club.bytecodeviewer.gui.theme;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.*;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.gui.components.SettingsDialogue;
import the.bytecode.club.bytecodeviewer.gui.components.VisibleComponent;
import the.bytecode.club.bytecodeviewer.translation.Translation;

import javax.swing.*;
import java.awt.*;

/**
 * @author Konloch
 * @author ThexXTURBOXx
 * @since 6/24/2021
 */
public enum LAFTheme
{
	SYSTEM("System Theme", RSTATheme.THEME_MATCH, Translation.SYSTEM_THEME), //System theme
	DARK("Dark Theme", RSTATheme.THEME_MATCH, Translation.DARK_THEME), //DarkLaf
	LIGHT("Light Theme", RSTATheme.THEME_MATCH, Translation.LIGHT_THEME), //Intellij theme
	ONE_DARK("One Dark Theme", RSTATheme.THEME_MATCH, Translation.ONE_DARK_THEME),
	SOLARIZED_DARK("Solarized Dark Theme", RSTATheme.THEME_MATCH, Translation.SOLARIZED_DARK_THEME),
	SOLARIZED_LIGHT("Solarized Light Theme", RSTATheme.THEME_MATCH, Translation.SOLARIZED_LIGHT_THEME),
	HIGH_CONTRAST_DARK("High Contrast Dark Theme", RSTATheme.THEME_MATCH, Translation.HIGH_CONTRAST_DARK_THEME),
	HIGH_CONTRAST_LIGHT("High Contrast Light Theme", RSTATheme.THEME_MATCH, Translation.HIGH_CONTRAST_LIGHT_THEME),
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
	
	public boolean isDark()
	{
		switch(this)
		{
			case DARK:
			case ONE_DARK:
			case SOLARIZED_DARK:
			case HIGH_CONTRAST_DARK:
				return true;
		}
		
		return false;
	}
	
	public void setLAF() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException
	{
		boolean darkLAF = true;
		
		switch(this)
		{
			default:
			case SYSTEM:
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				darkLAF = false;
				break;
			
			case DARK:
				LafManager.install(new DarculaTheme());
				break;
			
			case LIGHT:
				LafManager.install(new IntelliJTheme());
				break;
			
			case ONE_DARK:
				LafManager.install(new OneDarkTheme());
				break;
			
			case SOLARIZED_DARK:
				LafManager.install(new SolarizedDarkTheme());
				break;
			
			case SOLARIZED_LIGHT:
				LafManager.install(new SolarizedLightTheme());
				break;
			
			case HIGH_CONTRAST_DARK:
				LafManager.install(new HighContrastDarkTheme());
				break;
			
			case HIGH_CONTRAST_LIGHT:
				LafManager.install(new HighContrastLightTheme());
				break;
		}
		
		//test theme installed correctly
		if(darkLAF)
			failSafe();
		
		Configuration.showDarkLAFComponentIcons = darkLAF;
		
		if(BytecodeViewer.viewer != null)
		{
			BytecodeViewer.viewer.uiComponents.forEach(VisibleComponent::setDefaultIcon);
			//update all of the setting dialogue components
			SettingsDialogue.components.forEach(SwingUtilities::updateComponentTreeUI);
			
			//TODO instead of hiding this should update/rebuild the dialogue
			
			//hide any existing jDialogues
			SettingsDialogue.dialogues.forEach(Dialog::dispose);
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
