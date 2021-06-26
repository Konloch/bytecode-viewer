package the.bytecode.club.bytecodeviewer.util;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.tree.ClassNode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

import static the.bytecode.club.bytecodeviewer.Constants.gson;

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

public class MiscUtils
{
    private static CharsetEncoder asciiEncoder = StandardCharsets.US_ASCII.newEncoder(); // or "ISO-8859-1" for ISO Latin 1
    private static final String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String AN = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random rnd = new Random();
    private static HashSet<String> createdRandomizedNames = new HashSet<>();

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
    
    /**
     * Ensures it will only return a uniquely generated names, contains a dupe checker to be sure
     *
     * @return the unique randomized name of 25 characters.
     */
    public static String getRandomizedName() {
        boolean generated = false;
        String name = "";
        while (!generated) {
            String randomizedName = MiscUtils.randomString(25);
            if (!createdRandomizedNames.contains(randomizedName)) {
                createdRandomizedNames.add(randomizedName);
                name = randomizedName;
                generated = true;
            }
        }
        return name;
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
        File f;
        String m;
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
        while (b)
        {
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
    
    public static int fileContainersHash(ArrayList<FileContainer> fileContainers) {
        StringBuilder block = new StringBuilder();
        for (FileContainer container : fileContainers) {
            block.append(container.name);
            for (ClassNode node : container.classes) {
                block.append(node.name);
            }
        }
        
        return block.hashCode();
    }
    
    /**
     * Converts an array list to a string
     *
     * @param a array
     * @return string with newline per array object
     */
    public static String listToString(List<String> a) {
        return gson.toJson(a);
    }
    
    /**
     * @author JoshTheWolfe
     */
    @SuppressWarnings({"unchecked"})
    public static void updateEnv(String name, String val) throws ReflectiveOperationException {
        Map<String, String> env = System.getenv();
        Field field = env.getClass().getDeclaredField("m");
        field.setAccessible(true);
        ((Map<String, String>) field.get(env)).put(name, val);
    }
    
    public static BufferedImage loadImage(BufferedImage defaultImage, byte[] contents)
    {
        try {
            return ImageIO.read(new ByteArrayInputStream(contents));
        } catch (IOException e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }
        
        return defaultImage;
    }
    
    public static void deduplicateAndTrim(List<String> list, int maxLength)
    {
        List<String> temporaryList = new ArrayList<>();
        for(String s : list)
            if(!s.isEmpty() && !temporaryList.contains(s))
                temporaryList.add(s);
            
        list.clear();
        list.addAll(temporaryList);
        
        while(temporaryList.size() > maxLength)
            list.remove(0);
    }
    
    public static boolean isPureAscii(String v) {
        return asciiEncoder.canEncode(v);
    }
    
    public static String getChildFromPath(String path)
    {
        if (path != null && path.contains("/"))
        {
            String[] pathParts = StringUtils.split(path, "/");
            return pathParts[pathParts.length-1];
        }
        
        return path;
    }
}
