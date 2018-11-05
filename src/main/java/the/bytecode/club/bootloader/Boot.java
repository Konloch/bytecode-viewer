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
import the.bytecode.club.bytecodeviewer.ZipUtils;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;

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
 * @author Bibl (don't ban me pls)
 * @created 19 Jul 2015 03:22:37
 */
public class Boot {

    /*flags*/
    public static boolean globalstop = false;
    public static boolean completedboot = false;
    public static boolean downloading = false;

    private static InitialBootScreen screen;
    private static List<String> libsList = new ArrayList<String>();
    private static List<String> libsFileList = new ArrayList<String>();
    private static List<String> urlList = new ArrayList<String>();

    static {
        try {
            screen = new InitialBootScreen();
        } catch (Exception e) {
            new ExceptionUI(e);
        }
    }

    public static void boot(String[] args, boolean CLI) throws Exception {
    	/*if(System.getProperty("java.version").startsWith("9."))
    	{
    		BytecodeViewer.showMessage("Java 9.x is not supported yet, please wait till BCV 2.9.11\n\rJava 8 should work <3");
    		System.exit(0);
    		return;
    	}
    	if(System.getProperty("java.version").startsWith("10."))
    	{
    		BytecodeViewer.showMessage("Java 10.x is not supported yet, please wait till BCV 2.9.11\n\rJava 8 should work <3");
    		System.exit(0);
    		return;
    	}*/
    	
        bootstrap();
        ILoader<?> loader = findLoader();

        if (!CLI)
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
    }

    public static void hide() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                screen.setVisible(false);
            }
        });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void create(ILoader<?> loader, boolean clean) throws Exception {
        setState("Bytecode Viewer Boot Screen - Checking Libraries...");
        final File libsDirectory = libsDir();

        populateUrlList();

        if (globalstop) {
            while (true) {
                Thread.sleep(100);//just keep this thread halted.
            }
        }

        if (urlList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Bytecode Viewer ran into an issue, for some reason github is not returning what we're expecting. Please try rebooting, if this issue persists please contact @Konloch.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (clean)
            libsDirectory.delete();

        if (!libsDirectory.exists())
            libsDirectory.mkdir();

        populateLibsDirectory();

        screen.getProgressBar().setMaximum(urlList.size() * 2);

        int completedCheck = 0;

        for (String s : urlList) {
            String fileName = s.substring("https://github.com/Konloch/bytecode-viewer/blob/master/libs/".length(), s.length());
            File file = new File(libsDirectory, fileName);

            boolean passed = false;
            while (!passed) {
                if (!libsList.contains(fileName)) {
                    downloading = true;
                    setState("Bytecode Viewer Boot Screen - Downloading " + fileName + "...");
                    System.out.println("Downloading " + fileName);

                    InputStream is = null;
                    FileOutputStream fos = null;
                    try {
                        is = new URL("https://github.com/Konloch/bytecode-viewer/raw/master/libs/" + fileName).openConnection().getInputStream();
                        fos = new FileOutputStream(file);
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

                    try {
                        setState("Bytecode Viewer Boot Screen - Verifying " + fileName + "...");
                        System.out.println("Verifying " + fileName + "...");

                        File f = new File(BytecodeViewer.tempDirectory, "temp");
                        if(!f.exists())
                        {
                        	f.getParentFile().mkdirs();
                        }
                        ZipUtils.zipFile(file, f);
                        f.delete();

                        libsFileList.add(file.getAbsolutePath());
                        System.out.println("Download finished!");
                        passed = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Jar or Zip" + file.getAbsolutePath() + " is corrupt, redownloading.");
                        file.delete();
                    }
                } else if (BytecodeViewer.verify) { //verify its not corrupt each boot (adds 3 seconds boot time)
                    try {
                        setState("Bytecode Viewer Boot Screen - Verifying " + fileName + "...");
                        System.out.println("Verifying " + fileName + "...");

                        File f = new File(BytecodeViewer.tempDirectory, "temp");
                        ZipUtils.zipFile(file, f);
                        f.delete();

                        passed = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Jar or Zip" + file.getAbsolutePath() + " is corrupt, redownloading.");
                        libsFileList.remove(file.getAbsolutePath());
                        file.delete();
                    }
                } else {
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

        checkKrakatau();
        completedCheck++;
        screen.getProgressBar().setValue(completedCheck);

        checkEnjarify();
        completedCheck++;
        screen.getProgressBar().setValue(completedCheck);

        setState("Bytecode Viewer Boot Screen - Booting!");
        completedboot = true;
    }

    public static File libsDir() {
        File dir = new File(System.getProperty("user.home"), ".Bytecode-Viewer/libs");
        while (!dir.exists())
            dir.mkdirs();

        return dir;
    }

    public static void setState(String s) {
        screen.setTitle(s);
    }

    public static ILoader<?> findLoader() {
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

    public static void populateUrlList() throws Exception {
        HTTPRequest req = new HTTPRequest(new URL("https://github.com/Konloch/bytecode-viewer/tree/master/libs"));
        req.setTimeout(30000);
        for (String s : req.read())
            if (s.contains("href=\"/Konloch/bytecode-viewer/blob/master/libs/")) {
                urlList.add("https://github.com" + s.split("href=")[1].split("\"")[1]);
            }
    }

    public static void populateLibsDirectory() {
        if (libsDir() != null && libsDir().exists())
            for (File f : libsDir().listFiles()) {
                libsList.add(f.getName());
                libsFileList.add(f.getAbsolutePath());
            }
    }

    public static void downloadZipsOnly() throws Exception {
        for (String s : urlList) {
            String fileName = s.substring("https://github.com/Konloch/bytecode-viewer/blob/master/libs/".length(), s.length());
            File file = new File(libsDir(), fileName);

            boolean passed = false;
            while (!passed) {
                if (!libsList.contains(fileName) && fileName.endsWith(".zip")) {
                    downloading = true;
                    setState("Bytecode Viewer Boot Screen - Downloading " + fileName + "...");
                    System.out.println("Downloading " + fileName);

                    InputStream is = null;
                    FileOutputStream fos = null;
                    try {
                        is = new URL("https://github.com/Konloch/bytecode-viewer/raw/master/libs/" + fileName).openConnection().getInputStream();
                        fos = new FileOutputStream(file);
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
                    } catch (Exception e) {
                        e.printStackTrace();
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

                    try {
                        setState("Bytecode Viewer Boot Screen - Verifying " + fileName + "...");
                        System.out.println("Verifying " + fileName + "...");

                        File f = new File(BytecodeViewer.tempDirectory, "temp");
                        ZipUtils.zipFile(file, f);
                        f.delete();

                        libsFileList.add(file.getAbsolutePath());
                        System.out.println("Download finished!");
                        passed = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Jar or Zip" + file.getAbsolutePath() + " is corrupt, redownloading.");
                        file.delete();
                    }
                } else
                    passed = true;
            }
        }
    }

    public static void checkEnjarify() {
        setState("Bytecode Viewer Boot Screen - Checking Enjarify...");
        System.out.println("Checking enjarify");
        File enjarifyZip = null;
        for (File f : new File(BytecodeViewer.libsDirectory).listFiles()) {
            if (f.getName().toLowerCase().startsWith("enjarify-")) {
                BytecodeViewer.enjarifyVersion = f.getName().split("-")[1].split("\\.")[0];
                enjarifyZip = f;
            }
        }

        for (File f : new File(BytecodeViewer.getBCVDirectory()).listFiles()) {
            if (f.getName().toLowerCase().startsWith("enjarify_") && !f.getName().split("_")[1].split("\\.")[0].equals(BytecodeViewer.enjarifyVersion)) {
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
        if (!enjarifyDirectory.exists()) {
            try {
                setState("Bytecode Viewer Boot Screen - Updating to " + enjarifyDirectory.getName() + "...");
                ZipUtils.unzipFilesToPath(enjarifyZip.getAbsolutePath(), enjarifyDirectory.getAbsolutePath());
                System.out.println("Updated to enjarify v" + BytecodeViewer.enjarifyVersion);
            } catch (Exception e) {
                BytecodeViewer.showMessage("ERROR: There was an issue unzipping enjarify (possibly corrupt). Restart BCV." + BytecodeViewer.nl +
                        "If the error persists contact @Konloch.");
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                enjarifyZip.delete();
            }
        }

    }

    public static void checkKrakatau() {
        setState("Bytecode Viewer Boot Screen - Checking Krakatau...");
        System.out.println("Checking krakatau");

        File krakatauZip = null;
        for (File f : new File(BytecodeViewer.libsDirectory).listFiles()) {
            if (f.getName().toLowerCase().startsWith("krakatau-")) {
                BytecodeViewer.krakatauVersion = f.getName().split("-")[1].split("\\.")[0];
                krakatauZip = f;
            }
        }

        for (File f : new File(BytecodeViewer.getBCVDirectory()).listFiles()) {
            if (f.getName().toLowerCase().startsWith("krakatau_") && !f.getName().split("_")[1].split("\\.")[0].equals(BytecodeViewer.krakatauVersion)) {
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
        if (!krakatauDirectory.exists()) {
            try {
                setState("Bytecode Viewer Boot Screen - Updating to " + krakatauDirectory.getName() + "...");
                ZipUtils.unzipFilesToPath(krakatauZip.getAbsolutePath(), krakatauDirectory.getAbsolutePath());
                System.out.println("Updated to krakatau v" + BytecodeViewer.krakatauVersion);
            } catch (Exception e) {
                BytecodeViewer.showMessage("ERROR: There was an issue unzipping Krakatau decompiler (possibly corrupt). Restart BCV." + BytecodeViewer.nl +
                        "If the error persists contact @Konloch.");
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                krakatauZip.delete();
            }
        }
    }
}
