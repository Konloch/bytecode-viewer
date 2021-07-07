import the.bytecode.club.bytecodeviewer.api.*
import the.bytecode.club.bytecodeviewer.gui.components.MultipleChoiceDialogue;

import java.util.ArrayList;
import java.lang.reflect.Field;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode

import static the.bytecode.club.bytecodeviewer.Constants.nl;

/**
 * This is an example of a string decrypter plugin
 */
public class ExampleStringDecrypter extends Plugin {

    @Override
    public void execute(ArrayList<ClassNode> classNodesList) {
        PluginConsole gui = new PluginConsole("Example String Decrypter");

        MultipleChoiceDialogue dialogue = new MultipleChoiceDialogue("Bytecode Viewer - WARNING",
                "WARNING: This will load the classes into the JVM and execute the initialize function"
                        + nl + "for each class. IF THE FILE YOU'RE LOADING IS MALICIOUS, DO NOT CONTINUE.",
                new String[]{"Continue", "Cancel"});

        if(dialogue.promptChoice() == 0)
        {
            for(ClassNode cn : classNodesList)
            {
                the.bytecode.club.bytecodeviewer.api.BCV.getClassNodeLoader().addClass(cn);

                for(Object o : cn.fields.toArray())
                {
                    FieldNode f = (FieldNode) o;
                    if(f.name.equals("z")) {// && f.desc.equals("([Ljava/lang/String;)V")) {
                        try
                        {
                            for(Field f2 : the.bytecode.club.bytecodeviewer.api.BCV.getClassNodeLoader().nodeToClass(cn).getFields())
                            {
                                String s = f2.get(null);
                                if(s != null && !s.empty())
                                    gui.appendText(cn+":"+s);
                            }
                        } catch(Exception | StackOverflowError e) {}
                    }
                }

            }

            gui.setVisible(true);
        }
    }
}