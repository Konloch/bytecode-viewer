package the.bytecode.club.bytecodeviewer.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.filechooser.FileFilter;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.plugin.strategies.*;
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
public final class PluginManager {

    private static final Map<String, PluginLaunchStrategy> launchStrategies = new HashMap<>();
    private static final PluginFileFilter filter = new PluginFileFilter();
    private static Plugin pluginInstance;

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
    public static void runPlugin(Plugin newPluginInstance) {
        if (pluginInstance == null || pluginInstance.isFinished()) {
            pluginInstance = newPluginInstance;
            pluginInstance.start(); // start the thread
        } else if (!pluginInstance.isFinished()) {
            BytecodeViewer.showMessage("There is currently another plugin running right now, please wait for that to "
                    + "finish executing.");
        }
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

    public static void register(String name, PluginLaunchStrategy strat) {
        launchStrategies.put(name, strat);
    }

    public static Set<String> pluginExtensions() {
        return launchStrategies.keySet();
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
            return "BCV Plugins";
        }
    }
}