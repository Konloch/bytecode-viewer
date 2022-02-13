package the.bytecode.club.bytecodeviewer;

import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.JFileChooser;
import the.bytecode.club.bytecodeviewer.gui.components.FileChooser;
import the.bytecode.club.bytecodeviewer.gui.components.RunOptions;
import the.bytecode.club.bytecodeviewer.util.DialogUtils;
import the.bytecode.club.bytecodeviewer.util.JarUtils;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

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
 * Whenever a key is pressed on the swing UI it should get logged here
 *
 * @author Konloch
 * @since 7/6/2021
 */
public class GlobalHotKeys
{
	/**
	 * Checks the hotkeys
	 */
	public static void keyPressed(KeyEvent e)
	{
		if (System.currentTimeMillis() - Configuration.lastHotKeyExecuted <= (600))
			return;
		
		//CTRL + O
		//open resource
		if ((e.getKeyCode() == KeyEvent.VK_O) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0))
		{
			Configuration.lastHotKeyExecuted = System.currentTimeMillis();
			
			final File file = DialogUtils.fileChooser("Select File or Folder to open in BCV",
					"APKs, DEX, Class Files or Zip/Jar/War Archives",
					Constants.SUPPORTED_FILE_EXTENSIONS);
			
			if(file == null)
				return;
			
			BytecodeViewer.updateBusyStatus(true);
			BytecodeViewer.openFiles(new File[]{file}, true);
			BytecodeViewer.updateBusyStatus(false);
		}
		
		//CTRL + N
		//new workspace
		else if ((e.getKeyCode() == KeyEvent.VK_N) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0))
		{
			Configuration.lastHotKeyExecuted = System.currentTimeMillis();
			BytecodeViewer.resetWorkspace(true);
		}
		
		//CTRL + T
		//compile
		else if ((e.getKeyCode() == KeyEvent.VK_T) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0))
		{
			Configuration.lastHotKeyExecuted = System.currentTimeMillis();
			Thread t = new Thread(() -> BytecodeViewer.compile(true, false), "Compile");
			t.start();
		}
		
		//CTRL + R
		//Run remote code
		else if ((e.getKeyCode() == KeyEvent.VK_R) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0))
		{
			Configuration.lastHotKeyExecuted = System.currentTimeMillis();
			
			if (BytecodeViewer.promptIfNoLoadedClasses())
				return;
			
			new RunOptions().setVisible(true);
		}
		
		//CTRL + S
		//Export resources
		else if ((e.getKeyCode() == KeyEvent.VK_S) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0))
		{
			Configuration.lastHotKeyExecuted = System.currentTimeMillis();
			
			if (BytecodeViewer.promptIfNoLoadedResources())
				return;
			
			Thread resourceExport = new Thread(() ->
			{
				if (!BytecodeViewer.autoCompileSuccessful())
					return;
				
				JFileChooser fc = new FileChooser(Configuration.getLastSaveDirectory(),
						"Select Zip Export",
						"Zip Archives",
						"zip");
				
				int returnVal = fc.showSaveDialog(BytecodeViewer.viewer);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					Configuration.setLastSaveDirectory(fc.getSelectedFile());
					
					File file = MiscUtils.autoAppendFileExtension(".zip", fc.getSelectedFile());
					
					if (!DialogUtils.canOverwriteFile(file))
						return;
					
					BytecodeViewer.updateBusyStatus(true);
					Thread jarExport = new Thread(() -> {
						JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(),
								file.getAbsolutePath());
						BytecodeViewer.updateBusyStatus(false);
					}, "Jar Export");
					jarExport.start();
				}
			}, "Resource Export");
			resourceExport.start();
		}
		
		//CTRL + W
		//close active resource (currently opened tab)
		else if ((e.getKeyCode() == KeyEvent.VK_W) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0))
		{
			Configuration.lastHotKeyExecuted = System.currentTimeMillis();
			
			if (BytecodeViewer.hasActiveResource())
				BytecodeViewer.viewer.workPane.tabs.remove(BytecodeViewer.viewer.workPane.getActiveResource());
		}
		
		//CTRL + L
		//open last opened resource
		else if ((e.getKeyCode() == KeyEvent.VK_L) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0))
		{
			Configuration.lastHotKeyExecuted = System.currentTimeMillis();
			
			String recentFile = Settings.getRecentFile();
			
			if(!BytecodeViewer.hasResources() && recentFile != null)
			{
				File file = new File(recentFile);
				if(file.exists())
				{
					BytecodeViewer.openFiles(new File[]{file}, false);
				}
				else
				{
					BytecodeViewer.showMessage("The file " + file.getAbsolutePath() + " could not be found.");
					Settings.removeRecentFile(file);
				}
			}
		}
	}
}
