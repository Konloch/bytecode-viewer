package the.bytecode.club.bytecodeviewer.resources.importing;

import java.util.HashMap;
import the.bytecode.club.bytecodeviewer.resources.importing.impl.APKResourceImporter;
import the.bytecode.club.bytecodeviewer.resources.importing.impl.ClassResourceImporter;
import the.bytecode.club.bytecodeviewer.resources.importing.impl.DEXResourceImporter;
import the.bytecode.club.bytecodeviewer.resources.importing.impl.DirectoryResourceImporter;
import the.bytecode.club.bytecodeviewer.resources.importing.impl.FileResourceImporter;
import the.bytecode.club.bytecodeviewer.resources.importing.impl.XAPKResourceImporter;
import the.bytecode.club.bytecodeviewer.resources.importing.impl.ZipResourceImporter;

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
 * @since 6/26/2021
 */
public enum Import
{
	DIRECTORY(new DirectoryResourceImporter()),
	FILE(new FileResourceImporter()),
	//TODO ear needs to import the same as XAPK
	//TODO war needs to add the /libs correctly similar to XAPK
	ZIP(new ZipResourceImporter(), "zip", "jar", "war", "ear"),
	CLASS(new ClassResourceImporter(), "class"),
	XAPK(new XAPKResourceImporter(), "xapk"),
	APK(new APKResourceImporter(), "apk"),
	DEX(new DEXResourceImporter(), "dex"),
	;
	
	public static final HashMap<String, Import> extensionMap = new HashMap<>();
	
	private final Importer importer;
	private final String[] extensions;
	
	static
	{
		for(Import i : values())
			for(String s : i.extensions)
				extensionMap.put(s, i);
	}
	
	Import(Importer importer, String... extensions) {this.importer = importer;
		this.extensions = extensions;
	}
	
	public Importer getImporter()
	{
		return importer;
	}
}
