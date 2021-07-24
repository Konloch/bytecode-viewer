package the.bytecode.club.bytecodeviewer.resources.exporting;

import the.bytecode.club.bytecodeviewer.resources.exporting.impl.APKExport;
import the.bytecode.club.bytecodeviewer.resources.exporting.impl.DexExport;
import the.bytecode.club.bytecodeviewer.resources.exporting.impl.RunnableJarExporter;
import the.bytecode.club.bytecodeviewer.resources.exporting.impl.ZipExport;

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
 * @since 6/27/2021
 */
public enum Export
{
	RUNNABLE_JAR(new RunnableJarExporter()),
	ZIP(new ZipExport()),
	DEX(new DexExport()),
	APK(new APKExport())
	;
	
	private final Exporter exporter;
	
	Export(Exporter exporter) {this.exporter = exporter;}
	
	public Exporter getExporter()
	{
		return exporter;
	}
}
