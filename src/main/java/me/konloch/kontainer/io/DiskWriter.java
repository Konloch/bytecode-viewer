package me.konloch.kontainer.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * This method will save to disk
 *
 * @author Konloch
 */

public class DiskWriter {

    /**
     * Used to insert a difference string with preserving the file extension
     *
     * @param fileName   The file name
     * @param difference Normally an integer
     * @return The filename with the difference inserted and the file extension
     *         preserved
     */
    public static String insertFileName(String fileName, String difference) {
        String[] babe = fileName.split("\\.");
        int count = 0;
        int math = babe.length;
        StringBuilder m = new StringBuilder();

        for (String s2 : babe) {
            m.append(s2);
            if (math - 2 == count)
                m.append(difference).append(".");
            else if (math - 1 != count)
                m.append(".");
            count++;
        }

        return m.toString();
    }

    /**
     * Writes a new line to the file, if it doesn't exist it will automatically
     * create it.
     *
     * @param filename
     * @param fileContents
     * @param debug
     */
    public static synchronized void writeNewLine(String filename,
                                                 byte[] fileContents, boolean debug) {
        new File(filename).getParentFile().mkdirs();
        String original = filename;
        int counter = 0;

        boolean saved = false;
        int failSafe = 0;
        while (!saved && failSafe++ <= 42069)
        {
            try (FileWriter fr = new FileWriter(filename, true);
                 BufferedWriter bw = new BufferedWriter(fr);
                 PrintWriter writer = new PrintWriter(bw)) {
                writer.println(Arrays.toString(fileContents));
                if (debug)
                    System.out.println("Saved " + filename + " to disk");
                saved = true;
            } catch (Exception e) {
                if (debug)
                    System.out.println("Failed saving, trying to save as "
                            + filename);
                if (original.contains(".")) {
                    filename = insertFileName(original, "" + counter);
                } else
                    filename = original + counter;
                counter++;
            }
        }
    }
    
    /**
     * Writes a string to the file
     */
    public static void writeNewLine(String filename, String lineToWrite)
    {
        writeNewLine(filename, lineToWrite, false);
    }

    /**
     * Writes a string to the file
     */
    public static synchronized void writeNewLine(String filename,
                                                 String lineToWrite, boolean debug) {
        new File(filename).getParentFile().mkdirs();
        String original = filename;
        int counter = 0;

        boolean saved = false;
        int failSafe = 0;
        while (!saved && failSafe++ <= 42069)
        {
            try (FileWriter fr = new FileWriter(filename, true);
                 BufferedWriter bw = new BufferedWriter(fr);
                 PrintWriter writer = new PrintWriter(bw)) {
                writer.println(lineToWrite);
                if (debug)
                    System.out.println("Saved " + filename + ">" + lineToWrite
                            + " to disk");
                saved = true;
            } catch (Exception e) {
                if (debug)
                    System.out.println("Failed saving, trying to save as "
                            + filename);
                if (original.contains(".")) {
                    filename = insertFileName(original, "" + counter);
                } else
                    filename = original + counter;
                counter++;
            }
        }
    }

    /**
     * Deletes the original file if it exists, then writes the fileContents[] to
     * the file.
     *
     * @param filename
     * @param fileContents
     * @param debug
     */
    public static synchronized void replaceFileBytes(String filename,
                                                     byte[] fileContents, boolean debug) {
        new File(filename).getParentFile().mkdirs();
        File f = new File(filename);
        if (f.exists())
            f.delete();
    
        String original = filename;
        int counter = 0;

        boolean saved = false;
        int failSafe = 0;
        while (!saved && failSafe++ <= 42069)
        {
            try (FileOutputStream stream = new FileOutputStream(filename))
            {
                stream.write(fileContents);
                stream.flush();
                if (debug)
                    System.out.println("Saved " + filename + " to disk");
                saved = true;
            } catch (Exception e) {
                if (debug)
                    System.out.println("Failed saving, trying to save as "
                            + filename);
                if (original.contains(".")) {
                    filename = insertFileName(original, "" + counter);
                } else
                    filename = original + counter;
                counter++;
            }
        }
    }

    /**
     * Deletes the original file if it exists, then writes the lineToWrite to
     * the file.
     *
     * @param filename
     * @param lineToWrite
     * @param debug
     */
    public static synchronized void replaceFile(String filename,
                                                String lineToWrite, boolean debug) {
        new File(filename).getParentFile().mkdirs();
        File f = new File(filename);
        if (f.exists())
            f.delete();
        String original = filename;
        int counter = 0;

        boolean saved = false;
        int failSafe = 0;
        while (!saved && failSafe++ <= 42069)
        {
            try (FileWriter fr = new FileWriter(filename, true);
                 BufferedWriter bw = new BufferedWriter(fr);
                 PrintWriter writer = new PrintWriter(bw)) {
                writer.println(lineToWrite);
                if (debug)
                    System.out.println("Saved " + filename + ">" + lineToWrite
                            + " to disk");
                saved = true;
            } catch (Exception e) {
                if (debug)
                    System.out.println("Failed saving, trying to save as "
                            + filename + "_");
                if (original.contains(".")) {
                    filename = insertFileName(original, "" + counter);
                } else
                    filename = original + counter;
                counter++;
            }
        }
    }

}
