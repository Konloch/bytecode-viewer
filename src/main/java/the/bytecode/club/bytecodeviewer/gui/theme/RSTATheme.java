package the.bytecode.club.bytecodeviewer.gui.theme;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import the.bytecode.club.bytecodeviewer.Constants;

/**
 * @author ThexXTURBOXx
 * @since 6/23/2021
 */
public enum RSTATheme
{
	DEFAULT("Default (Recommended Light)", null),
	DARK("Dark (Recommended Dark)", "/org/fife/ui/rsyntaxtextarea/themes/dark.xml"),
	DEFAULT_ALT( "Default-Alt", "/org/fife/ui/rsyntaxtextarea/themes/default-alt.xml"),
	ECLIPSE("Eclipse", "/org/fife/ui/rsyntaxtextarea/themes/eclipse.xml"),
	IDEA("IntelliJ", "/org/fife/ui/rsyntaxtextarea/themes/idea.xml"),
	VS("Visual Studio", "/org/fife/ui/rsyntaxtextarea/themes/vs.xml"),
	DRUID( "Druid (Dark)", "/org/fife/ui/rsyntaxtextarea/themes/druid.xml"),
	MONOKAI( "Monokai (Dark)", "/org/fife/ui/rsyntaxtextarea/themes/monokai.xml");
	
	private final String readableName;
	private final String file;
	
	RSTATheme(String readableName, String file)
	{
		this.readableName = readableName;
		this.file = file;
	}
	
	public String getReadableName() {
		return readableName;
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