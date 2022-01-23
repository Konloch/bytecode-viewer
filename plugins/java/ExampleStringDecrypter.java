import java.lang.reflect.Field;
import java.util.List;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import the.bytecode.club.bytecodeviewer.api.*;
import the.bytecode.club.bytecodeviewer.gui.components.MultipleChoiceDialog;

import static the.bytecode.club.bytecodeviewer.Constants.nl;

/**
 ** This is an example of a String Decrypter Java Plugin for BCV.
 **
 ** @author [Your-Name-Goes-Here]
 **/

public class ExampleStringDecrypter extends Plugin {

    @Override
    public void execute(List<ClassNode> classNodesList) {
        PluginConsole gui = new PluginConsole("Example String Decrypter Java Edition");

        MultipleChoiceDialog dialog = new MultipleChoiceDialog("Bytecode Viewer - WARNING",
                "WARNING: This will load the classes into the JVM and execute the initialize function"
                        + nl + "for each class. IF THE FILE YOU'RE LOADING IS MALICIOUS, DO NOT CONTINUE.",
                new String[]{"Continue", "Cancel"});

        if (dialog.promptChoice() == 0) {
            boolean needsWarning = false;
            
            for (ClassNode cn : classNodesList) {
                try {
                    //load the class node into the classloader
                    BCV.getClassNodeLoader().addClass(cn);
    
                    for (Object o : cn.fields.toArray()) {
                        FieldNode f = (FieldNode) o;
        
                        //if the class contains the field z, get the class object from the class node
                        //then print out the value of the fields inside the class
                        //if the strings get decrypted on init, this allows you to dump the current values
                        
                        if (f.name.equals("z")) {
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
                } catch (Exception e) {
                    gui.appendText("Failed loading class " + cn.name);
                    e.printStackTrace();
                    needsWarning = true;
                }
            }
            
            if (needsWarning) {
                BytecodeViewer.showMessage("Some classes failed to decrypt, if you'd like to decrypt all of them\n"
                        + "makes sure you include ALL the libraries it requires.");
            }

            gui.setVisible(true);
        }
    }
}
