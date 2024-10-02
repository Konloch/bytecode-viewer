/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Konloch - Konloch.com / BytecodeViewer.com           *
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

package the.bytecode.club.bytecodeviewer.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Prevents name path collisions by allowing the same name to be used in multiple resource containers.
 *
 * @author Konloch
 */
public class LazyNameUtil
{
    public static boolean sameNameJarWorkspace = false;
    private static final Map<String, SeqAndCount> NAME_MAP = new HashMap<>();

    public static void reset()
    {
        NAME_MAP.clear();
    }

    public static String applyNameChanges(String name)
    {
        if (NAME_MAP.containsKey(name))
        {
            if (!sameNameJarWorkspace)
                sameNameJarWorkspace = true;

            SeqAndCount seqAndCount = NAME_MAP.get(name);
            NAME_MAP.put(name, seqAndCount.incrSeqAndCount());
            return FilenameUtils.removeExtension(name) + "#" + seqAndCount.getSeq() + "." + FilenameUtils.getExtension(name);
        }
        else
            NAME_MAP.put(name, SeqAndCount.init());

        return name;
    }

    public static void removeName(String name)
    {
        if (StringUtils.isBlank(name))
            return;

        if (name.contains("#"))
            name = name.substring(0, name.indexOf("#")) + name.substring(name.indexOf("."));

        SeqAndCount seqAndCount = NAME_MAP.get(name);
        if (seqAndCount == null)
            return;

        // sequence remain the same and decrease the count
        // still the count become 1
        if (seqAndCount.getCount() == 1)
            NAME_MAP.remove(name);
        else
            NAME_MAP.put(name, seqAndCount.decrCount());
    }
}
