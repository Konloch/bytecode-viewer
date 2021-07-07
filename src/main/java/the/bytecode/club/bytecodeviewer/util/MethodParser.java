package the.bytecode.club.bytecodeviewer.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

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
 * Methods parser.
 *
 * @author DreamSworK
 */
public class MethodParser {

    public static class Method {
        public String name;
        public List<String> params;

        public Method(String name, List<String> params) {
            this.name = name;
            this.params = params;
        }

        @Override
        public String toString() {
            String params = this.params.toString();
            return this.name + "(" + params.substring(1, params.length() - 1) + ")";
        }
    }

    public static final Pattern regex = Pattern.compile("\\s*(?:static|public|private|protected|final|abstract)"
            + "[\\w\\s.<>\\[\\]]*\\s+(?<name>[\\w.]+)\\s*\\((?<params>[\\w\\s,.<>\\[\\]$?]*)\\)");

    private final TreeMap<Integer, Method> methods = new TreeMap<>();

    private static String removeBrackets(String string) {
        if (string.indexOf('<') != -1 && string.indexOf('>') != -1) {
            return removeBrackets(string.replaceAll("<[^<>]*>", ""));
        }
        return string;
    }

    private static String getLastPart(String string, int character) {
        int ch = string.lastIndexOf(character);
        if (ch != -1) {
            string = string.substring(ch + 1);
        }
        return string;
    }

    public void addMethod(int line, String name, String params) {
        if (!name.isEmpty()) {
            name = getLastPart(name, '.');
            String[] args = {};
            if (!params.isEmpty()) {
                params = removeBrackets(params);
                args = params.split(",");
                for (int i = 0; i < args.length; i++) {
                    args[i] = args[i].trim();
                    if (args[i].indexOf(' ') != -1) {
                        String[] strings = args[i].split(" ");
                        args[i] = strings[strings.length - 2];
                    }
                    args[i] = getLastPart(args[i], '.');
                    args[i] = getLastPart(args[i], '$');
                }
            }
            Method method = new Method(name, Arrays.asList(args));
            methods.put(line, method);
        }
    }

    public boolean isEmpty() {
        return methods.isEmpty();
    }

    public Method getMethod(int line) {
        return methods.get(line);
    }

    public Integer[] getMethodsLines() {
        Integer[] lines = new Integer[methods.size()];
        return methods.keySet().toArray(lines);
    }

    public String getMethodName(int line) {
        Method method = methods.get(line);
        if (method != null) {
            if (!method.name.isEmpty())
                return method.name;
        }
        return "";
    }

    public List<String> getMethodParams(int line) {
        Method method = methods.get(line);
        if (method != null) {
            if (!method.params.isEmpty())
                return method.params;
        }
        return null;
    }

    public int findMethod(Method method) {
        return findMethod(method.name, method.params);
    }

    public int findMethod(String name, List<String> params) {
        for (Map.Entry<Integer, Method> entry : methods.entrySet()) {
            if (name.equals(entry.getValue().name) && params.size() == entry.getValue().params.size()) {
                if (params.equals(entry.getValue().params)) {
                    return entry.getKey();
                }
            }
        }
        return -1;
    }

    public int findActiveMethod(int line)
    {
        if (!methods.isEmpty())
        {
            Map.Entry<Integer, Method> low = methods.floorEntry(line);
            if (low != null) {
                return low.getKey();
            }
        }
        return -1;
    }

    public int findNearestMethod(int line) {
        if (!methods.isEmpty()) {
            if (methods.size() == 1) {
                return methods.firstKey();
            } else {
                Map.Entry<Integer, Method> low = methods.floorEntry(line);
                Map.Entry<Integer, Method> high = methods.ceilingEntry(line);
                if (low != null && high != null) {
                    return Math.abs(line - low.getKey()) < Math.abs(line - high.getKey()) ? low.getKey() :
                            high.getKey();
                } else if (low != null || high != null) {
                    return low != null ? low.getKey() : high.getKey();
                }
            }
        }
        return -1;
    }
}
