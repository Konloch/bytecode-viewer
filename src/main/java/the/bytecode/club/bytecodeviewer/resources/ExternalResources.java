package the.bytecode.club.bytecodeviewer.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.SettingsSerializer;
import the.bytecode.club.bytecodeviewer.gui.components.FileChooser;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.util.DialogUtils;
import the.bytecode.club.bytecodeviewer.util.JRTExtractor;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import static the.bytecode.club.bytecodeviewer.Constants.RT_JAR;
import static the.bytecode.club.bytecodeviewer.Constants.RT_JAR_DUMPED;
import static the.bytecode.club.bytecodeviewer.Constants.libsDirectory;
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
		testCommand(new String[]{"java", "-version"}, "java version", ()-> Configuration.java = "java");
		if(!Configuration.java.isEmpty())
			return Configuration.java;
		
		//TODO auto-detect the JRE path
		boolean block = true;
		//while (Configuration.java.isEmpty() && block)
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
		//while (Configuration.javaTools.isEmpty() && block)
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
			Configuration.python2Extra = true;
		});
		if(!Configuration.python2.isEmpty())
			return Configuration.python2;
		
		//check if 'python' command is bound as python 2.X
		testCommand(new String[]{"python", "--version"}, "python 2", ()-> Configuration.python2 = "python");
		if(!Configuration.python2.isEmpty())
			return Configuration.python2;
		
		//TODO auto-detect the Python path (C:/Program Files/Python)
		boolean block = true;
		//while (Configuration.python2.isEmpty() && block)
		{
			BytecodeViewer.showMessage(TranslatedStrings.YOU_NEED_TO_SET_YOUR_PYTHON_2_PATH.toString());
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
		//check if 'pypy3' command is bound as python 3.X
		//TODO test this and re-enable it
		/*testCommand(new String[]{"pypy3", "--version"}, "python 3", ()->{
			Configuration.python3 = "pypy3";
		});
		if(!Configuration.python3.isEmpty())
			return Configuration.python3;*/
		
		
		//check if 'python3' command is bound as python 3.X
		testCommand(new String[]{"python3", "--version"}, "python 3", ()-> Configuration.python3 = "python3");
		if(!Configuration.python3.isEmpty())
			return Configuration.python3;
		
		
		//check if 'python' command is bound as python 3.X
		testCommand(new String[]{"python", "--version"}, "python 3", ()-> Configuration.python3 = "python");
		if(!Configuration.python3.isEmpty())
			return Configuration.python3;
		
		
		//TODO auto-detect the Python path (C:/Program Files/Python)
		boolean block = true;
		//while (Configuration.python3.isEmpty() && block)
		{
			BytecodeViewer.showMessage(TranslatedStrings.YOU_NEED_TO_SET_YOUR_PYTHON_3_PATH.toString());
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
		final File file = DialogUtils.fileChooser(TranslatedStrings.SELECT_PYTHON_2.toString(),
				TranslatedStrings.PYTHON_2_EXECUTABLE.toString(),
				FileChooser.EVERYTHING);
		
		if(file == null)
			return;
		
		Configuration.python2 = file.getAbsolutePath();
		Configuration.python2Extra = false;
		SettingsSerializer.saveSettingsAsync();
	}
	
	public void selectPython3()
	{
		final File file = DialogUtils.fileChooser(TranslatedStrings.SELECT_PYTHON_3.toString(),
				TranslatedStrings.PYTHON_3_EXECUTABLE.toString(),
				FileChooser.EVERYTHING);
		
		if(file == null)
			return;
		
		Configuration.python3 = file.getAbsolutePath();
		Configuration.python3Extra = false;
		SettingsSerializer.saveSettingsAsync();
	}
	
	public void selectJava()
	{
		final File file = DialogUtils.fileChooser(TranslatedStrings.SELECT_JAVA.toString(),
				TranslatedStrings.JAVA_EXECUTABLE.toString(),
				FileChooser.EVERYTHING);
		
		if(file == null)
			return;
		
		Configuration.java = file.getAbsolutePath();
		SettingsSerializer.saveSettingsAsync();
	}
	
	public void selectJavac()
	{
		final File file = DialogUtils.fileChooser(TranslatedStrings.SELECT_JAVAC.toString(),
				TranslatedStrings.JAVAC_EXECUTABLE.toString(),
				FileChooser.EVERYTHING);
		
		if(file == null)
			return;
		
		Configuration.javac = file.getAbsolutePath();
		SettingsSerializer.saveSettingsAsync();
	}
	
	public void selectJRERTLibrary()
	{
		final File file = DialogUtils.fileChooser(TranslatedStrings.SELECT_JAVA_RT.toString(),
				TranslatedStrings.JAVA_RT_JAR.toString(),
				FileChooser.EVERYTHING);
		
		if(file == null)
			return;
		
		Configuration.rt = file.getAbsolutePath();
		SettingsSerializer.saveSettingsAsync();
	}
	
	public void selectJavaTools()
	{
		final File file = DialogUtils.fileChooser(TranslatedStrings.SELECT_JAVA_TOOLS.toString(),
				TranslatedStrings.JAVA_TOOLS_JAR.toString(),
				FileChooser.EVERYTHING);
		
		if(file == null)
			return;
		
		Configuration.javaTools = file.getAbsolutePath();
		SettingsSerializer.saveSettingsAsync();
	}
	
	public void selectOptionalLibraryFolder()
	{
		final File file = DialogUtils.fileChooser(TranslatedStrings.SELECT_LIBRARY_FOLDER.toString(),
				TranslatedStrings.OPTIONAL_LIBRARY_FOLDER.toString(),
				FileChooser.EVERYTHING);
		
		if(file == null)
			return;
		
		Configuration.library = file.getAbsolutePath();
		SettingsSerializer.saveSettingsAsync();
	}
	
	/**
	 * Finds a library from the library folder
	 */
	public String findLibrary(String nameContains)
	{
		for (File f : MiscUtils.listFiles(new File(libsDirectory)))
			if (f.getName().contains(nameContains))
				return f.getAbsolutePath();
		
		return null;
	}
	
	/**
	 * Searches a directory until the extension is found
	 */
	public File findFile(File basePath, String extension)
	{
		for(File f : MiscUtils.listFiles(basePath))
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
	private void testCommand(String[] command, String matchingText, Runnable onMatch)
	{
		//prevents reflection calls, the stacktrace can be faked to bypass this, so it's not perfect
		String executedClass = Thread.currentThread().getStackTrace()[2].getClassName();
		if(!executedClass.equals(ExternalResources.class.getCanonicalName()))
			return;
		
		try {
			//read the version output
			ProcessBuilder pb = new ProcessBuilder(command);
			Process p = pb.start();
			p.waitFor();
			
			//check for matching text
			if(readProcess(p).toLowerCase().contains(matchingText))
				onMatch.run();
		} catch (Exception ignored) { } //ignore
	}
	
	/**
	 * @author https://stackoverflow.com/a/16714180
	 */
	public String readProcess(Process process) throws IOException
	{
		try (InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader reader = new BufferedReader(isr)) {
			StringBuilder builder = new StringBuilder();
			String line;

			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(System.getProperty("line.separator"));
			}

			return builder.toString();
		}
	}
}
