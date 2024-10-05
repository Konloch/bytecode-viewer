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

package the.bytecode.club.bytecodeviewer.plugin;

import com.google.common.io.Files;
import com.konloch.disklib.DiskReader;
import com.konloch.disklib.DiskWriter;
import org.apache.commons.compress.utils.FileNameUtils;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.gui.components.FileChooser;
import the.bytecode.club.bytecodeviewer.gui.components.SearchableRSyntaxTextArea;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ComponentViewer;
import the.bytecode.club.bytecodeviewer.resources.IconResources;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponents;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJMenu;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJMenuItem;
import the.bytecode.club.bytecodeviewer.util.DialogUtils;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;
import the.bytecode.club.bytecodeviewer.util.SyntaxLanguage;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static the.bytecode.club.bytecodeviewer.Constants.FS;
import static the.bytecode.club.bytecodeviewer.Constants.TEMP_DIRECTORY;
import static the.bytecode.club.bytecodeviewer.Settings.addRecentPlugin;

/**
 * @author Konloch
 * @since 7/1/2021
 */

public class PluginWriter extends JFrame
{
    private SearchableRSyntaxTextArea area;
    private JMenuItem menuSaveAs;
    private JMenuItem menuSave;
    private String content;
    private String pluginName;
    private File savePath;
    private long lastModifiedPluginWriterPane = 0;

    public PluginWriter(PluginTemplate template) throws IOException
    {
        this.content = template.getContents();
        this.pluginName = "Template." + template.getExtension();

        buildGUI();
    }

    public PluginWriter(String content, String pluginName)
    {
        this.content = content;
        this.pluginName = pluginName;

        buildGUI();
    }

    public void buildGUI()
    {
        setTitle("Editing BCV Plugin: " + pluginName);
        setIconImages(IconResources.iconList);
        setSize(new Dimension(542, 316));

        area = (SearchableRSyntaxTextArea) Configuration.rstaTheme.apply(new SearchableRSyntaxTextArea());
        area.setOnCtrlS(this::save);
        area.setText(content);
        area.setCaretPosition(0);
        DefaultCaret caret = (DefaultCaret)area.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        SyntaxLanguage.setLanguage(area, pluginName);
        content = null;

        lastModifiedPluginWriterPane = System.currentTimeMillis();

        area.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
                lastModifiedPluginWriterPane = System.currentTimeMillis();
            }
        });

        //TODO this could be replaced with a file watch service
        // I'll probably come back and fix this in the future, but if anyone needs to replace it:
        //          - https://github.com/Konloch/GitWatch4J/ has a base you can use

        //every 1 second, read the file timestamps and if the file has changed throw trigger an update
        BytecodeViewer.getTaskManager().delayLoop(1_000, task ->
        {
            if(!area.isValid())
                task.stop();
            else
                updateUIFromDiskChanges(null);
        });

        JButton run = new JButton("Run");

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new TranslatedJMenu("File", TranslatedComponents.FILE);
        JMenuItem menuOpen = new TranslatedJMenuItem("Open...", TranslatedComponents.OPEN);
        JMenuItem menuRun = new TranslatedJMenuItem("Run", TranslatedComponents.RUN);
        menuSaveAs = new TranslatedJMenuItem("Save As...", TranslatedComponents.SAVE_AS);
        menuSave = new TranslatedJMenuItem("Save...", TranslatedComponents.SAVE);
        menuSave.setVisible(false);

        menuBar.add(menu);
        menu.add(menuOpen);
        menu.add(menuSaveAs);
        menu.add(menuSave);
        menu.add(menuRun);

        setJMenuBar(menuBar);
        add(area.getScrollPane());
        add(run, BorderLayout.SOUTH);

        menuOpen.addActionListener((l) -> openPlugin());
        run.addActionListener((l) -> runPlugin());
        menuRun.addActionListener((l) -> runPlugin());
        menuSaveAs.addActionListener((l) -> save());
        menuSave.addActionListener((l) -> save());

        this.setLocationRelativeTo(null);
    }

    @Override
    public void setVisible(boolean b)
    {
        if (Configuration.pluginWriterAsNewTab)
        {
            Component component = getComponent(0);

            JPanel p = new JPanel(new BorderLayout());
            JPanel p2 = new JPanel(new BorderLayout());
            p.add(p2, BorderLayout.NORTH);
            p.add(component, BorderLayout.CENTER);

            p2.add(getJMenuBar(), BorderLayout.CENTER);

            ComponentViewer.addComponentAsTab(pluginName, p);
        }
        else
        {
            super.setVisible(b);
        }
    }

    public void setPluginName(String name)
    {
        this.pluginName = name;
        setTitle("Editing BCV Plugin: " + name);
    }

    public void openPlugin()
    {
        final File file = DialogUtils.fileChooser("Select External Plugin", "External Plugin", Configuration.getLastPluginDirectory(), PluginManager.fileFilter(), Configuration::setLastPluginDirectory, FileChooser.EVERYTHING);

        if (file == null || !file.exists())
            return;

        try
        {
            area.setText(DiskReader.readString(file.getAbsolutePath()));
            area.setCaretPosition(0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        setSourceFile(file);
    }

    public void runPlugin()
    {
        File tempFile = new File(TEMP_DIRECTORY + FS + "temp" + MiscUtils.randomString(32) + FS + pluginName);
        tempFile.getParentFile().mkdirs();

        try
        {
            //update the UI from disk changes / write to disk if plugin writer input has been modified
            updateUIFromDiskChanges(tempFile);

            //run plugin from that location
            PluginManager.runPlugin(tempFile);
        }
        catch (Exception e)
        {
            BytecodeViewer.handleException(e);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        finally
        {
            tempFile.getParentFile().delete();
        }
    }

    public void save()
    {
        Thread exportThread = new Thread(() ->
        {
            if (!BytecodeViewer.autoCompileSuccessful())
                return;

            if (savePath == null)
            {
                try
                {
                    final String ext = FileNameUtils.getExtension(pluginName);
                    JFileChooser fc = FileChooser.create(Configuration.getLastPluginDirectory(), "Save Plugin", "BCV Plugin", ext);

                    int returnVal = fc.showSaveDialog(BytecodeViewer.viewer);
                    if (returnVal == JFileChooser.APPROVE_OPTION)
                    {
                        Configuration.setLastPluginDirectory(fc.getSelectedFile());

                        File file = fc.getSelectedFile();
                        String path = file.getAbsolutePath();

                        //auto append extension
                        if (!path.endsWith("." + ext))
                            path += "." + ext;

                        if (!DialogUtils.canOverwriteFile(path))
                            return;

                        //swap from save-as to having a defined path each save
                        setSourceFile(new File(path));
                    }
                    else
                    {
                        return;
                    }
                }
                catch (Exception e)
                {
                    BytecodeViewer.handleException(e);
                }
            }

            try
            {
                DiskWriter.write(savePath.getAbsolutePath(), area.getText());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            addRecentPlugin(savePath);
        }, "Plugin Editor Save");

        exportThread.start();
    }

    public void setSourceFile(File file)
    {
        menuSaveAs.setVisible(false);
        menuSave.setVisible(true);
        menuSaveAs.updateUI();
        menuSave.updateUI();
        savePath = file;

        setPluginName(file.getName());
    }

    public synchronized void updateUIFromDiskChanges(File tempFile)
    {
        try
        {
            //opened a plugin from (Plugins>Open Plugin or Plugins>Recent Plugins)
            if (savePath != null)
            {
                if(savePath.lastModified() <= lastModifiedPluginWriterPane)
                {
                    if(tempFile != null) //when user clicks 'Run' instead of running every second
                    {
                        //original save path should be overwritten
                        Files.write(area.getText().getBytes(StandardCharsets.UTF_8), savePath); //overwrite original plugin location with new data
                        Files.write(area.getText().getBytes(StandardCharsets.UTF_8), tempFile); //write to temporary file location
                    }
                }
                else
                {
                    //update content from latest disk data
                    content = DiskReader.readString(savePath.getAbsolutePath());

                    //update plugin writer UI on disk update
                    SwingUtilities.invokeLater(() ->
                    {
                        try
                        {
                            area.setText(content);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    });

                    lastModifiedPluginWriterPane = System.currentTimeMillis();

                    if(tempFile != null)
                        Files.write(content.getBytes(StandardCharsets.UTF_8), tempFile); //write to temporary file location
                }
            }
            else if(tempFile != null)//temp plugin editing (Plugins>New Java Plugin>Run)
            {
                Files.write(area.getText().getBytes(StandardCharsets.UTF_8), tempFile); //write to temporary file location
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
