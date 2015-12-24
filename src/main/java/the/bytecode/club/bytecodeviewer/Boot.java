package the.bytecode.club.bytecodeviewer;

import org.apache.commons.io.FileUtils;
import org.zeroturnaround.zip.ZipUtil;
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

    public static void boot() throws Exception {
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
        screen.getProgressBar().setMaximum(BootSequence.values().length);
        setState(BootSequence.CHECKING_LIBRARIES);

        checkEnjarify();
        checkKrakatau();

        setState(BootSequence.BOOTING);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                screen.setVisible(false);
            }
        });
    }

    public static void setState(BootSequence s) {
        screen.setTitle("Initialzing Bytecode Viewer - " + s.getMessage());
        screen.getProgressBar().setValue(s.ordinal());
        System.out.println(s.getMessage());
    }

    public static void checkEnjarify() {
        setState(BootSequence.CHECKING_ENJARIFY);

        for (File f : new File(BytecodeViewer.getBCVDirectory()).listFiles()) {
            if (f.getName().toLowerCase().startsWith("enjarify_") && !f.getName().split("_")[1].split("\\.")[0].equals(BytecodeViewer.enjarifyVersion)) {
                setState(BootSequence.CLEANING_ENJARIFY);
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
                setState(BootSequence.MOVING_ENJARIFY);
                Path temporaryEnjarifyZip = Files.createTempFile("enjarify", ".zip");
                Files.delete(temporaryEnjarifyZip);
                InputStream inputStream = Boot.class.getResourceAsStream("/enjarify-2.zip");
                Files.copy(inputStream, temporaryEnjarifyZip);
                ZipUtil.unpack(temporaryEnjarifyZip.toFile(), enjarifyDirectory);
                Files.delete(temporaryEnjarifyZip);
            } catch (Exception e) {
                BytecodeViewer.showMessage("ERROR: There was an issue unzipping enjarify (possibly corrupt). Restart BCV." + BytecodeViewer.nl +
                        "If the error persists contact @Konloch.");
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
            }
        }

    }

    public static void checkKrakatau() {
        setState(BootSequence.CHECKING_KRAKATAU);

        for (File f : new File(BytecodeViewer.getBCVDirectory()).listFiles()) {
            if (f.getName().toLowerCase().startsWith("krakatau_") && !f.getName().split("_")[1].split("\\.")[0].equals(BytecodeViewer.krakatauVersion)) {
                setState(BootSequence.CLEANING_KRAKATAU);
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
                setState(BootSequence.MOVING_KRAKATAU);
                Path temporaryKrakatauZip = Files.createTempFile("krakatau", ".zip");
                Files.delete(temporaryKrakatauZip);
                InputStream inputStream = Boot.class.getResourceAsStream("/Krakatau-8.zip");
                Files.copy(inputStream, temporaryKrakatauZip);
                ZipUtil.unpack(temporaryKrakatauZip.toFile(), krakatauDirectory);
                Files.delete(temporaryKrakatauZip);
            } catch (Exception e) {
                BytecodeViewer.showMessage("ERROR: There was an issue unzipping Krakatau decompiler (possibly corrupt). Restart BCV." + BytecodeViewer.nl +
                        "If the error persists contact @Konloch.");
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
            }
        }
    }

    enum BootSequence {
        CHECKING_LIBRARIES("Checking libraries"),
        CHECKING_ENJARIFY("Checking Enjarify"),
        CLEANING_ENJARIFY("Cleaning Enjarify"),
        MOVING_ENJARIFY("Moving Enjarify"),
        CHECKING_KRAKATAU("Checking Krakatau"),
        CLEANING_KRAKATAU("Cleaning Krakatau"),
        MOVING_KRAKATAU("Moving Krakatau"),
        BOOTING("Booting");

        private String message;

        BootSequence(String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }
    }
}
