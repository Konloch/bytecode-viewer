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
	 * Auto-detect Java via command-line
	 */
	public String getJavaCommand(boolean blockTillSelected)
	{
		if(!Configuration.java.isEmpty())
			return Configuration.java;
		
		//check CLI for java
		testCommand(new String[]{"java", "-version"}, "java version", ()->{
			Configuration.java = "java";
		});
		if(!Configuration.java.isEmpty())
			return Configuration.java;
		
		//TODO auto-detect the JRE path
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
	 * Check if java tools has been set
	 */
	public boolean hasJavaToolsSet()
	{
		return !getJavaTools(false).isEmpty();
	}
	
	/**
	 * Auto-detect Java tools.jar
	 */
	public String getJavaTools(boolean blockTillSelected)
	{
		boolean empty = Configuration.javaTools.isEmpty();
		
		if(!empty)
			return Configuration.javaTools;
		
		//TODO auto-detect the JDK path
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
	 * Check if the python 2 command has been set
	 */
	public boolean hasSetPython2Command()
	{
		return !getPython2Command(false).isEmpty();
	}
	
	/**
	 * Auto-detect python 2 via command-line
	 */
	public String getPython2Command(boolean blockTillSelected)
	{
		if(!Configuration.python2.isEmpty())
			return Configuration.python2;
		
		
		//check using python CLI flag
		testCommand(new String[]{"python", "-2", "--version"}, "python 2", ()->{
			Configuration.python2 = "python";
			Configuration.python2Extra = "-2";
		});
		if(!Configuration.python2.isEmpty())
			return Configuration.python2;
		
		
		//check if 'python' command is bound as python 2.X
		testCommand(new String[]{"python", "--version"}, "python 2", ()->{
			Configuration.python2 = "python";
		});
		if(!Configuration.python2.isEmpty())
			return Configuration.python2;
		
		
		//TODO auto-detect the Python path (C:/Program Files/Python)
		boolean block = true;
		while (Configuration.python2.isEmpty() && block)
		{
			BytecodeViewer.showMessage("You need to set your Python 2.7 (or PyPy 2.7 for speed) executable path.");
			selectPython2();
			block = !blockTillSelected; //signal block flag off
		}
		
		return Configuration.python2;
	}
	
	/**
	 * Check if the python 3 command has been set
	 */
	public boolean hasSetPython3Command()
	{
		return !getPython3Command(false).isEmpty();
	}
	
	/**
	 * Auto-detect python 3 via command-line
	 */
	public String getPython3Command(boolean blockTillSelected)
	{
		//check if 'python' command is bound as python 2.X
		testCommand(new String[]{"python", "--version"}, "python 3", ()->{
			Configuration.python3 = "python";
		});
		if(!Configuration.python3.isEmpty())
			return Configuration.python3;
		
		
		//TODO auto-detect the Python path (C:/Program Files/Python)
		boolean block = true;
		while (Configuration.python3.isEmpty() && block)
		{
			BytecodeViewer.showMessage("You need to set your Python 3.x (or PyPy 3.x for speed) executable path.");
			selectPython3();
			block = !blockTillSelected; //signal block flag off
		}
		
		return Configuration.python3;
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
				"Python 2.7 (Or PyPy 2.7 for speed) Executable",
				"everything");
		
		if(file == null)
			return;
		
		Configuration.python2 = file.getAbsolutePath();
		SettingsSerializer.saveSettingsAsync();
	}
	
	public void selectPython3()
	{
		final File file = DialogueUtils.fileChooser("Select Python 3.x Executable",
				"Python 3.x (Or PyPy 3.x for speed) Executable",
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
	 * Used to test the command-line for compatibility
	 */
	public void testCommand(String[] command, String matchingText, Runnable onMatch)
	{
		try
		{
			BytecodeViewer.sm.pauseBlocking();
			
			//read the version output
			ProcessBuilder pb = new ProcessBuilder(command);
			Process p = pb.start();
			p.waitFor();
			
			//check for matching text
			if(readProcess(p).toLowerCase().contains(matchingText))
			{
				onMatch.run();
			}
		}
		catch (Exception e) { } //ignore
		finally
		{
			BytecodeViewer.sm.resumeBlocking();
		}
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
