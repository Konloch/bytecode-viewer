package the.bytecode.club.bootloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import me.konloch.kontainer.io.HTTPRequest;

/**
 * @author Bibl (don't ban me pls)
 * @created 19 Jul 2015 03:22:37
 */
public class Boot {

	private static InitialBootScreen screen;

	public static void main(String[] args) throws Exception {
		ILoader loader = findLoader();
		
		screen = new InitialBootScreen();
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
		Class<?> klass = loader.loadClass("the.bytecode.club.bytecodeviewer.BytecodeViewer");
		klass.getDeclaredMethod("main", new Class<?>[] { String[].class }).invoke(null, new Object[] { args });
	}

	private static void create(ILoader loader, boolean clean) throws Exception {
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
						JarInfo jar = new JarInfo(f);
						ExternalLibrary lib = new ExternalLibrary(jar);
						loader.bind(lib);
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

	private static ILoader findLoader() {
		// TODO: Find from providers
		return new LibraryClassLoader();
	}
}