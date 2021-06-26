package the.bytecode.club.bytecodeviewer.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.Configuration;

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
 * Represents a file container
 *
 * @author Konloch
 */

public class FileContainer {

    public FileContainer(File f) {
        this.file = f;
        this.name = LazyNameUtil.applyNameChanges(f.getName());
    }

    public FileContainer(File f, String name) {
        this.file = f;
        this.name = LazyNameUtil.applyNameChanges(name);
    }

    public File file;
    public String name;
    public File APKToolContents = null;

    public HashMap<String, byte[]> files = new HashMap<>();
    public ArrayList<ClassNode> classes = new ArrayList<>();

    public ClassNode getClassNode(String name)
    {
        for (ClassNode c : classes)
            if (c.name.equals(name))
                return c;

        return null;
    }
}
