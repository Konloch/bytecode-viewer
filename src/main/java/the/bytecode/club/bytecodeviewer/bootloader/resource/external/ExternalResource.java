package the.bytecode.club.bytecodeviewer.bootloader.resource.external;

import java.io.IOException;
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
 * @author Bibl (don't ban me pls)
 * @created 19 Jul 2015 02:30:30
 */
public abstract class ExternalResource<T> {

    private final URL location;

    public ExternalResource(URL location) {
        if (location == null)
            throw new IllegalArgumentException();
        this.location = location;
    }

    public URL getLocation() {
        return location;
    }

    public abstract T load() throws IOException;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + location.hashCode();
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
        ExternalResource<?> other = (ExternalResource<?>) obj;
        return location.equals(other.location);
    }

    @Override
    public String toString() {
        return "Library @" + location.toExternalForm();
    }
}