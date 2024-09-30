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

package the.bytecode.club.bytecodeviewer.gui.components;

import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author Konloch
 * @since 6/25/2021
 */
public class FileChooser
{
    public static final FutureTask<JFileChooser> SINGLETON = new FutureTask<>(JFileChooser::new);
    public static final String EVERYTHING = "everything";

    public static JFileChooser create(File file, String title, String description, String... extensions) throws ExecutionException, InterruptedException
    {
        return create(false, file, title, description, extensions);
    }

    public static JFileChooser create(boolean skipFileFilter, File file, String title, String description, String... extensions) throws ExecutionException, InterruptedException
    {
        JFileChooser chooser = SINGLETON.get();

        Set<String> extensionSet = new HashSet<>(Arrays.asList(extensions));

        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        try
        {
            chooser.setSelectedFile(file);
        }
        catch (Exception ignored)
        {
        }

        chooser.setDialogTitle(title);
        chooser.setFileHidingEnabled(false);
        chooser.setAcceptAllFileFilterUsed(false);

        chooser.resetChoosableFileFilters();

        if (!skipFileFilter)
        {
            chooser.addChoosableFileFilter(new FileFilter()
            {
                @Override
                public boolean accept(File f)
                {
                    if (f.isDirectory())
                        return true;

                    if (extensions[0].equals(EVERYTHING))
                        return true;

                    return extensionSet.contains(MiscUtils.extension(f.getAbsolutePath()));
                }

                @Override
                public String getDescription()
                {
                    return description;
                }
            });
        }

        return chooser;
    }
}
