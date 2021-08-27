package the.bytecode.club.bytecodeviewer.plugin.preinstalled;

import java.util.List;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.Plugin;

/**
 * As long as there are no new opcodes or API changes you can use this plugin to downgrade compiled code
 *
 * 1) Import a JDK-11 (or higher) Jar resource inside of BCV
 * 2) Run this plugin
 * 3) Export as ZIP, then rename as Jar - Your ClassFiles will now run on JDK-8 (or whatever you selected)
 *
 * @author Konloch
 * @since 07/11/2021
 */
public class ChangeClassFileVersions extends Plugin
{
    @Override
    public void execute(List<ClassNode> classNodeList)
    {
        //prompt dialog for version number
        // TODO: include a little diagram of what JDK is which number
        int newVersion = Integer.parseInt(BytecodeViewer.showInput("Class Version Number: (52 = JDK 8)"));
        
        //update the ClassFile version
        classNodeList.forEach(classNode -> classNode.version = newVersion);
        
        //update the the container's resource byte-arrays
        BytecodeViewer.updateAllClassNodeByteArrays();
        
        //force refresh all tabs (this forces the decompilers to run with the latest resource data)
        BytecodeViewer.refreshAllTabs();
        
        //alert the changes to the user
        BytecodeViewer.showMessage("Set all of the class versions to " + newVersion);
    }
}
