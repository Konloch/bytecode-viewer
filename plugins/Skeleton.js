function execute(classNodeList)
{
    var PluginConsole = Java.type("the.bytecode.club.bytecodeviewer.api.PluginConsole");
    var gui = new PluginConsole("Skeleton");
    gui.setVisible(true);
    gui.appendText("executed skeleton");
}