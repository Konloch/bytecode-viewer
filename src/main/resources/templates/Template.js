var BCV = Java.type("the.bytecode.club.bytecodeviewer.api.BytecodeViewer");
var PluginConsole = Java.type("the.bytecode.club.bytecodeviewer.api.PluginConsole");
var gui = new PluginConsole("Javascript Template");

/**
 * Main function
 */
function execute(classNodeList)
{
	gui.setVisible(true); //show the console
	out("Class Nodes: " + classNodeList.size());

	//iterate through each class node
    for (index = 0; index < classNodeList.length; index++)
        process(classNodeList[index]);

	BCV.hideFrame(gui, 10000); //hides the console after 10 seconds
}

/**
 * Process each class node
 */
function process(cn)
{
	out("Node: " + cn.name + ".class");
	//TODO developer plugin code goes here
}

/**
 * Print to console
 */
function out(text)
{
	gui.appendText(text);
}