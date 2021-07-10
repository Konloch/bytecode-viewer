package the.bytecode.club.bytecodeviewer.util;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.api.BCV;

import java.io.File;

import static the.bytecode.club.bytecodeviewer.Constants.*;
import static the.bytecode.club.bytecodeviewer.Constants.RT_JAR_DUMPED;

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
public class BCVResourceUtils
{
	//rt.jar check
	public synchronized static void rtCheck()
	{
		if (Configuration.rt.isEmpty())
		{
			if (RT_JAR.exists())
				Configuration.rt = RT_JAR.getAbsolutePath();
			else if (RT_JAR_DUMPED.exists())
				Configuration.rt = RT_JAR_DUMPED.getAbsolutePath();
			else try {
					JRTExtractor.extractRT(RT_JAR_DUMPED.getAbsolutePath());
					Configuration.rt = RT_JAR_DUMPED.getAbsolutePath();
				} catch (Throwable t) {
					t.printStackTrace();
				}
		}
	}
}
