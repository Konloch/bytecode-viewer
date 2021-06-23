package the.bytecode.club.bytecodeviewer.gui;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import the.bytecode.club.bytecodeviewer.Constants;

public enum RSTATheme {

    DEFAULT("default", "Default (Recommended Light)", null),
    DARK("dark", "Dark (Recommended Dark)", "/org/fife/ui/rsyntaxtextarea/themes/dark.xml"),
    DEFAULT_ALT("default-alt", "Default-Alt", "/org/fife/ui/rsyntaxtextarea/themes/default-alt.xml"),
    ECLIPSE("eclipse", "Eclipse", "/org/fife/ui/rsyntaxtextarea/themes/eclipse.xml"),
    IDEA("idea", "IntelliJ", "/org/fife/ui/rsyntaxtextarea/themes/idea.xml"),
    VS("vs", "Visual Studio", "/org/fife/ui/rsyntaxtextarea/themes/vs.xml"),
    DRUID("druid", "Druid (Dark)", "/org/fife/ui/rsyntaxtextarea/themes/druid.xml"),
    MONOKAI("monokai", "Monokai (Dark)", "/org/fife/ui/rsyntaxtextarea/themes/monokai.xml");

    public static final RSTATheme[] VALUES = values();

    private final String name;
    private final String friendlyName;
    private final String file;

    RSTATheme(String name, String friendlyName, String file) {
        this.name = name;
        this.friendlyName = friendlyName;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public String getFriendlyName() {
        return friendlyName;
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
        for (RSTATheme t : VALUES) {
            if (t.name.equals(name)) {
                return t;
            }
        }
        return DEFAULT;
    }

}
