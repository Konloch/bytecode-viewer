package the.bytecode.club.bytecodeviewer.bootloader;

import de.skuzzle.semantic.Version;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import javax.swing.JFileChooser;
import me.konloch.kontainer.io.HTTPRequest;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.api.BCV;
import the.bytecode.club.bytecodeviewer.gui.components.FileChooser;
import the.bytecode.club.bytecodeviewer.gui.components.MultipleChoiceDialog;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;

import static the.bytecode.club.bytecodeviewer.Constants.VERSION;
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
 * @author Konloch
 */
public class UpdateCheck implements Runnable
{
	//just brute force download the url path
	//one of these works for every single version of BCV
	public static final String[] remoteGithubReleases = new String[]
	{
			//current url scheme since v2.9.12
			"https://github.com/Konloch/bytecode-viewer/releases/download/v{VERSION}/Bytecode-Viewer-{VERSION}.jar",
			//for v2.9.10 and v2.9.11
			"https://github.com/Konloch/bytecode-viewer/releases/download/{VERSION}/Bytecode-Viewer-{VERSION}.jar",
			//for v2.7.0 to v2.9.8
			"https://github.com/Konloch/bytecode-viewer/releases/download/v{VERSION}/BytecodeViewer.{VERSION}.zip",
			//for v2.0 to v2.6.0
			"https://github.com/Konloch/bytecode-viewer/releases/download/v{VERSION}/BytecodeViewer.{VERSION}.jar",
			//for v1.1 to v1.5.3
			"https://github.com/Konloch/bytecode-viewer/releases/download/b{VERSION}/BytecodeViewer.Beta.{VERSION}.jar",
			//for v1.4
			"https://github.com/Konloch/bytecode-viewer/releases/download/b.{VERSION}/BytecodeViewer.Beta.{VERSION}.jar",
			//for v1.0
			"https://github.com/Konloch/bytecode-viewer/releases/download/B{VERSION}/BytecodeViewer.jar",
			//zip variant of current url scheme since v2.9.12 (not currently used but incase it ever does)
			"https://github.com/Konloch/bytecode-viewer/releases/download/v{VERSION}/Bytecode-Viewer-{VERSION}.zip",
	};
	
	//a list of all of the released versions of BCV
	public static final String[] versions = new String[]
	{
			//"2.11.0",
			//"2.10.15",
			"2.10.14",
			"2.10.13",
			"2.10.12",
			"2.10.11",
			"2.9.22",
			"2.9.21",
			"2.9.20",
			"2.9.19",
			"2.9.18",
			"2.9.17",
			"2.9.16",
			"2.9.15",
			"2.9.14",
			"2.9.13",
			"2.9.12",
			"2.9.11",
			"2.9.10", //broken due to repo change
			"2.9.8", //broken due to repo change & zip
			"2.9.7", //broken due to repo change & zip
			"2.9.6", //zip
			"2.9.5", //zip
			"2.9.4", //zip
			"2.9.3", //zip
			"2.9.2", //zip
			"2.9.1", //zip
			"2.9.0", //zip
			"2.8.1", //zip
			"2.8.0", //zip
			"2.7.1", //zip
			"2.7.0", //zip
			"2.6.0",
			"2.5.2",
			"2.5.1",
			"2.5.0",
			"2.4.0",
			"2.3.0",
			"2.2.1",
			"2.2.0",
			"2.1.1",
			"2.1.0",
			"2.0.1",
			"2.0",
			"1.5.3",
			"1.5.2",
			"1.5.1",
			"1.5",
			"1.4",
			"1.3.1",
			"1.3",
			"1.2",
			"1.1",
			"1.0",
	};
	
	@Override
	public void run()
	{
		try {
			HTTPRequest r = new HTTPRequest(new URL("https://raw.githubusercontent.com/Konloch/bytecode-viewer/master/VERSION"));
			final Version version = Version.parseVersion(r.readSingle());
			final Version localVersion = Version.parseVersion(VERSION);
			
			try {
				//developer version
				if (Version.compare(localVersion, version) > 0)
					return;
			} catch (Exception ignored) { }
			
			MultipleChoiceDialog outdatedDialog = new MultipleChoiceDialog("Bytecode Viewer - Outdated Version",
					"Your version: " + localVersion + ", latest version: " + version + nl +
							"What would you like to do?",
					new String[]{"Open The Download Page", "Download The Updated Jar", "Do Nothing (And Don't Ask Again)"});
			
			int result = outdatedDialog.promptChoice();
			
			if (result == 0)
			{
				if (Desktop.isDesktopSupported())
					Desktop.getDesktop().browse(new URI("https://github.com/Konloch/bytecode-viewer/releases"));
				else
					BytecodeViewer.showMessage("Cannot open the page, please manually type it."
							+ nl + "https://github.com/Konloch/bytecode-viewer/releases");
			}
			else if (result == 1)
			{
				//TODO move this to after the file extension has been found
				final File file = promptFileSave("Jar Archives", "jar");
				
				if(file != null)
				{
					Thread downloadThread = new Thread(() ->
							downloadBCV(version.toString(), file, ()->{}, ()->{}), "Downloader");
					downloadThread.start();
				}
			}
			else if(result == 2)
			{
				//TODO save version into a hashset called doNotPrompt
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static File promptFileSave(String description, String extension) throws IOException
	{
		JFileChooser fc = new FileChooser(new File("./").getCanonicalFile(),
				"Select Save File",  description, extension);
		
		int returnVal = fc.showSaveDialog(BytecodeViewer.viewer);
		File file = null;
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			Configuration.setLastOpenDirectory(fc.getSelectedFile());
			
			file = fc.getSelectedFile();
			String nameLowercase = file.getAbsolutePath().toLowerCase();
			if (!nameLowercase.endsWith(".jar"))
				file = new File(file.getAbsolutePath() + ".jar");
			
			if (file.exists())
			{
				MultipleChoiceDialog overwriteDialog = new MultipleChoiceDialog("Bytecode Viewer - Overwrite File",
						"The file " + file + " exists, would you like to overwrite it?",
						new String[]{TranslatedStrings.YES.toString(), TranslatedStrings.NO.toString()});
				
				if (overwriteDialog.promptChoice() != 0)
					return null;
				
				file.delete();
			}
		}
		
		return file;
	}
	
	//used to download all released versions of BCV
	/*public static void main(String[] args)
	{
		BytecodeViewer.viewer = new MainViewerGUI();
		for(String version : versions)
		{
			//TODO most are jars, check which are zip and append zip as needed
			File file = new File("./" + version + ".zip");
			if(!file.exists())
				downloadBCV(version, file, () -> {}, () -> {});
		}
	}*/
	
	private static void downloadBCV(String version, File saveTo, Runnable onFinish, Runnable onFail)
	{
		boolean found = false;
		for(String urlAttempt : remoteGithubReleases)
		{
			try
			{
				String url = urlAttempt.replace("{VERSION}", version);
				
				if(validURl(url))
				{
					download(url, saveTo, onFinish);
					found = true;
					break;
				}
				
			} catch (FileNotFoundException ex) {
				//ignore 404s
			} catch (Exception e) {
				//print network errors but don't alert user
				e.printStackTrace();
			}
		}
		
		if(!found)
		{
			BCV.logE("Failed to download BCV v" + version);
			BytecodeViewer.showMessage("Unable to download BCV v" + version + ", please let Konloch know.");
			onFail.run();
		}
	}
	
	private static boolean validURl(String url) throws Exception
	{
		HTTPRequest request = new HTTPRequest(new URL(url));
		request.readSingle();
		return request.getStatusCode() == 200;
	}
	
	private static void download(String url, File saveTo, Runnable onFinish) throws Exception
	{
		BCV.log("Downloading from: " + url);
		BytecodeViewer.showMessage("Downloading the jar in the background, when it's finished you will be alerted with another message box."
				+ nl + nl + "Expect this to take several minutes.");

		try (InputStream is = new URL(url).openConnection().getInputStream();
			 FileOutputStream fos = new FileOutputStream(saveTo)) {
			byte[] buffer = new byte[8192];
			int len;
			int downloaded = 0;
			boolean flag = false;
			
			while ((len = is.read(buffer)) > 0)
			{
				fos.write(buffer, 0, len);
				fos.flush();
				
				downloaded += 8192;
				int mbs = downloaded / 1048576;
				if (mbs % 5 == 0 && mbs != 0)
				{
					if (!flag)
						System.out.println("Downloaded " + mbs + "MBs so far");
					flag = true;
				} else
					flag = false;
			}
		}
		
		BCV.log("Download finished!");
		BytecodeViewer.showMessage("Download successful! You can find the updated program at " + saveTo.getAbsolutePath());
		
		onFinish.run();
	}
}
