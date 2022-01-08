import java.lang.reflect.Field;
import java.util.List;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import the.bytecode.club.bytecodeviewer.api.*;
import the.bytecode.club.bytecodeviewer.gui.components.MultipleChoiceDialog;

import static the.bytecode.club.bytecodeviewer.Constants.nl;

/**
 * This is an example of a string decrypter plugin
 */
public class ExampleStringDecrypter extends Plugin {

    @Override
    public void execute(List<ClassNode> classNodesList) {
        PluginConsole gui = new PluginConsole("Example String Decrypter");

        MultipleChoiceDialog dialog = new MultipleChoiceDialog("Bytecode Viewer - WARNING",
                "WARNING: This will load the classes into the JVM and execute the initialize function"
                        + nl + "for each class. IF THE FILE YOU'RE LOADING IS MALICIOUS, DO NOT CONTINUE.",
                new String[]{"Continue", "Cancel"});

        if (dialog.promptChoice() == 0) {
            for (ClassNode cn : classNodesList) {
                BCV.getClassNodeLoader().addClass(cn);

                for (Object o : cn.fields.toArray()) {
                    FieldNode f = (FieldNode) o;
                    if (f.name.equals("z")) {// && f.desc.equals("([Ljava/lang/String;)V")) {
                        try {
                            for (Field f2 : BCV.getClassNodeLoader().nodeToClass(cn).getFields()) {
                                String s = (String) f2.get(null);
                                if (s != null && !s.isEmpty())
                                    gui.appendText(cn + ":" + s);
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }

            }

            gui.setVisible(true);
        }
    }

}
