
/**
 ** This is an example of a String Decrypter Javascript Plugin for BCV.
 **
 ** @author [Your-Name-Goes-Here]
 **/

var PluginConsole = Java.type("the.bytecode.club.bytecodeviewer.api.PluginConsole");
var MultipleChoiceDialog = Java.type("the.bytecode.club.bytecodeviewer.gui.components.MultipleChoiceDialog")
var BytecodeViewer = Java.type("the.bytecode.club.bytecodeviewer.api.BCV")

var dialog = new MultipleChoiceDialog("Bytecode Viewer - WARNING",
    "WARNING: This will load the classes into the JVM and execute the initialize function"
    + "\nfor each class. IF THE FILE YOU'RE LOADING IS MALICIOUS, DO NOT CONTINUE.",
    ["Continue", "Cancel"]);
var gui;

function execute(classNodeList) {
    gui = new PluginConsole("Example String Decrypter Javascript Edition");

    if (dialog.promptChoice() == 0) {
        var needsWarning = false;

        for (cnIndex = 0; cnIndex < classNodeList.length; cnIndex++) {
            try {
                var cn = classNodeList[cnIndex];

                //load the class node into the classloader
                BytecodeViewer.getClassNodeLoader().addClass(cn);

                var fields = cn.fields.toArray();
                for (fieldIndex = 0; fieldIndex < fields.length; fieldIndex++) {
                    var field = fields[fieldIndex];

                    //if the class contains the field z, get the class object from the class node
                    //then print out the value of the fields inside the class
                    //if the strings get decrypted on init, this allows you to dump the current values

                    if (field.name.equals("z")) {
                        try {
                            var loadedClass = BytecodeViewer.getClassNodeLoader().nodeToClass(cn);
                            var reflectedFields = loadedClass.getFields();

                            for (reflectedFieldIndex = 0; reflectedFieldIndex < reflectedFields.length; reflectedFieldIndex++) {
                                var reflectedField = reflectedFields[fieldIndex];
                                var s = reflectedField.get(null);

                                if (s != null && !s.empty())
                                    gui.appendText(cn + "->" + s);
                            }
                        } catch (ignored) {
                        }
                    }
                }
            } catch (e) {
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
