package the.bytecode.club.bytecodeviewer;

import org.apache.commons.io.FileUtils;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
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

public class APKTool {
    public static synchronized void decodeResources(File input, File output) {
        try {
            Path temporaryDirectory = Files.createTempDirectory("apkresources");
            Files.delete(temporaryDirectory);
            brut.apktool.Main.main(new String[]{"-s", "-f", "-o", temporaryDirectory.toAbsolutePath().toString(), "decode", input.getAbsolutePath()});
            File directory = temporaryDirectory.toFile();
            File original = new File(directory, "original");
            FileUtils.deleteDirectory(original);
            File classes = new File(directory, "classes.dex");
            classes.delete();
            File apktool = new File(directory, "apktool.yml");
            apktool.delete();
            ZipUtil.pack(directory, output);
            FileUtils.deleteDirectory(directory);
        } catch (Exception e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }
    }
}
