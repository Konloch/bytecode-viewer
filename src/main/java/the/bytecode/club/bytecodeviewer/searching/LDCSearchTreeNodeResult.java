package the.bytecode.club.bytecodeviewer.searching;

import javax.swing.tree.DefaultMutableTreeNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;

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
 * @since 7/29/2021
 */
public class LDCSearchTreeNodeResult extends DefaultMutableTreeNode
{
	public final ResourceContainer container;
	public final String resourceWorkingName;
	public final String ldc;
	public final String ldcType;
	
	public LDCSearchTreeNodeResult(ResourceContainer container, String resourceWorkingName,
	                               ClassNode cn, MethodNode method, FieldNode field,
	                               String ldc, String ldcType)
	{
		super("'"+ldc+"' -> " + cn.name);
		this.container = container;
		this.resourceWorkingName = resourceWorkingName;
		this.ldc = ldc;
		this.ldcType = ldcType;
	}
}
