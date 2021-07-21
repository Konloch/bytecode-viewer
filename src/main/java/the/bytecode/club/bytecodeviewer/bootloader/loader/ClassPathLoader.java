package the.bytecode.club.bytecodeviewer.bootloader.loader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
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
 * @created 21 Jul 2015 00:09:53
 */
public class ClassPathLoader implements ILoader<Object> {

    void extendClassPath(URL url) throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException {
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> urlClass = URLClassLoader.class;
        Method method = urlClass.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(urlClassLoader, url);
    }

    /*
     * (non-Javadoc)
     *
     * @see the.bytecode.club.bootloader.ILoader#bind(the.bytecode.club.bootloader .resource.ExternalResource)
     */
    @Override
    public void bind(ExternalResource<Object> resource) {
        try {
            if (resource != null) {
                URL url = resource.getLocation();
                if (url != null) {
                    extendClassPath(url);
                }
            }
        }/* catch (IOException e) {
			System.err.println("Error loading resource.");
			e.printStackTrace();
		}*/ catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            System.err.println("Error reflecting URLClassLoader.addURL(URL) ?");
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see the.bytecode.club.bootloader.ILoader#findClass(java.lang.String)
     */
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException, NoClassDefFoundError {
        return Class.forName(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see the.bytecode.club.bootloader.ILoader#loadClass(java.lang.String)
     */
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException, NoClassDefFoundError {
        return findClass(name);
    }
}