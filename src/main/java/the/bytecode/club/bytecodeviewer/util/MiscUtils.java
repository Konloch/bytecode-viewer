package the.bytecode.club.bytecodeviewer.util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.translation.Language;

import static the.bytecode.club.bytecodeviewer.BytecodeViewer.gson;

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
    private static final String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String AN = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random rnd = new Random();
    private static final Set<String> createdRandomizedNames = new HashSet<>();

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
        try (InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }

        try (InputStream is = process.getErrorStream();
             InputStreamReader isr = new InputStreamReader(is);
             BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }
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

    public static String getFileHeaderMagicNumber(byte[] fileContents)
    {
        if(fileContents == null || fileContents.length < 4)
            return StringUtils.EMPTY;
        
        return String.format("%02X%02X%02X%02X", fileContents[0],
                fileContents[1], fileContents[2],fileContents[3]);
    }
    
    public static File autoAppendFileExtension(String extension, File file)
    {
        if (!file.getName().endsWith(extension))
            file = new File(file.getAbsolutePath() + extension);
        
        return file;
    }
    
    public static String extension(String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }

    public static String append(File file, String extension) {
        String path = file.getAbsolutePath();
        if (!path.endsWith(extension))
            path += extension;
        return path;
    }
    
    public static int fileContainersHash(List<ResourceContainer> resourceContainers) {
        StringBuilder block = new StringBuilder();
        for (ResourceContainer container : resourceContainers) {
            block.append(container.name);
            for (ClassNode node : container.resourceClasses.values()) {
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
        try (ByteArrayInputStream bais = new ByteArrayInputStream(contents)) {
            return ImageIO.read(bais);
        } catch (IOException e) {
            BytecodeViewer.handleException(e);
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

        while(list.size() > maxLength)
            list.remove(list.size() - 1);
    }

    /**
     * Returns whether the bytes most likely represent binary data.
     * Based on https://stackoverflow.com/a/13533390/5894824
     */
    public static boolean guessIfBinary(byte[] data) {
        double ascii = 0;
        double other = 0;
        for (byte b : data) {
            if (b == 0x09 || b == 0x0A || b == 0x0C || b == 0x0D || (b >= 0x20 && b <= 0x7E)) ascii++;
            else other++;
        }
        return other != 0 && other / (ascii + other) > 0.25;
    }
    
    public static Language guessLanguage()
    {
        String userLanguage = System.getProperty("user.language");
        String systemLanguageCode = userLanguage != null ? userLanguage.toLowerCase() : "";
        
        return Language.getLanguageCodeLookup().getOrDefault(systemLanguageCode, Language.ENGLISH);
    }
    
    public static void setLanguage(Language language)
    {
        Configuration.language = language;
    
        try
        {
            Language.ENGLISH.setLanguageTranslations(); //load english first incase the translation file is missing anything
            language.setLanguageTranslations(); //load translation file and swap text around as needed
            SwingUtilities.updateComponentTreeUI(BytecodeViewer.viewer);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    /**
     * START's a new thread (Creates a new thread and runs that thread runnable on it)
     */
    public static Thread createNewThread(String threadName, Runnable threadRunnable)
    {
        return createNewThread(threadName, false, threadRunnable);
    }
    
    /**
     * START's a new thread (Creates a new thread and runs that thread runnable on it)
     * RUN's a new thread (Just executes the thread runnable on the active thread)
     */
    public static Thread createNewThread(String threadName, boolean runDontStart, Runnable threadRunnable)
    {
        Thread temporaryThread = new Thread(threadRunnable, threadName);
        
        if(runDontStart)
            temporaryThread.run();
        else
            temporaryThread.start();
        
        return temporaryThread;
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
    
    /**
     * Reads an InputStream and returns the read byte[]
     *
     * @param is InputStream
     * @return the read byte[]
     * @throws IOException
     */
    public static byte[] getBytes(final InputStream is) throws IOException
    {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int a;
            while ((a = is.read(buffer)) != -1)
                baos.write(buffer, 0, a);

            return baos.toByteArray();
        }
    }

    public static File[] listFiles(File file) {
        if (file == null)
            return new File[0];
        File[] list = file.listFiles();
        if (list != null)
            return list;
        return new File[0];
    }

    public static File deleteExistingFile(File file)
    {
        if (file.exists())
            file.delete();
        
        return file;
    }
}
