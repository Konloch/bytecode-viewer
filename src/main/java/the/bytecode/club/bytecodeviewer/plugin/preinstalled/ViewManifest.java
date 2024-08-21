/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Konloch - Konloch.com / BytecodeViewer.com           *
 *                                                                         *
 * This program is free software: you can redistribute it and/or modify    *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

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
public class ViewManifest extends Plugin
{
    @Override
    public void execute(List<ClassNode> classNodeList)
    {
        PluginConsole frame = new PluginConsole("View Manifest");
        frame.setVisible(true);
        
        //TODO android APKs may have AndroidManifests that can be viewed normally, this should be checked
        byte[] encodedAndroidManifest = activeContainer.getFileContents("AndroidManifest.xml");
        if(encodedAndroidManifest != null)
        {
            frame.appendText("Android APK Manifest:\r");
            byte[] decodedAndroidManifest = activeContainer.getFileContents("Decoded Resources/AndroidManifest.xml");
            if(decodedAndroidManifest != null)
                frame.appendText(new String(decodedAndroidManifest, StandardCharsets.UTF_8));
            else
                frame.appendText("Enable Settings>Decode APK Resources!");
        }
        
        byte[] jarManifest = activeContainer.getFileContents("META-INF/MANIFEST.MF");
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
