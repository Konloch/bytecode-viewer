package the.bytecode.club.bytecodeviewer.bootloader.resource.jar.contents;

import java.net.URL;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.bootloader.resource.DataContainer;
import the.bytecode.club.bytecodeviewer.bootloader.resource.jar.JarResource;

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
 * @created ages ago
 */
public class LocateableJarContents<C extends ClassNode> extends JarContents<C> {

    private final URL[] jarUrls;

    public LocateableJarContents(URL... jarUrls) {
        super();
        this.jarUrls = jarUrls;
    }

    public LocateableJarContents(DataContainer<C> classContents, DataContainer<JarResource> resourceContents,
                                 URL... jarUrls) {
        super(classContents, resourceContents);
        this.jarUrls = jarUrls;
    }

    public URL[] getJarUrls() {
        return jarUrls;
    }
}