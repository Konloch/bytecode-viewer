package the.bytecode.club.bytecodeviewer.bootloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import me.konloch.kontainer.io.HTTPRequest;
import org.apache.commons.io.FileUtils;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.bootloader.loader.AbstractLoaderFactory;
import the.bytecode.club.bytecodeviewer.bootloader.loader.ClassPathLoader;
import the.bytecode.club.bytecodeviewer.bootloader.loader.ILoader;
import the.bytecode.club.bytecodeviewer.bootloader.resource.external.EmptyExternalResource;
import the.bytecode.club.bytecodeviewer.bootloader.resource.external.ExternalResource;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;
import the.bytecode.club.bytecodeviewer.util.ZipUtils;

import static the.bytecode.club.bytecodeviewer.Constants.fs;
import static the.bytecode.club.bytecodeviewer.Constants.getBCVDirectory;
import static the.bytecode.club.bytecodeviewer.Constants.krakatauVersion;
import static the.bytecode.club.bytecodeviewer.Constants.krakatauWorkingDirectory;
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
 * @author Bibl (don't ban me pls)
 * @created 19 Jul 2015 03:22:37
 */
public class Boot {

    /*flags*/
    public static boolean globalstop = false;
    public static boolean completedboot = false;
    public static boolean downloading = false;

    private static InitialBootScreen screen;
    private static final List<String> libsList = new ArrayList<>();
    private static final List<String> libsFileList = new ArrayList<>();
    private static final List<String> urlList = new ArrayList<>();

    public static void boot(String[] args, boolean CLI) throws Exception {
        bootstrap();
        ILoader<?> loader = findLoader();

        screen = new InitialBootScreen();
        
        if (!CLI)
            SwingUtilities.invokeLater(() -> screen.setVisible(true));

        create(loader, args.length <= 0 || Boolean.parseBoolean(args[0]));

        SwingUtilities.invokeLater(() -> screen.setVisible(false));
    }

    public static void hide() {
        SwingUtilities.invokeLater(() -> screen.setVisible(false));
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
            JOptionPane.showMessageDialog(null, "Bytecode Viewer ran into an issue, for some reason github is not "
                    + "returning what we're expecting. Please try rebooting, if this issue persists please contact "
                    + "@Konloch.", "Error", JOptionPane.ERROR_MESSAGE);
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
            String fileName = s.substring("https://github.com/Konloch/bytecode-viewer/blob/master/libs/".length()
            );
            File file = new File(libsDirectory, fileName);

            boolean passed = false;
            while (!passed) {
                if (!libsList.contains(fileName)) {
                    downloading = true;
                    setState("Bytecode Viewer Boot Screen - Downloading " + fileName + "...");
                    System.out.println("Downloading " + fileName);

                    try (InputStream is = new URL("https://github.com/Konloch/bytecode-viewer/raw/master/libs/" + fileName)
                            .openConnection().getInputStream();
                        FileOutputStream fos = new FileOutputStream(file)) {
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
                    }

                    try {
                        setState("Bytecode Viewer Boot Screen - Verifying " + fileName + "...");
                        System.out.println("Verifying " + fileName + "...");

                        File f = new File(Constants.tempDirectory, "temp");
                        if (!f.exists()) {
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
                } else if (Configuration.verifyCorruptedStateOnBoot) { //verify its not corrupt each boot (adds 3 seconds boot time)
                    try {
                        setState("Bytecode Viewer Boot Screen - Verifying " + fileName + "...");
                        System.out.println("Verifying " + fileName + "...");

                        File f = new File(Constants.tempDirectory, "temp");
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
                String fileName =
                        urlS.substring("https://github.com/Konloch/bytecode-viewer/blob/master/libs/".length()
                        );
                if (fileName.equals(f.getName()))
                    delete = false;
            }
            if (delete) {
                f.delete();
                System.out.println("Detected & Deleted Foreign/Outdated Jar/File: " + f.getName());
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
                        ExternalResource res = new EmptyExternalResource<>(f.toURI().toURL());
                        loader.bind(res);
                        System.out.println("Successfully loaded " + f.getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                        f.delete();
                        JOptionPane.showMessageDialog(null, "Error, Library " + f.getName() + " is corrupt, please "
                                        + "restart to redownload it.",
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

    public static void setState(String s)
    {
        if (screen != null)
            screen.setTitle(s);
    }

    public static ILoader<?> findLoader() {
        // TODO: Find from providers
        // return new LibraryClassLoader();

        // TODO: Catch
        return AbstractLoaderFactory.find().spawnLoader();
    }

    private static void bootstrap() {
        AbstractLoaderFactory.register(ClassPathLoader::new);
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
        File libsDir = libsDir();
        if (libsDir.exists())
            for (File f : MiscUtils.listFiles(libsDir)) {
                libsList.add(f.getName());
                libsFileList.add(f.getAbsolutePath());
            }
    }

    public static void dropKrakatau() {
        File temp = new File(getBCVDirectory() + fs + "krakatau_" + krakatauVersion + ".zip");
        File krakatauDirectory = new File(krakatauWorkingDirectory);
        krakatauWorkingDirectory += fs + "Krakatau-master";
        if (!krakatauDirectory.exists() || temp.exists()) {
            if (temp.exists())
                temp.delete();

            setState("Bytecode Viewer Boot Screen - Extracting Krakatau");
            System.out.println("Extracting Krakatau");

            while (temp.exists())
                temp.delete();

            try (InputStream is = BytecodeViewer.class.getClassLoader().getResourceAsStream("Krakatau-"
                    + krakatauVersion + ".zip");
                 FileOutputStream baos = new FileOutputStream(temp)) {
                int r;
                byte[] buffer = new byte[8192];
                while ((r = Objects.requireNonNull(is).read(buffer)) >= 0) {
                    baos.write(buffer, 0, r);
                }

                ZipUtils.unzipFilesToPath(temp.getAbsolutePath(), krakatauDirectory.getAbsolutePath());
                temp.delete();
                System.out.println("Successfully extracted Krakatau");
            } catch (Exception e) {
                setState("Bytecode Viewer Boot Screen - ERROR, please contact @Konloch with your stacktrace.");
                BytecodeViewer.handleException(e);
            }
        }
    }

    public static void dropEnjarify() {
        File temp = new File(getBCVDirectory() + fs + "enjarify" + Constants.enjarifyVersion + ".zip");
        File enjarifyDirectory = new File(Constants.enjarifyWorkingDirectory);
        Constants.enjarifyWorkingDirectory += fs + "enjarify-master";
        if (!enjarifyDirectory.exists() || temp.exists()) {
            if (temp.exists())
                temp.delete();
            
            setState("Bytecode Viewer Boot Screen - Extracting Enjarify");
            System.out.println("Extracting Enjarify");

            while (temp.exists())
                temp.delete();

            try (InputStream is = BytecodeViewer.class.getClassLoader().getResourceAsStream("enjarify-" +
                    Constants.enjarifyVersion + ".zip");
                 FileOutputStream baos = new FileOutputStream(temp)) {
                int r;
                byte[] buffer = new byte[8192];
                while ((r = Objects.requireNonNull(is).read(buffer)) >= 0) {
                    baos.write(buffer, 0, r);
                }

                ZipUtils.unzipFilesToPath(temp.getAbsolutePath(), enjarifyDirectory.getAbsolutePath());
                temp.delete();
                System.out.println("Successfully extracted Enjarify");
            } catch (Exception e) {
                setState("Bytecode Viewer Boot Screen - ERROR, please contact @Konloch with your stacktrace.");
                BytecodeViewer.handleException(e);
            }
        }
    }

    public static void downloadZipsOnly() {
        for (String s : urlList) {
            String fileName = s.substring("https://github.com/Konloch/bytecode-viewer/blob/master/libs/".length()
            );
            File file = new File(libsDir(), fileName);

            boolean passed = false;
            while (!passed) {
                if (!libsList.contains(fileName) && fileName.endsWith(".zip")) {
                    downloading = true;
                    setState("Bytecode Viewer Boot Screen - Downloading " + fileName + "...");
                    System.out.println("Downloading " + fileName);

                    try (InputStream is = new URL("https://github.com/Konloch/bytecode-viewer/raw/master/libs/" + fileName)
                            .openConnection().getInputStream();
                         FileOutputStream fos = new FileOutputStream(file)) {
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
                    }

                    try {
                        setState("Bytecode Viewer Boot Screen - Verifying " + fileName + "...");
                        System.out.println("Verifying " + fileName + "...");

                        File f = new File(Constants.tempDirectory, "temp");
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
        for (File f : MiscUtils.listFiles(new File(Constants.libsDirectory))) {
            if (f.getName().toLowerCase().startsWith("enjarify-")) {
                Constants.enjarifyVersion = f.getName().split("-")[1].split("\\.")[0];
                enjarifyZip = f;
            }
        }

        for (File f : MiscUtils.listFiles(new File(getBCVDirectory()))) {
            if (f.getName().toLowerCase().startsWith("enjarify_") && !f.getName().split("_")[1].split("\\.")[0].equals(Constants.enjarifyVersion)) {
                setState("Bytecode Viewer Boot Screen - Removing Outdated " + f.getName() + "...");
                System.out.println("Removing oudated " + f.getName());
                try {
                    FileUtils.deleteDirectory(f);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    
        Constants.enjarifyWorkingDirectory = getBCVDirectory() + fs + "enjarify_" + Constants.enjarifyVersion + fs + "enjarify-master";
        File enjarifyDirectory = new File(getBCVDirectory() + fs + "enjarify_" + Constants.enjarifyVersion);
        if (!enjarifyDirectory.exists()) {
            try {
                setState("Bytecode Viewer Boot Screen - Updating to " + enjarifyDirectory.getName() + "...");
                ZipUtils.unzipFilesToPath(Objects.requireNonNull(enjarifyZip).getAbsolutePath(),
                        enjarifyDirectory.getAbsolutePath());
                System.out.println("Updated to enjarify v" + Constants.enjarifyVersion);
            } catch (Exception e) {
                BytecodeViewer.showMessage("ERROR: There was an issue unzipping enjarify (possibly corrupt). Restart "
                        + "BCV." + nl +
                        "If the error persists contact @Konloch.");
                BytecodeViewer.handleException(e);
                Objects.requireNonNull(enjarifyZip).delete();
            }
        }

    }

    public static void checkKrakatau() {
        setState("Bytecode Viewer Boot Screen - Checking Krakatau...");
        System.out.println("Checking krakatau");

        File krakatauZip = null;
        for (File f : MiscUtils.listFiles(new File(Constants.libsDirectory))) {
            if (f.getName().toLowerCase().startsWith("krakatau-")) {
                //System.out.println(f.getName());
                Constants.krakatauVersion = f.getName().split("-")[1].split("\\.")[0];
                krakatauZip = f;
            }
        }

        for (File f : MiscUtils.listFiles(new File(getBCVDirectory()))) {
            if (f.getName().toLowerCase().startsWith("krakatau_") && !f.getName().split("_")[1].split("\\.")[0].equals(Constants.krakatauVersion)) {
                setState("Bytecode Viewer Boot Screen - Removing Outdated " + f.getName() + "...");
                System.out.println("Removing oudated " + f.getName());
                try {
                    FileUtils.deleteDirectory(f);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Constants.krakatauWorkingDirectory = getBCVDirectory() + fs + "krakatau_" + Constants.krakatauVersion + fs + "Krakatau-master";

        File krakatauDirectory = new File(getBCVDirectory() + fs + "krakatau_" + Constants.krakatauVersion);
        if (!krakatauDirectory.exists()) {
            try {
                setState("Bytecode Viewer Boot Screen - Updating to " + krakatauDirectory.getName() + "...");
                ZipUtils.unzipFilesToPath(Objects.requireNonNull(krakatauZip).getAbsolutePath(),
                        krakatauDirectory.getAbsolutePath());
                System.out.println("Updated to krakatau v" + Constants.krakatauVersion);
            } catch (Exception e) {
                BytecodeViewer.showMessage("ERROR: There was an issue unzipping Krakatau decompiler (possibly "
                        + "corrupt). Restart BCV." + nl +
                        "If the error persists contact @Konloch.");
                BytecodeViewer.handleException(e);
                Objects.requireNonNull(krakatauZip).delete();
            }
        }
    }
}
