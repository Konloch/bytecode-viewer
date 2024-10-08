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

package the.bytecode.club.bytecodeviewer.resources.exporting.impl;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.gui.components.FileChooser;
import the.bytecode.club.bytecodeviewer.gui.components.MultipleChoiceDialog;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.resources.exporting.Exporter;
import the.bytecode.club.bytecodeviewer.util.APKTool;
import the.bytecode.club.bytecodeviewer.util.DialogUtils;
import the.bytecode.club.bytecodeviewer.util.JarUtils;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static the.bytecode.club.bytecodeviewer.Constants.FS;
import static the.bytecode.club.bytecodeviewer.Constants.TEMP_DIRECTORY;

/**
 * @author Konloch
 * @since 6/27/2021
 */

public class APKExport implements Exporter
{
    @Override
    public void promptForExport()
    {
        if (BytecodeViewer.promptIfNoLoadedResources())
            return;

        Collection<ResourceContainer> containers = BytecodeViewer.getResourceContainers();
        List<ResourceContainer> validContainers = new ArrayList<>();
        List<String> validContainersNames = new ArrayList<>();
        ResourceContainer container;

        for (ResourceContainer resourceContainer : containers)
        {
            if (resourceContainer.APKToolContents != null && resourceContainer.APKToolContents.exists())
            {
                validContainersNames.add(resourceContainer.name);
                validContainers.add(resourceContainer);
            }
        }

        if (!validContainers.isEmpty())
        {
            container = validContainers.get(0);

            //if theres only one file in the container don't bother asking
            if (validContainers.size() >= 2)
            {
                MultipleChoiceDialog dialog = new MultipleChoiceDialog("Bytecode Viewer - Select APK",
                    "Which file would you like to export as an APK?", validContainersNames.toArray(new String[0]));

                //TODO may be off by one
                container = (ResourceContainer) containers.stream().skip(dialog.promptChoice());
            }
        }
        else
        {
            BytecodeViewer.showMessage("You can only export as APK from a valid APK file. Make sure Settings>Decode Resources is ticked on."
                + "\n\nTip: Try exporting as DEX, it doesn't rely on decoded APK resources");
            return;
        }

        final ResourceContainer finalContainer = container;

        Thread exportThread = new Thread(() ->
        {
            try
            {
                if (!BytecodeViewer.autoCompileSuccessful())
                    return;

                JFileChooser fc = FileChooser.create(Configuration.getLastSaveDirectory(), "Select APK Export", "Android APK", "apk");

                int returnVal = fc.showSaveDialog(BytecodeViewer.viewer);
                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    Configuration.setLastSaveDirectory(fc.getSelectedFile());

                    final File file = MiscUtils.autoAppendFileExtension(".apk", fc.getSelectedFile());

                    if (!DialogUtils.canOverwriteFile(file))
                        return;

                    Thread saveThread = new Thread(() ->
                    {
                        try
                        {
                            BytecodeViewer.updateBusyStatus(true);
                            final String input = TEMP_DIRECTORY + FS + MiscUtils.getRandomizedName() + ".jar";

                            JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), input);

                            Thread buildAPKThread = new Thread(() ->
                            {
                                APKTool.buildAPK(new File(input), file, finalContainer);
                                BytecodeViewer.updateBusyStatus(false);
                            }, "Process APK");

                            buildAPKThread.start();
                        }
                        catch (IOException ex)
                        {
                            BytecodeViewer.updateBusyStatus(false);
                            BytecodeViewer.handleException(ex);
                        }
                    }, "Jar Export");

                    saveThread.start();
                }
            }
            catch (Exception e)
            {
                BytecodeViewer.handleException(e);
            }
        }, "Resource Export");

        exportThread.start();
    }
}
