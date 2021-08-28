package the.bytecode.club.bytecodeviewer.plugin.preinstalled;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.api.PluginConsole;

/**
 * @author Konloch
 * @since 07/11/2021
 */
public class ViewAPKAndroidPermissions extends Plugin
{
    @Override
    public void execute(List<ClassNode> classNodeList)
    {
        PluginConsole frame = new PluginConsole("Android Permissions");
        frame.setVisible(true);
        
        byte[] encodedAndroidManifest = activeContainer.getFileContents("AndroidManifest.xml");
        if(encodedAndroidManifest == null)
        {
            frame.appendText("This plugin only works on valid Android APKs");
            return;
        }
        
        byte[] decodedAndroidManifest = activeContainer.getFileContents("Decoded Resources/AndroidManifest.xml");
        if(decodedAndroidManifest != null)
        {
            String manifest = new String(decodedAndroidManifest, StandardCharsets.UTF_8);
            String[] lines = manifest.split("\r?\n");
            for(String line : lines)
                if(line.toLowerCase().contains("uses-permission"))
                {
                    String cleaned = line.trim();
                    if(cleaned.startsWith("<"))
                        cleaned = cleaned.substring(1);
                    if(cleaned.contains(" android:name=\""))
                        cleaned = cleaned.replace(" android:name=\"", ": ");
                    if(cleaned.endsWith("\"/>"))
                        cleaned = cleaned.substring(0, cleaned.length()-3);
                    frame.appendText(cleaned);
                }
        }
        else
            frame.appendText("Enable Settings>Decode APK Resources!");
    }
}
