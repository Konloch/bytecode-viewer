package the.bytecode.club.bytecodeviewer;

import org.apache.commons.io.FileUtils;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;

import javax.swing.*;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 * *
 * This program is free software: you can redistribute it and/or modify    *
 * it under the terms of the GNU General Public License as published by  *
 * the Free Software Foundation, either version 3 of the License, or     *
 * (at your option) any later version.                                   *
 * *
 * This program is distributed in the hope that it will be useful,       *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 * GNU General Public License for more details.                          *
 * *
 * You should have received a copy of the GNU General Public License     *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

/**
 * @author Konloch
 * @author Bibl (don't ban me pls)
 * @created 19 Jul 2015 03:22:37
 */
public class Boot {
    private static InitialBootScreen screen;

    static {
        try {
            screen = new InitialBootScreen();
        } catch (Exception e) {
            new ExceptionUI(e);
        }
    }

    public static void boot(String[] args, boolean CLI) throws Exception {
        BytecodeViewer.enjarifyWorkingDirectory = BytecodeViewer.getBCVDirectory() + BytecodeViewer.fs + "enjarify_" + BytecodeViewer.enjarifyVersion + BytecodeViewer.fs + "enjarify-master";
        BytecodeViewer.krakatauWorkingDirectory = BytecodeViewer.getBCVDirectory() + BytecodeViewer.fs + "krakatau_" + BytecodeViewer.krakatauVersion + BytecodeViewer.fs + "Krakatau-master";
        File enjarifyDirectory = new File(BytecodeViewer.getBCVDirectory() + BytecodeViewer.fs + "enjarify_" + BytecodeViewer.enjarifyVersion);
        File krakatauDirectory = new File(BytecodeViewer.getBCVDirectory() + BytecodeViewer.fs + "krakatau_" + BytecodeViewer.krakatauVersion);
        if (!enjarifyDirectory.exists() || !krakatauDirectory.exists()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    screen.setVisible(true);
                }
            });
        }
        setState("Bytecode Viewer Boot Screen - Checking Libraries...");
        screen.getProgressBar().setMaximum(3);

        int completedCheck = 0;

        checkKrakatau();
        completedCheck++;
        screen.getProgressBar().setValue(completedCheck);

        checkEnjarify();
        completedCheck++;
        screen.getProgressBar().setValue(completedCheck);

        setState("Bytecode Viewer Boot Screen - Booting!");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                screen.setVisible(false);
            }
        });
    }

    public static void setState(String s) {
        screen.setTitle(s);
    }

    public static void checkEnjarify() {
        setState("Bytecode Viewer Boot Screen - Checking Enjarify...");
        System.out.println("Checking enjarify");

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

        File enjarifyDirectory = new File(BytecodeViewer.getBCVDirectory() + BytecodeViewer.fs + "enjarify_" + BytecodeViewer.enjarifyVersion);
        if (!enjarifyDirectory.exists()) {
            try {
                Path temporaryEnjarifyZip = Files.createTempFile("enjarify", ".zip");
                Files.delete(temporaryEnjarifyZip);
                InputStream inputStream = Boot.class.getResourceAsStream("/enjarify-2.zip");
                Files.copy(inputStream, temporaryEnjarifyZip);
                ZipUtils.unzipFilesToPath(temporaryEnjarifyZip.normalize().toString(), enjarifyDirectory.getAbsolutePath());
                Files.delete(temporaryEnjarifyZip);
            } catch (Exception e) {
                BytecodeViewer.showMessage("ERROR: There was an issue unzipping enjarify (possibly corrupt). Restart BCV." + BytecodeViewer.nl +
                        "If the error persists contact @Konloch.");
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
            }
        }

    }

    public static void checkKrakatau() {
        setState("Bytecode Viewer Boot Screen - Checking Krakatau...");
        System.out.println("Checking krakatau");

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
        File krakatauDirectory = new File(BytecodeViewer.getBCVDirectory() + BytecodeViewer.fs + "krakatau_" + BytecodeViewer.krakatauVersion);
        if (!krakatauDirectory.exists()) {
            try {
                setState("Bytecode Viewer Boot Screen - Updating to " + krakatauDirectory.getName() + "...");
                Path temporaryKrakatauZip = Files.createTempFile("krakatau", ".zip");
                Files.delete(temporaryKrakatauZip);
                InputStream inputStream = Boot.class.getResourceAsStream("/Krakatau-8.zip");
                Files.copy(inputStream, temporaryKrakatauZip);
                ZipUtils.unzipFilesToPath(temporaryKrakatauZip.normalize().toString(), krakatauDirectory.getAbsolutePath());
                Files.delete(temporaryKrakatauZip);
                System.out.println("Updated to Krakatau v" + BytecodeViewer.krakatauVersion);
            } catch (Exception e) {
                BytecodeViewer.showMessage("ERROR: There was an issue unzipping Krakatau decompiler (possibly corrupt). Restart BCV." + BytecodeViewer.nl +
                        "If the error persists contact @Konloch.");
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
            }
        }
    }
}
