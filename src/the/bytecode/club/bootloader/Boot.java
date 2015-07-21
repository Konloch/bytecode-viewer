package the.bytecode.club.bootloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;

import me.konloch.kontainer.io.HTTPRequest;
import the.bytecode.club.bootloader.resource.EmptyExternalResource;
import the.bytecode.club.bootloader.resource.ExternalResource;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.CommandLineInput;
import the.bytecode.club.bytecodeviewer.ZipUtils;

/**
 * @author Konloch
 * @author Bibl (don't ban me pls)
 * @created 19 Jul 2015 03:22:37
 */
public class Boot {

	private static InitialBootScreen screen;

	public static void boot(String[] args, int CLI) throws Exception {
		if(CLI == CommandLineInput.STOP)
			return;
		
		bootstrap();
		ILoader<?> loader = findLoader();
		
		screen = new InitialBootScreen();

		if(CLI == CommandLineInput.OPEN_FILE)
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					screen.setVisible(true);
				}
			});
		
		create(loader, args.length > 0 ? Boolean.valueOf(args[0]) : true);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				screen.setVisible(false);
			}
		});
		
		/*Class<?> klass = loader.loadClass("the.bytecode.club.bytecodeviewer.BytecodeViewer");
		klass.getDeclaredMethod("BOOT", new Class<?>[] { String[].class }).invoke(null, new Object[] { args });*/
		
		if(CLI == CommandLineInput.OPEN_FILE)
			BytecodeViewer.BOOT(args, false);
		else {
			BytecodeViewer.BOOT(args, true);
			CommandLineInput.executeCommandLine(args);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void create(ILoader<?> loader, boolean clean) throws Exception {
		setState("Bytecode Viewer Boot Screen - Checking Libraries...");

		final File libsDirectory = libsDir();

		List<String> urlList = new ArrayList<String>();
		HTTPRequest req = new HTTPRequest(new URL("https://github.com/Konloch/bytecode-viewer/tree/master/libs"));
		for (String s : req.read())
			if (s.contains("href=\"/Konloch/bytecode-viewer/blob/master/libs/")) {
				urlList.add("https://github.com" + s.split("<a href=")[1].split("\"")[1]);
			}

		if (urlList.isEmpty()) {
			JOptionPane
					.showMessageDialog(
							null,
							"Bytecode Viewer ran into an issue, for some reason github is not returning what we're expecting. Please try rebooting, if this issue persists please contact @Konloch.",
							"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (clean)
			libsDirectory.delete();

		if (!libsDirectory.exists())
			libsDirectory.mkdir();

		List<String> libsList = new ArrayList<String>();
		List<String> libsFileList = new ArrayList<String>();
		for (File f : libsDirectory.listFiles()) {
			libsList.add(f.getName());
			libsFileList.add(f.getAbsolutePath());
		}

		screen.getProgressBar().setMaximum(urlList.size() * 2);

		int completedCheck = 0;

		for (String s : urlList) {
			String fileName = s.substring("https://github.com/Konloch/bytecode-viewer/blob/master/libs/".length(), s.length());
			if (!libsList.contains(fileName)) {
				setState("Bytecode Viewer Boot Screen - Downloading " + fileName);
				System.out.println("Downloading " + fileName);
				boolean passed = false;
				while (!passed) {
					InputStream is = null;
					FileOutputStream fos = null;
					try {
						is = new URL("https://github.com/Konloch/bytecode-viewer/raw/master/libs/" + fileName).openConnection().getInputStream();
						fos = new FileOutputStream(new File(libsDirectory, fileName));
						System.out.println("Downloading from " + s);
						byte[] buffer = new byte[8192];
						int len;
						int downloaded = 0;
						boolean flag = false;
						while ((len = is.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
							fos.flush();
							downloaded += 8192;
							int mbs = downloaded / 1048576;
							if (mbs % 5 == 0 && mbs != 0) {
								if (!flag)
									System.out.println("Downloaded " + mbs + "MBs so far");
								flag = true;
							} else
								flag = false;
						}
						libsFileList.add(new File(libsDirectory, fileName).getAbsolutePath());
					} finally {
						try {
							if (is != null) {
								is.close();
							}
						} finally {
							if (fos != null) {
								fos.flush();
							}
							if (fos != null) {
								fos.close();
							}
						}
					}
					System.out.println("Download finished!");
					passed = true;
				}
			}
			
			completedCheck++;
			screen.getProgressBar().setValue(completedCheck);
		}

		setState("Bytecode Viewer Boot Screen - Checking & Deleting Foreign/Outdated Libraries...");
		System.out.println("Checking & Deleting foreign/outdated libraries");
		for (String s : libsFileList) {
			File f = new File(s);
			boolean delete = true;
			for (String urlS : urlList) {
				String fileName = urlS.substring("https://github.com/Konloch/bytecode-viewer/blob/master/libs/".length(), urlS.length());
				if (fileName.equals(f.getName()))
					delete = false;
			}
			if (delete) {
				f.delete();
				System.out.println("Detected & Deleted Foriegn/Outdated Jar/File: " + f.getName());
			}
		}

		setState("Bytecode Viewer Boot Screen - Loading Libraries...");
		System.out.println("Loading libraries...");

		for (String s : libsFileList) {
			if (s.endsWith(".jar")) {
				File f = new File(s);
				if (f.exists()) {
					setState("Bytecode Viewer Boot Screen - Loading Library " + f.getName());
					System.out.println("Loading library " + f.getName());

					try {
						ExternalResource res = new EmptyExternalResource<Object>(f.toURI().toURL());
						loader.bind(res);
						System.out.println("Succesfully loaded " + f.getName());
					} catch (Exception e) {
						e.printStackTrace();
						f.delete();
						JOptionPane.showMessageDialog(null, "Error, Library " + f.getName() + " is corrupt, please restart to redownload it.",
								"Error", JOptionPane.ERROR_MESSAGE);
					}
				}

				completedCheck++;
				screen.getProgressBar().setValue(completedCheck);
			}
		}
		
		setState("Bytecode Viewer Boot Screen - Checking Krakatau...");
		System.out.println("Checking krakatau");

		File krakatauZip = null;
		for(File f : new File(BytecodeViewer.libsDirectory).listFiles()) {
			if(f.getName().toLowerCase().startsWith("krakatau-")) {
				BytecodeViewer.krakatauVersion = f.getName().split("-")[1].split("\\.")[0];
				krakatauZip = f;
			}
		}

		for(File f : new File(BytecodeViewer.getBCVDirectory()).listFiles()) {
			if(f.getName().toLowerCase().startsWith("krakatau_") && !f.getName().split("_")[1].split("\\.")[0].equals(BytecodeViewer.krakatauVersion)) {
				setState("Bytecode Viewer Boot Screen - Removing Outdated " + f.getName() + "...");
				System.out.println("Removing oudated " + f.getName());
				try {
					FileUtils.deleteDirectory(f);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		BytecodeViewer.krakatauWorkingDirectory = BytecodeViewer.getBCVDirectory() + BytecodeViewer.fs + "krakatau_" + BytecodeViewer.krakatauVersion + BytecodeViewer.fs + "Krakatau-master";		
		File krakatauDirectory = new File(BytecodeViewer.getBCVDirectory() + BytecodeViewer.fs + "krakatau_" + BytecodeViewer.krakatauVersion);
		if(!krakatauDirectory.exists()) {
			try {
				setState("Bytecode Viewer Boot Screen - Updating to "+krakatauDirectory.getName()+"...");
				ZipUtils.unzipFilesToPath(krakatauZip.getAbsolutePath(), krakatauDirectory.getAbsolutePath());
				System.out.println("Updated to krakatau v" + BytecodeViewer.krakatauVersion);
			} catch(Exception e) {
				BytecodeViewer.showMessage("ERROR: There was an issue unzipping Krakatau decompiler (possibly corrupt). Restart BCV."+BytecodeViewer.nl+
						"If the error persists contact @Konloch.");
				new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
				krakatauZip.delete();
			}
		}

		completedCheck++;
		screen.getProgressBar().setValue(completedCheck);


		setState("Bytecode Viewer Boot Screen - Checking Enjarify...");
		System.out.println("Checking enjarify");

		File enjarifyZip = null;
		for(File f : new File(BytecodeViewer.libsDirectory).listFiles()) {
			if(f.getName().toLowerCase().startsWith("enjarify-")) {
				BytecodeViewer.enjarifyVersion = f.getName().split("-")[1].split("\\.")[0];
				enjarifyZip = f;
			}
		}

		for(File f : new File(BytecodeViewer.getBCVDirectory()).listFiles()) {
			if(f.getName().toLowerCase().startsWith("enjarify_") && !f.getName().split("_")[1].split("\\.")[0].equals(BytecodeViewer.enjarifyVersion)) {
				setState("Bytecode Viewer Boot Screen - Removing Outdated " + f.getName() + "...");
				System.out.println("Removing oudated " + f.getName());
				try {
					FileUtils.deleteDirectory(f);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		BytecodeViewer.enjarifyWorkingDirectory = BytecodeViewer.getBCVDirectory() + BytecodeViewer.fs + "enjarify_" + BytecodeViewer.enjarifyVersion + BytecodeViewer.fs + "enjarify-master";		
		File enjarifyDirectory = new File(BytecodeViewer.getBCVDirectory() + BytecodeViewer.fs + "enjarify_" + BytecodeViewer.enjarifyVersion);
		if(!enjarifyDirectory.exists()) {
			try {
				setState("Bytecode Viewer Boot Screen - Updating to "+enjarifyDirectory.getName()+"...");
				ZipUtils.unzipFilesToPath(enjarifyZip.getAbsolutePath(), enjarifyDirectory.getAbsolutePath());
				System.out.println("Updated to enjarify v" + BytecodeViewer.enjarifyVersion);
			} catch(Exception e) {
				BytecodeViewer.showMessage("ERROR: There was an issue unzipping enjarify (possibly corrupt). Restart BCV."+BytecodeViewer.nl+
						"If the error persists contact @Konloch.");
				new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
				enjarifyZip.delete();
			}
		}
		completedCheck++;
		screen.getProgressBar().setValue(completedCheck);

		setState("Bytecode Viewer Boot Screen - Booting!");
	}

	private static File libsDir() {
		File dir = new File(System.getProperty("user.home"), ".Bytecode-Viewer");
		while (!dir.exists())
			dir.mkdirs();

		return new File(dir, "libs");
	}

	private static void setState(String s) {
		screen.setTitle(s);
	}

	private static ILoader<?> findLoader() {
		// TODO: Find from providers
		// return new LibraryClassLoader();
		
		// TODO: Catch
		return AbstractLoaderFactory.find().spawnLoader();
	}
	
	private static void bootstrap() {
		AbstractLoaderFactory.register(new LoaderFactory<Object>() {
			@Override
			public ILoader<Object> spawnLoader() {
				return new ClassPathLoader();
			}
		});
	}
}