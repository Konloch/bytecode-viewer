package the.bytecode.club.bytecodeviewer.plugin.preinstalled;

import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.api.PluginConsole;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * @author Konloch
 * @since 07/11/2021
 */
public class ViewManifest extends Plugin
{
    @Override
    public void execute(ArrayList<ClassNode> classNodeList)
    {
        PluginConsole frame = new PluginConsole("View Manifest");
        frame.setVisible(true);
        
        //TODO android APKs may have AndroidManifests that can be viewed normally, this should be checked
        byte[] encodedAndroidManifest = BytecodeViewer.getFileContents("AndroidManifest.xml");
        if(encodedAndroidManifest != null)
        {
            frame.appendText("Android APK Manifest:\r");
            byte[] decodedAndroidManifest = BytecodeViewer.getFileContents("Decoded Resources/AndroidManifest.xml");
            if(decodedAndroidManifest != null)
                frame.appendText(new String(decodedAndroidManifest, StandardCharsets.UTF_8));
            else
                frame.appendText("Enable Settings>Decode APK Resources!");
        }
        
        byte[] jarManifest = BytecodeViewer.getFileContents("META-INF/MANIFEST.MF");
        if(jarManifest != null)
        {
            if(!frame.getTextArea().getText().isEmpty())
                frame.appendText("\r\n\r\n");
    
            frame.appendText("Java Jar Manifest:\r");
            frame.appendText(new String(jarManifest, StandardCharsets.UTF_8));
        }
        
        if(frame.getTextArea().getText().isEmpty())
            frame.appendText("Manifest not found!");
    }
}