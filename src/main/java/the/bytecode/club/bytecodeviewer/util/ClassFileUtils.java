package the.bytecode.club.bytecodeviewer.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

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
 * @author Konloch
 * @since 7/6/2021
 */
public class ClassFileUtils
{
	/**
	 * Grab the byte array from the loaded Class object by getting the resource from the classloader
	 */
	public static byte[] getClassFileBytes(Class<?> clazz) throws IOException
	{
		try (InputStream is = clazz.getResourceAsStream("/" + clazz.getName().replace('.', '/') + ".class");
		     ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			int r;
			byte[] buffer = new byte[8192];
			while ((r = Objects.requireNonNull(is).read(buffer)) >= 0)
				baos.write(buffer, 0, r);
			return baos.toByteArray();
		}
	}
}
