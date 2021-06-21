package the.bytecode.club.bytecodeviewer.util;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.gui.ExportJar;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static the.bytecode.club.bytecodeviewer.Constants.fs;
import static the.bytecode.club.bytecodeviewer.Constants.tempDirectory;

/**
 * @author Konloch
 * @since 6/21/2021
 */
public class ResourceExporting
{
	
	public static void saveAsRunnableJar()
	{
		if (BytecodeViewer.getLoadedClasses().isEmpty()) {
			BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
			return;
		}
		Thread t = new Thread(() -> {
			if (BytecodeViewer.viewer.compileOnSave.isSelected() && !BytecodeViewer.compile(false))
				return;
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.isDirectory() || MiscUtils.extension(f.getAbsolutePath()).equals("zip");
				}
				
				@Override
				public String getDescription() {
					return "Zip Archives";
				}
			});
			fc.setFileHidingEnabled(false);
			fc.setAcceptAllFileFilterUsed(false);
			int returnVal = fc.showSaveDialog(BytecodeViewer.viewer);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				String path = file.getAbsolutePath();
				if (!path.endsWith(".jar"))
					path = path + ".jar";
				
				if (new File(path).exists()) {
					JOptionPane pane = new JOptionPane(
							"Are you sure you wish to overwrite this existing file?");
					Object[] options = new String[]{"Yes", "No"};
					pane.setOptions(options);
					JDialog dialog = pane.createDialog(BytecodeViewer.viewer,
							"Bytecode Viewer - Overwrite File");
					dialog.setVisible(true);
					Object obj = pane.getValue();
					int result = -1;
					for (int k = 0; k < options.length; k++)
						if (options[k].equals(obj))
							result = k;
					
					if (result == 0) {
						file.delete();
					} else {
						return;
					}
				}
				
				new ExportJar(path).setVisible(true);
			}
		});
		t.start();
	}
	
	public static void saveAsZip()
	{
		if (BytecodeViewer.getLoadedClasses().isEmpty()) {
			BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
			return;
		}
		Thread t = new Thread(() -> {
			if (BytecodeViewer.viewer.compileOnSave.isSelected() && !BytecodeViewer.compile(false))
				return;
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.isDirectory() || MiscUtils.extension(f.getAbsolutePath()).equals("zip");
				}
				
				@Override
				public String getDescription() {
					return "Zip Archives";
				}
			});
			fc.setFileHidingEnabled(false);
			fc.setAcceptAllFileFilterUsed(false);
			int returnVal = fc.showSaveDialog(BytecodeViewer.viewer);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				if (!file.getAbsolutePath().endsWith(".zip"))
					file = new File(file.getAbsolutePath() + ".zip");
				
				if (file.exists()) {
					JOptionPane pane = new JOptionPane(
							"Are you sure you wish to overwrite this existing file?");
					Object[] options = new String[]{"Yes", "No"};
					pane.setOptions(options);
					JDialog dialog = pane.createDialog(BytecodeViewer.viewer,
							"Bytecode Viewer - Overwrite File");
					dialog.setVisible(true);
					Object obj = pane.getValue();
					int result = -1;
					for (int k = 0; k < options.length; k++)
						if (options[k].equals(obj))
							result = k;
					
					if (result == 0) {
						file.delete();
					} else {
						return;
					}
				}
				
				final File file2 = file;
				
				BytecodeViewer.viewer.setIcon(true);
				Thread t17 = new Thread(() -> {
					JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(),
							file2.getAbsolutePath());
					BytecodeViewer.viewer.setIcon(false);
				});
				t17.start();
			}
		});
		t.start();
	}
	
	public static void saveAsDex()
	{
		if (BytecodeViewer.getLoadedClasses().isEmpty()) {
			BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
			return;
		}
		
		Thread t = new Thread(() -> {
			if (BytecodeViewer.viewer.compileOnSave.isSelected() && !BytecodeViewer.compile(false))
				return;
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.isDirectory() || MiscUtils.extension(f.getAbsolutePath()).equals("dex");
				}
				
				@Override
				public String getDescription() {
					return "Android DEX Files";
				}
			});
			fc.setFileHidingEnabled(false);
			fc.setAcceptAllFileFilterUsed(false);
			int returnVal = fc.showSaveDialog(BytecodeViewer.viewer);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				final File file = fc.getSelectedFile();
				String output = file.getAbsolutePath();
				if (!output.endsWith(".dex"))
					output = output + ".dex";
				
				final File file2 = new File(output);
				
				if (file2.exists()) {
					JOptionPane pane = new JOptionPane(
							"Are you sure you wish to overwrite this existing file?");
					Object[] options = new String[]{"Yes", "No"};
					pane.setOptions(options);
					JDialog dialog = pane.createDialog(BytecodeViewer.viewer,
							"Bytecode Viewer - Overwrite File");
					dialog.setVisible(true);
					Object obj = pane.getValue();
					int result = -1;
					for (int k = 0; k < options.length; k++)
						if (options[k].equals(obj))
							result = k;
					
					if (result == 0) {
						file.delete();
					} else {
						return;
					}
				}
				
				Thread t16 = new Thread(() -> {
					BytecodeViewer.viewer.setIcon(true);
					final String input = tempDirectory + fs + MiscUtils.getRandomizedName() + ".jar";
					JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), input);
					
					Thread t15 = new Thread(() -> {
						Dex2Jar.saveAsDex(new File(input), file2);
						
						BytecodeViewer.viewer.setIcon(false);
					});
					t15.start();
				});
				t16.start();
			}
		});
		t.start();
	}
	
	public static void saveAsAPK()
	{
		if (BytecodeViewer.getLoadedClasses().isEmpty()) {
			BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
			return;
		}
		
		//if theres only one file in the container don't bother asking
		List<FileContainer> containers = BytecodeViewer.getFiles();
		List<FileContainer> validContainers = new ArrayList<>();
		List<String> validContainersNames = new ArrayList<>();
		FileContainer container;
		
		for (FileContainer fileContainer : containers) {
			if (fileContainer.APKToolContents != null && fileContainer.APKToolContents.exists()) {
				validContainersNames.add(fileContainer.name);
				validContainers.add(fileContainer);
			}
		}
		
		if (!validContainers.isEmpty()) {
			container = validContainers.get(0);
			
			if (validContainers.size() >= 2) {
				JOptionPane pane = new JOptionPane("Which file would you like to export as an APK?");
				Object[] options = validContainersNames.toArray(new String[0]);
				
				pane.setOptions(options);
				JDialog dialog = pane.createDialog(BytecodeViewer.viewer, "Bytecode Viewer - Select APK");
				dialog.setVisible(true);
				Object obj = pane.getValue();
				int result = -1;
				for (int k = 0; k < options.length; k++)
					if (options[k].equals(obj))
						result = k;
				
				container = containers.get(result);
			}
		} else {
			BytecodeViewer.showMessage("You can only export as APK from a valid APK file. Make sure "
					+ "Settings>Decode Resources is ticked on.\n\nTip: Try exporting as DEX, it doesn't rely on "
					+ "decoded APK resources");
			return;
		}
		
		final FileContainer finalContainer = container;
		
		Thread t = new Thread(() -> {
			if (BytecodeViewer.viewer.compileOnSave.isSelected() && !BytecodeViewer.compile(false))
				return;
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.isDirectory() || MiscUtils.extension(f.getAbsolutePath()).equals("apk");
				}
				
				@Override
				public String getDescription() {
					return "Android APK";
				}
			});
			fc.setFileHidingEnabled(false);
			fc.setAcceptAllFileFilterUsed(false);
			int returnVal = fc.showSaveDialog(BytecodeViewer.viewer);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				final File file = fc.getSelectedFile();
				String output = file.getAbsolutePath();
				if (!output.endsWith(".apk"))
					output = output + ".apk";
				
				final File file2 = new File(output);
				
				if (file2.exists()) {
					JOptionPane pane = new JOptionPane(
							"Are you sure you wish to overwrite this existing file?");
					Object[] options = new String[]{"Yes", "No"};
					pane.setOptions(options);
					JDialog dialog = pane.createDialog(BytecodeViewer.viewer,
							"Bytecode Viewer - Overwrite File");
					dialog.setVisible(true);
					Object obj = pane.getValue();
					int result = -1;
					for (int k = 0; k < options.length; k++)
						if (options[k].equals(obj))
							result = k;
					
					if (result == 0) {
						file.delete();
					} else {
						return;
					}
				}
				
				Thread t14 = new Thread(() -> {
					BytecodeViewer.viewer.setIcon(true);
					final String input = tempDirectory + fs + MiscUtils.getRandomizedName() + ".jar";
					JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), input);
					
					Thread t13 = new Thread(() -> {
						APKTool.buildAPK(new File(input), file2, finalContainer);
						
						BytecodeViewer.viewer.setIcon(false);
					});
					t13.start();
				});
				t14.start();
			}
		});
		t.start();
	}
}
