package the.bytecode.club.bytecodeviewer.compilers;

import org.apache.commons.io.FileUtils;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.JarUtils;
import the.bytecode.club.bytecodeviewer.MiscUtils;
import the.bytecode.club.bytecodeviewer.Settings;

import java.io.*;

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
 *
 */
public class KrakatauAssembler extends Compiler {

	@Override
	public byte[] compile(String contents, String name) {
		if(Settings.PYTHON2_LOCATION.isEmpty()) {
			BytecodeViewer.showMessage("You need to set your Python (or PyPy for speed) 2.7 executable path.");
			BytecodeViewer.viewer.pythonC();
		}

		if(Settings.PYTHON2_LOCATION.isEmpty()) {
			BytecodeViewer.showMessage("You need to set Python!");
			return null;
		}

		String origName = name;
		name = MiscUtils.randomString(20);

		File tempD = new File(BytecodeViewer.tempDir, BytecodeViewer.fs + MiscUtils.randomString(32) + BytecodeViewer.fs);
		tempD.mkdir();
		
		File tempJ = new File(tempD.getAbsolutePath() + BytecodeViewer.fs+name+".j");
		try {
			FileUtils.write(tempJ, contents, "UTF-8", false);
		} catch (IOException e) {
			e.printStackTrace();
		}

		final File tempDirectory = new File(BytecodeViewer.tempDir, BytecodeViewer.fs + MiscUtils.randomString(32) + BytecodeViewer.fs);
		tempDirectory.mkdir();
		final File tempJar = new File(BytecodeViewer.tempDir, BytecodeViewer.fs + "temp"+MiscUtils.randomString(32)+".jar");
		JarUtils.saveAsJar(BytecodeViewer.getLoadedBytes(), tempJar.getAbsolutePath());
		
		BytecodeViewer.sm.stopBlocking();
        String log = "";
		try {
			ProcessBuilder pb = new ProcessBuilder(
					Settings.PYTHON2_LOCATION.get(),
					"-O", //love you storyyeller <3
					BytecodeViewer.krakatauDirectory.getAbsolutePath() + BytecodeViewer.fs + "assemble.py",
					"-out",
					tempDirectory.getAbsolutePath(),
					tempJ.getAbsolutePath()
			);

	        Process process = pb.start();
	        BytecodeViewer.createdProcesses.add(process);
	        
	        //Read out dir output
	        InputStream is = process.getInputStream();
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader br = new BufferedReader(isr);
	        String line;
	        while ((line = br.readLine()) != null) {
	            log += BytecodeViewer.nl + line;
	        }
	        br.close();
	        
	        log += BytecodeViewer.nl+BytecodeViewer.nl+"Error:"+BytecodeViewer.nl+BytecodeViewer.nl;
	        is = process.getErrorStream();
	        isr = new InputStreamReader(is);
	        br = new BufferedReader(isr);
	        while ((line = br.readLine()) != null) {
	            log += BytecodeViewer.nl + line;
	        }
	        br.close();
	        
	        int exitValue = process.waitFor();
	        log += BytecodeViewer.nl+BytecodeViewer.nl+"Exit Value is " + exitValue;
	        System.out.println(log);

			byte[] b = org.apache.commons.io.FileUtils.readFileToByteArray(new File(tempDirectory.getAbsolutePath() + BytecodeViewer.fs + origName + ".class"));
			tempDirectory.delete();
			tempJar.delete();
			return b;
		} catch(Exception e) {
			e.printStackTrace();
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(log);
		} finally {
			BytecodeViewer.sm.setBlocking();
		}
			
		return null;
	}
	
}
