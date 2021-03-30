package the.bytecode.club.bytecodeviewer.util;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Encoding Convert Utils
 *
 * @author hupan
 * @date 2019-11-19 14:29
 */
public class EncodeUtils {

    public static String stringToUnicode(String s) {
        try {
            StringBuilder out = new StringBuilder("");
            byte[] bytes = s.getBytes("unicode");

            for (int i = 0; i < bytes.length - 1; i += 2) {
                out.append("\\u");
                String str = Integer.toHexString(bytes[i + 1] & 0xff);
                for (int j = str.length(); j < 2; j++) {
                    out.append("0");
                }
                String str1 = Integer.toHexString(bytes[i] & 0xff);
                out.append(str1);
                out.append(str);
            }
            return out.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String unicodeToString(String str) {

        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            String group = matcher.group(2);
            ch = (char) Integer.parseInt(group, 16);
            String group1 = matcher.group(1);
            str = str.replace(group1, ch + "");
        }
        return str;
    }

    public static String convertStringToUTF8(String s) {
        if (s == null || StringUtils.EMPTY.equals(s)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        try {
            char c;
            for (int i = 0; i < s.length(); i++) {
                c = s.charAt(i);
                if (c >= 0 && c <= 255) {
                    sb.append(c);
                } else {
                    byte[] b;
                    b = Character.toString(c).getBytes(StandardCharsets.UTF_8);
                    for (int value : b) {
                        int k = value;
                        k = k < 0 ? k + 256 : k;
                        sb.append(Integer.toHexString(k).toUpperCase());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String convertUTF8ToString(String s) {
        if (s == null || StringUtils.EMPTY.equals(s)) {
            return null;
        }
        s = s.toUpperCase();
        int total = s.length() / 2;
        int pos = 0;
        byte[] buffer = new byte[total];
        for (int i = 0; i < total; i++) {
            int start = i * 2;
            buffer[i] = (byte) Integer.parseInt(s.substring(start, start + 2), 16);
            pos++;
        }

        return new String(buffer, 0, pos, StandardCharsets.UTF_8);
    }

}
