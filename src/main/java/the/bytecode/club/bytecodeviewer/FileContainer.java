package the.bytecode.club.bytecodeviewer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
 * Represents a file container
 * 
 * @author Konloch
 *
 */

public class FileContainer {
	public FileContainer(File f) {
		this.file = f;
		this.name = f.getName();
	}
	
	public File file;
	public String name;
	
	public HashMap<String, byte[]> files = new HashMap<String, byte[]>();
	private Map<String, ClassNode> classes = new HashMap<String, ClassNode>();

	public ClassNode getClassNode(String name) {
		if (!classes.containsKey(name)) {
			byte[] bytes = files.get(name + ".class");
			if (bytes != null) {
				ClassReader reader = new ClassReader(bytes);
				ClassNode classNode = new ClassNode();
				reader.accept(classNode, ClassReader.EXPAND_FRAMES);
				classes.put(name, classNode);
			}
		}
		return classes.get(name);
	}

	public Map<String, byte[]> getData() {
		return files;
	}

	public boolean remove(ClassNode classNode) {
		return classes.remove(classNode.name) != null;
	}

	@Deprecated
	public void add(ClassNode classNode) {
		classes.put(classNode.name, classNode);
	}

	@Deprecated
	public Collection<ClassNode> values() {
		return classes.values();
	}
}
