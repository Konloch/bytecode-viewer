package the.bytecode.club.bytecodeviewer.bootloader.classtree;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.tree.ClassNode;

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
 * @created 25 May 2015 (actually before this)
 */
public class ClassHelper {

    public static Map<String, ClassNode> convertToMap(Collection<ClassNode> classes) {
        Map<String, ClassNode> map = new HashMap<>();
        for (ClassNode cn : classes) {
            map.put(cn.name, cn);
        }
        return map;
    }

    public static <T, K> Map<T, K> copyOf(Map<T, K> src) {
        Map<T, K> dst = new HashMap<>();
        copy(src, dst);
        return dst;
    }

    public static <T, K> void copy(Map<T, K> src, Map<T, K> dst) {
        dst.putAll(src);
    }
}
