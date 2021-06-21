package me.konloch.kontainer.io;

import java.io.BufferedWriter;
import java.io.File;
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
        PrintWriter writer = null;
        String original = filename;
        int counter = 0;

        boolean saved = false;
        while (!saved) {
            try {
                writer = new PrintWriter(new BufferedWriter(new FileWriter(
                        filename, true)));
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
        writer.close();
    }

    /**
     * Writes a string to the file
     *
     * @param filename
     * @param lineToWrite
     * @param debug
     */
    public static synchronized void writeNewLine(String filename,
                                                 String lineToWrite, boolean debug) {
        PrintWriter writer = null;
        String original = filename;
        int counter = 0;

        boolean saved = false;
        while (!saved) {
            try {
                writer = new PrintWriter(new BufferedWriter(new FileWriter(
                        filename, true)));
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
        writer.close();
    }

    /**
     * Deletes the original file if it exists, then writes the fileContents[] to
     * the file.
     *
     * @param filename
     * @param fileContents
     * @param debug
     */
    public static synchronized void replaceFile(String filename,
                                                byte[] fileContents, boolean debug) {
        File f = new File(filename);
        if (f.exists())
            f.delete();
        PrintWriter writer = null;
        String original = filename;
        int counter = 0;

        boolean saved = false;
        while (!saved) {
            try {
                writer = new PrintWriter(new BufferedWriter(new FileWriter(
                        filename, true)));
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
        writer.close();
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
        File f = new File(filename);
        if (f.exists())
            f.delete();
        PrintWriter writer = null;
        String original = filename;
        int counter = 0;

        boolean saved = false;
        while (!saved) {
            try {
                writer = new PrintWriter(new BufferedWriter(new FileWriter(
                        filename, true)));
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
        writer.close();
    }

}