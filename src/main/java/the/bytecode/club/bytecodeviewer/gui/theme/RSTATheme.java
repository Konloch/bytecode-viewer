package the.bytecode.club.bytecodeviewer.gui.theme;

import com.github.weisj.darklaf.extensions.rsyntaxarea.DarklafRSyntaxTheme;
import java.io.InputStream;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponents;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
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

/**
 * @author ThexXTURBOXx
 * @since 6/23/2021
 */

public enum RSTATheme
{
	//uses the darklaf RSyntaxTextArea extension
	THEME_MATCH("Theme Match (Recommended)", null, TranslatedComponents.THEME_MATCH),
	//uses the default theme from RSyntaxTextArea
	DEFAULT("Default (Recommended Light)", "/org/fife/ui/rsyntaxtextarea/themes/default.xml", TranslatedComponents.DEFAULT_RECOMMENDED_LIGHT),
	//uses the default dark theme from RSyntaxTextArea
	DARK("Dark (Recommended Dark)", "/org/fife/ui/rsyntaxtextarea/themes/dark.xml", TranslatedComponents.DARK),
	
	DEFAULT_ALT( "Default-Alt", "/org/fife/ui/rsyntaxtextarea/themes/default-alt.xml", TranslatedComponents.DEFAULT_ALT),
	ECLIPSE("Eclipse", "/org/fife/ui/rsyntaxtextarea/themes/eclipse.xml", TranslatedComponents.ECLIPSE),
	IDEA("IntelliJ", "/org/fife/ui/rsyntaxtextarea/themes/idea.xml", TranslatedComponents.INTELLIJ),
	VS("Visual Studio", "/org/fife/ui/rsyntaxtextarea/themes/vs.xml", TranslatedComponents.VISUAL_STUDIO),
	DRUID( "Druid (Dark)", "/org/fife/ui/rsyntaxtextarea/themes/druid.xml", TranslatedComponents.DRUID_DARK),
	MONOKAI( "Monokai (Dark)", "/org/fife/ui/rsyntaxtextarea/themes/monokai.xml", TranslatedComponents.MONOKAI_DARK),
	;
	
	private final String readableName;
	private final String file;
	private final TranslatedComponents translatedComponents;
	
	RSTATheme(String readableName, String file, TranslatedComponents translatedComponents)
	{
		this.readableName = readableName;
		this.file = file;
		this.translatedComponents = translatedComponents;
	}
	
	public String getReadableName() {
		return readableName;
	}
	
	public TranslatedComponents getTranslation()
	{
		return translatedComponents;
	}
	
	public RSyntaxTextArea apply(RSyntaxTextArea area) {
		try {
			switch(this)
			{
				case THEME_MATCH:
					if (Configuration.lafTheme == LAFTheme.SYSTEM) {
						//on system theme force default theme
						try (InputStream is = Constants.class.getResourceAsStream(DEFAULT.file)) {
							Theme.load(is).apply(area);
						}
					} else
						new DarklafRSyntaxTheme().apply(area);
					break;
					
				default:
					try (InputStream is = Constants.class.getResourceAsStream(file)) {
						Theme.load(is).apply(area);
					}
					break;
			}
		} catch (Throwable ignored) {
		}
		return area;
	}
	
	public static RSTATheme parse(String name) {
		for (RSTATheme t : values()) {
			if (t.name().equals(name)) {
				return t;
			}
		}
		return DEFAULT;
	}
	
}