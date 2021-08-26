package the.bytecode.club.bytecodeviewer.compilers.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import me.konloch.kontainer.io.DiskWriter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.compilers.InternalCompiler;
import the.bytecode.club.bytecodeviewer.resources.ExternalResources;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.util.JarUtils;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import static the.bytecode.club.bytecodeviewer.Constants.fs;
import static the.bytecode.club.bytecodeviewer.Constants.krakatauWorkingDirectory;
import static the.bytecode.club.bytecodeviewer.Constants.nl;

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
 * Krakatau Java assembler, requires Python 2.7
 *
 * @author Konloch
 */
public class KrakatauAssembler extends InternalCompiler
{
    @Override
    public byte[] compile(String contents, String fullyQualifiedName)
    {
        if(!ExternalResources.getSingleton().hasSetPython2Command())
            return null;

        File tempD = new File(Constants.tempDirectory + fs + MiscUtils.randomString(32) + fs);
        tempD.mkdir();

        File tempJ = new File(tempD.getAbsolutePath() + fs + fullyQualifiedName + ".j");
        DiskWriter.replaceFile(tempJ.getAbsolutePath(), contents, true);

        final File tempDirectory = new File(Constants.tempDirectory + fs + MiscUtils.randomString(32) + fs);
        tempDirectory.mkdir();
        
        final File tempJar = new File(Constants.tempDirectory + fs + "temp" + MiscUtils.randomString(32) + ".jar");
        JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), tempJar.getAbsolutePath());
    
        StringBuilder log = new StringBuilder();
        
        try
        {
            String[] pythonCommands = new String[]{Configuration.python2};
            if(Configuration.python2Extra)
                pythonCommands = ArrayUtils.addAll(pythonCommands, "-2");
            
            ProcessBuilder pb = new ProcessBuilder(ArrayUtils.addAll(
                    pythonCommands,
                    "-O", //love you storyyeller <3
                    krakatauWorkingDirectory + fs + "assemble.py",
                    "-out",
                    tempDirectory.getAbsolutePath(),
                    tempJ.getAbsolutePath()
            ));

            Process process = pb.start();
            BytecodeViewer.createdProcesses.add(process);

            //Read out dir output
            try (InputStream is = process.getInputStream();
                 InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader br = new BufferedReader(isr)) {
                String line;
                while ((line = br.readLine()) != null)
                    log.append(nl).append(line);
            }

            log.append(nl).append(nl).append(TranslatedStrings.ERROR2).append(nl).append(nl);
            try (InputStream is = process.getErrorStream();
                 InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader br = new BufferedReader(isr)) {
                String line;
                while ((line = br.readLine()) != null)
                    log.append(nl).append(line);
            }

            int exitValue = process.waitFor();
            log.append(nl).append(nl).append(TranslatedStrings.EXIT_VALUE_IS).append(" ").append(exitValue);
            System.err.println(log);

            byte[] b = FileUtils.readFileToByteArray(Objects.requireNonNull(
                    ExternalResources.getSingleton().findFile(tempDirectory, ".class")));
            tempDirectory.delete();
            tempJar.delete();
            return b;
        } catch (Exception e) {
            e.printStackTrace();
            //BytecodeViewer.handleException(log.toString());
        }

        return null;
    }
}
