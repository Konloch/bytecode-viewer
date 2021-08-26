package the.bytecode.club.bytecodeviewer.bootloader.resource.external;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.bootloader.resource.jar.JarInfo;
import the.bytecode.club.bytecodeviewer.bootloader.resource.jar.JarResource;
import the.bytecode.club.bytecodeviewer.bootloader.resource.jar.contents.JarContents;

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
 * @author Bibl (don't ban me pls)
 * @created 19 Jul 2015 02:33:23
 */
public class ExternalLibrary extends ExternalResource<JarContents<ClassNode>> {

    /**
     * @param location
     */
    public ExternalLibrary(URL location) {
        super(location);
    }

    /**
     * @param jar
     */
    public ExternalLibrary(JarInfo jar) {
        super(createJarURL(jar));
    }

    public static URL createJarURL(JarInfo jar) {
        try {
            return jar.formattedURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] read(InputStream in) throws IOException {
        try (ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1)
                byteArrayOut.write(buffer, 0, bytesRead);
            return byteArrayOut.toByteArray();
        }
    }

    protected ClassNode create(byte[] b) {
        ClassReader cr = new ClassReader(b);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        return cn;
    }

    /* (non-Javadoc)
     * @see the.bytecode.club.bytecodeviewer.loadermodel.ExternalResource#load()
     */
    @Override
    public JarContents<ClassNode> load() throws IOException {
        JarContents<ClassNode> contents = new JarContents<>();

        JarURLConnection con = (JarURLConnection) getLocation().openConnection();
        JarFile jar = con.getJarFile();

        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            try (InputStream is = jar.getInputStream(entry)) {
                byte[] bytes = read(is);
                if (entry.getName().endsWith(".class")) {
                    ClassNode cn = create(bytes);
                    contents.getClassContents().add(cn);
                } else {
                    JarResource resource = new JarResource(entry.getName(), bytes);
                    contents.getResourceContents().add(resource);
                }
            }
        }

        return contents;
    }
}
