import java.util.List;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.api.*;

public class Skeleton extends Plugin {

    @Override
    public void execute(List<ClassNode> classNodesList) {
        PluginConsole gui = new PluginConsole("Skeleton");
        gui.setVisible(true);
        gui.appendText("executed skeleton");
    }

}
