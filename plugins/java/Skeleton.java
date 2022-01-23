import java.util.List;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.api.*;

/**
 ** This is a skeleton template for BCV's Java Plugin System
 **
 ** @author [Your Name Goes Here]
 **/

public class Skeleton extends Plugin {

    @Override
    public void execute(List<ClassNode> classNodesList) {
        PluginConsole gui = new PluginConsole("Skeleton Title");
        gui.setVisible(true);
        gui.appendText("executed skeleton example");
    }
}