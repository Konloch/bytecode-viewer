package the.bytecode.club.bytecodeviewer;

import com.google.gson.reflect.TypeToken;
import me.konloch.kontainer.io.DiskReader;
import me.konloch.kontainer.io.DiskWriter;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static the.bytecode.club.bytecodeviewer.BytecodeViewer.gson;
import static the.bytecode.club.bytecodeviewer.Configuration.maxRecentFiles;
import static the.bytecode.club.bytecodeviewer.Constants.*;

/**
 * @author Konloch
 * @since 6/29/2021
 */
public class Settings
{
	public static boolean firstBoot = true; //stays true after settings load on first boot
	public static boolean hasSetLanguageAsSystemLanguage = false;
	private static List<String> recentPlugins;
	private static List<String> recentFiles;
	
	static
	{
		try
		{
			if (new File(filesName).exists())
				recentFiles = gson.fromJson(DiskReader.loadAsString(filesName), new TypeToken<ArrayList<String>>() {}.getType());
			else
				recentFiles = DiskReader.loadArrayList(getBCVDirectory() + fs + "recentfiles.bcv", false);
			
			if (new File(pluginsName).exists())
				recentPlugins = gson.fromJson(DiskReader.loadAsString(pluginsName), new TypeToken<ArrayList<String>>() {}.getType());
			else
				recentPlugins = DiskReader.loadArrayList(getBCVDirectory() + fs + "recentplugins.bcv", false);
			
			MiscUtils.deduplicateAndTrim(recentFiles, maxRecentFiles);
			MiscUtils.deduplicateAndTrim(recentPlugins, maxRecentFiles);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Add the recent file
	 *
	 * @param f the recent file
	 */
	public static synchronized void addRecentFile(File f)
	{
		recentFiles.remove(f.getAbsolutePath()); // already added on the list
		recentFiles.add(0, f.getAbsolutePath());
		MiscUtils.deduplicateAndTrim(recentFiles, maxRecentFiles);
		DiskWriter.replaceFile(filesName, MiscUtils.listToString(recentFiles), false);
		resetRecentFilesMenu();
	}
	
	public static synchronized void removeRecentFile(File f)
	{
		if(recentFiles.remove(f.getAbsolutePath()))
		{
			DiskWriter.replaceFile(filesName, MiscUtils.listToString(recentFiles), false);
			resetRecentFilesMenu();
		}
	}
	
	public static String getRecentFile()
	{
		if(recentFiles.isEmpty())
			return null;
		
		return recentFiles.get(0);
	}
	
	/**
	 * Add to the recent plugin list
	 *
	 * @param f the plugin file
	 */
	public static synchronized void addRecentPlugin(File f)
	{
		recentPlugins.remove(f.getAbsolutePath()); // already added on the list
		recentPlugins.add(0, f.getAbsolutePath());
		MiscUtils.deduplicateAndTrim(recentPlugins, maxRecentFiles);
		DiskWriter.replaceFile(pluginsName, MiscUtils.listToString(recentPlugins), false);
		resetRecentFilesMenu();
	}
	
	public static synchronized void removeRecentPlugin(File f)
	{
		if(recentPlugins.remove(f.getAbsolutePath()))
		{
			DiskWriter.replaceFile(pluginsName, MiscUtils.listToString(recentPlugins), false);
			resetRecentFilesMenu();
		}
	}
	
	/**
	 * resets the recent files menu
	 */
	protected static void resetRecentFilesMenu()
	{
		//build recent files
		BytecodeViewer.viewer.recentFilesSecondaryMenu.removeAll();
		for (String s : recentFiles)
		{
			if (!s.isEmpty())
			{
				JMenuItem m = new JMenuItem(s);
				m.addActionListener(e ->
				{
					JMenuItem m12 = (JMenuItem) e.getSource();
					BytecodeViewer.openFiles(new File[]{new File(m12.getText())}, true);
				});
				BytecodeViewer.viewer.recentFilesSecondaryMenu.add(m);
			}
		}
		
		//build recent plugins
		BytecodeViewer.viewer.recentPluginsSecondaryMenu.removeAll();
		for (String s : recentPlugins)
		{
			if (!s.isEmpty())
			{
				JMenuItem m = new JMenuItem(s);
				m.addActionListener(e ->
				{
					JMenuItem m1 = (JMenuItem) e.getSource();
					BytecodeViewer.startPlugin(new File(m1.getText()));
				});
				BytecodeViewer.viewer.recentPluginsSecondaryMenu.add(m);
			}
		}
	}
}
