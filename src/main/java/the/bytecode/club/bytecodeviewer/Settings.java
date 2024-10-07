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

package the.bytecode.club.bytecodeviewer;

import com.google.gson.reflect.TypeToken;
import com.konloch.disklib.DiskReader;
import com.konloch.disklib.DiskWriter;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static the.bytecode.club.bytecodeviewer.BytecodeViewer.gson;
import static the.bytecode.club.bytecodeviewer.Configuration.maxRecentFiles;
import static the.bytecode.club.bytecodeviewer.Constants.*;

/**
 * @author Konloch
 * @since 6/29/2021
 */
public class Settings
{
    public static boolean firstBoot = true; //stays true after settings load on first boot
    public static boolean hasSetLanguageAsSystemLanguage = false;
    private static List<String> recentPlugins = new ArrayList<>();
    private static List<String> recentFiles = new ArrayList<>();

    //decompilers will automatically delete their temp files, useful to turn off if you want to quickly debug a decompilers results
    public static boolean DECOMPILERS_AUTOMATICALLY_CLEANUP = true;
    public static boolean DECOMPILERS_UNIFORM_SYNTAX_FORMATTING = false; //TODO

    static
    {
        try
        {
            File filesFile = new File(getBCVDirectory() + FS + "recentfiles.bcv");
            File pluginsFile = new File(getBCVDirectory() + FS + "recentplugins.bcv");

            if (new File(FILES_NAME).exists())
                recentFiles = gson.fromJson(DiskReader.readString(FILES_NAME), new TypeToken<ArrayList<String>>() {}.getType());
            else if (filesFile.exists())
                recentFiles = Arrays.asList(DiskReader.readArray(filesFile));

            if (new File(PLUGINS_NAME).exists())
                recentPlugins = gson.fromJson(DiskReader.readString(PLUGINS_NAME), new TypeToken<ArrayList<String>>() {}.getType());
            else if (pluginsFile.exists())
                recentPlugins = Arrays.asList(DiskReader.readArray(pluginsFile));

            MiscUtils.deduplicateAndTrim(recentFiles, maxRecentFiles);
            MiscUtils.deduplicateAndTrim(recentPlugins, maxRecentFiles);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Add the recent file
     *
     * @param f the recent file
     */
    public static synchronized void addRecentFile(File f)
    {
        recentFiles.remove(f.getAbsolutePath()); // already added on the list
        recentFiles.add(0, f.getAbsolutePath());
        MiscUtils.deduplicateAndTrim(recentFiles, maxRecentFiles);
        saveRecentFiles();
        resetRecentFilesMenu();
    }

    public static synchronized void removeRecentFile(File f)
    {
        if (recentFiles.remove(f.getAbsolutePath()))
        {
            saveRecentFiles();
            resetRecentFilesMenu();
        }
    }

    private static void saveRecentFiles()
    {
        try
        {
            DiskWriter.write(FILES_NAME, MiscUtils.listToString(recentFiles));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static String getRecentFile()
    {
        if (recentFiles.isEmpty())
            return null;

        return recentFiles.get(0);
    }

    /**
     * Add to the recent plugin list
     *
     * @param f the plugin file
     */
    public static synchronized void addRecentPlugin(File f)
    {
        recentPlugins.remove(f.getAbsolutePath()); // already added on the list
        recentPlugins.add(0, f.getAbsolutePath());
        MiscUtils.deduplicateAndTrim(recentPlugins, maxRecentFiles);
        saveRecentPlugins();
        resetRecentFilesMenu();
    }

    public static synchronized void removeRecentPlugin(File f)
    {
        if (recentPlugins.remove(f.getAbsolutePath()))
        {
            saveRecentPlugins();
            resetRecentFilesMenu();
        }
    }

    private static void saveRecentPlugins()
    {
        try
        {
            DiskWriter.write(PLUGINS_NAME, MiscUtils.listToString(recentPlugins));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * resets the recent files menu
     */
    protected static void resetRecentFilesMenu()
    {
        //build recent files
        BytecodeViewer.viewer.recentFilesSecondaryMenu.removeAll();

        for (String s : recentFiles)
        {
            if (!s.isEmpty())
            {
                JMenuItem m = new JMenuItem(s);

                m.addActionListener(e ->
                {
                    JMenuItem m12 = (JMenuItem) e.getSource();
                    BytecodeViewer.openFiles(new File[]{new File(m12.getText())}, true);
                });

                BytecodeViewer.viewer.recentFilesSecondaryMenu.add(m);
            }
        }

        //build recent plugins
        BytecodeViewer.viewer.recentPluginsSecondaryMenu.removeAll();

        for (String s : recentPlugins)
        {
            if (!s.isEmpty())
            {
                JMenuItem m = new JMenuItem(s);

                m.addActionListener(e ->
                {
                    JMenuItem m1 = (JMenuItem) e.getSource();
                    BytecodeViewer.startPlugin(new File(m1.getText()));
                });

                BytecodeViewer.viewer.recentPluginsSecondaryMenu.add(m);
            }
        }
    }
}
