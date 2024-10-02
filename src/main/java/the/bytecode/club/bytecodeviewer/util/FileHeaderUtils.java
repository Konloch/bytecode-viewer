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

import org.apache.commons.lang3.StringUtils;

/**
 * @author Konloch
 * @since 8/21/2024
 */
public class FileHeaderUtils
{
    public static final int JAVA_CLASS_FILE_HEADER = 0xCAFEBABE;

    public static boolean doesFileHeaderMatch(byte[] bytes, int fileHeader)
    {
        int bytesHeader = ((bytes[0] & 0xFF) << 24)
            | ((bytes[1] & 0xFF) << 16)
            | ((bytes[2] & 0xFF) << 8)
            | ((bytes[3] & 0xFF));

        return bytesHeader == fileHeader;
    }

    public static String getFileHeaderAsString(byte[] bytes)
    {
        if (bytes == null || bytes.length < 4)
            return StringUtils.EMPTY;

        return String.format("%02X%02X%02X%02X", bytes[0], bytes[1], bytes[2], bytes[3]);
    }
}
