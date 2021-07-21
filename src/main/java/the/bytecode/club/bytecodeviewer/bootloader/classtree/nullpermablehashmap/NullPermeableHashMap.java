package the.bytecode.club.bytecodeviewer.bootloader.classtree.nullpermablehashmap;

import java.util.HashMap;

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
public class NullPermeableHashMap<K, V> extends HashMap<K, V> {

    private static final long serialVersionUID = 1L;

    private final ValueCreator<V> creator;

    public NullPermeableHashMap(ValueCreator<V> creator) {
        this.creator = creator;
    }

    public NullPermeableHashMap() {
        this(new NullCreator<>());
    }

    public V getNonNull(K k) {
        V val = get(k);
        if (val == null) {
            val = creator.create();
            put(k, val);
        }
        return val;
    }
}