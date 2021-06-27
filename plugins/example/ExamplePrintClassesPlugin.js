/**
 * This is an example plugin
 */

var PluginConsole = Java.type("the.bytecode.club.bytecodeviewer.api.PluginConsole");

var gui = new PluginConsole("Example Plugin Print Loaded Classes");

function execute(classNodeList)
{
    for (index = 0; index < classNodeList.length; index++)
    {
        var cn = classNodeList[index];
        gui.appendText("Resource: " + cn.name + ".class");
    }

    gui.setVisible(true);
}