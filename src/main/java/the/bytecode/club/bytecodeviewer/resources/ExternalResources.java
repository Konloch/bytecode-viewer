package the.bytecode.club.bytecodeviewer.resources;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.SettingsSerializer;
import the.bytecode.club.bytecodeviewer.util.DialogueUtils;
import the.bytecode.club.bytecodeviewer.util.JRTExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import static the.bytecode.club.bytecodeviewer.Constants.*;

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
 * Anything that isn't accessible from inside of the JVM is here
 *
 * @author Konloch
 * @since 7/11/2021
 */
public class ExternalResources
{
	private static final ExternalResources SINGLETON = new ExternalResources();
	
	public static ExternalResources getSingleton()
	{
		return SINGLETON;
	}
	
	/**
	 * Auto-detect the Java command
	 */
	public String getJavaCommand(boolean blockTillSelected)
	{
		boolean empty = Configuration.java.isEmpty();
		
		if(!empty)
			return Configuration.java;
		
		try
		{
			BytecodeViewer.sm.pauseBlocking();
			//TODO read the version output to verify it exists
			ProcessBuilder pb = new ProcessBuilder("java", "-version");
			pb.start();
			
			Configuration.java = "java"; //java is set
			return Configuration.java;
		}
		catch (Exception e) { } //ignore
		finally
		{
			BytecodeViewer.sm.resumeBlocking();
		}
		
		//TODO auto-detect the Java path
		boolean block = true;
		while (Configuration.java.isEmpty() && block)
		{
			BytecodeViewer.showMessage("You need to set your Java path, this requires the JRE to be downloaded." +
					nl + "(C:/Program Files/Java/JDK_xx/bin/java.exe)");
			ExternalResources.getSingleton().selectJava();
			block = !blockTillSelected; //signal block flag off
		}
		
		return Configuration.java;
	}
	
	/**
	 * Check if the python command has been set
	 */
	public boolean hasJavaToolsSet()
	{
		return !getJavaTools(false).isEmpty();
	}
	
	/**
	 * Auto-detect the Java command
	 */
	public String getJavaTools(boolean blockTillSelected)
	{
		boolean empty = Configuration.javaTools.isEmpty();
		
		if(!empty)
			return Configuration.javaTools;
		
		//TODO auto-detect the Java path
		boolean block = true;
		while (Configuration.javaTools.isEmpty() && block)
		{
			BytecodeViewer.showMessage("You need to set your Java Tools path, this requires the JDK to be downloaded." +
					nl + "(C:/Program Files/Java/JDK_xx/lib/tools.jar)");
			ExternalResources.getSingleton().selectJavaTools();
			block = !blockTillSelected; //signal block flag off
		}
		
		return Configuration.javaTools;
	}
	
	/**
	 * Check if the python command has been set
	 */
	public boolean hasSetPythonCommand()
	{
		return !getPythonCommand(false).isEmpty();
	}
	
	/**
	 * Auto-detect the Java command
	 */
	public String getPythonCommand(boolean blockTillSelected)
	{
		boolean empty = Configuration.java.isEmpty();
		
		if(!empty)
			return Configuration.java;
		
		//check using python CLI flag
		try
		{
			BytecodeViewer.sm.pauseBlocking();
			
			//read the version output to verify python 2
			ProcessBuilder pb = new ProcessBuilder("python", "-2", "--version");
			Process p = pb.start();
			p.waitFor();
			
			//set python path
			if(readProcess(p).toLowerCase().contains("python 2"))
			{
				Configuration.python2 = "python";
				Configuration.python2Extra = "-2";
				return Configuration.python2;
			}
		}
		catch (Exception e) { } //ignore
		finally
		{
			BytecodeViewer.sm.resumeBlocking();
		}
		
		//check if 'python' command is bound as python 2.X
		try
		{
			BytecodeViewer.sm.pauseBlocking();
			
			//read the version output to verify python 2
			ProcessBuilder pb = new ProcessBuilder("python", "--version");
			Process p = pb.start();
			p.waitFor();
			
			//set python path
			if(readProcess(p).toLowerCase().contains("python 2"))
			{
				Configuration.python2 = "python";
				return Configuration.python2;
			}
		}
		catch (Exception e) { } //ignore
		finally
		{
			BytecodeViewer.sm.resumeBlocking();
		}
		
		//TODO auto-detect the Python path
		boolean block = true;
		while (Configuration.python2.isEmpty() && block)
		{
			BytecodeViewer.showMessage("You need to set your Python (or PyPy for speed) 2.7 executable path.");
			selectPython2();
			block = !blockTillSelected; //signal block flag off
		}
		
		return Configuration.python2;
	}
	
	//rt.jar check
	public synchronized void rtCheck()
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
	
	public void selectPython2()
	{
		final File file = DialogueUtils.fileChooser("Select Python 2.7 Executable",
				"Python (Or PyPy for speed) 2.7 Executable",
				"everything");
		
		if(file == null)
			return;
		
		Configuration.python2 = file.getAbsolutePath();
		SettingsSerializer.saveSettingsAsync();
	}
	
	public void selectPython3()
	{
		final File file = DialogueUtils.fileChooser("Select Python 3.x Executable",
				"Python (Or PyPy for speed) 3.x Executable",
				"everything");
		
		if(file == null)
			return;
		
		Configuration.python3 = file.getAbsolutePath();
		SettingsSerializer.saveSettingsAsync();
	}
	
	public void selectJavac()
	{
		final File file = DialogueUtils.fileChooser("Select Javac Executable",
				"Javac Executable (Requires JDK  'C:/Program Files/Java/JDK_xx/bin/javac.exe)",
				"everything");
		
		if(file == null)
			return;
		
		Configuration.javac = file.getAbsolutePath();
		SettingsSerializer.saveSettingsAsync();
	}
	
	public void selectJava()
	{
		final File file = DialogueUtils.fileChooser("Select Java Executable",
				"Java Executable (Inside Of JRE/JDK 'C:/Program Files/Java/JDK_xx/bin/java.exe')",
				"everything");
		
		if(file == null)
			return;
		
		Configuration.java = file.getAbsolutePath();
		SettingsSerializer.saveSettingsAsync();
	}
	
	public void selectJavaTools()
	{
		final File file = DialogueUtils.fileChooser("Select Java Tools Jar",
				"Java Tools Jar (Inside Of JDK 'C:/Program Files/Java/JDK_xx/lib/tools.jar')",
				"everything");
		
		if(file == null)
			return;
		
		Configuration.javaTools = file.getAbsolutePath();
		SettingsSerializer.saveSettingsAsync();
	}
	
	public void selectOptionalLibraryFolder()
	{
		final File file = DialogueUtils.fileChooser("Select Library Folder",
				"Optional Library Folder",
				"everything");
		
		if(file == null)
			return;
		
		Configuration.library = file.getAbsolutePath();
		SettingsSerializer.saveSettingsAsync();
	}
	
	public void selectJRERTLibrary()
	{
		final File file = DialogueUtils.fileChooser("Select JRE RT Jar",
				"JRE RT Library",
				"everything");
		
		if(file == null)
			return;
		
		Configuration.rt = file.getAbsolutePath();
		SettingsSerializer.saveSettingsAsync();
	}
	
	/**
	 * Finds a library from the library folder
	 */
	public String findLibrary(String nameContains)
	{
		for (File f : Objects.requireNonNull(new File(libsDirectory).listFiles()))
			if (f.getName().contains(nameContains))
				return f.getAbsolutePath();
		
		return null;
	}
	
	/**
	 * Searches a directory until the extension is found
	 */
	public File findFile(File basePath, String extension)
	{
		for(File f : basePath.listFiles())
		{
			if(f.isDirectory())
			{
				File child = findFile(f, extension);
				
				if(child != null)
					return child;
				
				continue;
			}
			
			if(f.getName().endsWith(extension))
				return f;
		}
		
		return null;
	}
	
	/**
	 * @author https://stackoverflow.com/a/16714180
	 */
	public String readProcess(Process process) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		StringBuilder builder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null)
		{
			builder.append(line);
			builder.append(System.getProperty("line.separator"));
		}
		
		return builder.toString();
	}
}
