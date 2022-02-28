package me.konloch.kontainer.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import the.bytecode.club.bytecodeviewer.util.EncodeUtils;

/**
 * Used to load from the disk, optional caching
 *
 * @author Konloch
 */

public class DiskReader {

    public static Random random = new Random();
    public static Map<String, List<String>> map = new HashMap<>();

    /**
     * Used to load from file, allows caching
     */
    public synchronized static List<String> loadArrayList(String fileName,
                                                          boolean cache) {
        List<String> array = new ArrayList<>();
        if (!map.containsKey(fileName)) {
            try {
                File file = new File(fileName);
                if (!file.exists()) // doesn't exist, return empty
                    return array;

                try (FileReader fr = new FileReader(file);
                     BufferedReader reader = new BufferedReader(fr)) {
                    String add;

                    while ((add = reader.readLine()) != null)
                        array.add(add);

                }

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

        try (FileReader fr = new FileReader(fileName);
             BufferedReader reader = new BufferedReader(fr)) {
            for (String add = reader.readLine(); add != null; add = reader.readLine()) {
                s.append(EncodeUtils.unicodeToString(add)).append(System.lineSeparator());
            }
        }

        return s.toString();
    }

    /**
     * Used to load a string via line number lineNumber = -1 means random.
     */
    public static String loadString(String fileName, int lineNumber,
                                    boolean cache) throws Exception {

        List<String> array;
        if (!map.containsKey(fileName)) {
            array = new ArrayList<>();
            File file = new File(fileName);

            try (FileReader fr = new FileReader(file);
                 BufferedReader reader = new BufferedReader(fr)) {
                String add;

                while ((add = reader.readLine()) != null)
                    array.add(add);
            }

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
