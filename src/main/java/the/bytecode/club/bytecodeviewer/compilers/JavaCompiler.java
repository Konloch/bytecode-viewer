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
 * Java Compiler
 * 
 * @author Konloch
 *
 */

public class JavaCompiler extends Compiler {

	@Override
	public byte[] compile(String contents, String name) {
		String fileStart = BytecodeViewer.tempDir.getAbsolutePath() + BytecodeViewer.fs + "temp"+MiscUtils.randomString(12)+BytecodeViewer.fs;
		String fileStart2 = BytecodeViewer.tempDir.getAbsolutePath() + BytecodeViewer.fs + "temp"+MiscUtils.randomString(12)+BytecodeViewer.fs;
		File java = new File(fileStart + BytecodeViewer.fs + name + ".java");
		File clazz = new File(fileStart2 + BytecodeViewer.fs + name + ".class");
		File cp = new File(BytecodeViewer.tempDir, "cpath_"+MiscUtils.randomString(12)+".jar");
		File tempD = new File(fileStart + BytecodeViewer.fs + name.substring(0,name.length() - name.split("/")[name.split("/").length-1].length()));
		tempD.mkdirs();
		new File(fileStart2).mkdirs();

		if(Settings.JAVAC_LOCATION.isEmpty()) {
			BytecodeViewer.showMessage("You need to set your Javac path, this requires the JDK to be downloaded."+BytecodeViewer.nl+"(C:/programfiles/Java/JRE_xx/bin/javac.exe)");
			BytecodeViewer.viewer.javac();
		}
		
		if(Settings.JAVAC_LOCATION.isEmpty()) {
			BytecodeViewer.showMessage("You need to set Javac!");
			return null;
		}

		try {
			FileUtils.write(java, contents, "UTF-8", false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		JarUtils.saveAsJar(BytecodeViewer.getLoadedBytes(), cp.getAbsolutePath());

		boolean cont = true;
		BytecodeViewer.sm.stopBlocking();
		try {
			String log = "";
			ProcessBuilder pb;
			
			if(Settings.PATH.isEmpty()) {
				pb = new ProcessBuilder(
						Settings.JAVAC_LOCATION.get(),
						"-d", fileStart2,
						"-classpath", cp.getAbsolutePath(),
						java.getAbsolutePath()
				);
			} else {
				pb = new ProcessBuilder(
						Settings.JAVAC_LOCATION.get(),
						"-d", fileStart2,
						"-classpath", cp.getAbsolutePath()+";"+Settings.PATH.get(),
						java.getAbsolutePath()
				);
			}
			
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
	        
	        if(!clazz.exists())
	        	throw new Exception(log);
	        
		} catch(Exception e) {
			cont = false;
			e.printStackTrace();
		}
		BytecodeViewer.sm.setBlocking();

		cp.delete();
		
		if(cont)
			try {
				return org.apache.commons.io.FileUtils.readFileToByteArray(clazz);
			} catch (IOException e) {
				new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
			}
		
		return null;
	}

}
