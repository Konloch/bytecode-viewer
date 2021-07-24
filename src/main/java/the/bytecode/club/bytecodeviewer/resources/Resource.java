package the.bytecode.club.bytecodeviewer.resources;

import org.objectweb.asm.tree.ClassNode;

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
 * @since 7/14/2021
 */
public class Resource
{
	public final String name;
	public String workingName;
	public final ResourceContainer container;
	
	public Resource(String name, String workingName, ResourceContainer container)
	{
		this.name = name;
		this.workingName = workingName;
		this.container = container;
	}
	
	/**
	 * Returns the resource bytes from the resource container
	 */
	public byte[] getResourceBytes()
	{
		return container.getFileContents(name);
	}
	
	/**
	 * Returns the resource bytes from the resource container
	 */
	public ClassNode getResourceClassNode()
	{
		return container.getClassNode(name);
	}
}
