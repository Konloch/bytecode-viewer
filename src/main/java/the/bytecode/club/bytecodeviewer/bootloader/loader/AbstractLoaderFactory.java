package the.bytecode.club.bytecodeviewer.bootloader.loader;

import java.util.HashMap;
import java.util.Map;
import the.bytecode.club.bytecodeviewer.bootloader.resource.external.ExternalResource;

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
 * @created 21 Jul 2015 00:18:07
 */
public final class AbstractLoaderFactory {

    private static final String DEFAULT_KEY = "default-factory";
    private static final Map<String, LoaderFactory<?>> FACTORY_CACHE = new HashMap<>();

    public static void register(LoaderFactory<?> factory) {
        register(DEFAULT_KEY, factory);
    }

    public static void register(String key, LoaderFactory<?> factory) {
        if (key == null || factory == null) {
            throw new IllegalArgumentException("null key or factory");
        }

        if (FACTORY_CACHE.containsKey(key)) {
            throw new IllegalArgumentException("factory already registered with key: " + key);
        }

        FACTORY_CACHE.put(key, factory);
    }

    public static void unregister(String key) {
        if (key == null) {
            throw new IllegalArgumentException("null key");
        }

        if (!FACTORY_CACHE.containsKey(key)) {
            throw new IllegalArgumentException("factory doesn't key for key: " + key);
        }

        FACTORY_CACHE.remove(key);
    }

    public static <T extends ExternalResource<?>> LoaderFactory<T> find() {
        return find(DEFAULT_KEY);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ExternalResource<?>> LoaderFactory<T> find(String key) {
        if (key == null) {
            throw new IllegalArgumentException("null key");
        }

        if (!FACTORY_CACHE.containsKey(key)) {
            throw new IllegalArgumentException("factory doesn't key for key: " + key);
        }

        return (LoaderFactory<T>) FACTORY_CACHE.get(key);
    }
}