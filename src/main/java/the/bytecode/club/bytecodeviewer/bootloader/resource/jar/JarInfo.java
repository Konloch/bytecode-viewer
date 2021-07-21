package the.bytecode.club.bytecodeviewer.bootloader.resource.jar;

import java.io.File;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
 * Holds information about a single local or external JarFile.
 *
 * @author Bibl
 * @created ages ago
 */
public class JarInfo {

    private final String path;
    private final JarType type;

    /**
     * Creates a new holder as the JarFile is on the local system.
     *
     * @param path Path to jar.
     */
    public JarInfo(File path) {
        this(path.getAbsolutePath(), JarType.FILE);
    }

    /**
     * Creates a new holder.
     *
     * @param path Path to jar.
     * @param type Type of jar.
     */
    public JarInfo(String path, JarType type) {
        this.path = path;
        this.type = type;
    }

    /**
     * Creates a new holder.
     *
     * @param url URL to jar.
     */
    public JarInfo(URL url) {
        this(url.toExternalForm(), JarType.WEB);
    }

    /**
     * @return Real path to JarFile.
     */
    public final String getPath() {
        return path;
    }

    public final JarType getType() {
        return type;
    }

    /**
     * Formats a string ready for a {@link JarURLConnection} to connect to.
     *
     * @return The formatted url.
     * @throws MalformedURLException
     */
    public URL formattedURL() throws MalformedURLException {
        StringBuilder sb = new StringBuilder().append("jar:").append(type.prefix()).append(path);
        if (type.equals(JarType.FILE) && !path.endsWith(".jar")) {
            File file = new File(path);
            if (!file.exists())
                sb.append(".jar");
        }
        sb.append("!/");
        return new URL(sb.toString());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((path == null) ? 0 : path.hashCode());
        result = (prime * result) + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        JarInfo other = (JarInfo) obj;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        return type == other.type;
    }
}