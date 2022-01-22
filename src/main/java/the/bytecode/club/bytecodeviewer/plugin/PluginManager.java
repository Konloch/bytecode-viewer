package the.bytecode.club.bytecodeviewer.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.filechooser.FileFilter;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.api.PluginConsole;
import the.bytecode.club.bytecodeviewer.gui.components.JFrameConsoleTabbed;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ComponentViewer;
import the.bytecode.club.bytecodeviewer.plugin.strategies.CompiledJavaPluginLaunchStrategy;
import the.bytecode.club.bytecodeviewer.plugin.strategies.GroovyPluginLaunchStrategy;
import the.bytecode.club.bytecodeviewer.plugin.strategies.JavaPluginLaunchStrategy;
import the.bytecode.club.bytecodeviewer.plugin.strategies.JavascriptPluginLaunchStrategy;
import the.bytecode.club.bytecodeviewer.plugin.strategies.PythonPluginLaunchStrategy;
import the.bytecode.club.bytecodeviewer.plugin.strategies.RubyPluginLaunchStrategy;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
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

/**
 * Supports loading of groovy, python or ruby scripts.
 *
 * Only allows one plugin to be running at once.
 *
 * @author Konloch
 * @author Bibl
 * @since 01/16/16, 14:36, Adaptable PluginLaunchStrategy system.
 */

public final class PluginManager
{
    private static final Map<String, PluginLaunchStrategy> launchStrategies = new HashMap<>();
    private static final PluginFileFilter filter = new PluginFileFilter();
    private static final List<Plugin> pluginInstances = new ArrayList<>();
    
    //TODO this system needs to be redone, currently it will conflict if more than one plugin is ran at the same time
    // the solution is to tie the plugin object into the plugin console,
    // then move all of this into the plugin class as non-static objects
    
    private static Plugin activePlugin;
    private static JFrameConsoleTabbed activeTabbedConsole;
    private static JFrameConsoleTabbed activeTabbedException;
    private static final Map<String, ExceptionUI> exceptionTabs = new HashMap<>();
    private static int consoleCount = 0;
    private static int exceptionCount = 0;
    private static int errorCounter = 1;

    static
    {
        launchStrategies.put("jar", new CompiledJavaPluginLaunchStrategy());
        launchStrategies.put("java", new JavaPluginLaunchStrategy());
        launchStrategies.put("js", new JavascriptPluginLaunchStrategy());

        GroovyPluginLaunchStrategy groovy = new GroovyPluginLaunchStrategy();
        launchStrategies.put("gy", groovy);
        launchStrategies.put("groovy", groovy);

        PythonPluginLaunchStrategy python = new PythonPluginLaunchStrategy();
        launchStrategies.put("py", python);
        launchStrategies.put("python", python);

        RubyPluginLaunchStrategy ruby = new RubyPluginLaunchStrategy();
        launchStrategies.put("rb", ruby);
        launchStrategies.put("ruby", ruby);
    }

    /**
     * Runs a new plugin instance
     *
     * @param newPluginInstance the new plugin instance
     */
    public static void runPlugin(Plugin newPluginInstance)
    {
        if (activePlugin != null && !activePlugin.isFinished())
        {
            BytecodeViewer.showMessage(TranslatedStrings.ONE_PLUGIN_AT_A_TIME.toString());
            return;
        }
        
        //reset the console count
        consoleCount = 0;
        exceptionCount = 0;
        
        //reset the active tabbed console
        activeTabbedConsole = null;
        activeTabbedException = null;
        exceptionTabs.clear();
    
        //reset the active plugin
        activePlugin = newPluginInstance;
        
        //clean the plugin list from dead threads
        pluginInstances.removeIf(Plugin::isFinished);
        
        //add to the list of running instances
        pluginInstances.add(newPluginInstance);
        
        //start the plugin thread
        newPluginInstance.start();
    }

    /**
     * Starts and runs a plugin from file
     *
     * @param f the file of the plugin
     * @throws Exception
     */
    public static void runPlugin(File f) throws Throwable
    {
        String ext = f.getName().substring(f.getName().lastIndexOf('.') + 1);
        PluginLaunchStrategy strategy = launchStrategies.get(ext);

        if (strategy == null)
            throw new RuntimeException(String.format("No launch strategy for extension %s (%s)", ext, f.getAbsolutePath()));

        Plugin p = strategy.run(f);

        if (p != null)
            runPlugin(p);
    }
    
    /**
     * Add an active console from a plugin being ran
     */
    public static void addExceptionUI(ExceptionUI ui)
    {
        if(activePlugin == null)
        {
            ui.setLocationRelativeTo(BytecodeViewer.viewer);
            ui.setVisible(true);
            return;
        }
        
        final String name = activePlugin.activeContainer == null
                ? "#" + (activeTabbedException.getTabbedPane().getTabCount() + 1)
                : activePlugin.activeContainer.name;
        
        ExceptionUI existingUI = exceptionTabs.get(name);
        
        int id = exceptionCount++;
        if(activeTabbedException == null)
        {
            String title = "Error #" + errorCounter++;
            activeTabbedException = new JFrameConsoleTabbed(title);
            
            if(Configuration.pluginConsoleAsNewTab)
                ComponentViewer.addComponentAsTab(title, activeTabbedException.getComponent(0));
            else
                activeTabbedException.setVisible(true);
        }
    
        if(existingUI == null)
        {
            activeTabbedException.addConsole(ui.getComponent(0), name);
            exceptionTabs.put(name, ui);
        }
        else
            existingUI.appendText("\n\r" + ui.getTextArea().getText());
    }
    
    /**
     * Add an active console from a plugin being ran
     */
    public static void addConsole(PluginConsole console)
    {
        int id = consoleCount++;
    
        if(activeTabbedConsole == null)
        {
            activeTabbedConsole = new JFrameConsoleTabbed(console.getTitle());
            
            if(Configuration.pluginConsoleAsNewTab)
                ComponentViewer.addComponentAsTab(console.getTitle(), activeTabbedConsole.getComponent(0));
            else
                activeTabbedConsole.setVisible(true);
        }
    
        console.setConsoleID(id);
        
        final String name = (activePlugin == null || activePlugin.activeContainer == null)
                ? ("#" + (activeTabbedConsole.getTabbedPane().getTabCount() + 1))
                : activePlugin.activeContainer.name;
        
        activeTabbedConsole.addConsole(console.getComponent(0), name);
    }

    public static void register(String name, PluginLaunchStrategy strat) {
        launchStrategies.put(name, strat);
    }

    public static Set<String> pluginExtensions() {
        return launchStrategies.keySet();
    }
    
    public static Map<String, PluginLaunchStrategy> getLaunchStrategies()
    {
        return launchStrategies;
    }
    
    public static FileFilter fileFilter() {
        return filter;
    }

    public static class PluginFileFilter extends FileFilter
    {
        @Override
        public boolean accept(File f) {
            if (f.isDirectory())
                return true;

            return PluginManager.pluginExtensions().contains(MiscUtils.extension(f.getAbsolutePath()));
        }

        @Override
        public String getDescription() {
            return TranslatedStrings.SELECT_EXTERNAL_PLUGIN_DESCRIPTION.toString();
        }
    }
}
