var BCV = Java.type("the.bytecode.club.bytecodeviewer.api.BCV");
var PluginConsole = Java.type("the.bytecode.club.bytecodeviewer.api.PluginConsole");
var gui;

/**
 ** [plugin description goes here]
 **
 ** @author [your name goes here]
 **/

/**
 * execute function - this gets executed when the plugin is ran
 */
function execute(classNodeList)
{
    //create & show the console
	gui = new PluginConsole("Javascript Template");
	gui.setVisible(true);

	//print to the console
	print("Class Nodes: " + classNodeList.size());

	//iterate through each class node
    for (index = 0; index < classNodeList.length; index++)
        processClassNode(classNodeList[index]);

    //hide the console after 10 seconds
	BCV.hideFrame(gui, 10000);
}

/**
 * process each class node
 */
function processClassNode(cn)
{
	print("Node: " + cn.name + ".class");

	//TODO developer plugin code goes here
}

/**
 * print to console
 */
function print(text)
{
	gui.appendText(text);
}