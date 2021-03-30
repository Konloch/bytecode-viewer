package the.bytecode.club.bytecodeviewer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Random;

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
 * A collection of Misc Utils.
 *
 * @author Konloch
 */

public class MiscUtils {
    private static final String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String AN = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static Random rnd = new Random();

    /**
     * Returns a random string without numbers
     *
     * @param len the length of the String
     * @return the randomized string
     */
    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public static void printProcess(Process process) throws Exception {
        //Read out dir output
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        br.close();

        is = process.getErrorStream();
        isr = new InputStreamReader(is);
        br = new BufferedReader(isr);
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        br.close();
    }

    /**
     * Returns a random string with numbers
     *
     * @param len the length of the String
     * @return the randomized string
     */
    public static String randomStringNum(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AN.charAt(rnd.nextInt(AN.length())));
        return sb.toString();
    }

    /**
     * Checks the file system to ensure it's a unique name
     *
     * @param start directory it'll be in
     * @param ext   the file extension it'll use
     * @return the unique name
     */
    public static String getUniqueName(String start, String ext) {
        String s = null;
        boolean b = true;
        File f = null;
        String m = null;
        while (b) {
            m = MiscUtils.randomString(32);
            f = new File(start + m + ext);
            if (!f.exists()) {
                s = start + m;
                b = false;
            }
        }
        return s;
    }

    /**
     * Checks the file system to ensure it's a unique number
     *
     * @param start directory it'll be in
     * @param ext   the file extension it'll use
     * @return the unique number
     */
    public static int getClassNumber(String start, String ext) {
        boolean b = true;
        int i = 0;
        while (b) {
            File tempF = new File(start + i + ext);
            if (!tempF.exists())
                b = false;
            else
                i++;
        }
        return i;
    }

    public static String extension(String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }

    public static String append(File file, String extension) {
        String path = file.getAbsolutePath();
        if (!path.endsWith(extension))
            path = path + extension;
        return path;
    }

    /**
     * @author JoshTheWolfe
     */
    @SuppressWarnings({ "unchecked" })
    public static void updateEnv(String name, String val) throws ReflectiveOperationException {
        Map<String, String> env = System.getenv();
        Field field = env.getClass().getDeclaredField("m");
        field.setAccessible(true);
        ((Map<String, String>) field.get(env)).put(name, val);
    }
}
