package the.bytecode.club.bytecodeviewer.gui.theme;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.translation.Translation;

/**
 * @author ThexXTURBOXx
 * @since 6/23/2021
 */
public enum RSTATheme
{
	DEFAULT("Default (Recommended Light)", null, Translation.DEFAULT_RECOMMENDED_LIGHT),
	DARK("Dark (Recommended Dark)", "/org/fife/ui/rsyntaxtextarea/themes/dark.xml", Translation.DARK_RECOMMENDED_DARK),
	DEFAULT_ALT( "Default-Alt", "/org/fife/ui/rsyntaxtextarea/themes/default-alt.xml", Translation.DEFAULT_ALT),
	ECLIPSE("Eclipse", "/org/fife/ui/rsyntaxtextarea/themes/eclipse.xml", Translation.ECLIPSE),
	IDEA("IntelliJ", "/org/fife/ui/rsyntaxtextarea/themes/idea.xml", Translation.INTELLIJ),
	VS("Visual Studio", "/org/fife/ui/rsyntaxtextarea/themes/vs.xml", Translation.VISUAL_STUDIO),
	DRUID( "Druid (Dark)", "/org/fife/ui/rsyntaxtextarea/themes/druid.xml", Translation.DRUID_DARK),
	MONOKAI( "Monokai (Dark)", "/org/fife/ui/rsyntaxtextarea/themes/monokai.xml", Translation.MONOKAI_DARK);
	
	private final String readableName;
	private final String file;
	private final Translation translation;
	
	RSTATheme(String readableName, String file, Translation translation)
	{
		this.readableName = readableName;
		this.file = file;
		this.translation = translation;
	}
	
	public String getReadableName() {
		return readableName;
	}
	
	public Translation getTranslation()
	{
		return translation;
	}
	
	public RSyntaxTextArea apply(RSyntaxTextArea area) {
		if (file != null) {
			try {
				Theme.load(Constants.class.getResourceAsStream(file)).apply(area);
			} catch (Throwable ignored) {
			}
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