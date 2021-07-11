package the.bytecode.club.bytecodeviewer.plugin;

import me.konloch.kontainer.io.DiskWriter;
import org.apache.commons.compress.utils.FileNameUtils;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.resources.IconResources;
import the.bytecode.club.bytecodeviewer.gui.components.FileChooser;
import the.bytecode.club.bytecodeviewer.gui.components.SearchableRSyntaxTextArea;
import the.bytecode.club.bytecodeviewer.translation.Translation;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJMenu;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJMenuItem;
import the.bytecode.club.bytecodeviewer.util.DialogueUtils;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;
import the.bytecode.club.bytecodeviewer.util.SyntaxLanguage;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static the.bytecode.club.bytecodeviewer.Constants.fs;
import static the.bytecode.club.bytecodeviewer.Constants.tempDirectory;
import static the.bytecode.club.bytecodeviewer.Settings.addRecentPlugin;

/**
 * @author Konloch
 * @since 7/1/2021
 */
public class PluginWriter extends JFrame
{
	private SearchableRSyntaxTextArea area;
	private JMenuItem menuSaveAs;
	private JMenuItem menuSave;
	private String content;
	private String pluginName;
	private File savePath;
	
	public PluginWriter(PluginTemplate template) throws IOException
	{
		this.content = template.getContents();
		this.pluginName = "Template." + template.getExtension();
		buildGUI();
	}
	
	public PluginWriter(String content, String pluginName)
	{
		this.content = content;
		this.pluginName = pluginName;
		buildGUI();
	}
	
	public void buildGUI()
	{
		setTitle("Editing BCV Plugin: " + pluginName);
		setIconImages(IconResources.iconList);
		setSize(new Dimension(542, 316));
		
		area = (SearchableRSyntaxTextArea) Configuration.rstaTheme.apply(new SearchableRSyntaxTextArea());
		area.setOnCtrlS(this::save);
		area.setText(content);
		area.setCaretPosition(0);
		area.setSyntaxEditingStyle(SyntaxLanguage.detectLanguage(pluginName, content).getSyntaxConstant());
		content = null;
		
		JButton run = new JButton("Run");
		
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new TranslatedJMenu("File", Translation.FILE);
		JMenuItem menuRun = new TranslatedJMenuItem("Run", Translation.RUN);
		menuSaveAs = new TranslatedJMenuItem("Save As...", Translation.SAVE_AS);
		menuSave = new TranslatedJMenuItem("Save...", Translation.SAVE);
		menuSave.setVisible(false);
		
		menuBar.add(menu);
		menu.add(menuSaveAs);
		menu.add(menuSave);
		menu.add(menuRun);
		
		setJMenuBar(menuBar);
		add(area.getScrollPane());
		add(run, BorderLayout.SOUTH);
		
		run.addActionListener((l)->runPlugin());
		menuRun.addActionListener((l)->runPlugin());
		menuSaveAs.addActionListener((l)-> save());
		menuSave.addActionListener((l)-> save());
		
		this.setLocationRelativeTo(null);
	}
	
	public void runPlugin()
	{
		File tempFile = new File(tempDirectory + fs + "temp" + MiscUtils.randomString(32) + fs + pluginName);
		tempFile.getParentFile().mkdirs();
		
		try
		{
			//write to temporary file location
			DiskWriter.replaceFile(tempFile.getAbsolutePath(), area.getText(), false);
			//run plugin from that location
			PluginManager.runPlugin(tempFile);
		}
		catch (Exception e)
		{
			BytecodeViewer.handleException(e);
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
		finally
		{
			tempFile.getParentFile().delete();
		}
	}
	
	public void setPluginName(String name)
	{
		this.pluginName = name;
		setTitle("Editing BCV Plugin: " + name);
	}
	
	public void save()
	{
		Thread exportThread = new Thread(() ->
		{
			if (!BytecodeViewer.autoCompileSuccessful())
				return;
			
			if(savePath == null)
			{
				final String ext = FileNameUtils.getExtension(pluginName);
				JFileChooser fc = new FileChooser(Configuration.getLastDirectory(),
						"Save Plugin",
						"BCV Plugin",
						ext);
				
				int returnVal = fc.showSaveDialog(BytecodeViewer.viewer);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					Configuration.lastDirectory = fc.getSelectedFile().getAbsolutePath();
					File file = fc.getSelectedFile();
					String path = file.getAbsolutePath();
					
					//auto append extension
					if (!path.endsWith("." + ext))
						path = path + "." + ext;
					
					if (!DialogueUtils.canOverwriteFile(path))
						return;
					
					//swap from save-as to having a defined path each save
					setSourceFile(new File(path));
				}
				else
				{
					return;
				}
			}
			
			DiskWriter.replaceFile(savePath.getAbsolutePath(), area.getText(), false);
			addRecentPlugin(savePath);
		}, "Plugin Editor Save");
		exportThread.start();
	}
	
	public void setSourceFile(File file)
	{
		menuSaveAs.setVisible(false);
		menuSave.setVisible(true);
		menuSaveAs.updateUI();
		menuSave.updateUI();
		savePath = file;
		setPluginName(file.getName());
	}
}