package the.bytecode.club.bytecodeviewer.bootloader.resource.jar;

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
 * Type of Jar Stored.
 *
 * @author Bibl
 * @created ages ago
 */
public enum JarType {

    /**
     * Local file
     **/
    FILE("file:"),
    /**
     * External URL
     **/
    WEB("");

    private final String prefix;

    JarType(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Gets the prefix for the JarURLConnection.
     **/
    public String prefix() {
        return prefix;
    }
}