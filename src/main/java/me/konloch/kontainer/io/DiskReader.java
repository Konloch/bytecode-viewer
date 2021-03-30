package me.konloch.kontainer.io;

import the.bytecode.club.bytecodeviewer.util.EncodeUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Used to load from the disk, optional caching
 *
 * @author Konloch
 */

public class DiskReader {

    public static Random random = new Random();
    public static HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

    /**
     * Used to load from file, allows caching
     */
    public synchronized static ArrayList<String> loadArrayList(String fileName,
                                                               boolean cache) {
        ArrayList<String> array = new ArrayList<String>();
        if (!map.containsKey(fileName)) {
            try {
                File file = new File(fileName);
                if (!file.exists()) // doesnt exist, return empty
                    return array;

                BufferedReader reader = new BufferedReader(new FileReader(file));
                String add;

                while ((add = reader.readLine()) != null)
                    array.add(add);

                reader.close();

                if (cache)
                    map.put(fileName, array);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            array = map.get(fileName);
        }

        return array;

    }

    /**
     * Used to load from file
     */
    public synchronized static String loadAsString(String fileName) throws Exception {
        StringBuilder s = new StringBuilder();

        BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));

        for (String add = reader.readLine(); add != null; add = reader.readLine()) {
            s.append(EncodeUtils.unicodeToString(add)).append(System.getProperty("line.separator"));
        }

        reader.close();

        return s.toString();
    }

    /**
     * Used to load a string via line number lineNumber = -1 means random.
     */
    public static String loadString(String fileName, int lineNumber,
                                    boolean cache) throws Exception {

        ArrayList<String> array;
        if (!map.containsKey(fileName)) {
            array = new ArrayList<String>();
            File file = new File(fileName);

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String add;

            while ((add = reader.readLine()) != null)
                array.add(add);

            reader.close();

            if (cache)
                map.put(fileName, array);
        } else {
            array = map.get(fileName);
        }

        if (lineNumber == -1) {
            int size = array.size();
            return array.get(random.nextInt(size));
        } else
            return array.get(lineNumber);
    }

}