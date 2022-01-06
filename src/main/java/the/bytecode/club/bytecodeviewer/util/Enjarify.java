package the.bytecode.club.bytecodeviewer.util;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.resources.ExternalResources;

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
        if(!ExternalResources.getSingleton().hasSetPython3Command())
            return;

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
    
            AtomicBoolean holdThread = new AtomicBoolean(true);
            
            //wait for the process to finish then signal when done
            new Thread(()->{
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    holdThread.set(false);
                }
            }, "Enjarify Wait Thread").start();
            
            //if python3 fails to close but it was able to process the APK
            new Thread(()->{
                while(holdThread.get())
                {
                    if(output.length() > 0)
                        holdThread.set(false);
                    
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignored) { }
                }
            }, "Enjarify Fail Safe Thread").start();
            
            //hold thread while enjarify is processing
            while(holdThread.get())
            {
                Thread.sleep(100);
            }
            
            //kill the python3 process if it's still alive
            if(process.isAlive())
                process.destroy();
            
            MiscUtils.printProcess(process);

        } catch (Exception e) {
            BytecodeViewer.handleException(e);
        }
    }
}
