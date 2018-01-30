package the.bytecode.club.bytecodeviewer;

import java.io.File;

import org.apache.commons.io.FileUtils;

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

public class APKTool {

    public static synchronized void decodeResources(File input, File output) {
        try {
            File dir = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + "Decoded Resources");
            FileUtils.deleteDirectory(dir);
            brut.apktool.Main.main(new String[]{"-s", "-f", "-o", dir.getAbsolutePath(), "decode", input.getAbsolutePath()});
            File original = new File(dir.getAbsolutePath() + BytecodeViewer.fs + "original");
            FileUtils.deleteDirectory(original);
            File classes = new File(dir.getAbsolutePath() + BytecodeViewer.fs + "classes.dex");
            classes.delete();
            File apktool = new File(dir.getAbsolutePath() + BytecodeViewer.fs + "apktool.yml");
            apktool.delete();
            File zip = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + MiscUtils.randomString(12) + ".zip");
            ZipUtils.zipFolder(dir.getAbsolutePath(), zip.getAbsolutePath(), null);
            if (zip.exists())
                zip.renameTo(output);
            FileUtils.deleteDirectory(dir);
        } catch (Exception e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }
    }
}
