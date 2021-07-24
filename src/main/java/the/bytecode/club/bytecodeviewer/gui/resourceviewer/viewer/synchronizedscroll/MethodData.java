package the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.synchronizedscroll;

import org.objectweb.asm.Type;

import java.util.Arrays;

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
 * @since 6/24/2021
 */
public class MethodData
{
	public String name, desc;
	
	@Override
	public boolean equals(final Object o)
	{
		return equals((MethodData) o);
	}
	
	public boolean equals(final MethodData md)
	{
		return this.name.equals(md.name) && this.desc.equals(md.desc);
	}
	
	public String constructPattern()
	{
		final StringBuilder pattern = new StringBuilder();
		pattern.append(name).append(" *\\(");
		final org.objectweb.asm.Type[] types = org.objectweb.asm.Type
				.getArgumentTypes(desc);
		pattern.append("(.*)");
		Arrays.stream(types).map(Type::getClassName)
				.forEach(clazzName -> pattern.append(clazzName.substring(clazzName.lastIndexOf(".") + 1)).append(
						"(.*)"));
		pattern.append("\\) *\\{");
		return pattern.toString();
	}
}
