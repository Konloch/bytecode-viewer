package the.bytecode.club.bytecodeviewer.util;

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

import org.apache.commons.io.FilenameUtils;

import java.util.HashMap;

/**
 * @author Konloch
 */
public class LazyNameUtil
{
    public static boolean SAME_NAME_JAR_WORKSPACE = false;

    private static final HashMap<String, Integer> nameMap = new HashMap<>();

    public static void reset()
    {
        nameMap.clear();
    }

    public static String applyNameChanges(String name)
    {
        if(nameMap.containsKey(name))
        {
            if(!SAME_NAME_JAR_WORKSPACE)
                SAME_NAME_JAR_WORKSPACE = true;

            int counter = nameMap.get(name)+1;
            nameMap.put(name, counter);

            return FilenameUtils.removeExtension(name)+"#"+counter+"."+FilenameUtils.getExtension(name);
        }
        else
        {
            nameMap.put(name, 1);
        }

        return name;
    }

}
