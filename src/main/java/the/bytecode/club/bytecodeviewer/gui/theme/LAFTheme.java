/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Konloch - Konloch.com / BytecodeViewer.com           *
 *                                                                         *
 * This program is free software: you can redistribute it and/or modify    *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

package the.bytecode.club.bytecodeviewer.gui.theme;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.listener.UIUpdater;
import com.github.weisj.darklaf.properties.icons.IconLoader;
import com.github.weisj.darklaf.theme.*;

import java.awt.*;
import java.util.Properties;
import javax.swing.*;
import javax.swing.text.JTextComponent;

import com.github.weisj.darklaf.theme.info.PresetIconRule;
import com.github.weisj.darklaf.theme.spec.ColorToneRule;
import org.checkerframework.checker.guieffect.qual.UI;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.gui.components.SettingsDialog;
import the.bytecode.club.bytecodeviewer.gui.components.VisibleComponent;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponents;

/**
 * @author Konloch
 * @author ThexXTURBOXx
 * @since 6/24/2021
 */
public enum LAFTheme
{
	SYSTEM("System Theme", RSTATheme.THEME_MATCH, TranslatedComponents.SYSTEM_THEME), //System theme
	DARK("Dark Theme", RSTATheme.THEME_MATCH, TranslatedComponents.DARK_THEME), //DarkLaf
	LIGHT("Light Theme", RSTATheme.THEME_MATCH, TranslatedComponents.LIGHT_THEME), //Intellij theme
	ONE_DARK("One Dark Theme", RSTATheme.THEME_MATCH, TranslatedComponents.ONE_DARK_THEME),
	SOLARIZED_DARK("Solarized Dark Theme", RSTATheme.THEME_MATCH, TranslatedComponents.SOLARIZED_DARK_THEME),
	SOLARIZED_LIGHT("Solarized Light Theme", RSTATheme.THEME_MATCH, TranslatedComponents.SOLARIZED_LIGHT_THEME),
	HIGH_CONTRAST_DARK("High Contrast Dark Theme", RSTATheme.THEME_MATCH, TranslatedComponents.HIGH_CONTRAST_DARK_THEME),
	HIGH_CONTRAST_LIGHT("High Contrast Light Theme", RSTATheme.THEME_MATCH, TranslatedComponents.HIGH_CONTRAST_LIGHT_THEME),
	;
	
	private final String readableName;
	private final RSTATheme rstaTheme;
	private final TranslatedComponents translatedComponents;
	
	LAFTheme(String readableName, RSTATheme rstaTheme, TranslatedComponents translatedComponents)
	{
		this.readableName = readableName;
		this.rstaTheme = rstaTheme;
		this.translatedComponents = translatedComponents;
	}
	
	public String getReadableName()
	{
		return readableName;
	}
	
	public RSTATheme getRSTATheme()
	{
		return rstaTheme;
	}
	
	public TranslatedComponents getTranslation()
	{
		return translatedComponents;
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

		if (!LafManager.isInstalled())
		{
			setupIconColors();
			// Invalidate themed icons
			IconLoader.updateThemeStatus(new Object());
		}

		Configuration.showDarkLAFComponentIcons = darkLAF;
		
		if(BytecodeViewer.viewer != null)
		{
			BytecodeViewer.viewer.uiComponents.forEach(VisibleComponent::setDefaultIcon);
			
			BytecodeViewer.viewer.resourcePane.rightClickMenu.updateUI();
			BytecodeViewer.viewer.searchBoxPane.rightClickMenu.updateUI();
			
			//update all of the setting dialog components
			SettingsDialog.components.forEach(SwingUtilities::updateComponentTreeUI);
			
			//TODO instead of hiding the currently opened dialogs it should update/rebuild the dialogs
			
			//hide any existing jDialogs
			SettingsDialog.dialogs.forEach(Dialog::dispose);
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

	/**
	 * Make sure that theme changes also affect components that are not in the UI tree.
	 */
	public static void registerThemeUpdate(JComponent... components) {
		for (JComponent comp : components) {
			UIUpdater.registerComponent(comp);
		}
	}

	private void setupIconColors()
	{
		Properties properties = new Properties();
		JTextComponent colorTemplateComponent = new JTextField();
		properties.put("textForeground", colorTemplateComponent.getForeground());
		colorTemplateComponent.setEnabled(false);
		properties.put("textForegroundInactive", colorTemplateComponent.getForeground());
		properties.put("textSelectionForeground", colorTemplateComponent.getSelectedTextColor());

		new LightIconThemeSupplier().loadIconTheme(properties, UIManager.getDefaults(), IconLoader.get());

		UIManager.getLookAndFeelDefaults().putAll(properties);
	}

	private static class LightIconThemeSupplier extends Theme
	{

		@Override
		protected PresetIconRule getPresetIconRule()
		{
			return PresetIconRule.LIGHT;
		}

		@Override
		public String getPrefix()
		{
			return "DO NOT USE";
		}

		@Override
		public String getName()
		{
			return getPrefix();
		}

		@Override
		protected Class<? extends Theme> getLoaderClass()
		{
			return LightIconThemeSupplier.class;
		}

		@Override
		public ColorToneRule getColorToneRule()
		{
			return ColorToneRule.LIGHT;
		}
	}
}
