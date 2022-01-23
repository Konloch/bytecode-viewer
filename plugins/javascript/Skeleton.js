
/**
 ** This is a skeleton template for BCV's Javascript Plugin System
 **
 ** @author [Your Name Goes Here]
 **/

function execute(classNodeList) {
    var PluginConsole = Java.type("the.bytecode.club.bytecodeviewer.api.PluginConsole");
    var gui = new PluginConsole("Skeleton Title");
    gui.setVisible(true);
    gui.appendText("executed skeleton example");
}