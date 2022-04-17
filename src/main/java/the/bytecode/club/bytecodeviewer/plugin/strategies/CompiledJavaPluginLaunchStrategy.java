package the.bytecode.club.bytecodeviewer.plugin.strategies;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.plugin.PluginLaunchStrategy;
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
 * @author Konloch
 * @author Bibl (don't ban me pls)
 * @created 1 Jun 2015
 */
public class CompiledJavaPluginLaunchStrategy implements PluginLaunchStrategy {

    private static final String PLUGIN_CLASS_NAME = Plugin.class.getCanonicalName().replace(".", "/");

    private final Set<LoadedPluginData> loaded = new HashSet<>();

    @Override
    public Plugin run(File file) throws Throwable {
        Set<LoadedNodeData> set = loadData(file);

        LoadedNodeData pdata = null;
        for (LoadedNodeData d : set) {
            ClassNode cn = d.node;
            if (Objects.equals(cn.superName, PLUGIN_CLASS_NAME)) {
                if (pdata == null) {
                    pdata = d;
                } else {
                    throw new RuntimeException("Multiple plugin subclasses.");
                }
            }
        }

        LoadingClassLoader cl = new LoadingClassLoader(pdata, set);
        Plugin p = cl.pluginKlass.getDeclaredConstructor().newInstance();
        LoadedPluginData npdata = new LoadedPluginData(pdata, cl, p);
        loaded.add(npdata);

        return p;
    }

    public Set<LoadedPluginData> getLoaded() {
        return loaded;
    }

    private static Set<LoadedNodeData> loadData(File jarFile) throws Throwable
    {
        try (FileInputStream fis = new FileInputStream(jarFile);
             ZipInputStream jis = new ZipInputStream(fis)) {
            ZipEntry entry;

            Set<LoadedNodeData> set = new HashSet<>();

            while ((entry = jis.getNextEntry()) != null) {
                try {
                    String name = entry.getName();
                    if (name.endsWith(".class")) {
                        byte[] bytes = MiscUtils.getBytes(jis);
                        if (MiscUtils.getFileHeaderMagicNumber(bytes).equalsIgnoreCase("cafebabe")) {
                            try {
                                ClassReader cr = new ClassReader(bytes);
                                ClassNode cn = new ClassNode();
                                cr.accept(cn, 0);
                                LoadedNodeData data = new LoadedNodeData(bytes, cn);
                                set.add(data);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println(jarFile + ">" + name + ": Header does not start with CAFEBABE, ignoring.");
                        }
                    }
                } catch (Exception e) {
                    BytecodeViewer.handleException(e);
                } finally {
                    jis.closeEntry();
                }
            }

            return set;
        }
    }

    public static class LoadedNodeData {
        private final byte[] bytes;
        private final ClassNode node;

        public LoadedNodeData(byte[] bytes, ClassNode node) {
            this.bytes = bytes;
            this.node = node;
        }
    }

    public static class LoadedPluginData {
        private final LoadedNodeData data;
        private final LoadingClassLoader classLoader;
        private final Plugin plugin;

        public LoadedPluginData(LoadedNodeData data, LoadingClassLoader classLoader, Plugin plugin) {
            this.data = data;
            this.classLoader = classLoader;
            this.plugin = plugin;
        }

        public LoadedNodeData getData() {
            return data;
        }

        public LoadingClassLoader getClassLoader() {
            return classLoader;
        }

        public Plugin getPlugin() {
            return plugin;
        }
    }

    public static class LoadingClassLoader extends ClassLoader {
        private final LoadedNodeData data;
        private final Map<String, LoadedNodeData> cache;
        private final Map<String, Class<?>> ccache;
        private final Class<? extends Plugin> pluginKlass;

        public LoadingClassLoader(LoadedNodeData data, Set<LoadedNodeData> set) throws Throwable {
            this.data = data;

            cache = new HashMap<>();
            ccache = new HashMap<>();

            for (LoadedNodeData d : set) {
                cache.put(d.node.name, d);
            }

            @SuppressWarnings("unchecked")
            Class<? extends Plugin> pluginKlass = (Class<? extends Plugin>) loadClass(data.node.name.replace("/", "."));

            if (pluginKlass == null)
                throw new RuntimeException();

            this.pluginKlass = pluginKlass;
        }

        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            name = name.replace(".", "/");

            System.out.println("finding " + name);

            if (ccache.containsKey(name))
                return ccache.get(name);

            LoadedNodeData data = cache.get(name);
            if (data != null) {
                byte[] bytes = data.bytes;
                Class<?> klass = defineClass(data.node.name.replace("/", "."), bytes, 0, bytes.length);
                ccache.put(name, klass);
                return klass;
            }

            return super.findClass(name);
        }

        public LoadedNodeData getPluginNode() {
            return data;
        }

        public Class<? extends Plugin> getPluginKlass() {
            return pluginKlass;
        }
    }
}
