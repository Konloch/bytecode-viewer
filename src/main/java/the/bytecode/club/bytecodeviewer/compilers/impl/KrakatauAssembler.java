/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Konloch - Konloch.com / BytecodeViewer.com           *
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

package the.bytecode.club.bytecodeviewer.compilers.impl;

import com.konloch.disklib.DiskWriter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.compilers.AbstractCompiler;
import the.bytecode.club.bytecodeviewer.resources.ExternalResources;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.util.JarUtils;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import static the.bytecode.club.bytecodeviewer.Constants.*;

/**
 * Krakatau Java assembler, requires Python 2.7
 *
 * @author Konloch
 */
public class KrakatauAssembler extends AbstractCompiler
{
    @Override
    public byte[] compile(String contents, String fullyQualifiedName)
    {
        if (!ExternalResources.getSingleton().hasSetPython2Command())
            return null;

        final File tempDirectory1 = new File(Constants.TEMP_DIRECTORY + FS + MiscUtils.randomString(32) + FS);
        final File tempDirectory2 = new File(Constants.TEMP_DIRECTORY + FS + MiscUtils.randomString(32) + FS);
        final File javaFile = new File(tempDirectory1.getAbsolutePath() + FS + fullyQualifiedName + ".j");
        final File tempJar = new File(Constants.TEMP_DIRECTORY + FS + "temp" + MiscUtils.randomString(32) + ".jar");
        final StringBuilder log = new StringBuilder();

        //create the temp directories
        tempDirectory1.mkdir();
        tempDirectory2.mkdir();

        try
        {
            //write the file we're assembling to disk
            DiskWriter.write(javaFile.getAbsolutePath(), contents);

            //write the entire temporary classpath to disk
            JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), tempJar.getAbsolutePath());

            String[] pythonCommands = new String[]{Configuration.python2};
            if (Configuration.python2Extra)
                pythonCommands = ArrayUtils.addAll(pythonCommands, "-2");

            ProcessBuilder pb = new ProcessBuilder(ArrayUtils.addAll(pythonCommands, "-O", //love you storyyeller <3
                krakatauWorkingDirectory + FS + "assemble.py", "-out", tempDirectory2.getAbsolutePath(), javaFile.getAbsolutePath()));

            Process process = pb.start();
            BytecodeViewer.createdProcesses.add(process);

            //Read out dir output
            try (InputStream is = process.getInputStream();
                 InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader br = new BufferedReader(isr))
            {
                String line;
                while ((line = br.readLine()) != null)
                    log.append(NL).append(line);
            }

            log.append(NL).append(NL).append(TranslatedStrings.ERROR2).append(NL).append(NL);
            try (InputStream is = process.getErrorStream();
                 InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader br = new BufferedReader(isr))
            {
                String line;
                while ((line = br.readLine()) != null)
                    log.append(NL).append(line);
            }

            int exitValue = process.waitFor();
            log.append(NL).append(NL).append(TranslatedStrings.EXIT_VALUE_IS).append(" ").append(exitValue);
            System.err.println(log);

            //read the assembled bytes from disk
            byte[] assembledBytes = FileUtils.readFileToByteArray(Objects.requireNonNull(ExternalResources.getSingleton().findFile(tempDirectory2, ".class")));

            //cleanup
            tempDirectory2.delete();
            tempJar.delete();

            //return the assembled file
            return assembledBytes;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //BytecodeViewer.handleException(log.toString());
        }

        return null;
    }
}
