
/**
 ** An example BCV Javascript Plugin.
 ** This is used to display all of loaded classnodes that have been imported into BCV.
 **
 ** @author [Your-Name-Goes-Here]
 **/

var PluginConsole = Java.type("the.bytecode.club.bytecodeviewer.api.PluginConsole");

var gui = new PluginConsole("Example Plugin Print Loaded Classes Javascript Edition");

function execute(classNodeList) {
    for (index = 0; index < classNodeList.length; index++) {
        var cn = classNodeList[index];
        gui.appendText("Resource: " + cn.name + ".class");
    }

    gui.setVisible(true);
}