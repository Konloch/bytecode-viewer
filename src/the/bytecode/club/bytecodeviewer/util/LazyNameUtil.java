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
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

/**
 * @author Konloch
 */
public class LazyNameUtil
{
    public static boolean SAME_NAME_JAR_WORKSPACE = false;

    private static final HashMap<String, SeqAndCount> nameMap = new HashMap<>();

    public static void reset()
    {
        nameMap.clear();
    }

    public static String applyNameChanges(String name) {
        if (nameMap.containsKey(name)) {
            if (!SAME_NAME_JAR_WORKSPACE) {
                SAME_NAME_JAR_WORKSPACE = true;
            }

            SeqAndCount seqAndCount = nameMap.get(name);
            nameMap.put(name, seqAndCount.incrSeqAndCount());
            return FilenameUtils.removeExtension(name) + "#" + seqAndCount.getSeq() + "." + FilenameUtils.getExtension(name);
        } else {
            nameMap.put(name, SeqAndCount.init());
        }

        return name;
    }

    public static void removeName(String name) {
        if (StringUtils.isBlank(name)) {
            return;
        }

        if (name.contains("#")) {
            name = name.substring(0, name.indexOf("#")) + name.substring(name.indexOf("."));
        }

        SeqAndCount seqAndCount = nameMap.get(name);
        if (seqAndCount == null) {
            return;
        }

        // sequence remain the same and decrease the count
        // still the count become 1
        if (seqAndCount.getCount() == 1) {
            nameMap.remove(name);
        } else {
            nameMap.put(name, seqAndCount.decrCount());
        }
    }

}


class SeqAndCount {
    Integer seq;
    Integer count;

    public static SeqAndCount init() {
        SeqAndCount seqAndCount = new SeqAndCount();
        seqAndCount.setSeq(1);
        seqAndCount.setCount(1);
        return seqAndCount;
    }

    public SeqAndCount incrSeq() {
        seq = seq + 1;
        return this;
    }

    public SeqAndCount incrCount() {
        count = count + 1;
        return this;
    }

    public SeqAndCount decrCount() {
        count = count - 1;
        return this;
    }

    public SeqAndCount incrSeqAndCount() {
        seq = seq + 1;
        count = count + 1;
        return this;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}