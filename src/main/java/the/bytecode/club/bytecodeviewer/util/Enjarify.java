package the.bytecode.club.bytecodeviewer.util;

import java.io.File;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;

import static the.bytecode.club.bytecodeviewer.Constants.enjarifyWorkingDirectory;

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
 * A simple wrapper for Enjarify.
 *
 * @author Konloch
 */

public class Enjarify {

    /**
     * Converts a .apk or .dex to .jar
     *
     * @param input  the input .apk or .dex file
     * @param output the output .jar file
     */
    public static synchronized void apk2Jar(File input, File output) {
        if (Configuration.python3.isEmpty()) {
            BytecodeViewer.showMessage("You need to set your Python (or PyPy for speed) 3.x executable path.");
            BytecodeViewer.viewer.selectPythonC3();
        }

        if (Configuration.python3.isEmpty()) {
            BytecodeViewer.showMessage("You need to set Python!");
            return;
        }

        BytecodeViewer.sm.stopBlocking();
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    Configuration.python3,
                    "-O",
                    "-m",
                    "enjarify.main",
                    input.getAbsolutePath(),
                    "-o",
                    output.getAbsolutePath(),
                    "-f"
            );

            pb.directory(new File(enjarifyWorkingDirectory));
            Process process = pb.start();
            BytecodeViewer.createdProcesses.add(process);
            process.waitFor();
            MiscUtils.printProcess(process);

        } catch (Exception e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        } finally {
            BytecodeViewer.sm.setBlocking();
        }
    }
}
