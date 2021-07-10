package the.bytecode.club.bytecodeviewer.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import me.konloch.kontainer.io.DiskWriter;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FilenameUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.ASMUtil;

import static the.bytecode.club.bytecodeviewer.Constants.*;

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
 * Loading and saving jars
 *
 * NOTE: This is in the process of being replaced with the Import & Export API
 *
 * @author Konloch
 * @author WaterWolf
 * @since 09/26/2011
 */

@Deprecated
public class JarUtils
{
    public static Object LOCK = new Object();
    
    /**
     * Loads the classes and resources from the input jar file
     *
     * @param jarFile the input jar file
     * @throws IOException
     */
    public static void importArchiveA(final File jarFile) throws IOException
    {
        FileContainer container = new FileContainer(jarFile);
        HashMap<String, byte[]> files = new HashMap<>();

        ZipInputStream jis = new ZipInputStream(new FileInputStream(jarFile));
        ZipEntry entry;
        while ((entry = jis.getNextEntry()) != null) {
            try {
                final String name = entry.getName();
                final byte[] bytes = MiscUtils.getBytes(jis);
                if (!name.endsWith(".class")) {
                    if (!entry.isDirectory())
                        files.put(name, bytes);
                } else {
                    if (MiscUtils.getFileHeader(bytes).equalsIgnoreCase("cafebabe"))
                    {
                        try {
                            final ClassNode cn = getNode(bytes);
                            container.resourceClasses.put(FilenameUtils.removeExtension(name), cn);
                        } catch (Exception e) {
                            System.err.println("Skipping: " + name);
                            e.printStackTrace();
                        }
                    } else {
                        if (!entry.isDirectory())
                            files.put(name, bytes);
                        //System.out.println(jarFile + ">" + name + ": Header does not start with CAFEBABE, ignoring.");
                    }
                }

            } catch (java.io.EOFException | ZipException e) {
                //ignore cause apache unzip
            } catch (Exception e) {
                BytecodeViewer.handleException(e);
            } finally {
                jis.closeEntry();
            }
        }
        jis.close();
        container.resourceFiles = files;
        BytecodeViewer.files.add(container);
    }
    
    
    /**
     * A fallback solution to zip/jar archive importing if the first fails
     *
     * @param jarFile the input jar file
     * @throws IOException
     */
    public static void importArchiveB(final File jarFile) throws IOException
    {
        //if this ever fails, worst case import Sun's jarsigner code from JDK 7 re-sign the jar to rebuild the CRC,
        // should also rebuild the archive byte offsets

        FileContainer container = new FileContainer(jarFile);
        HashMap<String, byte[]> files = new HashMap<>();

        try (ZipFile zipFile = new ZipFile(jarFile)) {
            Enumeration<? extends ZipArchiveEntry> entries = zipFile.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                String name = entry.getName();
                if (!entry.isDirectory()) {
                    try (InputStream in = zipFile.getInputStream(entry)) {
                        final byte[] bytes = MiscUtils.getBytes(in);

                        if (!name.endsWith(".class")) {
                            files.put(name, bytes);
                        } else {
                            if (MiscUtils.getFileHeader(bytes).equalsIgnoreCase("cafebabe"))
                            {
                                try {
                                    final ClassNode cn = getNode(bytes);
                                    container.resourceClasses.put(FilenameUtils.removeExtension(name), cn);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                files.put(name, bytes);
                            }
                        }

                    }
                }
            }
        }

        container.resourceFiles = files;
        BytecodeViewer.files.add(container);
    }
    
    public static ArrayList<ClassNode> loadClasses(final File jarFile) throws IOException
    {
        ArrayList<ClassNode> classes = new ArrayList<>();
        ZipInputStream jis = new ZipInputStream(new FileInputStream(jarFile));
        ZipEntry entry;
        while ((entry = jis.getNextEntry()) != null) {
            try {
                final String name = entry.getName();
                if (name.endsWith(".class")) {
                    byte[] bytes = MiscUtils.getBytes(jis);
                    if (MiscUtils.getFileHeader(bytes).equalsIgnoreCase("cafebabe"))
                    {
                        try {
                            final ClassNode cn = getNode(bytes);
                            classes.add(cn);
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
        jis.close();
        return classes;
    }

    /**
     * Loads resources only, just for .APK
     *
     * @param zipFile the input zip file
     * @throws IOException
     */
    public static HashMap<String, byte[]> loadResources(final File zipFile) throws IOException {
        if (!zipFile.exists())
            return null; //just ignore

        HashMap<String, byte[]> files = new HashMap<>();

        ZipInputStream jis = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry entry;
        while ((entry = jis.getNextEntry()) != null) {
            try {
                final String name = entry.getName();
                if (!name.endsWith(".class") && !name.endsWith(".dex")) {
                    if (!entry.isDirectory())
                        files.put(name, MiscUtils.getBytes(jis));

                    jis.closeEntry();
                }
            } catch (Exception e) {
                BytecodeViewer.handleException(e);
            } finally {
                jis.closeEntry();
            }
        }
        jis.close();

        return files;
    }

    /**
     * Creates a new ClassNode instances from the provided byte[]
     *
     * @param bytez the class file's byte[]
     * @return the ClassNode instance
     */
    public static ClassNode getNode(final byte[] bytez) {
        synchronized (LOCK)
        {
            return ASMUtil.bytesToNode(bytez);
        }
    }

    /**
     * Saves as jar with manifest
     *
     * @param nodeList the loaded ClassNodes
     * @param path     the exact path of the output jar file
     * @param manifest the manifest contents
     */
    public static void saveAsJar(ArrayList<ClassNode> nodeList, String path,
                                 String manifest) {
        try {
            JarOutputStream out = new JarOutputStream(
                    new FileOutputStream(path));
            for (ClassNode cn : nodeList) {
                ClassWriter cw = new ClassWriter(0);
                cn.accept(cw);

                out.putNextEntry(new ZipEntry(cn.name + ".class"));
                out.write(cw.toByteArray());
                out.closeEntry();
            }

            out.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
            out.write((manifest.trim() + "\r\n\r\n").getBytes());
            out.closeEntry();

            for (FileContainer container : BytecodeViewer.files)
                for (Entry<String, byte[]> entry : container.resourceFiles.entrySet()) {
                    String filename = entry.getKey();
                    if (!filename.startsWith("META-INF")) {
                        out.putNextEntry(new ZipEntry(filename));
                        out.write(entry.getValue());
                        out.closeEntry();
                    }
                }

            out.close();
        } catch (IOException e) {
            BytecodeViewer.handleException(e);
        }
    }

    /**
     * Saves a jar without the manifest
     *
     * @param nodeList The loaded ClassNodes
     * @param path     the exact jar output path
     */
    public static void saveAsJarClassesOnly(Collection<ClassNode> nodeList, String path)
    {
        synchronized (LOCK)
        {
            try
            {
                JarOutputStream out = new JarOutputStream(new FileOutputStream(path));
                ArrayList<String> noDupe = new ArrayList<>();
                for (ClassNode cn : nodeList)
                {
                    ClassWriter cw = new ClassWriter(0);
                    cn.accept(cw);
            
                    String name = cn.name + ".class";
            
                    if (!noDupe.contains(name))
                    {
                        noDupe.add(name);
                        out.putNextEntry(new ZipEntry(name));
                        out.write(cw.toByteArray());
                        out.closeEntry();
                    }
                }
        
                noDupe.clear();
                out.close();
            }
            catch (IOException e)
            {
                BytecodeViewer.handleException(e);
            }
        }
    }

    /**
     * Saves a jar without the manifest
     *
     * @param nodeList The loaded ClassNodes
     * @param dir      the exact jar output path
     */
    public static void saveAsJarClassesOnlyToDir(ArrayList<ClassNode> nodeList, String dir) {
        try {
            for (ClassNode cn : nodeList) {
                ClassWriter cw = new ClassWriter(0);
                cn.accept(cw);

                String name = dir + fs + cn.name + ".class";
                File f = new File(name);
                f.mkdirs();

                DiskWriter.replaceFile(name, cw.toByteArray(), false);
            }
        } catch (Exception e) {
            BytecodeViewer.handleException(e);
        }
    }

    /**
     * Saves a jar without the manifest
     *
     * @param nodeList The loaded ClassNodes
     * @param path     the exact jar output path
     */
    public static void saveAsJar(ArrayList<ClassNode> nodeList, String path) {
        try {
            JarOutputStream out = new JarOutputStream(new FileOutputStream(path));
            ArrayList<String> noDupe = new ArrayList<>();
            for (ClassNode cn : nodeList) {
                ClassWriter cw = new ClassWriter(0);
                cn.accept(cw);

                String name = cn.name + ".class";

                if (!noDupe.contains(name)) {
                    noDupe.add(name);
                    out.putNextEntry(new ZipEntry(name));
                    out.write(cw.toByteArray());
                    out.closeEntry();
                }
            }

            for (FileContainer container : BytecodeViewer.files)
                for (Entry<String, byte[]> entry : container.resourceFiles.entrySet()) {
                    String filename = entry.getKey();
                    if (!filename.startsWith("META-INF")) {
                        if (!noDupe.contains(filename)) {
                            noDupe.add(filename);
                            out.putNextEntry(new ZipEntry(filename));
                            out.write(entry.getValue());
                            out.closeEntry();
                        }
                    }
                }

            noDupe.clear();
            out.close();
        } catch (IOException e) {
            BytecodeViewer.handleException(e);
        }
    }
}
